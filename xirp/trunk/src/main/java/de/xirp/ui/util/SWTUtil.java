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
 * SWTUtil.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   Rabea Gransberger [rgransberger AT web.de]
 *
 * Changes
 * -------
 * 23.01.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.util;

import java.io.File;
import java.io.IOException;

import javolution.lang.MathLib;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Widget;

import de.xirp.ui.Application;
import de.xirp.util.DoublePoint;
import de.xirp.util.I18n;

/**
 * Contains frequently used UI-methods, all accessed statically.<br>
 * 
 * @author Matthias Gernand
 * @author Rabea Gransberger
 */
public final class SWTUtil {

	/**
	 * SWT allows the GUI thread to access SWT widgets only.<br>
	 * If another thread tries to access a widget an "invalid thread
	 * access exception" is thrown.<br>
	 * <br>
	 * In order to allow access to SWT widgets from other threads to
	 * the GUI thread this method may be used.<br>
	 * <br>
	 * The {@link Runnable} contains the code to execute in his
	 * <code>run</code>-Method.<br>
	 * <br>
	 * There is only <b>ONE</b> GUI thread, so remember to put only
	 * minimized code and no expensive calculations into the
	 * {@link Runnable} because otherwise the GUI will be blocked to
	 * long.<br>
	 * <br>
	 * This method returns immediately not waiting for the given
	 * {@link Runnable} to be executed.
	 * 
	 * @param runnable
	 *            Runnable containing the Code to execute in the GUI
	 *            thread
	 * @see Display#asyncExec(Runnable)
	 */
	public static final void asyncExec(final Runnable runnable) {
		Display display = Application.getApplication( ).getDisplay( );
		if (SWTUtil.swtAssert(display)) {
			display.asyncExec(runnable);
		}
	}

	/**
	 * SWT allows the GUI thread to access SWT widgets only.<br>
	 * If another thread tries to access a widget an "invalid thread
	 * access exception" is thrown.<br>
	 * <br>
	 * In order to allow access to SWT widgets from other threads to
	 * the GUI thread this method may be used.<br>
	 * <br>
	 * The {@link Runnable} contains the code to execute in his
	 * <code>run</code>-Method.<br>
	 * <br>
	 * There is only <b>ONE</b> GUI thread, so remember to put only
	 * minimized code and no expensive calculations into the
	 * {@link Runnable} because otherwise the GUI will be blocked to
	 * long.<br>
	 * <br>
	 * This method returns when the given {@link Runnable} was
	 * executed.
	 * 
	 * @param runnable
	 *            Runnable containing the code to execute in the GUI
	 *            thread
	 * @see Display#syncExec(Runnable)
	 */
	public static final void syncExec(final Runnable runnable) {
		Display display = Application.getApplication( ).getDisplay( );
		if (SWTUtil.swtAssert(display)) {
			display.syncExec(runnable);
		}
	}

	/**
	 * Centers the given dialog shell on the screen.
	 * 
	 * @param dialogShell
	 *            Shell of the dialog to center
	 */
	public static final void centerDialog(final Shell dialogShell) {

		Rectangle shellBounds = dialogShell.getParent( ).getBounds( );
		Point dialogSize = dialogShell.getSize( );

		dialogShell.setLocation((shellBounds.x + (shellBounds.width - dialogSize.x) / 2),
				(shellBounds.y + (shellBounds.height - dialogSize.y) / 2));
	}

	/**
	 * Convenience method for adding a grid layout to a component.<br>
	 * 
	 * @param toLayout
	 *            the Composite to add the layout to
	 * @param numColumns
	 *            number of columns for the grid layout
	 * @param makeColumnsEqualWidth
	 *            <code>true</code> if all the columns of the layout
	 *            should have equal width (use <code>true</code> if
	 *            there is only one column)
	 * @return The layout which was added to the component
	 * @see org.eclipse.swt.layout.GridLayout
	 */
	public static final GridLayout setGridLayout(final Composite toLayout,
			final int numColumns, final boolean makeColumnsEqualWidth) {

		GridLayout gl = new GridLayout( );
		gl.numColumns = numColumns;
		gl.makeColumnsEqualWidth = makeColumnsEqualWidth;
		toLayout.setLayout(gl);

		return gl;
	}

	/**
	 * Sets the margins of the given layout to <code>0</code>.
	 * 
	 * @param layout
	 *            the layout
	 */
	public static final void resetMargins(GridLayout layout) {
		layout.marginHeight = 0;
		layout.marginWidth = 0;
	}

	/**
	 * Sets the spacings of the given layout to <code>0</code>.
	 * 
	 * @param layout
	 *            the layout
	 */
	public static final void resetSpacings(GridLayout layout) {
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
	}

