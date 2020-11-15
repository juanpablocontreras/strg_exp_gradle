package experiment;

import java.util.ArrayList;

public class Batch_metrics {
	public long id;
	
	//information about the batch
	public long batch_size_in_bytes;
	public int num_io_requests_in_batch;
	public long batch_creation_time; //batch creation time in milliseconds
	public long batch_completion_time; //time stamp when batch has executed all of its requests and closed connection
	
	
	public long create_connection_time;
	public long close_connection_time;
	public ArrayList<Long> create_statement_times;
	public ArrayList<Long> exec_times;
	public ArrayList<Long> io_queue_times;
	
	public Batch_metrics(long id, long batch_creation_time) {
		this.id = id;
		
		num_io_requests_in_batch = 0;
		batch_size_in_bytes = 0;
		this.batch_creation_time = batch_creation_time;
		
		create_statement_times = new ArrayList<Long>();
		exec_times = new ArrayList<Long>();
		io_queue_times = new ArrayList<Long>();
		
		
	}
}
