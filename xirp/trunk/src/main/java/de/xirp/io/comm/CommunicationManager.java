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
 * CommunicationManager.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   Matthias Gernand [matthias.gernand AT gmx.de]
 *
 * Changes
 * -------
 * 10.03.2006:		Created by Rabea Gransberger.
 */
package de.xirp.io.comm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.xirp.io.ConnectionObject;
import de.xirp.io.comm.data.Datapool;
import de.xirp.io.comm.data.DatapoolException;
import de.xirp.io.comm.data.DatapoolManager;
import de.xirp.io.comm.handler.IHandler;
import de.xirp.io.comm.lowlevel.ByteEvent;
import de.xirp.io.comm.lowlevel.IByteListener;
import de.xirp.io.comm.lowlevel.ICommunicationInterface;
import de.xirp.io.comm.protocol.IProtocol;
import de.xirp.io.event.ConnectionEvent;
import de.xirp.io.event.ConnectionListener;
import de.xirp.managers.AbstractManager;
import de.xirp.managers.ManagerException;
import de.xirp.plugin.IPlugable;
import de.xirp.plugin.IPluginFilter;
import de.xirp.plugin.PluginManager;
import de.xirp.plugin.PluginType;
import de.xirp.plugin.SecurePluginView;
import de.xirp.profile.CommunicationProtocol;
import de.xirp.profile.ProfileManager;
import de.xirp.profile.Robot;
import de.xirp.profile.RobotNotFoundException;
import de.xirp.util.Constants;
import de.xirp.util.I18n;

/**
 * Manager for communication with the robots.
 * 
 * @author Rabea Gransberger
 * @author Matthias Gernand
 */
public final class CommunicationManager extends AbstractManager {

	/**
	 * Log4j Logger for this class
	 */
	private static final Logger LOGGER = Logger.getLogger(CommunicationManager.class);

	/**
	 * The currently used protocol classes(value) for each robot(key)
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, IProtocol> protocols = new HashMap<String, IProtocol>( );
	/**
	 * The currently used communication classes(value) for each
	 * robot(key)
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, ICommunicationInterface> cInterfaces = new HashMap<String, ICommunicationInterface>( );
	/**
	 * List of connected robot names
	 */
	private static Set<String> connected = new HashSet<String>( );
	/**
	 * Listeners which are informed on connects and disconnects
	 */
	private static Vector<ConnectionListener> listeners = new Vector<ConnectionListener>( );

	/**
	 * Received data amount for each robot
	 */
	private static Map<String, DataAmount> received = new HashMap<String, DataAmount>( );

	/**
	 * Sent data amount for each robot
	 */
	private static Map<String, DataAmount> sent = new HashMap<String, DataAmount>( );
	/**
	 * List of robot names for which listeners are registered
	 */
	private static List<String> listening = new ArrayList<String>( );

	/**
	 * Constructs a new CommunicationManager. The manager is
	 * initialized on startup. Never call this on your own. Use the
	 * statically provided methods.
	 * 
	 * @throws InstantiationException
	 *             if an instance of this manager does already exist
	 */
	public CommunicationManager() throws InstantiationException {
		super( );
	}

	/**
	 * Adds the given listener for being notified when bytes are
	 * received to the communication interface for the given robot
	 * name.
	 * 
	 * @param robotName
	 *            the name of the robot to add the listener to
	 * @param listener
	 *            the listener to add
	 * @return <code>true</code> if the listener was added,
	 *         <code>false</code> if there was no communication
	 *         interface for the given robot (robot not connected
	 *         f.e.)
	 * @see de.xirp.io.comm.lowlevel.ICommunicationInterface#addBytesReceivedListener(de.xirp.io.comm.lowlevel.IByteListener)
	 */
	@SuppressWarnings("unchecked")
	public static boolean addBytesReceivedListener(String robotName,
			IByteListener listener) {
		ICommunicationInterface communicationInterface = cInterfaces.get(robotName);
		if (communicationInterface != null) {
			communicationInterface.addBytesReceivedListener(listener);
			return true;
		}
		if (LOGGER.isDebugEnabled( )) {
			LOGGER.debug(I18n.getString("CommunicationManager.notadded.bytes.received", //$NON-NLS-1$
					robotName) +
					Constants.LINE_SEPARATOR);
		}
		return false;
	}

