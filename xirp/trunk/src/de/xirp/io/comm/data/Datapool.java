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
 * Datapool.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * 					 Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 05.02.2007:		Created by Matthias Gernand.
 */
package de.xirp.io.comm.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javolution.util.FastMap;
import javolution.util.FastTable;

import org.apache.commons.lang.builder.ToStringBuilder;

import de.xirp.io.comm.CommunicationManager;
import de.xirp.io.comm.handler.IHandler;
import de.xirp.io.event.ConnectionEvent;
import de.xirp.io.event.ConnectionListener;
import de.xirp.io.logging.RobotLogger;
import de.xirp.profile.Robot;
import de.xirp.ui.Application;
import de.xirp.util.Constants;
import de.xirp.util.I18n;
import de.xirp.util.collections.ConcurrentMultiValueHashMap;

/**
 * The datapool is the main point for data which is received from the
 * robot or send to the robot.<br/><br/> The data is saved under a
 * given key. Incoming data of an infrared sensor may be saved under
 * the keys ir_front and ir_rear whereas incoming data of a laser
 * scanner may be saved as whole float array under the key
 * laser_front.<br/><br/>Plugins may add listeners for send and
 * received data to the datapool and/or just get the actual value.
 * 
 * @author Matthias Gernand
 * @author Rabea Gransberger
 */
public final class Datapool {

	/**
	 * Log4j Logger for this class
	 */
	private static RobotLogger logClass = RobotLogger.getLogger(Datapool.class);

	/**
	 * Key for listeners registered for all data of the robot
	 */
	private final static String ROBOT_KEY = "%ROBOTALL%"; //$NON-NLS-1$

	/**
	 * The robot this datapool is for
	 */
	private Robot robot;
	/**
	 * The handler over which the robot sends data
	 */
	private IHandler handler;

	/**
	 * Listeners for incoming data.
	 * 
	 * @see #ROBOT_KEY is a special key for this
	 */
	private ConcurrentMultiValueHashMap<String, DatapoolListener> recvListeners = new ConcurrentMultiValueHashMap<String, DatapoolListener>( );
	/**
	 * Listeners for outgoing data.
	 * 
	 * @see #ROBOT_KEY is a special key for this
	 */
	private ConcurrentMultiValueHashMap<String, DatapoolListener> sendListeners = new ConcurrentMultiValueHashMap<String, DatapoolListener>( );

	/**
	 * Queue for sending data. Data is not sent directly, to prevent
	 * failures and to decouple the sender from the sending
	 */
	private final Queue<DatapoolMessage> sendQueue = new ConcurrentLinkedQueue<DatapoolMessage>( );
	/**
	 * Queue for received data. Data is not distributed directly to
	 * the registered listeners, to decouple the sending robot from
	 * the receivers
	 */
	// private final Queue<DatapoolMessage> receiveQueue = new
	// ConcurrentLinkedQueue<DatapoolMessage>( );
	private final DatapoolQueue receiveQueue = new DatapoolQueue( );
	/**
	 * Current data for each key
	 */
	private FastMap<String, Object> data = new FastMap<String, Object>( ).setShared(true);

	/**
	 * Threadpool used for distributing received data
	 */
	private ExecutorService threadPool = Executors.newCachedThreadPool( );

	/**
	 * Flag showing if the datapool is stopped. If this is the case
	 * all running threads should also stop
	 */
	private boolean stopped = false;

	/**
	 * Constructs a new datapool for the given robot, initializes
	 * threads and listeners.<br/><br/> The constructor should only
	 * be called by the {@link DatapoolManager}
	 * 
	 * @param robot
	 *            the robot to create the datapool for
	 */
	protected Datapool(Robot robot) {
		this.robot = robot;
		startThreads( );
		initListeners( );
	}

	/**
	 * Initializes all robot dependant listeners for this datapool
	 * which might give important event.<br/><br/> - On disconnect
	 * all queues and data for this robot is cleared
	 */
	private void initListeners() {
		CommunicationManager.addConnectionListener(new ConnectionListener( ) {

			@Override
			public void connectionEstablished(@SuppressWarnings("unused")
			ConnectionEvent event) {
				// not used
			}

			@Override
			public void disconnected(ConnectionEvent event) {
				if (event.getRobotName( ).equals(robot.getName( ))) {
					sendQueue.clear( );
					receiveQueue.clear( );
					data.clear( );
				}
			}

		});
	}

