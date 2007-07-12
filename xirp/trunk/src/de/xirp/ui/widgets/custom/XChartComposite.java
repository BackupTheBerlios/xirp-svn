/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2006, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * -------------------
 * ChartComposite.java
 * -------------------
 * (C) Copyright 2006, by Henry Proudhon and Contributors.
 *
 * Original Author:  Henry Proudhon (henry.proudhon AT insa-lyon.fr);
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * Changes
 * -------
 * 19-Jun-2006 : New class (HP);
 * 06-Nov-2006 : Added accessor methods for zoomInFactor and zoomOutFactor (DG);
 * 28-Nov-2006 : Added support for trace lines (HP);
 * 30-Nov-2006 : Improved zoom box handling (HP);
 * 06-Dec-2006 : Added (simplified) tool tip support (HP);
 * 11-Dec-2006 : Fixed popup menu location by fgiust, bug 1612770 (HP);
 * 31-Jan-2007 : Fixed some issues with the trace lines, fixed cross hair not being drawn,
 *               added getter and setter methods for the trace lines (HP); 
 */

/**
 * Based on ChartComposite.java, authors and license see above.
 * Removed stuff we don't need. Extended our own XComposite class.
 * Implemented setBackground() correctly. Used XCanvas instead of Canvas.
 * Fixed many memory leaks.
 */
package de.xirp.ui.widgets.custom;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.event.EventListenerList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.chart.plot.Zoomable;

import de.xirp.chart.ChartUtil;
import de.xirp.chart.fixedutils.SWTGraphics2D;
import de.xirp.chart.fixedutils.SWTUtils;
import de.xirp.ui.util.SWTUtil;
import de.xirp.util.Util;

/**
 * A SWT composite for displaying a {@link org.jfree.chart.JFreeChart}
 * object. <br>
 * <br>
 * The composite listens to the chart to receive notification of
 * changes to any component of the chart. The chart is redrawn
 * automatically whenever this notification is received.
 */
