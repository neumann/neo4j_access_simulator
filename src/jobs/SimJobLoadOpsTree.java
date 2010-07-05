package jobs;

import p_graph_service.PGraphDatabaseService;
import simulator.LogOperationFactory;
import simulator.tree.LogOperationFactoryTree;

public class SimJobLoadOpsTree extends SimJobLoadOps {

	public SimJobLoadOpsTree(String[] operationLogsIn,
			String operationLogsOutDir, PGraphDatabaseService pdb) {
		super(operationLogsIn, operationLogsOutDir, pdb);
	}

	public SimJobLoadOpsTree(String[] operationLogsIn,
			String operationLogsOutDir, PGraphDatabaseService pdb,
			boolean repeat) {
		super(operationLogsIn, operationLogsOutDir, pdb, repeat);
	}

	@Override
	protected LogOperationFactory getLogOperationFactory(String operationLogIn) {
		return new LogOperationFactoryTree(operationLogIn);
	}

}
