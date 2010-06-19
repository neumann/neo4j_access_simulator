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
import simulator.tree.hardColor.TreeHardColor;

public class ReadOps_onTree {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GraphDatabaseService db;	
		Simulator sim;
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_trffAdd0.01",0);
		sim = new TreeLog_Sim(db, "001Churn_search_trff","001Churn_search");
		sim.startSIM();
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_trffAdd0.01",0);
		sim = new TreeLog_Sim(db, "001Churn_count_trff","001Churn_count");
		sim.startSIM();

		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_trffAdd0.02",0);
		sim = new TreeLog_Sim(db, "002Churn_search_trff","002Churn_search");
		sim.startSIM();
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_trffAdd0.02",0);
		sim = new TreeLog_Sim(db, "002Churn_count_trff","002Churn_count");
		sim.startSIM();
	
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_trffAdd0.05",0);
		sim = new TreeLog_Sim(db, "005Churn_search_trff","005Churn_search");
		sim.startSIM();
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_trffAdd0.05",0);
		sim = new TreeLog_Sim(db, "005Churn_count_trff","005Churn_count");
		sim.startSIM();
		
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_trffAdd0.1",0);
		sim = new TreeLog_Sim(db, "01Churn_search_trff","01Churn_search");
		sim.startSIM();
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_trffAdd0.1",0);
		sim = new TreeLog_Sim(db, "01Churn_count_trff","01Churn_count");
		sim.startSIM();
		
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_trffAdd0.25",0);
		sim = new TreeLog_Sim(db, "025Churn_search_trff","025Churn_search");
		sim.startSIM();
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_trffAdd0.25",0);
		sim = new TreeLog_Sim(db, "025Churn_count_trff","025Churn_count");
		sim.startSIM();
		
		
		
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_minAdd0.01",0);
		sim = new TreeLog_Sim(db, "001Churn_search_min","001Churn_search");
		sim.startSIM();
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_minAdd0.01",0);
		sim = new TreeLog_Sim(db, "001Churn_count_min","001Churn_count");
		sim.startSIM();

		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_minAdd0.02",0);
		sim = new TreeLog_Sim(db, "002Churn_search_min","002Churn_search");
		sim.startSIM();
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_minAdd0.02",0);
		sim = new TreeLog_Sim(db, "002Churn_count_min","002Churn_count");
		sim.startSIM();
	
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_minAdd0.05",0);
		sim = new TreeLog_Sim(db, "005Churn_search_min","005Churn_search");
		sim.startSIM();
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_minAdd0.05",0);
		sim = new TreeLog_Sim(db, "005Churn_count_min","005Churn_count");
		sim.startSIM();
		
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_minAdd0.1",0);
		sim = new TreeLog_Sim(db, "01Churn_search_min","01Churn_search");
		sim.startSIM();
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_minAdd0.1",0);
		sim = new TreeLog_Sim(db, "01Churn_count_min","01Churn_count");
		sim.startSIM();
		
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_minAdd0.25",0);
		sim = new TreeLog_Sim(db, "025Churn_search_min","025Churn_search");
		sim.startSIM();
		
		db = new PGraphDatabaseServiceSIM("var/fstree-didic4_700kNodes_1300Relas_minAdd0.25",0);
		sim = new TreeLog_Sim(db, "025Churn_count_min","025Churn_count");
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
