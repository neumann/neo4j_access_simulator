package simulator.twitter;

import simulator.LogOperationFactory;
import simulator.Operation;

public class TwitterLog_Factory extends LogOperationFactory {

	public TwitterLog_Factory(String fn) {
		super(fn);
	}

	@Override
	public Operation createOperation(String[] args) {
		if(args[1].equals(ReadOp_Search.class.getName())){
			return new ReadOp_Search(args);
		}
		throw new Error("Unsupported GIS Operation: " + args[1]);
	}

}
