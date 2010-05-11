package simulator.tree;

import simulator.LogOperationFactory;
import simulator.Operation;

public class TreeLog_Factory extends LogOperationFactory {

	public TreeLog_Factory(String fn) {
		super(fn);
	}

	@Override
	public Operation createOperation(String[] args) {
		if(args[1].equals(LogReadOp_CountFiles.class.getName())){
			return new LogReadOp_CountFiles(args);
		}
		else if(args[1].equals(LogWriteOp_AddFile.class.getName())){
			return new LogWriteOp_AddFile(args);
		}
		else if(args[1].equals(LogWriteOp_DeleteItems.class.getName())){
			return new LogWriteOp_DeleteItems(args);
		}
		throw new Error("Unsupported GIS Operation: " + args[1]);
	}

}
