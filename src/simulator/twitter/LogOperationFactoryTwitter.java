package simulator.twitter;

import simulator.Empty_Op;
import simulator.LogOperationFactory;
import simulator.Operation;
import simulator.twitter.operations.ReadOp_Search;
import simulator.twitter.operations.Shuffle_WriteOp;

public class LogOperationFactoryTwitter extends LogOperationFactory {

	public LogOperationFactoryTwitter(String fn) {
		super(fn);
	}

	@Override
	public Operation createOperation(String[] args) {
		if (args[1].contains("twitter") && args[1].contains(".ReadOp_Search")) {
			return new Empty_Op(args);
		}
		if (args[1].contains("twitter") && args[1].contains(".Shuffle_WriteOp")) {
			return new Shuffle_WriteOp(args);
		}
		throw new Error("Unsupported Twitter Operation: " + args[1]);
	}

}
