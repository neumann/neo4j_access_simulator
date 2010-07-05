package simulator.gis.operations;

import org.neo4j.graphdb.GraphDatabaseService;

public class OperationGISDummy extends OperationGIS {

	public OperationGISDummy() {
		super(new String[] { "0", OperationGISDummy.class.getName() });
	}

	@Override
	public boolean onExecute(GraphDatabaseService db) {
		return true;
	}
}
