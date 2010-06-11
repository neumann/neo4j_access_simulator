package simulator.tree;

import org.neo4j.graphdb.GraphDatabaseService;
import simulator.Operation;

public class Empty_Op extends Operation {

	public Empty_Op(String[] args) {
		super(args);
	}

	@Override
	public boolean onExecute(GraphDatabaseService db) {
		return true;
	}

}
