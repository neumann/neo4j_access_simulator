package simulator.gis;

import org.neo4j.graphdb.GraphDatabaseService;

import simulator.Operation;

public class OperationShortestPath extends Operation {

	public OperationShortestPath(GraphDatabaseService db, long id, String[] args) {
		super(db, id, args);
	}

	@Override
	public boolean onExecute() {
		// TODO Auto-generated method stub
		return false;
	}

}
