package simpledb.buffer;

/**
 * CS 4432 Project 1
 *
 * We created this class to extend the Buffer clas and have the least recently
 * used time.
 *
 * This class represents an individual buffer used in least recently used
 * replacement policy.
 *
 * @author Lambert Wang
 */
public class LRUBuffer extends Buffer {

	protected long leastRecentlyUsedTimeMillis;

	/**
	 * Creates a LRUBuffer instance. Sets the last recently used time.
	 */
	public LRUBuffer() {
		leastRecentlyUsedTimeMillis = System.currentTimeMillis();
	}

	/**
	 * Returns the least recently used time in milliseconds.
	 *
	 * @return a long
	 */
	public long getLeastRecentlyUsedTimeMillis() {
		return leastRecentlyUsedTimeMillis;
	}

	/**
	 * Sets the least recently used time in milliseconds.
	 */
	public void setLeastRecentlyUsedTimeMillis() {
		leastRecentlyUsedTimeMillis = System.currentTimeMillis();
	}
}
