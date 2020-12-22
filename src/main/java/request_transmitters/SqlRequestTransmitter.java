package request_transmitters;

import java.sql.*;

import control_setters.Transmitter_setter;
import request_types.*;
import experiment.Batch_metrics;

public class SqlRequestTransmitter extends Transmitter{
	
	public Connection sqlcon = null;
	
	

	@Override
	public void setUpConnection(Batch_metrics batch_metrics) throws Exception {
		
		//get settings
		Transmitter_setter settings = new Transmitter_setter();
		
		//db driver registration
		Class.forName("com.mysql.jdbc.Driver");
		
		long start_t = System.nanoTime();

		//connection
		Connection sqlcon = DriverManager.getConnection(settings.connectionStr, settings.username, settings.password);
		
		long end_t = System.nanoTime();
		this.sqlcon = sqlcon;
		
		//add metrics measurements
		batch_metrics.create_connection_time = end_t - start_t;
		
	}
	


	@Override
	public void executeRequest(IORequest request, Batch_metrics batch_metrics) throws Exception {
		
		//System.out.println("performing the IO request: " + request.id);
		//System.out.println("Query: " + request.operation);
		
		long start_t;
		long end_t;
		
		start_t = System.nanoTime();
		Statement stmt = this.sqlcon.createStatement(); 
		end_t = System.nanoTime();
		
		batch_metrics.create_statement_times.add(end_t - start_t);
		
		start_t = System.nanoTime();
		
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
		
		end_t = System.nanoTime();
		
		batch_metrics.exec_times.add(end_t - start_t);
	}


	@Override
	public void closeConnection(Batch_metrics batch_metrics) throws Exception {
		
		long start_t = System.nanoTime();
		this.sqlcon.close();
		long end_t = System.nanoTime();
		
		batch_metrics.close_connection_time = end_t - start_t;
	}
	
}
