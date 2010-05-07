package simulator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.neo4j.graphdb.GraphDatabaseService;

import p_graph_service.PGraphDatabaseService;

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

	@SuppressWarnings("unchecked")
	public final boolean executeOn(GraphDatabaseService db) {

		if (db instanceof PGraphDatabaseService) {
			PGraphDatabaseService pdb = (PGraphDatabaseService) db;
			// take system snapshot
			long[] ids = pdb.getInstancesIDs();
			long[] nodes = new long[ids.length];
			long[] relas = new long[ids.length];

			// reset traffic
			pdb.resetTrafficRecords();
			for (int i = 0; i < ids.length; i++) {
				nodes[i] = pdb.getNumNodesOn(ids[i]);
				relas[i] = pdb.getNumRelationsOn(ids[i]);
			}

			// execute operation
			boolean res = onExecute(db);

			long[] serverTraffic = new long[ids.length];
			HashMap[] serverInterhops = new HashMap[ids.length];

			// take mesurements
			for (int i = 0; i < ids.length; i++) {
				nodes[i] -= pdb.getNumNodesOn(ids[i]);
				relas[i] -= pdb.getNumRelationsOn(ids[i]);
				serverTraffic[i] = pdb.getTrafficOn(ids[i]);
				serverInterhops[i] = pdb.getTrafficRecordFor(ids[i]);
			}

			// System.out.println("traffic " + Arrays.toString(serverTraffic));
			// System.out.println("iterHop " +
			// Arrays.toString(serverInterhops));

			Long sumInterhops = (long) 0;
			for (HashMap<Long, Long> partitionInterhops : serverInterhops) {
				for (Long interhopsTo : partitionInterhops.values()) {
					sumInterhops += interhopsTo;
				}
			}
			sumInterhops = sumInterhops / 2;

			info.put(INTERHOP_TAG, sumInterhops.toString());

			Long sumTraffic = (long) 0;
			for (long partitionTraffic : serverTraffic) {
				sumTraffic += partitionTraffic;
			}

			info.put(TRAFFIC_TAG, sumTraffic.toString());

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
