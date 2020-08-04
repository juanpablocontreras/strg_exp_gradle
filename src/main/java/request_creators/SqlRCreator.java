package request_creators;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import request_types.OperationType;
import request_types.SqlRequest;
import ioQueues.*;
import experiment.Creator_setter;

public class SqlRCreator extends Thread {
	
	private SyncListIOQueue ioRequestQueue;
	
	public SqlRCreator(SyncListIOQueue queue) 
	{
		this.ioRequestQueue = queue;
	}
	
	
	@Override
	public void run() {
		
		try {
			
			//getting settings
			Creator_setter settings = new Creator_setter();
			
			//System.out.println("starting creator");
			Class.forName("com.mysql.jdbc.Driver"); 
			
			//connection to origin db
			Connection sqlcon = DriverManager.getConnection(settings.connectionStr,settings.username,settings.password);
			
			
			//get every row of table (2 columns: must be int id and varchar data)
			Statement stmt=sqlcon.createStatement(); 

			String str_i = "INSERT INTO " + settings.table_name + " VALUES (";
			String str_e = ")";
			long startTime;
			long endTime;
			long totalTime;
			boolean isLastItem = false;
			
			//Get all 100 rows, one by one, and send them to the queue one by one
			for(int i=0; i<settings.total_number_of_items; i++) {
				
				startTime = System.currentTimeMillis();
				
				//get row
				ResultSet rs = stmt.executeQuery("SELECT * FROM " + settings.table_name + " WHERE id=" + i);
				
				//process row into an IORequest
				while(rs.next()) {
					String query = str_i;
					query += rs.getInt(1);
					query += ",";
					query += "\"" + rs.getString(2) + "\"";
					query += str_e;
					
					//indicate this is the last item to the handler using IORequest isLastItem field
					if(i == settings.total_number_of_items-1) {
						isLastItem = true;
					}
					
					SqlRequest request = new SqlRequest(
							0, 				//size
							rs.getInt(1),
							query,
							OperationType.PUT,
							isLastItem);
					
					
					//put request in queue when space is available
					while(!this.ioRequestQueue.add(request)) {
						Thread.sleep(100);
					}
					
					endTime = System.currentTimeMillis();
					totalTime = endTime - startTime;
				}
			}
			
			sqlcon.close();
			
		}catch(Exception e) {
			System.out.println("Creator error: ");
			System.out.println(e);
		}
	}
}
