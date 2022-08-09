import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimerTask;

public class MyTask extends TimerTask {
	@Override
	public void run(){
		try {
			File file =new File("history.txt");
			FileWriter fw = new FileWriter(file,true);
			if(Test.flag==1) {
				//三条信息都收到

				if(Test.flag1==1&&Test.flag2==1&&Test.flag3==1) {
					String getInc1 = Test.inc1+","+Test.inc2+","+Test.inc3;
					Test.snum++;
					Test.outputArea.append("收到完整数据："+getInc1+"\n");
					Test.outputArea.append("事件接收成功："+Test.snum+"\n");
					Test.outputArea.append("事件接收成功占比："+(double)Test.snum/(Test.fnum+Test.snum)+"\n");
					fw.append("收到完整数据："+getInc1+"\r\n");
					
					if(isRightInc()) {
						Test.outputArea.append("事件符合配置需求"+"\n");	
						Test.outputArea.append("-----------------------------------"+"\n");
						fw.append("事件符合配置需求"+"\r\n");
						fw.append("-----------------------------------"+"\r\n");
						clean();

					}
					else {
						Test.outputArea.append("事件不符合配置需求"+"\n");
						Test.outputArea.append("-----------------------------------"+"\n");
						fw.append("事件不符合配置需求"+"\r\n");
						fw.append("-----------------------------------"+"\r\n");
						clean();
					}
				}
				else {
					Test.fnum++;
					String getInc1 = Test.inc1+","+Test.inc2+","+Test.inc3;
					Test.outputArea.append("事件接受失败:"+Test.fnum+"\n");
					Test.outputArea.append("事件接受失败占比:"+(double)Test.fnum/(Test.fnum+Test.snum)+"\n");
					Test.outputArea.append("事件请求超时"+"\n");	
					Test.outputArea.append("-----------------------------------"+"\n");
					fw.append("事件请求超时"+"\r\n"+"收到部分数据："+getInc1+"\r\n");
					fw.append("-----------------------------------"+"\r\n");
					clean();
				}
				//告诉另外一个线程该判断已经执行过一次，另一个线程不再执行判断部分
				Test.flag=0;
				fw.close();
			}
		}catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

	}


	//判断事件是否正确
	public boolean isRightInc() {
		//储存事件
		List<String> DataList = new ArrayList<String>();

		//读取表格信息
		ReadInc Inc = new ReadInc();

		//实时获得可能修改的表格内容
		DataList = Inc.ReadMethod();
		//遍历表格内容
		for(String t: DataList) {
			String[] inc = t.split(",");
			if(inc.length>=3) {
				if(Test.inc1.equals(inc[0])&&((Test.inc2.equals(inc[1])&&Test.inc3.equals(inc[2]))||((Test.inc3.equals(inc[1])&&Test.inc2.equals(inc[2]))))) 
				{
					return true;
				}
			}

		}
		return false;

	}

	//清空消息
	public void clean() {
		Test.flag1 = 0;
		Test.flag2 = 0;
		Test.flag3 = 0;
		Test.inc1 = "";
		Test.inc2 = "";
		Test.inc3 = "";
	}

}
