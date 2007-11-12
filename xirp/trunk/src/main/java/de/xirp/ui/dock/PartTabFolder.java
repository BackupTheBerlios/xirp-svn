package de.xirp.ui.dock;

/*********************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others. All rights
 * reserved. This program and the accompanying materials! are made
 * available under the terms of the Common Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 * Cagatay Kavukcuoglu <cagatayk@acm.org> - Fix for bug 10025 -
 * Resizing views should not use height ratios
 ********************************************************************/

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IMemento;

import de.xirp.ui.widgets.custom.XTabFolder;
import de.xirp.ui.widgets.custom.XTabItem;
import de.xirp.util.I18n;
import de.xirp.util.II18nHandler;

/**
 * 
 */
public class PartTabFolder extends LayoutPart implements ILayoutContainer {

	/**
	 * Initialized in constructor.
	 */
	//FIXME static
	private static int tabLocation = -1;
	/**
	 * 
	 */
	@SuppressWarnings("unused")//$NON-NLS-1$
//	private static int nextUID = 1;
	/**
	 * 
	 */
	private XTabFolder tabFolder;
	/**
	 * 
	 */
	private Map<XTabItem, ILayoutPart> mapTabToPart = new HashMap<XTabItem, ILayoutPart>( );
	/**
	 * 
	 */
	private Map<ILayoutPart, CTabPartDragDrop> mapPartToDragMonitor = new HashMap<ILayoutPart, CTabPartDragDrop>( );
	/**
	 * 
	 */
	private boolean assignFocusOnSelection = true;
	/**
	 * 
	 */
	private ILayoutPart current;
	/**
	 * inactiveCurrent is only used when restoring the persisted state<br>
	 * of perspective on startup.<br>
	 */
	private ILayoutPart inactiveCurrent;
	/**
	 * 
	 */
	private Composite parent;
	/**
	 * 
	 */
	private boolean active = false;

	/**
	 * listen for mouse down on close button of tabitem
	 */
	private final CTabFolder2Adapter folderCloseListener = new CTabFolder2Adapter( ) {

		@Override
		public void close(CTabFolderEvent e) {
			ILayoutPart item = mapTabToPart.get(e.item);
			if (item != null) {
				remove(item);
			}
		}
	};

	/**
	 * listen for mouse down on tab to set focus.
	 */
	private MouseListener mouseListener = new MouseAdapter( ) {

		@Override
		public void mouseDown(MouseEvent e) {
			// PR#1GDEZ25 - If selection will change in mouse up
			// ignore mouse down.
			// Else, set focus.
			CTabItem newItem = tabFolder.getItem(new Point(e.x, e.y));
			if (newItem != null) {
				CTabItem oldItem = tabFolder.getSelection( );
				if (newItem != oldItem) {
					return;
				}
			}
			if (PartTabFolder.this.current != null) {
				PartTabFolder.this.current.setFocus( );
			}
		}
	};

	/**
	 * 
	 */
	protected class TabInfo { // APORT correct translation would be

		// to add
		// enclosing instance, APORT changed private to
		// internal
		/**
		 * 
		 */
		public String tabText;
		/**
		 * 
		 */
		public ILayoutPart part;
		
		public boolean isKey = false;
		
		public II18nHandler handler;
		public Object[] args = null;
	}

	/**
	 * 
	 */
	private TabInfo[] invisibleChildren;

	/**
	 * @param location
	 */
	public PartTabFolder(int location) {

		super("PartTabFolder");//$NON-NLS-1$
		setID(this.toString( )); // Each folder has a unique ID so
		// relative positioning is
		// unambiguous.

		// Get the location of the tabs from the preferences
		if (tabLocation == -1) {
			// tabLocation = getPreferenceStore().getInt(
			// IPreferenceConstants.VIEW_TAB_POSITION); APORT TODO
			tabLocation = location; // APORT TODO
		}

	}

	/**
	 * @see LayoutPart#isViewPane()
	 */
	@Override
	public boolean isViewPane() {
		return false;
	}

