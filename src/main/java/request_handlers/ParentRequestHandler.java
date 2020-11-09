package request_handlers;

import java.io.PrintWriter;
import java.io.FileWriter;

import java.io.IOException;
import java.util.ArrayList;

import experiment.Handler_setter;
import ioQueues.SyncListIOQueue;
import request_transmitters.Transmitter;
import request_types.IORequest;

public class ParentRequestHandler extends Thread{
	
	//IO request Queue
	protected SyncListIOQueue ioRequestQueue;
	
	//Transmitter
	protected Transmitter transmitter;
	
	//Handler Setter
	protected Handler_setter settings;
	
	//Output writing variables
	protected String logFolderPath = "logs";
	protected String logIdentifier;
	protected long batch_number = 0;
	protected boolean append_to_file = false;
	
	
	//Fields that should be in the settings:
	protected boolean output_batch_speed = false;
	protected boolean output_batch_size = false;
	protected boolean output_connection_creation_times = true;
	protected boolean output_close_connection_times = true;
	protected boolean output_exec_times = false;
	protected boolean output_io_queue_times_w = false;
	protected int pollingTime = 100; //amount of time between unsuccessful polls
	protected int numAttempts = 10; //number of attempts to get item from queue before proceeding to the data transfer anyway
	
	//variables for control flow
	protected boolean wasLastItemProcessed = false;
	
	
	//Constructor
	public ParentRequestHandler(	
			SyncListIOQueue requestQeue,
			Transmitter transmitter,
			String logIdentifier) throws Exception
	{
		if(requestQeue == null) {
			throw new Exception("constructor queue not initialized");
		}
		this.ioRequestQueue = requestQeue;
		this.transmitter = transmitter;
		this.logIdentifier = logIdentifier;
	}
	
	
	@Override
	public void run() {
		try {
			System.out.println("Request Handler running...");
			
			//Get and apply settings
			this.settings = new Handler_setter();
			this.settings.getSettings();
			
			while(!wasLastItemProcessed) {
				//each iteration builds a batch
				
				//System.out.println("starting a new batch...");
				
				//internal list to keep the batch's IO requests
				ArrayList<IORequest> dataTransferIORequests = new ArrayList<IORequest>(); 
				
				//set up initial batch properties
				Metrics metrics = new Metrics(0,0,System.nanoTime()); 
				
				//get data to be sent
				IORequest tempRequest;
				while(!is_batch_ready(this.settings, metrics)) {
					//get an IO request from the IO request queue into the batch 's internal list of IO requests
					
					//try getting an item from the IO queue
					tempRequest = this.ioRequestQueue.poll();
					if(tempRequest == null) {
						int attempt = numAttempts;
						
						while(tempRequest == null) {
							Thread.sleep(pollingTime);
							
							if(attempt-- <= 0) {
								//all attempts were made to get the item from queue
								//send batch with as many requests as it already has 
								if(metrics.num_io_requests_in_batch > 0) {
									//send request
									//write measurements of speed to the logs file
									send_data_transfer(dataTransferIORequests, metrics);
								}
								
								System.out.println("All attemps done...");
								System.out.println("is io queue empty?:  " + this.ioRequestQueue.isEmpty());
								return;
							}
							
							//try polling again
							tempRequest = this.ioRequestQueue.poll();
						}
					}
					
					//add request to data transfer batch
					dataTransferIORequests.add(tempRequest);
					
					//update batch properties
					metrics.num_io_requests_in_batch++;
					metrics.batch_size_in_bytes += tempRequest.size;
				}
				
				metrics.data_stransfer_end_time = System.nanoTime();
				
				//execute requests using transmitter
				send_data_transfer(dataTransferIORequests, metrics);
				
				//write metrics to the logs
				output_metrics(metrics);
				
				//update batch number
				batch_number++;
				
			} //end while for each data transfer
			
			
		}catch(Exception e) {
			System.out.println("Error in Request Handler");
			System.out.println(e);
		}
	}
	
	
	/**
	 * Determines if the batch is ready to be sent to the target database / file system
	 * @param settings
	 * @param num_io_requests_in_batch
	 * @param num_bytes_in_batch
	 * @param building_batch_start_time
	 * @return
	 */
	boolean is_batch_ready(Handler_setter settings, Metrics metrics) {
		
		switch(settings.max_type) {
		
		case SIZE:
			if(metrics.batch_size_in_bytes < settings.max_type_num)
				return false;
			return true;
			
		case NUM_IO_REQUESTS:
			if(metrics.num_io_requests_in_batch < settings.max_type_num)
				return false;
			return true;
			
		case PERIOD:
			if((System.nanoTime() - metrics.data_trasnfer_start_time) < settings.max_type_num)
				return false;
			return true;
		}
		
		return false;
	}
	
	
	private void send_data_transfer(ArrayList<IORequest> requests, Metrics metrics) throws Exception {
		//transmits requests and returns number of bytes transmitted
		
		long start;
		long end;
		
		//set up connection
		start = System.nanoTime();
		this.transmitter.setUpConnection();
		end = System.nanoTime();
		
		metrics.create_connection_times.add(end-start); //log time for connection
		
		//process requests
		for(IORequest request:requests) {
			transmitter.performIORequest(request);
			
			//check if last item was processed
			if(request.isLastItem) {
				this.wasLastItemProcessed = true;
				//System.out.println("All items processed. experiment finished");
			}
		}
		
		//close connection
		start = System.nanoTime();
		transmitter.closeConnection();
		end = System.nanoTime();
		
		metrics.close_connection_times.add(end-start);
	}
	
	
	private void output_metrics(Metrics metrics) throws IOException {
		
		
		if(this.output_batch_size) {
			FileWriter write = new FileWriter(this.logFolderPath + "/byte_size_" + this.logIdentifier, this.append_to_file);
			PrintWriter pwriter = new PrintWriter(write);
			
			pwriter.println(metrics.batch_size_in_bytes);
			
			pwriter.close();
			write.close();
		}
		
		
		if(this.output_batch_speed) {
			FileWriter write = new FileWriter(this.logFolderPath + "/batch_speed_" + this.logIdentifier, this.append_to_file);
			PrintWriter pwriter = new PrintWriter(write);
			
			double speed = (metrics.data_stransfer_end_time - metrics.data_trasnfer_start_time)/metrics.batch_size_in_bytes;
			pwriter.println(speed);
			
			pwriter.close();
			write.close();
		}
		
		
		if(this.output_close_connection_times) {
			FileWriter write = new FileWriter(this.logFolderPath + "/close_con" + this.logIdentifier, this.append_to_file);
			PrintWriter pwriter = new PrintWriter(write);
			
			for(Long time:metrics.close_connection_times) {
				pwriter.println(time);
			}
			
			pwriter.close();
			write.close();
		}
		
		
		if(this.output_connection_creation_times) {
			FileWriter write = new FileWriter(this.logFolderPath + "/create_con" + this.logIdentifier, this.append_to_file);
			PrintWriter pwriter = new PrintWriter(write);
			
			for(Long time:metrics.create_connection_times) {
				pwriter.println(time);
			}
			
			pwriter.close();
			write.close();
		}
		
		
		if(this.output_exec_times) {
			FileWriter write = new FileWriter(this.logFolderPath + "/exec_" + this.logIdentifier, this.append_to_file);
			PrintWriter pwriter = new PrintWriter(write);
			
			for(Long time:metrics.exec_times) {
				pwriter.println(time);
			}
			
			pwriter.close();
			write.close();
		}
		
		if(this.output_io_queue_times_w) {
			FileWriter write = new FileWriter(this.logFolderPath + "/queue_" + this.logIdentifier, this.append_to_file);
			PrintWriter pwriter = new PrintWriter(write);
			
			for(Long time:metrics.io_queue_times) {
				pwriter.println(time);
			}
			
			pwriter.close();
			write.close();
		}
		
		if(!this.append_to_file)
			this.append_to_file = true;
		
		
	}
	
}


final class Metrics{
	public long batch_size_in_bytes;
	public int num_io_requests_in_batch;
	public long data_trasnfer_start_time;
	public long data_stransfer_end_time;
	public ArrayList<Long> create_connection_times;
	public ArrayList<Long> close_connection_times;
	public ArrayList<Long> exec_times;
	public ArrayList<Long> io_queue_times;
	
	public Metrics(
			long batch_size_in_bytes,
			int num_io_requests_in_batch,
			long data_trasnfer_start_time) {
		
		this.data_trasnfer_start_time = data_trasnfer_start_time;
		this.data_stransfer_end_time = 0;
		
		this.batch_size_in_bytes = batch_size_in_bytes;
		this.num_io_requests_in_batch = num_io_requests_in_batch;
		
		create_connection_times = new ArrayList<Long>();
		close_connection_times = new ArrayList<Long>();
		exec_times = new ArrayList<Long>();
		io_queue_times = new ArrayList<Long>();
	}
}
