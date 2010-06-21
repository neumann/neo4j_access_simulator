package simulator.tree.hardColor;

import java.util.LinkedList;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import p_graph_service.core.DBInstanceContainer;
import p_graph_service.sim.PGraphDatabaseServiceSIM;

import simulator.tree.TreeArgs;
import simulator.tree.TreeArgs.TreeRelTypes;

public class TreeHardColorBalanced {
	public static void main(String[] args) {
		
//		GraphDatabaseService db = new PGraphDatabaseServiceSIM("var/fstree-nHard_700kNodes_1300Relas", 0);
		
		hardColor("var/fstree-nHard_700kNodes_1300Relas", 4);
	}

	public static void hardColor(String file, int partitions) {
		String graphDir = file;
		
		LinkedList<Node> nodesToGo = new LinkedList<Node>();
		GraphDatabaseService graphDB = new EmbeddedGraphDatabase(graphDir);
		
		//go to folder level
		Node curN = graphDB.getReferenceNode();
		while(!curN.hasProperty(TreeArgs.hasSub)){ 
			for (Relationship rs: curN.getRelationships(Direction.OUTGOING)) {
				nodesToGo.addLast(rs.getEndNode());
			}
			curN = nodesToGo.poll();
		}
				
		Transaction tx = graphDB.beginTx();
		try {
			int length = Math.round(nodesToGo.size()/(float)partitions);
			Byte col = 0;
			int count =0;
			for (Node n2g : nodesToGo) {		
				n2g.setProperty("_color", col);
				count ++;
				if(count >= length){
					System.out.println(count + " with color "+ col);
					count  = 0;
					col++;
				}
			}
			tx.success();
		} finally {
			tx.finish();
		}	
		
		graphDB.shutdown();
		System.exit(0);
		
		
		// color the rest of the tree according to its parents
		Transaction ty = graphDB.beginTx();
		int count = 0;
		int max = 100;
		int sumCount = 0;
		try {
			while (!nodesToGo.isEmpty()) {

				Node n = nodesToGo.remove();
				byte col = (Byte) n.getProperty("_color");
				for (Relationship rs : n.getRelationships()) {
					Node otherN = rs.getOtherNode(n);
					if (!otherN.hasProperty("_color")) {
						otherN.setProperty("_color", col);
						if (!nodesToGo.contains(otherN))
							nodesToGo.add(otherN);
						sumCount++;
						System.out.println(sumCount);
					}
				}

				count++;
				if (count >= max) {
					ty.success();
					ty.finish();
					ty = graphDB.beginTx();
					count = 0;
				}

				// System.out.println(nodesToGo.size());
			}
			ty.success();
		} finally {
			ty.finish();
		}
		
		
		int noncol = 0;
		for(Node allN : graphDB.getAllNodes()){
			if(!allN.hasProperty("_color"))noncol++;
		}
		
		
		
		System.out.println(noncol);
		graphDB.shutdown();
	}
}
