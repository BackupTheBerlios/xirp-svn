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
 * ProfileManager.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 15.02.2006:		Created by Matthias Gernand.
 */
package de.xirp.profile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import de.xirp.io.comm.data.DatapoolUtil;
import de.xirp.managers.AbstractManager;
import de.xirp.managers.ManagerException;
import de.xirp.util.Constants;
import de.xirp.util.I18n;
import de.xirp.util.collections.MultiValueHashMap;

/**
 * This class manages the profile data structure. When a new instance
 * of this manager is created all existing profile/robot/comm-spec
 * files are parsed into the
 * {@link de.xirp.profile.Profile profile} data
 * structure. <br>
 * <br>
 * All profiles are loaded, even if the profile is not completed. The
 * incomplete profiles are not available for use in the workspace but
 * can be completed using the profile editor. <br>
 * <br>
 * The manager provides methods for retrieving
 * {@link de.xirp.profile.Profile profile} and
 * {@link de.xirp.profile.Robot robot} beans. These beans
 * can be retrieved by their unique names.
 * 
 * @author Matthias Gernand
 * @see de.xirp.profile.Profile
 * @see de.xirp.profile.ProfileParser
 */
public final class ProfileManager extends AbstractManager {

	/**
	 * The logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(ProfileManager.class);
	/**
	 * The profile beans.
	 */
	private static MultiValueHashMap<String, Profile> profiles = new MultiValueHashMap<String, Profile>( );
	/**
	 * The incomplete profile beans.
	 */
	private static List<Profile> incompleteProfiles = Collections.emptyList( );
	/**
	 * The unique profile names.
	 */
	private static Set<String> profileNames = Collections.emptySet( );

	/**
	 * Constructor needed for the
	 * {@link de.xirp.managers.AbstractManager}
	 * architecture. Use the static methods provided by this class.
	 * <br>
	 * <br>
	 * The manager is initialized on startup. Never call this on your
	 * own. Use the statically provided methods.
	 * 
	 * @throws InstantiationException
	 *             if there is already an instance of this manager.
	 */
	public ProfileManager() throws InstantiationException {
		super( );
	}

	/**
	 * Returns the unique
	 * {@link de.xirp.profile.Profile profile} names.
	 * 
	 * @return An unmodifiable list with the profile names.
	 * @see de.xirp.profile.Profile
	 */
	public static Set<String> getProfileNames() {
		return Collections.unmodifiableSet(profileNames);
	}

	/**
	 * Returns all completed
	 * {@link de.xirp.profile.Profile profiles}.
	 * 
	 * @return An unmodifiable list with the completed profiles.
	 * @see de.xirp.profile.Profile
	 */
	public static List<Profile> getProfiles() {
		return Collections.unmodifiableList(profiles.values( ));
	}

	/**
	 * Returns all incomplete
	 * {@link de.xirp.profile.Profile profiles}.
	 * 
	 * @return A unmodifiable list with the incomplete profiles.
	 * @see de.xirp.profile.Profile
	 */
	public static List<Profile> getIncompleteProfiles() {
		return Collections.unmodifiableList(incompleteProfiles);
	}

	/**
	 * Returns the number of complete
	 * {@link de.xirp.profile.Profile profiles}.
	 * 
	 * @return The number of complete profile.
	 * @see de.xirp.profile.Profile
	 */
	public static int getProfileCount() {
		return profiles.size( );
	}

	/**
	 * Returns the number of incomplete
	 * {@link de.xirp.profile.Profile profiles}.
	 * 
	 * @return The number of incomplete profiles.
	 * @see de.xirp.profile.Profile
	 */
	public static int getIncompleteProfileCount() {
		return incompleteProfiles.size( );
	}

	/**
	 * Returns all {@link de.xirp.profile.Robot robots}
	 * of the profiles.
	 * 
	 * @return An unmodifiable list with all robots.
	 * @see de.xirp.profile.Robot
	 */
	public static List<Robot> getRobots() {
		List<Robot> robots = new ArrayList<Robot>( );
		for (Profile aux : getProfiles( )) {
			for (Robot robot : aux.getRobots( )) {
				robots.add(robot);
			}
		}
		return Collections.unmodifiableList(robots);
	}

	/**
	 * Returns the number of
	 * {@link de.xirp.profile.Robot robots}.
	 * 
	 * @return The number of robots.
	 * @see de.xirp.profile.Robot
	 */
	public static int getRobotCount() {
		int cnt = 0;
		for (Profile profile : profiles.values( )) {
			cnt += profile.getRobots( ).size( );
		}
		return cnt;
	}

