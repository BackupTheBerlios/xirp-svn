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
 * MazeComposite.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 03.03.2007:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.panels.virtual;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.xirp.ate.ATEAdapter;
import de.xirp.ate.ATEListener;
import de.xirp.ate.ATEManager;
import de.xirp.ate.Maze.MazeField;
import de.xirp.ui.event.ValueChangedEvent;
import de.xirp.ui.event.ValueChangedListener;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.widgets.custom.*;
import de.xirp.ui.widgets.custom.XStyledSpinner.SpinnerStyle;
import de.xirp.util.FutureRelease;
import de.xirp.util.Util;

/**
 * This is alpha API, may be removed in the future or
 * is planed to be integrated in version 3.0.0.
 * 
 * @author Matthias Gernand
 *
 */
@FutureRelease(version = "3.0.0") //$NON-NLS-1$
public class MazeComposite extends XComposite {

	//private static final Logger logClass = Logger.getLogger(MazeComposite.class);
	private XTable table;
	private XButton add;
	private XTextField varName;
	private List<String> variables = new ArrayList<String>( );
	private XButton remove;
	private XButton execute;
	private XCombo classes;
	private ATEListener listener;
	private MazeCanvas mc;


	/**
	 * @param parent
	 * @param style
	 */
	public MazeComposite(Composite parent, int style) {
		super(parent, style);
		init( );
	}

	/**
	 * 
	 */
	private void init() {
		listener = new ATEAdapter() {

			@Override
			public void classListChanged() {
				classes.removeAll();
				for (String s : ATEManager.getMazeJavaClasses( )) {
					classes.add(s);
				}
			}
			
		};
		ATEManager.addATEListener(listener);
		
		addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				ATEManager.removeATEListener(listener);		
			}
			
		});
		
		SWTUtil.setGridLayout(this, 5, true);

		mc = new MazeCanvas(this, 10, 10);
		SWTUtil.setGridData(mc, false, false, SWT.FILL, SWT.FILL, 4, 3);

		XGroup constantsGroup = new XGroup(this, SWT.NONE);
		constantsGroup.setText("Constants"); //$NON-NLS-1$
		SWTUtil.setGridData(constantsGroup, true, false, SWT.FILL, SWT.FILL, 1,
				1);
		SWTUtil.setGridLayout(constantsGroup, 2, true);

		for (MazeField mf : MazeField.values( )) {
			XLabel reward = new XLabel(constantsGroup, SWT.NONE);
			reward.setText(mf.name( ) + " - Reward:"); //$NON-NLS-1$
			SWTUtil.setGridData(reward, true, false, SWT.FILL, SWT.FILL, 1, 1);

			XStyledSpinner spinner = new XStyledSpinner(constantsGroup,
					SWT.BORDER, SpinnerStyle.ALL);
			SWTUtil.setGridData(spinner, true, false, SWT.FILL, SWT.FILL, 1, 1);
			spinner.setData("name", mf.name( )); //$NON-NLS-1$
			
			ATEManager.setConstant(mf.name( ), 0.0);
			
			spinner.addValueChangedListener(new ValueChangedListener() {

				public void valueChanged(ValueChangedEvent event) {
					XStyledSpinner sp = (XStyledSpinner) event.getSource( );
					ATEManager.setConstant((String)sp.getData("name"), event.getPreciseValue( )); //$NON-NLS-1$
				}		
			});
		}

		XGroup variablesGroup = new XGroup(this, SWT.NONE);
		variablesGroup.setText("Variables"); //$NON-NLS-1$
		SWTUtil.setGridData(variablesGroup, true, true, SWT.FILL, SWT.FILL, 1,
				1);
		SWTUtil.setGridLayout(variablesGroup, 2, true);

		varName = new XTextField(variablesGroup, SWT.NONE);
		SWTUtil.setGridData(varName, true, false, SWT.FILL, SWT.FILL, 2, 1);
		varName.addModifyListener(new ModifyListener( ) {

			public void modifyText(ModifyEvent e) {
				XTextField tf = (XTextField) e.widget;
				String text = tf.getText( );
				if (text.length( ) > 0 && StringUtils.isAsciiPrintable(text)
						&& StringUtils.isAlpha(text)
						&& !variables.contains(text)) {
					add.setEnabled(true);
				}
				else {
					add.setEnabled(false);
				}
			}

		});

		add = new XButton(variablesGroup);
		add.setText("Add"); //$NON-NLS-1$
		add.setEnabled(false);
		SWTUtil.setGridData(add, true, false, SWT.FILL, SWT.FILL, 2, 1);
		add.addSelectionListener(new SelectionAdapter( ) {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				XTableItem itm = new XTableItem(table, SWT.NONE);
				String text = varName.getText( );

				variables.add(text);
				itm.setText(0, text);
				//TODO
				itm.setText(1, "23.0"); //$NON-NLS-1$

				SWTUtil.packTable(table);
				add.setEnabled(false);
				
				ATEManager.setVariable(text, 23.0);
			}

		});

		table = new XTable(variablesGroup, SWT.FULL_SELECTION | SWT.SINGLE
				| SWT.BORDER);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		String[] titles = {"Variable", "Value"}; //$NON-NLS-1$ //$NON-NLS-2$
		for (int i = 0; i < titles.length; i++) {
			XTableColumn column = new XTableColumn(table, SWT.NONE);
			column.setText(titles[i]);
		}

		SWTUtil.packTable(table);
		SWTUtil.setGridData(table, true, true, SWT.FILL, SWT.FILL, 2, 1);

		remove = new XButton(variablesGroup);
		remove.setText("Remove selected"); //$NON-NLS-1$
		SWTUtil.setGridData(remove, true, false, SWT.FILL, SWT.FILL, 2, 1);
		remove.addSelectionListener(new SelectionAdapter( ) {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					int idx = table.getSelectionIndex( );
					String text = table.getItem(idx).getText(0);
					table.remove(idx);
					variables.remove(text);
				}
				catch (IllegalArgumentException ex) {
				}
			}

		});

		XGroup exeGroup = new XGroup(this, SWT.NONE);
		exeGroup.setText("Execute"); //$NON-NLS-1$
		SWTUtil.setGridData(exeGroup, true, false, SWT.FILL, SWT.FILL, 1,
				1);
		SWTUtil.setGridLayout(exeGroup, 2, true);
		
		classes = new XCombo(exeGroup, SWT.BORDER | SWT.READ_ONLY);
		for (String s : ATEManager.getMazeJavaClasses( )) {
			classes.add(s);
		}
		classes.addSelectionListener(new SelectionAdapter() {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				XCombo c = (XCombo) e.widget;
				if (!Util.isEmpty(c.getText( ))) {
					execute.setEnabled(true);
				}
			}
			
		});
		SWTUtil.setGridData(classes, true, false, SWT.FILL, SWT.FILL, 2, 1);
		
		execute = new XButton(exeGroup);
		execute.setText("Execute code"); //$NON-NLS-1$
		execute.setEnabled(false);
		SWTUtil.setGridData(execute, true, false, SWT.FILL, SWT.FILL, 2, 1);
		execute.addSelectionListener(new SelectionAdapter( ) {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				ATEManager.execute(classes.getText( ));
			}

		});
	}
	
	public static void main(String... strings) {
		
		BasicConfigurator.configure();
		
		Display display = new Display( );
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout( ));

		MazeComposite mc = new MazeComposite(shell, SWT.NONE);  

		shell.setSize(800, 500);
		shell.open( );
		while (!shell.isDisposed( )) {
			if (!display.readAndDispatch( ))
				display.sleep( );
		}
		display.dispose( );
	}

}
