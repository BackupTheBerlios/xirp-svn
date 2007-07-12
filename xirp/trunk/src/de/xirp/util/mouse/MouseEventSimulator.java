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
 * MouseEventSimulator.java
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

import de.xirp.ui.Application;
import de.xirp.util.Constants;
import de.xirp.util.FutureRelease;
import de.xirp.util.serialization.ObjectDeSerializer;
import de.xirp.util.serialization.SerializationException;

/**
 * Replays mouse events which were monitored and persisted by the
 * {@link MouseEventSaver}.<br>
 * <br>
 * This is alpha API, may be removed in the future or is planed to be
 * integrated in version 3.0.0.
 * 
 * @author Rabea Gransberger
 */
@FutureRelease(version = "3.0.0")
public class MouseEventSimulator {

	/**
	 * The Logger for this class.
	 */
	private static Logger logClass = Logger.getLogger(MouseEventSimulator.class);

	/**
	 * The list of events to simulate.
	 */
	private ArrayList<PastMouseEvent> events;

	/**
	 * Creates a new mouse event simulator which may replay the given
	 * list of events.
	 * 
	 * @param events
	 *            The events to replay
	 */
	private MouseEventSimulator(ArrayList<PastMouseEvent> events) {
		this.events = events;
	}

	/**
	 * Replays mouse events which were saved by the
	 * {@link MouseEventSaver}. During this time further monitoring
	 * of events is disabled.
	 */
	public static void doReplay() {
		try {
			ArrayList<PastMouseEvent> object = ObjectDeSerializer.<ArrayList<PastMouseEvent>> getObject(new File(MouseEventSaver.FILE_NAME));
			new MouseEventSimulator(object).execute( );
		}
		catch (IOException e) {
			logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
					+ Constants.LINE_SEPARATOR, e);
		}
		catch (SerializationException e) {
			logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
					+ Constants.LINE_SEPARATOR, e);
		}
	}

	/**
	 * Executes the replaying of the mouse events of this object in a
	 * thread.
	 */
	private void execute() {
		Thread thread = new Thread( ) {

			@Override
			public void run() {
				MouseEventSaver.deactivate( );
				int cnt = 0;
				while (cnt < events.size( )) {
					final PastMouseEvent event = events.get(cnt);
					try {
						sleep(event.getTime( ));
					}
					catch (InterruptedException e) {
						// do nothing
					}
					if (Application.getApplication( )
							.getShell( )
							.isFocusControl( )) {
						final Display display = Display.getDefault( );
						if (display != null) {
							display.syncExec(new Runnable( ) {

								public void run() {
									if (event.isMoveOnly( )) {
										move(display, event.getCoordinates( ));
									}
									else {
										click(display,
												event.getCoordinates( ),
												event.getButton( ));
									}
								}
							});
						}
						cnt++;
					}
				}
				MouseEventSaver.activate( );

			}
		};
		thread.start( );
	}

	/**
	 * Move the mouse to the given point.
	 * 
	 * @param display
	 *            the display on which the mouse should be moved.
	 * @param coord
	 *            the coordinates to which the mouse cursor should be
	 *            moved.
	 */
	private static void move(Display display, Point coord) {
		Event mouseMove = new Event( );
		mouseMove.x = coord.x;
		mouseMove.y = coord.y;
		mouseMove.type = SWT.MouseMove;
		display.post(mouseMove);
	}

	/**
	 * Move the mouse to the given point and executes the given event
	 * type with the given mouse button.
	 * 
	 * @param display
	 *            the display on which the mouse should be moved.
	 * @param coord
	 *            the coordinates to which the mouse cursor should be
	 *            moved.
	 * @param button
	 *            the button to use for the event
	 * @param type
	 *            the type which might by {@link SWT#MouseDown} or
	 *            {@link SWT#MouseUp}
	 */
	// private static void click(Display display, Point coord, int
	// button, int type) {
	// move(display, coord);
	//
	// Event mouseClick = new Event( );
	// mouseClick.button = button;
	// mouseClick.type = type;
	// display.post(mouseClick);
	// }
	/**
	 * Move the mouse to the given point and executes a click with the
	 * given mouse button.
	 * 
	 * @param display
	 *            the display on which the mouse should be moved.
	 * @param coord
	 *            the coordinates to which the mouse cursor should be
	 *            moved.
	 * @param button
	 *            the button to use for the event
	 */
	private static void click(Display display, Point coord, int button) {
		move(display, coord);

		Event mouseClick = new Event( );
		mouseClick.button = button;
		mouseClick.type = SWT.MouseDown;
		display.post(mouseClick);

		mouseClick.button = button;
		mouseClick.type = SWT.MouseUp;
		display.post(mouseClick);
	}
}
