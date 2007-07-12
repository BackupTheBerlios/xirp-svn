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
 * ExternalTools.java
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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents a container for 
 * {@link de.xirp.profile.Tool tools}. It just holds
 * a {@link java.util.Vector} of {@link de.xirp.profile.Tool}.
 * <br><br>
 * This class is annotated with annotations for mapping Java beans to
 * XML. Xirp uses JAXB to map the profile/robot/comm-spec XML files 
 * to the corresponding Java beans and vice versa.
 * 
 * @author Matthias Gernand
 * 
 * @see javax.xml.bind.annotation
 * @see de.xirp.profile.Tool
 */
@XmlType
public class ExternalTools implements Serializable {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	@XmlTransient
	private static final long serialVersionUID = -6349504588074528182L;
	/**
	 * The available {@link de.xirp.profile.Tool tools}.
	 */
	@XmlElement(name = "tool", required = true)//$NON-NLS-1$
	private Vector<Tool> tools = new Vector<Tool>( );

	/**
	 * Returns the available {@link de.xirp.profile.Tool tools}.
	 * 
	 * @return The tools.
	 * 
	 * @see de.xirp.profile.Tool
	 */
	@XmlTransient
	public Vector<Tool> getTools() {
		return tools;
	}

	/**
	 * Adds a {@link de.xirp.profile.Tool} to the
	 * available tools.
	 * 
	 * @param tool 
	 * 			The tool to add.
	 */
	public void addTool(Tool tool) {
		this.tools.add(tool);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((tools == null) ? 0 : tools.hashCode( ));
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass( ) != obj.getClass( ))
			return false;
		final ExternalTools other = (ExternalTools) obj;
		if (tools == null) {
			if (other.tools != null)
				return false;
		}
		else if (!tools.equals(other.tools))
			return false;
		return true;
	}

}
