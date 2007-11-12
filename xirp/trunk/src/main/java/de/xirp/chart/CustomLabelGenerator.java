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
 * CustomLabelGenerator.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 27.03.2007:		Created by Matthias Gernand.
 */
package de.xirp.chart;

import java.text.AttributedString;

import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.data.general.PieDataset;

import de.xirp.chart.ChartOptions.OptionName;
import de.xirp.util.I18n;

/**
 * A custom label generator used in generated pie charts in Xirp.
 * 
 * @author Matthias Gernand
 */
final class CustomLabelGenerator implements PieSectionLabelGenerator {

	/**
	 * The options for the chart to be generated.
	 */
	private ChartOptions options;

	/**
	 * Constructs a new custom label generator.
	 * 
	 * @param options
	 *            options of the chart
	 * @see de.xirp.chart.ChartOptions
	 */
	public CustomLabelGenerator(ChartOptions options) {
		super( );
		this.options = options;
	}

	/**
	 * Generates a section label for the given
	 * {@link org.jfree.data.general.PieDataset} and key. If
	 * <code>options.is(OptionName.USE_RELATIVE)</code> is
	 * <code>true</code> the method returns a string containing
	 * information about percentage. If is is <code>false</code> the
	 * method returns a string containing information about absolute
	 * occurrence.
	 * 
	 * @return A <code>String</code> containing either information
	 *         about percentage or absolute occurrences. This text is
	 *         set as label in the generated pie chart.
	 * @see de.xirp.chart.ChartManager
	 * @see org.jfree.chart.labels.PieSectionLabelGenerator#generateSectionLabel(org.jfree.data.general.PieDataset,
	 *      java.lang.Comparable)
	 */
	public String generateSectionLabel(PieDataset dataset, Comparable key) {
		String result = null;
		if (dataset != null) {
			result = key + " " + dataset.getValue(key).toString( ); //$NON-NLS-1$
			if (options.is(OptionName.USE_RELATIVE)) {
				result = result +
						I18n.getString("CustomLabelGenerator.text.percentOccurence"); //$NON-NLS-1$
			}
			else {
				result = result +
						I18n.getString("CustomLabelGenerator.text.occurence"); //$NON-NLS-1$
			}
		}
		return result;
	}

	/**
	 * Returns <code>null</code>. Is not used.
	 * 
	 * @see org.jfree.chart.labels.PieSectionLabelGenerator#generateAttributedSectionLabel(org.jfree.data.general.PieDataset,
	 *      java.lang.Comparable)
	 */
	public AttributedString generateAttributedSectionLabel(PieDataset dataset,
			Comparable key) {
		return null;
	}
}
