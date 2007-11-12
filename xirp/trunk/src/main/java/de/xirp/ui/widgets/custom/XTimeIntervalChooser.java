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
 * XTimeIntervalChooser.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * 					 Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 25.03.2007:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.custom;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import de.xirp.ui.util.ressource.ColorManager;
import de.xirp.util.II18nHandler;
import de.xirp.util.Util;

/**
 * Chooser for a time interval.
 * 
 * @author Matthias Gernand
 * @author Rabea Gransberger
 */
public class XTimeIntervalChooser extends XCanvas implements PaintListener,
		MouseListener, MouseMoveListener, ControlListener {

	/**
	 * The height of the control.
	 */
	private static final int HEIGHT = 35;
	/**
	 * Half the height of the control.
	 */
	private static final int HEIGHT_HALF = HEIGHT / 2;
	/**
	 * The default format string.
	 */
	private static final String DEFAULT_FORMAT = "dd.MM.yyyy HH:mm:ss,SSS"; //$NON-NLS-1$
	/**
	 * The empty time string.
	 */
	private static final String EMPTY_TIME = "00.00.0000 00:00:00,000"; //$NON-NLS-1$
	/**
	 * The start {@link java.util.Date date}.
	 */
	private Date start;
	/**
	 * The stop {@link java.util.Date date}.
	 */
	private Date stop;
	/**
	 * The range {@link java.util.Date date}.
	 */
	private Date range;
	/**
	 * The start selection in milliseconds.
	 */
	private long chosenStart = 0;
	/**
	 * The stop selection in milliseconds.
	 */
	private long chosenStop = 0;
	/**
	 * The y start position.
	 */
	private int yStart;
	/**
	 * The y stop position.
	 */
	private int yStop;
	/**
	 * The x start position.
	 */
	private int xStart = -1;
	/**
	 * The x stop position.
	 */
	private int xStop = -1;
	/**
	 * A flag indicating if the mouse is pressed.
	 */
	private boolean mousePressed = false;
	/**
	 * The format string.
	 */
	private String format = DEFAULT_FORMAT;
	/**
	 * A color constant.
	 */
	private final Color BLACK;
	/**
	 * The line color.
	 */
	private Color lineColor;
	/**
	 * The marker color.
	 */
	private Color markerColor;
	/**
	 * The time text color.
	 */
	private Color timeTextColor;
	/**
	 * The selection time text color.
	 */
	private Color chosenTimeTextColor;
	/**
	 * Flag indicating if a fixed range should be used or not.
	 */
	private boolean fixedRange;
	/**
	 * Flag indicating if the range initialization is done.
	 */
	private boolean rangeInit = true;
	/**
	 * The {@link org.eclipse.swt.events.SelectionListener selection}
	 * listeners.
	 */
	private static List<SelectionListener> listeners = new ArrayList<SelectionListener>( );

	/**
	 * Constructs a new time interval chooser. This constructor should
	 * be used in plugin-UI environment, because the
	 * {@link de.xirp.util.II18nHandler handler} is
	 * needed for translations.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 * @param handler
	 *            The handler.
	 * @see de.xirp.util.II18nHandler
	 */
	public XTimeIntervalChooser(Composite parent, int style,
			II18nHandler handler) {
		super(parent, style | SWT.DOUBLE_BUFFERED, handler);
		BLACK = ColorManager.getColor(0, 0, 0);
		this.fixedRange = false;
		init( );
	}

	/**
	 * Constructs a new time interval chooser. This constructor should
	 * be used in application-UI environment.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 */
	public XTimeIntervalChooser(Composite parent, int style) {
		super(parent, style | SWT.DOUBLE_BUFFERED);
		BLACK = ColorManager.getColor(0, 0, 0);
		this.fixedRange = false;
		init( );
	}

	/**
	 * Constructs a new time interval chooser. This constructor should
	 * be used in plugin-UI environment, because the
	 * {@link de.xirp.util.II18nHandler handler} is
	 * needed for translations.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 * @param handler
	 *            The handler.
	 * @param fixedRange
	 *            A flag indicating if a fixed range should be used.<br>
	 *            That means, the sliders are fixed in their range and
	 *            are moved together to hold the distance.
	 * @see de.xirp.util.II18nHandler
	 */
	public XTimeIntervalChooser(Composite parent, int style,
			II18nHandler handler, boolean fixedRange) {
		super(parent, style | SWT.DOUBLE_BUFFERED, handler);
		BLACK = ColorManager.getColor(0, 0, 0);
		this.fixedRange = fixedRange;
		init( );
	}

	/**
	 * Constructs a new time interval chooser. This constructor should
	 * be used in application-UI environment.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 * @param fixedRange
	 *            A flag indicating if a fixed range should be used.<br>
	 *            That means, the sliders are fixed in their range and
	 *            are moved together to hold the distance.
	 */
	public XTimeIntervalChooser(Composite parent, int style, boolean fixedRange) {
		super(parent, style | SWT.DOUBLE_BUFFERED);
		BLACK = ColorManager.getColor(0, 0, 0);
		this.fixedRange = fixedRange;
		init( );
	}

	/**
	 * Initializes the control and sets the listeners-
	 */
	private void init() {
		lineColor = BLACK;
		markerColor = BLACK;
		timeTextColor = BLACK;
		chosenTimeTextColor = BLACK;

		addPaintListener(this);
		addMouseListener(this);
		addMouseMoveListener(this);
		addControlListener(this);
		redraw( );

	}

	/**
	 * Paints the time interval chooser.
	 * 
	 * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
	 */
	public void paintControl(PaintEvent e) {

		if (this.fixedRange && !this.rangeInit) {
			Event ev = new Event( );
			ev.x = getBounds( ).width / 4;
			notifyListeners(SWT.MouseUp, ev);
			this.rangeInit = true;
		}

		Rectangle bounds = getBounds( );
		final int boundsHeighHalf = bounds.height / 2;
		yStart = boundsHeighHalf - HEIGHT_HALF;
		yStop = boundsHeighHalf + HEIGHT_HALF;
		e.gc.setForeground(lineColor);
		e.gc.drawLine(0, boundsHeighHalf, bounds.width, boundsHeighHalf);
		e.gc.setLineWidth(3);
		e.gc.drawLine(0, yStart, 0, yStop);
		final int lineWidthHalf = e.gc.getLineWidth( ) / 2;
		e.gc.drawLine(bounds.width - lineWidthHalf, yStart, bounds.width -
				lineWidthHalf, yStop);

		if (xStart < 0) {
			xStart = 0;
		}
		if (xStop < 0) {
			xStop = bounds.width;
		}

		e.gc.setForeground(markerColor);
		drawMarker(e.gc, bounds, xStart);
		drawMarker(e.gc, bounds, xStop);

		drawTimes(e.gc, bounds);

	}

	/**
	 * Sets the time format string.
	 * 
	 * @param format
	 *            The format string to set.
	 * @see java.text.SimpleDateFormat
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * Draws the time texts.
	 * 
	 * @param gc
	 *            The GC to draw on.
	 * @param bounds
	 *            The bounds.
	 */
	private void drawTimes(GC gc, Rectangle bounds) {
		String start = EMPTY_TIME;
		String stop = EMPTY_TIME;
		String chosenStart = EMPTY_TIME;
		String chosenStop = EMPTY_TIME;

		if (this.start != null) {
			start = Util.getTimeAsString(this.start, format);
			chosenStart = Util.getTimeAsString(getChosenStartInternal(bounds),
					format);
		}
		if (this.stop != null) {
			stop = Util.getTimeAsString(this.stop, format);
			chosenStop = Util.getTimeAsString(getChosenStopInternal(bounds),
					format);
		}
		gc.setForeground(timeTextColor);
		gc.drawString(start, 10, yStart, true);
		gc.drawString(stop,
				bounds.width - gc.stringExtent(stop).x - 10,
				yStart,
				true);

		gc.setForeground(chosenTimeTextColor);
		gc.drawString(chosenStart,
				10,
				yStop - gc.stringExtent(chosenStart).y,
				true);
		gc.drawString(chosenStop, bounds.width - gc.stringExtent(chosenStop).x -
				10, yStop - gc.stringExtent(chosenStop).y, true);

		fireSelectionEvent( );
	}

	/**
	 * Returns the x start for the given bounds.
	 * 
	 * @param bounds
	 *            The bounds.
	 * @return The x start.
	 */
	private int getXStart(Rectangle bounds) {
		if (start == null || stop == null) {
			return -1;
		}
		long scale = Util.scale(start.getTime( ),
				stop.getTime( ),
				0,
				bounds.width,
				chosenStart);
		return (int) scale;
	}

	/**
	 * Returns the y stop for the given bounds.
	 * 
	 * @param bounds
	 *            The bounds.
	 * @return The x stop.
	 */
	private int getXStop(Rectangle bounds) {
		if (start == null || stop == null) {
			return -1;
		}
		long scale = Util.scale(start.getTime( ),
				stop.getTime( ),
				0,
				bounds.width,
				chosenStop);
		return (int) scale;
	}

	/**
	 * Returns the selected start {@link java.util.Date date}.
	 * 
	 * @param bounds
	 *            The bounds.
	 * @return The start date.
	 */
	private Date getChosenStartInternal(Rectangle bounds) {
		long scale = Util.scale(0,
				bounds.width,
				start.getTime( ),
				stop.getTime( ),
				xStart);
		chosenStart = scale;
		return new Date(scale);
	}

	/**
	 * Returns the selected stop {@link java.util.Date date}.
	 * 
	 * @param bounds
	 *            The bounds.
	 * @return The stop date.
	 */
	private Date getChosenStopInternal(Rectangle bounds) {
		long scale = Util.scale(0,
				bounds.width,
				start.getTime( ),
				stop.getTime( ),
				xStop);
		chosenStop = scale;
		return new Date(scale);
	}

	/**
	 * Draws the markers.
	 * 
	 * @param gc
	 *            The gc to draw on.
	 * @param bounds
	 *            The bounds.
	 * @param x
	 *            The x position.
	 */
	private void drawMarker(GC gc, Rectangle bounds, int x) {
		gc.setLineWidth(3);
		gc.drawLine(x, yStart + 2, x, yStop - 2);
		final int lineWidthHalf = gc.getLineWidth( ) / 2;
		gc.drawPolygon(new int[] {x - 3, yStart + lineWidthHalf, x + 3,
				yStart + lineWidthHalf, x, yStart + 3});
		gc.drawPolygon(new int[] {x - 3, yStop - lineWidthHalf, x + 3,
				yStop - lineWidthHalf, x, yStop - 3});
	}

	/**
	 * Sets the start {@link java.util.Date date}.
	 * 
	 * @param start
	 *            The start to set.
	 */
	public void setStart(Date start) {
		this.start = start;
		if (this.stop != null && this.range == null && this.fixedRange) {
			setRange(calcDefaultRange( ));
		}
		redraw( );
	}

	/**
	 * Sets the stop {@link java.util.Date date}.
	 * 
	 * @param stop
	 *            The stop to set.
	 */
	public void setStop(Date stop) {
		this.stop = stop;
		if (this.start != null && this.range == null && this.fixedRange) {
			setRange(calcDefaultRange( ));
		}
		redraw( );
	}

	/**
	 * Calculates the default range.
	 * 
	 * @return The default time range.
	 */
	private Date calcDefaultRange() {
		long diff = this.stop.getTime( ) - this.start.getTime( );
		long def = diff / 5;
		return new Date(def);
	}

	/**
	 * Overridden to do nothing on double clicks.
	 * 
	 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseDoubleClick(MouseEvent e) {
		// Do nothing
	}

	/**
	 * Sets
	 * {@link de.xirp.ui.widgets.custom.XTimeIntervalChooser#mousePressed}
	 * to true to indicate that the mouse is currently pressed.
	 * 
	 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseDown(MouseEvent e) {
		mousePressed = true;
	}

	/**
	 * Sets
	 * {@link de.xirp.ui.widgets.custom.XTimeIntervalChooser#mousePressed}
	 * to false, because the mouse is no longer pressed. The chooser
	 * is adjusted.
	 * 
	 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseUp(MouseEvent e) {
		mousePressed = false;
		adjust(e);
		Rectangle bound = getBounds( );
		getChosenStartInternal(bound);
		getChosenStopInternal(bound);
	}

	/**
	 * If the mouse is currently pressed the chooser is adjusted.
	 * 
	 * @see org.eclipse.swt.events.MouseMoveListener#mouseMove(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseMove(MouseEvent e) {
		if (mousePressed) {
			adjust(e);
		}
	}

	/**
	 * Adjusts the chooser for the given positioning information from
	 * the given {@link org.eclipse.swt.events.MouseEvent}.
	 * 
	 * @param e
	 *            The mouse event.
	 * @see org.eclipse.swt.events.MouseEvent
	 */
	private void adjust(MouseEvent e) {
		double powStart = Math.pow(xStart - e.x, 2);
		double powStop = Math.pow(xStop - e.x, 2);
		Rectangle bounds = getBounds( );
		if (this.fixedRange) {
			long scale = Util.scale(start.getTime( ),
					stop.getTime( ),
					0,
					bounds.width,
					start.getTime( ) + range.getTime( ));
			if (powStart < powStop) {
				xStart = e.x;

				if (e.x < 0) {
					xStart = 0;
				}
				else {
					xStart = e.x;
				}
				xStart = (int) Math.min(xStart, bounds.width - scale);
				xStop = (int) (xStart + scale);
			}
			else {
				if (e.x > bounds.width) {
					xStop = bounds.width;
				}
				else {
					xStop = e.x;
				}
				xStop = (int) Math.max(xStop, scale);
				xStart = (int) (xStop - scale);
			}
		}
		else {
			if (powStart < powStop) {
				xStart = e.x;
			}
			else {
				if (e.x > bounds.width) {
					xStop = bounds.width;
				}
				else {
					xStop = e.x;
				}
			}
		}
		redraw( );
	}

	/**
	 * Sets the selected time text color.
	 * 
	 * @param chosenTimeTextColor
	 *            The selected time text color to set.
	 */
	public void setChosenTimeTextColor(Color chosenTimeTextColor) {
		this.chosenTimeTextColor = chosenTimeTextColor;
	}

	/**
	 * Sets the line color.
	 * 
	 * @param lineColor
	 *            The line color to set.
	 */
	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	/**
	 * Sets the marker color.
	 * 
	 * @param markerColor
	 *            The marker color to set.
	 */
	public void setMarkerColor(Color markerColor) {
		this.markerColor = markerColor;
	}

	/**
	 * Sets the time text color.
	 * 
	 * @param timeTextColor
	 *            The time text color to set.
	 */
	public void setTimeTextColor(Color timeTextColor) {
		this.timeTextColor = timeTextColor;
	}

	/**
	 * Returns the selected start {@link java.util.Date date}.
	 * 
	 * @return The start date.
	 */
	public Date getChosenStartDate() {
		return new Date(chosenStart);
	}

	/**
	 * Returns the selected stop {@link java.util.Date date}.
	 * 
	 * @return The stop date.
	 */
	public Date getChosenStopDate() {
		return new Date(chosenStop);
	}

	/**
	 * Does nothing.
	 * 
	 * @see org.eclipse.swt.events.ControlListener#controlMoved(org.eclipse.swt.events.ControlEvent)
	 */
	public void controlMoved(ControlEvent e) {
		// Do nothing
	}

	/**
	 * Recalculates start and stop points.
	 * 
	 * @see org.eclipse.swt.events.ControlListener#controlResized(org.eclipse.swt.events.ControlEvent)
	 */
	public void controlResized(ControlEvent e) {
		Rectangle bounds = getBounds( );
		if (xStart != -1) {
			xStart = getXStart(bounds);
		}
		if (xStop != -1) {
			xStop = getXStop(bounds);
		}
		redraw( );
	}

	/**
	 * Adds a {@link org.eclipse.swt.events.SelectionListener} to this
	 * control.
	 * 
	 * @param listener
	 *            The listener to add.
	 */
	public void addSelectionListener(SelectionListener listener) {
		listeners.add(listener);
	}

	/**
	 * Fires a selection event to all listeners.
	 */
	private void fireSelectionEvent() {
		Event e = new Event( );
		e.widget = this;
		e.height = HEIGHT;
		e.width = getBounds( ).width;
		e.display = this.getDisplay( );
		SelectionEvent se = new SelectionEvent(e);
		for (SelectionListener l : listeners) {
			l.widgetSelected(se);
		}
	}

	/**
	 * Sets the range of the markers. Works when a fixed range is
	 * requested.
	 * 
	 * @param range
	 *            The range to set.
	 */
	public void setRange(Date range) {
		this.range = range;
		this.fixedRange = true;
		this.rangeInit = false;
		redraw( );
	}

	/**
	 * Enables or disables the control.
	 * 
	 * @param enabled
	 *            <code>true</code>: control is enabled.
	 *            <code>false</code>: control is disabled.
	 * @see org.eclipse.swt.widgets.Control#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		// /TODO
		// if (enabled) {
		// lineColor;
		// markerColor;
		// timeTextColor;
		// chosenTimeTextColor;
		// }
		// else {
		// lineColor = GREY;
		// markerColor = GREY;
		// timeTextColor = GREY;
		// chosenTimeTextColor = GREY;
		// }
		// redraw();
	}
}
