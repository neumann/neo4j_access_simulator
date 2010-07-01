package simulator.twitter.operations;

import java.util.HashSet;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import p_graph_service.PGraphDatabaseService;
import p_graph_service.sim.PGraphDatabaseServiceSIM;

import simulator.Operation;

public class Shuffle_WriteOp extends Operation {

	public Shuffle_WriteOp(String[] args) {
		super(args);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onExecute(GraphDatabaseService db) {
		boolean res = false;
		
		Transaction tx = db.beginTx();
		try{
			PGraphDatabaseService pdb = (PGraphDatabaseServiceSIM) db;
			HashSet<Node> set = new HashSet<Node>();
			long nID = Long.parseLong(args[2]);
			set.add(pdb.getNodeById(nID));	
			pdb.moveNodes(set, pdb.getPlacementPolicy().getPosition());
			
			tx.success();
			res = true;
			
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("something went wrong");
		}finally{
			tx.finish();
		}
		
		return res;
	}

}
