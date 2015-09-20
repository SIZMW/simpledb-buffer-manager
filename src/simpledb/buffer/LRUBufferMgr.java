package simpledb.buffer;

import java.util.HashMap;

import simpledb.file.Block;

/**
 * This class handles pinning and unpinning buffers in memory using the least
 * recently used policy for buffer replacement.
 *
 * @author Aditya Nivarthi
 */
public class LRUBufferMgr extends AbstractBufferMgr {

	// The map of the memory buffers
	protected HashMap<Integer, LRUBuffer> buffer;

	/**
	 * Creates a LRUBufferMgr instance with the specified maximum number of
	 * buffers.
	 *
	 * @param numbuffs
	 *            The maximum number of buffers for memory.
	 */
	public LRUBufferMgr(int numbuffs) {
		super(numbuffs);
		buffer = new HashMap<Integer, LRUBuffer>();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see simpledb.buffer.AbstractBufferMgr#available()
	 */
	@Override
	public int available() {
		return bufferCountAvailable;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see simpledb.buffer.AbstractBufferMgr#chooseUnpinnedBuffer()
	 */
	@Override
	protected Buffer chooseUnpinnedBuffer() {
		// If there are buffer spaces left in memory
		if (bufferCountAvailable < maxBufferCount) {
			return new LRUBuffer();
		} else {
			// Find a buffer to remove from memory
			findLeastRecentlyUsed();
			return new LRUBuffer();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * simpledb.buffer.AbstractBufferMgr#findExistingBuffer(simpledb.file.Block)
	 */
	@Override
	protected Buffer findExistingBuffer(Block blk) {
		return buffer.get(new Integer(blk.number()));
	}

	/**
	 * Finds the least recently used buffer and removes it from memory.
	 */
	protected synchronized void findLeastRecentlyUsed() {
		// TODO Search and remove LRU buffer code here
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see simpledb.buffer.AbstractBufferMgr#flushAll(int)
	 */
	@Override
	protected synchronized void flushAll(int txnum) {
		for (Integer block : buffer.keySet()) {
			if (buffer.get(block).isModifiedBy(txnum)) {
				buffer.get(block).flush();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see simpledb.buffer.AbstractBufferMgr#pin(simpledb.file.Block)
	 */
	@Override
	protected synchronized Buffer pin(Block blk) {
		Buffer buff = findExistingBuffer(blk);
		if (buff == null) {
			buff = chooseUnpinnedBuffer();
			if (buff == null)
				return null;
			buff.assignToBlock(blk);
		}

		if (!buff.isPinned()) {
			bufferCountAvailable--;
		}
		buff.pin();

		((LRUBuffer) buff).setLastRecentlyUsedTimeMillis();

		return buff;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see simpledb.buffer.AbstractBufferMgr#pinNew(java.lang.String,
	 * simpledb.buffer.PageFormatter)
	 */
	@Override
	protected synchronized Buffer pinNew(String filename, PageFormatter fmtr) {
		Buffer buff = chooseUnpinnedBuffer();
		if (buff == null) {
			return null;
		}

		buff.assignToNew(filename, fmtr);
		bufferCountAvailable--;
		buff.pin();

		((LRUBuffer) buff).setLastRecentlyUsedTimeMillis();

		return buff;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see simpledb.buffer.AbstractBufferMgr#unpin(simpledb.buffer.Buffer)
	 */
	@Override
	protected synchronized void unpin(Buffer buff) {
		buff.unpin();
		if (!buff.isPinned()) {
			bufferCountAvailable++;
		}

		((LRUBuffer) buff).setLastRecentlyUsedTimeMillis();
	}
}
