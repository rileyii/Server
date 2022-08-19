import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlHelp {
	static final String Driver = "com.mysql.cj.jdbc.Driver";
	static final String DATABASE_URL = "jdbc:mysql://localhost/inc_history?serverTimezone=GMT%2B8&useSSL=false";
	static final String USERNAME = "root";
	static final String PASSWORD = "123456";
	
	private Connection connection = null;
	private PreparedStatement pStatement = null;
	private ResultSet rSet = null;
	

	// 链接数据库
	public void  connectDB() throws ClassNotFoundException {
		try {
			Class.forName(Driver);
			connection = DriverManager.getConnection( 
					DATABASE_URL, USERNAME, PASSWORD );
			System.out.println("数据库链接成功");
		} catch (SQLException e) {
			System.out.println("数据库链接失败");
			e.printStackTrace();
		}
	}
	
	// 关闭资源
	public void close() {
		if(rSet != null) {
			try {
				rSet.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(pStatement != null) {
			try {
				pStatement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//增加时间
	public  void addInc(String time,String os,String raley,String result) throws SQLException {
				
				// try finally 无论是否抛出异常都将执行 finally 中的语句
			try {
				// 先链接到数据库
				connectDB();
					
				String addsql = "insert into history values (?,?,?,?)";		
				pStatement = connection.prepareStatement(addsql);			
				pStatement.setString(1, time);
				pStatement.setString(2, os);
				pStatement.setString(3, raley);
				pStatement.setString(4, result);
				pStatement.executeUpdate();
					
			} catch (ClassNotFoundException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			} finally {
					
				close();
					
				}
				
		}
}
