package simulator.tree.operations;

import java.util.HashSet;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

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
		try{
			PGraphDatabaseService pdb = (PGraphDatabaseServiceSIM) db;
			HashSet<Node> set = new HashSet<Node>();
			long nID = Long.parseLong(args[2]);
			set.add(pdb.getNodeById(nID));	
			pdb.moveNodes(set, pdb.getPlacementPolicy().getPosition());
			return true;
		}catch (Exception e) {
			System.out.println("something went wrong");
		}
		
		
		
		return false;
	}

}
