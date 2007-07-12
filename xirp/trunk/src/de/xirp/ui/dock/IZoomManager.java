package de.xirp.ui.dock;

/**
 * 
 */
interface IZoomManager {

	/**
	 * @param pane
	 * @return boolean
	 */
	public boolean partChangeAffectsZoom(ILayoutPart pane);

	/**
	 * @return boolean
	 */
	public boolean isZoomed();

	/**
	 * @param pane
	 */
	public void zoomIn(ILayoutPart pane);

	/**
	 * 
	 */
	public void zoomOut();
}
