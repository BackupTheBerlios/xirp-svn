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
 * XStyledSpinner.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 08.05.2006:		Created by Rabea Gransberger.
 */
package de.xirp.ui.widgets.custom;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.xirp.ui.event.LocaleChangedEvent;
import de.xirp.ui.event.LocaleChangedListener;
import de.xirp.ui.event.ValueChangedEvent;
import de.xirp.ui.event.ValueChangedListener;
import de.xirp.ui.util.ApplicationManager;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ColorManager;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.util.Constants;
import de.xirp.util.I18n;
import de.xirp.util.Variables;

/**
 * A custom spinner which supports negative values, double values and
 * negative values depending on the style set.
 * 
 * @author Rabea Gransberger
 */
public class XStyledSpinner extends XComposite {

	/**
	 * The styles of the Spinner. More than one style can be used, for
	 * example SpinnerStyle.NEGATIVE | SpinnerStyle.DOUBLE allows
	 * double negative values for the spinner
	 */
	public static class SpinnerStyle {

		/**
		 * The normal style, which is none of the other styles
		 */
		public static final int NORMAL = 2;

		/**
		 * If this style is set, the spinner allows negative values
		 */
		public static final int NEGATIVE = 2 << 1;

		/**
		 * If this style is set, the spinner allows the input of
		 * decimals
		 */
		public static final int DOUBLE = 2 << 2;

		/**
		 * If this style is set, 2 buttons under the spinner allow to
		 * choose the position to which the increment is applied.
		 */
		public static final int CHOOSABLE_INCREMENT = 2 << 3;

		/**
		 * All styles in one
		 */
		public static final int ALL = NEGATIVE | DOUBLE | CHOOSABLE_INCREMENT;
	}

	/**
	 * The logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(I18n.class);

	/**
	 * The default minimum value for a spinner which allows negative
	 * values
	 */
	private static final int NEGATIVE_DEFAULT_MINIMUM = -100;

	/**
	 * Image for the up button
	 */
	private static final Image IMG_UP = ImageManager.getSystemImage(SystemImage.ARROW_UP_SMALL);

	/**
	 * Image for the down button
	 */
	private static final Image IMG_DOWN = ImageManager.getSystemImage(SystemImage.ARROW_DOWN_SMALL);

	/**
	 * Image for the left button
	 */
	private static final Image IMG_LEFT = ImageManager.getSystemImage(SystemImage.ARROW_LEFT_SMALL);

	/**
	 * Image for the right button
	 */
	private static final Image IMG_RIGHT = ImageManager.getSystemImage(SystemImage.ARROW_RIGHT_SMALL);

	/**
	 * The style which is used for this spinner
	 */
	private int spinnerStyle = SpinnerStyle.DOUBLE |
			SpinnerStyle.CHOOSABLE_INCREMENT;

	/**
	 * Flag indicating if the position for the increment is chooseable
	 * for this spinner
	 */
	private boolean showIncrement = false;

	/**
	 * Flag indicating if this spinner can show double values
	 */
	private boolean showDouble = false;

	/**
	 * Flag indicating if this spinner allows negative values
	 */
	private boolean allowNegative = false;

	/**
	 * Text field showing the current integer value of this spinner
	 */
	private XStyledText textValue;
	/**
	 * Text field showing the current decimal value of this spinner,
	 * if the spinner is able to show double values. Otherwise this
	 * variable is <code>null</code>
	 */
	private XStyledText decimalTextValue;
	/**
	 * Text field showing the decimal separator for the current locale
	 * if the spinner is able to show double values. Otherwise this
	 * variable is <code>null</code>
	 */
	private XLabel decimalSepLabel;

	/**
	 * The minimum value for this spinner
	 */
	private double minimum = 0;

	/**
	 * The maximum value for this spinner
	 */
	private double maximum = 100;

	/**
	 * The current value for this spinner
	 */
	private double value = 0;

	/**
	 * The increment for this spinner used when clicking on the
	 * increment, decrement buttons
	 */
	private int increment = 1;

	/**
	 * Flag indicating if the minimal value for this spinner was
	 * explicitly set by calling {@linkplain #setMinimum(double)}
	 */
	private boolean minimumSet = false;
	/**
	 * Flag indicating if the maximal value for this spinner was
	 * explicitly set by calling {@linkplain #setMaximum(double)}
	 */
	private boolean maximumSet = false;

