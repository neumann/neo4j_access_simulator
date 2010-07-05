package simulator.twitter;

import simulator.LogOperationFactory;
import simulator.Operation;
import simulator.twitter.operations.ReadOp_Search;
import simulator.twitter.operations.Shuffle_WriteOp;

public class TwitterLog_Factory extends LogOperationFactory {

	public TwitterLog_Factory(String fn) {
		super(fn);
	}

	@Override
	public Operation createOperation(String[] args) {
		if (args[1].contains("twitter") && args[1].contains(".ReadOp_Search")) {
			return new ReadOp_Search(args);
		}
		if (args[1].contains("twitter") && args[1].contains(".Shuffle_WriteOp")) {
			return new Shuffle_WriteOp(args);
		}
		throw new Error("Unsupported Twitter Operation: " + args[1]);
	}

}
