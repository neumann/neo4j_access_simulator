package simulator.gis;

import java.io.RandomAccessFile;
import java.util.StringTokenizer;
import java.util.Vector;

import simulator.LogOperationFactory;
import simulator.Operation;

public class LogOperationFactoryImplGIS extends LogOperationFactory {
	protected RandomAccessFile file = null;
	protected String curLine = null;
	protected long count;

	public LogOperationFactoryImplGIS(String fn) {
		super(fn);
	}

	@Override
	public Operation createOperation(String[] args) {
		// if (args[1].equals(OperationGISAddNode.class.getName())) {
		// return new OperationGISAddNode(Long.parseLong(args[0]), args);
		// }
		//
		// if (args[1].equals(OperationGISDeleteNode.class.getName())) {
		//
		// }
		//
		// if (args[1].equals(OperationGISShortestPathLocal.class.getName())) {
		//
		// }
		//
		// if (args[1].equals(OperationGISShortestPathGlobal.class.getName())) {
		//
		// }

		return null;
	}

	@Override
	public Operation next() {
		hasNext();
		count++;
		String[] args = extractArgs(curLine);
		return createOperation(args);
	}

	@Override
	protected String[] extractArgs(String curLine) {

		Vector<String> resultVector = new Vector<String>();

		int index = -1;

		String tokenOuter = null;

		StringTokenizer stOuter = new StringTokenizer(curLine, ";\t\n\r\f");

		while (stOuter.hasMoreTokens()) {

			index++;

			tokenOuter = stOuter.nextToken();

			if (index == Operation.ARGS_TAG_INDX) {
				for (String arg : tokenOuter.replaceAll("[\\[\\]]", "").split(
						", ")) {
					resultVector.add(arg);
				}
				break;
			}

		}

		return resultVector.toArray(new String[resultVector.size()]);
	}

}
