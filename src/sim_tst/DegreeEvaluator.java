package sim_tst;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class DegreeEvaluator {
	
	public static void main(String[] args) {
		GraphDatabaseService db = new EmbeddedGraphDatabase("var/fstree-didic2_700kNodes_1300Relas");
		DegreeEvaluator ev = new DegreeEvaluator(db);
		ev.calcDegreeSTD();
		
		try {

			File f = new File("degreeTree.info");
			ev.toFile(f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		db.shutdown();
		
	}
	
	private GraphDatabaseService db;
	
	private long nodeCount = 0;
	private double inDegSum = 0;
	private double outDegSum = 0;
	private double degSum = 0;
	
	int minInDegree = -1;
	int minOutDegree = -1;
	int minDegree = -1;
	
	int maxInDegree = -1;
	int maxOutDegree = -1;
	int maxDegree = -1;
	
	double avgOutDegree = -1;
	double avgInDegree = -1;
	double avgDegree = -1;
	
	double stdInDegree = -1;
	double stdOutDegree = -1;
	double stdDegree = -1;
	
	public DegreeEvaluator(GraphDatabaseService db) {
		this.db = db;
	}
	
	public void calcDegree(){
		
		// initiate
		nodeCount = 0;
		inDegSum = 0;
		outDegSum = 0;
		degSum = 0;
		
		minInDegree = Integer.MAX_VALUE;
		minOutDegree = Integer.MAX_VALUE;
		minDegree = Integer.MAX_VALUE;
		
		maxInDegree = 0;
		maxOutDegree = 0;
		maxDegree = 0;
		
		// calculate degree
		for(Node n : db.getAllNodes()){
			nodeCount ++;
			int deg = 0;
			int inDeg = 0;
			int outDeg = 0;
			for(Relationship rs :  n.getRelationships(Direction.OUTGOING)){
				outDeg ++;
			}
			for(Relationship rs :  n.getRelationships(Direction.INCOMING)){
				inDeg ++;
			}
			deg = inDeg+outDeg;
			
			inDegSum+=inDeg;
			outDegSum+=outDeg;
			degSum+=deg;
			
			if(minDegree == -1){
				minDegree = deg;
				minInDegree = inDeg;
				minOutDegree = outDeg;
				
				maxDegree = deg;
				maxInDegree = inDeg;
				maxOutDegree = outDeg;
			}else{
				
				if(deg < minDegree){
					minDegree = deg;
				}else if(deg > maxDegree){
					maxDegree = deg;
				}
				
				if(inDeg < minInDegree){
					minInDegree = inDeg;
				}else if(deg > maxInDegree){
					maxInDegree = inDeg;
				}
			
				if(outDeg < minOutDegree){
					minOutDegree = outDeg;
				}else if(deg > maxOutDegree){
					maxOutDegree = outDeg;
				}
			}	
		}
		
		avgInDegree = inDegSum/(double)nodeCount;
		avgOutDegree = outDegSum/(double)nodeCount;
		avgDegree = degSum/(double)nodeCount;	
	}
	
	public void calcDegreeSTD(){
		if(avgDegree == -1){
			calcDegree();
		}
		
		// calculate degree
		for(Node n : db.getAllNodes()){
			int deg = 0;
			int inDeg = 0;
			int outDeg = 0;	
			for(Relationship rs :  n.getRelationships(Direction.OUTGOING)){
				outDeg ++;
			}
			for(Relationship rs :  n.getRelationships(Direction.INCOMING)){
				inDeg ++;
			}
			deg = inDeg+outDeg;
			
			stdDegree += Math.pow((deg - avgDegree),2); 
			stdInDegree += Math.pow((inDeg - avgInDegree),2); 
			stdOutDegree += Math.pow((outDeg - avgOutDegree),2);
		}
		stdDegree = stdDegree/(nodeCount-1);
		stdInDegree = stdInDegree/(nodeCount-1);
		stdOutDegree = stdOutDegree/(nodeCount-1);
		
		stdDegree = Math.sqrt(stdDegree);
		stdInDegree = Math.sqrt(stdInDegree);
		stdOutDegree = Math.sqrt(stdOutDegree);
	}
	
	public void toFile(File f) throws FileNotFoundException{
		PrintStream ps = new PrintStream(f);
		ps.println("minInDegree = "+ minInDegree);
		ps.println("maxInDegree = "+ maxInDegree);
		ps.println("avgInDegree = "+ avgInDegree);
		ps.println("stdInDegree = "+ stdInDegree);
		ps.println();
		ps.println("minOutDegree = "+ minOutDegree);
		ps.println("maxOutDegree = "+ maxOutDegree);
		ps.println("avgOutDegree = "+ avgOutDegree);
		ps.println("stdOutDegree = "+ stdOutDegree);
		ps.println();
		ps.println("minDegree = "+ minDegree);
		ps.println("maxDegree = "+ maxDegree);
		ps.println("avgDegree = "+ avgDegree);
		ps.println("stdDegree = "+ stdDegree);
		ps.close();
	}

}
