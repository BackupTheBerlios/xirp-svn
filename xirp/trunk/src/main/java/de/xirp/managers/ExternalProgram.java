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
 * ExternalProgram.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]		 
 * Contributor(s):   Matthias Gernand [matthias.gernand AT gmx.de]
 *
 * Changes
 * -------
 * 20.02.2007:		Created by Rabea Gransberger.
 * 20.04.2007:		Modified to fit new external tools configuration.
 */
package de.xirp.managers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import de.xirp.profile.Executable;
import de.xirp.util.Constants;

/**
 * Definition of an external program which may be started using the
 * {@link ExternalProgramManager}
 * 
 * @author Rabea Gransberger
 * @author Matthias Gernand
 */
public final class ExternalProgram {

	/**
	 * The log4j Logger of this Class
	 */
	private static Logger logClass = Logger.getLogger(ExternalProgram.class);

	/**
	 * The freely choosable name of the external program
	 */
	private String name;
	/**
	 * Absolute path to the directory in which the program should be
	 * executed
	 */
	private String workingDir;
	/**
	 * Absolute path to the executable file which is called
	 */
	private String executable;
	/**
	 * Arguments passed to the executable
	 */
	private String args;
	/**
	 * The process if the program is already running
	 */
	private Process proc;
	/**
	 * Flag if the program is already running
	 */
	private boolean isRunning = false;
	/**
	 * Flag if the output of the program should be logged
	 */
	private boolean logOutput = false;
	/**
	 * Time to wait after starting this program before a next program
	 * should be started
	 */
	private long startupTime;

	/**
	 * Creates a new configuration for an external program which could
	 * be started using the {@link ExternalProgramManager}.
	 * 
	 * @param name
	 *            The freely choosable name of the external program
	 * @param workingDir
	 *            Absolute path to the directory in which the program
	 *            should be executed
	 * @param executable
	 *            Absolute path to the executable file which is called
	 * @param args
	 *            Arguments passed to the executable
	 * @param startupTime
	 *            Time to wait after starting this program before a
	 *            next program should be started
	 */
	public ExternalProgram(String name, String workingDir, String executable,
			String args, long startupTime) {
		this.name = name;
		this.workingDir = workingDir;
		this.executable = executable;
		this.args = args;
		this.startupTime = startupTime;
	}

	/**
	 * Creates a new configuration for an external program which could
	 * be started using the {@link ExternalProgramManager} from the
	 * given executable object which could be read from the profile.
	 * 
	 * @param exe
	 *            the executable object with the information for the
	 *            external program
	 */
	public ExternalProgram(Executable exe) {
		this(exe.getName( ),
				exe.getWorkingDirectory( ),
				exe.getExecutableName( ),
				exe.getArguments( ),
				exe.getWaitTime( ));
	}

	/**
	 * Sets the running process for this external program.
	 * 
	 * @param process
	 *            the process to set
	 */
	protected void setProc(Process process) {
		this.proc = process;
		isRunning = true;
		// Does not work
		if (logOutput) {
			Thread t = new Thread( ) {

				/*
				 * (non-Javadoc)
				 * 
				 * @see java.lang.Thread#run()
				 */
				@Override
				public void run() {
					BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream( )));
					while (isRunning) {
						String line = null;
						try {
							while ((line = reader.readLine( )) != null) {
								logClass.debug("#######" + name + ": " + line); //$NON-NLS-1$ //$NON-NLS-2$
								try {
									Thread.sleep(50);
								}
								catch (InterruptedException e) {
									logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
								}
							}
						}
						catch (IOException e) {
							logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
						}
						try {
							Thread.sleep(50);
						}
						catch (InterruptedException e) {
							logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
						}
					}
				}

			};
			t.setPriority(Thread.MAX_PRIORITY);
			t.start( );

		}
	}

	/**
	 * Checks if this program is already running.
	 * 
	 * @return <code>true</code> if the program is running
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * Gets the arguments passed to the executable on startup.
	 * 
	 * @return the arguments for the executable
	 */
	public String getArgs() {
		return args;
	}

	/**
	 * Gets the absolute path to the executable file.
	 * 
	 * @return the executables path
	 */
	public String getExecutable() {
		return executable;
	}

	/**
	 * Gets the name of this program.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the absolute path to the directory in which this external
	 * program should be started.
	 * 
	 * @return the working directory
	 */
	public String getWorkingDir() {
		return workingDir;
	}

	/**
	 * Destroys the program if it's currently running.
	 * 
	 * @see java.lang.Process#destroy()
	 */
	public void destroy() {
		if (isRunning) {
			isRunning = false;
			proc.destroy( );
		}
	}

	/**
	 * Gets the exit value if the program has been run and is already
	 * stopped now.
	 * 
	 * @return the value the program returned on exit
	 * @throws Exception
	 *             thrown if the program is not running
	 * @see java.lang.Process#exitValue()
	 */
	public int exitValue() throws Exception {
		if (isRunning) {
			throw new IllegalThreadStateException( );
		}
		return proc.exitValue( );
	}

	/**
	 * If the program is running gets the stream to which errors are
	 * written.
	 * 
	 * @return the stream to which errors are written or
	 *         <code>null</code> if the program is not running
	 * @see java.lang.Process#getErrorStream()
	 */
	public InputStream getErrorStream() {
		if (isRunning) {
			return proc.getErrorStream( );
		}
		return null;
	}

	/**
	 * If the program is running gets the stream for sending commands
	 * to the program.
	 * 
	 * @return the input stream or <code>null</code> if the program
	 *         is not running
	 * @see java.lang.Process#getErrorStream()
	 */
	public InputStream getInputStream() {
		if (isRunning) {
			return proc.getInputStream( );
		}
		return null;
	}

	/**
	 * If the program is running gets the stream where the program
	 * writes output to.
	 * 
	 * @return the output stream or <code>null</code> if the program
	 *         is not running
	 * @see java.lang.Process#getErrorStream()
	 */
	public OutputStream getOutputStream() {
		if (isRunning) {
			return proc.getOutputStream( );
		}
		return null;
	}

	/**
	 * Gets the time the program will need to start in milliseconds.
	 * 
	 * @return the startup time
	 */
	public long getStartupTime() {
		return startupTime;
	}

	/**
	 * Waits for the termination of the running program.
	 * 
	 * @return the exit code of the program
	 * @throws InterruptedException
	 * @see java.lang.Process#waitFor()
	 */
	protected int waitFor() throws InterruptedException {
		if (proc != null) {
			return proc.waitFor( );
		}
		return -1;
	}
}
