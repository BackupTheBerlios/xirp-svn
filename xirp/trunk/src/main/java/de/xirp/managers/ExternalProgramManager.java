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
 * ExternalProgramManager.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 20.02.2007:		Created by Rabea Gransberger.
 */
package de.xirp.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.xirp.util.Constants;

/**
 * Manager which allows to start external programs from the file
 * system.
 * 
 * @author Rabea Gransberger
 */
public final class ExternalProgramManager extends AbstractManager {

	/**
	 * The log4j Logger of this Class
	 */
	private static final Logger logClass = Logger.getLogger(ExternalProgramManager.class);
	/**
	 * List of all running external programs
	 */
	private static List<ExternalProgram> running = new ArrayList<ExternalProgram>( );

	/**
	 * Constructs a new external program manager. The manager is
	 * initialized on startup. Never call this on your own. Use the
	 * statically provided methods.
	 * 
	 * @throws InstantiationException
	 *             if an instance of this manager does already exist
	 */
	protected ExternalProgramManager() throws InstantiationException {
		super( );
	}

	/**
	 * Starts the given external program.
	 * 
	 * @param prog
	 *            the program to start
	 * @return <code>true</code> if the program was started
	 */
	public static boolean start(ExternalProgram prog) {
		String fullProgramPath = prog.getWorkingDir( ) + File.separator +
				prog.getExecutable( );
		ProcessBuilder proc = new ProcessBuilder(fullProgramPath,
				prog.getArgs( ));
		proc.directory(new File(prog.getWorkingDir( )));
		try {
			Process process = proc.start( );
			prog.setProc(process);
			running.add(prog);
			return true;
		}
		catch (IOException ex) {
			logClass.error("Error: " + ex.getMessage( ) + Constants.LINE_SEPARATOR, ex); //$NON-NLS-1$
			return false;
		}
	}

	/**
	 * Starts the given list of programs in the given order.<br/>
	 * After every program start the startup Thread waits for the
	 * number of milliseconds the program needs for startup before
	 * starting the next program.
	 * 
	 * @param progs
	 *            the programs to start
	 */
	public static void start(final List<ExternalProgram> progs) {
		Thread startUp = new Thread("ExternalProgramStartUp") { //$NON-NLS-1$

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() {
				startList(progs);
			}

		};
		startUp.start( );
	}

	/**
	 * Starts the given list of programs in the given order.<br/>
	 * After every program start the startup Thread waits for the
	 * number of milliseconds the program needs for startup before
	 * starting the next program.
	 * 
	 * @param progs
	 *            the programs to start
	 * @return <code>true</code> if all programs were started
	 *         successfully
	 */
	private static boolean startList(List<ExternalProgram> progs) {
		boolean b = true;
		for (ExternalProgram ext : progs) {
			b = start(ext) && b;
			try {
				Thread.sleep(ext.getStartupTime( ));
			}
			catch (InterruptedException e) {
				logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
			}
		}
		return b;
	}

	/**
	 * @see de.xirp.managers.AbstractManager#start()
	 */
	@Override
	protected void start() throws ManagerException {
		super.start( );
	}

	/**
	 * Terminates all external programs which were started by this
	 * manager and stops the manager itself.
	 * 
	 * @see de.xirp.managers.AbstractManager#stop()
	 */
	@Override
	protected void stop() throws ManagerException {
		super.stop( );
		for (ExternalProgram proc : running) {
			if (proc.isRunning( )) {
				proc.destroy( );
				try {
					proc.waitFor( );
				}
				catch (InterruptedException e) {
					logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
							+ Constants.LINE_SEPARATOR, e);
				}
			}
			try {
				logClass.debug("Destroyed external program " + proc.getName( ) + ". Exit code: " + proc.exitValue( ) + Constants.LINE_SEPARATOR); //$NON-NLS-1$ //$NON-NLS-2$
			}
			catch (Exception e) {
				throw new ManagerException(e);
			}
		}
	}

}
