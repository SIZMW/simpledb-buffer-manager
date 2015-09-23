package simpledb.buffer;

import java.util.Iterator;
import java.util.LinkedHashMap;

import simpledb.file.Block;

/**
 * This class handles pinning and unpinning buffers in memory using the clock
 * policy for buffer replacement.
 *
 * @author Aditya Nivarthi
 */
public class ClockBufferMgr extends AbstractBufferMgr {

	// The map of the memory buffers
	protected LinkedHashMap<Block, ClockBuffer> buffer;

	// Location of the clock head
	protected Block clockHeadPosition = null;

	/**
	 * Creates a LRUBuffer instance with the specified maximum number of
	 * buffers.
	 *
	 * @param numbuffs
	 *            The maximum number of buffers for memory.
	 */
	public ClockBufferMgr(int numbuffs) {
		super(numbuffs);
		buffer = new LinkedHashMap<Block, ClockBuffer>();
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
		if (numAvailable > 0) {
			return new ClockBuffer();
		}
		return findBufferClockPolicy();
	}

	/**
	 * Finds a buffer to remove by clock policy and removes it from memory.
	 */
	protected synchronized Buffer findBufferClockPolicy() {
		if (buffer.keySet().size() <= 0) {
			return new ClockBuffer();
		}

		Iterator<Block> iterator = buffer.keySet().iterator();
		outerloop: while (iterator.hasNext()) {
			if (iterator.next().equals(clockHeadPosition)) {
				break outerloop;
			}
		}

		for (int j = 0; j < 2; j++) {
			while (iterator.hasNext()) {
				Block blk = iterator.next();
				if (buffer.get(blk).isPinned()) {
				} else if (buffer.get(blk).getRefBit()) {
					buffer.get(blk).setRefBit(false);
				} else {
					clockHeadPosition = blk;
					return buffer.get(blk);
				}
			}
			iterator = buffer.keySet().iterator();
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
		return buffer.get(blk);
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
			buffer.put(blk, (ClockBuffer) buff);

			if (clockHeadPosition == null) {
				clockHeadPosition = blk;
			}
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

		buffer.put(buff.block(), (ClockBuffer) buff);

		if (clockHeadPosition == null) {
			clockHeadPosition = buff.block();
		}

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
