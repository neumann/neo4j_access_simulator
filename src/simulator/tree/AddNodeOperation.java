package simulator.tree;

import java.util.Iterator;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

import p_graph_service.PGraphDatabaseService;

import simulator.Operation;
import simulator.Rnd;

public class AddNodeOperation extends Operation {
	private PGraphDatabaseService pDB = null;
	
	public AddNodeOperation(GraphDatabaseService db, long id) {
		super(db,id, null);
		try {
			this.pDB = (PGraphDatabaseService)db;
		} catch (Exception e) {
			throw new Error("This class only works for PGraphDatabaseService implementations of the GraphDatabaseService interface");
		}
	}

	@Override
	public boolean onExecute() {
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
			
			// create a node connceted to an other node
			Node srtNode = iter.next();
			Node newNode = pDB.createNode();
			srtNode.createRelationshipTo(newNode, new RelationshipType() {
				@Override
				public String name() {
					return "New_Relation";
				}
			});
			
			tx.success();
			success = true;
		} finally {
			tx.finish();
		}
		
		
		return success;
	}
}
