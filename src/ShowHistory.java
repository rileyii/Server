import java.awt.BorderLayout;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class ShowHistory extends JFrame{
	static final String DATABASE_URL = "jdbc:mysql://localhost/inc_history?serverTimezone=GMT%2B8&useSSL=false";
	static final String USERNAME = "root";
	static final String PASSWORD = "123456";
	private String q = "SELECT * FROM history";

	private ResultSetTableModel tableModel;
	ShowHistory(){
		this.setTitle("历史记录");
		this.setSize(900,600);
		//打开页面时显示所有的书本

		try {
			tableModel = new ResultSetTableModel(DATABASE_URL,
					USERNAME, PASSWORD,q);
			JTable resultTable = new JTable( tableModel );		
			resultTable.setAutoCreateRowSorter(true);
			JScrollPane scroll = new JScrollPane(resultTable);
			this.add(scroll,BorderLayout.CENTER);
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
	}
}
