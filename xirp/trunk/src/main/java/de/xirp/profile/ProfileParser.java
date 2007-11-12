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
 * ProfileParser.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   Rabea Gransberger [rgransberger AT web.de]
 *
 * Changes
 * -------
 * 15.02.2006:		Created by Matthias Gernand.
 * 15.10.2006:		Switched parsing to castor.
 * 15.02.2007:		Switched parsing to JAXB.
 */
package de.xirp.profile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import de.xirp.util.Constants;
import de.xirp.util.I18n;
import de.xirp.util.Util;
import de.xirp.util.collections.MultiValueHashMap;

/**
 * This parser reads from XML profile/robot/comm-spec files and
 * constructs the internal data structures using annotated beans. <br>
 * <br>
 * The {@link de.xirp.profile.Profile profile},
 * {@link de.xirp.profile.Robot robot} and
 * {@link de.xirp.profile.CommunicationSpecification comm-spec}
 * files are parsed to a Java bean representation using the JAXB
 * parser. The parser uses the annotations in the Java beans to map
 * the structure of the XML to Java objects.
 * 
 * @author Matthias Gernand
 * @author Rabea Gransberger
 * @see de.xirp.profile.Profile
 * @see de.xirp.profile.Robot
 * @see de.xirp.profile.CommunicationSpecification
 * @see de.xirp.profile.ProfileManager
 * @see javax.xml.bind.annotation
 */
public final class ProfileParser {

	/**
	 * The logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(ProfileParser.class);
	/**
	 * The map of profiles, where every constructed
	 * {@link de.xirp.profile.Profile profile} is added
	 * to.
	 * 
	 * @see de.xirp.profile.Profile
	 */
	private static MultiValueHashMap<String, Profile> profiles = new MultiValueHashMap<String, Profile>( );
	/**
	 * The map of incomplete
	 * {@link de.xirp.profile.Profile profiles}, where
	 * every constructed incomplete profile is added to.
	 * 
	 * @see de.xirp.profile.Profile
	 */
	private static MultiValueHashMap<String, Profile> incompleteProfiles = new MultiValueHashMap<String, Profile>( );

	/**
	 * This constructor is declared private. The parsing process must
	 * be started using the static method
	 * {@link de.xirp.profile.ProfileParser#parse()}.
	 * The constructor initializes the parsing for each profile file
	 * in the profile directory. <br>
	 * <br>
	 * 
	 * @see de.xirp.profile.ProfileParser#parse()
	 * @see de.xirp.profile.ProfileManager
	 * @throws JAXBException
	 *             if something went wrong while parsing.
	 */
	private ProfileParser() throws JAXBException {
		init( );
	}

