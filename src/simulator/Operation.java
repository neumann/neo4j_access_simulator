package simulator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.neo4j.graphdb.GraphDatabaseService;

import p_graph_service.PGraphDatabaseService;
import p_graph_service.core.InstanceInfo;
import p_graph_service.core.InstanceInfo.InfoKey;

public abstract class Operation {
	protected static final String ID_TAG = "id";
	protected static final String TYPE_TAG = "type";
	protected static final String ARGS_TAG = "args";
	protected static final String GLOBAL_TRAFFIC = "global_traffic";
	protected static final String LOCAL_TRAFFIC = "local_traffic";
	protected static final String NODE_CHANGE = "n_change";
	protected static final String REL_CHANGE = "rel_change";

	public static final int ARGS_TAG_INDX = 2;

	protected final String[] args;
	protected LinkedHashMap<String, String> info;

	public String getType() {
		return (String) info.get(TYPE_TAG);
	}

	public long getId() {
		return Long.parseLong(info.get(ID_TAG));
	}

	public Operation(String[] args) {
		this.info = new LinkedHashMap<String, String>();
		this.info.put(ID_TAG, args[0]);
		this.info.put(TYPE_TAG, args[1]);
		this.info.put(ARGS_TAG, Arrays.toString(args));
		this.info.put(GLOBAL_TRAFFIC, Long.toString(0));
		this.info.put(LOCAL_TRAFFIC, Long.toString(0));

		this.args = args;

		if (!this.info.get(TYPE_TAG).equals(getType())) {
			throw new Error("Wrong Type " + this.info.get(TYPE_TAG)
					+ " called " + getType());
		}
	}

	public final String[] getInfoHeader() {
		String[] res = new String[this.info.size()];

		int i = 0;
		for (String infoKey : this.info.keySet()) {
			res[i] = infoKey;
			i++;
		}

		return res;
	}

	public final boolean executeOn(GraphDatabaseService db) {

		if (db instanceof PGraphDatabaseService) {
			PGraphDatabaseService pdb = (PGraphDatabaseService) db;

			// take system snapshot
			long[] ids = pdb.getInstancesIDs();
			InstanceInfo[] preSnapShot = new InstanceInfo[ids.length];

			for (int i = 0; i < ids.length; i++) {
				pdb.resetLoggingOn(ids[i]);
				preSnapShot[i] = (pdb.getInstanceInfoFor(ids[i])).takeSnapshot();
			}

			// execute operation
			boolean res = onExecute(db);

			InstanceInfo[] postSnapShot = new InstanceInfo[ids.length];
			for (int i = 0; i < ids.length; i++) {
				postSnapShot[i] = (pdb.getInstanceInfoFor(ids[i])).takeSnapshot();
			}
			
			// calculate changes to what was before
			InstanceInfo[] difference = new InstanceInfo[ids.length];
			for (int i = 0; i < ids.length; i++) {
				difference[i] = preSnapShot[i].differenceTo(postSnapShot[i]);
			}			
			
			// dynamic print of communication between charts
			HashMap<String, Long> gloTrafMap = new HashMap<String, Long>();
			for(int i=0; i<ids.length; i++){
				for(Long k :difference[i].globalTrafficMap.keySet()){
					String newK;
					if(ids[i]<k){
						newK = ids[i]+"_with_" + k;
					} else {
						newK = k+"_with_" + ids[i];
					}
					
					if(gloTrafMap.containsKey(newK)){
						gloTrafMap.put(newK, gloTrafMap.get(newK)+difference[i].globalTrafficMap.get(k));
					}else{
						gloTrafMap.put(newK, difference[i].globalTrafficMap.get(k));
					}
				}
			}
			for(String k : gloTrafMap.keySet()){
				info.put("traffic_"+k, gloTrafMap.get(k).toString());
			}
			
			// print numnodes for each chart
			for(int i=0; i<ids.length; i++){
				String dynKey = "Chart_"+ids[i]+"_";
				info.put(dynKey+"numNodes", postSnapShot[i].getValue(InfoKey.NumNodes)+"");
			}
			
			// print numrelas for each chart
			for(int i=0; i<ids.length; i++){
				String dynKey = "Chart_"+ids[i]+"_";
				info.put(dynKey+"numRelas", postSnapShot[i].getValue(InfoKey.NumRelas)+"");
			}
			// print loc traffic for each chart
			for(int i=0; i<ids.length; i++){
				String dynKey = "Chart_"+ids[i]+"_";
				info.put(dynKey+"traffic", difference[i].getValue(InfoKey.Loc_Traffic)+"");
			}
			
			// calculate global traffic and local traffic for plot
			Long sumInterHops = 0l;
			Long sumTraffic = 0l;
			for (InstanceInfo df : difference) {
				sumInterHops += df.getValue(InfoKey.Glo_Traffic);
				sumTraffic += df.getValue(InfoKey.Loc_Traffic);
			}
			info.put(GLOBAL_TRAFFIC, sumInterHops.toString());
			info.put(LOCAL_TRAFFIC, sumTraffic.toString());
			
			return res;
		}
		return onExecute(db);
	}

	public abstract boolean onExecute(GraphDatabaseService db);

	private String appendix = "";

	public String getApendix() {
		return appendix;
	}

	public final void appendToLog(String item) {
		appendix += item;
	}
}
