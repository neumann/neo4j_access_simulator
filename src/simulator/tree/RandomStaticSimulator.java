package simulator.tree;

import org.neo4j.graphdb.GraphDatabaseService;

import simulator.Operation;
import simulator.Rnd;
import simulator.Simulator;

public class RandomStaticSimulator extends Simulator {
	private long count = 0;
	
	public RandomStaticSimulator(GraphDatabaseService db, String logFile) {
		super(db, logFile);
	}

	@Override
	public void initiate() {
		Rnd.initiate(1000);
		count = 0;
	}

	@Override
	public void loop() {
		if(count == 100){
			getDB().shutdown();
			this.shutdown();
		}
		count++;
		Operation op = new CopyOfCountFilesInSubtree(getDB(), count++);
		System.out.println(op.getId());
		System.out.println(op.getType());
		System.out.println("--------------------"+ count);
		op.executeOn(getDB());
		logOperation(op);
	}

}
