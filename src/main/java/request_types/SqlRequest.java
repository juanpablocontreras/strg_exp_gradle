package request_types;

public class SqlRequest extends IORequest{
	

	public SqlRequest( 
			long size, 
			long id,
			String operation,
			OperationType optype,
			boolean isLastItem)
	{
		super(size, id, operation,optype,isLastItem);
	}
}
