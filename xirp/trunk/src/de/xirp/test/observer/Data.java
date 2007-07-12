package de.xirp.test.observer;

import java.util.Observable;

/**
 * Example for the Observer Pattern
 * @author Rabea Gransberger
 */
public class Data extends Observable {

	/**
	 * the value which is observed
	 */
	private long value = 50;

	/**
	 * @return the value
	 */
	public long getValue() {
		return value;
	}

	/**
	 * Sets the value and notifies the Observers (the UI)
	 * of the change
	 * @param l the value to set
	 */
	public void setValue(long l) {
		this.value = l;
		this.setChanged( );
		this.notifyObservers( );
	}
}
