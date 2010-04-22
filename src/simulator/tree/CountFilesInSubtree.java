package simulator.tree;

import java.util.Iterator;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import p_graph_service.PGraphDatabaseService;
import simulator.Operation;
import simulator.Rnd;

public class CountFilesInSubtree extends Operation{
	private PGraphDatabaseService pDB = null;
	
	public CountFilesInSubtree(GraphDatabaseService db, long id) {
		super(db, id, null);
		try {
			this.pDB = (PGraphDatabaseService)getDB();
		} catch (Exception e) {
			throw new Error("This class only works for PGraphDatabaseService implementations of the GraphDatabaseService interface");
		}
	}

	@Override
	public boolean execute() {
		boolean sucessful = false;
		
		Transaction tx = getDB().beginTx();
		try {
			
			// choose StartNode
			long numNodes = pDB.getNumNodes();
			long nthNode = Rnd.nextLong(0, numNodes, Rnd.RndType.unif);
			Iterator<Node> iter = pDB.getAllNodes().iterator();
			while (nthNode > 0) {
				nthNode--;
				iter.next();
			}
			Node srtNode = iter.next();
			
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