	/**
	 * The number of decimals to use for spinner style double
	 */
	private int currentDecimals = 2;

	/**
	 * The position to which an increment is applied if this spinner
	 * allows to choose the increment
	 */
	private int position = 0;
	/**
	 * The increment to use for the current position
	 */
	private double currentIncrement = increment;
	/**
	 * The style used for showing the position of the increment if
	 * this spinner allows to choose the increment
	 */
	private StyleRange style;

	/**
	 * Locale dependent format used to format the integer part of this
	 * spinners value
	 */
	private NumberFormat intFormat;
	/**
	 * Locale dependent format used to format the decimal part of this
	 * spinners value
	 */
	private NumberFormat decimalFormat;
	/**
	 * List of listeners which are informed if the value of this
	 * spinner changes
	 */
	private Vector<ValueChangedListener> listeners = new Vector<ValueChangedListener>( );
	/**
	 * List of selection listeners which are informed if the value of
	 * this spinner changes
	 */
	private Vector<Listener> selectionListeners = new Vector<Listener>( );

	/**
	 * Constructs a new styled spinner.
	 * 
	 * @param parent
	 *            the parent component for this spinner
	 * @param style
	 *            the SWT style for this spinner
	 * @param spinnerStyle
	 *            the spinner style to use
	 */
	public XStyledSpinner(Composite parent, int style, int spinnerStyle) {
		super(parent, style | SWT.BORDER);
		this.spinnerStyle = spinnerStyle;
		initGUI( );
	}

	/**
	 * Constructs a new styled spinner.
	 * 
	 * @param parent
	 *            the parent component for this spinner
	 * @param style
	 *            the SWT style for this spinner
	 */
	public XStyledSpinner(Composite parent, int style) {
		this(parent, style, SpinnerStyle.NORMAL);
	}

	/**
	 * Adds the listener of the given type to this spinner.<br>
	 * Currently the {@link SWT#Selection} style is supported.
	 */
	@Override
	public void addListener(int type, Listener listener) {
		if (type == SWT.Selection) {
			if (selectionListeners != null) {
				selectionListeners.add(listener);
			}
		}
		super.addListener(type, listener);
	}

	/**
	 * Removes the listener of the given type from this spinner.<br>
	 * Currently the {@link SWT#Selection} style is supported.
	 */
	@Override
	public void removeListener(int type, Listener listener) {
		if (type == SWT.Selection) {
			if (selectionListeners != null) {
				selectionListeners.remove(listener);
			}
		}
		super.removeListener(type, listener);
	}

	/**
	 * Called if the value of the spinner changes. It checks the
	 * bounds and updates the UI.
	 */
	private void valueChanged() {
		checkBounds( );

		double[] parts = getParts(value);

		String format = intFormat.format(parts[0]);
		textValue.setText(format);
		if (showDouble) {
			decimalTextValue.setText(decimalToString(parts[1]));
		}
	}

	/**
	 * Adds a listener to this spinner which is informed if the value
	 * changes.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void addValueChangedListener(ValueChangedListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes the given listener from the list.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeValueChangedListener(ValueChangedListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Sets the number of decimals to show for a spinner which can
	 * show double values.
	 * 
	 * @param decimals
	 *            the number of decimals to show
	 */
	public void setNumberOfDecimals(int decimals) {
		if (showDouble && decimals >= 1) {
			if (currentDecimals != decimals) {
				this.decimalFormat.setMaximumFractionDigits(decimals);
				this.decimalFormat.setMinimumFractionDigits(decimals);
				currentDecimals = decimals;
				valueChanged( );
			}
		}
	}

	/**
	 * Get the current value of this spinner as an <code>int</code>.
	 * If this spinner shows double values the value returned is
	 * casted to <code>int</code>
	 * 
	 * @return the current value as <code>int</code>
	 * @see #getSelectionDouble()
	 */
	public int getSelection() {
		return (int) value;
	}

	/**
	 * Gets the current value of this spinner.
	 * 
	 * @return the current value
	 */
	public double getSelectionDouble() {
		return value;
	}

	/**
	 * Sets the current value for this spinner.
	 * 
	 * @param selection
	 *            the value to set
	 */
	public void setSelection(int selection) {
		setSelection((double) selection);
	}

