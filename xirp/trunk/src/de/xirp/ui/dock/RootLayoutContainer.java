package de.xirp.ui.dock;

/*********************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others. All rights
 * reserved. This program and the accompanying materials are made
 * available under the terms of the Common Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ********************************************************************/

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IMemento;

/**
 * Represents the top level container.<br>
 */
class RootLayoutContainer extends PartSashContainer {

	/**
	 * 
	 */
	private static final int DEFAULT_FOLDER_STYLE = SWT.TOP;

	/**
	 * 
	 */
	public RootLayoutContainer() {
		// : base("root layout container"/*,page APORT TODO */) {
		// //$NON-NLS-1$
		super("root layout container"); //$NON-NLS-1$
	}

	/**
	 * Notification that a child layout part has been added to the<br>
	 * container. Subclasses may override this method to perform any<br>
	 * container specific work.<br>
	 * 
	 * @param child
	 */
	@Override
	public void childAdded(ILayoutPart child) {
		// do nothing
	}

	/**
	 * Gets root container for this part.<br>
	 * 
	 * @return RootLayoutContainer
	 */
	@Override
	public RootLayoutContainer getRootContainer() {
		return this;
	}

	/**
	 * Notification that a child layout part has been removed from the<br>
	 * container. Subclasses may override this method to perform any<br>
	 * container specific work.<br>
	 * 
	 * @param child
	 */
	@Override
	public void childRemoved(ILayoutPart child) {
		// do nothing
	}

	/**
	 * Subclasses override this method to specify the composite to use<br>
	 * to parent all children layout parts it contains.<br>
	 * 
	 * @param parentWidget
	 * @return Composite
	 */
	@Override
	public Composite createParent(Composite parentWidget) {
		return parentWidget;
	}

	/**
	 * Subclasses override this method to dispose of any swt resources<br>
	 * created during createParent.<br>
	 */
	@Override
	public void disposeParent() {
		// do nothing
	}

	/**
	 * Get the part control. This method may return null.<br>
	 * 
	 * @return Control
	 */
	@Override
	public Control getControl() {
		return this.parent;
	}

	/**
	 * @param memento
	 */
	public void restoreState(IMemento memento) {

		// MultiStatus result = new MultiStatus("RootLayoutContainer:
		// Problems restoring layout",null); //$NON-NLS-1$

		// Read the info elements.
		IMemento[] children = memento
				.getChildren(IDockingManagerConsts.TAG_INFO);

		// Create a part ID to part hashtable.
		Map<String, ILayoutPart> mapIDtoPart = new HashMap<String, ILayoutPart>(
				children.length);

		// Loop through the info elements.
		for(IMemento childMem : children){
//		for (int i = 0; i < children.length; i++) {
			// Get the info details.
//			IMemento childMem = children[i];
			String partID = childMem.getString(IDockingManagerConsts.TAG_PART);
			String relativeID = childMem
					.getString(IDockingManagerConsts.TAG_RELATIVE);
			IPageLayout.Location relationship = IPageLayout.Location.LEFT;
			float ratio = 0.0f;
			if (relativeID != null) {
				relationship = IPageLayout.Location.valueOf(childMem.getString(IDockingManagerConsts.TAG_RELATIONSHIP));
				
//				relationship = childMem.getInteger(
//						IDockingManagerConsts.TAG_RELATIONSHIP).intValue( );
				ratio = childMem.getFloat(IDockingManagerConsts.TAG_RATIO)
						.floatValue( );
			}
			String strFolder = childMem
					.getString(IDockingManagerConsts.TAG_FOLDER);

			// Create the part.
			ILayoutPart part = null;
			if (strFolder == null){
				part = new PartPlaceholder(partID);
			}
			else {
				PartTabFolder folder = new PartTabFolder(DEFAULT_FOLDER_STYLE);
				folder.setID(partID);
				// result.add(folder.restoreState(childMem.getChild(IDockingManagerConsts.TAG_FOLDER)));
				folder.restoreState(childMem
						.getChild(IDockingManagerConsts.TAG_FOLDER));
				ContainerPlaceholder placeholder = new ContainerPlaceholder(
						partID);
				placeholder.setRealContainer(folder);
				part = placeholder;
			}
			// 1FUN70C: ITPUI:WIN - Shouldn't set Container when not
			// active
			part.setContainer(this);

			// Add the part to the layout
			if (relativeID == null) {
				add(part);
			}
			else {
				ILayoutPart refPart = mapIDtoPart.get(relativeID);
				if (refPart != null) {
					add(part, relationship, ratio, refPart);
				}
				else {
					System.err
							.println("Unable to find part for ID: " + relativeID);//$NON-NLS-1$
				}
			}
			mapIDtoPart.put(partID, part);
		}
		// return result;

	}

	/**
	 * @param memento
	 */
	public void saveState(IMemento memento) {
		RelationshipInfo[] relationships = computeRelation( );
//		 MultiStatus result = new MultiStatus("Problem saving layout",null); //$NON-NLS-1$
		for(RelationshipInfo info : relationships){
		// Loop through the relationship array.
//		for (int i = 0; i < relationships.length; i++) {
			// Save the relationship info ..
			// private LayoutPart part;
			// private int relationship;
			// private float ratio;
			// private LayoutPart relative;
//			RelationshipInfo info = relationships[i];
			IMemento childMem = memento
					.createChild(IDockingManagerConsts.TAG_INFO);
			childMem.putString(IDockingManagerConsts.TAG_PART, info.part
					.getID( ));
			if (info.relative != null) {
				childMem.putString(IDockingManagerConsts.TAG_RELATIVE,
						info.relative.getID( ));
				childMem.putString(IDockingManagerConsts.TAG_RELATIONSHIP,
						info.relationship.name( ));
				childMem.putFloat(IDockingManagerConsts.TAG_RATIO, info.ratio);
			}

			// Is this part a folder or a placeholder for one?
			PartTabFolder folder = null;
			if (info.part instanceof PartTabFolder) {
				folder = (PartTabFolder) info.part;
			}
			else if (info.part instanceof ContainerPlaceholder) {
				ILayoutPart part = ((ContainerPlaceholder) info.part)
						.getRealContainer( );
				if (part instanceof PartTabFolder){
					folder = (PartTabFolder) part;
				}
			}

			// If this is a folder save the contents.
			if (folder != null) {
				childMem.putString(IDockingManagerConsts.TAG_FOLDER, "true");//$NON-NLS-1$
				IMemento folderMem = childMem
						.createChild(IDockingManagerConsts.TAG_FOLDER);
//				result.add(folder.saveState(folderMem));
				folder.saveState(folderMem);
			}
		}
		// return result;
	}
}
