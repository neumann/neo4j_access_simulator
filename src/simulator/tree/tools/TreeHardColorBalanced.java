package simulator.tree.tools;

import java.util.LinkedList;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import simulator.tree.TreeArgs;

public class TreeHardColorBalanced {
	public static void main(String[] args) {
		
		hardColor(args[0], Integer.parseInt(args[1]));
		
	}

	public static void hardColor(String file, int partitions) {
		String graphDir = file;
		
		LinkedList<Node> nodesToGo = new LinkedList<Node>();
		LinkedList<Node> rootFolder = new LinkedList<Node>();
		GraphDatabaseService graphDB = new EmbeddedGraphDatabase(graphDir);
		
		//go to folder on leaf level
		rootFolder.add(graphDB.getReferenceNode());
		while(!rootFolder.peek().hasProperty(TreeArgs.hasSub)){ 
			Node n = rootFolder.removeFirst();
			for (Relationship rs: n.getRelationships(Direction.OUTGOING)) {
				rootFolder.addLast(rs.getEndNode());
			}
		}
		while(!rootFolder.isEmpty()){
			Node n = rootFolder.remove();
			boolean ctrl = true;
			for (Relationship rs: n.getRelationships(TreeArgs.TreeRelTypes.CHILD_FOLDER,Direction.OUTGOING)) {
				rootFolder.addLast(rs.getEndNode());
				ctrl = false;
			}
			if(ctrl){
				nodesToGo.addLast(n);
			}
		}
			
		
		
		// calculate how many folder each gets
		int lenght = nodesToGo.size()/partitions;
		int left = nodesToGo.size() % partitions;
		int[] partFolder = new int[partitions];
		int sum = 0; 
		for (int i = 0; i < partFolder.length; i++) {
			partFolder[i]=lenght;
			if(left>0){
				partFolder[i]++;
				left--;
			}
			sum+=partFolder[i];
		}
		
		Transaction tx = graphDB.beginTx();
		try {
			Byte col = -1;
			for (int i = 0; i < partFolder.length; i++) {
				col++;
				for(int j = 0; j < partFolder[i]; j++){
					Node n = nodesToGo.remove();
					n.setProperty("_color", col);
					nodesToGo.add(n);
				}
			}
			tx.success();
		} finally {
			tx.finish();
		}	
		
		
		// color the rest of the tree according to its parents
		Transaction ty = graphDB.beginTx();
		int count = 0;
		int max = 1000;
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
