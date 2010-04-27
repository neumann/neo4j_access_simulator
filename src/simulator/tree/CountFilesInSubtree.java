package simulator.tree;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import simulator.Operation;

public class CountFilesInSubtree extends Operation{
	
	public CountFilesInSubtree(GraphDatabaseService db, long id, String[] args) {
		super(db, id, args);
	}

	@Override
	public boolean execute() {
		boolean sucessful = false;
		long startID = Long.parseLong(args[1]);
		
		Transaction tx = getDB().beginTx();
		try {
			Node srtNode = getDB().getNodeById(startID);
			long subfiles = countFiles(5, srtNode);
			
			System.out.println("subfiles: "+subfiles);
	
			tx.success();
			sucessful = true;
		} finally {
			tx.finish();
		}
		return sucessful;
	}
	
	private long countFiles(int maxDeep, Node curNode){
		long res = 0;
		for(Relationship rs : curNode.getRelationships(Direction.OUTGOING)){
			
			logMovement(rs);
			
			
			res = countFiles(maxDeep-1, rs.getEndNode());
		}
		return res; 
	}

}
