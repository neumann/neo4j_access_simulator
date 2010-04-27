package simulator.gis;

import org.neo4j.graphdb.GraphDatabaseService;

import simulator.Operation;

public class OperationShortestPath extends Operation {

	public OperationShortestPath(long id, String[] args) {
		super(id, args);
	}

	@Override
	public boolean onExecute(GraphDatabaseService db) {
		// TODO Auto-generated method stub
		return false;
	}

}
