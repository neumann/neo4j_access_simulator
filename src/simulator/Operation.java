package simulator;

import java.util.Arrays;
import java.util.LinkedHashMap;

import org.neo4j.graphdb.GraphDatabaseService;

import p_graph_service.PGraphDatabaseService;
import p_graph_service.core.InstanceInfo;
import p_graph_service.core.InstanceInfo.InfoKey;

public abstract class Operation {
	protected static final String ID_TAG = "id";
	protected static final String TYPE_TAG = "type";
	protected static final String ARGS_TAG = "args";
	protected static final String HOP_TAG = "hop";
	protected static final String INTERHOP_TAG = "interhop";
	protected static final String TRAFFIC_TAG = "traffic";
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
		this.info.put(HOP_TAG, Long.toString(0));
		this.info.put(INTERHOP_TAG, Long.toString(0));
		this.info.put(TRAFFIC_TAG, Long.toString(0));

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
				preSnapShot[i] = pdb.getInstanceInfoFor(ids[i]);	
			}

			// execute operation
			boolean res = onExecute(db);

			// calculate changes to what was before
			InstanceInfo[] difference = new InstanceInfo[ids.length];
			for (int i = 0; i < ids.length; i++) {
				difference[i] = preSnapShot[i].differenceTo(pdb.getInstanceInfoFor(ids[i]));	
			}
			
			// calculate sums for plot
			long sumInterHops =0;
			long sumIntraHops =0;
			long sumTraffic =0;
			for(InstanceInfo df : difference){
				sumInterHops += df.getValue(InfoKey.InterHop);
				sumIntraHops += df.getValue(InfoKey.IntraHop);
				sumTraffic += df.getValue(InfoKey.Traffic);
			}
			
			info.put(INTERHOP_TAG, sumInterHops+"");
			info.put(TRAFFIC_TAG, sumTraffic+"");
			info.put(HOP_TAG, sumIntraHops+"");
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
