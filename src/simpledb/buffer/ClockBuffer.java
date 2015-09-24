package simpledb.buffer;

/**
 * CS 4432 Project 1
 *
 * This class represents an individual buffer used in clock replacement policy.
 *
 * @author Aditya Nivarthi
 */
public class ClockBuffer extends Buffer {

	protected boolean refBit = false;

	/**
	 * Creates a ClockBuffer instance. Sets the reference bit to true.
	 */
	public ClockBuffer() {
		refBit = true;
	}

	/**
	 * Returns the reference bit state.
	 *
	 * @return a boolean
	 */
	public boolean getRefBit() {
		return refBit;
	}

	/**
	 * Sets the reference bit state to the specified state.
	 *
	 * @param state
	 *            The boolean state of the reference bit.
	 */
	public void setRefBit(boolean state) {
		refBit = state;
	}

	/**
	 * CS 4432 Project 1
	 *
	 * We added this method to override the method in Buffer and give the state
	 * of the reference bit.
	 *
	 * (non-Javadoc)
	 * 
	 * @see simpledb.buffer.Buffer#toString()
	 */
	@Override
	public String toString() {
		return super.toString() + ", Reference bit: " + refBit;
	}
}