	/**
	 * Updates the tab labels to reflect new part names.<br>
	 */
	public void updateTabs() {
		for (Entry<XTabItem, ILayoutPart> entry : mapTabToPart.entrySet( )) {
			ILayoutPart part = entry.getValue( );
			XTabItem item = entry.getKey( );
			String text = part.getName( );
			if (text.equals(item.getText( ))) {
				item.setText(text);
			}
		}
	}

	/**
	 * Add a part at an index.<br>
	 * 
	 * @param name
	 * @param index
	 * @param part
	 */
	public void add(String name, int index, ILayoutPart part, boolean isKey, II18nHandler handler, Object... args) {
		if (active && !(part instanceof PartPlaceholder)) {
			XTabItem tab = createPartTab(part, name, index,isKey,handler,args);
			index = tabFolder.indexOf(tab);
			setSelection(index);
		}
		else {
			TabInfo info = new TabInfo( );
			info.tabText = name;
			info.part = part;
			info.isKey = isKey;
			info.handler = handler;
			info.args = args;
			invisibleChildren = arrayAdd(invisibleChildren, info, index);
			if (active) {
				part.setContainer(this);
			}
		}
	}

	/**
	 * @param child
	 */
	public void add(ILayoutPart child) {
		int index = getItemCount( );
//		String key = child.getID( );//$NON-NLS-1$
		// if (child is PartPane) {
		// WorkbenchPartReference ref =
		// (WorkbenchPartReference)((PartPane)child).getPartReference();
		// label = ref.getRegisteredName();
		// } APORT
		String key = child.getNameKey( );

		add(key, index, child,true,child.getI18nHandler( ), child.getI18nArgs( ));
	}

	/**
	 * @return boolean
	 * @see ILayoutContainer#allowsBorder There is already a border
	 *      around the tab folder so no need for<br>
	 *      one from the parts.<br>
	 */
	public boolean allowsBorder() {
		return mapTabToPart.size( ) <= 1;
	}

	/**
	 * @param array
	 * @param item
	 * @param index
	 * @return TabInfo[]
	 */
	private TabInfo[] arrayAdd(TabInfo[] array, TabInfo item, int index) {

		if (item == null) {
			return array;
		}

		TabInfo[] result = null;

		if (array == null) {
			result = new TabInfo[1];
			result[0] = item;
		}
		else {
			if (index >= array.length) {
				index = array.length;
			}
			result = new TabInfo[array.length + 1];
			System.arraycopy(array, 0, result, 0, index);
			result[index] = item;
			System.arraycopy(array, index, result, index + 1, array.length
					- index);
		}

		return result;
	}

	/**
	 * @param array
	 * @param item
	 * @return TabInfo[]
	 */
	private TabInfo[] arrayRemove(TabInfo[] array, ILayoutPart item) {

		if (item == null) {
			return array;
		}

		TabInfo[] result = null;

		int index = -1;
		for (int i = 0, length = array.length; i < length; i++) {
			if (item == array[i].part) {
				index = i;
				break;
			}
		}
		if (index == -1)
			return array;

		if (array.length > 1) {
			result = new TabInfo[array.length - 1];
			System.arraycopy(array, 0, result, 0, index);
			System.arraycopy(array, index + 1, result, index, result.length
					- index);
		}
		return result;
	}

	/**
	 * Set the default bounds of a page in a CTabFolder.<br>
	 * 
	 * @param folder
	 * @return Rectangle
	 */
	public static Rectangle calculatePageBounds(XTabFolder folder) {
		if (folder == null) {
			return new Rectangle(0, 0, 0, 0);
		}
		Rectangle bounds = folder.getBounds( );
		Rectangle offset = folder.getClientArea( );
		bounds.x += offset.x;
		bounds.y += offset.y;
		bounds.width = offset.width;
		bounds.height = offset.height;
		return bounds;
	}