	/**
	 * Adds the given listener for being notified when bytes are sent
	 * to the communication interface for the given robot name.
	 * 
	 * @param robotName
	 *            the name of the robot to add the listener to
	 * @param listener
	 *            the listener to add
	 * @return <code>true</code> if the listener was added,
	 *         <code>false</code> if there was no communication
	 *         interface for the given robot (robot not connected
	 *         f.e.)
	 * @see de.xirp.io.comm.lowlevel.ICommunicationInterface#addBytesSendListener(de.xirp.io.comm.lowlevel.IByteListener)
	 */
	@SuppressWarnings("unchecked")
	public static boolean addBytesSendListener(String robotName,
			IByteListener listener) {
		ICommunicationInterface communicationInterface = cInterfaces.get(robotName);
		if (communicationInterface != null) {
			communicationInterface.addBytesSendListener(listener);
		}
		if (LOGGER.isDebugEnabled( )) {
			LOGGER.debug(I18n.getString("CommunicationManager.notadded.bytes.send", //$NON-NLS-1$
					robotName) +
					Constants.LINE_SEPARATOR);
		}
		return false;
	}

	/**
	 * Removes the given listener which was notified when bytes are
	 * received from the communication interface for the given robot
	 * name.
	 * 
	 * @param robotName
	 *            the name of the robot to remove the listener from
	 * @param listener
	 *            the listener to remove
	 * @return <code>true</code> if the listener was removed,
	 *         <code>false</code> if there was no communication
	 *         interface for the given robot (robot not connected
	 *         f.e.)
	 * @see de.xirp.io.comm.lowlevel.ICommunicationInterface#removeBytesReceivedListener(de.xirp.io.comm.lowlevel.IByteListener)
	 */
	public static boolean removeBytesReceivedListener(String robotName,
			IByteListener listener) {
		ICommunicationInterface<?, ?> communicationInterface = cInterfaces.get(robotName);
		if (communicationInterface != null) {
			communicationInterface.removeBytesReceivedListener(listener);
			return true;
		}
		if (LOGGER.isDebugEnabled( )) {
			LOGGER.debug(I18n.getString("CommunicationManager.notremoved.bytes.received", //$NON-NLS-1$
					robotName) +
					Constants.LINE_SEPARATOR);
		}
		return false;
	}

	/**
	 * Removes the given listener which was notified when bytes are
	 * send from the communication interface for the given robot name
	 * 
	 * @param robotName
	 *            the name of the robot to remove the listener from
	 * @param listener
	 *            the listener to remove
	 * @return <code>true</code> if the listener was removed,
	 *         <code>false</code> if there was no communication
	 *         interface for the given robot (robot not connected
	 *         f.e.)
	 * @see de.xirp.io.comm.lowlevel.ICommunicationInterface#removeBytesSendListener(de.xirp.io.comm.lowlevel.IByteListener)
	 */
	public static boolean removeBytesSendListener(String robotName,
			IByteListener listener) {
		ICommunicationInterface<?, ?> communicationInterface = cInterfaces.get(robotName);
		if (communicationInterface != null) {
			communicationInterface.removeBytesSendListener(listener);
			return true;
		}
		if (LOGGER.isDebugEnabled( )) {
			LOGGER.debug(I18n.getString("CommunicationManager.notremoved.bytes.send", //$NON-NLS-1$
					robotName) +
					Constants.LINE_SEPARATOR);
		}
		return false;
	}

