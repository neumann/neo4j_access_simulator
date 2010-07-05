package simulator.tree;

import simulator.LogOperationFactory;
import simulator.Operation;
import simulator.tree.operations.AddFile_WriteOp;
import simulator.tree.operations.CountFiles_ReadOp;
import simulator.tree.operations.DeleteItems_WriteOp;
import simulator.tree.operations.SearchFiles_ReadOp;
import simulator.tree.operations.Shuffle_WriteOp;

public class LogOperationFactoryTree extends LogOperationFactory {

	public LogOperationFactoryTree(String fn) {
		super(fn);
	}

	@Override
	public Operation createOperation(String[] args) {
		if (args[1].equals(CountFiles_ReadOp.class.getName())) {
			return new CountFiles_ReadOp(args);
		} else if (args[1].equals(AddFile_WriteOp.class.getName())) {
			return new AddFile_WriteOp(args);
		} else if (args[1].equals(DeleteItems_WriteOp.class.getName())) {
			return new DeleteItems_WriteOp(args);
		} else if (args[1].equals(SearchFiles_ReadOp.class.getName())) {
			return new SearchFiles_ReadOp(args);
		} else if (args[1].equals(Shuffle_WriteOp.class.getName())) {
			return new Shuffle_WriteOp(args);
		}
		throw new Error("Unsupported Tree Operation: " + args[1]);
	}

}
