package sim_tst;

import p_graph_service.PGraphDatabaseService;
import p_graph_service.core.PGraphDatabaseServiceImpl;
import simulator.Simulator;
import simulator.gis.SimulatorGIS;

public class GISTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		PGraphDatabaseService pDB = new PGraphDatabaseServiceImpl(
				"var/pDB-GIS", 0);
		Simulator sim = new SimulatorGIS(pDB, "");
		sim.startSIM();
		System.out.println("done");
	}

}
