package securityDB;
import java.sql.*;

public class SecurityDB 
{
	private Connection dbConn = null;
	
	private SecurityDB()
	{}
	
	public SecurityDB(final String address, final String username, final String password) 
			throws InstantiationException, 
			IllegalAccessException, 
			ClassNotFoundException, 
			SQLException 
	{
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		dbConn = DriverManager.getConnection("jdbc:mysql://"+ address + "?" +
                "user=" + username + "&password=" + password + "");
	}
	
	public void Test()
	{
		try 
		{
			Statement stmt = dbConn.createStatement();
			ResultSet rs = stmt.executeQuery(
				"SELECT * "
				+ "FROM Security_Officer "
			); 
			
			final int columns = rs.getMetaData().getColumnCount();
			
			for (int i = 1; i <= columns; i++)
			{
				System.out.printf("%-15s", rs.getMetaData().getColumnName(i));
			}
			System.out.println();
			
			while (rs.next()) 
			{
				for (int i = 1; i <= columns; i++) 
				{
					System.out.printf("%-15s", rs.getString(i));
				}
				System.out.println();
			}
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
