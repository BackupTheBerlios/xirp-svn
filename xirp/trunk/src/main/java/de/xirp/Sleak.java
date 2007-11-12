package de.xirp;

/*********************************************************************
 * Copyright (c) 2004 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html Contributors: IBM
 * Corporation - initial API and implementation
 ********************************************************************/

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

/**
 * Instructions on how to use the Sleak tool with a standlaone SWT
 * example: Modify the main method below to launch your application.
 * Run Sleak.
 */
public class Sleak {

	static Display display;
	Shell shell;
	List list;
	Canvas canvas;
	Button start, stop, check;
	Text text;
	Label label;

	Object[] oldObjects = new Object[0];
	Error[] oldErrors = new Error[0];
	Object[] objects = new Object[0];
	Error[] errors = new Error[0];

	/**
	 * The Logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(Xirp.class);

	public static void main(String[] args) {
		DeviceData data = new DeviceData( );
		data.tracking = true;
		Display display = new Display(data);
		Sleak sleak = new Sleak( );
		sleak.open( );

		// Launch you application here
		Xirp.run( );

		while (!sleak.shell.isDisposed( )) {
			if (!display.readAndDispatch( )) {
				display.sleep( );
			}
		}
		display.dispose( );
	}

	void open() {
		display = Display.getCurrent( );
		shell = new Shell(display);
		shell.setText("S-Leak"); //$NON-NLS-1$
		list = new List(shell, SWT.BORDER | SWT.V_SCROLL);
		list.addListener(SWT.Selection, new Listener( ) {

			public void handleEvent(Event event) {
				refreshObject( );
			}
		});
		text = new Text(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		canvas = new Canvas(shell, SWT.BORDER);
		canvas.addListener(SWT.Paint, new Listener( ) {

			public void handleEvent(Event event) {
				paintCanvas(event);
			}
		});
		check = new Button(shell, SWT.CHECK);
		check.setText("Stack"); //$NON-NLS-1$
		check.addListener(SWT.Selection, new Listener( ) {

			public void handleEvent(Event e) {
				toggleStackTrace( );
			}
		});
		start = new Button(shell, SWT.PUSH);
		start.setText("Snap"); //$NON-NLS-1$
		start.addListener(SWT.Selection, new Listener( ) {

			public void handleEvent(Event event) {
				refreshAll( );
			}
		});
		stop = new Button(shell, SWT.PUSH);
		stop.setText("Diff"); //$NON-NLS-1$
		stop.addListener(SWT.Selection, new Listener( ) {

			public void handleEvent(Event event) {
				refreshDifference( );
			}
		});
		label = new Label(shell, SWT.BORDER);
		label.setText("0 object(s)"); //$NON-NLS-1$
		shell.addListener(SWT.Resize, new Listener( ) {

			public void handleEvent(Event e) {
				layout( );
			}
		});
		check.setSelection(false);
		text.setVisible(false);
		Point size = shell.getSize( );
		shell.setSize(size.x / 2, size.y / 2);
		shell.open( );
	}

	void refreshLabel() {
		int colors = 0, cursors = 0, fonts = 0, gcs = 0, images = 0, regions = 0;
		for (int i = 0; i < objects.length; i++) {
			Object object = objects[i];
			if (object instanceof Color) {
				colors++;
			}
			if (object instanceof Cursor) {
				cursors++;
			}
			if (object instanceof Font) {
				fonts++;
			}
			if (object instanceof GC) {
				gcs++;
			}
			if (object instanceof Image) {
				images++;
			}
			if (object instanceof Region) {
				regions++;
			}
		}
		String string = ""; //$NON-NLS-1$
		if (colors != 0) {
			string += colors + " Color(s)\n"; //$NON-NLS-1$
		}
		if (cursors != 0) {
			string += cursors + " Cursor(s)\n"; //$NON-NLS-1$
		}
		if (fonts != 0) {
			string += fonts + " Font(s)\n"; //$NON-NLS-1$
		}
		if (gcs != 0) {
			string += gcs + " GC(s)\n"; //$NON-NLS-1$
		}
		if (images != 0) {
			string += images + " Image(s)\n"; //$NON-NLS-1$
		}
		if (regions != 0) {
			string += regions + " Region(s)\n"; //$NON-NLS-1$
		}
		if (string.length( ) != 0) {
			string = string.substring(0, string.length( ) - 1);
		}
		label.setText(string);
	}

	void refreshDifference() {
		DeviceData info = display.getDeviceData( );
		if (!info.tracking) {
			MessageBox dialog = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
			dialog.setText(shell.getText( ));
			dialog.setMessage("Warning: Device is not tracking resource allocation"); //$NON-NLS-1$
			dialog.open( );
		}
		Object[] newObjects = info.objects;
		Error[] newErrors = info.errors;
		Object[] diffObjects = new Object[newObjects.length];
		Error[] diffErrors = new Error[newErrors.length];
		int count = 0;
		for (int i = 0; i < newObjects.length; i++) {
			int index = 0;
			while (index < oldObjects.length) {
				if (newObjects[i] == oldObjects[index]) {
					break;
				}
				index++;
			}
			if (index == oldObjects.length) {
				diffObjects[count] = newObjects[i];
				diffErrors[count] = newErrors[i];
				count++;
			}
		}
		objects = new Object[count];
		errors = new Error[count];
		System.arraycopy(diffObjects, 0, objects, 0, count);
		System.arraycopy(diffErrors, 0, errors, 0, count);
		list.removeAll( );
		text.setText(""); //$NON-NLS-1$
		canvas.redraw( );
		for (int i = 0; i < objects.length; i++) {
			list.add(objectName(objects[i]));
		}
		refreshLabel( );
		layout( );
	}

	String objectName(Object object) {
		String string = object.toString( );
		int index = string.lastIndexOf('.');
		if (index == -1) {
			return string;
		}
		return string.substring(index + 1, string.length( ));
	}

	void toggleStackTrace() {
		refreshObject( );
		layout( );
	}

	void paintCanvas(Event event) {
		canvas.setCursor(null);
		int index = list.getSelectionIndex( );
		if (index == -1) {
			return;
		}
		GC gc = event.gc;
		Object object = objects[index];
		if (object instanceof Color) {
			if (((Color) object).isDisposed( )) {
				return;
			}
			gc.setBackground((Color) object);
			gc.fillRectangle(canvas.getClientArea( ));
			return;
		}
		if (object instanceof Cursor) {
			if (((Cursor) object).isDisposed( )) {
				return;
			}
			canvas.setCursor((Cursor) object);
			return;
		}
		if (object instanceof Font) {
			if (((Font) object).isDisposed( )) {
				return;
			}
			gc.setFont((Font) object);
			FontData[] array = gc.getFont( ).getFontData( );
			String string = ""; //$NON-NLS-1$
			String lf = text.getLineDelimiter( );
			for (int i = 0; i < array.length; i++) {
				FontData data = array[i];
				String style = "NORMAL"; //$NON-NLS-1$
				int bits = data.getStyle( );
				if (bits != 0) {
					if ((bits & SWT.BOLD) != 0) {
						style = "BOLD "; //$NON-NLS-1$
					}
					if ((bits & SWT.ITALIC) != 0) {
						style += "ITALIC"; //$NON-NLS-1$
					}
				}
				string += data.getName( ) + " " + data.getHeight( ) + " " //$NON-NLS-1$ //$NON-NLS-2$
						+ style + lf;
			}
			gc.drawString(string, 0, 0);
			return;
		}
		// NOTHING TO DRAW FOR GC
		// if (object instanceof GC) {
		// return;
		// }
		if (object instanceof Image) {
			if (((Image) object).isDisposed( )) {
				return;
			}
			gc.drawImage((Image) object, 0, 0);
			return;
		}
		if (object instanceof Region) {
			if (((Region) object).isDisposed( )) {
				return;
			}
			String string = ((Region) object).getBounds( ).toString( );
			gc.drawString(string, 0, 0);
			return;
		}
	}

	void refreshObject() {
		int index = list.getSelectionIndex( );
		if (index == -1) {
			return;
		}
		if (check.getSelection( )) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream( );
			PrintStream s = new PrintStream(stream);
			errors[index].printStackTrace(s);
			text.setText(stream.toString( ));
			text.setVisible(true);
			canvas.setVisible(false);
		}
		else {
			canvas.setVisible(true);
			text.setVisible(false);
			canvas.redraw( );
		}
	}

	void refreshAll() {
		oldObjects = new Object[0];
		oldErrors = new Error[0];
		refreshDifference( );
		oldObjects = objects;
		oldErrors = errors;
	}

	void layout() {
		Rectangle rect = shell.getClientArea( );
		int width = 0;
		String[] items = list.getItems( );
		GC gc = new GC(list);
		for (int i = 0; i < objects.length; i++) {
			width = Math.max(width, gc.stringExtent(items[i]).x);
		}
		gc.dispose( );
		Point size1 = start.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Point size2 = stop.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Point size3 = check.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Point size4 = label.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		width = Math.max(size1.x, Math.max(size2.x, Math.max(size3.x, width)));
		width = Math.max(64, Math.max(size4.x, list.computeSize(width,
				SWT.DEFAULT).x));
		start.setBounds(0, 0, width, size1.y);
		stop.setBounds(0, size1.y, width, size2.y);
		check.setBounds(0, size1.y + size2.y, width, size3.y);
		label.setBounds(0, rect.height - size4.y, width, size4.y);
		int height = size1.y + size2.y + size3.y;
		list.setBounds(0, height, width, rect.height - height - size4.y);
		text.setBounds(width, 0, rect.width - width, rect.height);
		canvas.setBounds(width, 0, rect.width - width, rect.height);
	}
}
