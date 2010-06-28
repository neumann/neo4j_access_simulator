package simulator.tree;

import org.neo4j.graphdb.GraphDatabaseService;

import simulator.Operation;
import simulator.OperationFactory;
import simulator.Rnd;
import simulator.Simulator;

public class TreeOps_Sim extends Simulator {
	private OperationFactory fac;
	private int i;
	private simType type;
	private Distribution dis = null;
	
	public enum simType {
		SEARCH, COUNT, MIX
	}
	
	
	public TreeOps_Sim(GraphDatabaseService db, String logFile, int lenght, simType t) {
		super(db, logFile);
		this.i = lenght;
		this.type = t;
	}
	
	
	public TreeOps_Sim(GraphDatabaseService db, String logFile, int lenght, simType t,Distribution dis ) {
		super(db, logFile);
		this.i = lenght;
		this.type = t;
		this.dis = dis;
	}
	
	

	@Override
	public void initiate() {
		switch (type) {
		case MIX:
			if(dis == null){
				return;
				
			}
			fac = new TreeOps_Factory(i, getDB(), dis,1000);
			return;
		case COUNT:
			dis = new Distribution(1,0,0,0, 0);
			fac = new TreeOps_Factory( i, getDB(), dis, i);
			return;
		case SEARCH:
			dis = new Distribution(0,1,0,0, 0);
			fac = new TreeOps_Factory( i, getDB(), dis, i);
			return;
		default:
			break;
		}
		
//		System.out.println("done inst");
	}

	@Override
	public void loop() {
		if(fac.hasNext()){
			Operation op = fac.next();
			op.executeOn(getDB());
			//System.out.println(op.getId());
			logOperation(op);
		}else{
			getDB().shutdown();
			shutdown();
		}
	}

}
