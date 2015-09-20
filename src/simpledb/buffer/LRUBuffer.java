package simpledb.buffer;

/**
 * This class represents an individual buffer used in least recently used
 * replacement policy.
 *
 * @author Aditya Nivarthi
 */
public class LRUBuffer extends Buffer {

	protected long leastRecentlyUsedTimeMillis;

	/**
	 * Creates a LRUBuffer instance. Sets the last recently used time.
	 */
	public LRUBuffer() {
		super();
		leastRecentlyUsedTimeMillis = System.currentTimeMillis();
	}

	/**
	 * Returns the least recently used time in milliseconds.
	 *
	 * @return a long
	 */
	public long getLastRecentlyUsedTimeMillis() {
		return leastRecentlyUsedTimeMillis;
	}

	/**
	 * Sets the least recently used time in milliseconds.
	 */
	public void setLastRecentlyUsedTimeMillis() {
		leastRecentlyUsedTimeMillis = System.currentTimeMillis();
	}
}
