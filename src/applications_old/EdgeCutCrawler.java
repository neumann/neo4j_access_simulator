package applications_old;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import p_graph_service.PGraphDatabaseService;
import p_graph_service.sim.PGraphDatabaseServiceSIM;
public class EdgeCutCrawler {

	public static void main(String[] args) {
		
		File var = new File("/home/martin/MasterThesis/results");
		String[] inputDBs = var.list();
		
		for (int i = 0; i < inputDBs.length; i++) {
			File tst = new File(var.getAbsolutePath()+"/"+inputDBs[i]+"/");
			if(!tst.isDirectory())continue;
			tst = new File(var.getAbsolutePath()+"/"+inputDBs[i], var.getAbsolutePath()+"/"+inputDBs[i]+"/edgeCutInfo");
			if(tst.exists())continue;
			System.out.println(var.getAbsolutePath()+"/"+inputDBs[i]+"/");
			
			cal(var.getAbsolutePath()+"/"+inputDBs[i], var.getAbsolutePath()+"/"+inputDBs[i]+"/edgeCutInfo", 4);
		}
		
	}

	public static void calOnDB(PGraphDatabaseService dbServ, String out, int part){
		long[][] edgeInfo = new long[part][part];
		// not really necessary
		for (int i = 0; i < edgeInfo.length; i++) {
			for (int j = 0; j < edgeInfo.length; j++) {
				edgeInfo[i][j]=0;
			}
		}
		for(Node n : dbServ.getAllNodes()){
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
