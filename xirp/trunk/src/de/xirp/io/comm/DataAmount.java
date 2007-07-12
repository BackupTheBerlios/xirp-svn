/** 
 * ============================================================================
 * Xirp 2: eXtendable interface for robotic purposes.
 * ============================================================================
 * 
 * Copyright (C) 2005-2007, by Authors and Contributors listed in CREDITS.txt
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at:
 *
 * 				http://www.opensource.org/licenses/cpl1.0.php
 *
 * ----------------------------
 * DataAmount.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * 					 Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 02.05.2007:		Created by Matthias Gernand.
 */
package de.xirp.io.comm;

import java.util.Locale;

import de.xirp.util.I18n;

/**
 * This class formats data amounts given in bytes to a readable format
 * like megabytes.<br/><br/>It ranges from bytes to yotta bytes (22<sup>80</sup>
 * bytes) and allows adding of bytes or other data amounts. The data
 * is internally stored as a double with it's unit. A double can hold
 * 16 Yotta Bytes (16 * 2<sup>80</sup> bytes). So there should be
 * enough space.
 * 
 * @author Rabea Gransberger
 * @author Matthias Gernand
 */
public final class DataAmount {

	/**
	 * The units of data amounts
	 */
	protected enum DataAmountUnit {
		/**
		 * A byte = 4 bit
		 */
		BYTES {

			@Override
			public int factor() {
				return 0;
			}

			@Override
			public String unitName() {
				return "b"; //$NON-NLS-1$
			}

			@Override
			public DataAmountUnit next() {
				return KILO_BYTES;
			}
		},
		/**
		 * 1 kb = 2<sup>10</sup> bytes
		 */
		KILO_BYTES {

			@Override
			public int factor() {
				return 10;
			}

			@Override
			public String unitName() {
				return "kb"; //$NON-NLS-1$
			}

			@Override
			public DataAmountUnit next() {
				return MEGA_BYTES;
			}
		},
		/**
		 * 1 Mb = 2<sup>20</sup> bytes
		 */
		MEGA_BYTES {

			@Override
			public int factor() {
				return 20;
			}

			@Override
			public String unitName() {
				return "Mb"; //$NON-NLS-1$
			}

			@Override
			public DataAmountUnit next() {
				return GIGA_BYTES;
			}
		},
		/**
		 * 1 Gb = 2<sup>30</sup> bytes
		 */
		GIGA_BYTES {

			@Override
			public int factor() {
				return 30;
			}

			@Override
			public String unitName() {
				return "Gb"; //$NON-NLS-1$
			}

			@Override
			public DataAmountUnit next() {
				return TERA_BYTES;
			}
		},
		/**
		 * 1 Tb = 2<sup>40</sup> bytes
		 */
		TERA_BYTES {

			@Override
			public int factor() {
				return 40;
			}

			@Override
			public String unitName() {
				return "Tb"; //$NON-NLS-1$
			}

			@Override
			public DataAmountUnit next() {
				return PETA_BYTES;
			}
		},
		/**
		 * 1 Pb = 2<sup>50</sup> bytes
		 */
		PETA_BYTES {

			@Override
			public int factor() {
				return 50;
			}

			@Override
			public String unitName() {
				return "Pb"; //$NON-NLS-1$
			}

			@Override
			public DataAmountUnit next() {
				return EXA_BYTES;
			}
		},
		/**
		 * 1 Eb = 2<sup>60</sup> bytes
		 */
		EXA_BYTES {

			@Override
			public int factor() {
				return 60;
			}

			@Override
			public String unitName() {
				return "Eb"; //$NON-NLS-1$
			}

			@Override
			public DataAmountUnit next() {
				return ZETTA_BYTES;
			}
		},
		/**
		 * 1 Zb = 2<sup>70</sup> bytes
		 */
		ZETTA_BYTES {

			@Override
			public int factor() {
				return 70;
			}

			@Override
			public String unitName() {
				return "Zb"; //$NON-NLS-1$
			}

			@Override
			public DataAmountUnit next() {
				return YOTTA_BYTES;
			}
		},
		/**
		 * 1 Yb = 2<sup>80</sup> bytes
		 */
		YOTTA_BYTES {

			@Override
			public int factor() {
				return 80;
			}

			@Override
			public String unitName() {
				return "Yb"; //$NON-NLS-1$
			}

			@Override
			public DataAmountUnit next() {
				return YOTTA_BYTES;
			}
		};

		/**
		 * Gets the power factor for this dataamount unit
		 * 
		 * @return the factor f for 2^f bytes
		 */
		public abstract int factor();

		/**
		 * The printable name of the unit
		 * 
		 * @return printable name of the unit
		 */
		public abstract String unitName();

		/**
		 * The next higher unit f.e.: Mb -> Gb
		 * 
		 * @return the next unit
		 */
		public abstract DataAmountUnit next();
	}

	/**
	 * The value for this unit
	 */
	private double data;
	/**
	 * The unit for the data
	 */
	private DataAmountUnit unit;

	/**
	 * Constructs a new data amount with zero bytes
	 */
	public DataAmount() {
		this(0);
	}

	/**
	 * Constructs a new data amount with the given number of bytes.<br/><br/>
	 * You may add zero if you wish to perform a conversion to the
	 * best unit for this value.
	 * 
	 * @param bytes
	 *            the number of bytes
	 */
	public DataAmount(double bytes) {
		this.data = bytes;
		this.unit = DataAmountUnit.BYTES;
	}

	/**
	 * Adds the given number of bytes to this data amount and returns
	 * the new data amount.
	 * 
	 * @param amount
	 *            the number of bytes to add
	 * @return the old data amount plus the given number of bytes
	 */
	public DataAmount add(double amount) {
		this.add(new DataAmount(amount));
		return this;
	}

	/**
	 * Adds the given data amount to this data amount and returns the
	 * new data amount. The new data amounts unit is in the best
	 * applicable unit.
	 * 
	 * @param amount
	 *            the data amount to add
	 * @return the old data amount plus the given data amount
	 */
	public DataAmount add(DataAmount amount) {
		this.data += convert(amount != null ? amount : new DataAmount( ),
				this.unit);

		if (this.unit != this.unit.next( )) {
			double pow = 1024;
			while (this.data >= pow) {
				this.unit = this.unit.next( );
				this.data /= pow;
			}
		}
		return this;
	}

	/**
	 * Converts the given data amount to a value in the given unit.
	 * 
	 * @param amount
	 *            the data amount to convert
	 * @param target
	 *            the unit to convert to
	 * @return the converted value
	 */
	private double convert(DataAmount amount, DataAmountUnit target) {
		if (amount.unit == target) {
			return amount.data;
		}
		else {
			int i = amount.unit.factor( ) - target.factor( );
			double pow = Math.pow(2, i);
			return amount.data * pow;
		}
	}

	/**
	 * @return the data
	 */
	protected double getData() {
		return data;
	}

	/**
	 * @return the unit
	 */
	protected DataAmountUnit getUnit() {
		return unit;
	}

	/**
	 * Gives back this data amounts value with its unit in readable
	 * format: value space unit.
	 */
	@Override
	public String toString() {
		String value = I18n.getDefaultDecimalFormat( ).format(data);
		return value + " " + unit.unitName( ); //$NON-NLS-1$
	}

	public static void main(String[] args) {
		I18n.setLocale(Locale.GERMAN);
		DataAmount amount = new DataAmount(1024);
		System.out.println(amount);
		amount.add(0);
		System.out.println(amount);

		amount.add(amount).add(amount);
		System.out.println(amount);
	}
}