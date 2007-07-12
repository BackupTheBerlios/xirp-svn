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
 * Units.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 01.02.2007:		Created by Matthias Gernand.
 * 15.02.2007:		Added annotations for parsing with JAXB.
 */
package de.xirp.profile;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlTransient;

/**
 * This class holds several enumerations which all provide constants
 * for describing the unit a a value. F.e. A value of 5 is very
 * abstract, but combined with the constant
 * {@link de.xirp.profile.Units.PowerSourceUnit#VOLT} it
 * makes more sense. This constants are used throughout the profiles
 * and robot beans. <br>
 * <br>
 * Every constant in the enumerations of this class represent an
 * option defined in the <code>robot.dtd</code> and
 * <code>robot.xsd</code> files. If an constant is added the
 * constant must be added to the definition files as well. <br>
 * <br>
 * <b>Example</b> for sensors and actuators in <code>DTD</code>:<br>
 * {@literal <!ATTLIST actuator unit (DEGREE|PERCENT|PARTICLES_PER_MILLION|CENTIMETER|KELVIN|CELSIUS|FAHRENHEIT|MILES_PER_HOUR|KILOMETERS_PER_HOUR|METERS_PER_HOUR|METERS_PER_MINUTE|CENTIMETERS_PER_HOUR|CENTIMETERS_PER_MINUTE) #REQUIRED>}
 * {@literal <!ATTLIST sensor unit (DEGREE|PERCENT|PARTICLES_PER_MILLION|CENTIMETER|KELVIN|CELSIUS|FAHRENHEIT|MILES_PER_HOUR|KILOMETERS_PER_HOUR|METERS_PER_HOUR|METERS_PER_MINUTE|CENTIMETERS_PER_HOUR|CENTIMETERS_PER_MINUTE) #REQUIRED>}
 * <br>
 * <br>
 * This class is annotated with annotations for mapping Java beans to
 * XML. Xirp uses JAXB to map the profile/robot/comm-spec XML files to
 * the corresponding Java beans and vice versa.
 * 
 * @author Matthias Gernand
 * @see javax.xml.bind.annotation
 * @see de.xirp.profile.Width
 * @see de.xirp.profile.Height
 * @see de.xirp.profile.Length
 * @see de.xirp.profile.Weight
 * @see de.xirp.profile.Actuator
 * @see de.xirp.profile.Sensor
 * @see de.xirp.profile.PowerSource
 */
public final class Units implements Serializable {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	@XmlTransient
	private static final long serialVersionUID = 4726031172481859752L;

	/**
	 * This enumeration holds constants for the possible units of
	 * power sources. There are five possible units: <br>
	 * <br>
	 * <ul>
	 * <li>VOLT</li>
	 * <li>AMPERE</li>
	 * <li>PERCENT</li>
	 * <li>FARAD</li>
	 * <li>CCM</li>
	 * </ul>
	 * <br>
	 * <br>
	 * These constants are used in the
	 * <code>&lt;powersource&gt;</code> tag in the
	 * <code>*.bot</code> xml files.
	 * 
	 * @author Matthias Gernand
	 */
	@XmlEnum(String.class)
	public enum PowerSourceUnit {
		/**
		 * Unit for voltage.
		 */
		VOLT {

			@Override
			public String unitName() {
				return "V";
			}
		},
		/**
		 * Unit for electric current.
		 */
		AMPERE {

			@Override
			public String unitName() {
				return "A";
			}
		},
		/**
		 * Unit for percentage.
		 */
		PERCENT {

			@Override
			public String unitName() {
				return "%";
			}
		},
		/**
		 * Unit for electric capacity.
		 */
		FARAD {

			@Override
			public String unitName() {
				return "F";
			}
		},
		/**
		 * Unit for volume.
		 */
		CCM {

			@Override
			public String unitName() {
				return "ccm";
			}
		};

		/**
		 * Returns the short for the unit.
		 * 
		 * @return The short.
		 */
		public abstract String unitName();
	}

