import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimerTask;

//判断线程
public class MyTask implements Runnable{
	List<String> DataList;

	public void run(){
		try {
			File file =new File("history.txt");
			FileWriter fw = new FileWriter(file,true);
			//储存事件
			DataList = new ArrayList<String>();
			//读取表格信息
			ReadInc Inc = new ReadInc();
			//实时获得可能修改的表格内容
			DataList = Inc.ReadMethod();
			SqlHelp sh = new SqlHelp(); 

			while(true) {
				//超过开始时间到下一次循环+2s时间，判定门口机消息未按时到达，为超时状况
				if((System.currentTimeMillis()-Test.startTime)>=(Test.reTime+2000)) {
					Test.startTime = System.currentTimeMillis();   //未按间隔时间达到，则以此时间为开始时间，再次等待
					Test.outputArea.append("-----------------------------------"+"\n");
					Test.outputArea.append("门口机消息超时，未按照设定间隔时间范围内到达！"+"\n");
					Test.outputArea.append("-----------------------------------"+"\n");
					Date date = new Date();
					SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					try {
						sh.addInc(dateFormat.format(date), null, null,"门口机消息超时，未按照设定间隔时间范围内到达！");
					} catch (SQLException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}

				}
				if(Test.os.size()>=1) {
					//恰好收到了一条消息
					if(Test.os.size()==1) {
						String inc1 = Test.os.get(0);
						long os_inc1 = Test.os_time.get(0);
						Test.startTime = os_inc1;
						//超过了2s进入判断
						if((System.currentTimeMillis()-os_inc1)>=2000) {
							//超时情况,不遍历判断，节省时间
							if(Test.raley.size()==0) {
								Test.outputArea.append("-----------------------------------"+"\n");
								Test.outputArea.append("Raley消息超时未收到"+"\n");
								Test.outputArea.append("-----------------------------------"+"\n");
								Date date = new Date();
								SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
								try {
									sh.addInc(dateFormat.format(date), inc1, null,"Raley消息超时未收到");
								} catch (SQLException e) {
									// TODO 自动生成的 catch 块
									e.printStackTrace();
								}
								Test.os.remove(0);
								Test.os_time.remove(0);
							}
							else if(Test.raley.size()==1) {
								//在时限内只收到一条消息(-500,2000)
								if((Test.raley_time.get(0)-os_inc1)<=2000&&(Test.raley_time.get(0)-os_inc1)>=-500) {
									Test.outputArea.append("-----------------------------------"+"\n");
									Test.outputArea.append("Raley消息未收全"+"\n");
									Test.outputArea.append("-----------------------------------"+"\n");
									Date date = new Date();
									SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
									try {
										sh.addInc(dateFormat.format(date), inc1, Test.raley.get(0),"Raley消息未收全");
									} catch (SQLException e) {
										// TODO 自动生成的 catch 块
										e.printStackTrace();
									}
									Test.raley.clear();
									Test.raley_time.clear();
									Test.os.remove(0);
									Test.os_time.remove(0);
								}
								//此外的情况：下一次循环的Raley已经存入，不去处理该数据
								else {
									Test.outputArea.append("Raley消息超时未收到"+"\n");
									Date date = new Date();
									SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
									try {
										sh.addInc(dateFormat.format(date), inc1, null,"Raley消息超时未收到");
									} catch (SQLException e) {
										// TODO 自动生成的 catch 块
										e.printStackTrace();
									}
									Test.os.remove(0);
									Test.os_time.remove(0);
								}

							}
							//大于两条消息 找到时间窗口内的数据进行判断
							else {
								String[] raley_inc = new String[]{"","",""};
								//遍历表格内容
								int flag = 0;
								for(String t: DataList) {
									String[] inc = t.split(",");
									if(inc[0].equals(inc1)) {
										raley_inc[0]=inc[1];
										raley_inc[1]=inc[2];
										flag = 1;
										break;
									}
								}
								if(flag==0) {
									Test.outputArea.append("-----------------------------------"+"\n");
									Test.outputArea.append("收到门口机消息："+inc1+"，表格中未找到门口机匹配的事件"+"\n");
									Test.outputArea.append("-----------------------------------"+"\n");
									Date date = new Date();
									SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
									try {
										sh.addInc(dateFormat.format(date), inc1, null,"表格中未找到门口机匹配的事件");
									} catch (SQLException e) {
										// TODO 自动生成的 catch 块
										e.printStackTrace();
									}
									cleanImf(os_inc1);
								}
								else {
									//得到时间范围内的raley消息
									int time_end = -1;
									for(int i = 0 ; i < Test.raley.size();i++) {
										if((Test.raley_time.get(i)-os_inc1)<=2000&&(Test.raley_time.get(i)-os_inc1)>=-500) {
											time_end = i;
										}
										else {
											break;
										}
									}
									//没有消息是此次对应时间窗口内的，不进行判断
									if(time_end==-1) {
										Test.outputArea.append("-----------------------------------"+"\n");
										Test.outputArea.append("Raley消息超时未收到"+"\n");
										Test.outputArea.append("-----------------------------------"+"\n");
										Date date = new Date();
										SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
										try {
											sh.addInc(dateFormat.format(date), inc1, null,"Raley消息超时未收到");
										} catch (SQLException e) {
											// TODO 自动生成的 catch 块
											e.printStackTrace();
										}
										Test.os.remove(0);
										Test.os_time.remove(0);
									}
									//只收到一次
									else if(time_end==0) {
										Test.outputArea.append("-----------------------------------"+"\n");
										Test.outputArea.append("Raley消息未收全"+"\n");
										Test.outputArea.append("-----------------------------------"+"\n");
										Date date = new Date();
										SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
										try {
											sh.addInc(dateFormat.format(date), inc1, Test.raley.get(0),"Raley消息未收全");
										} catch (SQLException e) {
											// TODO 自动生成的 catch 块
											e.printStackTrace();
										}
										Test.raley.remove(0);
										Test.raley_time.remove(0);
										Test.os.remove(0);
										Test.os_time.remove(0);
									}
									else{
										//时间窗口内都包涵Raley两个数据，判定该事件完整
										List<String> raley_inc2 = Test.raley.subList(0, time_end+1);
										if(raley_inc2.contains(raley_inc[0])&&raley_inc2.contains(raley_inc[1])){
											Test.outputArea.append("-----------------------------------"+"\n");
											Test.outputArea.append("门口机："+inc1+"\n");
											Test.outputArea.append("Raley:"+raley_inc2+"\n");
											Test.outputArea.append("时间窗口范围内完整事件与配置相同，正确"+"\n");
											Test.outputArea.append("-----------------------------------"+"\n");
											Date date = new Date();
											SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
											try {
												String sum = new String();
												for(int i = 0 ; i < raley_inc2.size();i++ ) {
													sum = sum + raley_inc2.get(i)+",";
												}
												sh.addInc(dateFormat.format(date), inc1, sum ,"时间窗口范围内完整事件与配置相同，正确");
											} catch (SQLException e) {
												// TODO 自动生成的 catch 块
												e.printStackTrace();
											}
											Test.raley.subList(0,time_end+1).clear();//丢弃判断过的数据
											Test.raley_time.subList(0,time_end+1).clear();
											Test.os.remove(0);
											Test.os_time.remove(0);

										}
										else {			
											Test.outputArea.append("-----------------------------------"+"\n");
											Test.outputArea.append("门口机："+inc1+"\n");
											Test.outputArea.append("Raley:"+raley_inc2+"\n");
											Test.outputArea.append("时间窗口范围内完整事件与配置不同，错误"+"\n");
											Test.outputArea.append("-----------------------------------"+"\n");
											Date date = new Date();
											SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
											try {
												String sum = new String();
												for(int i = 0 ; i < raley_inc2.size();i++ ) {
													sum = sum + raley_inc2.get(i)+",";
												}
												sh.addInc(dateFormat.format(date), inc1, sum ,"时间窗口范围内完整事件与配置不同，错误");
											} catch (SQLException e) {
												// TODO 自动生成的 catch 块
												e.printStackTrace();
											}
											Test.raley.subList(0,time_end+1).clear();//丢弃判断过的数据
											Test.raley_time.subList(0,time_end+1).clear();
											Test.os.remove(0);
											Test.os_time.remove(0);
										}
									}

								}

							}
						}
					}
				}

				//在下一次循环消息来之前，清除已判断过的数据
				while(Test.raley.size()>0) {
					if((System.currentTimeMillis()-Test.raley_time.get(0))>(Test.reTime-500)){
						//时间窗口外到下一次时间窗口之间的数据判定为超时数据，丢弃不进行判断
						Test.outputArea.append("(超时数据："+Test.raley.get(0)+")\n");
						Date date = new Date();
						SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
						try {
							sh.addInc(dateFormat.format(date),null , Test.raley.get(0) ,"超时数据");
						} catch (SQLException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}
						Test.raley_time.remove(0);
						Test.raley.remove(0);
					}		
					else {
						break;
					}
				}

				//等待100ms再判断一次
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}

			}


		}catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();

		}	

	}

	//清空时间窗口内的消息
	public void cleanImf(long inc1) {
		//得到时间范围内的raley消息
		int time_end = -1;
		for(int i = 0 ; i < Test.raley.size();i++) {
			if((Test.raley_time.get(i)-inc1)<=2000&&(Test.raley_time.get(i)-inc1)>=-500) {
				time_end = i;
			}
			else {
				break;
			}
		}
		Test.raley.subList(0,time_end+1).clear();//丢弃判断过的数据
		Test.raley_time.subList(0,time_end+1).clear();
		Test.os.remove(0);
		Test.os_time.remove(0);
	}

}
