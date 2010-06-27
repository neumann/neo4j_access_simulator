package jobs;

import java.util.ArrayList;
import java.util.Arrays;

import p_graph_service.PGraphDatabaseService;
import simulator.OperationFactory;
import simulator.Simulator;
import simulator.gis.OperationFactoryGIS;
import simulator.gis.OperationFactoryGISConfig;
import simulator.gis.SimulatorGIS;

public class SimJobGenerateOpsGIS implements SimJob {

	private ArrayList<OperationFactoryGISConfig> operationFactoryConfigs = null;
	private PGraphDatabaseService pdb = null;

	public SimJobGenerateOpsGIS(
			OperationFactoryGISConfig[] operationFactoryConfigs,
			PGraphDatabaseService pdb) {
		this.operationFactoryConfigs = new ArrayList<OperationFactoryGISConfig>(
				Arrays.asList(operationFactoryConfigs));
		this.pdb = pdb;
	}

	@Override
	public void start() throws Exception {
		if (operationFactoryConfigs.isEmpty() == true)
			throw new Exception("No simulation configuration specified");

		OperationFactoryGISConfig config = operationFactoryConfigs.remove(0);

		OperationFactory operationFactory = new OperationFactoryGIS(pdb, config);

		Simulator sim = new SimulatorGIS(pdb, config.getOperationLogOut(),
				operationFactory);
		sim.startSIM();
		sim.shutdown();

		try {
			sim.join();
		} catch (InterruptedException e1) {
		}
	}

}
