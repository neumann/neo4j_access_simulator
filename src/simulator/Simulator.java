package simulator;

import org.neo4j.graphdb.GraphDatabaseService;

public abstract class Simulator extends Thread {
	private GraphDatabaseService db = null;
	private boolean exit;
	private boolean active;
	private boolean toHold;
	
	public Simulator(GraphDatabaseService db) {
		this.db=db;
	}
	
	public void shutdown(){
		exit = true;
		
		if(!active){
			db.shutdown();
			System.exit(0);
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
				wait();
				active = false;
			} catch (InterruptedException ie) {
				active = true;
			}
		}
	}
}
