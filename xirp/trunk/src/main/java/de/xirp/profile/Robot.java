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
 * Robot.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 15.02.2006:		Created by Matthias Gernand.
 * 15.02.2007:		Added annotations for parsing with JAXB.
 */
package de.xirp.profile;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * This class represents a
 * {@link de.xirp.profile.Robot robot} in a
 * {@link de.xirp.profile.Profile profile}. <br>
 * <br>
 * This class is annotated with annotations for mapping Java beans to
 * XML. Xirp uses JAXB to map the profile/robot/comm-spec XML files to
 * the corresponding Java beans and vice versa.
 * 
 * @author Matthias Gernand
 * @see javax.xml.bind.annotation
 * @see de.xirp.profile.Profile
 */
@XmlRootElement(name = "robot")
public final class Robot implements Serializable, Comparable<Robot> {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	@XmlTransient
	private static final long serialVersionUID = 4244141572602886348L;
	/**
	 * Constant indicating a "no robot" state.
	 */
	public static final String NAME_NONE = "NoRobot"; //$NON-NLS-1$
	/**
	 * Constant indicating a "all robots" state.
	 */
	public static final String NAME_ALL = "AllRobots"; //$NON-NLS-1$
	/**
	 * The name of the robot.
	 */
	@XmlAttribute(name = "name", required = true)
	private String name = ""; //$NON-NLS-1$
	/**
	 * The completed state of the robot.
	 */
	@XmlAttribute(name = "complete", required = true)
	private boolean complete = false;
	/**
	 * The type of the robot.
	 */
	@XmlAttribute(name = "type", required = true)
	private RobotType type = null;
	/**
	 * The robots
	 * {@link de.xirp.profile.RobotSpecs specification}.
	 * 
	 * @see de.xirp.profile.RobotSpecs
	 */
	@XmlElement(name = "robotspecs", required = true)
	private RobotSpecs specs = null;
	/**
	 * The robots
	 * {@link de.xirp.profile.Actuators actuators}.
	 * 
	 * @see de.xirp.profile.Actuators
	 */
	@XmlElement(name = "actuators", required = true)
	private Actuators actuators = null;
	/**
	 * The robots
	 * {@link de.xirp.profile.PowerSource power source(s)}.
	 * 
	 * @see de.xirp.profile.PowerSource
	 */
	@XmlElement(name = "powersource", required = true)
	private Vector<PowerSource> powersources = new Vector<PowerSource>( );
	/**
	 * The
	 * {@link de.xirp.profile.Sensorgroup sensor groups}
	 * of the robot.
	 * 
	 * @see de.xirp.profile.Sensorgroup
	 */
	@XmlElement(name = "sensorgroup", required = false)
	private Vector<Sensorgroup> sensorgroups = new Vector<Sensorgroup>( );
	/**
	 * Name of the communication specification file.
	 */
	@XmlElement(name = "communicationspecification", required = true)
	private String commSpecFileName = ""; //$NON-NLS-1$
	/**
	 * The {@link de.xirp.profile.Multimedia multimedia}
	 * devices of the robot.
	 * 
	 * @see de.xirp.profile.Multimedia
	 */
	@XmlElement(name = "multimedia", required = false)
	private Multimedia multimedia = null;
	/**
	 * The {@link de.xirp.profile.Plugins plugins} of the
	 * robot.
	 * 
	 * @see de.xirp.profile.Plugins
	 */
	@XmlElement(name = "plugins", required = false)
	private Plugins plugins = null;
	/**
	 * The
	 * {@link de.xirp.profile.CommunicationSpecification comm-spec}
	 * of the robot.
	 * 
	 * @see de.xirp.profile.CommunicationSpecification
	 */
	private CommunicationSpecification communicationSpecification = null;
	/**
	 * The robots BOT {@link java.io.File}.
	 */
	@XmlTransient
	private File botFile;

