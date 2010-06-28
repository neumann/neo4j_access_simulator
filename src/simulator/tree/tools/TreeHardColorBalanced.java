package simulator.tree.tools;

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
		
		hardColor("var/fstree-nHard2_700kNodes_1300Relas", 2);
	}

	public static void hardColor(String file, int partitions) {
		String graphDir = file;
		
		LinkedList<Node> nodesToGo = new LinkedList<Node>();
		LinkedList<Node> rootFolder = new LinkedList<Node>();
		GraphDatabaseService graphDB = new EmbeddedGraphDatabase(graphDir);
		
		//go to folder level
		rootFolder.add(graphDB.getReferenceNode());
		while(!rootFolder.peek().hasProperty(TreeArgs.hasSub)){ 
			Node n = rootFolder.removeFirst();
			for (Relationship rs: n.getRelationships(Direction.OUTGOING)) {
				rootFolder.addLast(rs.getEndNode());
			}
		}
		for(Node n : rootFolder){
			for (Relationship rs: n.getRelationships(TreeArgs.TreeRelTypes.CHILD_FOLDER,Direction.OUTGOING)) {
				nodesToGo.addLast(rs.getEndNode());
			}
		}
		
				
		Transaction tx = graphDB.beginTx();
		int lenght = Math.round(nodesToGo.size()/(float)partitions);
		try {
			Byte col = -1;
			int j =0;
			for(int i = 0 ; i < partitions; i++){
				col++;
				j = i*lenght;
				while( j < (i+1)*lenght && j< nodesToGo.size()){
					nodesToGo.get(j).setProperty("_color", col);
					j++;
				}
			}
			while (j<nodesToGo.size()) {
				nodesToGo.get(j).setProperty("_color", col);
				j++;
			}
			tx.success();
		} finally {
			tx.finish();
		}	
		
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
