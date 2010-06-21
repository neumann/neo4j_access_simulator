package simulator.tree;

import java.util.Vector;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import simulator.Evaluator;
import simulator.Operation;
import simulator.OperationFactory;
import simulator.Rnd;
import simulator.Rnd.RndType;

public class TreeOps_Factory implements OperationFactory {
	private double invNumMax = 0;
	private double numMax = 0;

	private static final int STEP_SIZE = 100;

	private final Distribution dis;
	private int length;

	private long[] file;
	private long[] folder;

	private int folderPoint = 0;
	private int invFilePoint = 0;

	private final GraphDatabaseService db;
	private long count = 0;

	public TreeOps_Factory(int lenght, GraphDatabaseService db, Distribution dis) {
		this.db = db;
		this.length = lenght;
		this.dis = dis;

		// calculate max
		Transaction tx = db.beginTx();
		try {
			for (Node n : db.getAllNodes()) {
				if (n.hasProperty(TreeArgs.listLenght)) {
					int val = (Integer) n.getProperty(TreeArgs.listLenght);
					double invVal = 1 / ((double) val);
					numMax += val;
					invNumMax += invVal;
				}
			}
			tx.success();
		} finally {
			tx.finish();
		}
		// add sample
		folder = Rnd.getSampleFromDB(db, new lengthEvaluatorFolders(), numMax,
				STEP_SIZE, RndType.unif);

		// delete sample
		file = Rnd.getSampleFromDB(db, new invLengthEvaluator(), invNumMax,
				STEP_SIZE, RndType.unif);
	}

	@Override
	public boolean hasNext() {
		if (count < length)
			return true;
		return false;
	}

	@Override
	public Operation next() {
		if (!hasNext())
			return null;

		double choice = Rnd.nextDouble(RndType.unif);
		if (choice < dis.delOp) {
			// sample still valid?
			long validNodeID = -1;
			Node validNode = null;

			while (validNode == null) {
				validNodeID = file[invFilePoint];
				invFilePoint++;
				Transaction tx = db.beginTx();
				tx.success();
				try {
					validNode = db.getNodeById(validNodeID);
				} catch (Exception e) {
					// nothing to do here
				} finally {
					tx.finish();
				}
				if (invFilePoint >= STEP_SIZE) {
					file = Rnd.getSampleFromDB(db, new invLengthEvaluator(),
							invNumMax, STEP_SIZE, RndType.unif);
					invFilePoint = 0;
				}
			}
			String[] args = { count + "", DeleteItems_WriteOp.class.getName(),
					validNodeID + "" };
			invNumMax--;
			count++;
			return new DeleteItems_WriteOp(args);
		}

		if (choice < dis.addOp) {
			// sample still valid?
			long validNodeID = -1;
			Node validNode = null;

			while (validNode == null) {
				validNodeID = file[invFilePoint];
				invFilePoint++;
				Transaction tx = db.beginTx();
				tx.success();
				try {
					validNode = db.getNodeById(validNodeID);

				} catch (Exception e) {
					// nothing to do here
				} finally {
					tx.finish();
				}

				if (invFilePoint >= STEP_SIZE) {
					file = Rnd.getSampleFromDB(db, new invLengthEvaluator(),
							invNumMax, STEP_SIZE, RndType.unif);
					invFilePoint = 0;
				}
			}

			String[] args = { count + "", AddFile_WriteOp.class.getName(),
					validNodeID + "" };
			invNumMax++;
			count++;
			return new AddFile_WriteOp(args);
		}

		if (choice < dis.searchOp) {
			// sample still valid?
			long validNodeID = -1;
			Node validNode = null;

			while (validNode == null) {
				validNodeID = folder[folderPoint];
				folderPoint++;
				try {
					validNode = db.getNodeById(validNodeID);
				} catch (Exception e) {
					// nothing to do here
				}

				if (folderPoint >= STEP_SIZE) {
					folder = Rnd.getSampleFromDB(db,
							new lengthEvaluatorFolders(), numMax, STEP_SIZE,
							RndType.unif);
					folderPoint = 0;
				}
			}

			Operation res = createSearchOp(validNodeID);
			count++;
			return res;

		}

		if (choice < dis.countOp) {
			// sample still valid?
			long validNodeID = -1;
			Node validNode = null;

			while (validNode == null) {
				validNodeID = folder[folderPoint];
				folderPoint++;
				Transaction tx = db.beginTx();
				tx.success();
				try {
					validNode = db.getNodeById(validNodeID);
				} catch (Exception e) {
					// nothing to do here
				} finally {
					tx.finish();
				}

				if (folderPoint >= STEP_SIZE) {
					folder = Rnd.getSampleFromDB(db,
							new lengthEvaluatorFolders(), numMax, STEP_SIZE,
							RndType.unif);
					folderPoint = 0;
				}
			}
			String[] args = { count + "", CountFiles_ReadOp.class.getName(),
					validNodeID + "" };
			count++;
			return new CountFiles_ReadOp(args);
		}

		return null;

	}

	private Operation createSearchOp(long id) {
		long srtNID = id;
		long endNID = -1;

		// startNode
		Node sNode = db.getNodeById(id);
		Vector<Node> n2Go = new Vector<Node>();
		n2Go.add(sNode);
		int marker = 0;
		while(marker <= n2Go.size()){
			Node n = n2Go.get(marker);
			for(Relationship rs : n.getRelationships(TreeArgs.TreeRelTypes.CHILD_ITEM, Direction.OUTGOING)){
				n2Go.add(rs.getEndNode());
			}
		}
		long res = Rnd.nextLong(0, n2Go.size()-1, Rnd.RndType.unif);
		endNID = n2Go.get((int)res).getId();
		
		String[] args = { count + "", SearchFiles_ReadOp.class.getName(),
				srtNID + "", endNID + "" };
		return new SearchFiles_ReadOp(args);
	}

	@Override
	public void shutdown() {
		// nothing to do here
	}

	private class invLengthEvaluator extends Evaluator {
		@Override
		public double evaluate(Node n) {
			if (n.hasProperty(TreeArgs.name)) {
				if (((String) n.getProperty(TreeArgs.name)).contains("File")) {
					return ((double) 1);
				}
			}
			return 0;
		}
	}

	private class lengthEvaluatorFolders extends Evaluator {
		@Override
		public double evaluate(Node n) {
			if (n.hasProperty(TreeArgs.listLenght)
					&& n.hasProperty(TreeArgs.name)) {
				if (n.hasProperty(TreeArgs.hasSub)) {
					return (Integer) n.getProperty(TreeArgs.listLenght);
				}
				String name = (String) n.getProperty(TreeArgs.name);
				if (name.contains("Folder")) {
					return (Integer) n.getProperty(TreeArgs.listLenght);
				}
			}
			return 0;
		}
	}
}
