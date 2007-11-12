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
 * AbstractStreamCommunicationInterface.java
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
package de.xirp.io.comm.lowlevel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import javax.naming.InvalidNameException;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.io.output.CountingOutputStream;

import de.xirp.io.comm.CommunicationManager;
import de.xirp.io.comm.DataAmount;
import de.xirp.io.event.ConnectionEvent;
import de.xirp.io.logging.RobotLogger;
import de.xirp.plugin.PluginInfo;
import de.xirp.util.Constants;
import de.xirp.util.I18n;

/**
 * An abstract default implementation for communication interface
 * which send <code>byte []</code> and receives over a
 * <code>InputStream</code>
 * 
 * @author Matthias Gernand
 * @author Rabea Gransberger
 */
public abstract class AbstractStreamCommunicationInterface extends
		AbstractCommunicationInterface<byte[], InputStream> {

	/**
	 * Log4j Logger for this class
	 */
	private static RobotLogger logClass = RobotLogger.getLogger(AbstractStreamCommunicationInterface.class);

	/**
	 * <code>true</code> if connection to device established
	 */
	protected boolean connected = false;

	/**
	 * Time in milliseconds between two datums send
	 */
	protected int SEND_SLEEP_TIME = 15;

	/**
	 * Map which saves the sending status for each registered
	 * periodically sending thread. If value is false, the thread does
	 * not do anything. This is needed because there is no Thread.stop
	 * method available.
	 */
	protected FastMap<String, Timer> periodicSenders = new FastMap<String, Timer>( ).setShared(true);
	/**
	 * Stream of data sent from the robot to the GUI
	 */
	protected CountingInputStream in = null;
	/**
	 * Stream of data sent by GUI to the robot
	 */
	protected CountingOutputStream out = null;
	/**
	 * Flag which holds the sending status of this communication
	 * device.<br>
	 * <code>true</code> if sending thread is active,
	 * <code>false</code> otherwise
	 */
	protected boolean sending = false;
	/**
	 * Data is not send directly over the communication device. It's
	 * stored in this structure and send by a Thread to be sure that
	 * each data-fragment is sent even if the program is busy at the
	 * moment the data originally should be sent
	 */
	private FastList<byte[]> toSend = new FastList<byte[]>( );

	/**
	 * Constructs a new communication interface plugin for the given
	 * robot and information about the plugin itself.<br/><br/>Don't
	 * call it on your own, it is called when a connection to the
	 * robot is established.
	 * 
	 * @param robotName
	 *            the name of the robot this handler is for
	 * @param ownInfo
	 *            information about this plugin
	 */
	public AbstractStreamCommunicationInterface(String robotName,
			PluginInfo ownInfo) {
		super(robotName, ownInfo);
	}

	/**
	 * @see de.xirp.io.comm.lowlevel.ICommunicationInterface#isConnected()
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Sends data periodically.
	 * 
	 * @param name
	 *            the name to use for the send timer
	 * @param time
	 *            the time interval in milliseconds between two sends
	 * @param data
	 *            the data to send
	 * @throws InvalidNameException
	 *             if the name is already in use
	 */
	public void sendPeriodically(String name, int time, byte[] data)
			throws InvalidNameException {

		if (periodicSenders.get(name) != null) {
			throw new InvalidNameException(I18n.getString("AbstractCommunication.log.alreadyExcecutingThread") + name); //$NON-NLS-1$
		}

		Timer timer = new Timer(name);
		timer.schedule(getSendPeriodicTask(data), 0, time);
		periodicSenders.put(name, timer);
	}

	/**
	 * Constructs a timer task which will send the given data array.
	 * 
	 * @param data
	 *            byte array which will be send
	 * @return the task to use within a timer
	 */
	private TimerTask getSendPeriodicTask(final byte[] data) {
		TimerTask task = new TimerTask( ) {

			@Override
			public void run() {
				if (connected) {
					send(data);
				}
			}
		};
		return task;
	}

	/**
	 * Stops the periodic sending for the given tasks name.
	 * 
	 * @param name
	 *            the name of the task to stop
	 * @return <code>true</code> if the task was found and stopped,
	 *         <code>false</code> otherwise
	 */
	public boolean stopSendPeriodically(String name) {
		Timer timer = periodicSenders.get(name);
		if (timer != null) {
			timer.cancel( );
			periodicSenders.remove(name);
			return true;
		}
		return false;
	}

	/**
	 * Stops to send data.
	 * 
	 * @return <code>true</code> if sending was stopped
	 */
	private boolean stopSending() {
		// Disable periodical sending
		for (FastMap.Entry<String, Timer> e = periodicSenders.head( ), end = periodicSenders.tail( ); (e = e.getNext( )) != end;) {
			e.getValue( ).cancel( );
		}
		periodicSenders.clear( );

		// do not send old data after a reconnect
		toSend.clear( );
		sending = false;
		return true;
	}

	/**
	 * Starts the thread which sends data from the GUI to the robot.<br/>
	 * The data which should be send is read from an internal
	 * structure. The GUI writes the data to this structure by calling
	 * {@link #send(byte[])}. Furthermore a thread fires bytes
	 * send/receive events from time to time.
	 */
	protected void startSend() {
		if (sending) {
			return;
		}
		sending = true;

		Thread sendReceive = new Thread(robotName + "_SendRecvStats") { //$NON-NLS-1$

			@Override
			public void run() {
				while (sending && isConnected( )) {
					int inCnt = in.getCount( );
					int outCnt = out.getCount( );
					// check if the stream is still there
					try {
						in.available( );
					}
					catch (IOException e) {
						CommunicationManager.disconnect(getRobotName( ));
						logClass.debug(getRobotName( ), e.getMessage( ) +
								Constants.LINE_SEPARATOR, e);
					}

					in.resetCount( );
					out.resetCount( );

					fireByteReceivedEvent(new ByteEvent(AbstractStreamCommunicationInterface.this,
							new DataAmount(inCnt)));
					fireByteSendEvent(new ByteEvent(AbstractStreamCommunicationInterface.this,
							new DataAmount(outCnt)));
					try {
						Thread.sleep(250);
					}
					catch (InterruptedException e) {
						// do nothing
					}

				}
			}
		};
		sendReceive.start( );

		Thread thread = new Thread(robotName + "_SendThread") { //$NON-NLS-1$

			@Override
			public void run() {
				// Run the Thread until receiving
				// was disabled or Comm disconnected
				while (sending && isConnected( )) {
					try {
						if (!toSend.isEmpty( )) {
							byte[] value = toSend.removeFirst( );

							if (value != null) {
								out.write(value);
							}
						}
					}
					catch (IOException e) {
						logClass.error(robotName,
								I18n.getString("AbstractCommunication.log.errorSendingOver", //$NON-NLS-1$
										getClass( ).getSimpleName( ),
										e.getMessage( )) +
										Constants.LINE_SEPARATOR,
								e);
					}
					// Need to sleep so that
					// other threads can run
					try {
						sleep(SEND_SLEEP_TIME);
					}
					catch (Exception e) {
						// do nothing
					}
				}
			}
		};
		// Start Receiving Thread
		thread.start( );
		logClass.debug(robotName,
				I18n.getString("AbstractCommunication.log.runningSending") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
	}

	/**
	 * @param value
	 *            the byte array to send
	 * @see de.xirp.io.comm.lowlevel.ICommunicationInterface#send(java.lang.Object)
	 */
	public void send(byte[] value) {
		if (connected) {
			toSend.add(value);
		}
	}

	/**
	 * Disconnects from the interface, stops sending, closes the
	 * streams and fires a disconnect event.
	 * 
	 * @see de.xirp.io.comm.lowlevel.ICommunicationInterface#disconnect()
	 */
	@SuppressWarnings("unchecked")
	public void disconnect() {
		try {
			if (connected) {
				stopSending( );
				in.close( );
				out.close( );
				connected = false;
				this.fireDisconnectEvent(new ConnectionEvent(this,
						this.robotName));
				logClass.info(robotName,
						I18n.getString("AbstractCommunication.log.successfullyDisconnected", getClass( ) //$NON-NLS-1$
								.getSimpleName( )) +
								Constants.LINE_SEPARATOR);
			}
			running = false;
		}
		catch (IOException e) {
			logClass.error(robotName,
					I18n.getString("AbstractCommunication.log.errorDisconnecting", getClass( ) //$NON-NLS-1$
							.getSimpleName( ),
							e.getMessage( )) +
							Constants.LINE_SEPARATOR,
					e);
		}
	}

	/**
	 * Gets the input stream which is used for receiving data.
	 * 
	 * @return the input stream used for receiving data
	 * @see de.xirp.io.comm.lowlevel.ICommunicationInterface#receive()
	 */
	public InputStream receive() {
		return in;
	}

}
