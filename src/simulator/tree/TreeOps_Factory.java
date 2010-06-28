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
import simulator.tree.operations.AddFile_WriteOp;
import simulator.tree.operations.CountFiles_ReadOp;
import simulator.tree.operations.DeleteItems_WriteOp;
import simulator.tree.operations.SearchFiles_ReadOp;
import simulator.tree.operations.Shuffle_WriteOp;

public class TreeOps_Factory implements OperationFactory {
	private double fileMax = 0;
	private double folderMax = 0;
	private double fileFolderMax = 0;
	

	private Evaluator fileEval = new FileEvaluator();
	private Evaluator folderEval = new FolderEvaluator();
	private Evaluator fileFolderEval = new FileFolderEvaluator();
	
	
	private final int STEP_SIZE;

	private final Distribution dis;
	private int length;

	private long[] file;
	private long[] folder;
	private long[] fileFolder;
	
	
	private int folderPoint = 0;
	private int filePoint = 0;
	private int fileFolderPoint = 0;
	
	
	private final GraphDatabaseService db;
	private long count = 0;

	public TreeOps_Factory(int lenght, GraphDatabaseService db, Distribution dis, int Stepsize) {
		this.STEP_SIZE = Stepsize;
		this.db = db;
		this.length = lenght;
		this.dis = dis;

		// calculate max
		for (Node n : db.getAllNodes()) {
			fileMax += fileEval.evaluate(n);
			folderMax += folderEval.evaluate(n);
		}
		fileFolderMax = fileMax+folderMax;
		
		// add sample
		folder = Rnd.getSampleFromDB(db, folderEval, folderMax,
				STEP_SIZE, RndType.unif);

		// delete sample
		file = Rnd.getSampleFromDB(db, fileEval, fileMax,
				STEP_SIZE, RndType.unif);
		
		fileFolder = Rnd.getSampleFromDB(db, fileFolderEval, fileFolderMax, Stepsize, RndType.unif);
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
				validNodeID = file[filePoint];
				filePoint++;
				Transaction tx = db.beginTx();
				tx.success();
				try {
					validNode = db.getNodeById(validNodeID);
				} catch (Exception e) {
					// nothing to do here
				} finally {
					tx.finish();
				}
				if (filePoint >= STEP_SIZE) {
					file = Rnd.getSampleFromDB(db, fileEval,
							fileMax, STEP_SIZE, RndType.unif);
					filePoint = 0;
				}
			}
			String[] args = { count + "", DeleteItems_WriteOp.class.getName(),
					validNodeID + "" };
			fileMax--;
			count++;
			return new DeleteItems_WriteOp(args);
		}

		if (choice < dis.addOp) {
			// sample still valid?
			long validNodeID = -1;
			Node validNode = null;

			while (validNode == null) {
				validNodeID = file[filePoint];
				filePoint++;
				Transaction tx = db.beginTx();
				tx.success();
				try {
					validNode = db.getNodeById(validNodeID);

				} catch (Exception e) {
					// nothing to do here
				} finally {
					tx.finish();
				}

				if (filePoint >= STEP_SIZE) {
					file = Rnd.getSampleFromDB(db, fileEval,
							fileMax, STEP_SIZE, RndType.unif);
					filePoint = 0;
				}
			}

			String[] args = { count + "", AddFile_WriteOp.class.getName(),
					validNodeID + "" };
			fileMax++;
			count++;
			return new AddFile_WriteOp(args);
		}

		if (choice < dis.searchOp) {
			// sample still valid?
			long validNodeID = -1;
			Node validNode = null;

			while (validNode == null) {
				validNodeID = fileFolder[fileFolderPoint];
				fileFolderPoint++;
				try {
					validNode = db.getNodeById(validNodeID);
				} catch (Exception e) {
					// nothing to do here
				}

				if (fileFolderPoint >= STEP_SIZE) {
					fileFolder = Rnd.getSampleFromDB(db,
							fileFolderEval, fileFolderMax, STEP_SIZE,
							RndType.unif);
					fileFolderPoint = 0;
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
							folderEval, folderMax, STEP_SIZE,
							RndType.unif);
					folderPoint = 0;
				}
			}
			String[] args = { count + "", CountFiles_ReadOp.class.getName(),
					validNodeID + "" };
			count++;
			return new CountFiles_ReadOp(args);
		}

		if (choice < dis.shuffleOp) {
			// sample still valid?
			long validNodeID = -1;
			Node validNode = null;

			while (validNode == null) {
				validNodeID = file[filePoint];
				filePoint++;
				Transaction tx = db.beginTx();
				tx.success();
				try {
					validNode = db.getNodeById(validNodeID);
				} catch (Exception e) {
					// nothing to do here
				} finally {
					tx.finish();
				}
				if (filePoint >= STEP_SIZE) {
					file = Rnd.getSampleFromDB(db, new FileEvaluator(),
							fileMax, STEP_SIZE, RndType.unif);
					filePoint = 0;
				}
			}
			String[] args = { count + "", Shuffle_WriteOp.class.getName(),
					validNodeID + "" };
			fileMax--;
			count++;
			return new Shuffle_WriteOp(args);
		}
		return null;

	}

	private Operation createSearchOp(long id) {
		long srtNID = -1;
		long endNID = id;

		// endNode
		Node curN = db.getNodeById(endNID);
		Vector<Long> n2Go = new Vector<Long>();
		
		while(curN != null){
			Node node =  curN;
			curN = null;
			for (Relationship rs : node.getRelationships(Direction.INCOMING)) {
				if(!TreeArgs.isEvent(rs)){
					curN = rs.getStartNode();
					n2Go.add(curN.getId());
					break;
				}
			}
		}
	
		int choice = (int)Rnd.nextLong(0, n2Go.size(), RndType.expo);
		srtNID = n2Go.get(choice);
		
		String[] args = { count + "", SearchFiles_ReadOp.class.getName(), srtNID + "", endNID + "" };
		return new SearchFiles_ReadOp(args);
	}

	@Override
	public void shutdown() {
		// nothing to do here
	}

	private class FileEvaluator extends Evaluator {
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

	private class FolderEvaluator extends Evaluator {
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
	
	private class FileFolderEvaluator extends Evaluator {
		private Evaluator fileEval = new FileEvaluator();
		private Evaluator folderEval = new FolderEvaluator();
		
		@Override
		public double evaluate(Node n) {
			return fileEval.evaluate(n)+folderEval.evaluate(n);
		}
	}
}
