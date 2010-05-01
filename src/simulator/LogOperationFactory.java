package simulator;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.StringTokenizer;
import java.util.Vector;

public abstract class LogOperationFactory implements OperationFactory {
	protected RandomAccessFile file = null;
	protected String curLine = null;
	protected long count;

	public LogOperationFactory(String fn) {
		this.count = 0;
		try {
			file = new RandomAccessFile(fn, "r");
		} catch (Exception e) {
		}
	}

	@Override
	public boolean hasNext() {
		if (curLine != null)
			return true;
		if (file != null) {
			try {
				curLine = file.readLine();
				if (curLine != null) {
					return true;
				} else {
					file.close();
					file = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public Operation next() {
		hasNext();
		count++;
		String[] args = extractArgs(curLine);
		return createOperation(args);
	}

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

	public abstract Operation createOperation(String[] args);

	@Override
	public void shutdown() {
		try {
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