public class XChartComposite extends XComposite implements ChartChangeListener,
		SelectionListener, ControlListener {

	/** Default setting for buffer usage. */
	public static final boolean DEFAULT_BUFFER_USED = false;
	/** The default panel width. */
	public static final int DEFAULT_WIDTH = 680;
	/** The default panel height. */
	public static final int DEFAULT_HEIGHT = 420;
	/** The default limit below which chart scaling kicks in. */
	public static final int DEFAULT_MINIMUM_DRAW_WIDTH = 300;
	/** The default limit below which chart scaling kicks in. */
	public static final int DEFAULT_MINIMUM_DRAW_HEIGHT = 200;
	/** The default limit below which chart scaling kicks in. */
	public static final int DEFAULT_MAXIMUM_DRAW_WIDTH = 1000;
	/** The default limit below which chart scaling kicks in. */
	public static final int DEFAULT_MAXIMUM_DRAW_HEIGHT = 1000;
	/** The minimum size required to perform a zoom on a rectangle */
	public static final int DEFAULT_ZOOM_TRIGGER_DISTANCE = 10;
	/** Export pdf action command. */
	public static final String EXPORT_PDF_COMMAND = "EXPORT_PDF"; //$NON-NLS-1$
	/** Export png action command. */
	public static final String EXPORT_PNG_COMMAND = "EXPORT_PNG"; //$NON-NLS-1$
	/** Export jpeg action command. */
	public static final String EXPORT_JPEG_COMMAND = "EXPORT_JPEG"; //$NON-NLS-1$
	/** Zoom in (both axes) action command. */
	public static final String ZOOM_IN_BOTH_COMMAND = "ZOOM_IN_BOTH"; //$NON-NLS-1$
	/** Zoom in (domain axis only) action command. */
	public static final String ZOOM_IN_DOMAIN_COMMAND = "ZOOM_IN_DOMAIN"; //$NON-NLS-1$
	/** Zoom in (range axis only) action command. */
	public static final String ZOOM_IN_RANGE_COMMAND = "ZOOM_IN_RANGE"; //$NON-NLS-1$
	/** Zoom out (both axes) action command. */
	public static final String ZOOM_OUT_BOTH_COMMAND = "ZOOM_OUT_BOTH"; //$NON-NLS-1$
	/** Zoom out (domain axis only) action command. */
	public static final String ZOOM_OUT_DOMAIN_COMMAND = "ZOOM_DOMAIN_BOTH"; //$NON-NLS-1$
	/** Zoom out (range axis only) action command. */
	public static final String ZOOM_OUT_RANGE_COMMAND = "ZOOM_RANGE_BOTH"; //$NON-NLS-1$
	/** Zoom reset (both axes) action command. */
	public static final String ZOOM_RESET_BOTH_COMMAND = "ZOOM_RESET_BOTH"; //$NON-NLS-1$
	/** Zoom reset (domain axis only) action command. */
	public static final String ZOOM_RESET_DOMAIN_COMMAND = "ZOOM_RESET_DOMAIN"; //$NON-NLS-1$
	/** Zoom reset (range axis only) action command. */
	public static final String ZOOM_RESET_RANGE_COMMAND = "ZOOM_RESET_RANGE"; //$NON-NLS-1$
	/** The chart that is displayed in the panel. */
	public JFreeChart chart;
	/** The canvas to display the chart */
	private XCanvas canvas;
	/** Storage for registered (chart) mouse listeners. */
	private EventListenerList chartMouseListeners;
	/**
	 * A flag that controls whether or not the off-screen buffer is
	 * used.
	 */
	private boolean useBuffer;
	/** A flag that indicates that the buffer should be refreshed. */
	private boolean refreshBuffer;
	/** A flag that indicates that the tooltips should be displayed. */
	private boolean displayToolTips;
	/** A buffer for the rendered chart. */
	private org.eclipse.swt.graphics.Image chartBuffer;
	/** The height of the chart buffer. */
	private int chartBufferHeight;
	/** The width of the chart buffer. */
	private int chartBufferWidth;
	/**
	 * The minimum width for drawing a chart (uses scaling for smaller
	 * widths).
	 */
	private int minimumDrawWidth;
	/**
	 * The minimum height for drawing a chart (uses scaling for
	 * smaller heights).
	 */
	private int minimumDrawHeight;
	/**
	 * The maximum width for drawing a chart (uses scaling for bigger
	 * widths).
	 */
	private int maximumDrawWidth;
	/**
	 * The maximum height for drawing a chart (uses scaling for bigger
	 * heights).
	 */
	private int maximumDrawHeight;
	/** The popup menu for the frame. */
	private Menu popup;
	/** The drawing info collected the last time the chart was drawn. */
	private ChartRenderingInfo info;
	/** The chart anchor point. */
	private Point2D anchor;
	/** The scale factor used to draw the chart. */
	private double scaleX;
	/** The scale factor used to draw the chart. */
	private double scaleY;
	/** The plot orientation. */
	private PlotOrientation orientation = PlotOrientation.VERTICAL;
	/** A flag that controls whether or not domain zooming is enabled. */
	private boolean domainZoomable = false;
	/** A flag that controls whether or not range zooming is enabled. */
	private boolean rangeZoomable = false;
	/**
	 * The zoom rectangle starting point (selected by the user with a
	 * mouse click). This is a point on the screen, not the chart
	 * (which may have been scaled up or down to fit the panel).
	 */
	private org.eclipse.swt.graphics.Point zoomPoint = null;
	/** The zoom rectangle (selected by the user with the mouse). */
	private transient Rectangle zoomRectangle = null;
	/**
	 * The minimum distance required to drag the mouse to trigger a
	 * zoom.
	 */
	private int zoomTriggerDistance;
	/**
	 * A flag that controls whether or not horizontal tracing is
	 * enabled.
	 */
	private boolean horizontalAxisTrace = false;
	/**
	 * A flag that controls whether or not vertical tracing is
	 * enabled.
	 */
	private boolean verticalAxisTrace = false;
	/** A vertical trace line. */
	private transient int verticalTraceLineX;
	/** A horizontal trace line. */
	private transient int horizontalTraceLineY;
	/** Menu item for zooming in on a chart (both axes). */
	private XMenuItem zoomInBothMenuItem;
	/** Menu item for zooming in on a chart (domain axis). */
	private XMenuItem zoomInDomainMenuItem;
	/** Menu item for zooming in on a chart (range axis). */
	private XMenuItem zoomInRangeMenuItem;
	/** Menu item for zooming out on a chart. */
	private XMenuItem zoomOutBothMenuItem;
	/** Menu item for zooming out on a chart (domain axis). */
	private XMenuItem zoomOutDomainMenuItem;
	/** Menu item for zooming out on a chart (range axis). */
	private XMenuItem zoomOutRangeMenuItem;
	/** Menu item for resetting the zoom (both axes). */
	private XMenuItem zoomResetBothMenuItem;
	/** Menu item for resetting the zoom (domain axis only). */
	private XMenuItem zoomResetDomainMenuItem;
	/** Menu item for resetting the zoom (range axis only). */
	private XMenuItem zoomResetRangeMenuItem;
	/**
	 * A flag that controls whether or not file extensions are
	 * enforced.
	 */
	private boolean enforceFileExtensions;
	/** The factor used to zoom in on an axis range. */
	private double zoomInFactor = 0.5;
	/** The factor used to zoom out on an axis range. */
	private double zoomOutFactor = 2.0;
	/** The parent */
	private Composite parent;
	/** The robot name */
	private String robotName;

	/**
	 * Constructs a {@link org.jfree.chart.JFreeChart chart}
	 * composite.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style of the composite.
	 * @param chart
	 *            The chart.
	 * @param useBuffer
	 *            A flag controlling whether or not an off-screen
	 *            buffer is used.
	 * @see org.jfree.chart.JFreeChart
	 */
	public XChartComposite(Composite parent, int style, JFreeChart chart,
			boolean useBuffer, String robotName) {
		this(parent,
				style,
				chart,
				DEFAULT_WIDTH,
				DEFAULT_HEIGHT,
				DEFAULT_MINIMUM_DRAW_WIDTH,
				DEFAULT_MINIMUM_DRAW_HEIGHT,
				DEFAULT_MAXIMUM_DRAW_WIDTH,
				DEFAULT_MAXIMUM_DRAW_HEIGHT,
				useBuffer,
				true,
				true,
				true,
				robotName);
	}

	/**
	 * Constructs a {@link org.jfree.chart.JFreeChart chart}
	 * composite.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style of the composite.
	 * @param jfreechart
	 *            the chart.
	 * @param width
	 *            the preferred width of the panel.
	 * @param height
	 *            the preferred height of the panel.
	 * @param minimumDrawWidth
	 *            the minimum drawing width.
	 * @param minimumDrawHeight
	 *            the minimum drawing height.
	 * @param maximumDrawWidth
	 *            the maximum drawing width.
	 * @param maximumDrawHeight
	 *            the maximum drawing height.
	 * @param useBuffer
	 *            a flag that indicates whether to use the off-screen
	 *            buffer to improve performance (at the expense of
	 *            memory).
	 * @param zoom
	 *            a flag indicating whether or not zoom options should
	 *            be added to the popup menu.
	 * @param tooltips
	 *            a flag indicating whether or not tooltips should be
	 *            enabled for the chart.
	 * @param export
	 *            a flag indicating whether or not the export options
	 *            should be added to the popup menu
	 * @param robotName
	 *            name of the robot
	 * @see org.jfree.chart.JFreeChart
	 */
	private XChartComposite(Composite parent, int style, JFreeChart jfreechart,
			int width, int height, int minimumDrawWidth, int minimumDrawHeight,
			int maximumDrawWidth, int maximumDrawHeight, boolean useBuffer,
			boolean zoom, boolean tooltips, boolean export, String robotName) {

		super(parent, style | SWT.DOUBLE_BUFFERED | SWT.BACKGROUND);
		this.parent = parent;
		init(jfreechart,
				minimumDrawWidth,
				minimumDrawHeight,
				maximumDrawWidth,
				maximumDrawHeight,
				useBuffer,
				zoom,
				tooltips,
				export,
				robotName);
	}

	/**
	 * @param jfreechart
	 * @param minimumDrawWidth
	 * @param minimumDrawHeight
	 * @param maximumDrawWidth
	 * @param maximumDrawHeight
	 * @param useBuffer
	 * @param zoom
	 * @param tooltips
	 * @param export
	 * @param robotName
	 */
	private void init(JFreeChart jfreechart, int minimumDrawWidth,
			int minimumDrawHeight, int maximumDrawWidth, int maximumDrawHeight,
			boolean useBuffer, boolean zoom, boolean tooltips, boolean export,
			String robotName) {

		setChartAndRobotName(jfreechart, robotName);
		addControlListener(this);
		chartMouseListeners = new EventListenerList( );
		setLayout(new FillLayout( ));
		info = new ChartRenderingInfo( );

		this.useBuffer = useBuffer;
		this.refreshBuffer = false;
		this.minimumDrawWidth = minimumDrawWidth;
		this.minimumDrawHeight = minimumDrawHeight;
		this.maximumDrawWidth = maximumDrawWidth;
		this.maximumDrawHeight = maximumDrawHeight;
		this.zoomTriggerDistance = DEFAULT_ZOOM_TRIGGER_DISTANCE;

		setDisplayToolTips(tooltips);

		canvas = new XCanvas(this, SWT.DOUBLE_BUFFERED | SWT.BACKGROUND);

		canvas.addPaintListener(new PaintListener( ) {

			public void paintControl(PaintEvent e) {
				if (chart != null) {
					paintChart(e);
				}
			}
		});

		if (chart != null) {
			chart.addChangeListener(this);
			Plot plot = chart.getPlot( );
			this.domainZoomable = false;
			this.rangeZoomable = false;
			if (plot instanceof Zoomable) {
				Zoomable z = (Zoomable) plot;
				this.domainZoomable = z.isDomainZoomable( );
				this.rangeZoomable = z.isRangeZoomable( );
				this.orientation = z.getOrientation( );
			}
		}

		// set up popup menu...
		this.popup = null;
		if (zoom) {
			this.popup = createPopupMenu(zoom, export);
		}

		Listener listener = new Listener( ) {

			public void handleEvent(Event event) {
				if (XChartComposite.this.chart != null) {
					handleMouseEvents(event);
				}
			}

		};

		canvas.addListener(SWT.MouseDown, listener);
		canvas.addListener(SWT.MouseMove, listener);
		canvas.addListener(SWT.MouseUp, listener);

		this.enforceFileExtensions = true;
	}

	/**
	 * @param event
	 */
	private void handleMouseEvents(Event event) {
		switch (event.type) {
			case SWT.MouseDown:
				Rectangle scaledDataArea = getScreenDataArea(event.x, event.y);
				zoomPoint = getPointInRectangle(event.x,
						event.y,
						scaledDataArea);
				Rectangle insets = getClientArea( );
				int x = (int) ((event.x - insets.x) / scaleX);
				int y = (int) ((event.y - insets.y) / scaleY);

				anchor = new Point2D.Double(x, y);
				chart.setNotify(true); // force a redraw
				canvas.redraw( );
				// new entity code...
				Object[] listeners = chartMouseListeners.getListeners(ChartMouseListener.class);
				if (listeners.length == 0) {
					return;
				}

				ChartEntity entity = null;
				if (info != null) {
					EntityCollection entities = info.getEntityCollection( );
					if (entities != null) {
						entity = entities.getEntity(x, y);
					}
				}
				java.awt.event.MouseEvent mouseEvent = SWTUtils.toAwtMouseEvent(event);
				ChartMouseEvent chartEvent = new ChartMouseEvent(getChart( ),
						mouseEvent,
						entity);
				for (int i = listeners.length - 1; i >= 0; i -= 1) {
					((ChartMouseListener) listeners[i]).chartMouseClicked(chartEvent);
				}
				break;
			case SWT.MouseMove:
				// handle axis trace
				if (horizontalAxisTrace || verticalAxisTrace) {
					horizontalTraceLineY = event.y;
					verticalTraceLineX = event.x;
					canvas.redraw( );
				}
				// handle tool tips in a simple way
				if (displayToolTips) {
					String s = getToolTipText(new MouseEvent(event));
					if (s != null && !s.equals(canvas.getToolTipText( ))) {
						canvas.setToolTipText(s);
					}
				}
				// handle zoom box
				if (zoomPoint == null) {
					return;
				}
				scaledDataArea = getScreenDataArea(zoomPoint.x, zoomPoint.y);
				org.eclipse.swt.graphics.Point movingPoint = getPointInRectangle(event.x,
						event.y,
						scaledDataArea);
				// handle zoom
				boolean hZoom = false;
				boolean vZoom = false;
				if (orientation == PlotOrientation.HORIZONTAL) {
					hZoom = rangeZoomable;
					vZoom = domainZoomable;
				}
				else {
					hZoom = domainZoomable;
					vZoom = rangeZoomable;
				}
				if (hZoom && vZoom) {
					// selected rectangle shouldn't extend outside the
					// data area...
					zoomRectangle = new Rectangle(zoomPoint.x,
							zoomPoint.y,
							movingPoint.x - zoomPoint.x,
							movingPoint.y - zoomPoint.y);
				}
				else if (hZoom) {
					zoomRectangle = new Rectangle(zoomPoint.x,
							scaledDataArea.y,
							movingPoint.x - zoomPoint.x,
							scaledDataArea.height);
				}
				else if (vZoom) {
					zoomRectangle = new Rectangle(scaledDataArea.x,
							zoomPoint.y,
							scaledDataArea.width,
							event.y - zoomPoint.y);
				}
				canvas.redraw( );
				break;
			case SWT.MouseUp:
				if (zoomRectangle == null) {
					Rectangle screenDataArea = getScreenDataArea(event.x,
							event.y);
					if (screenDataArea != null) {
						zoomPoint = getPointInRectangle(event.x,
								event.y,
								screenDataArea);
					}
					if (popup != null && event.button == 3) {
						org.eclipse.swt.graphics.Point pt = canvas.toDisplay(event.x,
								event.y);
						displayPopupMenu(pt.x, pt.y);
					}
				}
				else {
					hZoom = false;
					vZoom = false;
					if (orientation == PlotOrientation.HORIZONTAL) {
						hZoom = rangeZoomable;
						vZoom = domainZoomable;
					}
					else {
						hZoom = domainZoomable;
						vZoom = rangeZoomable;
					}
					boolean zoomTrigger1 = hZoom &&
							Math.abs(zoomRectangle.width) >= zoomTriggerDistance;
					boolean zoomTrigger2 = vZoom &&
							Math.abs(zoomRectangle.height) >= zoomTriggerDistance;
					if (zoomTrigger1 || zoomTrigger2) {
						// if the box has been drawn backwards,
						// restore the auto bounds
						if ((hZoom && (zoomRectangle.x + zoomRectangle.width < zoomPoint.x)) ||
								(vZoom && (zoomRectangle.y +
										zoomRectangle.height < zoomPoint.y))) {
							restoreAutoBounds( );
						}
						else {
							zoom(zoomRectangle);
						}
						canvas.redraw( );
					}
				}
				zoomPoint = null;
				zoomRectangle = null;
				break;
			default:
				zoomPoint = null;
				zoomRectangle = null;
		}
	}

	/**
	 * @param e
	 */
	private void paintChart(PaintEvent e) {
		SWTGraphics2D sg2 = new SWTGraphics2D(e.gc, false);

		// first determine the size of the chart rendering area...
		// no insets for SWT, try getClientArea
		Rectangle available = getBounds( );

		// work out if scaling is required...
		boolean scale = false;
		int drawWidth = available.width;
		int drawHeight = available.height;
		if (drawWidth == 0.0 || drawHeight == 0.0) {
			return;
		}
		scaleX = 1.0;
		scaleY = 1.0;
		if (drawWidth < minimumDrawWidth) {
			scaleX = (double) drawWidth / minimumDrawWidth;
			drawWidth = minimumDrawWidth;
			scale = true;
		}
		else if (drawWidth > maximumDrawWidth) {
			scaleX = (double) drawWidth / maximumDrawWidth;
			drawWidth = maximumDrawWidth;
			scale = true;
		}
		if (drawHeight < minimumDrawHeight) {
			scaleY = (double) drawHeight / minimumDrawHeight;
			drawHeight = minimumDrawHeight;
			scale = true;
		}
		else if (drawHeight > maximumDrawHeight) {
			scaleY = (double) drawHeight / maximumDrawHeight;
			drawHeight = maximumDrawHeight;
			scale = true;
		}
		// are we using the chart buffer?
// if (useBuffer) {
// //SwtGraphics2D sg2 = new SwtGraphics2D( e.gc );
// chartBuffer = (org.eclipse.swt.graphics.Image) canvas
// .getData("double-buffer-image");
// // do we need to fill the buffer?
// if (chartBuffer == null || chartBufferWidth != available.width
// || chartBufferHeight != available.height) {
// chartBufferWidth = available.width;
// chartBufferHeight = available.height;
// if (chartBuffer != null) {
// chartBuffer.dispose( );
// }
// chartBuffer = new org.eclipse.swt.graphics.Image(getDisplay( ),
// chartBufferWidth, chartBufferHeight);
// refreshBuffer = true;
// }
//
// // do we need to redraw the buffer?
// if (refreshBuffer) {
// if (scale) {
// sg2.scale(scaleX, scaleY);
// }
// // Performs the actual drawing here ...
// GC gci = new GC(chartBuffer);
// SWTGraphics2D sg2d = new SWTGraphics2D(gci, true);
// chart.draw(sg2d, new Rectangle2D.Double(0, 0, chartBufferWidth,
// chartBufferHeight), getAnchor( ), info);
// canvas.setData("double-buffer-image", chartBuffer);
// sg2d.dispose( ); // this disposes the new GC() also
// refreshBuffer = false;
// }
// // zap the buffer onto the canvas...
// sg2.drawImage(chartBuffer, 0, 0);
// }
// // or redrawing the chart every time...
// else {
		chart.draw(sg2, new Rectangle2D.Double(0,
				0,
				getBounds( ).width,
				getBounds( ).height), getAnchor( ), info);
		// }
		Rectangle area = getScreenDataArea( );
		if (verticalAxisTrace && area.x < verticalTraceLineX &&
				area.x + area.width > verticalTraceLineX) {
			e.gc.drawLine(verticalTraceLineX,
					area.y,
					verticalTraceLineX,
					area.y + area.height);
		}
		if (horizontalAxisTrace && area.y < horizontalTraceLineY &&
				area.y + area.height > horizontalTraceLineY) {
			e.gc.drawLine(area.x,
					horizontalTraceLineY,
					area.x + area.width,
					horizontalTraceLineY);
		}
		verticalTraceLineX = 0;
		horizontalTraceLineY = 0;
		if (zoomRectangle != null) {
			e.gc.drawRectangle(zoomRectangle);
		}

// if (e.data != null && e.data.equals(Boolean.TRUE)) {
// e.gc.dispose( );
// }
		sg2.dispose( );
	}

	/**
	 * Returns the X scale factor for the chart. This will be 1.0 if
	 * no scaling has been used.
	 * 
	 * @return The scale factor.
	 */
	public double getScaleX() {
		return this.scaleX;
	}

	/**
	 * Returns the Y scale factory for the chart. This will be 1.0 if
	 * no scaling has been used.
	 * 
	 * @return The scale factor.
	 */
	public double getScaleY() {
		return this.scaleY;
	}

	/**
	 * Returns the anchor point.
	 * 
	 * @return The anchor point (possibly <code>null</code>).
	 */
	public Point2D getAnchor() {
		return this.anchor;
	}

	/**
	 * Sets the anchor point. This method is provided for the use of
	 * subclasses, not end users.
	 * 
	 * @param anchor
	 *            the anchor point (<code>null</code> permitted).
	 */
	protected void setAnchor(Point2D anchor) {
		this.anchor = anchor;
	}

	/**
	 * Returns the chart contained in the panel.
	 * 
	 * @return The chart (possibly <code>null</code>).
	 */
	public JFreeChart getChart() {
		return this.chart;
	}

	/**
	 * Sets the chart that is displayed in the panel.
	 * 
	 * @param chart
	 *            the chart (<code>null</code> permitted).
	 */
	public void setChartAndRobotName(JFreeChart chart, String robotName) {
		// stop listening for changes to the existing chart
		if (this.chart != null) {
			this.chart.removeChangeListener(this);
		}

		// add the new chart
		this.chart = chart;
		if (chart != null) {
			this.chart.addChangeListener(this);
			Plot plot = chart.getPlot( );
			this.domainZoomable = false;
			this.rangeZoomable = false;
			if (plot instanceof Zoomable) {
				Zoomable z = (Zoomable) plot;
				this.domainZoomable = z.isDomainZoomable( );
				this.rangeZoomable = z.isRangeZoomable( );
				this.orientation = z.getOrientation( );
			}
			this.chart.setBackgroundPaint(SWTUtils.toAwtColor((getBackground( ))));
		}
		else {
			this.domainZoomable = false;
			this.rangeZoomable = false;
		}
		if (this.useBuffer) {
			this.refreshBuffer = true;
		}

		this.robotName = robotName;

		forceRedraw( );
	}

	/**
	 * Returns the zoom in factor.
	 * 
	 * @return The zoom in factor.
	 * @see #setZoomInFactor(double)
	 */
	public double getZoomInFactor() {
		return this.zoomInFactor;
	}

	/**
	 * Sets the zoom in factor.
	 * 
	 * @param factor
	 *            the factor.
	 * @see #getZoomInFactor()
	 */
	public void setZoomInFactor(double factor) {
		this.zoomInFactor = factor;
	}

	/**
	 * Returns the zoom out factor.
	 * 
	 * @return The zoom out factor.
	 * @see #setZoomOutFactor(double)
	 */
	public double getZoomOutFactor() {
		return this.zoomOutFactor;
	}

	/**
	 * Sets the zoom out factor.
	 * 
	 * @param factor
	 *            the factor.
	 * @see #getZoomOutFactor()
	 */
	public void setZoomOutFactor(double factor) {
		this.zoomOutFactor = factor;
	}

	/**
	 * Returns <code>true</code> if file extensions should be
	 * enforced, and <code>false</code> otherwise.
	 * 
	 * @return The flag.
	 */
	public boolean isEnforceFileExtensions() {
		return this.enforceFileExtensions;
	}

	/**
	 * Sets a flag that controls whether or not file extensions are
	 * enforced.
	 * 
	 * @param enforce
	 *            the new flag value.
	 */
	public void setEnforceFileExtensions(boolean enforce) {
		this.enforceFileExtensions = enforce;
	}

	/**
	 * Returns a point based on (x, y) but constrained to be within
	 * the bounds of the given rectangle. This method could be moved
	 * to JCommon.
	 * 
	 * @param x
	 *            the x-coordinate.
	 * @param y
	 *            the y-coordinate.
	 * @param area
	 *            the rectangle (<code>null</code> not permitted).
	 * @return A point within the rectangle.
	 */
	@SuppressWarnings("cast")
	private org.eclipse.swt.graphics.Point getPointInRectangle(int x, int y,
			Rectangle area) {
		x = (int) Math.max(area.x, Math.min(x, area.x + area.width));
		y = (int) Math.max(area.y, Math.min(y, area.y + area.height));
		return new org.eclipse.swt.graphics.Point(x, y);
	}

	/**
	 * Zooms in on an anchor point (specified in screen coordinate
	 * space).
	 * 
	 * @param x
	 *            the x value (in screen coordinates).
	 * @param y
	 *            the y value (in screen coordinates).
	 */
	public void zoomInBoth(double x, double y) {
		zoomInDomain(x, y);
		zoomInRange(x, y);
	}

	/**
	 * Decreases the length of the domain axis, centered about the
	 * given coordinate on the screen. The length of the domain axis
	 * is reduced by the value of {@link #getZoomInFactor()}.
	 * 
	 * @param x
	 *            the x coordinate (in screen coordinates).
	 * @param y
	 *            the y-coordinate (in screen coordinates).
	 */
	public void zoomInDomain(double x, double y) {
		Plot p = this.chart.getPlot( );
		if (p instanceof Zoomable) {
			Zoomable plot = (Zoomable) p;
			plot.zoomDomainAxes(this.zoomInFactor,
					this.info.getPlotInfo( ),
					translateScreenToJava2D(new Point((int) x, (int) y)));
		}
	}

	/**
	 * Decreases the length of the range axis, centered about the
	 * given coordinate on the screen. The length of the range axis is
	 * reduced by the value of {@link #getZoomInFactor()}.
	 * 
	 * @param x
	 *            the x-coordinate (in screen coordinates).
	 * @param y
	 *            the y coordinate (in screen coordinates).
	 */
	public void zoomInRange(double x, double y) {
		Plot p = this.chart.getPlot( );
		if (p instanceof Zoomable) {
			Zoomable z = (Zoomable) p;
			z.zoomRangeAxes(this.zoomInFactor,
					this.info.getPlotInfo( ),
					translateScreenToJava2D(new Point((int) x, (int) y)));
		}
	}

	/**
	 * Zooms out on an anchor point (specified in screen coordinate
	 * space).
	 * 
	 * @param x
	 *            the x value (in screen coordinates).
	 * @param y
	 *            the y value (in screen coordinates).
	 */
	public void zoomOutBoth(double x, double y) {
		zoomOutDomain(x, y);
		zoomOutRange(x, y);
	}

	/**
	 * Increases the length of the domain axis, centered about the
	 * given coordinate on the screen. The length of the domain axis
	 * is increased by the value of {@link #getZoomOutFactor()}.
	 * 
	 * @param x
	 *            the x coordinate (in screen coordinates).
	 * @param y
	 *            the y-coordinate (in screen coordinates).
	 */
	public void zoomOutDomain(double x, double y) {
		Plot p = this.chart.getPlot( );
		if (p instanceof Zoomable) {
			Zoomable z = (Zoomable) p;
			z.zoomDomainAxes(this.zoomOutFactor,
					this.info.getPlotInfo( ),
					translateScreenToJava2D(new Point((int) x, (int) y)));
		}
	}

	/**
	 * Increases the length the range axis, centered about the given
	 * coordinate on the screen. The length of the range axis is
	 * increased by the value of {@link #getZoomOutFactor()}.
	 * 
	 * @param x
	 *            the x coordinate (in screen coordinates).
	 * @param y
	 *            the y-coordinate (in screen coordinates).
	 */
	public void zoomOutRange(double x, double y) {
		Plot p = this.chart.getPlot( );
		if (p instanceof Zoomable) {
			Zoomable z = (Zoomable) p;
			z.zoomRangeAxes(this.zoomOutFactor,
					this.info.getPlotInfo( ),
					translateScreenToJava2D(new Point((int) x, (int) y)));
		}
	}

	/**
	 * Zooms in on a selected region.
	 * 
	 * @param selection
	 *            the selected region.
	 */
	@SuppressWarnings("cast")
	public void zoom(Rectangle selection) {

		// get the origin of the zoom selection in the Java2D space
		// used for
		// drawing the chart (that is, before any scaling to fit the
		// panel)
		Point2D selectOrigin = translateScreenToJava2D(new Point(selection.x,
				selection.y));
		PlotRenderingInfo plotInfo = this.info.getPlotInfo( );
		Rectangle scaledDataArea = getScreenDataArea((int) (selection.x + selection.width) / 2,
				(int) (selection.y + selection.height) / 2);
		if ((selection.height > 0) && (selection.width > 0)) {

			double hLower = (selection.x - scaledDataArea.x) /
					(double) scaledDataArea.width;
			double hUpper = (selection.x + selection.width - scaledDataArea.x) /
					(double) scaledDataArea.width;
			double vLower = (scaledDataArea.y + scaledDataArea.height -
					selection.y - selection.height) /
					(double) scaledDataArea.height;
			double vUpper = (scaledDataArea.y + scaledDataArea.height - selection.y) /
					(double) scaledDataArea.height;
			Plot p = this.chart.getPlot( );
			if (p instanceof Zoomable) {
				Zoomable z = (Zoomable) p;
				if (z.getOrientation( ) == PlotOrientation.HORIZONTAL) {
					z.zoomDomainAxes(vLower, vUpper, plotInfo, selectOrigin);
					z.zoomRangeAxes(hLower, hUpper, plotInfo, selectOrigin);
				}
				else {
					z.zoomDomainAxes(hLower, hUpper, plotInfo, selectOrigin);
					z.zoomRangeAxes(vLower, vUpper, plotInfo, selectOrigin);
				}
			}

		}

	}

	/**
	 * Receives notification of changes to the chart, and redraws the
	 * chart.
	 * 
	 * @param event
	 *            details of the chart change event.
	 */
	public void chartChanged(ChartChangeEvent event) {
		this.refreshBuffer = true;
		Plot plot = chart.getPlot( );
		if (plot instanceof Zoomable) {
			Zoomable z = (Zoomable) plot;
			this.orientation = z.getOrientation( );
		}
		forceRedraw( );
	}

	/**
	 * Forces a redraw of the canvas by invoking a new PaintEvent.
	 */
	public void forceRedraw() {
		if (SWTUtil.swtAssert(canvas)) {
// Event ev = new Event( );
// ev.gc = new GC(canvas);
// ev.x = 0;
// ev.y = 0;
// ev.data = Boolean.TRUE;
// ev.width = canvas.getBounds( ).width;
// ev.height = canvas.getBounds( ).height;
// ev.count = 0;
// canvas.notifyListeners(SWT.Paint, ev);
			canvas.redraw( );
		}
	}

	/**
	 * Restores the auto-range calculation on both axes.
	 */
	public void restoreAutoBounds() {
		restoreAutoDomainBounds( );
		restoreAutoRangeBounds( );
	}

	/**
	 * Restores the auto-range calculation on the domain axis.
	 */
	public void restoreAutoDomainBounds() {
		Plot p = this.chart.getPlot( );
		if (p instanceof Zoomable) {
			Zoomable z = (Zoomable) p;
			z.zoomDomainAxes(0.0,
					this.info.getPlotInfo( ),
					SWTUtils.toAwtPoint(this.zoomPoint));
		}
	}

	/**
	 * Restores the auto-range calculation on the range axis.
	 */
	public void restoreAutoRangeBounds() {
		Plot p = this.chart.getPlot( );
		if (p instanceof ValueAxisPlot) {
			Zoomable z = (Zoomable) p;
			z.zoomRangeAxes(0.0,
					this.info.getPlotInfo( ),
					SWTUtils.toAwtPoint(this.zoomPoint));
		}
	}

	/**
	 * Applies any scaling that is in effect for the chart drawing to
	 * the given rectangle.
	 * 
	 * @param rect
	 *            the rectangle.
	 * @return A new scaled rectangle.
	 */
	public Rectangle scale(Rectangle2D rect) {
		Rectangle insets = this.getClientArea( );
		int x = (int) Math.round(rect.getX( ) * getScaleX( )) + insets.x;
		int y = (int) Math.round(rect.getY( ) * this.getScaleY( )) + insets.y;
		int w = (int) Math.round(rect.getWidth( ) * this.getScaleX( ));
		int h = (int) Math.round(rect.getHeight( ) * this.getScaleY( ));
		return new Rectangle(x, y, w, h);
	}

	/**
	 * Returns the data area for the chart (the area inside the axes)
	 * with the current scaling applied (that is, the area as it
	 * appears on screen).
	 * 
	 * @return The scaled data area.
	 */
	public Rectangle getScreenDataArea() {
		Rectangle2D dataArea = this.info.getPlotInfo( ).getDataArea( );
		Rectangle clientArea = this.getClientArea( );
		int x = (int) (dataArea.getX( ) * this.scaleX + clientArea.x);
		int y = (int) (dataArea.getY( ) * this.scaleY + clientArea.y);
		int w = (int) (dataArea.getWidth( ) * this.scaleX);
		int h = (int) (dataArea.getHeight( ) * this.scaleY);
		return new Rectangle(x, y, w, h);
	}

	/**
	 * Returns the data area (the area inside the axes) for the plot
	 * or subplot, with the current scaling applied.
	 * 
	 * @param x
	 *            the x-coordinate (for subplot selection).
	 * @param y
	 *            the y-coordinate (for subplot selection).
	 * @return The scaled data area.
	 */
	public Rectangle getScreenDataArea(int x, int y) {
		PlotRenderingInfo plotInfo = this.info.getPlotInfo( );
		Rectangle result;
		if (plotInfo.getSubplotCount( ) == 0) {
			result = getScreenDataArea( );
		}
		else {
			// get the origin of the zoom selection in the Java2D
			// space used for
			// drawing the chart (that is, before any scaling to fit
			// the panel)
			Point2D selectOrigin = translateScreenToJava2D(new Point(x, y));
			int subplotIndex = plotInfo.getSubplotIndex(selectOrigin);
			if (subplotIndex == -1) {
				return null;
			}
			result = scale(plotInfo.getSubplotInfo(subplotIndex).getDataArea( ));
		}
		return result;
	}

	/**
	 * Translates a Java2D point on the chart to a screen location.
	 * 
	 * @param java2DPoint
	 *            the Java2D point.
	 * @return The screen location.
	 */
	public Point translateJava2DToScreen(Point2D java2DPoint) {
		Rectangle insets = this.getClientArea( );
		int x = (int) (java2DPoint.getX( ) * this.scaleX + insets.x);
		int y = (int) (java2DPoint.getY( ) * this.scaleY + insets.y);
		return new Point(x, y);
	}

	/**
	 * Translates a screen location to a Java SWT point.
	 * 
	 * @param screenPoint
	 *            the screen location.
	 * @return The Java2D coordinates.
	 */
	public Point translateScreenToJavaSWT(Point screenPoint) {
		Rectangle insets = this.getClientArea( );
		int x = (int) ((screenPoint.x - insets.x) / this.scaleX);
		int y = (int) ((screenPoint.y - insets.y) / this.scaleY);
		return new Point(x, y);
	}

	/**
	 * Translates a screen location to a Java2D point.
	 * 
	 * @param screenPoint
	 *            the screen location.
	 * @return The Java2D coordinates.
	 */
	public Point2D translateScreenToJava2D(Point screenPoint) {
		Rectangle insets = this.getClientArea( );
		int x = (int) ((screenPoint.x - insets.x) / this.scaleX);
		int y = (int) ((screenPoint.y - insets.y) / this.scaleY);
		return new Point2D.Double(x, y);
	}

	/**
	 * Returns the flag that controls whether or not a horizontal axis
	 * trace line is drawn over the plot area at the current mouse
	 * location.
	 * 
	 * @return A boolean.
	 */
	public boolean getHorizontalAxisTrace() {
		return this.horizontalAxisTrace;
	}

	/**
	 * A flag that controls trace lines on the horizontal axis.
	 * 
	 * @param flag
	 *            <code>true</code> enables trace lines for the
	 *            mouse pointer on the horizontal axis.
	 */
	public void setHorizontalAxisTrace(boolean flag) {
		this.horizontalAxisTrace = flag;
	}

	/**
	 * Returns the flag that controls whether or not a vertical axis
	 * trace line is drawn over the plot area at the current mouse
	 * location.
	 * 
	 * @return A boolean.
	 */
	public boolean getVerticalAxisTrace() {
		return this.verticalAxisTrace;
	}

	/**
	 * A flag that controls trace lines on the vertical axis.
	 * 
	 * @param flag
	 *            <code>true</code> enables trace lines for the
	 *            mouse pointer on the vertical axis.
	 */
	public void setVerticalAxisTrace(boolean flag) {
		this.verticalAxisTrace = flag;
	}

	/**
	 * @param displayToolTips
	 *            the displayToolTips to set
	 */
	public void setDisplayToolTips(boolean displayToolTips) {
		this.displayToolTips = displayToolTips;
	}

	/**
	 * Returns a string for the tooltip.
	 * 
	 * @param e
	 *            the mouse event.
	 * @return A tool tip or <code>null</code> if no tooltip is
	 *         available.
	 */
	public String getToolTipText(org.eclipse.swt.events.MouseEvent e) {
		String result = null;
		if (this.info != null) {
			EntityCollection entities = this.info.getEntityCollection( );
			if (entities != null) {
				Rectangle insets = getBounds( );
				ChartEntity entity = entities.getEntity((int) ((e.x - insets.x) / this.scaleX),
						(int) ((e.y - insets.y) / this.scaleY));
				if (entity != null) {
					result = entity.getToolTipText( );
				}
			}
		}
		return result;

	}

	/**
	 * The idea is to modify the zooming options depending on the type
	 * of chart being displayed by the panel.
	 * 
	 * @param x
	 *            horizontal position of the popup.
	 * @param y
	 *            vertical position of the popup.
	 */
	protected void displayPopupMenu(int x, int y) {
		if (popup != null && chart != null) {
			// go through each zoom menu item and decide whether or
			// not to
			// enable it...
			Plot plot = chart.getPlot( );
			boolean isDomainZoomable = false;
			boolean isRangeZoomable = false;
			if (plot instanceof Zoomable) {
				Zoomable z = (Zoomable) plot;
				isDomainZoomable = z.isDomainZoomable( );
				isRangeZoomable = z.isRangeZoomable( );
			}
			if (zoomInDomainMenuItem != null) {
				zoomInDomainMenuItem.setEnabled(isDomainZoomable);
			}
			if (zoomOutDomainMenuItem != null) {
				zoomOutDomainMenuItem.setEnabled(isDomainZoomable);
			}
			if (zoomResetDomainMenuItem != null) {
				zoomResetDomainMenuItem.setEnabled(isDomainZoomable);
			}

			if (zoomInRangeMenuItem != null) {
				zoomInRangeMenuItem.setEnabled(isRangeZoomable);
			}
			if (zoomOutRangeMenuItem != null) {
				zoomOutRangeMenuItem.setEnabled(isRangeZoomable);
			}

			if (zoomResetRangeMenuItem != null) {
				zoomResetRangeMenuItem.setEnabled(isRangeZoomable);
			}

			if (zoomInBothMenuItem != null) {
				zoomInBothMenuItem.setEnabled(isDomainZoomable &
						isRangeZoomable);
			}
			if (zoomOutBothMenuItem != null) {
				zoomOutBothMenuItem.setEnabled(isDomainZoomable &
						isRangeZoomable);
			}
			if (zoomResetBothMenuItem != null) {
				zoomResetBothMenuItem.setEnabled(isDomainZoomable &
						isRangeZoomable);
			}

			popup.setLocation(x, y);
			popup.setVisible(true);
		}

	}

	/**
	 * Creates a popup menu for the canvas.
	 * 
	 * @param zoom
	 *            include menu items for zooming.
	 * @param export
	 *            include menu item for exporting.
	 * @return The popup menu.
	 */
	protected Menu createPopupMenu(boolean zoom, boolean export) {

		Menu result = new Menu(this);
		boolean separator = false;

		if (export) {
			// TODO: exports
			XMenuItem exportPDF = new XMenuItem(result, SWT.PUSH);
			exportPDF.setTextForLocaleKey("HChartComposite.menu.item.exportPDF"); //$NON-NLS-1$
			exportPDF.setData(EXPORT_PDF_COMMAND);
			exportPDF.addSelectionListener(this);

			XMenuItem exportPNG = new XMenuItem(result, SWT.PUSH);
			exportPNG.setTextForLocaleKey("HChartComposite.menu.item.exportPNG"); //$NON-NLS-1$
			exportPNG.setData(EXPORT_PNG_COMMAND);
			exportPNG.addSelectionListener(this);

			XMenuItem exportJPEG = new XMenuItem(result, SWT.PUSH);
			exportJPEG.setTextForLocaleKey("HChartComposite.menu.item.exportJPG"); //$NON-NLS-1$
			exportJPEG.setData(EXPORT_JPEG_COMMAND);
			exportJPEG.addSelectionListener(this);
			separator = true;
		}

		if (zoom) {
			if (separator) {
				new MenuItem(result, SWT.SEPARATOR);
				separator = false;
			}

			Menu zoomInMenu = new Menu(result);
			XMenuItem zoomInMenuItem = new XMenuItem(result, SWT.CASCADE);
			zoomInMenuItem.setTextForLocaleKey("HChartComposite.menu.item.zoomIn"); //$NON-NLS-1$
			zoomInMenuItem.setMenu(zoomInMenu);

			zoomInBothMenuItem = new XMenuItem(zoomInMenu, SWT.PUSH);
			zoomInBothMenuItem.setTextForLocaleKey("HChartComposite.menu.item.allAxes"); //$NON-NLS-1$
			zoomInBothMenuItem.setData(ZOOM_IN_BOTH_COMMAND);
			zoomInBothMenuItem.addSelectionListener(this);

			new MenuItem(zoomInMenu, SWT.SEPARATOR);

			zoomInDomainMenuItem = new XMenuItem(zoomInMenu, SWT.PUSH);
			zoomInDomainMenuItem.setTextForLocaleKey("HChartComposite.menu.item.domainAxis"); //$NON-NLS-1$
			zoomInDomainMenuItem.setData(ZOOM_IN_DOMAIN_COMMAND);
			zoomInDomainMenuItem.addSelectionListener(this);

			zoomInRangeMenuItem = new XMenuItem(zoomInMenu, SWT.PUSH);
			zoomInRangeMenuItem.setTextForLocaleKey("HChartComposite.menu.item.rangeAxis"); //$NON-NLS-1$
			zoomInRangeMenuItem.setData(ZOOM_IN_RANGE_COMMAND);
			zoomInRangeMenuItem.addSelectionListener(this);

			Menu zoomOutMenu = new Menu(result);
			XMenuItem zoomOutMenuItem = new XMenuItem(result, SWT.CASCADE);
			zoomOutMenuItem.setTextForLocaleKey("HChartComposite.menu.item.zoomOut"); //$NON-NLS-1$
			zoomOutMenuItem.setMenu(zoomOutMenu);

			zoomOutBothMenuItem = new XMenuItem(zoomOutMenu, SWT.PUSH);
			zoomOutBothMenuItem.setTextForLocaleKey("HChartComposite.menu.item.allAxes"); //$NON-NLS-1$
			zoomOutBothMenuItem.setData(ZOOM_OUT_BOTH_COMMAND);
			zoomOutBothMenuItem.addSelectionListener(this);

			new MenuItem(zoomOutMenu, SWT.SEPARATOR);

			zoomOutDomainMenuItem = new XMenuItem(zoomOutMenu, SWT.PUSH);
			zoomOutDomainMenuItem.setTextForLocaleKey("HChartComposite.menu.item.domainAxis"); //$NON-NLS-1$
			zoomOutDomainMenuItem.setData(ZOOM_OUT_DOMAIN_COMMAND);
			zoomOutDomainMenuItem.addSelectionListener(this);

			zoomOutRangeMenuItem = new XMenuItem(zoomOutMenu, SWT.PUSH);
			zoomOutRangeMenuItem.setTextForLocaleKey("HChartComposite.menu.item.rangeAxis"); //$NON-NLS-1$
			zoomOutRangeMenuItem.setData(ZOOM_OUT_RANGE_COMMAND);
			zoomOutRangeMenuItem.addSelectionListener(this);

//			Menu autoRangeMenu = new Menu(result);
//			XMenuItem autoRangeMenuItem = new XMenuItem(result, SWT.CASCADE);
//			autoRangeMenuItem.setTextForLocaleKey("HChartComposite.menu.item.autoRange"); //$NON-NLS-1$
//			autoRangeMenuItem.setMenu(autoRangeMenu);
//
//			zoomResetBothMenuItem = new XMenuItem(autoRangeMenu, SWT.PUSH);
//			zoomResetBothMenuItem
//					.setTextForLocaleKey("HChartComposite.menu.item.allAxes"); //$NON-NLS-1$
//			zoomResetBothMenuItem.setData(ZOOM_RESET_BOTH_COMMAND);
//			zoomResetBothMenuItem.addSelectionListener(this);
//
//			new MenuItem(autoRangeMenu, SWT.SEPARATOR);
//
//			zoomResetDomainMenuItem = new XMenuItem(autoRangeMenu,
//					SWT.PUSH);
//			zoomResetDomainMenuItem
//					.setTextForLocaleKey("HChartComposite.menu.item.domainAxis"); //$NON-NLS-1$
//			zoomResetDomainMenuItem.setData(ZOOM_RESET_DOMAIN_COMMAND);
//			zoomResetDomainMenuItem.addSelectionListener(this);
//
//			zoomResetRangeMenuItem = new XMenuItem(autoRangeMenu, SWT.PUSH);
//			zoomResetRangeMenuItem
//					.setTextForLocaleKey("HChartComposite.menu.item.rangeAxis"); //$NON-NLS-1$
//			zoomResetRangeMenuItem.setData(ZOOM_RESET_RANGE_COMMAND);
//			zoomResetRangeMenuItem.addSelectionListener(this);
		}
		return result;
	}

	/**
	 * Handles action events generated by the popup menu.
	 * 
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(
	 *      org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent e) {

	}

	/**
	 * Handles action events generated by the popup menu.
	 * 
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(
	 *      org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent e) {
		String command = (String) ((MenuItem) e.getSource( )).getData( );

		if (command.equals(EXPORT_PDF_COMMAND)) {
			export(chart, EXPORT_PDF_COMMAND);
		}
		else if (command.equals(EXPORT_PNG_COMMAND)) {
			export(chart, EXPORT_PNG_COMMAND);
		}
		else if (command.equals(EXPORT_JPEG_COMMAND)) {
			export(chart, EXPORT_JPEG_COMMAND);
		}
		/*
		 * in the next zoomPoint.x and y replace by e.x and y for now.
		 * this helps to handle the mouse events and besides, those
		 * values are unused AFAIK.
		 */
		else if (command.equals(ZOOM_IN_BOTH_COMMAND)) {
			zoomInBoth(e.x, e.y);
		}
		else if (command.equals(ZOOM_IN_DOMAIN_COMMAND)) {
			zoomInDomain(e.x, e.y);
		}
		else if (command.equals(ZOOM_IN_RANGE_COMMAND)) {
			zoomInRange(e.x, e.y);
		}
		else if (command.equals(ZOOM_OUT_BOTH_COMMAND)) {
			zoomOutBoth(e.x, e.y);
		}
		else if (command.equals(ZOOM_OUT_DOMAIN_COMMAND)) {
			zoomOutDomain(e.x, e.y);
		}
		else if (command.equals(ZOOM_OUT_RANGE_COMMAND)) {
			zoomOutRange(e.x, e.y);
		}
		else if (command.equals(ZOOM_RESET_BOTH_COMMAND)) {
			restoreAutoBounds( );
		}
		else if (command.equals(ZOOM_RESET_DOMAIN_COMMAND)) {
			restoreAutoDomainBounds( );
		}
		else if (command.equals(ZOOM_RESET_RANGE_COMMAND)) {
			restoreAutoRangeBounds( );
		}
		forceRedraw( );
	}

	/**
	 * Exports the given chart with the given export command, f.e.
	 * export as PDF.
	 * 
	 * @param chart
	 *            The chart to export.
	 * @param command
	 *            The export command.
	 */
	private void export(final JFreeChart chart, final String command) {
		if (Util.isEmpty(robotName)) {
			robotName = "unknown_robot"; //$NON-NLS-1$
		}
		SWTUtil.showBusyWhile(parent.getShell( ), new Runnable( ) {

			public void run() {
				if (command.equals(EXPORT_PDF_COMMAND)) {
					ChartUtil.exportPDF(chart, robotName);
				}
				else if (command.equals(EXPORT_PNG_COMMAND)) {
					ChartUtil.exportPNG(chart, robotName);
				}
				else if (command.equals(EXPORT_JPEG_COMMAND)) {
					ChartUtil.exportJPG(chart, robotName);
				}
			}
		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.widgets.Control#setBackground(org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setBackground(Color color) {
		super.setBackground(color);
		if (canvas != null) {
			canvas.setBackground(color);
		}
		if (chart != null) {
			chart.setBackgroundPaint(SWTUtils.toAwtColor(color));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.events.ControlListener#controlMoved(org.eclipse.swt.events.ControlEvent)
	 */
	public void controlMoved(ControlEvent e) {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.events.ControlListener#controlResized(org.eclipse.swt.events.ControlEvent)
	 */
	public void controlResized(ControlEvent e) {
		forceRedraw( );
	}
}
