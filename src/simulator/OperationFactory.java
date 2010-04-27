package simulator;

import org.neo4j.graphdb.GraphDatabaseService;

public abstract class OperationFactory {
	private GraphDatabaseService db;
	protected GraphDatabaseService getDB(){
		return db;
	}
	
	public OperationFactory(GraphDatabaseService db) {
		this.db= db;
	}
	
	public abstract boolean hasNext();
	public abstract Operation next();
	public abstract void shutdown();
	
}