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
 * ProfileGenerator.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   Rabea Gransberger [rgransberger AT web.de]
 *
 * Changes
 * -------
 * 30.04.2006:		Created by Matthias Gernand.
 */
package de.xirp.profile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FilenameUtils;

import de.xirp.util.Constants;
import de.xirp.util.I18n;

/**
 * Generates a XML representation of profile, robot and comm-spec
 * Java beans. The generator uses JAXB and annotated Java beans 
 * to create the XML representation.
 * <br><br>
 * To create a <code>*.pro</code> file use the method
 * {@link de.xirp.profile.ProfileGenerator#generatePRO(Profile, File)}.
 * The XML is generated based on the {@link de.xirp.profile.Profile} and
 * saved as <code>conf/profiles/*.pro</code> file.
 * <br><br>
 * To create a <code>*.bot</code> file use the method
 * {@link de.xirp.profile.ProfileGenerator#generateBOT(Robot, File)}.
 * The XML is generated based on the {@link de.xirp.profile.Robot} and
 * saved as <code>conf/profiles/robots/*.bot</code> file.
 * <br><br>
 * To create a <code>*.cms</code> file use the method
 * {@link de.xirp.profile.ProfileGenerator#generateCMS(CommunicationSpecification, File)}.
 * The XML is generated based on the {@link de.xirp.profile.CommunicationSpecification} and
 * saved as <code>conf/profiles/commspecs/*.cms</code> file.
 * 
 * @author Matthias Gernand
 * @author Rabea Gransberger
 * 
 * @see de.xirp.profile.Profile
 * @see de.xirp.profile.Robot
 * @see de.xirp.profile.CommunicationSpecification
 */
public final class ProfileGenerator {

	/**
	 * The default constructor is declared private because only
	 * the statically declared methods should be used.
	 */
	private ProfileGenerator() {

	}

	/**
	 * Generates a PRO file with the given path from the given 
	 * {@link de.xirp.profile.Profile profile} bean.
	 * 
	 * @param profile
	 * 			The profile to generate the XML for.        
	 * @param proFile
	 * 			The file to write the result to. Must be the full path.
	 *            
	 * @throws JAXBException if the given profile was null, or something 
	 * 						 went wrong generating the xml.
	 * @throws FileNotFoundException if something went wrong generating the xml.
	 * 
	 * @see de.xirp.profile.Profile
	 */
	public static void generatePRO(Profile profile, File proFile)
			throws JAXBException, FileNotFoundException {

		if (profile == null) {
			throw new JAXBException(I18n.getString("ProfileGenerator.exception.profileNull")); //$NON-NLS-1$
		}

		String fileName = FilenameUtils.getBaseName(proFile.getName( ));

		JAXBContext jc = JAXBContext.newInstance(Profile.class);
		Marshaller m = jc.createMarshaller( );
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(profile, new FileOutputStream(Constants.CONF_PROFILES_DIR
				+ File.separator + fileName + Constants.PROFILE_POSTFIX));
	}

	/**
	 * Generates a BOT file with the given path from the given 
	 * {@link de.xirp.profile.Robot robot} bean.
	 * 
	 * @param robot
	 * 			The robot to generate the XML for. 
	 * @param botFile
	 * 			The file to write the result to. Must be the full path.
	 * 
	 * @throws JAXBException if the given robot was null, or something 
	 * 						 went wrong generating the xml.
	 * @throws FileNotFoundException if something went wrong generating the xml.
	 */
	public static void generateBOT(Robot robot, File botFile)
			throws FileNotFoundException, JAXBException {

		if (robot == null) {
			throw new JAXBException(I18n.getString("ProfileGenerator.exception.robotNull")); //$NON-NLS-1$
		}

		String fileName = FilenameUtils.getBaseName(botFile.getName( ));

		JAXBContext jc = JAXBContext.newInstance(Robot.class);
		Marshaller m = jc.createMarshaller( );
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(robot, new FileOutputStream(Constants.CONF_ROBOTS_DIR
				+ File.separator + fileName + Constants.ROBOT_POSTFIX));
	}

	/**
	 * Generates a BOT file with the given path from the given 
	 * {@link de.xirp.profile.CommunicationSpecification comm-spec} 
	 * bean.
	 * 
	 * @param commSpec
	 * 			The comm-spec to generate the XML for. 
	 * @param cmsFile
	 * 			The file to write the result to. Must be the full path.
	 * 
	 * @throws JAXBException if the given comm-spec was null, or something 
	 * 						 went wrong generating the xml.
	 * @throws FileNotFoundException if something went wrong generating the xml.
	 */
	public static void generateCMS(CommunicationSpecification commSpec,
			File cmsFile) throws JAXBException, FileNotFoundException {

		if (commSpec == null) {
			throw new JAXBException(I18n.getString("ProfileGenerator.exception.comSpecNull")); //$NON-NLS-1$
		}

		String fileName = FilenameUtils.getBaseName(cmsFile.getName( ));

		JAXBContext jc = JAXBContext.newInstance(CommunicationSpecification.class);
		Marshaller m = jc.createMarshaller( );
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(commSpec, new FileOutputStream(Constants.CONF_COMMSPECS_DIR
				+ File.separator + fileName + Constants.COMM_SPEC_POSTFIX));
	}
}