	/**
	 * Sets the current value for this spinner.<br>
	 * It this spinner is not in double mode, the given value is
	 * casted to an <code>int</code> before setting it.
	 * 
	 * @param selection
	 *            the value to set
	 */
	public void setSelection(double selection) {
		this.value = Math.min(selection, maximum);
		this.value = Math.max(value, minimum);

		if (!showDouble) {
			this.value = (int) value;
		}

		valueChanged( );
	}

	/**
	 * Gets the increment used by this spinner.
	 * 
	 * @return the increment
	 */
	public int getIncrement() {
		return increment;
	}

	/**
	 * Sets the increment used by this spinner, which is the amount
	 * added to the value of the spinner when using the buttons to
	 * modify the value.<br>
	 * <br>
	 * Currently an increment of 1 is supported.
	 * 
	 * @param increment
	 *            the increment to set. Currently not used.
	 */
	public void setIncrement(int increment) {
		// TODO use given increment
		this.increment = 1;
		currentIncrement = increment;
	}

	/**
	 * Gets the maximum value of this spinner.
	 * 
	 * @return Returns the max.
	 */
	public double getMaximum() {
		return maximum;
	}

	/**
	 * Gets the minimum value of this spinner.
	 * 
	 * @return Returns the min.
	 */
	public double getMinimum() {
		return minimum;
	}

