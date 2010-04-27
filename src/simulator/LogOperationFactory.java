package simulator;

import java.io.IOException;
import java.io.RandomAccessFile;

import org.neo4j.graphdb.GraphDatabaseService;

public abstract class LogOperationFactory extends OperationFactory {
	private RandomAccessFile file = null;
	private String curLine = null;
	private long count;

	public LogOperationFactory(GraphDatabaseService db, String fn) {
		super(db);
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
		String[] split = curLine.split("* ");
		return createOperation(count, split[0], split);
	}

	public abstract Operation createOperation(long id, String type,
			String[] args);

	@Override
	public void shutdown() {
		try {
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
