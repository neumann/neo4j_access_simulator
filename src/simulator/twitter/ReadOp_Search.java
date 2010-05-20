package simulator.twitter;

import java.util.Iterator;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import simulator.Operation;

public class ReadOp_Search extends Operation {

	public ReadOp_Search(String[] args) {
		super(args);
	}

	@Override
	public boolean onExecute(GraphDatabaseService db) {
		Transaction tx = db.beginTx();
		try {
			Node snode = db.getNodeById(Long.parseLong(args[2]));
			Iterator<Relationship> rsIter = snode.getRelationships(Direction.OUTGOING).iterator();
			while (rsIter.hasNext()) {
				Node endNode = rsIter.next().getEndNode();
				for(Relationship rs: endNode.getRelationships(Direction.OUTGOING)){
					Node n = rs.getEndNode();
					n.getId();
				}
			}
			tx.success();
		} catch (Exception e) {
			return false;
		}	
		finally {
			tx.finish();
		}
		return true;
	}

}
