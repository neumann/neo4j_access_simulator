package simulator;

import java.io.PrintStream;

import org.neo4j.graphdb.GraphDatabaseService;

public abstract class Simulator{
	private static final String logFileDelim = ";";
	private PrintStream log = null;

	private GraphDatabaseService db = null;
	private boolean toExit = false;
	private boolean toHold = false;

	private boolean hasLoggedInfoHeaderNames = false;

	protected GraphDatabaseService getDB() {
		return db;
	}

	protected void logOperation(Operation op) {
		if (hasLoggedInfoHeaderNames == false) {
			for (String tag : op.getInfoHeader()) {
				log.print(tag);
				log.print(logFileDelim);
			}
			log.println();
			hasLoggedInfoHeaderNames = true;
		}

		for (String str : op.getInfoHeader()) {
			log.print(op.info.get(str));
			log.print(logFileDelim);
		}
		log.print(op.getApendix());
		log.print(logFileDelim);
		log.println();
	}

	public Simulator(GraphDatabaseService db, String logFile) {
		this.db = db;
		try {
			this.log = new PrintStream(logFile);
		} catch (Exception e) {
			System.out.println("Cannot create logfile");
		}
	}

	public void shutdown(){
		this.toExit = true;
	}

	public abstract void initiate();

	public abstract void loop();

	public void startSIM() {	
		initiate();
		run();	
	}

	public void toHold() {
		this.toHold = true;
	}

	public void unHold() {
		this.toHold = false;
	}

	public void run() {
		while (!toExit) {
			loop();
		}
		log.flush();
		log.close();
	}

}
