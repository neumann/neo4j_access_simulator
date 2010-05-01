package simulator;

import java.io.PrintStream;

import org.neo4j.graphdb.GraphDatabaseService;

public abstract class Simulator extends Thread {
	private static final String logFileDelim = ";";
	private PrintStream log = null;

	private GraphDatabaseService db = null;
	private boolean toExit = false;
	private boolean toHold = false;

	protected GraphDatabaseService getDB() {
		return db;
	}

	protected void logOperation(Operation op) {
		for (String str : Operation.getInfoHeader()) {
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
			for (String tag : Operation.getInfoHeader()) {
				log.print(tag);
				log.print(logFileDelim);
			}
			log.println();
		} catch (Exception e) {
			System.out.println("Cannot create logfile");
		}
	}

	public void shutdown() {
		this.toExit = true;
		if (getState() == Thread.State.WAITING) {
			this.notify();
		}
	}

	public abstract void initiate();

	public abstract void loop();

	public void startSIM() {
		if (getState() == Thread.State.NEW) {
			initiate();
			run();
		}
	}

	public void toHold() {
		this.toHold = true;
	}

	public void unHold() {
		this.toHold = false;
		if (getState() == Thread.State.WAITING) {
			this.notify();
		}
	}

	@Override
	public void run() {
		while (!toExit) {
			if (!toHold) {
				loop();
			}
			if (toHold) {
				try {
					toHold = false;
					wait();
				} catch (InterruptedException ie) {
					// do nothing
				}
			}
		}
		log.flush();
		log.close();
	}
}