	/**
	 * Initializes all auxiliary data structures. First the
	 * {@link de.xirp.profile.Profile profile} beans are
	 * parsed. After that, all same named
	 * {@link de.xirp.profile.Profile profiles} obtain a
	 * unique name. The incomplete
	 * {@link de.xirp.profile.Profile profiles} are added
	 * to the corresponding map and the complete
	 * {@link de.xirp.profile.Profile profiles} are also
	 * added to their corresponding map. <br>
	 * <br>
	 * After that step, the
	 * {@link de.xirp.profile.Robot robot} beans are
	 * created by the parser for each
	 * {@link de.xirp.profile.Profile profile}. The
	 * {@link de.xirp.profile.Robot robots} are created
	 * and added to the corresponding {@link java.util.Vector vector}
	 * in the {@link de.xirp.profile.Profile profiles}.
	 * While parsing the
	 * {@link de.xirp.profile.Robot robots} the contained
	 * {@link de.xirp.profile.CommunicationSpecification comm-spec}
	 * beans are parsed and added to the corresponding
	 * {@link java.util.Vector vector} in the
	 * {@link de.xirp.profile.Robot robot}. After all
	 * {@link de.xirp.profile.Robot robots} have been
	 * created, all same named
	 * {@link de.xirp.profile.Robot robots} obtain a
	 * unique name. <br>
	 * <br>
	 * All {@link de.xirp.profile.Profile profiles} and
	 * {@link de.xirp.profile.Robot robots} obtain unique
	 * names to guarantee that
	 * {@link de.xirp.profile.Profile profiles} and
	 * {@link de.xirp.profile.Robot robots} can be
	 * clearly identified. The unique names are achieved by
	 * enumerating the same named objects.
	 * 
	 * @throws JAXBException
	 *             if something went wrong while parsing.
	 * @see de.xirp.profile.Robot
	 * @see de.xirp.profile.Profile
	 * @see de.xirp.profile.CommunicationSpecification
	 */
	private void init() throws JAXBException {
		logClass.info(I18n.getString("ProfileParser.log.parsing")); //$NON-NLS-1$
		File profDir = new File(Constants.CONF_PROFILES_DIR);
		File dtdP = new File(Constants.CONF_PROFILES_DIR + File.separator +
				"profile.dtd"); //$NON-NLS-1$

		File dtdC = new File(Constants.CONF_COMMSPECS_DIR + File.separator +
				"communication.dtd"); //$NON-NLS-1$

		File dtdR = new File(Constants.CONF_ROBOTS_DIR + File.separator +
				"robot.dtd"); //$NON-NLS-1$

		if (!dtdP.exists( )) {
			try {
				throw new FileNotFoundException(I18n.getString("ProfileParser.log.noProfilesDTD", dtdP.getName( ))); //$NON-NLS-1$
			}
			catch (FileNotFoundException e) {
				logClass.info(I18n.getString("ProfileParser.log.failed") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
				logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
						+ Constants.LINE_SEPARATOR, e);
			}
		}
		else if (!dtdC.exists( )) {
			try {
				throw new FileNotFoundException(I18n.getString("ProfileParser.log.noProfilesDTD", dtdC.getName( ))); //$NON-NLS-1$
			}
			catch (FileNotFoundException e) {
				logClass.info(I18n.getString("ProfileParser.log.failed") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
				logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
						+ Constants.LINE_SEPARATOR, e);
			}
		}
		else if (!dtdR.exists( )) {
			try {
				throw new FileNotFoundException(I18n.getString("ProfileParser.log.noProfilesDTD", dtdR.getName( ))); //$NON-NLS-1$
			}
			catch (FileNotFoundException e) {
				logClass.info(I18n.getString("ProfileParser.log.failed") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
				logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
						+ Constants.LINE_SEPARATOR, e);
			}
		}

		File[] files = profDir.listFiles(new FilenameFilter( ) {

			public boolean accept(File dir, String name) {
				return name.endsWith(Constants.PROFILE_POSTFIX);
			}
		});

		if (files.length <= 0) {
			try {
				throw new FileNotFoundException(I18n.getString("ProfileParser.exception.noProfilesFound")); //$NON-NLS-1$
			}
			catch (FileNotFoundException e) {
				logClass.info(I18n.getString("ProfileParser.log.failed") //$NON-NLS-1$
						+
						Constants.LINE_SEPARATOR +
						e.getMessage( ) +
						Constants.LINE_SEPARATOR);
				logClass.info(I18n.getString("ProfileParser.log.noProfilesLoaded") //$NON-NLS-1$
						+
						Constants.LINE_SEPARATOR);
			}
		}

		MultiValueHashMap<String, Profile> profileNames = new MultiValueHashMap<String, Profile>( );

		Profile profile;
		for (File xmlFile : files) {
			profile = parseProfile(xmlFile);
			profile.setProFile(xmlFile);
			profileNames.put(profile.getName( ), profile);
		}

		List<Profile> prfls = new ArrayList<Profile>( );
		for (Entry<String, List<Profile>> e : profileNames.entrySet( )) {
			List<Profile> list = e.getValue( );
			if (list.size( ) > 1) {
				for (int i = 0; i < list.size( ); i++) {
					Profile aux = list.get(i);
					aux.setName(aux.getName( ) + " #" + (i + 1)); //$NON-NLS-1$
					prfls.add(aux);
				}
			}
			else {
				prfls.add(list.get(0));
			}
		}

		MultiValueHashMap<String, Robot> robotNames = new MultiValueHashMap<String, Robot>( );

		for (Profile p : prfls) {
			for (Robot r : p.getRobots( )) {
				robotNames.put(r.getName( ), r);
			}
		}

		for (Entry<String, List<Robot>> e : robotNames.entrySet( )) {
			List<Robot> list = e.getValue( );
			if (list.size( ) > 1) {
				for (int i = 0; i < list.size( ); i++) {
					Robot aux = list.get(i);
					aux.setName(aux.getName( ) + " #" + (i + 1)); //$NON-NLS-1$
				}
			}
		}

		for (Profile p : prfls) {
			if (p.isComplete( )) {
				profiles.put(p.getName( ), p);
			}
			else {
				incompleteProfiles.put(p.getName( ), p);
			}
		}
	}

