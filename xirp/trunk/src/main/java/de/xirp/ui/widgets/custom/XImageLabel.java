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
 * XImageLabel.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 18.04.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.custom;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;

/**
 * This widget is enabled with on-the-fly translation and on-the-fly
 * color change capabilities.
 * 
 * @author Matthias Gernand
 */
public final class XImageLabel extends XLabel {

	/**
	 * Flag indicating the status.
	 */
	private boolean _done = false;
	/**
	 * The "done" image.
	 */
	private Image done;
	/**
	 * The "not done" image.
	 */
	private Image notdone;

	/**
	 * Constructs a new image label.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 */
	public XImageLabel(Composite parent, int style) {
		super(parent, style);
		init( );
	}

	/**
	 * Initializes the image label. Sets the image and the initial
	 * state.
	 */
	private void init() {
		done = ImageManager.getSystemImage(SystemImage.DONE);
		notdone = ImageManager.getSystemImage(SystemImage.NOT_DONE);
		super.setImage(notdone);
		super.setToolTipTextForLocaleKey("ImageLabel.gui.toolTip.partNotDone"); //$NON-NLS-1$
	}

	/**
	 * Sets the state of the image label.
	 * 
	 * @param done
	 *            <code>true</code>, when the state should be
	 *            "done"<br>
	 *            <code>false</code>, when the state should be "not
	 *            done"
	 */
	public void setDone(boolean done) {
		_done = done;
		if (done) {
			super.setImage(this.done);
			super.setToolTipTextForLocaleKey("ImageLabel.gui.toolTip.partDone"); //$NON-NLS-1$
		}
		else {
			super.setImage(notdone);
			super.setToolTipTextForLocaleKey("ImageLabel.gui.toolTip.partNotDone"); //$NON-NLS-1$
		}
	}

	/**
	 * Returns whether the state of the label is "done" or "not done".
	 * 
	 * @return A <code>boolean</code><br>
	 *         <code>true</code>, when state is "done".<br>
	 *         <code>false</code>, when state is "not done".
	 */
	public boolean isDone() {
		return _done;
	}

	/**
	 * Overridden. Nothing is done, when this method implementation is
	 * called, because the text must be empty.
	 * 
	 * @param text
	 *            The text to set.
	 * @see org.eclipse.swt.widgets.Label#setText(java.lang.String)
	 */
	@Override
	public void setText(String text) {
		// do nothing
	}

	/**
	 * Overridden. Nothing is done, when this method implementation is
	 * called, because the images are fixed.
	 * 
	 * @param image
	 *            The image to set.
	 * @see org.eclipse.swt.widgets.Label#setImage(Image)
	 */
	@Override
	public void setImage(Image image) {
		// do nothing
	}

	/**
	 * Overridden. Nothing is done, when this method implementation is
	 * called, because the tool tips are fixed.
	 * 
	 * @see org.eclipse.swt.widgets.Control#setToolTipText(java.lang.String)
	 */
	@Override
	public void setToolTipText(String text) {
		// do nothing
	}

	/**
	 * Overridden. Nothing is done, when this method implementation is
	 * called, because the text must be empty.
	 * 
	 * @see org.eclipse.swt.widgets.Label#setText(java.lang.String)
	 */
	@Override
	public void setTextForLocaleKey(String key, Object... objects) {
		// do nothing
	}

	/**
	 * Overridden. Nothing is done, when this method implementation is
	 * called, because the tool tips are fixed.
	 * 
	 * @see org.eclipse.swt.widgets.Control#setToolTipText(java.lang.String)
	 */
	@Override
	public void setToolTipTextForLocaleKey(String key, Object... objects) {
		// do nothing
	}
}
