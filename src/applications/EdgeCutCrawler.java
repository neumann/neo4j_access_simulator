package applications;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import p_graph_service.sim.PGraphDatabaseServiceSIM;

public class EdgeCutCrawler {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		cal("var/fstree-didic2_700kNodes_1300Relas", "cut_didic2", 2);
		cal("var/fstree-didic4_700kNodes_1300Relas", "cut_didic4", 4);
		
		cal("var/fstree-nHard2_700kNodes_1300Relas", "cut_nHard2", 2);
		cal("var/fstree-nHard4_700kNodes_1300Relas", "cut_nHard4", 4);
		
		cal("var/fstree-rand2_700kNodes_1300Relas", "cut_rand2", 2);
		cal("var/fstree-rand4_700kNodes_1300Relas", "cut_rand4", 4);
		
		cal("var/fstree-Hard2_700kNodes_1300Relas", "cut_Hard2", 2);
		cal("var/fstree-Hard4_700kNodes_1300Relas", "cut_Hard4", 4);
		
	}

	
	private static void cal(String dbFolder, String out, int part){
		long[][] edgeInfo = new long[part][part];
		
		GraphDatabaseService db = new PGraphDatabaseServiceSIM(dbFolder, 0);
		
		for(Node n : db.getAllNodes()){
			Byte colS = (Byte) n.getProperty("_color");
			for(Relationship rs: n.getRelationships(Direction.OUTGOING)){
				Byte colE = (Byte) rs.getEndNode().getProperty("_color");
				edgeInfo[colS][colE]++;
			}
		}
		PrintStream ps;
		try {
			ps = new PrintStream(out);
			for(int i = 0; i< part; i++){
				for(int j = 0; j < part; j++){
					System.out.println(i + " "+j);
					ps.print(edgeInfo[i][j]+" ");
				}
				ps.println();
			}
			ps.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		db.shutdown();
	}
}
