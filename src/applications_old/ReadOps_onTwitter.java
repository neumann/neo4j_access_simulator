package applications_old;

import java.io.File;
import java.util.Arrays;

import graph_gen_utils.NeoFromFile;
import graph_gen_utils.partitioner.Partitioner;
import graph_gen_utils.partitioner.PartitionerAsRandom;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import p_graph_service.sim.PGraphDatabaseServiceSIM;
import simulator.LogOperationFactory;
import simulator.SimulatorBasic;
import simulator.Simulator;
import simulator.twitter.LogOperationFactoryTwitter;

public class ReadOps_onTwitter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GraphDatabaseService db;
		Simulator sim;

		String var = args[0];
		File varF = new File(var);
		String inputLog = args[1];
		String outPutFolder = args[2];

		String[] inputDBs = varF.list();
		System.out.println(Arrays.toString(inputDBs));
		for (int i = 0; i < inputDBs.length; i++) {
			File tst = new File(var + "/" + inputDBs[i] + "/");
			if (!tst.isDirectory())
				continue;
			System.out.println("applying " + inputLog + " to " + inputDBs[i]);
			db = new PGraphDatabaseServiceSIM(var + "/" + inputDBs[i], 0);
			LogOperationFactory fac = new LogOperationFactoryTwitter(inputLog);
			sim = new SimulatorBasic(db, outPutFolder + "/" + inputDBs[i] + "_"
					+ inputLog, fac);
			sim.startSIM();
		}

	}

	public static void convertDB() {
		GraphDatabaseService db = new EmbeddedGraphDatabase(
				"target/_700kNodes_1300Relas");
		Partitioner part = new PartitionerAsRandom((byte) 4);
		try {
			NeoFromFile.applyPtnToNeo(db, part);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.shutdown();
		System.out.println("done");

		PGraphDatabaseServiceSIM sim = new PGraphDatabaseServiceSIM(
				"target/_700kNodes_1300Relas", 0);
		sim.shutdown();

		for (long id : sim.getInstancesIDs()) {
			System.out.println(sim.getInstanceInfoFor(id));
		}

		// PGraphDatabaseService pDB = NeoFromFile.writePNeoFromNeo("var/pDB",
		// db);
		// pDB.shutdown();
	}

}