	/**
	 * Checks if the threads should keep running
	 * 
	 * @return <code>true</code> if the datapool is not stopped and
	 *         the main shell is not disposed
	 */
	private boolean keepThreadRunning() {
		return !stopped
				&& !Application.getApplication( ).getShell( ).isDisposed( );
	}

	/**
	 * Starts all threads of the datapool. Currently these are the
	 * send and receive thread which work on the send and receive
	 * queues and will stop when the datapool itself is stopped
	 */
	private void startThreads() {
		Thread sendThread = new Thread("DatapoolSend_" + robot.getName( )) { //$NON-NLS-1$

			@Override
			public void run() {
				while (keepThreadRunning( )) {
					// get the next message to send
					DatapoolMessage sendMsg = sendQueue.poll( );
					if (sendMsg != null && handler != null) {
						// Send over Robot and fire the event
						handler.sendToRobot(sendMsg);
						fireSendEvent(new DatapoolEvent(this,
								robot,
								sendMsg.getTimestamp( ),
								sendMsg.getKey( ),
								sendMsg.getValue( )));
					}
					try {
						Thread.sleep(5);
					}
					catch (InterruptedException e) {
						// do nothing
					}
				}
			}
		};
		sendThread.start( );
		Thread receiveThread = new Thread("DatapoolReceive_" + robot.getName( )) { //$NON-NLS-1$

			@Override
			public void run() {
				while (keepThreadRunning( )) {
					// Get the next receive message
					DatapoolMessage receiveMsg = receiveQueue.poll( );
					if (receiveMsg != null) {
						// get the old data for this key and
						// check if the current data is really new
						Object aux = data.put(receiveMsg.getKey( ),
								receiveMsg.getValue( ));

						boolean changed = aux == null
								|| !aux.equals(receiveMsg.getValue( ));

						// fire the event to the listeners
						fireReceiveEvent(new DatapoolEvent(this,
								robot,
								receiveMsg.getTimestamp( ),
								receiveMsg.getKey( ),
								receiveMsg.getValue( )), changed);

					}
					try {
						Thread.sleep(5);
					}
					catch (InterruptedException e) {
						// do nothing
					}
				}
			}
		};
		receiveThread.start( );

	}