	/**
	 * The parser is created and started by this method.
	 */
	protected static void parse() {
		try {
			new ProfileParser( );
		}
		catch (Exception e) {
			logClass.info(I18n.getString("ProfileParser.log.failed") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
			logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
					+ Constants.LINE_SEPARATOR, e);
			logClass.info(I18n.getString("ProfileParser.log.terminatedAbnormal") + Constants.LINE_SEPARATOR); //$NON-NLS-1$ 
		}
	}

	/**
	 * Returns the complete
	 * {@link de.xirp.profile.Profile profile} map.
	 * 
	 * @return The available complete profiles.
	 * @see de.xirp.profile.Profile
	 */
	protected static MultiValueHashMap<String, Profile> getProfiles() {
		return profiles;
	}

	/**
	 * Returns {@link de.xirp.profile.Profile profile}
	 * names.
	 * 
	 * @return The profile names.
	 * @see de.xirp.profile.Profile
	 */
	protected static Set<String> getProfileNames() {
		return profiles.keySet( );
	}

	/**
	 * Returns the incomplete
	 * {@link de.xirp.profile.Profile profile} map.
	 * 
	 * @return The available incomplete profiles.
	 * @see de.xirp.profile.Profile
	 */
	protected static List<Profile> getIncompleteProfiles() {
		return incompleteProfiles.values( );
	}

	/**
	 * Parses the given
	 * {@link de.xirp.profile.Profile profile} file and
	 * returns the resulting
	 * {@link de.xirp.profile.Profile profile} bean. <br>
	 * <br>
	 * This method calls the
	 * {@link de.xirp.profile.ProfileParser#parseRobot(String)}
	 * parsing method for the robot file names available in the just
	 * created {@link de.xirp.profile.Profile profile}
	 * bean. At return time of this method the
	 * {@link de.xirp.profile.Profile profile} is
	 * complete, including
	 * {@link de.xirp.profile.Robot robots} and
	 * {@link de.xirp.profile.CommunicationSpecification comm-specs}.
	 * 
	 * @param xmlFile
	 *            The XML profile file.
	 * @return The parsed profile bean.
	 * @throws JAXBException
	 *             if something went wrong while parsing.
	 * @see de.xirp.profile.Profile profile
	 * @see de.xirp.profile.Robot
	 * @see de.xirp.profile.CommunicationSpecification
	 */
	private static Profile parseProfile(File xmlFile) throws JAXBException {
		logClass.info(I18n.getString("ProfileParser.log.parsing.profile", xmlFile) + //$NON-NLS-1$
				Constants.LINE_SEPARATOR);

		SchemaFactory factory = getFactory( );

		JAXBContext jc = JAXBContext.newInstance(Profile.class);
		Unmarshaller um = jc.createUnmarshaller( );

		try {
			Schema schema = factory.newSchema(new File(Constants.CONF_PROFILES_XML_SCHEMA));
			um.setSchema(schema);
		}
		catch (SAXException e) {
			logClass.error("Error: " + e.getMessage( ) + //$NON-NLS-1$
					Constants.LINE_SEPARATOR, e);
		}

		Profile profile = (Profile) um.unmarshal(xmlFile);

		for (String robotName : profile.getRobotFileNames( )) {
			Robot robot = parseRobot(robotName, factory);
			if (robot != null) {
				if (robot.isComplete( )) {
					profile.addRobot(robot);
				}
				else {
					logClass.warn(Constants.LINE_SEPARATOR +
							I18n.getString("ProfileParser.log.robotNotCompleted", //$NON-NLS-1$
									robot.getName( ),
									Constants.LINE_SEPARATOR,
									(robotName + Constants.ROBOT_POSTFIX)) +
							Constants.LINE_SEPARATOR);
				}
			}
		}
		return profile;
	}

	/**
	 * Parses the robot from the given file.
	 * 
	 * @param fileName
	 *            the file name to parse the robot from
	 * @return the robot parsed from the file.
	 */
	protected static Robot parseRobot(String fileName) {
		return parseRobot(fileName, getFactory( ));

	}

