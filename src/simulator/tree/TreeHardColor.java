package simulator.tree;

import java.util.LinkedList;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class TreeHardColor {
	public static void main(String[] args) {
		String graphDir = "var/250000db";
		int mod = 2;
		LinkedList<Node> nodesToGo = new LinkedList<Node>();

		GraphDatabaseService graphDB = new EmbeddedGraphDatabase(graphDir);

		int transcount = 0;
		
		// initiate color on the 1rst level
		Transaction tx = graphDB.beginTx();
		try {
			int col = 0;
			graphDB.getReferenceNode().setProperty("_color", (byte)col);
			Node organisation = graphDB.getReferenceNode().getSingleRelationship(TreeArgs.TreeRelTypes.REF_ORGANISATIONS, Direction.OUTGOING).getEndNode();
			
			for (Relationship rs : organisation.getRelationships(
					Direction.OUTGOING)) {
				col = col%mod;
				Node n = rs.getEndNode();
				if(n.hasProperty("_color")){
					throw new Error("graph allready has colors");
				}
				n.setProperty("_color", (byte)col);
				nodesToGo.add(n);
				col++;	
			}
			transcount++;
			
			if(transcount%100 == 0){
				tx.success();
				tx.finish();
				tx = graphDB.beginTx();
			}
			
			tx.success();
		} finally {
			tx.finish();
		}

		Transaction ty = graphDB.beginTx();
		try {
		while (!nodesToGo.isEmpty()) {
			

				Node n = nodesToGo.remove();
				byte col = (Byte) n.getProperty("_color");
				for (Relationship rs : n.getRelationships()) {
					Node otherN = rs.getOtherNode(n);
					if (!otherN.hasProperty("_color")) {
						otherN.setProperty("_color", col);
						if(!nodesToGo.contains(otherN))nodesToGo.add(otherN);
					}
				}
			
//			System.out.println(nodesToGo.size());
		}
			ty.success();
		} finally {
			ty.finish();
		}
		System.out.println("done");
		graphDB.shutdown();
	}
}
