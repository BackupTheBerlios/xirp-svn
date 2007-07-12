package de.xirp.ui.dock;

import java.util.ArrayList;

import org.eclipse.ui.IMemento;

// / A DockingManager is analogous to a Layout, it can be attached to
// / a Composite. The DockingManager controls the layout of the
// Composite's
// / children.
// / Content: The swtdock Manager works with descendants of LayoutPart
// / objects for it's Content.
// / Events: PartOpened, PartClosed, PartActivated, PartDeactivated,
// / PartBroughtToTop
// /
// / Usage:
// / // create a swtdock Manager
// / DockingManager dm = new DockingManager(composite);
// /
// / // hook events
// / dm.partOpened += new XXXXHandler(hhhh);
// /
// / // add some parts
// / mypart = new MyPart();
// / dm.addPart(mypart);
// /
// / // activate a part
// / dm.activate(mypart);
// /
// / // close a part
// / dm.removePart(mypart);
// / mypart.dispose();
// /
/**
 * 
 */
interface IDockingManager {

	/**
	 * @param part
	 */
	public void addPart(ILayoutPart part);

	/**
	 * @param part
	 * @return boolean
	 */
	public boolean bringPartToTop(ILayoutPart part);

	/**
	 * 
	 */
	public void dispose();

	/**
	 * @param part
	 */
	public void openTracker(ILayoutPart part);

	/**
	 * @param result
	 */
	public void collectViewPanes(ArrayList<ILayoutPart> result);

	/**
	 * @return RootLayoutContainer
	 */
	public RootLayoutContainer getLayout();

	/**
	 * @param part
	 */
	public void removePart(ILayoutPart part);

	/**
	 * @param id
	 * @return ILayoutPart
	 */
	public ILayoutPart findPart(String id);

	/**
	 * @param part
	 */
	public void updateNames(ILayoutPart part);

	// public IStatus restoreState(IMemento memento);
	// public IStatus saveState(IMemento memento);
	/**
	 * @param memento
	 */
	public void restoreState(IMemento memento);

	/**
	 * @param memento
	 */
	public void saveState(IMemento memento);

}
