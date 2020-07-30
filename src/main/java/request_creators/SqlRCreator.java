package request_creators;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;

import request_types.OperationType;
import request_types.SqlRequest;
import ioQueues.*;

public class SqlRCreator extends Thread {
	
	private SyncListIOQueue ioRequestQueue;
	private String orig_db_name;
	private String target_db_name;
	private String tableName;
	private String target_db_user;
	private String target_db_password;
	private int lastItemId = 99;
	
	public SqlRCreator(
			SyncListIOQueue queue, 
			String orig_db_name,
			String target_db_name,
			String target_db_user,
			String target_db_password,
			String tableName) 
	{
		this.ioRequestQueue = queue;
		this.orig_db_name = orig_db_name;
		this.target_db_user = target_db_user;
		this.target_db_password = target_db_password;
		this.target_db_name = target_db_name;
		this.tableName = tableName;
	}
	
	
	@Override
	public void run() {
		
		try {
			
			Class.forName("com.mysql.jdbc.Driver"); 
			
			//connection to origin db
			Connection sqlcon = DriverManager.getConnection(
			"jdbc:mysql://localhost:3306/" + 
			this.orig_db_name +
			"?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
			"juan",
			this.target_db_password); 
			
			//set up parameters for target db
			String[] params = new String[4];
			params[0] = this.target_db_name;
			params[1] = this.target_db_user;
			params[2] = this.target_db_password;
			params[3] = this.tableName;
			
			//get every row of table (2 columns: must be int id and varchar data)
			Statement stmt=sqlcon.createStatement(); 
			

			String str_i = "INSERT INTO " + this.tableName + " VALUES (";
			String str_e = ")";
			long startTime;
			long endTime;
			long totalTime;
			boolean isLastItem = false;
			
			//Get all 100 rows, one by one, and send them to the queue one by one
			for(int i=0; i<100; i++) {
				
				startTime = System.currentTimeMillis();
				
				//get row
				ResultSet rs = stmt.executeQuery("SELECT * FROM " + this.tableName + " WHERE id=" + i);
				
				//process row into an IORequest
				while(rs.next()) {
					String query = str_i;
					query += rs.getInt(1);
					query += ",";
					query += "\"" + rs.getString(2) + "\"";
					query += str_e;
					
					//indicate this is the last item to the handler using IORequest isLastItem field
					if(rs.getInt(1) == this.lastItemId) {
						isLastItem = true;
					}
							
					SqlRequest request = new SqlRequest(
							0, 				//size
							rs.getInt(1), 
							params, 
							query,
							OperationType.PUT,
							isLastItem);
					
					//put request in queue when space is available
					while(!this.ioRequestQueue.add(request)) {
						Thread.sleep(100);
					}
					
					endTime = System.currentTimeMillis();
					totalTime = endTime - startTime;
					//System.out.println("Creator created new item in " + totalTime);
				}
			}
			
			sqlcon.close();
			
		}catch(Exception e) {
			System.out.println(e);
		}
	}
}
