package securityDB;
import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.Date;

/**
 * This class provides the interface to post surveillance videos, and raise alarm event to the security database.
 * @version 1.0
 * @author <a href="mailto:jabrack@mix.wvu.edu">Jordan Brack</a>,  <a href="mailto:hazheng@mix.wvu.edu">Haofan Zheng</a>
 *
 */
public class SecurityDB 
{
	enum PostVideoErrorType
	{
		Success,
		InvalidResolution,
		InvalidCameraUID,
		InvalidVideoData,
		DatabaseRejected,
		InternalError
	}
	
	enum RaiseAlarmErrorType
	{
		Success,
		InvalidCameraUID,
		InvalidDuration,
		DatabaseRejected,
		InternalError
	}
	
	private Connection dbConn = null;
	private final String m_cameraUID;
	private String m_lastErrorMsg;
	
	private boolean IsCameraUIDValid() throws SQLException
	{
		final String queryCamUID = 
				"SELECT 1 "
				+ "FROM Camera "
				+ "WHERE Camera_UID = ? ";
		PreparedStatement prep = dbConn.prepareStatement(queryCamUID);
		prep.setString(1, m_cameraUID);
		ResultSet result = prep.executeQuery();
		
		return result.next();
	}
	
	private final String GetCameraSpotUUID() throws SQLException
	{
		final String queryCamUID = 
				"SELECT Spot_UUID "
				+ "FROM Camera "
				+ "WHERE Camera_UID = ? ";
		PreparedStatement prep = dbConn.prepareStatement(queryCamUID);
		prep.setString(1, m_cameraUID);
		ResultSet result = prep.executeQuery();
		
		result.next();
		return result.getString(1);
	}
	
	public SecurityDB(final String address, final String username, final String password, final String cameraUID) 
			throws InstantiationException, 
			IllegalAccessException, 
			ClassNotFoundException, 
			SQLException 
	{
		m_lastErrorMsg = "";
		m_cameraUID = cameraUID;
		
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		dbConn = DriverManager.getConnection("jdbc:mysql://"+ address + "?" +
                "user=" + username + "&password=" + password + "");
	}
	
	public PostVideoErrorType PostVideo(
			final Date startTime, 
			final long durationMicroSec, 
			final byte[] thumbnailData, 
			final byte[] videoData, 
			final int resolutionWidth, 
			final int resolutionHeight,
			final String videoFormat)
	{
		m_lastErrorMsg = "";
		if(resolutionWidth <= 0 || resolutionWidth <= 0)
		{
			return PostVideoErrorType.InvalidResolution;
		}
		
		if(videoData.length <= 0)
		{
			return PostVideoErrorType.InvalidVideoData;
		}
		
		try 
		{
			if(!IsCameraUIDValid())
			{
				return PostVideoErrorType.InvalidCameraUID;
			}
			
			final String queryPostRecord = 
					"INSERT INTO `Surveillance_Video`(`Start_Time`, `Thumbnail`, `Video_Data`, `Camera_UID`, `Duration_us`, `Resolution_Width`, `Resolution_Height`, `Video_Format`) "
					+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?) ";
			PreparedStatement prep = dbConn.prepareStatement(queryPostRecord);
			
			prep.setTimestamp(1, new Timestamp(startTime.getTime()));
			prep.setBinaryStream(2, new ByteArrayInputStream(thumbnailData), thumbnailData.length);
			prep.setBinaryStream(3, new ByteArrayInputStream(videoData), videoData.length);
			prep.setString(4, m_cameraUID);
			prep.setLong(5, durationMicroSec);
			prep.setInt(6, resolutionWidth);
			prep.setInt(7, resolutionHeight);
			prep.setString(8, videoFormat);
			
			final int insertResult = prep.executeUpdate();
			
			if(insertResult <= 0)
			{
				return PostVideoErrorType.DatabaseRejected;
			}
		} 
		catch (SQLException e) 
		{
			m_lastErrorMsg = e.getMessage();
			return PostVideoErrorType.InternalError;
		}
		return PostVideoErrorType.Success;
	}
	
	public RaiseAlarmErrorType RaiseAlarm(
			final Date startTime, 
			final Date endTime
			)
	{
		m_lastErrorMsg = "";
		
		try 
		{
			if(!IsCameraUIDValid())
			{
				return RaiseAlarmErrorType.InvalidCameraUID;
			}
			
			final String spotUUID = GetCameraSpotUUID();
			
			final String queryRaiseAlarm = 
					"INSERT INTO `Alarm_Event`(`Start_Time`, `End_Time`, `Spot_UUID`) "
					+ "VALUES(?, ?, ?) ";
			PreparedStatement prep = dbConn.prepareStatement(queryRaiseAlarm);
			
			prep.setTimestamp(1, new Timestamp(startTime.getTime()));
			prep.setTimestamp(2, new Timestamp(endTime.getTime()));
			prep.setString(3, spotUUID);
			
			final int insertResult = prep.executeUpdate();
			
			if(insertResult <= 0)
			{
				return RaiseAlarmErrorType.DatabaseRejected;
			}
		} 
		catch (SQLException e) 
		{
			m_lastErrorMsg = e.getMessage();
			return RaiseAlarmErrorType.InternalError;
		}
		return RaiseAlarmErrorType.Success;
	}
	
	public RaiseAlarmErrorType RaiseAlarm(
			final Date startTime, 
			final int durationSec
			)
	{
		m_lastErrorMsg = "";
		
		if(durationSec <= 0)
		{
			return RaiseAlarmErrorType.InvalidDuration;
		}
		
		try 
		{
			if(!IsCameraUIDValid())
			{
				return RaiseAlarmErrorType.InvalidCameraUID;
			}
			
			final String spotUUID = GetCameraSpotUUID();
			
			final String queryRaiseAlarm = 
					"INSERT INTO `Alarm_Event`(`Start_Time`, `Duration_s`, `Spot_UUID`) "
					+ "VALUES(?, ?, ?) ";
			PreparedStatement prep = dbConn.prepareStatement(queryRaiseAlarm);
			
			prep.setTimestamp(1, new Timestamp(startTime.getTime()));
			prep.setInt(2, durationSec);
			prep.setString(3, spotUUID);
			
			final int insertResult = prep.executeUpdate();
			
			if(insertResult <= 0)
			{
				return RaiseAlarmErrorType.DatabaseRejected;
			}
		} 
		catch (SQLException e) 
		{
			m_lastErrorMsg = e.getMessage();
			return RaiseAlarmErrorType.InternalError;
		}
		return RaiseAlarmErrorType.Success;
	}
	
	public final String GetLastErrorMessage()
	{
		return m_lastErrorMsg;
	}

}