	/**
	 * @see LayoutPart#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {

		if (tabFolder != null) {
			return;
		}

		// Create control.
		this.parent = parent;
		tabFolder = new XTabFolder(parent, tabLocation | SWT.BORDER);
		tabFolder.setSimple(false);
		tabFolder.setTabPosition(SWT.BOTTOM);

		// listener to switch between visible tabItems
		tabFolder.addListener(SWT.Selection, new Listener( ) {

			public void handleEvent(Event e) {
				ILayoutPart item = mapTabToPart.get(e.item);
				// Item can be null when tab is just created but not
				// map yet.
				if (item != null) {
					setSelection(item);
					if (assignFocusOnSelection) {
						item.setFocus( );
					}
				}
			}
		});

		// listener to resize visible components
		tabFolder.addListener(SWT.Resize, new Listener( ) {

			public void handleEvent(Event e) {
				setControlSize( );
			}
		});

		// listener to detect close
		if ((tabLocation & SWT.CLOSE) != 0) {
			tabFolder.addCTabFolder2Listener(folderCloseListener);
		}

		// listen for mouse down on tab to set focus.
		tabFolder.addMouseListener(this.mouseListener);

		// enable for drag & drop target
		tabFolder.setData(this);

		// Create pages.
		if (invisibleChildren != null) {
			TabInfo[] stillInactive = new TabInfo[0];
			int tabCount = 0;
			for (int i = 0, length = invisibleChildren.length; i < length; i++) {
				TabInfo tabInfo = invisibleChildren[i];
				if (tabInfo.part instanceof PartPlaceholder) {
					tabInfo.part.setContainer(this);
					TabInfo[] newStillInactive = new TabInfo[stillInactive.length + 1];
					System.arraycopy(stillInactive, 0, newStillInactive, 0,
							stillInactive.length);
					newStillInactive[stillInactive.length] = tabInfo;
					stillInactive = newStillInactive;
				}
				else {
					createPartTab(tabInfo.part,
							tabInfo.tabText, tabCount,tabInfo.isKey,tabInfo.handler,tabInfo.args);
					++tabCount;
				}
			}
			invisibleChildren = stillInactive;
		}

		active = true;

		// Set current page.
		if (getItemCount( ) > 0) {
			int newPage = 0;
			if (current != null) {
				newPage = indexOf(current);
			}
			setSelection(newPage);
		}

	}

	/**
	 * @param part
	 * @param tabName
	 * @param tabIndex
	 * @param isKey 
	 * @param handler 
	 * @param args 
	 * @return CTabItem
	 */
	private XTabItem createPartTab(ILayoutPart part, String tabName,
			int tabIndex, boolean isKey, II18nHandler handler, Object[] args) {
		XTabItem tabItem;

		if (tabIndex < 0) {
			tabItem = new XTabItem(this.tabFolder, SWT.NONE,handler);
		}
		else {
			tabItem = new XTabItem(this.tabFolder, SWT.NONE, tabIndex,handler);
		}
		if(isKey){
			tabItem.setTextForLocaleKey(tabName,args);
		}
		else{
			tabItem.setText(tabName);
		}

		mapTabToPart.put(tabItem, part);

		part.createControl(this.parent);
		part.setContainer(this);

		// Because the container's allow border api
		// is dependent on the number of tabs it has,
		// reset the container so the parts can update.
		if (mapTabToPart.size( ) == 2) {
			Iterator<ILayoutPart> parts = mapTabToPart.values( ).iterator( );
			((LayoutPart) parts.next( )).setContainer(this);
			((LayoutPart) parts.next( )).setContainer(this);
		}

		return tabItem;
	}

	/**
	 * Remove the ability to d&d using the tab.<br>
	 * 
	 * @param part
	 */
	public void disableDrag(ILayoutPart part) {
		PartDragDrop partDragDrop = mapPartToDragMonitor.get(part);
		if (partDragDrop != null) {
			partDragDrop.dispose( );
			mapPartToDragMonitor.remove(part);
		}

		// remove d&d on folder when no tabs left
		if (mapPartToDragMonitor.size( ) == 1) {
			partDragDrop = mapPartToDragMonitor.get(this);
			if (partDragDrop != null) {
				partDragDrop.dispose( );
				mapPartToDragMonitor.remove(this);
			}
		}
	}

