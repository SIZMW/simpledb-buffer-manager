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
	protected HashMap<Block, LRUBuffer> buffer;

	/**
	 * Creates a LRUBufferMgr instance with the specified maximum number of
	 * buffers.
	 *
	 * @param numbuffs
	 *            The maximum number of buffers for memory.
	 */
	public LRUBufferMgr(int numbuffs) {
		super(numbuffs);
		buffer = new HashMap<Block, LRUBuffer>();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see simpledb.buffer.AbstractBufferMgr#available()
	 */
	@Override
	public int available() {
		return numAvailable;
	}

	/**
	 * Chooses an unpinned buffer to replace with a new page. If there is no
	 * space in the memory buffer, the least recently used buffer is emptied and
	 * returned.
	 *
	 * (non-Javadoc)
	 *
	 * @see simpledb.buffer.AbstractBufferMgr#chooseUnpinnedBuffer()
	 */
	@Override
	protected Buffer chooseUnpinnedBuffer() {
		if (numAvailable > 0) {
			return new LRUBuffer();
		}

		return findLeastRecentlyUsed();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * simpledb.buffer.AbstractBufferMgr#findExistingBuffer(simpledb.file.Block)
	 */
	@Override
	protected Buffer findExistingBuffer(Block blk) {
		return buffer.get(blk);
	}

	/**
	 * Finds the least recently used buffer and removes it from memory. If the
	 * memory buffer is empty, returns a new buffer to write into. Otherwise,
	 * returns the least recently used buffer.
	 */
	protected synchronized LRUBuffer findLeastRecentlyUsed() {
		long time = -1;
		Block blk = null;

		if (buffer.keySet().size() <= 0) {
			return new LRUBuffer();
		}

		for (Block block : buffer.keySet()) {
			LRUBuffer buff = buffer.get(block);
			if (!buff.isPinned()) {
				if (time == -1) {
					time = buff.getLeastRecentlyUsedTimeMillis();
					blk = block;
				} else if (time > buff.getLeastRecentlyUsedTimeMillis()) {
					time = buff.getLeastRecentlyUsedTimeMillis();
					blk = block;
				}
			}
		}

		if (blk != null) {
			return buffer.remove(blk);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see simpledb.buffer.AbstractBufferMgr#flushAll(int)
	 */
	@Override
	protected synchronized void flushAll(int txnum) {
		for (Block block : buffer.keySet()) {
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

			if (buff == null) {
				return null;
			}
			buff.assignToBlock(blk);
			buffer.put(blk, (LRUBuffer) buff);
		}

		if (!buff.isPinned()) {
			numAvailable--;
		}
		buff.pin();

		((LRUBuffer) buff).setLeastRecentlyUsedTimeMillis();

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
		buffer.put(buff.block(), (LRUBuffer) buff);
		numAvailable--;
		buff.pin();

		((LRUBuffer) buff).setLeastRecentlyUsedTimeMillis();

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
			numAvailable++;
		}

		((LRUBuffer) buff).setLeastRecentlyUsedTimeMillis();
	}
}
