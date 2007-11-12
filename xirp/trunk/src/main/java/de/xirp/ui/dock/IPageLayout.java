package de.xirp.ui.dock;

import org.eclipse.swt.SWT;

/*********************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others. All rights
 * reserved. This program and the accompanying materials are made
 * available under the terms of the Common Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html Contributors: IBM
 * Corporation - initial API and implementation
 ********************************************************************/

class IPageLayout {
	
	public enum Location {
		/**
		 * Relationship static finalant indicating a part should be placed
		 * to the left of its relative.
		 */
		LEFT,//1
		/**
		 * Relationship static finalant indicating a part should be placed
		 * to the right of its relative.
		 */
		RIGHT,//2
		/**
		 * Relationship static finalant indicating a part should be placed
		 * above its relative.
		 */
		TOP,//3
		/**
		 * Relationship static finalant indicating a part should be placed
		 * below its relative.
		 */
		BOTTOM;//4
		
		public int getSWTConstant(){
			return isVertical() ? SWT.VERTICAL
					: SWT.HORIZONTAL;
		}
		
		public boolean isVertical(){
			return (this == LEFT || this == RIGHT);
		}
		
		public boolean isHorizontal(){
			return (this == TOP || this == BOTTOM);
		}
		
		public boolean isLeft(){
			return (this == LEFT || this == TOP);
		}
	}

	/**
	 * Minimum acceptable ratio value when adding a view
	 * 
	 * @since 2.0
	 */
	public static final float RATIO_MIN = 0.05f;

	/**
	 * Maximum acceptable ratio value when adding a view
	 * 
	 * @since 2.0
	 */
	public static final float RATIO_MAX = 0.95f;
}
