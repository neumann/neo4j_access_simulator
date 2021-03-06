package jobs;

import p_graph_service.PGraphDatabaseService;
import simulator.LogOperationFactory;
import simulator.gis.LogOperationFactoryGIS;

public class SimJobLoadOpsGIS extends SimJobLoadOps {

	public SimJobLoadOpsGIS(String[] operationLogsIn,
			String operationLogsOutDir, PGraphDatabaseService pdb) {
		super(operationLogsIn, operationLogsOutDir, pdb);
	}

	public SimJobLoadOpsGIS(String[] operationLogsIn,
			String operationLogsOutDir, PGraphDatabaseService pdb,
			boolean repeat) {
		super(operationLogsIn, operationLogsOutDir, pdb, repeat);
	}

	@Override
	protected LogOperationFactory getLogOperationFactory(String operationLogIn) {
		return new LogOperationFactoryGIS(operationLogIn);
	}

}
