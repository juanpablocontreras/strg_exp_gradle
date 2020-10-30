package request_handlers;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import experiment.Handler_setter;
import ioQueues.SyncListIOQueue;
import request_transmitters.Transmitter;
import request_types.IORequest;

public class PeriodicRequestHandler extends Thread{
	//Creates a batch and sends it to be executed by the target IO after a fixed amount of time
	//Corresponds to scenario 2 of the experiment
	
	long period = 0;
	
	protected SyncListIOQueue ioRequestQueue; //reference to the IORequestQueue
	protected int pollingTime = 100; //amount of time between unsuccessful polls
	protected int numAttempts = 10; //number of attempts to get item from queue before proceeding to the data transfer anyway
	protected Transmitter transmitter;
	protected boolean wasLastItemProcessed = false;
	protected Handler_setter settings;
	
	protected String logFolderPath = "logs";
	protected String logIdentifier;
	protected PrintWriter batch_avg_speed_line;
	protected boolean append_to_file = false;
	
	
	
	public PeriodicRequestHandler(	
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
			System.out.println("Periodic Handler running...");
			//Get and apply settings
			this.settings = new Handler_setter();
			this.settings.getSettings();
			
			//list to hold the IO requests for one data transfer
			ArrayList<IORequest> dataTransferIORequests;
			
			//get fixed period
			this.period = this.settings.max_type_num;
			System.out.println("Fixed Period: " + this.period);
			
			
			
			while(!wasLastItemProcessed) {
				//each iteration does one data transfer
				//System.out.println("starting new data transfer");
				
				dataTransferIORequests = new ArrayList<IORequest>();
				
				
				//get data to be sent
				IORequest tempRequest;
				int dt_size = 0; //size of data transfer in bytes
				long dt_start = System.currentTimeMillis();
				while(System.currentTimeMillis() - dt_start < this.period) { //Gets all IO requests in the batch
					
					//System.out.println("getting io request from queue");
					
					//try getting an item from the IO queue
					tempRequest = this.ioRequestQueue.poll();
					if(tempRequest == null) {
						int attempt = numAttempts;
						
						while(tempRequest == null) {
							Thread.sleep(pollingTime);
							
							if(attempt-- <= 0) {
								//all attempts were made to get the item from queue
								//send batch with as many requests as it already has 
								if(dt_size > 0) {
									//send request
									//write measurements of speed to the logs file
									transmit(dataTransferIORequests, dt_start, System.currentTimeMillis());
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
				}
				
				//execute requests using transmitter
				transmit(dataTransferIORequests, dt_start, System.currentTimeMillis());
				
				//System.out.println("data transfer happened");
			}
			
		}catch(Exception e) {
			System.out.println("Error in Periodic Request Handler");
			System.out.println(e);
		}
	}
	
	
	private void transmit(ArrayList<IORequest> requests, long start_time, long endTime) throws Exception{
		if(requests != null && !requests.isEmpty()) {
			long transfer_size = send_data_transfer(requests);
			write_speed(start_time, endTime, transfer_size);
		}
	}
	
	private long send_data_transfer(ArrayList<IORequest> requests) throws Exception {
		//transmits requests and returns number of bytes transmitted
		
		//set up connection
		this.transmitter.setUpConnection();
		
		//start number of bytes at zero
		long num_bytes = 0;
		
		//process requests
		for(IORequest request:requests) {
			transmitter.performIORequest(request);
			num_bytes += request.size;
			
			//check if last item was processed
			if(request.isLastItem) {
				this.wasLastItemProcessed = true;
				//System.out.println("All items processed. experiment finished");
			}
		}
		
		//close connection
		transmitter.closeConnection();
		
		return num_bytes;
	}

	
	private void write_speed(long start_time, long end_time, long num_bytes_transfered ) throws IOException {
		long duration = end_time - start_time;
		long speed = num_bytes_transfered / duration;
		
		FileWriter write = new FileWriter(this.logFolderPath + "/speeds" + this.logIdentifier, this.append_to_file);
		this.batch_avg_speed_line = new PrintWriter(write);
		
		this.batch_avg_speed_line.println(speed);
		
		this.batch_avg_speed_line.close();
		
		if(this.append_to_file == false) 
			this.append_to_file = true;
		
	}
	
	
	
}
