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
 * Maze.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 06.04.2007:		Created by Matthias Gernand.
 */
package de.xirp.ate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.xirp.util.FutureRelease;

/**
 * This class represents a maze. Is contains several methods for
 * modifying the state of an field in the maze and methods for moving
 * the agent and setting the width and height of the maze. <br>
 * <br>
 * This is alpha API, may be removed in the future or is planed to be
 * integrated in version 3.0.0.
 * 
 * @author Matthias Gernand
 */
@FutureRelease(version = "3.0.0")
public class Maze implements Serializable {

	/**
	 * The serial version UID for this serializable class
	 */
	private static final long serialVersionUID = -3694337882176881850L;
	/**
	 * A array-array of <code>MazeField</code> for holding the
	 * states of the fields.
	 */
	private MazeField[][] fields;
	/**
	 * Agent position x.
	 */
	private int agentX = 0;
	/**
	 * Agent position y.
	 */
	private int agentY = 0;
	/**
	 * A list of {@link de.xirp.ate.MazeListener}
	 */
	private List<MazeListener> listeners = new ArrayList<MazeListener>( );

	/**
	 * An enum with constants indicating the state of a field in the
	 * maze. There are four possible states available.
	 * <ul>
	 * <li>FREE, a free field.</li>
	 * <li>WALL, a field containing a wall.</li>
	 * <li>GOAL, a goal field.</li>
	 * <li>TRAP, a field containing a trap.</li>
	 * </ul>
	 * 
	 * @author Matthias Gernand
	 */
	public enum MazeField implements Serializable {
		/**
		 * A free field.
		 */
		FREE,
		/**
		 * A field containing a wall.
		 */
		WALL,
		/**
		 * A goal field.
		 */
		GOAL,
		/**
		 * A field containing a trap.
		 */
		TRAP,
		// ...
	}

	/**
	 * Constructs a new {@link de.xirp.ate.Maze} for the
	 * given widthdraw and height.
	 * 
	 * @param width
	 *            The number of fields for the widthdraw of the maze.
	 * @param height
	 *            The number of fields for the height of the maze.
	 */
	public Maze(int width, int height) {
		fields = new MazeField[height][width];
		init( );
	}

	/**
	 * Initializes the {@link de.xirp.ate.Maze}. All
	 * fields are set to
	 * {@link de.xirp.ate.Maze.MazeField#FREE}
	 * 
	 * @see de.xirp.ate.Maze.MazeField
	 */
	private void init() {
		for (MazeField[] row : fields) {
			for (int x = 0; x < row.length; x++) {
				row[x] = MazeField.FREE;
			}
		}
	}

	/**
	 * Sets set specified field to the given field type.
	 * 
	 * @param x
	 *            The x position of the field.
	 * @param y
	 *            The y position of the field.
	 * @param type
	 *            The desired type of the field.
	 * @throws ArrayIndexOutOfBoundsException
	 *             if the specified field does not exist.
	 */
	public void setField(int x, int y, MazeField type)
			throws ArrayIndexOutOfBoundsException {
		fields[y][x] = type;
		fireMazeModified( );
	}

	/**
	 * Informs the listeners when the maze changed.
	 * 
	 * @see de.xirp.ate.MazeListener
	 */
	private void fireMazeModified() {
		for (MazeListener l : listeners) {
			l.mazeModified(this);
		}
	}

	/**
	 * Adds a listener to the list of listeners.
	 * 
	 * @param l
	 *            The listener to add.
	 */
	public void addMazeListener(MazeListener l) {
		listeners.add(l);
	}

	/**
	 * Removes a listener to the list of listeners.
	 * 
	 * @param l
	 *            The listener to remove.
	 */
	public void removeMazeListener(MazeListener l) {
		listeners.remove(l);
	}

	/**
	 * Gets the state of the specified field.
	 * 
	 * @param x
	 *            The x position of the field.
	 * @param y
	 *            The y position of the field.
	 * @return The state of the field as
	 *         {@link de.xirp.ate.Maze.MazeField}.
	 * @throws ArrayIndexOutOfBoundsException
	 *             if the specified field does not exist.
	 */
	public MazeField getField(int x, int y)
			throws ArrayIndexOutOfBoundsException {
		return fields[y][x];
	}

	/**
	 * Returns the height of the {@link de.xirp.ate.Maze}.
	 * 
	 * @return The height as <code>int</code>.
	 */
	public int getHeight() {
		return fields.length;
	}

	/**
	 * Returns the widthdraw of the
	 * {@link de.xirp.ate.Maze}.
	 * 
	 * @return The widthdraw as <code>int</code>.
	 */
	public int getWidth() {
		return fields[0].length;
	}

	/**
	 * Sets the agent to the given field.
	 * 
	 * @param x
	 *            The x position of the agent.
	 * @param y
	 *            The y position of the agent.
	 * @throws ArrayIndexOutOfBoundsException
	 *             if the specified field does not exist.
	 */
	public void setAgent(int x, int y) throws ArrayIndexOutOfBoundsException {
		if (x < 0 || y < 0 || x >= getWidth( ) || y >= getHeight( )) {
			throw new ArrayIndexOutOfBoundsException("Out of the maze: (x=" + x + ",y=" + y + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		this.agentX = x;
		this.agentY = y;
		fireMazeModified( );
	}

	/**
	 * Returns the x position of the agent.
	 * 
	 * @return The x position of the agent.
	 */
	public int getAgentX() {
		return agentX;
	}

	/**
	 * Returns the y position of the agent.
	 * 
	 * @return The y position of the agent.
	 */
	public int getAgentY() {
		return agentY;
	}
}
