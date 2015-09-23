package simpledb.buffer;

import simpledb.file.Block;

/**
 * This abstract class is used to define the methods needed for a buffer
 * manager.
 *
 * @author Aditya Nivarthi
 */
public abstract class AbstractBufferMgr {

	// The total number of buffers that can fit in memory
	protected int maxBufferCount;

	// The current number of free buffers
	protected int numAvailable = 0;

	/**
	 * Creates a AbstractBufferMgr instance with the specified maximum number of
	 * buffers.
	 *
	 * @param numbuffs
	 *            The maximum number of buffers for memory.
	 */
	public AbstractBufferMgr(int numbuffs) {
		maxBufferCount = numbuffs;
		numAvailable = numbuffs;
	}

	/**
	 * Returns the number of free memory buffers.
	 *
	 * @return an integer
	 */
	public abstract int available();

	/**
	 * Chooses an unpinned buffer in memory.
	 *
	 * @return a Buffer
	 */
	protected abstract Buffer chooseUnpinnedBuffer();

	/**
	 * Finds if the specified block is currently in memory.
	 *
	 * @param blk
	 *            The block to find in memory.
	 * @return a Buffer
	 */
	protected abstract Buffer findExistingBuffer(Block blk);

	/**
	 * Flushes all buffers modified within the specified transaction.
	 *
	 * @param txnum
	 *            The transaction number for the transaction to flush all
	 *            buffers for.
	 */
	protected abstract void flushAll(int txnum);

	/**
	 * Pins the specified block in memory. If the block is not in memory, it is
	 * brought to memory.
	 *
	 * @param blk
	 *            The block to pin in memory.
	 * @return a Buffer
	 */
	protected abstract Buffer pin(Block blk);

	/**
	 * Pins a new buffer in memory with the file name and the page format
	 * specified.
	 *
	 * @param filename
	 *            The name of the file to pin in memory.
	 * @param fmtr
	 *            The formatter to use for this page.
	 * @return a Buffer
	 */
	protected abstract Buffer pinNew(String filename, PageFormatter fmtr);

	/**
	 * Unpin the specified buffer from memory.
	 *
	 * @param buff
	 *            The buffer to unpin in memory.
	 */
	protected abstract void unpin(Buffer buff);
}
