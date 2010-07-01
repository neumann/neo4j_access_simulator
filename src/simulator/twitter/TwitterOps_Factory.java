package simulator.twitter;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import p_graph_service.PGraphDatabaseService;
import p_graph_service.core.InstanceInfo;
import p_graph_service.core.InstanceInfo.InfoKey;

import simulator.Evaluator;
import simulator.Operation;
import simulator.OperationFactory;
import simulator.Rnd;
import simulator.Rnd.RndType;
import simulator.twitter.operations.ReadOp_Search;
import simulator.twitter.operations.Shuffle_WriteOp;

public class TwitterOps_Factory implements OperationFactory {
	private long[] sampleRead;
	private long[] sampleWrite;
	
	private int count = 0;
	private double maxRead;
	private double maxWrite;
	private double[] dis;

	public TwitterOps_Factory(int numOperation, GraphDatabaseService db, double[] dis) {
		this.maxRead = 0;
		this.maxWrite = 0;
		this.dis = dis;
		
		if (!(db instanceof PGraphDatabaseService)) {
			throw new Error(
					"ReadOnly Factory only works for PGraphDatabaseService implementations");
		}

		PGraphDatabaseService pdb = (PGraphDatabaseService) db;
		for (long id : pdb.getInstancesIDs()) {
			InstanceInfo inf = pdb.getInstanceInfoFor(id) ;
			maxRead += inf.getValue(InfoKey.NumRelas);
			maxWrite += inf.getValue(InfoKey.NumNodes);
		}

		sampleRead = Rnd.getSampleFromDB(pdb, new OutEdge(), maxRead, numOperation,
				RndType.unif);
		sampleWrite = Rnd.getSampleFromDB(pdb, new Uniform(), maxWrite, numOperation, RndType.unif);
	}

	@Override
	public boolean hasNext() {	
		return (count < sampleRead.length && count < sampleWrite.length);
	}

	@Override
	public Operation next() {
		if (hasNext()) {
			double choice = Rnd.nextDouble(RndType.unif);
			
			if(choice < dis[0]){
				String[] args = { count + "", ReadOp_Search.class.getName(),
						sampleRead[count] + "" };
				count++;
				return new ReadOp_Search(args);
			}
			
			if(choice < dis[0]+dis[1]){
				String[] args = { count + "", Shuffle_WriteOp.class.getName(),
						sampleWrite[count] + "" };
				count++;
				return new Shuffle_WriteOp(args);
			}
		}
		return null;
	}

	@Override
	public void shutdown() {
		// nothing to do here
	}

	private class OutEdge extends Evaluator {

		@Override
		public double evaluate(Node n) {
			double res = 0;
			for (@SuppressWarnings("unused")
			Relationship rs : n.getRelationships(Direction.OUTGOING)) {
				res++;
			}
			return res;
		}	
	}
	
	private class Uniform extends Evaluator {
		@Override
		public double evaluate(Node n) {
			return 1;
		}	
	}
}