	/**
	 * Returns the {@link de.xirp.profile.Robot} object
	 * for the given robot name.
	 * 
	 * @param robotName
	 *            The robot name to return the robot object for.
	 * @return The corresponding robot object.
	 * @see de.xirp.profile.Robot
	 * @throws RobotNotFoundException
	 *             if the desired robot could not be found.
	 */
	public static Robot getRobot(String robotName)
			throws RobotNotFoundException {

		for (Profile profile : getProfiles( )) {
			for (Robot robot : profile.getRobots( )) {
				if (robot.getName( ).equals(robotName)) {
					return robot;
				}
			}
		}
		throw new RobotNotFoundException(robotName);
	}

	/**
	 * Returns the {@link de.xirp.profile.Profile} bean
	 * for the given profile name.
	 * 
	 * @param name
	 *            The name to get the profile for.
	 * @return The corresponding profile.
	 */
	public static Profile getProfile(String name) {
		return profiles.get(name).get(0);
	}

	/**
	 * Gets the datapool keys for all sensors of the given robot.
	 * 
	 * @param robotName
	 *            the name of the robot
	 * @return all keys for the sensors of the robot
	 */
	public static List<String> getSensorDatapoolKeys(String robotName) {
		List<String> keys = new ArrayList<String>( );
		try {
			Robot robot = getRobot(robotName);
			for (Sensorgroup sg : robot.getSensorgroups( )) {
				@SuppressWarnings("unused")
				String aux = sg.getDatapoolKey( );
				for (Sensor s : sg.getSensors( )) {
					keys.add(DatapoolUtil.createDatapoolKey(sg, s));
				}
			}
		}
		catch (RobotNotFoundException e) {
			return Collections.emptyList( );
		}
		return Collections.unmodifiableList(keys);
	}

	/**
	 * Starts the parsing of the existing <code>*.pro</code> files.
	 * The profile data is not available until this manager has
	 * started. <br>
	 * <br>
	 * This method is called from the
	 * {@link de.xirp.managers.ManagerFactory} on
	 * startup. Do not call it on your own.
	 * 
	 * @see de.xirp.managers.AbstractManager#start()
	 */
	@Override
	protected void start() throws ManagerException {
		super.start( );
		logClass.info(I18n.getString("ProfileManager.log.profileManagerStarting")); //$NON-NLS-1$
		ProfileParser.parse( );
		profiles = ProfileParser.getProfiles( );
		incompleteProfiles = ProfileParser.getIncompleteProfiles( );
		profileNames = ProfileParser.getProfileNames( );
		logClass.info(I18n.getString("ProfileManager.log.finished") + Constants.LINE_SEPARATOR); //$NON-NLS-1$ 
	}

	/**
	 * Does nothing. <br>
	 * <br>
	 * This method is called from the
	 * {@link de.xirp.managers.ManagerFactory} on
	 * shutdown. Do not call it on your own.
	 * 
	 * @see de.xirp.managers.AbstractManager#stop()
	 */
	@Override
	protected void stop() throws ManagerException {
		super.stop( );
	}

	/**
	 * Returns an incomplete
	 * {@link de.xirp.profile.Robot} bean. Incomplete
	 * robots are not loaded while parsing at startup. This method is
	 * used when editing an unfinished robot in the robot editor.
	 * 
	 * @param file
	 *            The file to load the bean for.
	 * @return The desired robot bean.
	 * @see de.xirp.profile.Robot
	 */
	public static Robot getRobotFromFile(File file) {
		String name = FilenameUtils.getBaseName(file.getName( ));
		return ProfileParser.parseRobot(name);
	}

	/**
	 * Returns an incomplete
	 * {@link de.xirp.profile.CommunicationSpecification}
	 * bean. Incomplete comm-specs are not loaded while parsing at
	 * startup. This method is used when editing an unfinished
	 * comm-spec in the comm-spec editor.
	 * 
	 * @param file
	 *            The file to load the bean for.
	 * @return The desired comm-spec bean.
	 * @see de.xirp.profile.CommunicationSpecification
	 */
	public static CommunicationSpecification getCommSpecFromFile(File file) {
		String name = FilenameUtils.getBaseName(file.getName( ));
		return ProfileParser.parseComSpec(name);
	}
}