	/**
	 * @see LayoutPart#dispose()
	 */
	@Override
	public void dispose() {
		if (!active) {
			return;
		}

		// combine active and inactive entries into one
		TabInfo[] newInvisibleChildren = new TabInfo[mapTabToPart.size( )];

		if (invisibleChildren != null) {
			// tack the inactive ones on at the end
			newInvisibleChildren = new TabInfo[newInvisibleChildren.length
					+ invisibleChildren.length];
			System.arraycopy(invisibleChildren, 0, newInvisibleChildren,
					mapTabToPart.size( ), invisibleChildren.length);
		}

		for (Entry<XTabItem, ILayoutPart> entry : mapTabToPart.entrySet( )) {
			// Iterator keys = mapTabToPart.keySet( ).iterator( );
			// while (keys.hasNext( )) {
			XTabItem item = entry.getKey( );
			ILayoutPart part = entry.getValue( );
			TabInfo info = new TabInfo( );
			info.tabText = item.getText( );
			info.part = part;
			newInvisibleChildren[tabFolder.indexOf(item)] = info;
			disableDrag(part);
		}

		invisibleChildren = newInvisibleChildren;

		if (invisibleChildren != null) {
			for (TabInfo info : invisibleChildren) {
				// for (int i = 0, length = invisibleChildren.length;
				// i < length; i++) {
				info.part.setContainer(null);
			}
		}

		mapTabToPart.clear( );

		if (tabFolder != null) {
			tabFolder.dispose( );
		}
		tabFolder = null;

		active = false;

	}

	/**
	 * Enable the view pane to be d&d via its tab.<br>
	 * 
	 * @param pane
	 * @param listener
	 */
	public void enableDrag(/* ViewPane */ILayoutPart pane,
			IPartDropListener listener) {
		// make sure its not already registered
		if (mapPartToDragMonitor.containsKey(pane)) {
			return;
		}

		CTabItem tab = getTab(pane);
		if (tab == null) {
			return;
		}

		CTabPartDragDrop dragSource = new CTabPartDragDrop(pane,
				this.tabFolder, tab);
		mapPartToDragMonitor.put(pane, dragSource);
		dragSource.addDropListener(listener);

		// register d&d on empty tab area the first time thru
		if (mapPartToDragMonitor.size( ) == 1) {
			dragSource = new CTabPartDragDrop(this, this.tabFolder, null);
			mapPartToDragMonitor.put(this, dragSource);
			dragSource.addDropListener(listener);
		}
	}

	/**
	 * Open the tracker to allow the user to move the specified part<br>
	 * using keyboard.<br>
	 * 
	 * @param part
	 */
	public void openTracker(LayoutPart part) {
		CTabPartDragDrop dnd = mapPartToDragMonitor.get(part);
		dnd.openTracker( );

	}

	/**
	 * @see LayoutPart#getBounds()
	 */
	@Override
	public Rectangle getBounds() {
		return tabFolder.getBounds( );
	}

	/**
	 * @see LayoutPart#setBounds(org.eclipse.swt.graphics.Rectangle)
	 */
	@Override
	public void setBounds(Rectangle value) {
		if (tabFolder != null) {
			tabFolder.setBounds(value);
		}
		setControlSize( );
	}

	// getMinimumHeight() added by cagatayk@acm.org
	/**
	 * @see LayoutPart#getMinimumHeight()
	 */
	@Override
	public int getMinimumHeight() {
		if (current == null || tabFolder == null || tabFolder.isDisposed( )) {
			return super.getMinimumHeight( );
		}

		if (getItemCount( ) > 1) {
			Rectangle trim = tabFolder.computeTrim(0, 0, 0, current
					.getMinimumHeight( ));
			return trim.height;
		}
		else {
			return current.getMinimumHeight( );
		}
	}

	/**
	 * @see ILayoutContainer#getChildren()
	 */
	public ILayoutPart[] getChildren() {

		ILayoutPart[] children = new ILayoutPart[0];

		if (invisibleChildren != null) {
			children = new LayoutPart[invisibleChildren.length];
			for (int i = 0, length = invisibleChildren.length; i < length; i++) {
				children[i] = invisibleChildren[i].part;
			}
		}

		int count = mapTabToPart.size( );
		if (count > 0) {
			int index = children.length;
			ILayoutPart[] newChildren = new ILayoutPart[children.length + count];
			System.arraycopy(children, 0, newChildren, 0, children.length);
			children = newChildren;
			for (int nX = 0; nX < count; nX++) {
				CTabItem tabItem = tabFolder.getItem(nX);
				children[index] = mapTabToPart.get(tabItem);
				index++;
			}
		}

		return children;

	}

