package simpledb.buffer;

import simpledb.file.Block;

/**
 * This class handles pinning and unpinning buffers in memory using the clock
 * policy for buffer replacement.
 *
 * @author Aditya Nivarthi
 */
public class ClockBufferMgr extends AbstractBufferMgr {

	// The map of the memory buffers
	protected ClockBuffer[] bufferpool;

	// Location of the clock head
	protected int clockHeadPosition = 0;

	/**
	 * Creates a LRUBuffer instance with the specified maximum number of
	 * buffers.
	 *
	 * @param numbuffs
	 *            The maximum number of buffers for memory.
	 */
	public ClockBufferMgr(int numbuffs) {
		super(numbuffs);
		bufferpool = new ClockBuffer[numbuffs];
		for (int i = 0; i < numbuffs; i++) {
			bufferpool[i] = new ClockBuffer();
		}
		clockHeadPosition = 0;
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

	/*
	 * (non-Javadoc)
	 *
	 * @see simpledb.buffer.AbstractBufferMgr#chooseUnpinnedBuffer()
	 */
	@Override
	protected Buffer chooseUnpinnedBuffer() {
		return findBufferClockPolicy();
	}

	/**
	 * Finds a buffer to remove by clock policy and removes it from memory.
	 */
	protected synchronized Buffer findBufferClockPolicy() {
		for (int j = 0; j < 2; j++) {
			for (int i = clockHeadPosition; i < bufferpool.length; i++) {
				if (bufferpool[i].isPinned()) {
				} else if (bufferpool[i].getRefBit()) {
					bufferpool[i].setRefBit(false);
				} else {
					clockHeadPosition = i;
					return bufferpool[i];
				}
			}
			clockHeadPosition = 0;
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * simpledb.buffer.AbstractBufferMgr#findExistingBuffer(simpledb.file.Block)
	 */
	@Override
	protected Buffer findExistingBuffer(Block blk) {
		for (Buffer buff : bufferpool) {
			Block b = buff.block();
			if (b != null && b.equals(blk)) {
				return buff;
			}
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
		for (Buffer buff : bufferpool) {
			if (buff.isModifiedBy(txnum)) {
				buff.flush();
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
		}
		if (!buff.isPinned()) {
			numAvailable--;
		}
		buff.pin();
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
		numAvailable--;
		buff.pin();
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
	}
}
