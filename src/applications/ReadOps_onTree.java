package applications;

import graph_gen_utils.NeoFromFile;
import graph_gen_utils.partitioner.Partitioner;
import graph_gen_utils.partitioner.PartitionerAsRandom;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import p_graph_service.PGraphDatabaseService;
import p_graph_service.core.DBInstanceContainer;
import p_graph_service.sim.PGraphDatabaseServiceSIM;
import simulator.Simulator;
import simulator.tree.TreeLog_Sim;
import simulator.tree.TreeOps_Sim;
import simulator.tree.TreeLog_Factory;
import simulator.tree.TreeOps_Sim.simType;
import simulator.tree.tools.TreeHardColor;
import simulator.tree.tools.TreeHardColorBalanced;

public class ReadOps_onTree {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GraphDatabaseService db;	
		Simulator sim;
		
//		TreeHardColorBalanced.hardColor("var/fstree-nHard4_700kNodes_1300Relas", 4);
		TreeHardColorBalanced.hardColor("var/fstree-nHard2_700kNodes_1300Relas", 2);
//		EdgeCutCrawler.cal("var/fstree-nHard4_700kNodes_1300Relas", "var/fstree-nHard4_700kNodes_1300Relas/edgeCutInfo", 4);
		EdgeCutCrawler.cal("var/fstree-nHard2_700kNodes_1300Relas", "var/fstree-nHard2_700kNodes_1300Relas/edgeCutInfo", 2);
	
		if(true)return;
		
		db = new PGraphDatabaseServiceSIM("var/fstree-nHard4_700kNodes_1300Relas",0);
		sim = new TreeOps_Sim(db,"readCountLog", 10000, simType.COUNT);
		sim.startSIM();
		
		//-------------------------------
		
		
		db = new PGraphDatabaseServiceSIM("var/fstree-nHard2_700kNodes_1300Relas",0);
		sim = new TreeLog_Sim(db,"readCountLog_nHard2", "readCountLog");
		sim.startSIM();
		
		db = new PGraphDatabaseServiceSIM("var/fstree-nHard4_700kNodes_1300Relas",0);
		sim = new TreeLog_Sim(db,"readCountLog_nHard4", "readCountLog");
		sim.startSIM();
		
		db = new PGraphDatabaseServiceSIM("var/fstree-rand4_700kNodes_1300Relas",0);
		sim = new TreeLog_Sim(db,"readCountLog_rand4", "readCountLog");
		sim.startSIM();
		
		
		
		db = new PGraphDatabaseServiceSIM("var/fstree-rand2_700kNodes_1300Relas",0);
		sim = new TreeLog_Sim(db,"readCountLog_rand2", "readCountLog");
		sim.startSIM();
		
		
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas",0);
		sim = new TreeLog_Sim(db,"readCountLog_didic4", "readCountLog");
		sim.startSIM();
		
		
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic2_700kNodes_1300Relas",0);
		sim = new TreeLog_Sim(db,"readCountLog_didic2", "readCountLog");
		sim.startSIM();
		
		
		
		
		db = new PGraphDatabaseServiceSIM("var/fstree-Hard4_700kNodes_1300Relas",0);
		sim = new TreeLog_Sim(db,"readCountLog_Hard4", "readCountLog");
		sim.startSIM();
		
		
		db = new PGraphDatabaseServiceSIM("var/fstree-Hard2_700kNodes_1300Relas",0);
		sim = new TreeLog_Sim(db,"readCountLog_Hard2", "readCountLog");
		sim.startSIM();
		
		
		
		// run the other experiments
//		WriteOps_onTree.runWriteOps();
//		 convertDB();

	}

	public static void convertDB() {
		GraphDatabaseService db = new EmbeddedGraphDatabase("target/_700kNodes_1300Relas");
		Partitioner part = new PartitionerAsRandom((byte) 4);
		try {
			NeoFromFile.applyPtnToNeo(db, part);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 db.shutdown();
		 System.out.println("done");

		 PGraphDatabaseServiceSIM sim = new PGraphDatabaseServiceSIM("target/_700kNodes_1300Relas", 0);
		 sim.shutdown();
		
		 for(long id : sim.getInstancesIDs()){
			 System.out.println(sim.getInstanceInfoFor(id));
		 }
		
//		PGraphDatabaseService pDB = NeoFromFile.writePNeoFromNeo("var/pDB", db);
//		pDB.shutdown();
	}
	
	

}
