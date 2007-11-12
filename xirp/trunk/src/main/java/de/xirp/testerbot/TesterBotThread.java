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
 * TesterBotThread.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 01.04.2007:		Created by Matthias Gernand.
 */
package de.xirp.testerbot;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.output.CountingOutputStream;
import org.apache.log4j.Logger;

import de.xirp.io.comm.DataAmount;
import de.xirp.util.Constants;

/**
 * This thread is invoked when a client connects to the testerbot
 * server. For each connected client exists one running thread.
 * 
 * @author Matthias Gernand
 */
public class TesterBotThread extends Thread {

	/**
	 * The logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(TesterBotThread.class);
	/**
	 * The time in milliseconds of a "run".
	 */
	private static final long RUN_TIME = TimeUnit.MINUTES.toMillis(15);
	/**
	 * Indicates it the thread can run or not.
	 */
	private boolean canRun = false;
	/**
	 * The output stream.
	 */
	private OutputStream out;
	/**
	 * A output steam counting the bytes sent.
	 */
	private CountingOutputStream cnt;
	/**
	 * The object output stream.
	 */
	private ObjectOutputStream oos;
	/**
	 * A package to send.
	 */
	private TesterBotPackage pack;
	/**
	 * Sleep time between the sending of two packages.
	 */
	private long sleep = 250;
	/**
	 * The client socket.
	 */
	private Socket client;
	/**
	 * The starting time in milliseconds.
	 */
	private long startingTime;
	/**
	 * A map holding the value
	 * {@link de.xirp.testerbot.AbstractValueCreator creators}.
	 * 
	 * @see de.xirp.testerbot.AbstractValueCreator
	 */
	private Map<String, AbstractValueCreator> values = new HashMap<String, AbstractValueCreator>( );
	/**
	 * The server instance.
	 */
	private TesterBot bot;
	/**
	 * The number of send packages.
	 */
	protected static long packs = 0;
	/**
	 * The sent {@link de.xirp.io.comm.DataAmount amount}
	 * of data.
	 * 
	 * @see de.xirp.io.comm.DataAmount
	 */
	protected static DataAmount amount = new DataAmount( );

	/**
	 * Constructs a new thread in the server for the given client
	 * socket and server instance.
	 * 
	 * @param bot
	 *            The server instance.
	 * @param client
	 *            The client socket.
	 */
	public TesterBotThread(TesterBot bot, Socket client) {
		super( );
		this.canRun = true;
		this.client = client;
		this.bot = bot;
		init( );
	}

	/**
	 * Initializes the value
	 * {@link de.xirp.testerbot.AbstractValueCreator creators}.
	 * 
	 * @see de.xirp.testerbot.AbstractValueCreator
	 */
	private void init() {
		values.put(TesterBotPackage.BATTERY_1, new TesterBotBattery(RUN_TIME));
		values.put(TesterBotPackage.BATTERY_2, new TesterBotBattery(RUN_TIME));
		values.put(TesterBotPackage.COMPASS_1, new TesterBotCompass(RUN_TIME));
		values.put(TesterBotPackage.GYRO_1, new TesterBotGyro(RUN_TIME, true));
		values.put(TesterBotPackage.GYRO_2, new TesterBotGyro(RUN_TIME, false));
		values.put(TesterBotPackage.SPEED_1, new TesterBotSpeed(RUN_TIME));
		values.put(TesterBotPackage.TEMPERATURE_1,
				new TesterBotTemperature(RUN_TIME));
		values.put(TesterBotPackage.IR_1, new TesterBotIr(RUN_TIME, 1));
		values.put(TesterBotPackage.IR_2, new TesterBotIr(RUN_TIME, 2));
		values.put(TesterBotPackage.IR_3, new TesterBotIr(RUN_TIME, 3));
		values.put(TesterBotPackage.IR_4, new TesterBotIr(RUN_TIME, 4));
		values.put(TesterBotPackage.SONIC_1, new TesterBotSonic(RUN_TIME, 1));
		values.put(TesterBotPackage.SONIC_2, new TesterBotSonic(RUN_TIME, 2));
		values.put(TesterBotPackage.SONIC_3, new TesterBotSonic(RUN_TIME, 3));
		values.put(TesterBotPackage.SONIC_4, new TesterBotSonic(RUN_TIME, 4));
		values.put(TesterBotPackage.LASER_1, new TesterBotLaser(RUN_TIME));
		values.put(TesterBotPackage.THERMO_1, new TesterBotThermopile(RUN_TIME));
		values.put(TesterBotPackage.CO2_1, new TesterBotCO2(RUN_TIME));
		values.put(TesterBotPackage.MESSAGE_1, new TesterBotMessage(RUN_TIME));
	}

	/**
	 * The thread runs as long as the {@link #canRun} flag is
	 * <code>true</code>.
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		startingTime = System.currentTimeMillis( );
		try {
			out = client.getOutputStream( );
			cnt = new CountingOutputStream(out);
			oos = new ObjectOutputStream(cnt);
			while (canRun) {
				long elapsedTime = System.currentTimeMillis( ) - startingTime;
				if (elapsedTime > RUN_TIME) {
					startingTime = System.currentTimeMillis( );
					elapsedTime = System.currentTimeMillis( ) - startingTime;
				}
				pack = new TesterBotPackage( );
				for (Entry<String, AbstractValueCreator> e : values.entrySet( )) {
					pack.set(e.getKey( ), e.getValue( ).calculate(elapsedTime));
				}
				oos.writeObject(pack);
				oos.flush( );
				packs++;
				amount.add(cnt.getCount( ));
				cnt.resetCount( );
				Thread.sleep(sleep);
			}
		}
		catch (Exception e) {
			halt( );
		}
	}

	/**
	 * The thread is halted by setting {@link #canRun} to
	 * <code>false</code>. The thread is remove from the thread
	 * list and the client socket is closed.
	 */
	protected void halt() {
		this.canRun = false;
		try {
			bot.clientCounter--;
			bot.threadList.remove(this);
			client.close( );
		}
		catch (IOException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
	}

	/**
	 * Returns the number of currently send packages.
	 * 
	 * @return The number of packages.
	 */
	public long getPacksSend() {
		return packs;
	}

	/**
	 * Returns the
	 * {@link de.xirp.io.comm.DataAmount amount} of
	 * currently send data.
	 * 
	 * @return The data amount.
	 * @see de.xirp.io.comm.DataAmount
	 */
	public DataAmount getDataAmount() {
		return amount;
	}
}