	/**
	 * Adds the listener to the list of listeners which are informed
	 * on connects and disconnects to/from robots.<br/><br/>If the
	 * listener is already contained in the list, it's not added
	 * again.<br/><br/>Note: You will receive events for all robots,
	 * so remember to check if the robot name in the event is equal to
	 * the robot you like to receive events from.
	 * 
	 * @param listener
	 *            The listener to add
	 */
	public static void addConnectionListener(ConnectionListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/**
	 * Removes the given connection listener from the list of
	 * listeners which were notified on connects and disconnects to
	 * robots.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public static void removeConnectionListener(ConnectionListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Checks if a connection to the given robot exists.
	 * 
	 * @param robotName
	 *            name of the Robot
	 * @return <code>true</code> if connection to robot exists
	 */
	public static boolean isConnected(String robotName) {
		return connected.contains(robotName);
	}

	/**
	 * Gets the communication interface for the given robot and main
	 * class.
	 * 
	 * @param robotName
	 *            the robot to get the interface for
	 * @param className
	 *            the main class of the interface
	 * @return the interface or <code>null</code> if no plugin for
	 *         the given main class was found or the plugin was not
	 *         instance of
	 *         {@link de.xirp.io.comm.lowlevel.ICommunicationInterface}
	 */
	@SuppressWarnings("unchecked")
	private static ICommunicationInterface getCommunication(String robotName,
			String className) {
		IPlugable commPlugin = PluginManager.runPlugin(robotName, className);
		commPlugin = getOriginalPlugin(commPlugin);
		if (commPlugin != null && commPlugin instanceof ICommunicationInterface) {
			commPlugin.setRobot(robotName);

			ICommunicationInterface com = (ICommunicationInterface) commPlugin;
			return com;
		}
		return null;
	}

	/**
	 * Gets the protocol for the given robot and main class.
	 * 
	 * @param robotName
	 *            the robot to get the protocol for
	 * @param className
	 *            the main class of the protocol
	 * @return the protocol or <code>null</code> if no plugin for
	 *         the given main class was found or the plugin was not
	 *         instance of
	 *         {@link de.xirp.io.comm.protocol.IProtocol}
	 */
	@SuppressWarnings("unchecked")
	private static IProtocol getProtocol(String robotName, String className) {
		IPlugable protocol = PluginManager.runPlugin(robotName, className);
		protocol = getOriginalPlugin(protocol);
		if (protocol != null && protocol instanceof IProtocol) {
			protocol.setRobot(robotName);

			IProtocol com = (IProtocol) protocol;
			return com;
		}
		return null;
	}

	/**
	 * Adds bytes received and sent listeners to the given
	 * communication, and notifies the connection listeners that a
	 * connection was established.
	 * 
	 * @param robotName
	 *            the robot name of the communication
	 * @param com
	 *            the communication object
	 */
	@SuppressWarnings("unchecked")
	private static void register(final String robotName,
			ICommunicationInterface com) {
		if (!listening.contains(robotName)) {
			com.addBytesReceivedListener(new IByteListener( ) {

				public void handleBytes(ByteEvent e) {
					DataAmount r = received.get(robotName);
					if (r == null) {
						r = new DataAmount( );
					}
					received.put(robotName, r.add(e.getAmount( )));

				}

			});

			com.addBytesSendListener(new IByteListener( ) {

				public void handleBytes(ByteEvent e) {
					DataAmount s = sent.get(robotName);
					if (s == null) {
						s = new DataAmount( );
					}
					sent.put(robotName, s.add(e.getAmount( )));
				}

			});

			com.addConnectionEventListener(new ConnectionListener( ) {

				public void connectionEstablished(@SuppressWarnings("unused")
				ConnectionEvent event) {
					// TODO Auto-generated method stub

				}

				public void disconnected(ConnectionEvent event) {
					removeRobot(event.getRobotName( ));
// disconnect(event.getRobotName( ));
				}

			});

			listening.add(robotName);
		}

		ConnectionEvent event = new ConnectionEvent(CommunicationManager.class,
				robotName);

		for (ConnectionListener listener : listeners) {
			listener.connectionEstablished(event);
		}
	}

	/**
	 * Connects to the robot using the data contained in the given
	 * connection object.<br/><br/>Note: Should not be called from
	 * outside this applications API.
	 * 
	 * @param connectObj
	 *            data for the connection
	 * @return <code>true</code> if connection was established, or
	 *         the robot was already connected<br>
	 *         <code>false</code> otherwise
	 */
	@SuppressWarnings("unchecked")
	public static boolean connect(ConnectionObject connectObj) {

		final String robotName = connectObj.getRobotName( );
		if (isConnected(robotName)) {
			if (LOGGER.isInfoEnabled( )) {
				LOGGER.info(I18n.getString("CommunicationManager.alredy.connected", //$NON-NLS-1$
						robotName) +
						Constants.LINE_SEPARATOR);
			}
			return true;
		}
		boolean success = false;
		try {
			// Get communication interface
			ICommunicationInterface com = getCommunication(robotName,
					connectObj.getCommunicationClass( ));

			if (com != null) {
				// get protocol
				IProtocol protocol = getProtocol(robotName,
						connectObj.getProtocolClass( ));
				if (protocol != null) {
					protocol.setCInterface(com);

					// get handler and register with protocol and
					// datapool
					getAndRegisterHandler(robotName, protocol);

					// start the communication
					if (protocol.startCommunication( )) {
						addRobot(robotName, com, protocol);
						// register the listeners
						register(robotName, com);

						if (LOGGER.isInfoEnabled( )) {
							LOGGER.info(I18n.getString("CommunicationManager.connection.established", //$NON-NLS-1$
									robotName) +
									Constants.LINE_SEPARATOR);
						}

						success = true;
					}
					else {
						removeRobot(connectObj.getRobotName( ));
					}

				}
				else {
					throw new Exception(I18n.getString("CommunicationManager.log.pluginNotInstanceOfIRobotCommunication",//$NON-NLS-1$
							connectObj.getProtocolClass( )) +
							Constants.LINE_SEPARATOR);
				}

				// stop protocol plugin
				if (!success) {
					PluginManager.runPlugin(robotName,
							connectObj.getProtocolClass( ));
				}
			}
			else {
				throw new Exception(I18n.getString("CommunicationManager.log.pluginNotInstanceOfICommunication",//$NON-NLS-1$
						connectObj.getCommunicationClass( )) +
						Constants.LINE_SEPARATOR);
			}

			// stop communication plugin
			if (!success) {
				PluginManager.runPlugin(robotName,
						connectObj.getCommunicationClass( ));
			}
		}
		catch (Exception e) {
			LOGGER.error(I18n.getString("CommunicationManager.log.errorWhileConnecting", //$NON-NLS-1$
					connectObj.toString( ),
					e.getMessage( )) +
					Constants.LINE_SEPARATOR,
					e);
		}

		if (success) {
			connected.add(robotName);
		}

		return success;
	}

	/**
	 * Disconnect from the robot with the given name. Listeners are
	 * notified about the disconnect. Sent and received bytes are
	 * reset.<br/><br/>Note: Should not be called from outside this
	 * applications API.
	 * 
	 * @param robotName
	 *            name of the robot
	 */
	@SuppressWarnings("unchecked")
	public static void disconnect(String robotName) {
		if (connected.contains(robotName)) {
			IProtocol robotCom = getProtocol(robotName);
			if (robotCom != null) {
				robotCom.stopCommunication( );

				ConnectionEvent connectionEvent = new ConnectionEvent(CommunicationManager.class,
						robotName);
				for (ConnectionListener listener : listeners) {
					listener.disconnected(connectionEvent);
				}

				removeRobot(robotName);
				received.remove(robotName);
				sent.remove(robotName);

				connected.remove(robotName);
			}
		}
	}

	/**
	 * Extracts the original plugin of the given plugin if this is an
	 * instance of {@link SecurePluginView}.
	 * 
	 * @param plugin
	 *            a plugin
	 * @return the original plugin of the {@link SecurePluginView} or
	 *         the given plugin itself
	 */
	private static IPlugable getOriginalPlugin(IPlugable plugin) {
		if (plugin instanceof SecurePluginView) {
			SecurePluginView secure = (SecurePluginView) plugin;
			return secure.getOriginalPlugin( );
		}
		return plugin;
	}

	/**
	 * Gets the handler corresponding to the given robot name and
	 * protocol and registers it with the protocol and datapool.
	 * 
	 * @param robotName
	 *            the robot name for the handler
	 * @param protocol
	 *            the protocol to get the corresponding handler for
	 */
	@SuppressWarnings("unchecked")
	private static void getAndRegisterHandler(String robotName,
			IProtocol protocol) {
		try {
			Robot robot = ProfileManager.getRobot(robotName);
			IHandler handler = null;
			String protocolClass = protocol.getClass( ).getName( );
			for (CommunicationProtocol comSpec : robot.getCommunicationSpecification( )
					.getCommunicationProtocols( )) {

				if (comSpec.getClassName( ).equalsIgnoreCase(protocolClass)) {
					String mainClass = comSpec.getMessageHandler( );

					IPlugable<?> plugin = PluginManager.runPlugin(robotName,
							mainClass);
					plugin = getOriginalPlugin(plugin);
					if (plugin != null && plugin instanceof IHandler) {
						handler = (IHandler) plugin;
						plugin.setRobot(robotName);
						break;
					}
					else {
						PluginManager.stopPlugin(robotName,
								mainClass,
								plugin != null ? plugin.getIdentifier( ) : null);
					}

				}
			}
			if (handler != null) {
				handler.setIProtocol(protocol);
				try {
					Datapool dp = DatapoolManager.getDatapool(robotName);
					if (dp != null) {
						dp.setHandler(handler);
					}
				}
				catch (DatapoolException e) {
					LOGGER.error("Error " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
				}
			}
			else {
				LOGGER.warn(I18n.getString("CommunicationManager.log.noHandlerForRobot", robotName) + Constants.LINE_SEPARATOR); //$NON-NLS-1$ 
			}
		}
		catch (RobotNotFoundException e) {
			LOGGER.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}

	}

	/**
	 * Adds the protocol and communication to the internal cache.
	 * 
	 * @param robotName
	 *            the robot name
	 * @param com
	 *            the communication
	 * @param protocol
	 *            the protocol
	 */
	@SuppressWarnings("unchecked")
	private static void addRobot(String robotName, ICommunicationInterface com,
			IProtocol protocol) {
		protocols.put(robotName, protocol);
		cInterfaces.put(robotName, com);
	}

	/**
	 * Removes the protocol and communication from the internal cache.
	 * 
	 * @param robotName
	 *            the robot name
	 */
	private static void removeRobot(String robotName) {
		protocols.remove(robotName);
		cInterfaces.remove(robotName);
	}

	/**
	 * Gets the protocol for this robot (if any and the robot is
	 * connected).
	 * 
	 * @param robotName
	 *            name of the robot
	 * @return protocol for this robot, or <code>null</code> if no
	 *         protocol was found (f.i. if the robot is not connected)
	 * @deprecated This method will be removed in future releases and
	 *             should not be used furthermore. Use the
	 *             Handler/Datapool Mechanism instead
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public static IProtocol getProtocol(String robotName) {
		return protocols.get(robotName);
	}

	/**
	 * Get's the names of all plugin classes which have the type
	 * {@link PluginType#COMMUNICATION} and belong to the given robot.
	 * 
	 * @param robotName
	 *            the name of the robot
	 * @return list of string with fully qualified class names
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getCommunicationClasses(String robotName) {
		List<IPlugable> coms = PluginManager.getPlugins(robotName,
				new IPluginFilter( ) {

					public boolean filterPlugin(IPlugable plugin) {
						return PluginType.containsType(plugin,
								PluginType.COMMUNICATION);
					}

				});

		ArrayList<String> vec = new ArrayList<String>(coms.size( ));
		for (IPlugable plugin : coms) {
			vec.add(plugin.getInfo( ).getMainClass( ));
		}
		return Collections.unmodifiableList(vec);
	}

	/**
	 * The communication classes keep track of the bytes sent and
	 * received. This class gets the sum of the received bytes of the
	 * communication class for the given robot.
	 * 
	 * @param robotName
	 *            the name of the robot
	 * @return dataAmount received from the robot
	 */
	public static DataAmount getBytesReceived(String robotName) {
		DataAmount r = received.get(robotName);
		if (r == null) {
			return new DataAmount( );
		}
		return r;
	}

	/**
	 * The communication classes keep track of the bytes sent and
	 * received. This class gets the sum of the sent bytes of the
	 * communication class for the given robot.
	 * 
	 * @param robotName
	 *            the name of the robot
	 * @return DataAmount sent to the robot
	 */
	public static DataAmount getBytesSend(String robotName) {
		DataAmount s = sent.get(robotName);
		if (s == null) {
			return new DataAmount( );
		}
		return s;
	}

	/**
	 * Does nothing.<br/><br/>This method is called from the
	 * {@link de.xirp.managers.ManagerFactory} on
	 * startup. Do not call it on your own.
	 * 
	 * @see de.xirp.managers.AbstractManager#start()
	 */
	@Override
	protected void start() throws ManagerException {
		super.start( );
	}

	/**
	 * Stops this manager and disconnects from all robots.<br/><br/>
	 * This method is called from the
	 * {@link de.xirp.managers.ManagerFactory} on
	 * shutdown. Do not call it on your own.
	 * 
	 * @see de.xirp.managers.AbstractManager#stop()
	 */
	@Override
	protected void stop() throws ManagerException {
		super.stop( );
		for (String robotName : protocols.keySet( )) {
			disconnect(robotName);
		}
	}
}
