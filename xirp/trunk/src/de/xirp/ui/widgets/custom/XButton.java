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
 * XButton.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 17.01.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.custom;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.xirp.util.II18nHandler;

/**
 * This widget is enabled with on-the-fly translation and on-the-fly
 * color change capabilities. <br>
 * <br>
 * The text shown on the button is controlled by the items in the
 * enumeration
 * {@link de.xirp.ui.widgets.custom.XButton.XButtonType}.
 * 
 * @author Matthias Gernand
 * @see de.xirp.ui.widgets.custom.XButton.XButtonType
 */
public class XButton extends AbstractXButton {

	/**
	 * The type of the button.
	 */
	private XButtonType type;

	/**
	 * The enumeration holds items which control the text of the
	 * button.
	 * 
	 * @author Matthias Gernand
	 */
	public enum XButtonType {
		/**
		 * XButton without text, can be set manually.
		 */
		NULL,
		/**
		 * XButton containing text: "Apply".
		 */
		APPLY,
		/**
		 * XButton containing text: "Save".
		 */
		SAVE,
		/**
		 * XButton containing text: "Reset".
		 */
		RESET,
		/**
		 * XButton containing text: "Default".
		 */
		DEFAULT,
		/**
		 * XButton containing text: "Close".
		 */
		CLOSE,
		/**
		 * XButton containing text: "OK".
		 */
		OK,
		/**
		 * XButton containing text: "Cancel".
		 */
		CANCEL,
		/**
		 * XButton containing text: "Yes".
		 */
		YES,
		/**
		 * XButton containing text: "No".
		 */
		NO,
		/**
		 * XButton containing text: "...".
		 */
		LOOKUP
	}

	/**
	 * Constructs a new button. The type of the button is
	 * {@link XButtonType#NULL}. This constructor should be used in
	 * application-UI environment.
	 * 
	 * @param parent
	 *            The parent.
	 */
	public XButton(Composite parent) {
		super(parent, SWT.PUSH);
		this.setSize(80, 25);
		this.setAlignment(SWT.CENTER);
		this.type = XButtonType.NULL;
		init( );
	}

	/**
	 * Constructs a new button. This constructor should be used in
	 * application-UI environment.
	 * 
	 * @param parent
	 *            The parent.
	 * @param type
	 *            The type.
	 * @see de.xirp.ui.widgets.custom.XButton.XButtonType
	 */
	public XButton(Composite parent, XButtonType type) {
		super(parent, SWT.PUSH);
		this.setSize(80, 25);
		this.setAlignment(SWT.CENTER);
		this.type = type;
		init( );
	}

	/**
	 * Constructs a new button. This constructor should be used in
	 * plugin-ui environment, because the
	 * {@link de.xirp.util.II18nHandler handler} is
	 * needed for translations. The type of the button is
	 * {@link XButtonType#NULL}.
	 * 
	 * @param parent
	 *            The parent.
	 * @param handler
	 *            The handler.
	 * @see de.xirp.util.II18nHandler
	 */
	public XButton(Composite parent, II18nHandler handler) {
		super(parent, SWT.PUSH, handler);
		this.setSize(80, 25);
		this.setAlignment(SWT.CENTER);
		this.type = XButtonType.NULL;
		init( );
	}

	/**
	 * Constructs a new button. This constructor should be used in
	 * plugin-UI environment, because the
	 * {@link de.xirp.util.II18nHandler handler} is
	 * needed for translations.
	 * 
	 * @param parent
	 *            The parent.
	 * @param type
	 *            The type.
	 * @param handler
	 *            The handler.
	 * @see de.xirp.util.II18nHandler
	 */
	public XButton(Composite parent, XButtonType type, II18nHandler handler) {
		super(parent, SWT.PUSH, handler);
		this.setSize(80, 25);
		this.setAlignment(SWT.CENTER);
		this.type = type;
		init( );
	}

	/**
	 * Initializes the button.
	 */
	private void init() {
		switch (type) {
			case NULL:
				break;
			case APPLY:
				this.setTextForLocaleKey("XButton.button.apply"); //$NON-NLS-1$
				this.setToolTipTextForLocaleKey("XButton.tooltip.apply"); //$NON-NLS-1$
				break;
			case SAVE:
				this.setTextForLocaleKey("XButton.button.save"); //$NON-NLS-1$
				this.setToolTipTextForLocaleKey("XButton.tooltip.save"); //$NON-NLS-1$
				break;
			case RESET:
				this.setTextForLocaleKey("XButton.button.reset"); //$NON-NLS-1$
				this.setToolTipTextForLocaleKey("XButton.tooltip.reset"); //$NON-NLS-1$
				break;
			case DEFAULT:
				this.setTextForLocaleKey("XButton.button.default"); //$NON-NLS-1$
				this.setToolTipTextForLocaleKey("XButton.tooltip.default"); //$NON-NLS-1$
				break;
			case CLOSE:
				this.setTextForLocaleKey("XButton.button.close"); //$NON-NLS-1$
				this.setToolTipTextForLocaleKey("XButton.tooltip.close"); //$NON-NLS-1$
				break;
			case OK:
				this.setTextForLocaleKey("XButton.button.ok"); //$NON-NLS-1$
				this.setToolTipTextForLocaleKey("XButton.tooltip.ok"); //$NON-NLS-1$
				break;
			case CANCEL:
				this.setTextForLocaleKey("XButton.button.cancel"); //$NON-NLS-1$
				this.setToolTipTextForLocaleKey("XButton.tooltip.cancel"); //$NON-NLS-1$
				break;
			case YES:
				this.setTextForLocaleKey("XButton.button.yes"); //$NON-NLS-1$
				this.setToolTipTextForLocaleKey("XButton.tooltip.yes"); //$NON-NLS-1$
				break;
			case NO:
				this.setTextForLocaleKey("XButton.button.no"); //$NON-NLS-1$
				this.setToolTipTextForLocaleKey("XButton.tooltip.no"); //$NON-NLS-1$
				break;
			case LOOKUP:
				this.setTextForLocaleKey("XButton.button.lookup"); //$NON-NLS-1$
				this.setToolTipTextForLocaleKey("XButton.tooltip.lookup"); //$NON-NLS-1$
				break;
			default:
				break;
		}
	}

	/**
	 * Returns the
	 * {@link de.xirp.ui.widgets.custom.XButton.XButtonType type}
	 * of the button.
	 * 
	 * @return The type.
	 */
	public XButtonType getButtonType() {
		return type;
	}
}
