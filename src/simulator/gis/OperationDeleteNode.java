package simulator.gis;

import org.neo4j.graphdb.GraphDatabaseService;

import simulator.Operation;

public class OperationDeleteNode extends Operation {

	public OperationDeleteNode(GraphDatabaseService db, long id, String[] args) {
		super(db, id, args);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onExecute() {
		// TODO Auto-generated method stub
		return false;
	}

}
