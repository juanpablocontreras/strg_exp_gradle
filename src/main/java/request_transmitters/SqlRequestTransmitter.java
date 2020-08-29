package request_transmitters;

import java.sql.*;
import request_types.*;
import experiment.Transmitter_setter;

public class SqlRequestTransmitter extends Transmitter{
	
	public Connection sqlcon = null;
	
	

	@Override
	public void setUpConnection() throws Exception {
		
		//get settings
		Transmitter_setter settings = new Transmitter_setter();
		
		//db driver registration
		Class.forName("com.mysql.jdbc.Driver"); 

		//connection
		Connection sqlcon = DriverManager.getConnection(settings.connectionStr, settings.username, settings.password);
				//"jdbc:mysql://target-instance.cauebsweajza.us-east-2.rds.amazonaws.com" + 
				//":3306/" + 
				//params[0] +
				//"?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false",
				//params[1],
				//params[2]
				//		); 
		this.sqlcon = sqlcon;
		
	}


	@Override
	public void performIORequest(IORequest request) throws Exception {
		
		//System.out.println("performing the IO request: " + request.id);
		//System.out.println("Query: " + request.operation);
		
		Statement stmt = this.sqlcon.createStatement(); 
		
		switch(request.optype) {
		case GET:
			stmt.executeQuery(request.operation);
			break;
		case POST:
		case PUT:
		case PATCH:
		case DELETE:
			stmt.executeUpdate(request.operation);
			break;
		default:
			stmt.executeQuery(request.operation);
			break;
		}
	}


	@Override
	public void closeConnection() throws Exception {
		
		this.sqlcon.close();
		//System.out.println("closing transmitter connection");
	}
	
}
