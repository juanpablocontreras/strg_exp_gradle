package request_transmitters;

import request_types.IORequest;
import experiment.Batch_metrics;

public  abstract class Transmitter {
	
	
	public abstract void setUpConnection(Batch_metrics batch_metrics) throws Exception;
	
	public abstract void executeRequest(IORequest request, Batch_metrics batch_metrics) throws Exception;
	
	public abstract void closeConnection(Batch_metrics batch_metrics) throws Exception;
	
}
