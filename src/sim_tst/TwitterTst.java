package sim_tst;

import java.util.Date;

import org.neo4j.graphdb.GraphDatabaseService;

import p_graph_service.PGraphDatabaseService;
import p_graph_service.core.PGraphDatabaseServiceImpl;
import p_graph_service.sim.PGraphDatabaseServiceSIM;

import simulator.Simulator;
import simulator.twitter.TwitterLog_Sim;
import simulator.twitter.ReadOnly_Sim;

public class TwitterTst {
	public static void main(String[] args) {
		PGraphDatabaseService db;
		Simulator sim;
	
		db = new PGraphDatabaseServiceSIM("var/bla", 0);
		
//		sim = new TwitterLog_Sim(db, "/home/martin/MasterThesis/Experiments/Twitter/logFileOut_didic2", "/home/martin/MasterThesis/Experiments/Twitter/logFileOut_s");
//		sim = new TwitterLog_Sim(db, "logFileOut", "logFileIn");
		
//		sim.startSIM();
		db.shutdown();
	}
}
