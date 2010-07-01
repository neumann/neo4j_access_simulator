package simulator.tree.tools;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import simulator.tree.TreeArgs;

public class TreeInformationGratherer {
	private static GraphDatabaseService db;
	public static void main(String[] args) {
		db = new EmbeddedGraphDatabase(args[0]);
		
		NodeInfo();
		TreeHight();
		NodesPerLevel();
		
		db.shutdown();
	}
	
	
	
	private static void NodesPerLevel(){
		Vector<Integer> vec = new Vector<Integer>();
		HashSet<Node> cur = new HashSet<Node>();
		HashSet<Node> future = new HashSet<Node>();
		
		cur.add(db.getReferenceNode());
		while(!cur.isEmpty()){
			vec.add(cur.size());
			for(Node n : cur){
				for(Relationship rs : n.getRelationships(Direction.OUTGOING)){
					if(!TreeArgs.isEvent(rs)){
						future.add(rs.getEndNode());
					}
				}
			}
			cur = future;
			future = new HashSet<Node>();
		}
		System.out.println(vec);
	}
	

	private static void TreeHight(){
		int hight = 0;
		for(Node n: db.getAllNodes()){
			int steps = StepsToRoot(n);
			if(steps > hight)hight = steps;
		}
		
		System.out.println("hight: "+ hight);
	}
	
	private static int StepsToRoot(Node n){
		int res = 0;
		Node cur = n;
		while (cur!=null){
			Iterator<Relationship> iter = cur.getRelationships(Direction.INCOMING).iterator();
			cur = null;
			while(cur == null && iter.hasNext()){
				Relationship rs = iter.next();
				if(!TreeArgs.isEvent(rs)){
					cur = rs.getStartNode();
					res ++;
				}
			}
		}
		return res;
	}
	
	private static void NodeInfo(){
		long foldercount = 0l;
		long filecount = 0l;
		long orgaisationcount = 0l;
		long usercount = 0l;
		long nodecout = 0l;
		
		for(Node n : db.getAllNodes()){
			nodecout++;
			if(n.hasProperty("name")){
				String name = (String)n.getProperty("name");
				
				if(name.contains("Folder")){
					foldercount++;
				}
				
				if(name.contains("File")){
					filecount++;
				}
				
				if(name.contains("user")){
					usercount++;
					foldercount++;
				}
				
				if(name.contains("Org")){
					orgaisationcount++;
				}
				
			}
		}
		
		System.out.println("Folder: "+foldercount);
		System.out.println("File: "+ filecount);
		System.out.println("user: "+ usercount);
		System.out.println("Org: "+ orgaisationcount);
		System.out.println("Nodes: "+ nodecout);

	}
	
}
