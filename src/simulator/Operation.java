package simulator;

import java.util.Arrays;
import java.util.HashMap;
import org.neo4j.graphdb.GraphDatabaseService;

import p_graph_service.PGraphDatabaseService;
import p_graph_service.core.InstanceInfo;

public abstract class Operation {
	public static final int ID_TAG_INDX = 0;
	public static final int TYPE_TAG_INDX = 1;
	public static final int ARGS_TAG_INDX = 2;
	public static final int HOP_TAG_INDX = 3;
	public static final int INTERHOP_TAG_INDX = 4;
	public static final int TRAFFIC_TAG_INDX = 5;
	public static final int GIS_PATH_LENGTH_TAG_INDX = 6;
	public static final int GIS_DISTANCE_TAG_INDX = 7;

	protected static final String ID_TAG = "id";
	protected static final String TYPE_TAG = "type";
	protected static final String ARGS_TAG = "args";
	protected static final String HOP_TAG = "hop";
	protected static final String INTERHOP_TAG = "interhop";
	protected static final String TRAFFIC_TAG = "traffic";
	protected static final String NODE_CHANGE = "n_change";
	protected static final String REL_CHANGE = "rel_change";

	protected static final String GIS_PATH_LENGTH_TAG = "pathlen";
	protected static final String GIS_DISTANCE_TAG = "distance";

	public static String[] getInfoHeader() {
		String[] res = new String[8];
		res[ID_TAG_INDX] = ID_TAG;
		res[TYPE_TAG_INDX] = TYPE_TAG;
		res[ARGS_TAG_INDX] = ARGS_TAG;
		res[HOP_TAG_INDX] = HOP_TAG;
		res[INTERHOP_TAG_INDX] = INTERHOP_TAG;
		res[TRAFFIC_TAG_INDX] = TRAFFIC_TAG;
		res[GIS_PATH_LENGTH_TAG_INDX] = GIS_PATH_LENGTH_TAG;
		res[GIS_DISTANCE_TAG_INDX] = GIS_DISTANCE_TAG;
		return res;
	}

	protected final String[] args;
	protected HashMap<String, String> info;

	public String getType() {
		return (String) info.get(TYPE_TAG);
	}

	public long getId() {
		return Long.parseLong(info.get(ID_TAG));
	}

	public Operation(String[] args) {
		this.info = new HashMap<String, String>();
		for (String key : getInfoHeader()) {
			info.put(key, "");
		}
		this.info.put(ID_TAG, args[0]);
		this.info.put(TYPE_TAG, getClass().getName());
		this.info.put(ARGS_TAG, Arrays.toString(args));
		this.info.put(HOP_TAG, Long.toString(0));
		this.info.put(INTERHOP_TAG, Long.toString(0));
		this.info.put(TRAFFIC_TAG, Long.toString(0));
		this.info.put(GIS_PATH_LENGTH_TAG, Long.toString(0));
		this.info.put(GIS_DISTANCE_TAG, Long.toString(0));

		this.args = args;

		if (!args[TYPE_TAG_INDX].equals(getType())) {
			throw new Error("Wrong Type " + args[TYPE_TAG_INDX] + " called "
					+ getType());
		}

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
				sumInterHops += df.interHop;
				sumIntraHops += df.intraHop;
				sumTraffic += df.traffic;
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