	/**
	 * Adds the given message to a queue of messages which are send to
	 * a robot within a thread.
	 * 
	 * @param message
	 *            the message to send
	 * @return <code>false</code> if there was no more space in the
	 *         send queue or there was no handler available
	 */
	public boolean sendToRobot(DatapoolMessage message) {
		boolean ableToSend = false;
		if (handler != null) {
			ableToSend = sendQueue.offer(message);
			if (!ableToSend) {
				logClass.warn(robot.getName( ),
						I18n.getString("Datapool.log.failedToSend") //$NON-NLS-1$
								+ ToStringBuilder.reflectionToString(message)
								+ I18n.getString("Datapool.log.reasonCouldNotOffer") //$NON-NLS-1$
								+ Constants.LINE_SEPARATOR);
			}
		}
		else {
			logClass.warn(robot.getName( ),
					I18n.getString("Datapool.log.failedToSend") + ToStringBuilder.reflectionToString(message) //$NON-NLS-1$
							+ I18n.getString("Datapool.log.reasonNoHandler") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
		}
		return ableToSend;

	}

	/**
	 * Adds the given message to a queue of messages which were
	 * received from the robot and should be distributed to the
	 * registered listeners
	 * 
	 * @param message
	 *            the message which was received
	 * @return <code>false</code> if the message or it's value was
	 *         <code>null</code><br>
	 *         <code>true</code> if the message was successfully
	 *         added to the queue and will be distributed to the
	 *         listeners
	 */
	public boolean receiveToDatapool(DatapoolMessage message) {
		if (message == null || message.getValue( ) == null) {
			logClass.warn(robot.getName( ),
					I18n.getString("Datapool.log.failedToReceiveMessageNull") //$NON-NLS-1$
							+ Constants.LINE_SEPARATOR);
			return false;
		}

		boolean ableToReceive = receiveQueue.offer(message);
		if (!ableToReceive) {
			logClass.warn(robot.getName( ),
					I18n.getString("Datapool.log.failedToReceive") //$NON-NLS-1$
							+ message
							+ I18n.getString("Datapool.log.reasonCouldNotOffer") //$NON-NLS-1$
							+ Constants.LINE_SEPARATOR);
		}
		return ableToReceive;

	}

	/**
	 * Sets the handler which is used by this datapool for sending
	 * messages to the robot</br> This method is called automatically
	 * when connecting to the robot, and must not be called explictly
	 * 
	 * @param handler
	 *            the handler to set
	 */
	public void setHandler(IHandler handler) {
		this.handler = handler;
	}

	/**
	 * Get's the handler of the datapools robot
	 * 
	 * @return the handler for the robot or <code>null</code> if the
	 *         handler was not set yet, f.e. if no connection to the
	 *         robot was ever established
	 */
	public IHandler getHandler() {
		return handler;
	}

	/**
	 * Distributes the given event to all listeners which are
	 * registered to the events key or to the robot in an own thread
	 * 
	 * @param event
	 *            the event to distribute
	 */
	private void fireSendEvent(final DatapoolEvent event) {
		threadPool.execute(new Runnable( ) {

			public void run() {
				for (DatapoolListener listener : sendListeners.get(event.getKey( ))) {
					listener.valueChanged(event);
				}
				for (DatapoolListener listener : sendListeners.get(ROBOT_KEY)) {
					listener.valueChanged(event);
				}
			}

		});

	}

	/**
	 * Distributes the given event to all listeners which are
	 * registered to the events key or to the robot in an own thread
	 * 
	 * @param event
	 *            the event to distribute
	 * @param changed
	 *            listeners which like to be notified only when data
	 *            has changed are skipped is this flag is
	 *            <code>false</code>
	 */
	private void fireReceiveEvent(final DatapoolEvent event,
			final boolean changed) {
		threadPool.execute(new Runnable( ) {

			public void run() {
				for (DatapoolListener listener : recvListeners.get(event.getKey( ))) {
					if (listener.notifyOnlyWhenChanged( )) {
						if (changed) {
							listener.valueChanged(event);
						}
					}
					else {
						listener.valueChanged(event);
					}
				}
				for (DatapoolListener listener : recvListeners.get(ROBOT_KEY)) {
					if (listener.notifyOnlyWhenChanged( )) {
						if (changed) {
							listener.valueChanged(event);
						}
					}
					else {
						listener.valueChanged(event);
					}
				}
			}
		});
	}

	/**
	 * Gets the received value for the given key from the datapool
	 * 
	 * @param key
	 *            the key to get the value for
	 * @return the value or <code>null</code> if no value for the
	 *         given key exists
	 */
	public Object getValue(String key) {
		return data.get(key);
	}

	/**
	 * Adds the given listeners to the list of listeners which are
	 * notified if data for the given key is received from the robot
	 * 
	 * @param key
	 *            the key to register on
	 * @param listener
	 *            the listener to register
	 * @see #addRobotReceiveListener(DatapoolListener)
	 */
	public void addDatapoolReceiveListener(String key, DatapoolListener listener) {
		if (key != null && listener != null) {
			recvListeners.put(key, listener);
		}
	}

	/**
	 * Adds the given listeners to the list of listeners which are
	 * notified if data is received from the robot
	 * 
	 * @param listener
	 *            the listener to register
	 * @see #addRobotReceiveListener(DatapoolListener)
	 */
	public void addRobotReceiveListener(DatapoolListener listener) {
		addDatapoolReceiveListener(ROBOT_KEY, listener);
	}

	/**
	 * Removes the given listeners from the list of listeners which
	 * are notified if data for the given key is received from the
	 * robot
	 * 
	 * @param key
	 *            the key to deregister from
	 * @param listener
	 *            the listener to deregister
	 * @return <code>true</code> if the listener was removed or the
	 *         key or listener given were <code>null</code>
	 * @see #removeRobotReceiveListener(DatapoolListener)
	 */
	public boolean removeDatapoolReceiveListener(String key,
			DatapoolListener listener) {
		if (key != null && listener != null) {
			return recvListeners.remove(key, listener);
		}
		return true;
	}

	/**
	 * Removes the given listeners from the list of listeners which
	 * are notified if data is received from the robot
	 * 
	 * @param listener
	 *            the listener to deregister
	 * @return <code>true</code> if the listener was removed or the
	 *         key or listener given were <code>null</code>
	 * @see #removeRobotReceiveListener(DatapoolListener)
	 */
	public boolean removeRobotReceiveListener(DatapoolListener listener) {
		return removeDatapoolReceiveListener(ROBOT_KEY, listener);
	}

	/**
	 * Removes every occurrence of the given listeners from the list
	 * of listeners which are notified if data is received from the
	 * robot
	 * 
	 * @param listener
	 *            the listener to deregister
	 * @see #removeRobotReceiveListener(DatapoolListener)
	 */
	public void removeDatapoolReceiveListener(DatapoolListener listener) {
		List<String> keys = new ArrayList<String>( );

		for (Entry<String, FastTable<DatapoolListener>> e : recvListeners.entrySet( )) {
			for (DatapoolListener dl : e.getValue( )) {
				if (dl.equals(listener)) {
					keys.add(e.getKey( ));
				}
			}
		}

		for (String key : keys) {
			removeDatapoolReceiveListener(key, listener);
		}
	}

	/**
	 * Adds the given listeners to the list of listeners which are
	 * notified if data for the given key is send to the robot
	 * 
	 * @param key
	 *            the key to register on
	 * @param listener
	 *            the listener to register
	 * @see #addRobotSendListener(DatapoolListener)
	 */
	public void addDatapoolSendListener(String key, DatapoolListener listener) {
		if (key != null && listener != null) {
			sendListeners.put(key, listener);
		}
	}

	/**
	 * Adds the given listeners to the list of listeners which are
	 * notified if data is send to the robot
	 * 
	 * @param listener
	 *            the listener to register
	 * @see #addRobotSendListener(DatapoolListener)
	 */
	public void addRobotSendListener(DatapoolListener listener) {
		addDatapoolSendListener(ROBOT_KEY, listener);
	}

	/**
	 * Removes the given listeners from the list of listeners which
	 * are notified if data for the given key is received from the
	 * robot
	 * 
	 * @param key
	 *            the key to deregister from
	 * @param listener
	 *            the listener to deregister
	 * @return <code>true</code> if the listener was removed or the
	 *         key or listener given were <code>null</code>
	 * @see #removeRobotReceiveListener(DatapoolListener)
	 */
	public boolean removeDatapoolSendListener(String key,
			DatapoolListener listener) {
		if (key != null && listener != null) {
			return sendListeners.remove(key, listener);
		}
		return true;
	}

	/**
	 * Removes the given listeners from the list of listeners which
	 * are notified if data is received from the robot
	 * 
	 * @param listener
	 *            the listener to deregister
	 * @return <code>true</code> if the listener was removed or the
	 *         key or listener given were <code>null</code>
	 * @see #removeRobotReceiveListener(DatapoolListener)
	 */
	public boolean removeRobotSendListener(DatapoolListener listener) {
		return removeDatapoolSendListener(ROBOT_KEY, listener);
	}

	/**
	 * Removes every occurrence of the given listeners from the list
	 * of listeners which are notified if data is received from the
	 * robot
	 * 
	 * @param listener
	 *            the listener to deregister
	 * @see #removeRobotReceiveListener(DatapoolListener)
	 */
	public void removeDatapoolSendListener(DatapoolListener listener) {
		List<String> keys = new ArrayList<String>( );

		for (Entry<String, FastTable<DatapoolListener>> e : sendListeners.entrySet( )) {
			for (DatapoolListener dl : e.getValue( )) {
				if (dl.equals(listener)) {
					keys.add(e.getKey( ));
				}
			}
		}

		for (String key : keys) {
			removeDatapoolSendListener(key, listener);
		}
	}

	/**
	 * Stops this datapool: Shuts down all running threads of this
	 * datapool.<br/><br/> This method is called when stopping the
	 * datapool manager. This datapool may not be started again after
	 * calling this method.
	 */
	protected void stop() {
		this.threadPool.shutdown( );
		stopped = true;
	}
}
