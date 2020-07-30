package request_transmitters;

import java.sql.*;

import request_types.*;

public class SqlRequestTransmitter extends Transmitter{
	
	public Connection sqlcon = null;
	

	@Override
	public void setUpConnection(String params[]) throws Exception {
		//params:
		//[0] Target Database name
		//[1] Target Database user
		//[2] Target Database user password
		//[3] Target Table name
		
		//db driver registration
		Class.forName("com.mysql.jdbc.Driver"); 

		//connection
		Connection sqlcon = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/" + 
				params[0] +
				"?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
				params[1],
				params[2]); 
		this.sqlcon = sqlcon;
		
	}


	@Override
	public void performIORequest(IORequest request) throws Exception {
		
		System.out.println("performing the IO request: " + request.id);
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
