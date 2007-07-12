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
 * TextFieldAppender.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 10.03.2006:		Created by Rabea Gransberger.
 */
package de.xirp.io.logging;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.RGB;

import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ColorManager;

/**
 * Log4j appender for textfields (StyledText).<br>
 * Messages are logged in special colors for each level.
 * 
 * @author Rabea Gransberger
 */
public class TextFieldAppender extends AppenderSkeleton {

	/**
	 * The Text to write the Log to
	 */
	private StyledText text;
	/**
	 * Color for Logging Type ERROR
	 */
	private static final RGB ERROR = new RGB(255, 0, 0);
	/**
	 * Color for Logging Type WARN
	 */
	private static final RGB WARN = new RGB(255, 127, 0);
	/**
	 * Color for Logging Type FATAL
	 */
	private static final RGB FATAL = new RGB(175, 0, 0);
	/**
	 * Color for Logging Type DEBUG
	 */
	private static final RGB DEBUG = new RGB(0, 0, 255);
	/**
	 * Color for Logging Type INFO
	 */
	private static final RGB INFO = new RGB(0, 0, 0);

	/**
	 * Sets the text field to which this appender should append the
	 * log events
	 * 
	 * @param textField
	 *            TextField to append the log text to
	 */
	public void setTextField(StyledText textField) {
		this.text = textField;
	}

	/**
	 * Writes the log message of the event to the Styled Text and
	 * shows it in different colors for the different log types. For
	 * example error messages are shown in Red. If the current Thread
	 * is not the UI Thread, an asyncExec is called to display the
	 * Message
	 * 
	 * @param logEvent
	 *            The event holding the type and message
	 * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
	 */
	@Override
	protected void append(final LoggingEvent logEvent) {
		if (SWTUtil.swtAssert(text)) {
			Level level = logEvent.getLevel( );
			final String rendered = logEvent.getRenderedMessage( );

			final StyleRange range = new StyleRange( );

			range.length = rendered.length( );

			if (level == Level.DEBUG) {
				range.foreground = ColorManager.getColor(DEBUG);
			}
			else if (level == Level.WARN) {
				range.foreground = ColorManager.getColor(WARN);
			}
			else if (level == Level.ERROR) {
				range.foreground = ColorManager.getColor(ERROR);
			}
			else if (level == Level.FATAL) {
				range.foreground = ColorManager.getColor(FATAL);
			}
			else {
				range.foreground = ColorManager.getColor(INFO);
			}

			// Minimal Code needed for async Exec
			SWTUtil.asyncExec(new Runnable( ) {

				public void run() {
					if (SWTUtil.swtAssert(text)) {
						int start = text.getText( ).length( );
						range.start = start;
						text.append(rendered);
						text.setStyleRange(range);
						// Scroll to end
						text.setCaretOffset(text.getText( ).length( ));
						text.showSelection( );
					}
				}
			});
		}
	}

	/**
	 * @see org.apache.log4j.AppenderSkeleton#requiresLayout()
	 */
	@Override
	public boolean requiresLayout() {
		return false;
	}

	/**
	 * @see org.apache.log4j.AppenderSkeleton#close()
	 */
	@Override
	public void close() {
		// Nothing to close
	}

}
