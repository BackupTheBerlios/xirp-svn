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
 * XMessageBox.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 01.02.2007:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.XButton;
import de.xirp.ui.widgets.custom.XComposite;
import de.xirp.ui.widgets.custom.XDialog;
import de.xirp.ui.widgets.custom.XLabel;
import de.xirp.ui.widgets.custom.XShell;
import de.xirp.ui.widgets.custom.XButton.XButtonType;

/**
 * This dialog is used to communicate an interact with
 * the user. The purpose is corresponding to the 
 * {@link org.eclipse.swt.widgets.MessageBox} dialog from SWT, 
 * but the {@link de.xirp.ui.widgets.dialogs.XMessageBox}
 * offers on board i18n and fits into the appearance of the application.
 * 
 * @author Matthias Gernand
 * 
 * @see org.eclipse.swt.widgets.MessageBox
 */
public final class XMessageBox extends XDialog {

	/**
	 * The parent.
	 */
	private Shell parent;
	/**
	 * The dialogs shell.
	 */
	private XShell dialogShell;
	/**
	 * The type of the message shown.
	 * 
	 * @see de.xirp.ui.widgets.dialogs.XMessageBox.HMessageBoxType
	 */
	private HMessageBoxType type;
	/**
	 * The text of the message.
	 */
	private String message;
	/**
	 * The i18n key of the message.
	 */
	private String messageKey;
	/**
	 * The message arguments.
	 */
	private Object[] messageArgs;
	/**
	 * An image.
	 */
	private Image imgBig;
	/**
	 * The button types of the used buttons.
	 * 
	 * @see de.xirp.ui.widgets.custom.XButton.XButtonType
	 */
	private XButtonType[] types;
	/**
	 * Helper variable for holding the number
	 * of rows used in the layout of the box
	 * to open. 
	 */
	private int rowNumber;
	/**
	 * The default button style if no button types are given.
	 * 
	 * @see de.xirp.ui.widgets.custom.XButton.XButtonType
	 */
	private XButtonType btnType = XButtonType.CANCEL;
	/**
	 * The width of the dialog.
	 */
	private static final int WIDTH = 312;
	/**
	 * The height of the dialog.
	 */
	private static final int HEIGHT = 128;

	/**
	 * This enumeration holds constants for 
	 * indicating the type of the message to
	 * show.
	 * 
	 * @author Matthias Gernand
	 *
	 */
	public enum HMessageBoxType {
		/**
		 * An information dialog.
		 */
		INFO,
		/**
		 * A waring dialog.
		 */
		WARN,
		/**
		 * A question dialog.
		 */
		QUESTION,
		/**
		 * An error dialog.
		 */
		ERROR
	}

	/**
	 * Constructs a new message box. The type of the message dialog
	 * is specified by the <code>type</code> parameter. Several
	 * button types can optionally be given to the constructor.
	 * If no type is given only one cancel button will be generated.
	 * If f.e. {@link de.xirp.ui.widgets.custom.XButton.XButtonType#OK}
	 * and {@link de.xirp.ui.widgets.custom.XButton.XButtonType#CLOSE}
	 * are given the dialog will have an "ok" and a "close" button.
	 * 
	 * @param parent
	 * 				The parent.
	 * @param type
	 * 				The type of the message.
	 * @param types
	 * 				The optional buttons to display.
	 * 
	 * @see de.xirp.ui.widgets.custom.XButton.XButtonType
	 */
	public XMessageBox(Shell parent, HMessageBoxType type, XButtonType... types) {
		super(parent, SWT.DIALOG_TRIM | SWT.NO_TRIM);
		this.parent = parent;
		this.type = type;
		this.types = types;
		this.rowNumber = types.length;
		if (rowNumber == 1) {
			rowNumber = 2;
		}
	}

	/**
	 * Sets the message to show in the message box.
	 * 
	 * @param message
	 * 				The message to show.
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Sets the corresponding message for the given i18n key.
	 * 
	 * @param key
	 * 			The key.
	 * @param objects
	 * 			The arguments.
	 */
	public void setMessageForLocaleKey(String key, Object... objects) {
		this.messageKey = key;
		this.messageArgs = objects;
		setMessage(getHandler( ).getString(messageKey, messageArgs));
	}

