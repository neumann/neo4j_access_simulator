package simulator.twitter;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import p_graph_service.PGraphDatabaseService;
import p_graph_service.core.InstanceInfo.InfoKey;

import simulator.Evaluator;
import simulator.Operation;
import simulator.OperationFactory;
import simulator.Rnd;
import simulator.Rnd.RndType;

public class ReadOnly_Factory implements OperationFactory {
	private long[] sample;
	private int count = 0;
	private double max;

	public ReadOnly_Factory(int numOperation, GraphDatabaseService db) {
		this.max = 0;
		
		if(!(db instanceof PGraphDatabaseService)){
			throw new Error("ReadOnly Factory only works for PGraphDatabaseService implementations");
		}
		
		PGraphDatabaseService pdb = (PGraphDatabaseService)db;
		for(long id : pdb.getInstancesIDs()){
			max += pdb.getInstanceInfoFor(id).getValue(InfoKey.NumRelas);
		}
		
		sample = Rnd.getSampleFromDB(pdb, new OutEdge(), max, numOperation, RndType.unif);
		
		
	}

	@Override
	public boolean hasNext() {
		return count < sample.length;
	}

	@Override
	public Operation next() {
		if(hasNext()){
			String[] args = { count + "", ReadOp_Search.class.getName(),
					sample[count]+"" };
			count++;
			return new ReadOp_Search(args);
		}
		
		return null;
	}

	@Override
	public void shutdown() {
		// nothing to do here
	}
	
	private class OutEdge extends Evaluator{

		@Override
		public double evaluate(Node n) {
			double res = 0;
			for(@SuppressWarnings("unused") Relationship rs : n.getRelationships(Direction.OUTGOING)){
				res++;
			}	
			return res;
		}
		
	}
}
