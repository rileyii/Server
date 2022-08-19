import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

//监听门口机端口
public class Server1 implements Runnable {
	public void run() {
		//接受数据包
		DatagramSocket ds;  //门口机信息
		try {
			Test.outputArea.append("监听门口机服务器已启动......."+"\n");
			String getInc = ""; //得到的事件
			ds = new DatagramSocket(Test.EnNum); // 监听指定端口
			while(true) {
				//接收的字节数组
				// 收取到的数据存储在buffer中，由packet.getOffset(), packet.getLength()指定起始位置和长度
				byte[] buffer = new byte[1024];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				//收取一个UDP数据包
				try {
					ds.receive(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}   
				Test.flag = 1;
				//原楼层/目的楼层
				getInc = String.valueOf(buffer[1])+"/"+String.valueOf(buffer[4]); 
				
				Test.flag1 = 1;  //收到门口机信息
				Test.inc1 = getInc;   //门口机发出的具体信息	
				
				//存入内容和时间戳
				
				Test.os.add(getInc);
				Test.os_time.add(System.currentTimeMillis());
				Test.outputArea.append("收到门口机事件："+ getInc+"(源楼层/目的楼层)\n");
			}

		}catch (IOException e) {
			e.printStackTrace();
		}	


	}
}
