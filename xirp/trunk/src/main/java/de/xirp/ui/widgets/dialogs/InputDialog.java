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
 * InputDialog.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 30.04.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Shell;

import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.XButton;
import de.xirp.ui.widgets.custom.XDialog;
import de.xirp.ui.widgets.custom.XLabel;
import de.xirp.ui.widgets.custom.XShell;
import de.xirp.ui.widgets.custom.XTextField;
import de.xirp.ui.widgets.custom.XButton.XButtonType;
import de.xirp.util.Constants;

/**
 * This dialog is used to capture user input.
 * 
 * @author Matthias Gernand
 */
public final class InputDialog extends XDialog {

	/**
	 * A string constant.
	 */
	private static final String CHECK = "check"; //$NON-NLS-1$
	/**
	 * Width of the dialog.
	 */
	private static final int WIDTH = 275;
	/**
	 * Shell of the dialog.
	 */
	private XShell dialogShell;
	/**
	 * Parent shell.
	 */
	private Shell parent;
	/**
	 * The texts which will return.
	 */
	private List<String> texts = new ArrayList<String>( );
	/**
	 * A OK button.
	 */
	private XButton ok;
	/**
	 * A CANCEL button.
	 */
	private XButton cancel;
	/**
	 * The i18n keys of the input field names.
	 */
	private String[] keys = null;
	/**
	 * The text fields.
	 */
	private ArrayList<XTextField> fields;
	/**
	 * The logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(InputDialog.class);

	/**
	 * Constructs a new dialog on the parent shell for the given
	 * plugin type.
	 * 
	 * @param parent
	 *            the parent.
	 * @param keys
	 *            The i18n keys of the input field names.<br>
	 *            This array specifies the number of input fields.
	 */
	public InputDialog(Shell parent, String... keys) {
		super(parent, SWT.DIALOG_TRIM);
		this.parent = parent;
		this.keys = keys;
		fields = new ArrayList<XTextField>(keys.length);
		texts = new ArrayList<String>(keys.length);
	}

	/**
	 * Sets the corresponding input field to the non-check mode. The
	 * text field will not be checked. The list of text fields is zero
	 * indexed.
	 * 
	 * @param field
	 *            The field to not-check.
	 */
	private void fieldDoesNotNeedCheck(int field) {
		try {
			fields.get(field).setData(CHECK, Boolean.FALSE);
		}
		catch (RuntimeException e) {
			logClass.debug("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
	}

	/**
	 * Returns the text(s) entered in the text field(s) or an empty
	 * list, if the dialog was canceled.
	 * 
	 * @param fieldsWhichDontNeedCheck
	 * 			The indexes of the fields that don't need to be checked.<br>
	 * 			the fields are zero indexed.
	 * 
	 * @return The entered text(s).
	 */
	public List<String> open(int... fieldsWhichDontNeedCheck) {
		dialogShell = new XShell(parent, SWT.DIALOG_TRIM |
				SWT.APPLICATION_MODAL);

		dialogShell.addShellListener(new ShellAdapter( ) {

			@Override
			public void shellClosed(ShellEvent e) {
				SWTUtil.secureDispose(dialogShell);
			}
		});

		if (keys.length == 0) {
			dialogShell.setTextForLocaleKey("InputDialog.gui.dialogTitle.enterDesiredtext"); //$NON-NLS-1$
		}
		else {
			dialogShell.setTextForLocaleKey("InputDialog.gui.dialogTitle.enterDesiredTexts"); //$NON-NLS-1$
		}

		dialogShell.setImage(ImageManager.getSystemImage(SystemImage.QUESTION));

		SWTUtil.setGridLayout(dialogShell, 2, true);

		if (keys.length == 0) {
			final XTextField textField = new XTextField(dialogShell, SWT.NONE);
			textField.addModifyListener(new ModifyListener( ) {

				public void modifyText(ModifyEvent e) {
					check( );
				}
			});
			SWTUtil.setGridData(textField,
					true,
					false,
					SWT.FILL,
					SWT.BEGINNING,
					2,
					1);
			textField.setData(CHECK, Boolean.TRUE);
			fields.add(textField);
		}

		for (String key : keys) {
			XLabel l = new XLabel(dialogShell, SWT.NONE);
			l.setTextForLocaleKey(key);
			SWTUtil.setGridData(l, true, false, SWT.FILL, SWT.BEGINNING, 1, 1);

			final XTextField textField = new XTextField(dialogShell, SWT.NONE);
			textField.addModifyListener(new ModifyListener( ) {

				public void modifyText(ModifyEvent e) {
					check( );
				}
			});
			SWTUtil.setGridData(textField,
					true,
					false,
					SWT.FILL,
					SWT.BEGINNING,
					1,
					1);
			textField.setData(CHECK, Boolean.TRUE);
			fields.add(textField);
		}

		ok = new XButton(dialogShell, XButtonType.OK);
		ok.setEnabled(false);
		SWTUtil.setGridData(ok, true, false, SWT.FILL, SWT.BEGINNING, 1, 1);
		ok.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				for (XTextField f : fields) {
					texts.add(f.getText( ));
				}
				dialogShell.close( );
			}
		});

		cancel = new XButton(dialogShell, XButtonType.CANCEL);
		SWTUtil.setGridData(cancel, true, false, SWT.FILL, SWT.BEGINNING, 1, 1);
		cancel.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				texts.clear( );
				dialogShell.close( );
			}
		});

		dialogShell.setDefaultButton(ok);

		dialogShell.pack( );
		dialogShell.setSize(WIDTH, dialogShell.getSize( ).y);
		SWTUtil.centerDialog(dialogShell);
		dialogShell.open( );
		for (int i : fieldsWhichDontNeedCheck) {
			fieldDoesNotNeedCheck(i);
		}
		check( );

		SWTUtil.blockDialogFromReturning(dialogShell);

		return Collections.unmodifiableList(texts);
	}

	/**
	 * Checks all text fields with the flag <code>CHECK</code> set
	 * to true, if a text was entered.
	 */
	protected void check() {
		boolean b = true;
		for (XTextField f : fields) {
			if ((Boolean) f.getData(CHECK)) {
				b &= !f.getText( ).equals(""); //$NON-NLS-1$
			}
			else {
				b &= true;
			}
		}
		ok.setEnabled(b);
	}
}
