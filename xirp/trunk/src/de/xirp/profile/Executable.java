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
 * Executable.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 13.04.2007:		Created by Matthias Gernand.
 */
package de.xirp.profile;

import java.io.File;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * This class represents an executable program. Many of this classes
 * together are contained in a
 * {@link de.xirp.profile.Tool}. this class contains
 * informations about executable file name, path, arguments for this
 * executable and a wait time after the execution of the program. An
 * instance of this class can be passed to the
 * {@link de.xirp.managers.ExternalProgram} class. This
 * object can be passed to
 * {@link de.xirp.managers.ExternalProgramManager#start(de.xirp.managers.ExternalProgram)}.
 * this method then invokes the external program defined in the
 * {@link de.xirp.profile.Executable} class. <br>
 * <br>
 * This class is annotated with annotations for mapping Java beans to
 * XML. Xirp uses JAXB to map the profile/robot/comm-spec XML files to
 * the corresponding Java beans and vice versa.
 * 
 * @author Matthias Gernand
 * @see javax.xml.bind.annotation
 * @see de.xirp.profile.Tool
 * @see de.xirp.managers.ExternalProgramManager
 * @see de.xirp.managers.ExternalProgram
 */
@XmlType
public class Executable implements Serializable, Cloneable {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	@XmlTransient
	private static final long serialVersionUID = 6532182408302109507L;
	/**
	 * The arguments of the program as string.
	 */
	@XmlElement(name = "args", required = false)
	private String arguments = ""; //$NON-NLS-1$
	/**
	 * The name of the {@link de.xirp.profile.Executable}.
	 * this id <b>not</b> the file name. It is a describing name.
	 */
	@XmlAttribute(name = "name", required = true)
	private String name = ""; //$NON-NLS-1$
	/**
	 * The path to the executable in the file system.
	 */
	@XmlAttribute(name = "path", required = true)
	private String path = ""; //$NON-NLS-1$
	/**
	 * The time to wait after the invocation of the program in
	 * milliseconds.
	 */
	@XmlAttribute(name = "wait", required = true)
	private long waitTime = 0;

	/**
	 * Returns the arguments string.
	 * 
	 * @return The arguments.
	 */
	@XmlTransient
	public String getArguments() {
		return arguments;
	}

	/**
	 * Sets the arguments string.
	 * 
	 * @param arguments
	 *            The arguments to set.
	 */
	public void setArguments(String arguments) {
		this.arguments = arguments;
	}

	/**
	 * Returns the describing name.
	 * 
	 * @return The name.
	 */
	@XmlTransient
	public String getName() {
		return name;
	}

	/**
	 * Sets the describing name.
	 * 
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the complete path to the executable file.
	 * 
	 * @return The path to the executable.
	 */
	@XmlTransient
	public String getPath() {
		return path;
	}

	/**
	 * Sets the path to the executable file.
	 * 
	 * @param path
	 *            The path to set.
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Returns the working directory where the executable is located
	 * in.
	 * 
	 * @return The working directory.
	 */
	@XmlTransient
	public String getWorkingDirectory() {
		File f = new File(path);
		return f.getParent( );
	}

	/**
	 * Returns the executable's file name.
	 * 
	 * @return The file name.
	 */
	@XmlTransient
	public String getExecutableName() {
		File f = new File(path);
		return f.getName( );
	}

	/**
	 * Returns the wait time in milliseconds.
	 * 
	 * @return The wait time.
	 */
	@XmlTransient
	public long getWaitTime() {
		return waitTime;
	}

	/**
	 * Sets the wait time in milliseconds. The set milliseconds are
	 * waited until the next
	 * {@link de.xirp.profile.Executable} from a
	 * {@link de.xirp.profile.Tool} is evaluated and
	 * executed.
	 * 
	 * @param waitTime
	 *            The wait time to set.
	 * @see de.xirp.profile.Tool
	 */
	public void setWaitTime(long waitTime) {
		this.waitTime = waitTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Executable clone() {
		Executable e = new Executable( );
		e.setArguments(getArguments( ));
		e.setName(getName( ));
		e.setPath(getPath( ));
		e.setWaitTime(getWaitTime( ));
		return e;
	}

}
