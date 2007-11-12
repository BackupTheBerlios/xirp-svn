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
 * Tool.java
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

import java.io.Serializable;
import java.util.Vector;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents a collection of
 * {@link de.xirp.profile.Executable executable} files.
 * If the tool is invoked the
 * {@link de.xirp.profile.Executable executables} are
 * started in order. This eases f.e. the start up of a robot
 * simulation environment which probably has more many different
 * programs that have to be started. A tool is identified by its name.
 * <br>
 * <br>
 * This class is annotated with annotations for mapping Java beans to
 * XML. Xirp uses JAXB to map the profile/robot/comm-spec XML files to
 * the corresponding Java beans and vice versa.
 * 
 * @author Matthias Gernand
 * @see javax.xml.bind.annotation
 * @see de.xirp.profile.Executable
 * @see de.xirp.profile.ExternalTools
 * @see de.xirp.managers.ExternalProgram
 * @see de.xirp.managers.ExternalProgramManager
 */
@XmlType
public class Tool implements Serializable, Cloneable {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	@XmlTransient
	private static final long serialVersionUID = 98134064944700688L;
	/**
	 * The name of the tool.
	 */
	@XmlAttribute(name = "name", required = true)
	private String name = ""; //$NON-NLS-1$
	/**
	 * The {@link java.util.Vector} of
	 * {@link de.xirp.profile.Executable} available in
	 * this tool.
	 * 
	 * @see de.xirp.profile.Executable
	 * @see de.xirp.profile.ExternalTools
	 * @see de.xirp.managers.ExternalProgram
	 * @see de.xirp.managers.ExternalProgramManager
	 */
	@XmlElement(name = "executable", required = true)
	private Vector<Executable> executables = new Vector<Executable>( );

	/**
	 * Returns the name of the tool.
	 * 
	 * @return The name.
	 */
	@XmlTransient
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the tool.
	 * 
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the available
	 * {@link de.xirp.profile.Executable executables}.
	 * 
	 * @return The executables.
	 */
	@XmlTransient
	public Vector<Executable> getExecutables() {
		return executables;
	}

	/**
	 * Adds a {@link de.xirp.profile.Executable} to the
	 * available
	 * {@link de.xirp.profile.Executable executables}.#
	 * 
	 * @param executable
	 *            The executable to add.
	 */
	public void addExecutable(Executable executable) {
		this.executables.add(executable);
	}

	/**
	 * Returns a deep copy of this object. The
	 * {@link de.xirp.profile.Executable executables}
	 * contained in the {@link java.util.Vector} are also cloned and
	 * added to the tool that is returned by this method.
	 * 
	 * @return A deep copy of this tool.
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Tool clone() {
		Tool tool = new Tool( );
		tool.setName(this.getName( ));
		for (Executable e : getExecutables( )) {
			tool.addExecutable(e.clone( ));
		}
		return tool;
	}

}
