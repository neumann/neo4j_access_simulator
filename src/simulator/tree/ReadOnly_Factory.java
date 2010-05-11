package simulator.tree;

import java.util.TreeMap;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import simulator.Operation;
import simulator.OperationFactory;
import simulator.Rnd;

public class ReadOnly_Factory implements OperationFactory {
	private Object[] sample;
	private int count = 0;
	
	public ReadOnly_Factory(int numOperation, GraphDatabaseService db) {
		TreeMap<Object, Double> nodeMap = new TreeMap<Object, Double>();
		double max =0;
		
		// calculate max
		Transaction tx = db.beginTx();
		try {
			for(Node n : db.getAllNodes()){			
				if(n.hasProperty(TreeArgs.listLenght) && n.hasProperty("name")){
					if(((String)n.getProperty("name")).contains("older")){
						int val = (Integer)n.getProperty(TreeArgs.listLenght);
						max += val;
						nodeMap.put(n.getId(), new Double(val));
					}
				}
			}
			tx.success();
		} finally {
			tx.finish();
		}
//		System.out.println(nodeMap);
		sample = Rnd.getSampleFromMap(nodeMap, max, numOperation, Rnd.RndType.unif);
//		System.out.println(Arrays.toString(sample));
	}
	
	@Override
	public boolean hasNext() {
		return count < sample.length;
	}

	@Override
	public Operation next() {
//		System.out.println(((Long)sample[count]).toString());
		
		String[] args = {count+"", LogReadOp_CountFiles.class.getName(), ((Long)sample[count]).toString()};
		count ++;
		return new LogReadOp_CountFiles(args);
	}

	@Override
	public void shutdown() {
		// nothing to do here
	}

}