	/**
	 * Enumeration for indication the type of the robot. There are
	 * four constants for the robot type available: <br>
	 * <br>
	 * <ul>
	 * <li>WHEEL</li>
	 * <li>WALK</li>
	 * <li>FLY</li>
	 * <li>OTHER</li>
	 * </ul>
	 * 
	 * @author Matthias Gernand
	 */
	@XmlEnum(String.class)
	public static enum RobotType {
		/**
		 * Indicates that robot has wheels.
		 */
		WHEEL,
		/**
		 * Indicates that robot has legs.
		 */
		WALK,
		/**
		 * Indicates that the robot can fly.
		 */
		FLY,
		/**
		 * Indicates that robot does not fly, walk or roll.
		 */
		OTHER;
	}

	/**
	 * Returns the name of the robot.
	 * 
	 * @return Returns the name.
	 */
	@XmlTransient
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the robot.
	 * 
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the
	 * {@link de.xirp.profile.Sensorgroup sensor groups}
	 * of the robot.
	 * 
	 * @return The sensor groups of the robot.
	 * @see de.xirp.profile.Sensorgroup
	 */
	@XmlTransient
	public List<Sensorgroup> getSensorgroups() {
		return Collections.unmodifiableList(sensorgroups);
	}

	/**
	 * Add the given {@link de.xirp.profile.Sensorgroup}
	 * to this robot.
	 * 
	 * @param sensorgroup
	 *            The group to add.
	 * @see de.xirp.profile.Sensorgroup
	 */
	public void addSensorgroup(Sensorgroup sensorgroup) {
		this.sensorgroups.add(sensorgroup);
	}

	/**
	 * Add the
	 * {@link de.xirp.profile.PowerSource power source}
	 * to this robot.
	 * 
	 * @param powersource
	 *            The power source to add.
	 * @see de.xirp.profile.PowerSource
	 */
	public void addPowerSource(PowerSource powersource) {
		this.powersources.add(powersource);
	}

	/**
	 * Returns the
	 * {@link de.xirp.profile.Robot.RobotType type} of
	 * the robot.
	 * 
	 * @return Returns the type.
	 * @see de.xirp.profile.Robot.RobotType
	 */
	@XmlTransient
	public RobotType getType() {
		return type;
	}

	/**
	 * Sets the
	 * {@link de.xirp.profile.Robot.RobotType type} of
	 * the robot.
	 * 
	 * @param type
	 *            The type to set.
	 * @see de.xirp.profile.Robot.RobotType
	 */
	public void setType(RobotType type) {
		this.type = type;
	}

	/**
	 * Returns the
	 * {@link de.xirp.profile.Multimedia multimedia}
	 * devices of the robot.
	 * 
	 * @return The multimedia devices.
	 * @see de.xirp.profile.Multimedia
	 */
	@XmlTransient
	public Multimedia getMultimedia() {
		return multimedia;
	}

	/**
	 * Sets the
	 * {@link de.xirp.profile.Multimedia multimedia}
	 * devices of the robot.
	 * 
	 * @param multimedia
	 *            The multimedia devices to set.
	 * @see de.xirp.profile.Multimedia
	 */
	public void setMultimedia(Multimedia multimedia) {
		this.multimedia = multimedia;
	}

	/**
	 * Returns the
	 * {@link de.xirp.profile.CommunicationSpecification comm-spec}
	 * of the robot.
	 * 
	 * @return The comm-spec.
	 * @see de.xirp.profile.CommunicationSpecification
	 */
	@XmlTransient
	public CommunicationSpecification getCommunicationSpecification() {
		return communicationSpecification;
	}

	/**
	 * Sets the
	 * {@link de.xirp.profile.CommunicationSpecification comm-spec}
	 * of the robot.
	 * 
	 * @param communicationSpecification
	 *            The comm-spec to set.
	 * @see de.xirp.profile.CommunicationSpecification
	 */
	public void setCommunicationSpecification(
			CommunicationSpecification communicationSpecification) {
		this.communicationSpecification = communicationSpecification;
	}

