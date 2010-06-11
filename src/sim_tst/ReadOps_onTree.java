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
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_randAdd0.01",0);
		sim = new TreeOps_Sim(db, "001Churn_search", 10000, simType.SEARCH);
		sim.startSIM();
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_randAdd0.01",0);
		sim = new TreeOps_Sim(db, "001Churn_count", 10000, simType.COUNT);
		sim.startSIM();

		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_randAdd0.02",0);
		sim = new TreeOps_Sim(db, "001Churn_search", 10000, simType.SEARCH);
		sim.startSIM();
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_randAdd0.02",0);
		sim = new TreeOps_Sim(db, "001Churn_count", 10000, simType.COUNT);
		sim.startSIM();
	
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_randAdd0.05",0);
		sim = new TreeOps_Sim(db, "001Churn_search", 10000, simType.SEARCH);
		sim.startSIM();
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_randAdd0.05",0);
		sim = new TreeOps_Sim(db, "001Churn_count", 10000, simType.COUNT);
		sim.startSIM();
		
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_randAdd0.1",0);
		sim = new TreeOps_Sim(db, "001Churn_search", 10000, simType.SEARCH);
		sim.startSIM();
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_randAdd0.1",0);
		sim = new TreeOps_Sim(db, "001Churn_count", 10000, simType.COUNT);
		sim.startSIM();
		
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_randAdd0.25",0);
		sim = new TreeOps_Sim(db, "001Churn_search", 10000, simType.SEARCH);
		sim.startSIM();
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_randAdd0.25",0);
		sim = new TreeOps_Sim(db, "001Churn_count", 10000, simType.COUNT);
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
