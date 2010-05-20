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

	private final double DelOp_Chance = 0.5;
	private final double AddOp_Chance = 1;
	private final double ReadSearch_Chance = 0;
	private final double ReadOp_Chance = 0;
	private int length;

	private long[] sampleInv;
	private long[] sample;

	private int samplePoint = 0;
	private int invSamplePoint = 0;

	private final GraphDatabaseService db;
	private long count = 0;

	public TreeOps_Factory(int lenght,GraphDatabaseService db ) {
		this.db = db;
		this.length = lenght;

		System.out.println("loading");
		
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
		sample = Rnd.getSampleFromDB(db, new lengthEvaluatorFolders(), numMax,
				STEP_SIZE, RndType.unif);

		// delete sample
		sampleInv = Rnd.getSampleFromDB(db, new invLengthEvaluator(),
				invNumMax, STEP_SIZE, RndType.unif);
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
		if (choice < DelOp_Chance) {
			// sample still valid?
			long validNodeID = -1;
			Node validNode = null;

			while (validNode == null) {
				validNodeID = sampleInv[invSamplePoint];
				invSamplePoint++;
				Transaction tx = db.beginTx();
				tx.success();
				try {
					validNode = db.getNodeById(validNodeID);
				} catch (Exception e) {
					// nothing to do here
				} finally {
					tx.finish();
				}
				if (invSamplePoint >= STEP_SIZE) {
					sampleInv = Rnd.getSampleFromDB(db,
							new invLengthEvaluator(), invNumMax, STEP_SIZE,
							RndType.unif);
					invSamplePoint = 0;
				}
			}
			String[] args = { count + "",
					DeleteItems_WriteOp.class.getName(), validNodeID + "" };
			invNumMax--;
			count++;
			return new DeleteItems_WriteOp(args);
		}

		if (choice < AddOp_Chance) {
			// sample still valid?
			long validNodeID = -1;
			Node validNode = null;

			while (validNode == null) {
				validNodeID = sampleInv[invSamplePoint];
				invSamplePoint++;
				Transaction tx = db.beginTx();
				tx.success();
				try {
					validNode = db.getNodeById(validNodeID);
					
				} catch (Exception e) {
					// nothing to do here
				} finally {
					tx.finish();
				}

				if (invSamplePoint >= STEP_SIZE) {
					sampleInv = Rnd.getSampleFromDB(db,
							new invLengthEvaluator(), invNumMax, STEP_SIZE,
							RndType.unif);
					invSamplePoint = 0;
				}
			}

			String[] args = { count + "", AddFile_WriteOp.class.getName(),
					validNodeID + "" };
			invNumMax++;
			count++;
			return new AddFile_WriteOp(args);
		}

		if (choice < ReadSearch_Chance) {
			// sample still valid?
			long validNodeID = -1;
			Node validNode = null;

			while (validNode == null) {
				validNodeID = sample[samplePoint];
				samplePoint++;
				Transaction tx = db.beginTx();
				tx.success();
				try {
					validNode = db.getNodeById(validNodeID);
				} catch (Exception e) {
					// nothing to do here
				} finally {
					tx.finish();
				}

				if (samplePoint >= STEP_SIZE) {
					sample = Rnd.getSampleFromDB(db,
							new lengthEvaluatorFolders(), numMax, STEP_SIZE,
							RndType.unif);
					samplePoint = 0;
				}
			}
			
			Operation res = createSearchOp(validNodeID);
			count++;
			return res;
			
		}

		if (choice < ReadOp_Chance) {
			// sample still valid?
			long validNodeID = -1;
			Node validNode = null;

			while (validNode == null) {
				validNodeID = sample[samplePoint];
				samplePoint++;
				Transaction tx = db.beginTx();
				tx.success();
				try {
					validNode = db.getNodeById(validNodeID);
				} catch (Exception e) {
					// nothing to do here
				} finally {
					tx.finish();
				}

				if (samplePoint >= STEP_SIZE) {
					sample = Rnd.getSampleFromDB(db,
							new lengthEvaluatorFolders(), numMax, STEP_SIZE,
							RndType.unif);
					samplePoint = 0;
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
				if (!n.hasProperty(TreeArgs.hasSub)) {
					files.add(n);
				}
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
		} finally{
			tx.finish();
		}

		String[] args = { count + "", SearchFiles_ReadOp.class.getName(),
				srtNID + "", endNID + "" };
		
		if (srtNID < 0 || endNID < 0) {
			return null;
		}

		return new SearchFiles_ReadOp(args);
	}

	@Override
	public void shutdown() {
		// nothing to do here
	}

	private class invLengthEvaluator extends Evaluator {
		@Override
		public double evaluate(Node n) {
			if (n.hasProperty(TreeArgs.listLenght) && n.hasProperty("name")) {
				if(((String)n.getProperty("name")).contains("ile")){
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
				String name = (String) n.getProperty(TreeArgs.name);
				if (name.contains("older")) {
					return (Integer) n.getProperty(TreeArgs.listLenght);
				}
			}
			return 0;
		}
	}
}
