package jobs;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import p_graph_service.PGraphDatabaseService;
import simulator.OperationFactory;
import simulator.Simulator;
import simulator.gis.LogOperationFactoryGIS;
import simulator.gis.SimulatorGIS;

public class SimJobLoadOpsGIS implements SimJob {

	private ArrayList<String> operationLogsIn = null;
	private File operationLogOutDir = null;
	private PGraphDatabaseService pdb = null;

	public SimJobLoadOpsGIS(String[] operationLogsIn, String operationLogsOutDir,
			PGraphDatabaseService pdb) {
		this.operationLogsIn = new ArrayList<String>(Arrays
				.asList(operationLogsIn));
		this.operationLogOutDir = new File(operationLogsOutDir);
		this.pdb = pdb;
	}

	@Override
	public void start() throws Exception {
		if (operationLogsIn.isEmpty() == true)
			throw new Exception("No input Operation log has been specified");

		String operationLogIn = operationLogsIn.remove(0);
		String operationLogOut = logInToLogOut(operationLogIn);

		OperationFactory operationFactory = new LogOperationFactoryGIS(
				operationLogIn);

		Simulator sim = new SimulatorGIS(pdb, operationLogOut, operationFactory);
		sim.startSIM();
	}

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
