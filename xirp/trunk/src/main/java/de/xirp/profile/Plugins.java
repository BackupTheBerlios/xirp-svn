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
 * Plugins.java
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
import java.util.List;
import java.util.Vector;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents a container for the 
 * {@link de.xirp.profile.Plugin plugins} of the robot.
 * <br><br>
 * This class is annotated with annotations for mapping Java beans to
 * XML. Xirp uses JAXB to map the profile/robot/comm-spec XML files 
 * to the corresponding Java beans and vice versa.
 * 
 * @author Matthias Gernand
 * 
 * @see javax.xml.bind.annotation
 * @see de.xirp.profile.Plugin
 */
@XmlType
public final class Plugins implements Serializable {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	@XmlTransient
	private static final long serialVersionUID = 4622214997872799019L;
	/**
	 * The {@link de.xirp.profile.Plugin plugins} of the robot.
	 */
	@XmlElement(name = "plugin", required = true)//$NON-NLS-1$
	private Vector<Plugin> plugins = new Vector<Plugin>( );

	/**
	 * Returns an unmodifiable list with the 
	 * {@link de.xirp.profile.Plugin plugins}.
	 * 
	 * @return The plugins.
	 * 
	 * @see de.xirp.profile.Plugin
	 */
	@XmlTransient
	public List<Plugin> getPlugins() {
		return Collections.unmodifiableList(plugins);
	}

	/**
	 * Adds a {@link de.xirp.profile.Plugin} to the list 
	 * of plugins.
	 * 
	 * @param plugin
	 *            The plugin to add.
	 */
	public void addPlugin(Plugin plugin) {
		this.plugins.add(plugin);
	}

}
