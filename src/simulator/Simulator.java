package simulator;

import java.io.PrintStream;
import java.util.HashMap;

import org.neo4j.graphdb.GraphDatabaseService;

public abstract class Simulator extends Thread {
	private static final String delim = ";";
	
	private GraphDatabaseService db = null;
	private PrintStream log = null;
	private boolean exit;
	private boolean active;
	private boolean toHold;
	
	protected GraphDatabaseService getDB(){
		return db;
	}
	
	protected void logOperation(Operation op){
		HashMap<String, String> info = op.getHopInfo();
		for(String str : Operation.getInfoHeader()){
			log.print(info.get(str));
			log.print(delim);
		}
		log.println();
	}
	
	public Simulator(GraphDatabaseService db, String logFile) {
		this.db=db;
		try {
			this.log = new PrintStream(logFile);
		} catch (Exception e) {
		}
	}
	
	public void shutdown(){
		this.exit = true;
		if(!active){
			this.notify();
		}
	}
	
	public abstract void initiate();
	public abstract void loop();
	
	public void start(){
		run();
	}
	
	public void toHold(){
		this.toHold = true;
	}
	
	public void unHold(){
		this.toHold = false;
		if(!active){
			this.notify();
		}
	}
	
	@Override
	public void run() {
		while (!exit) {
			while (active && !toHold ){
				loop();
			}
			try {
				active = false;
				wait();
			} catch (InterruptedException ie) {
				active = true;
			}
		}
		log.flush();
		log.close();
	}
}
