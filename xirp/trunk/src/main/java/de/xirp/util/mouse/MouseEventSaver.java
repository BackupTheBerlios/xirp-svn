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
 * MouseEventSaver.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 20.09.2006:		Created by Rabea Gransberger.
 */
package de.xirp.util.mouse;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import de.xirp.managers.AbstractManager;
import de.xirp.managers.ManagerException;
import de.xirp.util.Constants;
import de.xirp.util.FutureRelease;

/**
 * Manager which may save mouse events occurred on the UI to be able
 * to replay them again.<br>
 * <br>
 * This is alpha API, may be removed in the future or is planed to be
 * integrated in version 3.0.0.
 * 
 * @author Rabea Gransberger
 */
@FutureRelease(version = "3.0.0")
public class MouseEventSaver extends AbstractManager {

	/**
	 * The Logger for this class.
	 */
	private static Logger logClass = Logger.getLogger(MouseEventSaver.class);

	/**
	 * Time of the last mouse event
	 */
	private static long lastEvent = System.currentTimeMillis( );
	/**
	 * List of mouse events which were monitored
	 */
	private static ArrayList<PastMouseEvent> events = new ArrayList<PastMouseEvent>( );

	/**
	 * The path to the file in which the events should be persisted.
	 */
	protected static final String FILE_NAME = Constants.CONF_DIR +
			Constants.FS + "test.dat"; //$NON-NLS-1$

	/**
	 * <code>true</code> if mouse events should be saved
	 */
	private static boolean activated = false;

	/**
	 * Creates a new manager which may save mouse events which
	 * occurred on the UI to be able to replay them. The manager is
	 * initialized on startup. Never call this on your own. Use the
	 * statically provided methods.
	 * 
	 * @throws InstantiationException
	 *             if an instance of this manager does already exist
	 */
	public MouseEventSaver() throws InstantiationException {
		super( );
	}

	/**
	 * @see de.xirp.managers.AbstractManager#start()
	 */
	@Override
	protected void start() throws ManagerException {
		super.start( );
		lastEvent = System.currentTimeMillis( );
	}

	/**
	 * @see de.xirp.managers.AbstractManager#stop()
	 */
	@Override
	protected void stop() throws ManagerException {
		super.stop( );
		save( );
	}

	/**
	 * Deactivates monitoring of mouse events.
	 */
	public static void deactivate() {
		activated = false;
	}

	/**
	 * Activates monitoring of mouse events.
	 */
	public static void activate() {
		activated = true;
	}

	/**
	 * Saves the events to a file which is loaded by the
	 * {@link MouseEventSimulator#doReplay()} method for replay.
	 */
	public static void save() {
		if (!events.isEmpty( )) {
//			try {
//				ObjectSerializer.<ArrayList<PastMouseEvent>> writeToDisk(events,
//						new File(FILE_NAME));
//			}
//			catch (IOException e) {
//				logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
//						+ Constants.LINE_SEPARATOR, e);
//			}
		}
	}

	/**
	 * Gets the delta between the last event and the current and sets
	 * the time of the last event to the current.
	 * 
	 * @return the delta between last and current event in
	 *         milliseconds.
	 */
	private static long getTimeDiff() {
		long time = System.currentTimeMillis( );
		long delta = time - lastEvent;
		lastEvent = time;
		return delta;
	}

	/**
	 * Monitors a mouse click event with the default left mouse
	 * button. The event is monitored only if monitoring is active.
	 */
	public static void monitorClick() {
		monitorClick(1);
	}

	/**
	 * Monitors a mouse click event with the given mouse button. The
	 * event is monitored only if monitoring is active.
	 * 
	 * @param button
	 *            The mouse button read from a mouse event f.e.
	 */
	public static void monitorClick(int button) {
		if (activated) {
			Point cursorLocation = Display.getCurrent( ).getCursorLocation( );
			long timeDiff = getTimeDiff( );

			events.add(new PastMouseEvent(timeDiff, cursorLocation, button));
		}
	}

	/**
	 * Monitors a mouse move event with to the current location of the
	 * mouse cursor. The event is monitored only if monitoring is
	 * active.
	 */
	public static void monitorMove() {
		if (activated) {
			Point cursorLocation = Display.getCurrent( ).getCursorLocation( );
			long timeDiff = getTimeDiff( );

			events.add(new PastMouseEvent(timeDiff, cursorLocation));
		}
	}

}
