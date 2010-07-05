package jobs;

import p_graph_service.PGraphDatabaseService;
import simulator.LogOperationFactory;
import simulator.twitter.LogOperationFactoryTwitter;

public class SimJobLoadOpsTwitter extends SimJobLoadOps {

	public SimJobLoadOpsTwitter(String[] operationLogsIn,
			String operationLogsOutDir, PGraphDatabaseService pdb) {
		super(operationLogsIn, operationLogsOutDir, pdb);
	}

	public SimJobLoadOpsTwitter(String[] operationLogsIn,
			String operationLogsOutDir, PGraphDatabaseService pdb,
			boolean repeat) {
		super(operationLogsIn, operationLogsOutDir, pdb, repeat);
	}

	@Override
	protected LogOperationFactory getLogOperationFactory(String operationLogIn) {
		return new LogOperationFactoryTwitter(operationLogIn);
	}

}