	/**
	 * Parses the given
	 * {@link de.xirp.profile.Robot robot} file and
	 * returns the resulting
	 * {@link de.xirp.profile.Robot robot} bean. <br>
	 * <br>
	 * This method calls the
	 * {@link de.xirp.profile.ProfileParser#parseComSpec(String)}
	 * method for all available
	 * {@link de.xirp.profile.CommunicationSpecification comm-spec}
	 * file names from the just created
	 * {@link de.xirp.profile.Robot robot} object. At
	 * return time of the method the returned
	 * {@link de.xirp.profile.Robot robot} is complete
	 * including the
	 * {@link de.xirp.profile.CommunicationSpecification comm-specs}.
	 * 
	 * @param fileName
	 *            The XML robot file name (without postfix and path).
	 * @param factory
	 *            The factory which is used to load the XML schema
	 *            definition used for validating the XML.
	 * @return the parsed robot or <code>null</code> if an error
	 *         occurred.
	 * @see de.xirp.profile.Robot
	 * @see de.xirp.profile.CommunicationSpecification
	 */
	private static Robot parseRobot(String fileName, SchemaFactory factory) {
		if (!Util.isEmpty(fileName)) {
			try {
				JAXBContext jc = JAXBContext.newInstance(Robot.class);
				Unmarshaller um = jc.createUnmarshaller( );

				try {
					Schema schema = factory.newSchema(new File(Constants.CONF_ROBOTS_XML_SCHEMA));
					um.setSchema(schema);
				}
				catch (SAXException e) {
					logClass.error("Error: " + e.getMessage( ) + //$NON-NLS-1$
							Constants.LINE_SEPARATOR, e);
				}

				Robot robot = (Robot) um.unmarshal(new File(Constants.CONF_ROBOTS_DIR +
						File.separator + fileName + Constants.ROBOT_POSTFIX));

				String cmsFileName = robot.getCommSpecFileName( );
				CommunicationSpecification comSpec = parseComSpec(cmsFileName,
						factory);
				if (comSpec.isComplete( )) {
					robot.setCommunicationSpecification(comSpec);
				}
				else {
					logClass.warn(Constants.LINE_SEPARATOR +
							I18n.getString("ProfileParser.log.comspecNotCompleted", //$NON-NLS-1$
									Constants.LINE_SEPARATOR,
									(cmsFileName + Constants.COMM_SPEC_POSTFIX)) +
							Constants.LINE_SEPARATOR);
				}

				robot.setBotFile(new File(Constants.CONF_ROBOTS_DIR, fileName));
				return robot;
			}
			catch (JAXBException e) {
				logClass.error("Error " + e.getMessage( ) +
						Constants.LINE_SEPARATOR, e);
				return null;
			}
		}
		else {
			return null;
		}
	}

	/**
	 * Gets the factory for parsing the file using XML schema
	 * validation.
	 * 
	 * @return the factory
	 */
	private static SchemaFactory getFactory() {
		return SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	}

	/**
	 * Parses the communication specification from the given file.
	 * 
	 * @param fileName
	 *            the name of the file to parse
	 * @return the parsed com specs
	 */
	protected static CommunicationSpecification parseComSpec(String fileName) {
		return parseComSpec(fileName, getFactory( ));

	}

	/**
	 * Parses the given
	 * {@link de.xirp.profile.CommunicationSpecification comm-spec}
	 * file and returns the resulting
	 * {@link de.xirp.profile.CommunicationSpecification comm-spec}
	 * bean.
	 * 
	 * @param fileName
	 *            The XML comm-spec file name (without postfix and
	 *            path).
	 * @param factory
	 *            The factory which is used to load the XML schema
	 *            definition used for validating the XML.
	 * @return the parsed communicationSpecification or
	 *         <code>null</code> if an error occurred.
	 */
	private static CommunicationSpecification parseComSpec(String fileName,
			SchemaFactory factory) {

		if (!Util.isEmpty(fileName)) {
			CommunicationSpecification comSpec;
			try {
				JAXBContext jc = JAXBContext.newInstance(CommunicationSpecification.class);
				Unmarshaller um = jc.createUnmarshaller( );
				try {
					Schema schema = factory.newSchema(new File(Constants.CONF_COMMSPECS_XML_SCHEMA));
					um.setSchema(schema);
				}
				catch (SAXException e) {
					logClass.error("Error: " + e.getMessage( ) + //$NON-NLS-1$
							Constants.LINE_SEPARATOR, e);
				}
				comSpec = (CommunicationSpecification) um.unmarshal(new File(Constants.CONF_COMMSPECS_DIR +
						File.separator + fileName + Constants.COMM_SPEC_POSTFIX));

				comSpec.setCmsFile(new File(Constants.CONF_COMMSPECS_DIR,
						fileName));
				return comSpec;
			}
			catch (JAXBException e) {
				logClass.error("Error " + e.getMessage( ) //$NON-NLS-1$
						+ Constants.LINE_SEPARATOR, e);
				return null;
			}
		}
		else {
			return null;
		}
	}
}
