package simulator.tree;

import java.util.TreeMap;
import java.util.Vector;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import simulator.Operation;
import simulator.OperationFactory;
import simulator.Rnd;
import simulator.Rnd.RndType;

public class SearchFilesOnly_Factory implements OperationFactory {
	private Object[] sample;
	private int count = 0;
	private GraphDatabaseService db;

	public SearchFilesOnly_Factory(int numOperation, GraphDatabaseService db) {
		this.db = db;
		TreeMap<Object, Double> nodeMap = new TreeMap<Object, Double>();
		double max = 0;
		// calculate max
		Transaction tx = db.beginTx();
		try {
			for (Node n : db.getAllNodes()) {
				if (n.hasProperty(TreeArgs.listLenght) && n.hasProperty("name")) {
					if (((String) n.getProperty("name")).contains("older")) {
						int val = (Integer) n.getProperty(TreeArgs.listLenght);
						max += val;
						nodeMap.put(n.getId(), new Double(val));
					}
				}
			}
			tx.success();
		} finally {
			tx.finish();
		}

		// System.out.println(nodeMap);
		sample = Rnd.getSampleFromMap(nodeMap, max, numOperation,
				Rnd.RndType.unif);
		// System.out.println(Arrays.toString(sample));
	}

	@Override
	public boolean hasNext() {
		return count < sample.length;
	}

	@Override
	public Operation next() {
		// System.out.println(((Long)sample[count]).toString());

		Operation res = createSearchOp((Long) sample[count]);
		count++;
		return res;
	}

	@Override
	public void shutdown() {
		// nothing to do here
	}

	private Operation createSearchOp(long id) {
		long srtNID = -1;
		long endNID = -1;

		// find startNode
		Transaction tx = db.beginTx();
		try {
			Node sNode = db.getNodeById(id);

			Vector<Node> files = new Vector<Node>();
			files.add(sNode);
			for (Relationship rs : sNode.getRelationships(
					TreeArgs.TreeRelTypes.CHILD_ITEM, Direction.OUTGOING)) {
				Node n = rs.getEndNode();
				files.add(n);
			}
			Node n = files.get((int) Rnd.nextLong(0, files.size() - 1,
					RndType.unif));
			endNID = n.getId();

			// walk to beginning
			while (srtNID == -1) {
				Relationship rs = n.getSingleRelationship(
						TreeArgs.TreeRelTypes.CHILD_ITEM, Direction.INCOMING);
				if (rs == null) {
					srtNID = n.getId();
				} else {
					n = rs.getStartNode();
				}
			}

			tx.success();
		} finally {
			tx.finish();
		}

		String[] args = { count + "", SearchFiles_ReadOp.class.getName(),
				srtNID + "", endNID + "" };

		if (srtNID < 0 || endNID < 0) {
			return null;
		}

		return new SearchFiles_ReadOp(args);
	}

}
