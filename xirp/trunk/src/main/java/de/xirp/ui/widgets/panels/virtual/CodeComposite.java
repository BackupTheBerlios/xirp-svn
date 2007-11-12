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
 * CodeComposite.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * 					 Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 03.03.2007:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.panels.virtual;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;

import de.xirp.ate.ATEManager;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.widgets.custom.XButton;
import de.xirp.ui.widgets.custom.XComposite;
import de.xirp.ui.widgets.custom.XLabel;
import de.xirp.ui.widgets.custom.XStyledText;
import de.xirp.ui.widgets.custom.XTabFolder;
import de.xirp.ui.widgets.custom.XTabItem;
import de.xirp.ui.widgets.custom.XTextField;
import de.xirp.ui.widgets.custom.XButton.XButtonType;
import de.xirp.util.Constants;
import de.xirp.util.FutureRelease;
import de.xirp.util.Util;

/**
 * This is alpha API, may be removed in the future or is planed to be
 * integrated in version 3.0.0.
 * 
 * @author Matthias Gernand
 */
@FutureRelease(version = "3.0.0")
public class CodeComposite extends XComposite {

	private XTabFolder openFiles;
	private XButton generate;
	private XStyledText code;
	private XTabItem item;

	/**
	 * @param parent
	 * @param style
	 */
	public CodeComposite(Composite parent, int style) {
		super(parent, style);
		init( );
	}

	/**
	 * 
	 */
	private void init() {
		SWTUtil.setGridLayout(this, 3, false);
// gl.horizontalSpacing = 0;
// gl.verticalSpacing = 0;
// gl.marginHeight = 0;
// gl.marginWidth = 0;
// gl.marginTop = 0;
// gl.marginBottom = 0;
// gl.marginLeft = 0;
// gl.marginRight = 0;

		XLabel name = new XLabel(this, SWT.NONE);
		name.setTextForLocaleKey("Enter name for new class:"); //$NON-NLS-1$
		SWTUtil.setGridData(name, true, false, SWT.FILL, SWT.CENTER, 1, 1);

		final XTextField nameField = new XTextField(this, SWT.NONE);
		SWTUtil.setGridData(nameField, true, false, SWT.FILL, SWT.FILL, 1, 1);

		XButton newClass = new XButton(this, XButtonType.OK);
		SWTUtil.setGridData(newClass, true, false, SWT.FILL, SWT.FILL, 1, 1);
		newClass.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				String text = nameField.getText( );
				if (!Util.isEmpty(text) && text.endsWith(".java")) { //$NON-NLS-1$
					openTab(text);
				}
			}

		});

		openFiles = new XTabFolder(this, SWT.NONE);
		SWTUtil.setGridData(openFiles, true, true, SWT.FILL, SWT.FILL, 3, 1);

		generate = new XButton(this);
		generate.setText("Compile"); //$NON-NLS-1$
		SWTUtil.setGridData(generate, true, false, SWT.FILL, SWT.FILL, 3, 1);
		generate.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				ATEManager.buildCode(code.getText( ), item.getText( ));
			}

		});
	}

	/**
	 * @param name
	 */
	private void openTab(String name) {
		item = new XTabItem(openFiles, SWT.NONE);
		item.setText(name);

		code = new XStyledText(openFiles, SWT.H_SCROLL | SWT.V_SCROLL, false);
		code.setDoubleClickEnabled(true);
		code.setAlignment(SWT.LEFT);
		code.setWordWrap(false);

		code.setText("public void algorithm() {" + Constants.LINE_SEPARATOR); //$NON-NLS-1$
		code.append(Constants.LINE_SEPARATOR + Constants.LINE_SEPARATOR);
		code.append("}"); //$NON-NLS-1$

		item.setControl(code);

		openFiles.setSelection(item);
	}
}
