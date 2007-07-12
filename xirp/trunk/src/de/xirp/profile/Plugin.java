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
 * Plugin.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 03.06.2006:		Created by Matthias Gernand.
 * 15.02.2007:		Added annotations for parsing with JAXB.
 */
package de.xirp.profile;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

/**
 * This class Represents a plugin of the robot.<br>
 * Each plugin may have one or more associated sensors.<br>
 * Each plugin may set the flag <code>multimedia</code> to
 * <code>true</code> if it uses multimedia devices. Additional
 * {@link de.xirp.profile.Option options} can be given to
 * the plugin. <br>
 * <br>
 * This class is annotated with annotations for mapping Java beans to
 * XML. Xirp uses JAXB to map the profile/robot/comm-spec XML files to
 * the corresponding Java beans and vice versa.
 * 
 * @author Matthias Gernand
 * @see javax.xml.bind.annotation
 * @see de.xirp.profile.Option
 */
@XmlType
public final class Plugin implements Serializable {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	@XmlTransient
	private static final long serialVersionUID = 1201007225939596883L;
	/**
	 * The main class of the plugin.
	 */
	@XmlElement(name = "class", required = true)
	private String clazz = ""; //$NON-NLS-1$
	/**
	 * The names of the sensors associated with this plugin.
	 */
	@XmlElement(name = "sensorname", required = false)
	private List<String> sensornames = new Vector<String>( );
	/**
	 * Uses the plugin the multimedia devices of the robot?
	 */
	@XmlElement(name = "usemultimedia", required = false)
	private boolean multimedia = false;
	/**
	 * The options of the plugin.
	 */
	@XmlElement(name = "option", required = false)
	private List<Option> options = new Vector<Option>( );
	/**
	 * The name of the plugin.
	 */
	@XmlAttribute(name = "name", required = true)
	private String name = ""; //$NON-NLS-1$

	/**
	 * Returns the class name of the plugin.
	 * 
	 * @return The class name.
	 */
	@XmlTransient
	public String getClassName() {
		return StringUtils.trimToEmpty(clazz);
	}

	/**
	 * Sets the class name of the plugin.
	 * 
	 * @param clazz
	 *            The class name to set.
	 */
	public void setClassName(String clazz) {
		this.clazz = StringUtils.trimToEmpty(clazz);
	}

	/**
	 * Returns the name of the plugin.
	 * 
	 * @return The name.
	 */
	@XmlTransient
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the plugin.
	 * 
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Adds an {@link de.xirp.profile.Option} to this
	 * plugins options.
	 * 
	 * @param option
	 *            The option to add.
	 * @see de.xirp.profile.Option
	 */
	public void addOption(Option option) {
		options.add(option);
	}

	/**
	 * Returns the {@link de.xirp.profile.Option options}
	 * of this plugin.
	 * 
	 * @return An unmodifiable list of options.
	 * @see de.xirp.profile.Option
	 */
	@XmlTransient
	public List<Option> getOptions() {
		return Collections.unmodifiableList(options);
	}

	/**
	 * Returns the {@link de.xirp.profile.Option options}
	 * of this plugin as map (option name to option value) for
	 * convenience.
	 * 
	 * @return The options in a map.
	 * @see de.xirp.profile.Option
	 */
	public Map<String, String> getOptionsAsMap() {
		Map<String, String> optionMap = new HashMap<String, String>( );
		for (Option option : options) {
			optionMap.put(option.getName( ), option.getValue( ));
		}
		return Collections.unmodifiableMap(optionMap);
	}

	/**
	 * Adds a sensor name to this plugin.
	 * 
	 * @param name
	 *            The name of the sensor to add.
	 */
	public void addSensorname(String name) {
		sensornames.add(name);
	}

	/**
	 * Gets the names of the associated sensors of this plugin.
	 * 
	 * @return An unmodifiable list with the sensor names.
	 */
	@XmlTransient
	public List<String> getSensornames() {
		return Collections.unmodifiableList(sensornames);
	}

	/**
	 * Determine if the plugin uses the multimedia devices of the
	 * robot.
	 * 
	 * @return <code>true</code> if the plugin likes to use the
	 *         multimedia devices of the robot.
	 */
	@XmlTransient
	public boolean isMultimedia() {
		return multimedia;
	}

	/**
	 * Sets the flag if the robot uses the multimedia devices of the
	 * robot.
	 * 
	 * @param multimedia
	 *            <code>true</code> if the plugin likes to use the
	 *            multimedia devices of the robot.
	 */
	public void setMultimedia(boolean multimedia) {
		this.multimedia = multimedia;
	}

	/**
	 * Returns the unique identifier of this plugin.
	 * 
	 * @return The unique identifier of the plugin.
	 */
	@XmlTransient
	public String getUniqueIdentifier() {
		List<String> sensornames = getSensornames( );
		if (!sensornames.isEmpty( )) {
			StringBuilder builder = new StringBuilder( );
			for (Iterator<String> it = sensornames.iterator( ); it.hasNext( );) {

				builder.append(it.next( ));
				if (it.hasNext( )) {
					builder.append(":"); //$NON-NLS-1$
				}
			}
			return builder.toString( );
		}
		else {
			return null;
		}
	}

}