	/**
	 * Sets the grid data for a component which parent has a grid
	 * layout.<br>
	 * 
	 * @param addLayoutDataTo
	 *            the control to add the data to
	 * @param grabExcessHorizontalSpace
	 *            grabExcessHorizontalSpace specifies whether the
	 *            width of the cell changes depending on the size of
	 *            the parent Composite
	 * @param grabExcessVerticalSpace
	 *            grabExcessVerticalSpace specifies whether the height
	 *            of the cell changes depending on the size of the
	 *            parent Composite
	 * @param horizontalAlignment
	 *            horizontalAlignment specifies how controls will be
	 *            positioned horizontally within a cell.
	 * @param verticalAlignment
	 *            verticalAlignment specifies how controls will be
	 *            positioned vertically within a cell.
	 * @param horizontalSpan
	 *            horizontalSpan specifies the number of column cells
	 *            that the control will take up.
	 * @param verticalSpan
	 *            verticalSpan specifies the number of row cells that
	 *            the control will take up.
	 * @return GirdData<br>
	 *         The grid data which was added to the control
	 * @see org.eclipse.swt.layout.GridData
	 */
	public static final GridData setGridData(final Control addLayoutDataTo,
			final boolean grabExcessHorizontalSpace,
			final boolean grabExcessVerticalSpace,
			final int horizontalAlignment, final int verticalAlignment,
			final int horizontalSpan, final int verticalSpan) {

		GridData gd = new GridData( );
		gd.grabExcessHorizontalSpace = grabExcessHorizontalSpace;
		gd.grabExcessVerticalSpace = grabExcessVerticalSpace;
		gd.horizontalAlignment = horizontalAlignment;
		gd.verticalAlignment = verticalAlignment;
		gd.horizontalSpan = horizontalSpan;
		gd.verticalSpan = verticalSpan;
		addLayoutDataTo.setLayoutData(gd);

		return gd;
	}

	/**
	 * Converts the given Point which is relative to the first Control
	 * to a Point relative to the second control.<br>
	 * 
	 * @param x
	 *            X Coordinate of the Point to convert
	 * @param y
	 *            Y Coordinate of the Point to convert
	 * @param from
	 *            Control to which the Point coordinates are relative
	 * @param to
	 *            Control to which the Point coordinates should be
	 *            relative after conversion
	 * @return Point<br>
	 *         Relative point to the second control
	 */
	public static final Point convertPoint(final int x, final int y,
			final Control from, final Control to) {
		Point point = new Point(x, y);
		return convertPoint(point, from, to);
	}

	/**
	 * Converts the given Point which is relative to the first Control
	 * to a Point relative to the second control.<br>
	 * 
	 * @param point
	 *            The Point to Convert
	 * @param from
	 *            Control to which the Point coordinates are relative
	 * @param to
	 *            Control to which the Point coordinates should be
	 *            relative after conversion
	 * @return Point<br>
	 *         relative point to the second control
	 */
	public static final Point convertPoint(final Point point,
			final Control from, final Control to) {
		Point displayMouse = from.toDisplay(point);
		Point convert = to.toControl(displayMouse);

		return convert;
	}

	/**
	 * Blocks a dialog from returning a value to soon. A event loop is
	 * installed. When the dialog shell disposes the code after this
	 * method call will be executed. Like: returning <code>true</code>
	 * or something else.<br>
	 * 
	 * @param dialogShell
	 *            The dialogs shell
	 */
	public static final void blockDialogFromReturning(final Shell dialogShell) {
		Display display = dialogShell.getParent( ).getDisplay( );
		while (!dialogShell.isDisposed( )) {
			if (!display.readAndDispatch( )) {
				display.sleep( );
			}
		}
	}

	/**
	 * Shows a busy cursor while the given Runnable is running.<br>
	 * 
	 * @param parent
	 *            The parent shell
	 * @param runnable
	 *            The runnable to execute
	 * @see java.lang.Runnable
	 */
	public static final void showBusyWhile(final Shell parent,
			final Runnable runnable) {
		BusyIndicator.showWhile(parent.getDisplay( ), runnable);
	}

	/**
	 * Packs the columns of the given table.
	 * 
	 * @param table
	 *            The table to pack.
	 */
	public static final void packTable(final Table table) {
		for (TableColumn c : table.getColumns( )) {
			c.pack( );
		}
	}

	/**
	 * Checks if, the given constant is set in the given style.
	 * 
	 * @param style
	 *            The style
	 * @param constant
	 *            The constant to check
	 * @return <code>true</code> if the constant is set.<br>
	 *         <code>false</code> if the constant is not set.<br>
	 */
	public static final boolean checkStyle(final int style, final int constant) {
		return ((style & constant) != 0);
	}

	/**
	 * Disposes the given widget only if it is not <code>null</code>
	 * or not disposed.
	 * 
	 * @param widget
	 *            The widget to dispose.
	 * @return <code>true</code> if dispose was successful<br>
	 *         <code>false</coed> if dispose was not successful<br>
	 */
	public static final boolean secureDispose(Widget widget) {
		if (SWTUtil.swtAssert(widget)) {
			widget.dispose( );
			return true;
		}
		return false;
	}

