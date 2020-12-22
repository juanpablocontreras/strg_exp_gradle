package global_enums;

public enum Creator_Distribution {
	// The distribution the Creator will use to put items into the IO Request Queue.
	
	NONE, 			//No distribution. All database items are loaded in the queue before request handler is created
	CONSTANT,		//Database items are put into the IO Request queue at constant intervals
	STANDARD,		
	NORMAL,
	POISSON,
	
}
