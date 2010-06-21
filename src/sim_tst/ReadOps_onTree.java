package sim_tst;

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

public class ReadOps_onTree {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GraphDatabaseService db;	
		Simulator sim;
		
		db = new PGraphDatabaseServiceSIM("var/fstree-nHard4_700kNodes_1300Relas",0);
		sim = new TreeOps_Sim(db,"readSearchLog", 10000, simType.SEARCH);
		sim.startSIM();
		
		db = new PGraphDatabaseServiceSIM("var/fstree-nHard4_700kNodes_1300Relas",0);
		sim = new TreeOps_Sim(db,"readCountLog", 10000, simType.COUNT);
		sim.startSIM();
		
		
		
//		db = new PGraphDatabaseServiceSIM("var/fstree-nHard2_700kNodes_1300Relas",0);
//		sim = new TreeLog_Sim(db,"readSearchLog_nHard2", "readSearchLog");
//		sim.startSIM();
//		
//		db = new PGraphDatabaseServiceSIM("var/fstree-didic2_700kNodes_1300Relas",0);
//		sim = new TreeLog_Sim(db,"readCountLog_nHard2", "readCountLog");
//		sim.startSIM();
					
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
