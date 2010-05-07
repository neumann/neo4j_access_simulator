package simulator.tree;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import simulator.Operation;

public class LogWriteOp_AddFile extends Operation {

	public LogWriteOp_AddFile(String[] args) {
		super(args);
	}

	@Override
	public boolean onExecute(GraphDatabaseService db) {
		boolean res = false;
		Transaction tx = db.beginTx();
		try {
			Node snode = db.getNodeById(Long.parseLong(args[2]));
			if(snode.hasProperty(TreeArgs.name)){
				String name = (String) snode.getProperty(TreeArgs.name);
//				System.out.println(name);
				if(name.contains("Folder")){
					int count = (Integer) snode.getProperty(TreeArgs.listLenght);
					snode.setProperty(TreeArgs.listLenght, count++);
					
					Node nNode = db.createNode();
//					System.out.println("--------------------- "+ nNode.getId());
					nNode.setProperty(TreeArgs.name, "FileBy "+ args[0]+" "+ args[1]);
					nNode.setProperty(TreeArgs.listLenght, new Integer(1));
					nNode.setProperty(TreeArgs.size, new Long(0));
					snode.createRelationshipTo(nNode, TreeArgs.TreeRelTypes.CHILD_ITEM);
				}
			}
			res = true;
			tx.success();
		} finally{
			tx.finish();
		}
		return res;
	}

}
