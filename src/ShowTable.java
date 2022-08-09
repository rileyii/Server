import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


public class ShowTable extends JFrame implements ActionListener {
	private JButton add;
	private JButton delete;
	private JButton preservation;
	private JButton cancel;
	DefaultTableModel model;
	List<String> DataList;
	JTable t;
	ShowTable(){
		this.setTitle("表格操作");
		this.setSize(450,700);
		List<String> DataList = new ArrayList<String>();
		//读取表格信息
		ReadInc Inc = new ReadInc();
		//实时获得可能修改的表格内容
		DataList = Inc.ReadMethod();
		int recordrow = 0;
		if (DataList != null) {
			recordrow = DataList.size();
		}
		String[][] rinfo = new String[recordrow+50][3];
		for (int i = 0; i < recordrow + 50 ; i++) {		
			if(i < recordrow) {
				String[] array = DataList.get(i).split(",");
				for(int j = 0 ; j < 3 ; ++j) {
					rinfo[i][j]= array[j];
				}
			}
			else {
				for(int j = 0 ; j < 3 ; ++j) {
					rinfo[i][j]= "";
				}
			}

		}

		//制作显示的表格
		String[] tbheadnames = {"门口机信息", "Raley呼梯", "Raley授权"};
		model = new MytableModel(rinfo, tbheadnames);
		t = new JTable(model);
		t.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		t.setAutoCreateRowSorter(true);

		//添加按钮
		add = new JButton("添加");
		delete = new JButton("删除");
		preservation = new JButton("保存");
		cancel = new JButton("取消");
		JPanel j = new JPanel();
		this.add(j,BorderLayout.NORTH);
		j.add(add);
		j.add(delete);
		j.add(preservation);
		j.add(cancel);

		//添加监听
		add.addActionListener(this);
		delete.addActionListener(this);
		preservation.addActionListener(this);
		cancel.addActionListener(this);


		//添加下滑框
		JScrollPane sPane2 = new JScrollPane(t);
		this.add(sPane2, BorderLayout.CENTER);

		this.setLocationRelativeTo(null);
		this.setVisible(true);

	}
	//监听事件
	public void actionPerformed(ActionEvent e){
		String f = e.getActionCommand();
		if(f == "添加"){
			String[] newRow = {"","",""}; 
			model.addRow(newRow);
		}
		else if(f == "删除") {
			if (t.getSelectedRow() != -1) {
				model.removeRow(t.getSelectedRow());
			}
			else {
				JOptionPane.showMessageDialog(null, "请选中一行");
			}
		}
		else if(f == "取消") {
			this.setVisible(false);
		}
		else if(f == "保存") {
			int row = t.getRowCount();
			List<String> new_data = new ArrayList<String>();
			for(int i = 0 ; i < row ; i++) {
				//加入不为空的数据
				if(!t.getValueAt(i, 0).equals("")&&!t.getValueAt(i, 1).equals("")&&!t.getValueAt(i, 2).equals("")) {
					String new_inc = t.getValueAt(i, 0) + ","+ t.getValueAt(i, 1)+ ","+t.getValueAt(i, 2);
					new_data.add(new_inc);
					//System.out.print(new_inc);
				}

			}
			//表格存入数据
			try {
				PrintWriter fw = new PrintWriter("incident.txt");
				for(int i =0 ; i < new_data.size(); i++) {
					fw.println(new_data.get(i));
				}
				fw.close();
			} catch (FileNotFoundException e1) {
				// TODO 自动生成的 catch 块
				e1.printStackTrace();
			}
			JOptionPane.showMessageDialog(null, "保存成功！");

			this.setVisible(false);
		}
	}

	//生成表格
	public class MytableModel extends DefaultTableModel {

		public MytableModel(Object[][] tableData, Object[] colnames) {
			super(tableData, colnames);
		}

		public boolean isCellEditable(int row, int column) {
			return true;
		}
	}
	//	public static void main(String[] args) {
	//		// TODO 自动生成的方法存根
	//		ShowTable s = new ShowTable();
	//	}

}
