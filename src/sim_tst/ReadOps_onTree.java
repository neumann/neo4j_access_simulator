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
import simulator.tree.TreeHardColor;
import simulator.tree.TreeOps_Sim;
import simulator.tree.TreeLog_Factory;
import simulator.tree.TreeOps_Sim.simType;

public class ReadOps_onTree {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GraphDatabaseService db;	
		Simulator sim;
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic2_700kNodes_1300Relas",0);
		sim = new TreeOps_Sim(db, "didic2_search", 10000, simType.SEARCH);
		sim.startSIM();
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic2_700kNodes_1300Relas",0);
		sim = new TreeOps_Sim(db, "didic2_count", 10000, simType.COUNT);
		sim.startSIM();

		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas",0);
		sim = new TreeLog_Sim(db, "didic4_search", "didic2_search");
		sim.startSIM();
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas",0);
		sim = new TreeLog_Sim(db, "didic4_count", "didic2_count");
		sim.startSIM();
		
		
		
		db = new PGraphDatabaseServiceSIM("var/fstree-hard2_700kNodes_1300Relas",0);
		sim = new TreeLog_Sim(db, "hard2_search", "didic2_search");
		sim.startSIM();
		
		db = new PGraphDatabaseServiceSIM("var/fstree-hard2_700kNodes_1300Relas",0);
		sim = new TreeLog_Sim(db, "hard2_count", "didic2_count");
		sim.startSIM();
		
		db = new PGraphDatabaseServiceSIM("var/fstree-hard4_700kNodes_1300Relas",0);
		sim = new TreeLog_Sim(db, "hard4_search", "didic2_search");
		sim.startSIM();
		
		db = new PGraphDatabaseServiceSIM("var/fstree-hard4_700kNodes_1300Relas",0);
		sim = new TreeLog_Sim(db, "hard4_count", "didic2_count");
		sim.startSIM();
		
		
		
		db = new PGraphDatabaseServiceSIM("var/fstree-rand2_700kNodes_1300Relas",0);
		sim = new TreeLog_Sim(db, "rand2_search", "didic2_search");
		sim.startSIM();
		
		db = new PGraphDatabaseServiceSIM("var/fstree-rand2_700kNodes_1300Relas",0);
		sim = new TreeLog_Sim(db, "rand2_count", "didic2_count");
		sim.startSIM();
		
		db = new PGraphDatabaseServiceSIM("var/fstree-rand4_700kNodes_1300Relas",0);
		sim = new TreeLog_Sim(db, "rand4_search", "didic2_search");
		sim.startSIM();
		
		db = new PGraphDatabaseServiceSIM("var/fstree-rand4_700kNodes_1300Relas",0);
		sim = new TreeLog_Sim(db, "rand4_count", "didic2_count");
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
