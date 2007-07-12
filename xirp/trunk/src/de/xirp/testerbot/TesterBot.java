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
 * TesterBot.java
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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.xirp.io.comm.DataAmount;
import de.xirp.util.Constants;
import de.xirp.util.I18n;

/**
 * A simple server, serving random values for several keys.
 * This server can be used as load test tool and simple 
 * test tool for application testing.
 * 
 * @author Matthias Gernand
 *
 */
public class TesterBot {

	/**
	 * The logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(TesterBot.class);
	/**
	 * The server socket.
	 */
	private ServerSocket server;
	/**
	 * Flag indicating if the server is started.
	 */
	private boolean started = false;
	/**
	 * The running threads of the server.
	 */
	protected List<TesterBotThread> threadList = new ArrayList<TesterBotThread>( );
	/**
	 * The port number of the server.
	 */
	private short port;
	/**
	 * The number of clients.
	 */
	protected long clientCounter;

	/**
	 * Constructs a new server for the given port.
	 * 
	 * @param port
	 * 			The port to establish the server on.
	 */
	public TesterBot(short port) {
		this.port = port;
	}

	/**
	 * Starts the server.
	 * 
	 * @throws IOException if something went wrong starting the server.
	 */
	protected void startServer() throws IOException {
		logClass.info(I18n.getString("TesterBot.log.starting")); //$NON-NLS-1$
		server = new ServerSocket(port);
		started = true;
		clientCounter = 0;
		Thread t = new Thread( ) {

			/* (non-Javadoc)
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() {
				while (started) {
					try {
						Socket client = server.accept( );
						logClass.info(I18n.getString("TesterBot.log.newConnection", client.getInetAddress( ).getHostName( )) //$NON-NLS-1$
								+ Constants.LINE_SEPARATOR);
						TesterBotThread tbt = new TesterBotThread(TesterBot.this,
								client);
						threadList.add(tbt);
						tbt.start( );
						clientCounter++;
					}
					catch (IOException e) {
						logClass.warn("Warning: " + e.getMessage( ) //$NON-NLS-1$
								+ Constants.LINE_SEPARATOR, e);
					}
				}
			}

		};
		logClass.info(I18n.getString("TesterBot.log.completed") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
		logClass.info(I18n.getString("TesterBot.log.waitingForClients", port) + Constants.LINE_SEPARATOR); //$NON-NLS-1$ //$NON-NLS-2$
		t.start( );
	}

	/**
	 * Stops the server.
	 * 
	 * @throws IOException if something went wrong stopping the server.
	 */
	protected void stopServer() throws IOException {
		started = false;
		halt( );
		TesterBotThread.amount = new DataAmount( );
		TesterBotThread.packs = 0;
		threadList.clear( );
		server.close( );
		logClass.info(I18n.getString("TesterBot.log.stopped") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
	}

	/**
	 * Halts all running threads.
	 */
	private void halt() {
		List<TesterBotThread> list = new ArrayList<TesterBotThread>(threadList);
		for (TesterBotThread tbt : list) {
			tbt.halt( );
		}
	}

	/**
	 * Returns the number of connected clients.
	 * 
	 * @return The number of connected clients.
	 */
	protected synchronized long getClientNumber() {
		return clientCounter;
	}

	/**
	 * Returns the number of packages sent.
	 * 
	 * @return The number of packages.
	 */
	public synchronized long getPacksSend() {
		int packs = 0;
		for (TesterBotThread tbt : threadList) {
			packs += tbt.getPacksSend( );
		}
		return packs;
	}

	/**
	 * Returns the {@link de.xirp.io.comm.DataAmount amount}
	 * of sent data.
	 * 
	 * @return	The amount of sent data.
	 */
	public synchronized DataAmount getDataAmount() {
		DataAmount amount = new DataAmount( );
		for (TesterBotThread tbt : threadList) {
			amount.add(tbt.getDataAmount( ));
		}
		return amount;
	}

	/**
	 * Returns if the server is running or not.
	 * 
	 * @return A <code>boolean</code><br>
	 * 			<code>true</code>: if the server is running.<br>
	 * 			<code>false</code>: if the server is not running.<br>
	 */
	public synchronized boolean isRunning() {
		return started;
	}
}
