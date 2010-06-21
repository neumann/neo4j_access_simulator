package sim_tst;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;

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
		args = new String[2];
		args[0] = "var/fstree-nHard4_700kNodes_1300Relas";
		args[1] = "var/fstree-nHard4_700kNodes_1300Relas/edgeCutInfo";
		
		HashMap<String, Long> edgeInfo = new HashMap<String, Long>();
		GraphDatabaseService db = new PGraphDatabaseServiceSIM(args[0], 0);
		
		for(Node n : db.getAllNodes()){
			Byte colS = (Byte) n.getProperty("_color");
			for(Relationship rs: n.getRelationships(Direction.OUTGOING)){
				Byte colE = (Byte) rs.getEndNode().getProperty("_color");
				String k = colS + " to " + colE;
				if(edgeInfo.containsKey(k)){
					edgeInfo.put(k, edgeInfo.get(k)+1);
				}else{
					edgeInfo.put(k, 1l);
				}
			}
		}
		PrintStream ps;
		try {
			ps = new PrintStream(args[1]);
			ps.println(edgeInfo);
			ps.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		db.shutdown();
	}

}
