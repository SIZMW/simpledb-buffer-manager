package simpledb.buffer;

/**
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
}