	/**
	 * @see LayoutPart#getControl()
	 */
	@Override
	public Control getControl() {
		return tabFolder;
	}

	/**
	 * Answer the number of children.<br>
	 * 
	 * @return int
	 */
	public int getItemCount() {
		if (active) {
			return tabFolder.getItemCount( );
		}
		else if (invisibleChildren != null) {
			return invisibleChildren.length;
		}
		else {
			return 0;
		}

	}

	/**
	 * Get the parent control.<br>
	 * 
	 * @return Composite
	 */
	public Composite getParent() {
		return tabFolder.getParent( );
	}

	/**
	 * @return int
	 */
	public int getSelection() { // APORT renamed getSelection to
		// SelectionIndex
		if (!active) {
			return 0;
		}
		return tabFolder.getSelectionIndex( );
	}

	/**
	 * Returns the tab for a part.<br>
	 * 
	 * @param child
	 * @return CTabItem
	 */
	private CTabItem getTab(ILayoutPart child) {
		for (Entry<XTabItem, ILayoutPart> entry : mapTabToPart.entrySet( )) {
			// Iterator tabs = mapTabToPart.keySet( ).iterator( );
			// while (tabs.hasNext( )) {
			XTabItem tab = entry.getKey( );
			if (entry.getValue( ) == child)
				return tab;
		}

		return null;
	}

	/**
	 * Returns the visible child.<br>
	 * 
	 * @return ILayoutPart
	 */
	public ILayoutPart getVisiblePart() {
		if (current == null) {
			return inactiveCurrent;
		}
		return current;
	}

	/**
	 * @param item
	 * @return int
	 */
	public int indexOf(ILayoutPart item) {
		for (Entry<XTabItem, ILayoutPart> entry : mapTabToPart.entrySet( )) {
			// Iterator keys = mapTabToPart.keySet( ).iterator( );
			// while (keys.hasNext( )) {
			XTabItem tab = entry.getKey( );
			ILayoutPart part = entry.getValue( );
			if (part.equals(item))
				return tabFolder.indexOf(tab);
		}

		return 0;
	}

	/**
	 * @see ILayoutContainer#remove
	 */
	public void remove(ILayoutPart child) {

		if (active && !(child instanceof PartPlaceholder)) {
			for (Entry<XTabItem, ILayoutPart> entry : mapTabToPart.entrySet( )) {
				// Iterator keys = mapTabToPart.keySet( ).iterator( );
				// while (keys.hasNext( )) {
				XTabItem key = entry.getKey( );
				if (entry.getValue( ).equals(child)) {
					removeTab(key);
					break;
				}
			}
		}
		else if (invisibleChildren != null) {
			invisibleChildren = arrayRemove(invisibleChildren, child);
		}

		if (active) {
			child.setVisible(false);
			child.setContainer(null);
		}
	}

	/**
	 * @param tab
	 */
	private void removeTab(CTabItem tab) {
		// disable any d&d based on this tab
		ILayoutPart part = mapTabToPart.get(tab);
		if (part != null) {
			disableDrag(part);
		}

		// remove the tab now
		// Note, that disposing of the tab causes the
		// tab folder to select the next tab and fires
		// a selection event. In this situation, do
		// not assign focus.
		assignFocusOnSelection = false;
		mapTabToPart.remove(tab);
		tab.dispose( );
		assignFocusOnSelection = true;

		// Because the container's allow border api
		// is dependent on the number of tabs it has,
		// reset the container so the parts can update.
		if (mapTabToPart.size( ) == 1) {
			Iterator<ILayoutPart> parts = mapTabToPart.values( ).iterator( );
			((LayoutPart) parts.next( )).setContainer(this);
		}
	}

