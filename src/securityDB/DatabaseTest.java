package securityDB;
import java.sql.SQLException;

import securityDB.SecurityDB;

public class DatabaseTest {

	public static void main(String args[])
	{
		SecurityDB secDB = null;
		try 
		{
			secDB = new SecurityDB("security-db-mysql.cy89i85gvki0.us-west-2.rds.amazonaws.com:3306/security", "jbhz", "zxczxczxc");
			System.out.println("Connected!");
		} 
		catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		if(secDB != null)
		{
			secDB.Test();
		}
	}

}
