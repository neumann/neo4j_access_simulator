package simulator.gis;

import org.neo4j.graphdb.GraphDatabaseService;

import simulator.Operation;

public class OperationDeleteNode extends Operation {

	public OperationDeleteNode(long id, String[] args) {
		super(id, args);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onExecute(GraphDatabaseService db) {
		// TODO Auto-generated method stub
		return false;
	}

}
