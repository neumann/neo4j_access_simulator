package simulator;

public interface OperationFactory {
	
	public abstract boolean hasNext();

	public abstract Operation next();

	public abstract void shutdown();

}