	/**
	 * This enumeration holds constants for the possible units of
	 * distances. There are five possible units: <br>
	 * <br>
	 * <ul>
	 * <li>MILLIMETER</li>
	 * <li>CENTIMETER</li>
	 * <li>DECIMETER</li>
	 * <li>METER</li>
	 * </ul>
	 * <br>
	 * <br>
	 * These constants are used in the <code>&lt;height&gt;</code>,
	 * <code>&lt;length&gt;</code>, <code>&lt;width&gt;</code>
	 * and <code>&lt;position&gt;</code> tags in the
	 * <code>*.bot</code> xml files.
	 * 
	 * @author Matthias Gernand
	 */
	@XmlEnum(String.class)
	public enum DistanceUnit {
		/**
		 * Unit for millimeters.
		 */
		MILLIMETER {

			@Override
			public String unitName() {
				return "mm";
			}

			@Override
			protected int toMillis() {
				return 1;
			}
		},
		/**
		 * Unit for centimeters.
		 */
		CENTIMETER {

			@Override
			public String unitName() {
				return "cm";
			}

			@Override
			protected int toMillis() {
				return 10;
			}
		},
		/**
		 * Unit for decimeters.
		 */
		DECIMETER {

			@Override
			public String unitName() {
				return "dm";
			}

			@Override
			protected int toMillis() {
				return 10000;
			}
		},
		/**
		 * Unit for meters.
		 */
		METER {

			@Override
			public String unitName() {
				return "m";
			}

			@Override
			protected int toMillis() {
				return 1000;
			}
		};

		/**
		 * Returns the short of the unit.
		 * 
		 * @return The short.
		 */
		public abstract String unitName();

		/**
		 * Factor for multiplication of the unit to get it to
		 * millimeter.
		 * 
		 * @return the multiplicator to get millimeters.
		 */
		protected abstract int toMillis();

		/**
		 * Converts the given value of the given from unit to a value
		 * in the given to unit.
		 * 
		 * @param value
		 *            the value in the from unit.
		 * @param from
		 *            the unit of the given value.
		 * @param to
		 *            the unit for the returned value.
		 * @return the value converted from the given unit to the
		 *         other given unit.
		 */
		public static double convert(double value, DistanceUnit from,
				DistanceUnit to) {

			if (from == to) {
				return value;
			}

			double millimeter = value * from.toMillis( );
			if (to == DistanceUnit.MILLIMETER) {
				return millimeter;
			}
			double dest = millimeter / to.toMillis( );

			return dest;
		}
	}

	/**
	 * This enumeration holds constants for the possible units of
	 * masses. There are three possible units: <br>
	 * <br>
	 * <ul>
	 * <li>GRAM</li>
	 * <li>KILOGRAM</li>
	 * <li>TON</li>
	 * </ul>
	 * <br>
	 * <br>
	 * These constants are used in the <code>&lt;weight&gt;</code>
	 * tag in the <code>*.bot</code> xml files.
	 * 
	 * @author Matthias Gernand
	 */
	@XmlEnum(String.class)
	public enum MassUnit {
		/**
		 * Unit for grams.
		 */
		GRAM {

			@Override
			public String unitName() {
				return "g";
			}

			@Override
			protected int toGram() {
				return 1;
			}
		},
		/**
		 * Unit for kilograms.
		 */
		KILOGRAM {

			@Override
			public String unitName() {
				return "kg";
			}

			@Override
			protected int toGram() {
				return 1000;
			}
		},
		/**
		 * Unit for tons.
		 */
		TON {

			@Override
			public String unitName() {
				return "t";
			}

			@Override
			protected int toGram() {
				return 1000000;
			}
		};

		/**
		 * Returns the short of the unit.
		 * 
		 * @return The short.
		 */
		public abstract String unitName();

		/**
		 * Factor for multiplication of the unit to get it to gram.
		 * 
		 * @return the multiplicator to get grams.
		 */
		protected abstract int toGram();

		/**
		 * Converts the given value of the given from unit to a value
		 * in the given to unit.
		 * 
		 * @param value
		 *            the value in the from unit.
		 * @param from
		 *            the unit of the given value.
		 * @param to
		 *            the unit for the returned value.
		 * @return the value converted from the given unit to the
		 *         other given unit.
		 */
		public static double convert(double value, MassUnit from, MassUnit to) {

			if (from == to) {
				return value;
			}

			double gram = value * from.toGram( );
			if (to == MassUnit.GRAM) {
				return gram;
			}
			double dest = gram / to.toGram( );

			return dest;
		}
	}