	/**
	 * Returns the message text.
	 * 
	 * @return The message.
	 */
	private String getMessage() {
		return this.message;
	}

	/**
	 * Returns the message i18n key.
	 * 
	 * @return The key.
	 */
	private String getMessageKey() {
		return this.messageKey;
	}

	/**
	 * Returns the message arguments.
	 * 
	 * @return The arguments.
	 */
	private Object[] getMessageArgs() {
		return this.messageArgs;
	}

	/**
	 * Opens the message box. Returns the type of the clicked button,
	 * or the default type.
	 * 
	 * @return The type of the clicked button, or the default
	 * 			type {@link de.xirp.ui.widgets.custom.XButton.XButtonType#CANCEL}
	 * 			if the dialog was closed using the "x" in the upper right corner or the 
	 * 			"ESC" key.
	 */
	public XButtonType open() {
		dialogShell = new XShell(parent, SWT.DIALOG_TRIM
				| SWT.APPLICATION_MODAL);

		dialogShell.addShellListener(new ShellAdapter( ) {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.ShellAdapter#shellClosed(org.eclipse.swt.events.ShellEvent)
			 */
			@Override
			public void shellClosed(ShellEvent e) {
				SWTUtil.secureDispose(dialogShell);
			}
		});

		switch (type) {
			case INFO:
				dialogShell.setImage(ImageManager.getSystemImage(SystemImage.DIALOG_WARNING));
				imgBig = ImageManager.getSystemImage(SystemImage.DIALOG_WARNING);
				break;
			case WARN:
				dialogShell.setImage(ImageManager.getSystemImage(SystemImage.DIALOG_WARNING));
				imgBig = ImageManager.getSystemImage(SystemImage.DIALOG_WARNING);
				break;
			case QUESTION:
				dialogShell.setImage(ImageManager.getSystemImage(SystemImage.DIALOG_QUESTION));
				imgBig = ImageManager.getSystemImage(SystemImage.DIALOG_QUESTION);
				break;
			case ERROR:
				dialogShell.setImage(ImageManager.getSystemImage(SystemImage.DIALOG_ERROR));
				imgBig = ImageManager.getSystemImage(SystemImage.DIALOG_ERROR);
				break;
		}

		dialogShell.setSize(WIDTH, HEIGHT);
		SWTUtil.setGridLayout(dialogShell, rowNumber, false);
		dialogShell.setTextForLocaleKey(getTextKey( ), getTextArgs( ));

		XComposite img = new XComposite(dialogShell, SWT.DOUBLE_BUFFERED);
		img.setSize(52, 52);
		img.addPaintListener(new PaintListener( ) {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
			 */
			public void paintControl(PaintEvent event) {
				GC gc = event.gc;
				gc.drawImage(imgBig, 5, 5);
			}
		});
		SWTUtil.setGridData(img, false, false, SWT.LEFT, SWT.CENTER, 1, 1);

		XLabel message = new XLabel(dialogShell, SWT.WRAP);
		if (getMessageKey( ) != null) {
			message.setTextForLocaleKey(getMessageKey( ), getMessageArgs( ));
		}
		else {
			message.setTextForLocaleKey(getMessage( ));
		}

		SWTUtil.setGridData(message,
				true,
				true,
				SWT.LEFT,
				SWT.CENTER,
				rowNumber - 1,
				1);

		XComposite com = new XComposite(dialogShell, SWT.NONE);
		SWTUtil.setGridData(com, false, false, SWT.FILL, SWT.FILL, rowNumber, 1);

		SWTUtil.setGridLayout(com, types.length, true);

		for (XButtonType type : types) {
			final XButton btn = new XButton(com, type);
			btn.addSelectionListener(new SelectionAdapter( ) {

				/* (non-Javadoc)
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					btnType = btn.getButtonType( );
					dialogShell.close( );
				}
			});
			SWTUtil.setGridData(btn, true, true, SWT.FILL, SWT.FILL, 1, 1);
		}

		dialogShell.pack( );
		SWTUtil.centerDialog(dialogShell);
		dialogShell.open( );

		SWTUtil.blockDialogFromReturning(dialogShell);

		return btnType;
	}
}
