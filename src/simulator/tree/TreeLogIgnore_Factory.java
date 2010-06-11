package simulator.tree;

import simulator.LogOperationFactory;
import simulator.Operation;

public class TreeLogIgnore_Factory extends LogOperationFactory {

	public TreeLogIgnore_Factory(String fn) {
		super(fn);
	}

	@Override
	public Operation createOperation(String[] args) {
		if(args[1].equals(CountFiles_ReadOp.class.getName())){
			return new Empty_Op(args);
		}
		else if(args[1].equals(AddFile_WriteOp.class.getName())){
			return new AddFile_WriteOp(args);
		}
		else if(args[1].equals(DeleteItems_WriteOp.class.getName())){
			return new DeleteItems_WriteOp(args);
		}
		else if(args[1].equals(SearchFiles_ReadOp.class.getName())){
			return new Empty_Op(args);
		}
		throw new Error("Unsupported GIS Operation: " + args[1]);
	}

}
