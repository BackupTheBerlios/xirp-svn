package de.xirp.test;

/**
 * @author Rabea
 * 
 */
public class Triple {

	double x = -1;
	double y = -1;
	double z = -1;

	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public Triple(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public String toString() {
		return x + ", " + y + ", " + z; //$NON-NLS-1$ //$NON-NLS-2$
	}

}
