package simulator.tree;

import javax.print.attribute.standard.PDLOverrideSupported;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import simulator.Operation;

public class CountFilesInSubtree extends Operation{
	
	public CountFilesInSubtree(GraphDatabaseService db, long id, String[] args) {
		super(id, args);
	}

	@Override
	public boolean onExecute(GraphDatabaseService db) {
		boolean sucessful = false;
		long startID = Long.parseLong(args[1]);
		
		Transaction tx = db.beginTx();
		try {
			Node srtNode = db.getNodeById(startID);
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
			res = countFiles(maxDeep-1, rs.getEndNode());
		}
		return res; 
	}
}