	/**
	 * Disposes the given device only if it is not <code>null</code>
	 * or not disposed.
	 * 
	 * @param device
	 *            The device to dispose.
	 * @return <code>true</code> if dispose was successful<br>
	 *         <code>false</coed> if dispose was not successful<br>
	 */
	public static final boolean secureDispose(Device device) {
		if (SWTUtil.swtAssert(device)) {
			device.dispose( );
			return true;
		}
		return false;
	}

	/**
	 * Disposes the given resource only if it is not <code>null</code>
	 * or not disposed.
	 * 
	 * @param resource
	 *            The widget to dispose.
	 * @return <code>true</code> if dispose was successful<br>
	 *         <code>false</coed> if dispose was not successful<br>
	 */
	public static final boolean secureDispose(Resource resource) {
		if (SWTUtil.swtAssert(resource)) {
			resource.dispose( );
			return true;
		}
		return false;
	}

	/**
	 * Tests if a widget is not <code>null</code> and not disposed.
	 * 
	 * @param widget
	 *            The widget to test.
	 * @return <code>true</code> if widget is not <code>null</code>
	 *         and not disposed.<br>
	 *         <code>false</code> if widget is either
	 *         <code>null</code> or disposed.<br>
	 */
	public static final boolean swtAssert(Widget widget) {
		return (widget != null && !widget.isDisposed( ));
	}

	/**
	 * Tests if a device is not <code>null</code> and not disposed.
	 * 
	 * @param device
	 *            The device to test.
	 * @return <code>true</code> if device is not <code>null</code>
	 *         and not disposed.<br>
	 *         <code>false</code> if device is either
	 *         <code>null</code> or disposed.<br>
	 */
	public static final boolean swtAssert(Device device) {
		return (device != null && !device.isDisposed( ));
	}

	/**
	 * Tests if a resource is not <code>null</code> and not
	 * disposed.
	 * 
	 * @param resource
	 *            The resource to test.
	 * @return <code>true</code> if resource is not
	 *         <code>null</code> and not disposed.<br>
	 *         <code>false</code> if resource is either
	 *         <code>null</code> or disposed.<br>
	 */
	public static final boolean swtAssert(Resource resource) {
		return (resource != null && !resource.isDisposed( ));
	}

	/**
	 * Rotates the given point around the given center.
	 * 
	 * @param p
	 *            the point to rotate
	 * @param center
	 *            the center of rotation
	 * @param phi
	 *            the angle for rotation in radian.
	 * @return the rotated point
	 */
	public static final Point rotate(Point p, Point center, double phi) {
		double cosPhi = MathLib.cos(phi);
		double sinPhi = MathLib.sin(phi);

		Point newPoint = new Point(p.x, p.y);

		newPoint.x = newPoint.x - center.x;
		newPoint.y = newPoint.y - center.y;

		double newX = sinPhi * newPoint.x + cosPhi * newPoint.y + center.x;
		double newY = -cosPhi * newPoint.x + sinPhi * newPoint.y + center.y;

		newPoint.x = (int) Math.rint(newX);
		newPoint.y = (int) Math.rint(newY);
		return newPoint;
	}

	/**
	 * Rotates the given point around the given center.
	 * 
	 * @param p
	 *            the point to rotate
	 * @param center
	 *            the center of rotation
	 * @param phi
	 *            the angle for rotation in radian.
	 * @return the rotated point
	 */
	public static final DoublePoint rotate(DoublePoint p, DoublePoint center,
			double phi) {
		double cosPhi = MathLib.cos(phi);
		double sinPhi = MathLib.sin(phi);

		DoublePoint newPoint = new DoublePoint(p.x, p.y);

		newPoint.x = newPoint.x - center.x;
		newPoint.y = newPoint.y - center.y;

		double newX = sinPhi * newPoint.x + cosPhi * newPoint.y + center.x;
		double newY = -cosPhi * newPoint.x + sinPhi * newPoint.y + center.y;

		newPoint.x = (int) Math.rint(newX);
		newPoint.y = (int) Math.rint(newY);
		return newPoint;
	}

	/**
	 * Opens the given file with the default program for the files
	 * extension as registered at the OS.
	 * 
	 * @param path
	 *            the absolute path to the file.
	 * @throws IOException
	 *             thrown if no registered program for the given file
	 *             extension was found
	 */
	public static final void openFile(String path) throws IOException {
		Program p = Program.findProgram(FilenameUtils.getExtension(path));
		if (p != null) {
			p.execute(path);
		}
		else {
			throw new IOException(I18n.getString("SWTUtil.exception.noSuitableProgramFound")); //$NON-NLS-1$
		}
	}

	/**
	 * Opens the given file with the default program for the files
	 * extension as registered at the OS.
	 * 
	 * @param file
	 *            the file
	 * @throws IOException
	 *             thrown if no registered program for the given file
	 *             extension was found
	 */
	public static final void openFile(File file) throws IOException {
		openFile(file.getAbsolutePath( ));
	}
}
