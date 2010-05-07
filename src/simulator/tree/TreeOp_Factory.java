package simulator.tree;

import java.util.Arrays;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import simulator.Operation;
import simulator.OperationFactory;
import simulator.Rnd;
import simulator.Rnd.RndType;

public class TreeOp_Factory implements OperationFactory {
	private double invNumMax = 0;
	private double numMax = 0;
	
	private static final int STEP_SIZE = 100;
	//private static final double ReadOp_Chance = 0.8;
	private static final double DelOp_Chance = 0.1;
	private static final double AddOp_Chance = 0.9;
	
	private long[] sampleInv;
	private long[] sample;
	private int samplePoint=0;
	private int invSamplePoint=0;
	
	private final GraphDatabaseService db;
	private long count = 0;
	
	public TreeOp_Factory(GraphDatabaseService db) {
		this.db = db;
		
		// calculate max
		Transaction tx = db.beginTx();
		try {
			for(Node n : db.getAllNodes()){
				if(n.hasProperty(TreeArgs.listLenght)){
					int val = (Integer)n.getProperty(TreeArgs.listLenght);
					double invVal = 1/((double)val);
					numMax += val;
					invNumMax += invVal;
				}
			}
			tx.success();
		} finally {
			tx.finish();
		}
		
		sample = Rnd.getSampleFromDB(db, TreeArgs.listLenght, numMax , STEP_SIZE, RndType.unif);
//		System.out.println(Arrays.toString(sample));
		sampleInv = Rnd.getInverseSampleFromDB(db, TreeArgs.listLenght, invNumMax, STEP_SIZE, RndType.unif);
//		System.out.println(Arrays.toString(sampleInv));
	}

	@Override
	public boolean hasNext() {
		return true;
	}

	@Override
	public Operation next() {
		// update sample
		if(samplePoint >= STEP_SIZE){
			sample = Rnd.getSampleFromDB(db, TreeArgs.listLenght, numMax , STEP_SIZE, RndType.unif);
			samplePoint =0;
		}
		if(invSamplePoint >= STEP_SIZE){
			sampleInv = Rnd.getInverseSampleFromDB(db, TreeArgs.listLenght, invNumMax, STEP_SIZE, RndType.unif);
			invSamplePoint = 0;
		}
	
		double choice = Rnd.nextDouble(RndType.unif);
		if(choice < DelOp_Chance + AddOp_Chance){
			// check if still valid
			long validNodeID = -1;
			Node validNode = null;
			
			while ( invSamplePoint < STEP_SIZE && validNode == null){
				validNodeID = sampleInv[invSamplePoint];
				invSamplePoint++;
				Transaction tx = db.beginTx();
				try {
					validNode = db.getNodeById(validNodeID);
					tx.success();
				} catch (Exception e) {
					// nothing to do here
				} finally{
					tx.finish();
				}
				
				if(invSamplePoint >= STEP_SIZE){
					sampleInv = Rnd.getInverseSampleFromDB(db, TreeArgs.listLenght, invNumMax, STEP_SIZE, RndType.unif);
					invSamplePoint = 0;
				}
			}
			
			
			if(choice < (DelOp_Chance)){
				
				String[] args = {count+"", LogWriteOp_DeleteItems.class.getName(), validNodeID+""};
				invNumMax--;
				count ++;
				return new LogWriteOp_DeleteItems(args);				
			}else{
				String[] args = {count+"", LogWriteOp_AddFile.class.getName(), validNodeID+""};
				invNumMax++;
				count ++;
				return new LogWriteOp_AddFile(args);
			}
		}else {
			// check if still valid
			long validNodeID = -1;
			Node validNode = null;
			
			while ( samplePoint < STEP_SIZE && validNode == null){
				validNodeID = sample[samplePoint];
				samplePoint++;
				Transaction tx = db.beginTx();
				try {
					validNode = db.getNodeById(validNodeID);
					tx.success();
				} catch (Exception e) {
					// nothing to do here
				} finally{
					tx.finish();
				}
				
				if(samplePoint >= STEP_SIZE){
					sample = Rnd.getInverseSampleFromDB(db, TreeArgs.listLenght, numMax, STEP_SIZE, RndType.unif);
					samplePoint = 0;
				}
			}
			String[] args = {count+"", LogReadOp_CountFiles.class.getName(), validNodeID+""};
			count ++;
			return new LogReadOp_CountFiles(args);
		}
		
	}

	@Override
	public void shutdown() {
		// nothing to do here
	}

}
