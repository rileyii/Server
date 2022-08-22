import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Test extends JFrame implements ActionListener{
	public static JTextArea outputArea;
	public static long snum = 0;
	public static long fnum = 0;
	public static int os_num = 0;

	public static long startTime;
	public static long reTime;
	
	//信息判断变量
	public static int flag1 = 0;
	public static int flag2 = 0;
	public static int flag3 = 0;
	public static int flag = 1;

	//信息内容交互
	public static String inc1 = "";
	public static String inc2 = "";
	public static String inc3 = "";

	Server1 s1 = new Server1();
	Server2 s2 = new Server2();
	Thread t1 = new Thread(s1);
	Thread t2 = new Thread(s2);
	MyTask m = new MyTask();
	Thread t3 = new Thread(m);



	private JButton editTable;
	private JButton start;
	private JButton end;
	private JButton changeEN;
	private JButton changeRaley;
	private JButton checkHistory;

	public static long sleep_time = 1000;
	public static int EnNum = 16415;
	public static int RaleyNum = 6666;
	
	//储存内容和时间点
	public static List<String> os = new ArrayList<String>();
	public static List<Long> os_time = new ArrayList<Long>();
	public static List<String> raley = new ArrayList<String>();
	public static List<Long> raley_time = new ArrayList<Long>();
	

	//初始化界面
	Test(){

		this.setTitle("测试界面");
		this.setSize(700,500);
		outputArea = new JTextArea(); 
		outputArea.setEditable(false);
		JScrollPane scroll = new JScrollPane(outputArea); 

		//分别设置水平和垂直滚动条总是出现 
		scroll.setHorizontalScrollBarPolicy( 
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); 
		scroll.setVerticalScrollBarPolicy( 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		add(scroll, BorderLayout.CENTER );

		editTable = new JButton("编辑表格内容");
		start =  new JButton("开始");
		end =  new JButton("退出");
		changeEN = new JButton("修改监听门口机端口");
		changeRaley = new JButton("修改监听Raley端口");
		checkHistory = new JButton("查看历史记录");

		editTable.addActionListener(this);
		start.addActionListener(this);
		end.addActionListener(this);
		changeEN.addActionListener(this);
		changeRaley.addActionListener(this);
		checkHistory.addActionListener(this);
		
		editTable.setPreferredSize(new Dimension(160, 50));
		start.setPreferredSize(new Dimension(160, 50));
		end.setPreferredSize(new Dimension(160, 50));
		changeEN.setPreferredSize(new Dimension(160, 50));
		changeRaley.setPreferredSize(new Dimension(160, 50));
		checkHistory.setPreferredSize(new Dimension(160, 50));

		JPanel j = new JPanel(new GridLayout(6, 1, 0, 20));
		j.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createRaisedBevelBorder(), "功能区"));
		add(j,BorderLayout.WEST);
		j.add(editTable);
		j.add(changeEN);
		j.add(changeRaley);
		j.add(checkHistory);
		j.add(start);
		j.add(end);

		//自动定位到最后一行
		outputArea.getDocument().addDocumentListener(new DocumentListener(){
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				outputArea.setCaretPosition(outputArea.getText().length());
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
			}
		});

		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		this.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e){
		String f = e.getActionCommand();
		if(f == "编辑表格内容"){
			ShowTable s = new ShowTable();
		}
		else if(f == "开始"){
			String time = JOptionPane.showInputDialog("请输入门口机发送消息的间隔秒数(大于等于3s):");
			if(time!=null){		
				int t = Integer.valueOf(time);
				if(t>=3) {
					t1.start();
					t2.start();
					t3.start();
					startTime = System.currentTimeMillis();
					reTime = t*1000;
					//移除监听部分，开始后不允许修改端口号和表格
					editTable.removeActionListener(this);
					start.removeActionListener(this);
					changeEN.removeActionListener(this);
					changeRaley.removeActionListener(this);;
				}
				else {
					JOptionPane.showMessageDialog(null, "时间需大于等于3s");
				}
			}

		}
		else if(f == "退出") {
			setVisible(false); //退出
			System.exit(0);
		}
		else if(f == "修改监听门口机端口") {
			String en = JOptionPane.showInputDialog("请输入新的端口号:");
			if(en!=null) {
				int t = Integer.valueOf(en);
				if(t<=0||t>65535) {
					JOptionPane.showMessageDialog(null, "无效端口");
				}
				else if(t==Test.RaleyNum) {
					JOptionPane.showMessageDialog(null, "监听门口机端口号不能和监听Raley端口号相同");
				}
			}
		}
		else if(f == "修改监听Raley端口") {
			String raley = JOptionPane.showInputDialog("请输入新的端口号:");
			if(raley!=null) {
				int t = Integer.valueOf(raley);
				if(t<=0||t>65535) {
					JOptionPane.showMessageDialog(null, "无效端口");
				}
				else if(t==Test.EnNum) {
					JOptionPane.showMessageDialog(null, "监听Raley端口号不能和监听门口机端口号相同");
				}
			}
		}
		else if(f == "查看历史记录") {
			ShowHistory s = new ShowHistory();
		}

	}
	
	
	public static void main(String[] args) {
		Test t = new Test();
	}
	
	

	
}
