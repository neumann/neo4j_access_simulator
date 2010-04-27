package simulator;

import java.util.Arrays;
import java.util.HashMap;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Relationship;

public abstract class Operation {
	private static final String isHalf_tag = "_IsHalf";
	
	protected static final String type_tag = "type";
	protected static final String args_tag = "args";
	protected static final String interHop_tag = "interHop";
	protected static final String hop_tag = "hop";
	protected static final String id_tag = "id";
	
	public static String[] getInfoHeader(){
		String[] res = new String[4];
		res[0] = id_tag;
		res[1] = type_tag;
		res[2] = hop_tag;
		res[3] = interHop_tag;
		return res;
	}
	
	private GraphDatabaseService db;
	protected HashMap<String, String> info;
	protected final String[] args;
	
	public String getType(){
		return (String)info.get(type_tag);
	}
	
	public long getId(){
		return Long.parseLong(info.get(id_tag));
	}
	
	protected GraphDatabaseService getDB(){
		return db;
	}
	
	protected void logMovement(Relationship rs){
		long count = Long.parseLong(info.get(hop_tag));
		count ++;
		info.put(hop_tag, Long.toString(count));
		
		if(rs.hasProperty(isHalf_tag)){
			long iCount = Long.parseLong(info.get(hop_tag));
			iCount ++;
			info.put(hop_tag, Long.toString(iCount));			
		}
	}
	
	public Operation(GraphDatabaseService db, long id, String[] args) {
		this.db = db;
		this.info = new HashMap<String, String>();
		for(String key : getInfoHeader()){
			info.put(key, "");
		}
		this.info.put(interHop_tag, Long.toString(0));
		this.info.put(hop_tag, Long.toString(0));
		this.info.put(id_tag,Long.toString(id));
		this.info.put(args_tag, Arrays.toString(args));
		this.info.put(type_tag,getClass().getName());
		this.args = args;
	}
	
	public abstract boolean execute();
	
	public HashMap<String, String> getHopInfo(){
		return info;
	}
}
