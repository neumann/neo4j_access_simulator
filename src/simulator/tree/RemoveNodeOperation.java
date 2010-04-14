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

public class RemoveNodeOperation extends Operation {
	private PGraphDatabaseService pDB = null;
	
	public RemoveNodeOperation(GraphDatabaseService db, long id) {
		super(db, id);
		try {
			this.pDB = (PGraphDatabaseService)getDB();
		} catch (Exception e) {
			throw new Error("This class only works for PGraphDatabaseService implementations of the GraphDatabaseService interface");
		}
	}

	@Override
	public boolean execute() {
		boolean success = false;
		
		Transaction tx = pDB.beginTx();
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
	
			// delete choose node and repair structure
			if(srtNode.hasRelationship(Direction.INCOMING)){
				Node replace = srtNode.getRelationships(Direction.INCOMING).iterator().next().getStartNode();
				
				for(Relationship rs : srtNode.getRelationships(Direction.OUTGOING)){
					Relationship rsRepair = replace.createRelationshipTo(rs.getEndNode(), rs.getType());
					for(String key : rs.getPropertyKeys()){
						rsRepair.setProperty(key, rs.getProperty(key));
					}
					rs.delete();
				}
				srtNode.delete();
			}
			
			tx.success();
			success = true;
		} finally {
			tx.finish();
		}
		
		
		return success;
	}

}
