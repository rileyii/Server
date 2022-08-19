


import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.swing.table.AbstractTableModel;

//构建表格
public class ResultSetTableModel extends AbstractTableModel 
{
	private Connection connection;
	private Statement statement;
	private ResultSet resultSet;
	private ResultSetMetaData metaData;
	private int numberOfRows;
	private String[] name= {
			"时间点","门口机","Raley","事件结果"	}; 
	private int col = 0;
	private boolean connectedToDatabase = false;	
	public ResultSetTableModel( String url, String username,
			String password, String query ) throws SQLException
	{         

		connection = DriverManager.getConnection( url, username, password );
		statement = connection.createStatement( 
				ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY );

		connectedToDatabase = true;

		setQuery( query );
	}

	public Class getColumnClass( int column ) throws IllegalStateException
	{

		if ( !connectedToDatabase ) 
			throw new IllegalStateException( "Not Connected to Database" );

		try 
		{
			String className =  metaData.getColumnClassName( column + 1 );
			return Class.forName( className );
		} 
		catch ( Exception exception ) 
		{
			exception.printStackTrace();
		} 

		return Object.class; 
	} 


	public int getColumnCount() throws IllegalStateException
	{   

		if ( !connectedToDatabase ) 
			throw new IllegalStateException( "Not Connected to Database" );

		try 
		{
			return metaData.getColumnCount(); 
		} 
		catch ( SQLException sqlException ) 
		{
			sqlException.printStackTrace();
		}

		return 0; 
	} 

	public String getColumnName( int column ) throws IllegalStateException
	{    

		if ( !connectedToDatabase ) 
			throw new IllegalStateException( "Not Connected to Database" );
		if(col<=3) {  
			return name[col++]; 
		}  
		if(col>3) {
			col = 0;
			return name[col++];
		}
		return "";
	} 

	// 返回结果表的行数
	public int getRowCount() throws IllegalStateException
	{       
		if ( !connectedToDatabase ) 
			throw new IllegalStateException( "Not Connected to Database" );

		return numberOfRows;
	} 

	//得到在某列某行位置的内容
	public Object getValueAt( int row, int column ) 
			throws IllegalStateException
	{

		if ( !connectedToDatabase ) 
			throw new IllegalStateException( "Not Connected to Database" );
		try 
		{
			resultSet.absolute( row + 1 );
			return resultSet.getObject( column + 1 );
		} 
		catch ( SQLException sqlException ) 
		{
			sqlException.printStackTrace();
		} 

		return ""; 
	} 


	public void setQuery( String query ) 
			throws SQLException, IllegalStateException 
	{

		if ( !connectedToDatabase ) 
			throw new IllegalStateException( "Not Connected to Database" );

		resultSet = statement.executeQuery( query );

		metaData = resultSet.getMetaData();

		resultSet.last();                  
		numberOfRows = resultSet.getRow();      

		fireTableStructureChanged();
	} 

	// close Statement and Connection               
	public void disconnectFromDatabase()            
	{              
		if ( connectedToDatabase )                  
		{
			// close Statement and Connection            
			try                                          
			{                                            
				resultSet.close();                        
				statement.close();                        
				connection.close();                       
			} // end try                                 
			catch ( SQLException sqlException )          
			{                                            
				sqlException.printStackTrace();           
			} // end catch                               
			finally  // update database connection status
			{                                            
				connectedToDatabase = false;              
			} // end finally                             
		} // end if
	} // end method disconnectFromDatabase          
}  // end class ResultSetTableModel




