package simpledb.buffer;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.logging.Level;

import simpledb.file.Block;
import simpledb.server.SimpleDB;

/**
 * CS 4432 Project 1
 *
 * We added this class to handle clock replacement policy for a buffer manager.
 *
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
	 * Creates a ClockBufferMgr instance with the specified maximum number of
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
		printBufferContents();
		if (numAvailable > 0) {
			return new ClockBuffer();
		}

		long startTime = System.currentTimeMillis();
		SimpleDB.getLogger().log(Level.INFO, "Start time: " + startTime);

		Buffer ret = findBufferClockPolicy();

		long endTime = System.currentTimeMillis();
		SimpleDB.getLogger().log(Level.INFO, "End time: " + endTime);
		SimpleDB.getLogger().log(Level.INFO, "Time elapsed: " + (endTime - startTime) + " ms");

		return ret;
	}

	/**
	 * Finds a buffer to remove by clock policy and removes it from memory.
	 */
	protected synchronized Buffer findBufferClockPolicy() {
		// Check if buffer is empty
		if (buffer.keySet().size() <= 0) {
			return new ClockBuffer();
		}

		// Find the clock head position block in the list of buffer slots
		Iterator<Block> iterator = buffer.keySet().iterator();
		outerloop: while (iterator.hasNext()) {
			if (iterator.next().equals(clockHeadPosition)) {
				break outerloop;
			}
		}

		// Run at least the partial pass from clockHeadPosition to the end of
		// the buffer list, and then repeat from the start of the buffer list.
		for (int j = 0; j < 2; j++) {
			while (iterator.hasNext()) {
				Block blk = iterator.next();

				// Skip pinned blocks
				if (buffer.get(blk).isPinned()) {
				} else if (buffer.get(blk).getRefBit()) {
					// Set reference bits to false (0) if not pinned
					buffer.get(blk).setRefBit(false);
					SimpleDB.getLogger().log(Level.INFO, "Set reference bit to 'false' on block: " + blk);
				} else {
					// Found a block to replace
					clockHeadPosition = blk;
					SimpleDB.getLogger().log(Level.INFO, "Removed block: " + blk + " from buffer");
					return buffer.remove(blk);
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
		ClockBuffer buff = buffer.get(blk);
		if (buff != null) {
			buff.setRefBit(true);
		}

		return buff;
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
		SimpleDB.getLogger().log(Level.INFO, "Searched for existing block: " + blk + " and block was: " + buff);

		if (buff == null) {
			buff = chooseUnpinnedBuffer();
			if (buff == null) {
				SimpleDB.getLogger().log(Level.SEVERE, "Unpinned buffer was null");
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
			numAvailable = (numAvailable < 0) ? 0 : numAvailable;
		}

		SimpleDB.getLogger().log(Level.INFO, "Number available: " + numAvailable);

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
			SimpleDB.getLogger().log(Level.SEVERE, "Unpinned buffer was null");
			return null;
		}
		buff.assignToNew(filename, fmtr);
		buffer.put(buff.block(), (ClockBuffer) buff);

		if (clockHeadPosition == null) {
			clockHeadPosition = buff.block();
		}

		numAvailable--;
		numAvailable = (numAvailable < 0) ? 0 : numAvailable;

		SimpleDB.getLogger().log(Level.INFO, "Number available: " + numAvailable);

		buff.pin();
		return buff;
	}

	/**
	 * Prints the buffer contents to the log output.
	 */
	protected void printBufferContents() {
		String output = "";
		for (Block blk : buffer.keySet()) {
			output += blk + ": " + buffer.get(blk) + "\n";
		}

		SimpleDB.getLogger().log(Level.INFO, "\n\nBuffer Contents:\n" + output);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see simpledb.buffer.AbstractBufferMgr#unpin(simpledb.buffer.Buffer)
	 */
	@Override
	protected synchronized void unpin(Buffer buff) {
		buff.unpin();
		SimpleDB.getLogger().log(Level.INFO, "Buffer unpinned: " + buff);
	}
}
