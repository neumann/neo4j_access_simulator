package sim_tst;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class DegreeEvaluator {
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
			for(Relationship rs :n.getRelationships()){
				deg++;
				if(n.equals(rs.getStartNode())){
					outDeg++;
				}else{
					inDeg++;
				}
			}
			inDegSum+=inDeg;
			outDegSum+=outDegSum;
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
					minInDegree = deg;
				}else if(deg > maxInDegree){
					maxInDegree = deg;
				}
			
				if(outDeg < minOutDegree){
					minOutDegree = deg;
				}else if(deg > maxOutDegree){
					maxOutDegree = deg;
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
			for(Relationship rs :n.getRelationships()){
				deg++;
				if(n.equals(rs.getStartNode())){
					outDeg++;
				}else{
					inDeg++;
				}
			}
			
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
	
	

}
