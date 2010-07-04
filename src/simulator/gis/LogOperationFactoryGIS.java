package simulator.gis;

import java.io.RandomAccessFile;
import java.util.HashSet;

import simulator.LogOperationFactory;
import simulator.Operation;

public class LogOperationFactoryGIS extends LogOperationFactory {
	protected RandomAccessFile file = null;
	protected String curLine = null;
	protected long count;
	protected HashSet<String> ignoreOps = null;

	public LogOperationFactoryGIS(String fn) {
		super(fn);
		this.ignoreOps = new HashSet<String>();
	}

	public LogOperationFactoryGIS(String fn, String[] ignoreOps) {
		super(fn);
		this.ignoreOps = new HashSet<String>();
		for (String ignoreOp : ignoreOps) {
			this.ignoreOps.add(ignoreOp);
		}
	}

	@Override
	public Operation createOperation(String[] args) {
		if (ignoreOps.contains(args[1]))
			return new OperationGISDummy();

		if (args[1].equals(OperationGISAddNode.class.getName())) {
			return new OperationGISAddNode(args);
		}
    
		if (args[1].equals(OperationGISDeleteNode.class.getName())) {
			return new OperationGISDeleteNode(args);
		}

		if (args[1].equals(OperationGISShortestPathShort.class.getName())) {
			return new OperationGISShortestPathShort(args);
		}

		if (args[1].equals(OperationGISShortestPathLong.class.getName())) {
			return new OperationGISShortestPathLong(args);
		}

		if (args[1].equals(OperationGISShuffleNode.class.getName())) {
			return new OperationGISShuffleNode(args);
		}

		throw new Error("Unsupported GIS Operation: " + args[1]);
	}

}
