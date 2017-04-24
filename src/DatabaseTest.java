import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import securityDB.SecurityDB;

public class DatabaseTest {

	public static void main(String args[]) throws IOException
	{
		SecurityDB secDB = null;
		try 
		{
			secDB = new SecurityDB("security-db-mysql.cy89i85gvki0.us-west-2.rds.amazonaws.com:3306/security", "jbhz", "zxczxczxc", "76d3efaa9f74c950abbfda55497f585e");
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
			File picFile = new File("pic1.png");
			File videoFile = new File("video1.mp4");
			
			FileInputStream fileBinaryStream = new FileInputStream(picFile);
			byte[] picFileData = new byte[fileBinaryStream.available()];
			fileBinaryStream.read(picFileData);
			fileBinaryStream.close();
			
			fileBinaryStream = new FileInputStream(videoFile);
			byte[] videoFileData = new byte[fileBinaryStream.available()];
			fileBinaryStream.read(videoFileData);
			fileBinaryStream.close();
			
			long duration = 6000000;
			
			Date startTime = new Date(System.currentTimeMillis());
			
			SecurityDB.PostVideoErrorType postResult = secDB.PostVideo(startTime, duration, picFileData, videoFileData, 1280, 720, "mp4");
			switch(postResult)
			{
			case Success:
				System.out.println("Success!");
				break;
			case DatabaseRejected:
			case InternalError:
			case InvalidCameraUID:
			case InvalidResolution:
			case InvalidVideoData:
			default:
				System.out.println("Error!");
				System.out.println(secDB.GetLastErrorMessage());
				break;
			}
		}
	}

}