	/**
	 * Reorder the tab representing the specified pane. If a tab<br>
	 * exists under the specified x,y location, then move the tab<br>
	 * before it, otherwise place it as the last tab.<br>
	 * 
	 * @param pane
	 * @param x
	 * @param y
	 */
	public void reorderTab(/* ViewPane */ILayoutPart pane, int x, int y) {
		CTabItem sourceTab = getTab(pane);
		if (sourceTab == null) {
			return;
		}

		// adjust the y coordinate to fall within the tab area
		Point location = new Point(1, 1);
		if ((tabFolder.getStyle( ) & SWT.BOTTOM) != 0) {
			location.y = tabFolder.getSize( ).y - 4; // account for
			// 3 pixel
			// border
		}

		// adjust the x coordinate to be within the tab area
		if (x > location.x) {
			location.x = x;
		}

		// find the tab under the adjusted location.
		CTabItem targetTab = tabFolder.getItem(location);

		// no tab under location so move view's tab to end
		if (targetTab == null) {
			// do nothing if already at the end
			if (tabFolder.indexOf(sourceTab) != tabFolder.getItemCount( ) - 1) {
				reorderTab(pane, sourceTab, -1);
			}

			return;
		}

		// do nothing if over view's own tab
		if (targetTab == sourceTab) {
			return;
		}

		// do nothing if already before target tab
		int sourceIndex = tabFolder.indexOf(sourceTab);
		int targetIndex = tabFolder.indexOf(targetTab);
		if (sourceIndex == targetIndex - 1) {
			return;
		}

		reorderTab(pane, sourceTab, targetIndex);
	}

	/**
	 * Reorder the tab representing the specified pane.<br>
	 * 
	 * @param pane
	 * @param sourceTab
	 * @param newIndex
	 */
	private void reorderTab(/* ViewPane */ILayoutPart pane,
			CTabItem sourceTab, int newIndex) {
		// remember if the source tab was the visible one
		boolean wasVisible = (tabFolder.getSelection( ) == sourceTab);

		// create the new tab at the specified index
		XTabItem newTab;
		if (newIndex < 0) {
			newTab = new XTabItem(tabFolder, SWT.NONE);
		}
		else {
			newTab = new XTabItem(tabFolder, SWT.NONE, newIndex);
		}

		// map it now before events start coming in...
		mapTabToPart.put(newTab, pane);

		// update the drag & drop
		CTabPartDragDrop partDragDrop = mapPartToDragMonitor.get(pane);
		partDragDrop.setTab(newTab);

		// dispose of the old tab and remove it
		String sourceLabel = sourceTab.getText( );
		mapTabToPart.remove(sourceTab);
		assignFocusOnSelection = false;
		sourceTab.dispose( );
		assignFocusOnSelection = true;

		// update the new tab's title and visibility
		newTab.setText(sourceLabel);
		if (wasVisible) {
			tabFolder.setSelection(newTab);
			setSelection(pane);
			pane.setFocus( );
		}
	}

	/**
	 * Reparent a part. Also reparent visible children...<br>
	 * 
	 * @see LayoutPart#reparent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void reparent(Composite newParent) {
		if (!newParent.isReparentable( )) {
			return;
		}

		Control control = getControl( );
		if ((control == null) || (control.getParent( ) == newParent)) {
			return;
		}

		super.reparent(newParent);

		// reparent also the visible children.
		Iterator<ILayoutPart> enuma = mapTabToPart.values( ).iterator( );
		while (enuma.hasNext( ))
			((LayoutPart) enuma.next( )).reparent(newParent);
	}

	/**
	 * @see ILayoutContainer#replace(ILayoutPart, ILayoutPart)
	 */
	public void replace(ILayoutPart oldChild, ILayoutPart newChild) {

		if ((oldChild instanceof PartPlaceholder)
				&& !(newChild instanceof PartPlaceholder)) {
			replaceChild((PartPlaceholder) oldChild, newChild);
			return;
		}

		if (!(oldChild instanceof PartPlaceholder)
				&& (newChild instanceof PartPlaceholder)) {
			replaceChild(oldChild, (PartPlaceholder) newChild);
			return;
		}

	}

