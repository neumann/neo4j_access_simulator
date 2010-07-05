package jobs;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import p_graph_service.PGraphDatabaseService;
import simulator.LogOperationFactory;
import simulator.OperationFactory;
import simulator.Simulator;
import simulator.SimulatorBasic;

public abstract class SimJobLoadOps implements SimJob {

	private ArrayList<String> operationLogsIn = null;
	private File operationLogOutDir = null;
	private PGraphDatabaseService pdb = null;
	private int operationLogIndex = 0;
	private boolean repeat = false;

	public SimJobLoadOps(String[] operationLogsIn, String operationLogsOutDir,
			PGraphDatabaseService pdb) {
		this.operationLogsIn = new ArrayList<String>(Arrays
				.asList(operationLogsIn));
		this.operationLogOutDir = new File(operationLogsOutDir);
		this.pdb = pdb;
	}

	public SimJobLoadOps(String[] operationLogsIn, String operationLogsOutDir,
			PGraphDatabaseService pdb, boolean repeat) {
		this.repeat = repeat;
		this.operationLogsIn = new ArrayList<String>(Arrays
				.asList(operationLogsIn));
		this.operationLogOutDir = new File(operationLogsOutDir);
		this.pdb = pdb;
	}

	@Override
	public void start() throws Exception {
		if ((operationLogsIn.isEmpty() == true)
				|| (operationLogIndex >= operationLogsIn.size()))
			throw new Exception("No input Operation log has been specified");

		String operationLogIn = operationLogsIn.get(operationLogIndex);
		String operationLogOut = logInToLogOut(operationLogIn);

		OperationFactory operationFactory = getLogOperationFactory(operationLogIn);

		Simulator sim = new SimulatorBasic(pdb, operationLogOut,
				operationFactory);
		sim.startSIM();
		sim.shutdown();

		operationLogIndex++;
		if (repeat == false)
			return;

		if (operationLogIndex >= operationLogsIn.size())
			operationLogIndex = 0;
	}

	protected abstract LogOperationFactory getLogOperationFactory(
			String operationLogIn);

	private String logInToLogOut(String operationLogIn) {
		int slashIndex = (operationLogIn.lastIndexOf("/") == -1) ? -1
				: operationLogIn.lastIndexOf("/");

		String logInFile = operationLogIn.substring(slashIndex + 1,
				operationLogIn.length());

		int dotIndex = (logInFile.lastIndexOf(".") == -1) ? logInFile.length()
				: logInFile.lastIndexOf(".");

		String logInName = logInFile.substring(0, dotIndex);
		String logInExt = logInFile.substring(dotIndex, logInFile.length());

		return String.format("%s/%s_OUT%s", operationLogOutDir
				.getAbsolutePath(), logInName, logInExt);
	}

}
