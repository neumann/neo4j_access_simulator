package simulator;

import org.neo4j.graphdb.GraphDatabaseService;

public class Empty_Op extends Operation {

	public Empty_Op(String[] args) {
		super(args);
	}

	@Override
	public boolean onExecute(GraphDatabaseService db) {
		return true;
	}

}
