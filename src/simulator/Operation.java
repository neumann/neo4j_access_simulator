package simulator;

import java.util.Arrays;
import java.util.HashMap;
import org.neo4j.graphdb.GraphDatabaseService;

public abstract class Operation {
	protected static final String type_tag = "type";
	protected static final String args_tag = "args";
	protected static final String interHop_tag = "interHop";
	protected static final String hop_tag = "hop";
	protected static final String id_tag = "id";
	protected static final String traffic_tag = "traffic";

	public static String[] getInfoHeader() {
		String[] res = new String[5];
		res[0] = id_tag;
		res[1] = type_tag;
		res[2] = hop_tag;
		res[3] = interHop_tag;
		res[4] = traffic_tag;
		return res;
	}

	protected final String[] args;
	protected HashMap<String, String> info;

	public String getType() {
		return (String) info.get(type_tag);
	}

	public long getId() {
		return Long.parseLong(info.get(id_tag));
	}

	public Operation(long id, String[] args) {
		this.info = new HashMap<String, String>();
		for (String key : getInfoHeader()) {
			info.put(key, "");
		}
		this.info.put(interHop_tag, Long.toString(0));
		this.info.put(hop_tag, Long.toString(0));
		this.info.put(id_tag, Long.toString(id));
		this.info.put(args_tag, Arrays.toString(args));
		this.info.put(type_tag, getClass().getName());
		this.args = args;

		if (!args[0].equals(getType())) {
			throw new Error("Wrong Type " + args[0] + " called " + getType());
		}

	}

	public final boolean executeOn(GraphDatabaseService db) {
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