	/**
	 * Returns the
	 * {@link de.xirp.profile.Plugins plugins} of the
	 * robot.
	 * 
	 * @return The plugins.
	 * @see de.xirp.profile.Plugins
	 */
	@XmlTransient
	public Plugins getPlugins() {
		return plugins;
	}

	/**
	 * Sets the {@link de.xirp.profile.Plugins plugins}
	 * of the robot.
	 * 
	 * @param plugins
	 *            The plugins to set.
	 * @see de.xirp.profile.Plugins
	 */
	public void setPlugins(Plugins plugins) {
		this.plugins = plugins;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "robot {" + getName( ) + "," + getType( ) + "}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Returns the {@link de.xirp.profile.RobotSpecs} of
	 * the robot.
	 * 
	 * @see de.xirp.profile.RobotSpecs
	 * @return The robot specs.
	 */
	@XmlTransient
	public RobotSpecs getRobotSpecs() {
		return specs;
	}

	/**
	 * Sets the {@link de.xirp.profile.RobotSpecs} of the
	 * robot.
	 * 
	 * @see de.xirp.profile.RobotSpecs
	 * @param specs
	 *            The robot specs to set.
	 */
	public void setRobotSpecs(RobotSpecs specs) {
		this.specs = specs;
	}

	/**
	 * Returns the robots
	 * {@link de.xirp.profile.Actuators actuators}.
	 * 
	 * @see de.xirp.profile.Actuators
	 * @return The robots actuators.
	 */
	@XmlTransient
	public Actuators getActuators() {
		return actuators;
	}

	/**
	 * Sets the robots
	 * {@link de.xirp.profile.Actuators actuators}.
	 * 
	 * @see de.xirp.profile.Actuators
	 * @param actuators
	 *            The actuators to set.
	 */
	public void setActuators(Actuators actuators) {
		this.actuators = actuators;
	}

	/**
	 * Returns the robots
	 * {@link de.xirp.profile.PowerSource power sources}.
	 * 
	 * @see de.xirp.profile.PowerSource
	 * @return The robots power sources.
	 */
	@XmlTransient
	public List<PowerSource> getPowerSources() {
		return Collections.unmodifiableList(powersources);
	}

	/**
	 * Returns the file name of the associated comm spec file.
	 * 
	 * @return The comm-spec file name.
	 */
	@XmlTransient
	public String getCommSpecFileName() {
		return commSpecFileName;
	}

	/**
	 * Sets the file name of the associated comm spec file.
	 * 
	 * @param commSpecFileName
	 *            The comm-spec file name to set.
	 */
	public void setCommSpecFileName(String commSpecFileName) {
		this.commSpecFileName = commSpecFileName;
	}

	/**
	 * Returns the {@link de.xirp.profile.Plugin plugins}
	 * for the specified plugin main class associated with this robot.
	 * 
	 * @param pluginMainClass
	 *            The main class of the plugin.
	 * @return The list of plugins or <code>null</code> if no plugin
	 *         was found.
	 * @see de.xirp.profile.Plugin
	 */
	public List<Plugin> getPlugins(String pluginMainClass) {
		List<Plugin> lst = new ArrayList<Plugin>( );
		for (Plugin plugin : getPlugins( ).getPlugins( )) {
			if (plugin.getClassName( ).equalsIgnoreCase(pluginMainClass)) {
				lst.add(plugin);
			}
		}
		return lst;
	}

	/**
	 * Returns a {@link de.xirp.profile.Sensorgroup} for
	 * the given long name (ignoring the case).
	 * 
	 * @param longName
	 *            The long name of the sensor group.
	 * @return The sensor group or <code>null</code> if no group was
	 *         found for the given name.
	 * @see de.xirp.profile.Sensorgroup
	 */
	public Sensorgroup getSensorgroup(String longName) {
		for (Sensorgroup sensor : getSensorgroups( )) {
			if (sensor.getLongName( ).equalsIgnoreCase(longName)) {
				return sensor;
			}
		}
		return null;
	}

	/**
	 * Returns all
	 * {@link de.xirp.profile.Sensorgroup sensor groups}
	 * for the given plugin main class and identifier.
	 * 
	 * @param pluginMainClass
	 *            The main class of the plugin.
	 * @param identifier
	 *            The identifier of the plugin.
	 * @return A unmodifiable list with the sensor groups.
	 * @see de.xirp.profile.Sensorgroup
	 */
	public List<Sensorgroup> getSensorgroups(String pluginMainClass,
			String identifier) {
		List<Plugin> lst = getPlugins(pluginMainClass);
		List<Sensorgroup> displays = new ArrayList<Sensorgroup>( );
		for (Plugin plugin : lst) {
			String id = plugin.getUniqueIdentifier( );
			if (id == identifier || (id != null && id.equals(identifier))) {
				List<String> sensornames = plugin.getSensornames( );
				for (Sensorgroup sensor : getSensorgroups( )) {
					if (sensornames.contains(sensor.getLongName( ))) {
						displays.add(sensor);
					}
				}
			}
		}
		return Collections.unmodifiableList(displays);
	}

	/**
	 * Returns all
	 * {@link de.xirp.profile.Sensorgroup sensor groups}
	 * for the given plugin main class.
	 * 
	 * @param pluginMainClass
	 *            The main class of the plugin.
	 * @return An unmodifiable list with the sensor groups
	 * @see de.xirp.profile.Sensorgroup
	 */
	public List<Sensorgroup> getSensorgroups(String pluginMainClass) {
		List<Plugin> lst = getPlugins(pluginMainClass);
		List<Sensorgroup> displays = new ArrayList<Sensorgroup>( );
		for (Plugin plugin : lst) {
			List<String> sensornames = plugin.getSensornames( );
			for (Sensorgroup sensor : getSensorgroups( )) {
				if (sensornames.contains(sensor.getLongName( ))) {
					displays.add(sensor);
				}
			}
		}
		return Collections.unmodifiableList(displays);
	}

	/**
	 * Returns the list of
	 * {@link de.xirp.profile.ActuatorGroup actuator groups}
	 * of the robot.
	 * 
	 * @return The actuator groups of the robot.
	 */
	public List<ActuatorGroup> getActuatorGroups() {
		return getActuators( ).getActuatorGroups( );
	}

	/**
	 * Returns all {@link de.xirp.profile.Plugin plugins}
	 * of the robot.
	 * 
	 * @return All plugins for the robot name.
	 */
	public List<Plugin> getAllPlugins() {
		return getPlugins( ).getPlugins( );
	}

	/**
	 * Compares to robots. The robots are compared by their name.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Robot r) {
		return getName( ).compareToIgnoreCase(r.getName( ));
	}

	/**
	 * Returns the completed status of the robot.
	 * 
	 * @return A <code>boolean</code><br>
	 *         <code>true</code>: the robot is complete.<br>
	 *         <code>false</code>: the robot is not complete.<br>
	 */
	@XmlTransient
	public boolean isComplete() {
		return complete;
	}

	/**
	 * Sets the completed status of the robot.
	 * 
	 * @param complete
	 *            <code>true</code>: the robot is complete.<br>
	 *            <code>false</code>: the robot is not complete.<br>
	 */
	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	/**
	 * Returns the BOT {@link java.util.File}.
	 * 
	 * @return The bot file.
	 */
	@XmlTransient
	public File getBotFile() {
		return botFile;
	}

	/**
	 * Sets the BOT {@link java.util.File}.
	 * 
	 * @param botFile
	 *            tThe bot file to set.
	 */
	public void setBotFile(File botFile) {
		this.botFile = botFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((name == null) ? 0 : name.hashCode( ));
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass( ) != obj.getClass( )) {
			return false;
		}
		final Robot other = (Robot) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		}
		else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}
}
