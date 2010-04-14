package simulator.tree;

import org.neo4j.graphdb.GraphDatabaseService;

import simulator.Operation;
import simulator.OperationFactory;

public class ReadOperationsOnlyFactory extends OperationFactory {
	private long counter; 
	
	public ReadOperationsOnlyFactory(GraphDatabaseService db) {
		super(db);
		this.counter = 0;	
	}
	
	@Override
	public Operation nextOperation() {
		counter++;
		
		return new CalculateSubtreeOperation(getDB(), counter);
	}

}
