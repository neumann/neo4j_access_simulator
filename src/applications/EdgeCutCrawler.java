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
		cal("var/fstree-Didic4_700kNodes_1300Relas", "cut_didic4", 4);
		
		cal("var/fstree-Hard2_700kNodes_1300Relas", "cut_hard2", 2);
		cal("var/fstree-Hard4_700kNodes_1300Relas", "cut_hard4", 4);
		
		cal("var/fstree-nHard2_700kNodes_1300Relas", "cut_nhard2", 2);
		cal("var/fstree-nHard4_700kNodes_1300Relas", "cut_nhard4", 4);
		
		cal("var/fstree-rand2_700kNodes_1300Relas", "cut_rand2", 2);
		cal("var/fstree-rand4_700kNodes_1300Relas", "cut_rand4", 4);
		
	}

	
	public static void cal(String dbFolder, String out, int part){
		long[][] edgeInfo = new long[part][part];
		// not really necessary
		for (int i = 0; i < edgeInfo.length; i++) {
			for (int j = 0; j < edgeInfo.length; j++) {
				edgeInfo[i][j]=0;
			}
		}
		
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
			ps.print("EndP ");
			for (int i = 0; i < edgeInfo.length; i++) {
				ps.print(i+" ");
			}
			ps.println();
			for(int i = 0; i< part; i++){
				ps.print("StartP"+i + " ");
				for(int j = 0; j < part; j++){
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
