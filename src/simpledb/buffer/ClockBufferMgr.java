package simpledb.buffer;

import java.util.HashMap;

import simpledb.file.Block;

/**
 * This class handles pinning and unpinning buffers in memory using the clock
 * policy for buffer replacement.
 *
 * @author Aditya Nivarthi
 */
public class ClockBufferMgr extends AbstractBufferMgr {

	// The map of the memory buffers
	protected HashMap<Integer, ClockBuffer> buffer;

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
		buffer = new HashMap<Integer, ClockBuffer>();
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
			return new Buffer();
		} else {
			// Find a buffer to remove from memory
			findBufferClockPolicy();
			return new Buffer();
		}
	}

	/**
	 * Finds a buffer to remove by clock policy and removes it from memory.
	 */
	protected synchronized void findBufferClockPolicy() {
		// TODO Search and remove clock policy buffer code here
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
		((ClockBuffer) buff).setRefBit(true);
		clockHeadPosition = buff.block().number();

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
		((ClockBuffer) buff).setRefBit(true);
		clockHeadPosition = buff.block().number();

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
		((ClockBuffer) buff).setRefBit(true);
		clockHeadPosition = buff.block().number();
		if (!buff.isPinned()) {
			bufferCountAvailable++;
		}
	}
}
