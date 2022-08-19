import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

//监听Ralye端口
public class Server2 implements Runnable {
	public void run() {
		//接受数据包
		DatagramSocket ds; 
		Test.outputArea.append("监听Ralye服务器已启动......"+"\n");
		int msgNum = 0;  //收到的数据信息条数
		String getInc = ""; //得到的事件
		try {
			ds = new DatagramSocket(Test.RaleyNum);
			while(true) {
				byte[] buffer = new byte[1024];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				ds.receive(packet);	
				//				Test.flag = 1;  //判断函数是否己执行过的判断变量
				//收到的字符串
				String s = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
				//收到指令
				if(s.startsWith("RelayCtrl")) {
					//字符串处理
					String[] array1 = s.split("/");
					String s1 = array1[1].substring(2);  //得到Raley编号
					String[] array2 = array1[2].split(",");  //得到Raley按键数字

					//收到一条信息判断是否在同一个Raley上
					if(array2.length==2) {
						Test.inc2 = s1 + "/" + array2[0];
						Test.inc3 = s1 + "/" + array2[1];
						Test.flag2 = 1;
						Test.flag3 = 1;
						Test.outputArea.append("收到Raley指令："+ Test.inc2+"\n");
						Test.outputArea.append("收到Raley指令："+ Test.inc3+"\n");

						//存入内容和时间戳
						long time = System.currentTimeMillis();
						Test.raley.add(Test.inc2);
						Test.raley.add(Test.inc3);
						Test.raley_time.add(time);
						Test.raley_time.add(time);
					}
					else {
						Test.inc2 = s1 + "/" + array2[0];
						Test.outputArea.append("收到Raley指令："+ Test.inc2+"\n");						
						//存入内容和时间戳
						long time = System.currentTimeMillis();
						Test.raley.add(Test.inc2);
						Test.raley_time.add(time);
					}

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