	/**
	 * Sets the maximum value of this spinner.<br>
	 * Note: The Maximum value can be set only once and may only be
	 * set after the minimum value was set.<br>
	 * If the given maximum is smaller than the minium, the given
	 * value is set to be the minimum, and the old minimum is set to
	 * be the maximum. The method will return <code>true</code> in
	 * this case.
	 * 
	 * @param max
	 *            The Maximum value to set
	 * @return <code>true</code> if the given maximum was set
	 */
	public boolean setMaximum(double max) {
		if (maximumSet) {
			logClass.warn(I18n.getString("HStyledSpinner.log.subsequentCallToSetMaximumIgnored") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
			return false;
		}

		if (!minimumSet) {
			logClass.warn(I18n.getString("HStyledSpinner.log.callToMinimumPriorToSetMaximum") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
			return false;
		}

		maximum = max;

		if (maximum < minimum) {
			double temp = minimum;
			minimum = maximum;
			maximum = temp;
		}

		maximumSet = true;

		return true;
	}

	/**
	 * Sets the minimum value for this spinner.<br>
	 * Note: If the minimum was already set the call is ignored. If
	 * the given minimum is negative, but no negative values are
	 * allowed for this spinner, the minimum is set to zero. The
	 * method will return true in this case.<br>
	 * This method will set the maximum to the given minimum + 100 but
	 * allows the user to set the maximum on its own.
	 * 
	 * @param min
	 *            the minimum value to set
	 * @return <code>true</code> if the given minimum was set
	 */
	public boolean setMinimum(double min) {
		if (minimumSet) {
			logClass.warn(I18n.getString("HStyledSpinner.log.subsequentCallToSetMinimumIgnored") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
			return false;
		}
		if (min < 0 && !allowNegative) {
			minimum = 0;
			logClass.warn(I18n.getString("HStyledSpinner.log.negativeAroOnlyAllowedWithStylenegative") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
		}
		else {
			minimum = min;
		}

		maximum = minimum + 100;

		minimumSet = true;

		return true;
	}

	/**
	 * Initializes the listener for changes of the locale which will
	 * re-initialize locale depended variables, like the formatters
	 * used for displaying the current value.
	 */
	private void initListener() {
		final LocaleChangedListener listener = new LocaleChangedListener( ) {

			public void localeChanged(@SuppressWarnings("unused")
			LocaleChangedEvent event) {
				if (SWTUtil.swtAssert(decimalSepLabel)) {
					decimalSepLabel.setText(Character.toString(I18n.getDecimalSeparator( )));
				}
				initDecimalNumberFormat( );
				initIntNumberFormat( );
				valueChanged( );
			}

		};
		ApplicationManager.addLocaleChangedListener(listener);
		addDisposeListener(new DisposeListener( ) {

			public void widgetDisposed(@SuppressWarnings("unused")
			DisposeEvent event) {
				ApplicationManager.removeLocaleChangedListener(listener);
			}

		});
	}

	/**
	 * Initializes the formatter which is used to format the integer
	 * part of the current value.
	 */
	private void initIntNumberFormat() {
		intFormat = NumberFormat.getIntegerInstance(I18n.getLocale( ));
		intFormat.setRoundingMode(RoundingMode.DOWN);
		intFormat.setGroupingUsed(false);

		intFormat.setParseIntegerOnly(true);
	}

	/**
	 * Initializes the formatter which is used to format the decimal
	 * part of the current value if the spinner style allows double
	 * values
	 */
	private void initDecimalNumberFormat() {
		if (showDouble) {
			decimalFormat = NumberFormat.getNumberInstance(I18n.getLocale( ));
			decimalFormat.setMaximumIntegerDigits(0);
			if (!(currentDecimals >= 1)) {
				currentDecimals = 1;
			}
			decimalFormat.setMinimumFractionDigits(currentDecimals);
			decimalFormat.setMaximumFractionDigits(currentDecimals);
		}
	}

	/**
	 * Checks if the given text is numeric according to the currently
	 * used format.
	 * 
	 * @param text
	 *            the text to check
	 * @param decimal
	 *            the decimal part of the value does not allow a minus
	 *            sign, so check for this if this flag is set
	 * @return <code>true</code> if the text is numeric
	 */
	private boolean isNumeric(String text, boolean decimal) {
		ParsePosition pos = new ParsePosition(0);
		try {
			intFormat.parse(text, pos);
			if (pos.getIndex( ) == text.length( )) {
				if (decimal || !allowNegative) {
					if (text.startsWith(Character.toString(I18n.getMinusSign( )))) {
						return false;
					}
				}
				return true;
			}
			else {
				if (allowNegative && !decimal) {
					if (text.startsWith(Character.toString(I18n.getMinusSign( )))) {
						return true;
					}
				}
				return false;
			}
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * Initializes the user interface for this spinner depended to the
	 * style set.
	 */
	private void initGUI() {
		checkStyle( );

		initListener( );
		initIntNumberFormat( );
		initDecimalNumberFormat( );

		if (allowNegative) {
			minimum = NEGATIVE_DEFAULT_MINIMUM;
		}

		SWTUtil.setGridLayout(this, showDouble ? DOUBLE_OVERALL_COLS
				: NORMAL_OVERALL_COLS, false);
		GridLayout gl = (GridLayout) this.getLayout( );
		gl.horizontalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.verticalSpacing = 0;

		textValue = new XStyledText(this, SWT.SINGLE, false);
		textValue.setText("0"); //$NON-NLS-1$
		textValue.setOrientation(SWT.HORIZONTAL);
		textValue.setDoubleClickEnabled(false);
		textValue.setTextLimit(1000);

		GridData gd = SWTUtil.setGridData(textValue,
				true,
				true,
				SWT.FILL,
				SWT.FILL,
				2,
				2);
		gd.widthHint = 30;

		textValue.addFocusListener(new FocusListener( ) {

			public void focusGained(@SuppressWarnings("unused")
			FocusEvent e) {
				textValue.setBackground(ColorManager.getColor(Variables.FOCUS_COLOR));
			}

			public void focusLost(@SuppressWarnings("unused")
			FocusEvent e) {
				textValue.setBackground(ColorManager.getColor(Constants.DEFAULT_COLOR_WHITE));

				String text = textValue.getText( );
				if (isNumeric(text, false)) {
					try {
						updateInt(Integer.parseInt(text));
					}
					catch (NumberFormatException e1) {
						// do nothing
					}
				}
			}

		});

		textValue.addExtendedModifyListener(new ExtendedModifyListener( ) {

			public void modifyText(ExtendedModifyEvent e) {
				String wholeText = textValue.getText( );
				if (!isNumeric(wholeText, false)) {
					textValue.setText(wholeText.substring(0, e.start) +
							wholeText.substring(e.start + 1));
					textValue.setCaretOffset(e.start);
				}
				if (showIncrement) {
					updatePosition( );
				}
			}
		});

		textValue.addDisposeListener(new DisposeListener( ) {

			public void widgetDisposed(@SuppressWarnings("unused")
			DisposeEvent ev) {
				String text = textValue.getText( );
				if (isNumeric(text, false)) {
					try {
						updateInt(Integer.parseInt(text));
					}
					catch (NumberFormatException e1) {
						// do nothing
					}
				}
			}

		});

		if (showDouble) {
			decimalSepLabel = new XLabel(this, SWT.FLAT);
			decimalSepLabel.setText(Character.toString(I18n.getDecimalSeparator( )));
			gd = SWTUtil.setGridData(decimalSepLabel,
					false,
					false,
					SWT.CENTER,
					SWT.CENTER,
					2,
					2);

			decimalTextValue = new XStyledText(this, SWT.SINGLE, false);
			decimalTextValue.setText("0"); //$NON-NLS-1$
			decimalTextValue.setAlignment(SWT.LEFT);
			gd = SWTUtil.setGridData(decimalTextValue,
					true,
					true,
					SWT.FILL,
					SWT.FILL,
					2,
					2);
			gd.widthHint = 30;

			decimalTextValue.setOrientation(SWT.HORIZONTAL);
			decimalTextValue.setDoubleClickEnabled(false);
			decimalTextValue.setTextLimit(1000);

			decimalTextValue.addFocusListener(new FocusListener( ) {

				public void focusGained(@SuppressWarnings("unused")
				FocusEvent e) {
					decimalTextValue.setBackground(ColorManager.getColor(Variables.FOCUS_COLOR));
				}

				public void focusLost(@SuppressWarnings("unused")
				FocusEvent e) {
					decimalTextValue.setBackground(ColorManager.getColor(Constants.DEFAULT_COLOR_WHITE));

					String text = decimalTextValue.getText( );

					if (isNumeric(text, true)) {
						try {
							updateDecimal(Double.parseDouble("0." + text)); //$NON-NLS-1$

						}
						catch (NumberFormatException e1) {
							// do nothing
						}
					}
				}

			});
			textValue.addDisposeListener(new DisposeListener( ) {

				public void widgetDisposed(@SuppressWarnings("unused")
				DisposeEvent ev) {
					String text = decimalTextValue.getText( );

					if (isNumeric(text, true)) {
						try {
							updateDecimal(Double.parseDouble("0." + text)); //$NON-NLS-1$

						}
						catch (NumberFormatException e1) {
							// do nothing
						}
					}
				}

			});

			decimalTextValue.addExtendedModifyListener(new ExtendedModifyListener( ) {

				public void modifyText(ExtendedModifyEvent e) {
					String wholeText = decimalTextValue.getText( );

					if (!isNumeric(wholeText, true)) {
						decimalTextValue.setText(wholeText.substring(0, e.start) +
								wholeText.substring(e.start + 1));
						decimalTextValue.setCaretOffset(e.start);
					}

					if (showIncrement) {
						updatePosition( );
					}
				}
			});
		}

		XButton buttonUp = new XButton(this);
		buttonUp.setImage(IMG_UP);
		gd = SWTUtil.setGridData(buttonUp,
				false,
				false,
				SWT.RIGHT,
				SWT.TOP,
				1,
				1);
		gd.heightHint = 7;
		gd.widthHint = 12;

		buttonUp.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(@SuppressWarnings("unused")
			SelectionEvent event) {
				increment( );
			}

		});

		XButton buttonDown = new XButton(this);
		buttonDown.setImage(IMG_DOWN);
		gd = SWTUtil.setGridData(buttonDown,
				false,
				false,
				SWT.RIGHT,
				SWT.BOTTOM,
				1,
				1);
		gd.widthHint = 12;
		gd.heightHint = 7;
		buttonDown.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(@SuppressWarnings("unused")
			SelectionEvent event) {
				decrement( );
			}
		});
		if (showIncrement) {
			XButton buttonLeft = new XButton(this);
			buttonLeft.setImage(IMG_LEFT);
			gd = SWTUtil.setGridData(buttonLeft,
					false,
					false,
					showDouble ? SWT.FILL : SWT.LEFT,
					SWT.BOTTOM,
					showDouble ? DOUBLE_INCREMENT_COLS : NORMAL_INCREMENT_COLS,
					1);
			gd.heightHint = 10;
			gd.widthHint = 10;
			buttonLeft.addSelectionListener(new SelectionAdapter( ) {

				@Override
				public void widgetSelected(@SuppressWarnings("unused")
				SelectionEvent event) {
					if (position > 0) {
						position--;
						updatePosition( );
					}
				}
			});

			XButton buttonRight = new XButton(this);
			buttonRight.setImage(IMG_RIGHT);
			gd = SWTUtil.setGridData(buttonRight,
					false,
					false,
					showDouble ? SWT.FILL : SWT.LEFT,
					SWT.BOTTOM,
					showDouble ? DOUBLE_INCREMENT_COLS : NORMAL_INCREMENT_COLS,
					1);
			gd.widthHint = 10;
			gd.heightHint = 10;
			buttonRight.addSelectionListener(new SelectionAdapter( ) {

				@Override
				public void widgetSelected(@SuppressWarnings("unused")
				SelectionEvent event) {
					int length = textValue.getText( ).length( ) - 1;
					if (showDouble) {
						length += decimalTextValue.getText( ).length( );
					}
					if (position < length) {
						position++;
						updatePosition( );
					}
				}
			});
			style = new StyleRange( );
			style.background = ColorManager.getColor(255, 190, 190);

			updatePosition( );
		}
	}

	/**
	 * Sets some flags according to the current style.
	 */
	private void checkStyle() {
		showIncrement = SWTUtil.checkStyle(spinnerStyle,
				SpinnerStyle.CHOOSABLE_INCREMENT);

		showDouble = SWTUtil.checkStyle(spinnerStyle, SpinnerStyle.DOUBLE);

		allowNegative = SWTUtil.checkStyle(spinnerStyle, SpinnerStyle.NEGATIVE);
	}

	/**
	 * Gets the integer and decimal part of the given double.<br>
	 * 50.667 will be returned as [50.0; 0.667].
	 * 
	 * @param d
	 *            the double to split
	 * @return an array of doubles containing the integer and the
	 *         decimal part
	 */
	private double[] getParts(double d) {
		double[] parts = new double[2];
		String t = Double.toString(d);
		String[] rt = t.split("\\."); //$NON-NLS-1$
		parts[0] = Double.parseDouble(rt[0]);
		parts[1] = Double.parseDouble("0." + rt[1]); //$NON-NLS-1$
		return parts;
	}

	/**
	 * Updates only the decimal of the current value with the given
	 * decimal.<br>
	 * 50.6 with new decimal of 0.7 will update to 50.7.
	 * 
	 * @param newDecimal
	 *            the new decimal for the current value
	 */
	private void updateDecimal(double newDecimal) {
		double oldValue = value;
		double parts[] = getParts(value);
		value = parts[0] + newDecimal;
		valueChanged( );
		if (oldValue != value) {
			fireSelectionEvent( );
		}
	}

	/**
	 * Updates only the integer part of the current value with the
	 * given integer.<br>
	 * 50.6 with new integer of 5 will update to 5.6.
	 * 
	 * @param newInt
	 *            the new integer for the current value
	 */
	private void updateInt(double newInt) {
		double oldValue = value;
		double parts[] = getParts(value);
		value = newInt + parts[1];
		valueChanged( );
		if (oldValue != value) {
			fireSelectionEvent( );
		}
	}

	/**
	 * Normal number of columns for the layout
	 */
	private static final int NORMAL_OVERALL_COLS = 3;
	/**
	 * Number of columns for the layout when double style is set
	 */
	private static final int DOUBLE_OVERALL_COLS = 7;

	/**
	 * Normal number of columns to use for the increment/decrement
	 * buttons
	 */
	private static final int NORMAL_INCREMENT_COLS = 1;
	/**
	 * Number of columns to use for the increment/decrement buttons
	 * with double style
	 */
	private static final int DOUBLE_INCREMENT_COLS = 3;

	/**
	 * Updates the position of the increment for normal style.
	 */
	private void updatePositionNormal() {
		int textLength = this.textValue.getText( ).length( );

		if (position >= textLength) {
			position = textLength - 1;
		}
		style.start = position;
		if (value < 0 && position == 0) {
			style.length = 2;
		}
		else {
			style.length = 1;
		}
		textValue.setStyleRanges(new StyleRange[] {style});
	}

	/**
	 * Updates the position of the increment for double style.
	 */
	private void updatePositionDouble() {
		int textLength = this.textValue.getText( ).length( );
		int decimalLength = this.decimalTextValue.getText( ).length( );
		int sumLength = textLength + decimalLength;
		if (position >= sumLength) {
			position = sumLength - 1;
		}

		if (position >= textLength) {
			int tempPos = position - textLength;
			style.start = tempPos;
			style.length = 1;
			decimalTextValue.setStyleRanges(new StyleRange[] {style});

			style.length = 0;
			textValue.setStyleRanges(new StyleRange[] {style});
		}
		else {
			style.start = position;
			if (value < 0 && position == 0) {
				style.length = 2;
			}
			else {
				style.length = 1;
			}
			textValue.setStyleRanges(new StyleRange[] {style});

			style.length = 0;
			decimalTextValue.setStyleRanges(new StyleRange[] {style});
		}
	}

	/**
	 * Updates the position of the increment if increment is used.
	 */
	private void updatePosition() {
		if (position < 0) {
			position = 0;
		}
		if (position == 0 && this.value < 0) {
			position++;
		}
		if (this.showIncrement) {
			if (showDouble) {
				updatePositionDouble( );
			}
			else {
				updatePositionNormal( );
			}

			this.currentIncrement = this.getIncrementInternal( );
		}
	}

	/**
	 * Checks if the current value is inside the minimum/maximum of
	 * this spinner, and corrects the value to the minimum/maximum if
	 * bounds were exceeded.
	 */
	private void checkBounds() {
		if (value < minimum) {
			value = minimum;
		}
		if (value > maximum) {
			value = maximum;
		}
	}

	/**
	 * Fires an value changed event for all registered listeners.
	 */
	private void fireSelectionEvent() {
		ValueChangedEvent valueChangedEvent = new ValueChangedEvent(this, value);
		for (ValueChangedListener listener : listeners) {
			listener.valueChanged(valueChangedEvent);
		}
		Event event = new Event( );
		event.display = this.getDisplay( );
		event.text = Double.toString(this.getSelectionDouble( ));
		event.widget = this;
		event.type = SWT.Selection;
		for (Listener listener : selectionListeners) {
			listener.handleEvent(event);
		}
	}

	/**
	 * Formats the given decimal with the locales decimal format and
	 * cuts the preceding decimal separator off.
	 * 
	 * @param decimal
	 *            the decimal to format
	 * @return the decimal as <code>int</code>
	 */
	private String decimalToString(double decimal) {
		return decimalFormat.format(decimal).substring(1);
	}

	/**
	 * Calculates the increment to use according to the style of the
	 * spinner.
	 * 
	 * @return the increment to use
	 */
	private double getIncrementInternal() {
		if (showIncrement) {
			int textLength = textValue.getText( ).length( );
			double factor = textLength - position - 1;
			double base = 10;
			if (showDouble) {
				if (position >= textLength) {
					factor = (position - textLength) + 1;
					base = 0.1;
				}
			}
			double multi = Math.pow(base, factor);
			return increment * multi;
		}
		else {
			return increment;
		}
	}

	/**
	 * Increments the current value at the current position and
	 * updates the position if needed. Afterwards an selection event
	 * is fired which informs the listeners of the changed data.
	 */
	private void increment() {
		int textLength = getTextLength( );
		value += currentIncrement;

		valueChanged( );
		if (showIncrement) {
			// if the new value is longer than the old
			// value update the position
			// to remain at the same 10 power
			int newTextLength = getTextLength( );
			if (value > 0) {
				if (newTextLength > textLength) {
					position += 1;
					updatePosition( );
				}
			}
			else {
				if (textLength > newTextLength) {
					position -= 1;
					updatePosition( );
				}
			}
		}
		fireSelectionEvent( );
	}

	/**
	 * Gets the length of the text field.
	 * 
	 * @return the length
	 */
	private int getTextLength() {
		int textLength = textValue.getText( ).length( );
		if (value < 0) {
			textLength--;
		}
		return textLength;
	}

	/**
	 * Decrements the current value at the current position and
	 * updates the position if needed. Afterwards an selection event
	 * is fired which informs the listeners of the changed data.
	 */
	private void decrement() {
		int textLength = getTextLength( );
		value -= currentIncrement;

		valueChanged( );
		if (showIncrement) {
			// if the new value is longer than the old
			// value update the position
			// to remain at the same 10 power
			int newTextLength = getTextLength( );
			if (value > 0) {
				if (textLength > newTextLength) {
					position -= 1;
					updatePosition( );
				}
			}
			else {
				if (newTextLength > textLength) {
					position += 1;
					updatePosition( );
				}
			}
		}
		fireSelectionEvent( );
	}

	/**
	 * Checks if this spinner supports doubles.
	 * 
	 * @return <code>true</code> if this spinner has double style
	 */
	public boolean isDouble() {
		return showDouble;
	}
}