	/**
	 * @param oldChild
	 * @param newChild
	 */
	private void replaceChild(ILayoutPart oldChild, PartPlaceholder newChild) {

		// remove old child from display
		if (active) {
			for (Entry<XTabItem, ILayoutPart> entry : mapTabToPart.entrySet( )) {
				// Iterator keys = mapTabToPart.keySet( ).iterator( );
				// while (keys.hasNext( )) {
				XTabItem key = entry.getKey( );
				ILayoutPart part = entry.getValue( );
				if (part == oldChild) {
					boolean partIsActive = (current == oldChild);
					TabInfo info = new TabInfo( );
					info.part = newChild;
					info.tabText = key.getText( );
					removeTab(key);
					int index = 0;
					if (invisibleChildren != null) {
						index = invisibleChildren.length;
					}
					invisibleChildren = arrayAdd(invisibleChildren, info, index);
					oldChild.setVisible(false);
					oldChild.setContainer(null);
					newChild.setContainer(this);
					if (tabFolder.getItemCount( ) > 0 && !partIsActive) {
						setControlSize( );
					}
					break;
				}
			}
		}
		else if (invisibleChildren != null) {
			for (int i = 0, length = invisibleChildren.length; i < length; i++) {
				if (invisibleChildren[i].part == oldChild) {
					invisibleChildren[i].part = newChild;
				}
			}
		}
	}

	/**
	 * @param oldChild
	 * @param newChild
	 */
	private void replaceChild(PartPlaceholder oldChild, ILayoutPart newChild) {
		if (invisibleChildren == null) {
			return;
		}

		for (int i = 0, length = invisibleChildren.length; i < length; i++) {
			TabInfo tabInfo = invisibleChildren[i];
			if (tabInfo.part == oldChild) {
				if (active) {
					TabInfo info = tabInfo;
					invisibleChildren = arrayRemove(invisibleChildren, oldChild);
					oldChild.setContainer(null);

					// if (newChild is PartPane) {
					// WorkbenchPartReference ref =
					// (WorkbenchPartReference)((PartPane)newChild).getPartReference();
					// info.tabText = ref.getRegisteredName();
					// }
					info.tabText = newChild.getName( );
					String key = newChild.getNameKey( );
					if(key != null){
						info.tabText = key;
						info.isKey = true;
						info.handler = newChild.getI18nHandler( );
						info.args = newChild.getI18nArgs( );
					}
					XTabItem item = createPartTab(newChild, info.tabText, -1,info.isKey,info.handler,info.args);
					int index = tabFolder.indexOf(item);
					setSelection(index);
				}
				else {
					tabInfo.part = newChild;
					// On restore, all views are initially represented
					// by
					// placeholders and then
					// they are replaced with the real views. The
					// following code
					// is used to preserve the active
					// tab when a prespective is restored from its
					// persisted
					// state.
					if (inactiveCurrent != null && inactiveCurrent == oldChild) {
						current = newChild;
						inactiveCurrent = null;
					}
				}
				break;
			}
		}
	}

	/**
	 * @param memento
	 */
	public void restoreState(IMemento memento) {
		// Read the active tab.
		String activeTabID = memento
				.getString(IDockingManagerConsts.TAG_ACTIVE_PAGE_ID);

		// Read the page elements.
		IMemento[] children = memento
				.getChildren(IDockingManagerConsts.TAG_PAGE);
		if (children != null) {
			// Loop through the page elements.
			for (int i = 0; i < children.length; i++) {
				// Get the info details.
				IMemento childMem = children[i];
				String partID = childMem
						.getString(IDockingManagerConsts.TAG_CONTENT);
				String tabText = childMem
						.getString(IDockingManagerConsts.TAG_LABEL);

				// IViewDescriptor descriptor =
				// (IViewDescriptor)WorkbenchPlugin.getDefault().
				// getViewRegistry().find(partID);
				// if(descriptor != null)
				// tabText = descriptor.getLabel();

				// Create the part.
				// TODO: LayoutPart or ILayoutPart??
				ILayoutPart part = new PartPlaceholder(partID);
				add(tabText, i, part,true,I18n.getGenericI18n());
				// 1FUN70C: ITPUI:WIN - Shouldn't set Container when
				// not active
				part.setContainer(this);
				if (partID.equals(activeTabID)) {
					// Mark this as the active part.
					inactiveCurrent = part;
				}
			}
		}
	}

