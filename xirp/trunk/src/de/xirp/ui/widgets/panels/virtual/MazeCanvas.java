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
 * MazeCanvas.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 03.03.2007:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.panels.virtual;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import de.xirp.ate.ATEAdapter;
import de.xirp.ate.ATEManager;
import de.xirp.ate.Maze;
import de.xirp.ate.Maze.MazeField;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ColorManager;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.widgets.custom.XCanvas;
import de.xirp.util.Constants;
import de.xirp.util.FutureRelease;

/**
 * This is alpha API, may be removed in the future or is planed to be
 * integrated in version 3.0.0.
 * 
 * @author Matthias Gernand
 */
@FutureRelease(version = "3.0.0")
public class MazeCanvas extends XCanvas {

	private static final String Y_KEY = "y"; //$NON-NLS-1$
	private static final String X_KEY = "x"; //$NON-NLS-1$
	private static final String TYPE_KEY = "type"; //$NON-NLS-1$
	private static final RGB GOAL_COLOR = new RGB(170, 255, 153);
	private static final RGB WALL_COLOR = new RGB(0, 0, 0);
	private static final RGB TRAP_COLOR = new RGB(255, 114, 109);
	private static final RGB FREE_COLOR = new RGB(99, 182, 255);
	private int width;
	private int height;
	private Maze maze;
	private double size;
	private static final ImageData originalImage = ImageManager.getFullPathImage(Constants.IMAGE_DIR +
			File.separator + "marvin.png").getImageData( ); //$NON-NLS-1$
	private Image agentImage;
	private ATEAdapter listener;

	/**
	 * @param parent
	 * @param width
	 * @param height
	 */
	public MazeCanvas(Composite parent, int width, int height) {
		super(parent, SWT.DOUBLE_BUFFERED);
		this.width = width;
		this.height = height;
		init( );
	}

	/**
	 * 
	 */
	private void init() {
		listener = new ATEAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see de.xirp.ate.ATEAdapter#mazeChanged()
			 */
			@Override
			public void mazeChanged(Maze maze) {
				setMaze(maze);
			}

		};
		ATEManager.addATEListener(listener);

		addDisposeListener(new DisposeListener( ) {

			public void widgetDisposed(DisposeEvent e) {
				ATEManager.removeATEListener(listener);
			}

		});

		agentImage = ImageManager.getFullPathImage(Constants.IMAGE_DIR +
				File.separator + "marvin.png"); //$NON-NLS-1$

		addPaintListener(new PaintListener( ) {

			public void paintControl(PaintEvent e) {
				if (maze != null) {
					paintMaze(e.gc);
				}
			}

		});

		addControlListener(new ControlAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.ControlAdapter#controlResized(org.eclipse.swt.events.ControlEvent)
			 */
			@Override
			public void controlResized(ControlEvent e) {
				Rectangle rect = getClientArea( );
				double sizeX = rect.width / width;
				double sizeY = rect.height / height;

				size = Math.min(sizeX, sizeY);

				if (!((int) size <= 0)) {
					SWTUtil.secureDispose(agentImage);
					agentImage = new Image(getDisplay( ),
							originalImage.scaledTo((int) size, (int) size));
				}
			}

		});

		final Menu context = new Menu(this);
		for (MazeField f : MazeField.values( )) {
			MenuItem itm = new MenuItem(context, SWT.PUSH);
			itm.setText(f.name( ));
			itm.setData(TYPE_KEY, f);
			itm.addSelectionListener(new SelectionAdapter( ) {

				/*
				 * (non-Javadoc)
				 * 
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					int x = (Integer) context.getData(X_KEY);
					int y = (Integer) context.getData(Y_KEY);
					MenuItem itm = (MenuItem) e.widget;
					MazeField type = (MazeField) itm.getData(TYPE_KEY);
					try {
						maze.setField(x, y, type);
						redraw( );
					}
					catch (ArrayIndexOutOfBoundsException ex) {
					}
				}

			});
		}

		new MenuItem(context, SWT.SEPARATOR);

		MenuItem itm = new MenuItem(context, SWT.PUSH);
		itm.setText("AGENT"); //$NON-NLS-1$
		itm.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int x = (Integer) context.getData(X_KEY);
				int y = (Integer) context.getData(Y_KEY);
				try {
					maze.setAgent(x, y);
					redraw( );
				}
				catch (ArrayIndexOutOfBoundsException ex) {
				}
			}

		});
		setMenu(context);

		addMouseListener(new MouseAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.MouseAdapter#mouseDown(org.eclipse.swt.events.MouseEvent)
			 */
			@Override
			public void mouseDown(MouseEvent e) {
				Point p = ((Control) e.widget).toDisplay(new Point(e.x, e.y));
				context.setLocation(p);
				context.setData(X_KEY, (int) (e.x / size));
				context.setData(Y_KEY, (int) (e.y / size));
				context.setVisible(true);
			}
		});
	}

	/**
	 * @param gc
	 */
	private void paintMaze(GC gc) {
		Rectangle rect = getClientArea( );
		rect.width -= 1;
		rect.height -= 1;
		gc.drawRectangle(rect);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				MazeField field = maze.getField(x, y);
				int posY = (int) (y * size);
				int posX = (int) (x * size);
				switch (field) {
					case FREE:
						gc.setBackground(ColorManager.getColor(FREE_COLOR));
						gc.fillRectangle(posX, posY, (int) size, (int) size);
						break;
					case GOAL:
						gc.setBackground(ColorManager.getColor(GOAL_COLOR));
						gc.fillRectangle(posX, posY, (int) size, (int) size);
						break;
					case WALL:
						gc.setBackground(ColorManager.getColor(WALL_COLOR));
						gc.fillRectangle(posX, posY, (int) size, (int) size);
						break;
					case TRAP:
						gc.setBackground(ColorManager.getColor(TRAP_COLOR));
						gc.fillRectangle(posX, posY, (int) size, (int) size);
						break;
					default:
						break;
				}
				gc.drawRectangle(posX, posY, (int) size, (int) size);
			}
		}
		int posY = (int) (maze.getAgentY( ) * size);
		int posX = (int) (maze.getAgentX( ) * size);
		gc.drawImage(agentImage, posX, posY);
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param height
	 *            the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
		updateMaze( );
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
		updateMaze( );
	}

	public void setMaze(Maze maze) {
		this.maze = maze;
		this.width = maze.getWidth( );
		this.height = maze.getHeight( );
		redraw( );
	}

	/**
	 * 
	 */
	private void updateMaze() {
		maze = new Maze(width, height);
		ATEManager.setCurrentMaze(maze);
		redraw( );
	}
}
