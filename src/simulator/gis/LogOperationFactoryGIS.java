package simulator.gis;

import java.io.RandomAccessFile;

import simulator.LogOperationFactory;
import simulator.Operation;

public class LogOperationFactoryGIS extends LogOperationFactory {
	protected RandomAccessFile file = null;
	protected String curLine = null;
	protected long count;

	public LogOperationFactoryGIS(String fn) {
		super(fn);
	}

	@Override
	public Operation createOperation(String[] args) {
		if (args[1].equals(OperationGISAddNode.class.getName())) {
			return new OperationGISAddNode(args);
		}

		if (args[1].equals(OperationGISDeleteNode.class.getName())) {
			return new OperationGISDeleteNode(args);
		}

		if (args[1].equals(OperationGISShortestPathLocal.class.getName())) {
			return new OperationGISShortestPathLocal(args);
		}

		if (args[1].equals(OperationGISShortestPathGlobal.class.getName())) {
			return new OperationGISShortestPathGlobal(args);
		}

		throw new Error("Unsupported GIS Operation: " + args[1]);
	}

}