	/**
	 * This enumeration holds constants for the possible units of
	 * values of sensors or actuators. There are fourteen possible
	 * units: <br>
	 * <br>
	 * <ul>
	 * <li>RPM</li>
	 * <li>DEGREE</li>
	 * <li>PERCENT</li>
	 * <li>PARTICLES_PER_MILLION</li>
	 * <li>CENTIMETER</li>
	 * <li>KELVIN</li>
	 * <li>CELSIUS</li>
	 * <li>FAHRENHEIT</li>
	 * <li>MILES_PER_HOUR</li>
	 * <li>KILOMETERS_PER_HOUR</li>
	 * <li>METERS_PER_HOUR</li>
	 * <li>METERS_PER_MINUTE</li>
	 * <li>CENTIMETERS_PER_HOUR</li>
	 * <li>CENTIMETERS_PER_MINUTE</li>
	 * </ul>
	 * <br>
	 * <br>
	 * These constants are used in the and <code>&lt;sensor&gt;</code>
	 * tags in the <code>*.bot</code> xml files.
	 * 
	 * @author Matthias Gernand
	 */
	@XmlEnum(String.class)
	public enum SensorValueUnit {
		/**
		 * Unit for rounds per minute. 
		 */
		RPM {
			
			@Override
			public String unitName() {
				return "rpm";
			}
			
		},
		/**
		 * Unit for degrees.
		 */
		DEGREE {

			@Override
			public String unitName() {
				return "\u00b0";
			}
		},
		/**
		 * Unit for percentage.
		 */
		PERCENT {

			@Override
			public String unitName() {
				return "%";
			}
		},
		/**
		 * Unit for particles per million.
		 */
		PARTICLES_PER_MILLION {

			@Override
			public String unitName() {
				return "ppm";
			}
		},
		/**
		 * Unit for centimeters.
		 */
		CENTIMETER {

			@Override
			public String unitName() {
				return "cm";
			}
		},
		/**
		 * Unit for degrees kelvin.
		 */
		KELVIN {

			@Override
			public String unitName() {
				return "\u00b0K";
			}
		},
		/**
		 * Unit for degrees celsius.
		 */
		CELSIUS {

			@Override
			public String unitName() {
				return "\u00b0C";
			}
		},
		/**
		 * Unit for degrees fahrenheit.
		 */
		FAHRENHEIT {

			@Override
			public String unitName() {
				return "\u00b0F";
			}
		},
		/**
		 * Unit for miles per hour.
		 */
		MILES_PER_HOUR {

			@Override
			public String unitName() {
				return "mph";
			}
		},
		/**
		 * Unit for kilometers per hour.
		 */
		KILOMETERS_PER_HOUR {

			@Override
			public String unitName() {
				return "km/h";
			}
		},
		/**
		 * Unit for meters per hour.
		 */
		METERS_PER_HOUR {

			@Override
			public String unitName() {
				return "m/h";
			}
		},
		/**
		 * Unit for meters per minute.
		 */
		METERS_PER_MINUTE {

			@Override
			public String unitName() {
				return "m/min";
			}
		},
		/**
		 * Unit for centimeters per hour.
		 */
		CENTIMETERS_PER_HOUR {

			@Override
			public String unitName() {
				return "cm/h";
			}
		},
		/**
		 * Unit for centimeters per minute.
		 */
		CENTIMETERS_PER_MINUTE {

			@Override
			public String unitName() {
				return "cm/min";
			}
		};

		/**
		 * Returns the short of the unit.
		 * 
		 * @return The short.
		 */
		public abstract String unitName();
	}
	
	/**
	 * This enumeration holds constants for the possible units of
	 * values of sensors or actuators. There are three possible
	 * units: <br>
	 * <br>
	 * <ul>
	 * <li>RPM</li>
	 * <li>DEGREE</li>
	 * <li>PERCENT</li>
	 * </ul>
	 * <br>
	 * <br>
	 * These constants are used in the <code>&lt;actuator&gt;</code>
	 * tag in the <code>*.bot</code> xml files.
	 * 
	 * @author Matthias Gernand
	 */
	@XmlEnum(String.class)
	public enum ActuatorValueUnit {
		/**
		 * Unit for rounds per minute. 
		 */
		RPM {
			
			@Override
			public String unitName() {
				return "rpm";
			}
			
		},
		/**
		 * Unit for degrees.
		 */
		DEGREE {

			@Override
			public String unitName() {
				return "\u00b0";
			}
		},
		/**
		 * Unit for percentage.
		 */
		PERCENT {

			@Override
			public String unitName() {
				return "%";
			}
		};
		
		/**
		 * Returns the short of the unit.
		 * 
		 * @return The short.
		 */
		public abstract String unitName();
	}
}