	/**
	 * @param memento
	 */
	public void saveState(IMemento memento) {
		// Save the active tab.
		if (current != null){
			memento.putString(IDockingManagerConsts.TAG_ACTIVE_PAGE_ID, current
					.getID( ));
		}

		if (mapTabToPart.size( ) == 0) {
			// Loop through the invisible children.
			if (invisibleChildren != null) {
				for (TabInfo info : invisibleChildren) {
					// Save the info.
					// Fields in TabInfo ..
					// private String tabText;
					// private ILayoutPart part;
					IMemento childMem = memento
							.createChild(IDockingManagerConsts.TAG_PAGE);
					childMem.putString(IDockingManagerConsts.TAG_LABEL,
							info.tabText);
					childMem.putString(IDockingManagerConsts.TAG_CONTENT,
							info.part.getID( ));
				}
			}
		}
		else {
			ILayoutPart[] children = getChildren( );
			CTabItem keys[] = new CTabItem[mapTabToPart.size( )];
			mapTabToPart.keySet( ).toArray(keys);
			if (children != null) {
				for (ILayoutPart part : children) {
					IMemento childMem = memento
							.createChild(IDockingManagerConsts.TAG_PAGE);
					childMem.putString(IDockingManagerConsts.TAG_CONTENT, part
							.getID( ));
					boolean found = false;
					for (CTabItem item : keys) {
						// for (int j = 0; j < keys.length; j++) {
						if (mapTabToPart.get(item) == part) {
							childMem.putString(IDockingManagerConsts.TAG_LABEL,
									item.getText( ));
							found = true;
							break;
						}
					}
					if (!found) {
						for (TabInfo info : invisibleChildren) {
							// for (int j = 0; j <
							// invisibleChildren.length; j++) {
							if (info.part == part) {
								childMem.putString(
										IDockingManagerConsts.TAG_LABEL,
										info.tabText);
								found = true;
								break;
							}
						}
					}
					if (!found) {
						childMem.putString(IDockingManagerConsts.TAG_LABEL,
								"LabelNotFound");//$NON-NLS-1$
					}
				}
			}
		}
	}

	/**
	 * Set the size of a page in the folder.<br>
	 */
	private void setControlSize() {
		if (current == null || tabFolder == null) {
			return;
		}
		Rectangle bounds;
		if (mapTabToPart.size( ) > 1) {
			bounds = calculatePageBounds(tabFolder);
		}
		else {
			bounds = tabFolder.getBounds( );
		}
		current.setBounds(bounds);
		current.moveAbove(tabFolder);
	}

	/**
	 * @param index
	 */
	public void setSelection(int index) {
		if (!active) {
			return;
		}

		if (mapTabToPart.size( ) == 0) {
			setSelection(null);
			return;
		}

		// make sure the index is in the right range
		if (index < 0) {
			index = 0;
		}
		if (index > mapTabToPart.size( ) - 1) {
			index = mapTabToPart.size( ) - 1;
		}
		tabFolder.setSelection(index);

		CTabItem item = tabFolder.getItem(index);
		ILayoutPart part = mapTabToPart.get(item);
		setSelection(part);
	}

	/**
	 * @param part
	 */
	private void setSelection(ILayoutPart part) {

		if (!active) {
			return;
		}
		if (part instanceof PartPlaceholder) {
			return;
		}

		// Deactivate old / Activate new.
		if (current != null && current != part) {
			current.setVisible(false);
		}
		current = part;
		if (current != null) {
			setControlSize( );
			current.setVisible(true);
		}

		/*
		 * Detached window no longer supported - remove when confirmed //
		 * set the title of the detached window to reflact the active
		 * tab Window window = getWindow(); if (window instanceof
		 * DetachedWindow) { if (current == null || !(current
		 * instanceof PartPane))
		 * window.getShell().setText("");//$NON-NLS-1$ else
		 * window.getShell().setText(((PartPane)current).getPart().getTitle()); }
		 */
	}

	/**
	 * @see LayoutPart#targetPartFor(de.xirp.ui.dock.ILayoutPart)
	 */
	@Override
	public ILayoutPart targetPartFor(ILayoutPart dragSource) {
		return this;
	}

	/* (non-Javadoc)
	 * @see de.xirp.ui.dock.ILayoutPart#getNameKey()
	 */
	@Override
	public String getNameKey() {
		return null;
	}

}
