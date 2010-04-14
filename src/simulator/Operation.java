package simulator;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Relationship;

public abstract class Operation {
	private GraphDatabaseService db;
	private long info[];
	private long id;
	
	protected GraphDatabaseService getDB(){
		return db;
	}
	
	public long getId(){
		return id;
	}
	
	protected void logMovement(Relationship rs){
		info[0]++;
		if(rs.hasProperty("_IsHalf")){
			info[1]++;
		}
	}
	
	public Operation(GraphDatabaseService db, long id) {
		this.db = db;
		this.id = id;
		this.info = new long[2];
	}
	
	public abstract boolean execute();
	
	public long[] getHopInfo(){
		return info.clone();
	}
	
}
