package ioQueues;

import java.util.ArrayList;
import java.util.Iterator;

import request_types.IORequest;

public class SyncListIOQueue {
	
	private ArrayList<IORequest> queue; //Queue that holds the IO requests
	private int maxSize; //maximum size the Queue can grow to (maximum number of elements
	
	public SyncListIOQueue(int maxSize) {
		this.queue = new ArrayList<IORequest>();
		this.maxSize = maxSize;
	}
	
	
	public synchronized boolean add(IORequest request) {
		if(this.queue.size() < this.maxSize) {
			request.queueTimeArrival = System.currentTimeMillis();
			this.queue.add(request);
			//System.out.println("Request ID: " + request.id + " added to queue at time: " + request.queueTimeArrival);
			return true;
		}else {
			return false;
		}
	}
	
	public synchronized IORequest poll() {
		if(!this.queue.isEmpty()) {
			IORequest temp = this.queue.remove(0);
			temp.queueTimePolled = System.currentTimeMillis();
			//System.out.println("Request ID: " + temp.id + " Removed from Queue at time: " + temp.queueTimePolled);
			return temp;
		}else {
			return null;
		}
	}
	
	public synchronized boolean isEmpty() {
		return this.queue.isEmpty();
	}
	
	public synchronized IORequest peek() {
		return this.queue.get(0);
	}
	
	public synchronized Iterator<IORequest> iterator() {
		return this.queue.iterator();
	}
	
	public synchronized void printIds() {
		
		if(this.queue.isEmpty()) {
			System.out.println("Queue is empty");
		}else {
			System.out.println("Queue has elements:");
			this.queue.forEach((temp) -> System.out.println(temp.id));
		}
	}

}
