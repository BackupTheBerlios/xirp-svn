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
 * Profile.java
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
import java.util.Vector;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * This class represents a profile. It is used by the
 * {@link de.xirp.profile.ProfileParser} and the 
 * {@link de.xirp.profile.ProfileManager}. It should <br>not</b>
 * be altered directly. Use the profile editor to apply changes.
 * <br><br>
 * A profile can contain optional informations about external
 * {@link de.xirp.profile.ExternalTools tools}.
 * They can be started in order to give the user more flexibility in
 * his work with f.e. robot simulators which may have several different
 * executables to start in order.
 * <br><br>
 * The profile mainly contains the associated 
 * {@link de.xirp.profile.Robot} objects. This objects contain all
 * information about the robot. The profile only declares that this profiles
 * uses the associated {@link de.xirp.profile.Robot robots}.
 * <br><br>
 * This class is annotated with annotations for mapping Java beans to
 * XML. Xirp uses JAXB to map the profile/robot/comm-spec XML files 
 * to the corresponding Java beans and vice versa.
 * 
 * @author Matthias Gernand
 * @see javax.xml.bind.annotation
 * @see de.xirp.profile.ProfileParser
 * @see de.xirp.profile.ProfileManager
 * @see de.xirp.profile.Robot
 * @see de.xirp.profile.ExternalTools
 * @see de.xirp.profile.Tool
 * @see de.xirp.profile.Executable
 */
@XmlRootElement(name = "profile")
public final class Profile implements Serializable, Comparable<Profile> {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	@XmlTransient
	private static final long serialVersionUID = -7096373236386700100L;
	/**
	 * The name of the profile.
	 */
	@XmlAttribute(name = "name", required = true)
	private String name;
	/**
	 * Indicates if the profile is completed.
	 */
	@XmlAttribute(name = "complete", required = true)
	private boolean complete;
	/**
	 * The optional available {@link de.xirp.profile.ExternalTools}
	 * of the profile.
	 * 
	 * @see de.xirp.profile.ExternalTools
	 */
	@XmlElement(name = "externaltools", required = false)
	private ExternalTools externalTools = new ExternalTools( );
	/**
	 * The PRO {@link java.io.File} of the profile.
	 */
	@XmlTransient
	private File proFile;
	/**
	 * The associated robot file names of this profile. The file name
	 * is entered without the extension, f.e. if the profile wants to use
	 * the robot specified in the file <code>conf/profiles/robots/testerbot.bot</code>
	 * only <code>testerbot</code> is entered in this list.<
	 */
	@XmlElement(name = "robot", required = true)
	private Vector<String> robotFileNames = new Vector<String>( );
	/**
	 * The associated {@link de.xirp.profile.Robot} objects of
	 * this profile.
	 * 
	 * @see de.xirp.profile.Robot
	 */
	private Vector<Robot> robots = new Vector<Robot>( );

	/**
	 * Returns the name of the profile.
	 * 
	 * @return The name.
	 */
	@XmlTransient
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the profile.
	 * 
	 * @param name 
	 * 			The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the associated {@link de.xirp.profile.Robot robots} 
	 * of this profile.
	 * 
	 * @return The robots.
	 * 
	 * @see de.xirp.profile.Robot
	 */
	@XmlTransient
	public Vector<Robot> getRobots() {
		return robots;
	}

	/**
	 * Adds a {@link de.xirp.profile.Robot} to the profile.
	 * 
	 * @param robot
	 *            The robot to add.
	 *            
	 * @see de.xirp.profile.Robot
	 */
	public void addRobot(Robot robot) {
		this.robots.add(robot);
	}

	/**
	 * Indicates if the profile is completed or not.
	 * 
	 * @return A <code>boolean</code>.<br>
	 * 			<code>true</code>: the profile is competed.<br>
	 * 			<code>false</code>: the profile is not competed.<br>
	 */
	@XmlTransient
	public boolean isComplete() {
		return complete;
	}

	/**
	 * Sets the completed status of the profile.
	 * 
	 * @param complete <br>
	 * 			The complete status to set.<br>
	 * 			<code>true</code>: the profile is competed.<br>
	 * 			<code>false</code>: the profile is not competed.<br>			
	 */
	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	/**
	 * Returns the PRO {@link java.io.File} of the profile.
	 * 
	 * @return The pro file.
	 */
	@XmlTransient
	public File getProFile() {
		return proFile;
	}

	/**
	 * Sets the PRO {@link java.io.File} of the profile.
	 * 
	 * @param proFile 
	 * 			The pro file to set.
	 */
	public void setProFile(File proFile) {
		this.proFile = proFile;
	}

	/**
	 * Compares two profiles. The profiles are compared
	 * by the name of the profile.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Profile p) {
		return getName( ).compareToIgnoreCase(p.getName( ));
	}

	/**
	 * Returns the available {@link de.xirp.profile.ExternalTools}
	 * of the profile.
	 * 
	 * @return The external tools object.
	 * 
	 * @see de.xirp.profile.ExternalTools
	 */
	@XmlTransient
	public ExternalTools getExternalTools() {
		return externalTools;
	}

	/**
	 * Sets the {@link de.xirp.profile.ExternalTools}
	 * of the profile.
	 * 
	 * @param externalTools 
	 * 			The external tools object to set.
	 * 
	 * @see de.xirp.profile.ExternalTools
	 */
	public void setExternalTools(ExternalTools externalTools) {
		this.externalTools = externalTools;
	}

	/**
	 * Returns the file names of the associated 
	 * {@link de.xirp.profile.Robot robots}.
	 * 
	 * @return The robot file names.
	 * 
	 * @see de.xirp.profile.Robot
	 */
	@XmlTransient
	public Vector<String> getRobotFileNames() {
		return robotFileNames;
	}

	/**
	 * Adds a file name to the list of already associated 
	 * {@link de.xirp.profile.Robot robots}.
	 * 
	 * @param robotFileName 
	 * 			The robot file name to add.
	 * 
	 * @see de.xirp.profile.Robot
	 */
	public void addRobotFileName(String robotFileName) {
		this.robotFileNames.add(robotFileName);
	}
}
