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
 * XWizardPage.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 16.01.2007:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.custom;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ColorManager;
import de.xirp.util.Variables;

/**
 * This wizard page sets all necessary background colors to
 * provide a good look, when used with XWidgets.
 * 
 * @author Matthias Gernand
 *
 */
public abstract class XWizardPage extends WizardPage {

	/**
	 * The number of rows used in the layout of the 
	 * content control.
	 */
	private int layoutRowCount;

	/**
	 * Constructs a new wizard page.
	 * 
	 * @param key
	 * 			The key.
	 * @param title
	 * 			The title.
	 * @param id
	 * 			The id.
	 * @param layoutRowCount
	 * 			The number of rows.
	 */
	public XWizardPage(String key, String title, ImageDescriptor id,
			int layoutRowCount) {
		super(key, title, id);
		this.layoutRowCount = layoutRowCount;
		//initListeners();
	}

	/**
	 * Constructs a new wizard page.
	 * 
	 * @param title
	 * 			The title.
	 * @param layoutRowCount
	 * 			The number of rows.
	 */
	public XWizardPage(String title, int layoutRowCount) {
		super(title);
		this.layoutRowCount = layoutRowCount;
		//initListeners();
	}

	/**
	 * Creates the content composite, the rows in the 
	 * {@link org.eclipse.swt.layout.GridLayout layout}
	 * used are given in by the constructors.
	 * The backgrounds are set corresponding to the colors 
	 * currently used in the application.
	 * 
	 * @see org.eclipse.swt.layout.GridLayout
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		XComposite com = new XComposite(parent, SWT.NONE);
		SWTUtil.setGridLayout(com, this.layoutRowCount, true);

		parent.setBackground(ColorManager.getColor(Variables.BACKGROUND_COLOR));
		parent.getParent( )
				.setBackground(ColorManager.getColor(Variables.BACKGROUND_COLOR));

		addContent(com);

		setControl(com);
	}

	/**
	 * Add your content to the composite. You will not have to 
	 * setControl() at the end.
	 * 
	 * @param com
	 * 			The content to add.
	 */
	public abstract void addContent(Composite com);

	/**
	 * Initializes the control and sets the appearance listeners
	 */
	//TODO
	//	private void initListeners() {
	//		appearanceListener = new AppearanceChangedEventListener( ) {
	//
	//			public void appearanceChanged(AppearanceChangedEvent event) {
	//				parent.setBackground(ColorManager.getColor(Variables.BACKGROUND_COLOR));
	//				parentParent.setBackground(ColorManager.getColor(Variables.BACKGROUND_COLOR));
	//			}
	//
	//		};
	//		ApplicationManager
	//				.addAppearanceChangedEventListener(appearanceListener);
	//
	//		addDisposeListener(new DisposeListener( ) {
	//
	//			public void widgetDisposed(DisposeEvent e) {
	//				ApplicationManager
	//						.removeAppearanceChangedEventListener(appearanceListener);
	//			}
	//
	//		});
	//	}
}
