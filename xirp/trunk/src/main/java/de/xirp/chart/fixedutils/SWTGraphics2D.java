/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2006, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ------------------
 * SWTGraphics2D.java
 * ------------------
 * (C) Copyright 2006, by Henry Proudhon and Contributors.
 *
 * Original Author:  Henry Proudhon (henry.proudhon AT insa-lyon.fr);
 * Contributor(s):   Matthias Gernand (matthias.gernand AT gmx.de);
 *                   Rabea Gransberger (RGransberger AT web.de);
 *
 * Changes
 * -------
 * 14-Jun-2006 : New class (HP);
 * 29-Jan-2007 : fixed the fillRect method (HP);
 * 31-Jan-2007 : moved the dummy JPanel to SWTUtils.java,
 *               implemented the drawLine method (HP);
 *               
 * 28-Mar-2007 : Fixed memory leak caused by undisposed Fonts,
 * 				 Added constructor to determine if the GC must be disposed,
 * 				 The SWTGraphics2D object now _must_ be disposed;   
 * 
 * 02-Apr-2007 : Closed memory leak, due to SWT resources,
 * 				 corrected comments on the GC, it is not a composite, it is a
 * 				 graphics context; 
 * 24-Apr-2007 : Fixed possible crash due to NPE in toSwtPath;
 */
package de.xirp.chart.fixedutils;

import java.awt.*;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.graphics.Transform;

/**
 * This is a class utility to draw Graphics2D stuff on a swt
 * composite. It is presently developed to use JFreeChart with the
 * Standard Widget Toolkit but may be of a wider use later. You may
 * dispose this object in any case to free SWT ressources and avoid
 * memory leaks!
 */
public class SWTGraphics2D extends Graphics2D {

	private static final Logger logClass = Logger.getLogger(SWTGraphics2D.class);
	/** The swt graphic context */
	private GC gc;
	/** A ArrayList holding the resourses to dispose */
	private java.util.List<Resource> resourcesToDispose = new ArrayList<Resource>( );
	/** Flag indicating if the used GC is on created using new GC() */
	private boolean isANewGC;

	private static final String LINE_SEPARATOR = System.getProperty("line.separator",
			"\n");

	/**
	 * Creates a new instance.
	 * 
	 * @param gc
	 *            the graphics context.
	 */
	private SWTGraphics2D(GC gc) {
		super( );
		this.gc = gc;
	}

	/**
	 * Creates a new instance.
	 * 
	 * @param gc
	 *            the graphics context.
	 */
	public SWTGraphics2D(GC gc, boolean isANewGC) {
		this(gc);
		this.isANewGC = isANewGC;
	}

	/**
	 * Perform a switch between foreground and background color of gc.
	 * This is needed for consistency with the awt behaviour, and is
	 * required notably for the filling methods.
	 */
	private void switchColors() {
		org.eclipse.swt.graphics.Color bg = gc.getBackground( );
		gc.setBackground(gc.getForeground( ));
		gc.setForeground(bg);
	}

	/**
	 * Converts an AWT <code>Shape</code> into a SWT
	 * <code>Path</code>.
	 * 
	 * @param shape
	 *            the shape.
	 * @return The path.
	 */
	private Path toSwtPath(Shape shape) {
		if (shape == null) {
			return null;
		}
		int type;
		float[] coords = new float[6];
		Path path = new Path(this.gc.getDevice( ));
		PathIterator pit = shape.getPathIterator(null);
		while (!pit.isDone( )) {
			type = pit.currentSegment(coords);
			switch (type) {
				case (PathIterator.SEG_MOVETO):
					path.moveTo(coords[0], coords[1]);
					break;
				case (PathIterator.SEG_LINETO):
					path.lineTo(coords[0], coords[1]);
					break;
				case (PathIterator.SEG_QUADTO):
					path.quadTo(coords[0], coords[1], coords[2], coords[3]);
					break;
				case (PathIterator.SEG_CUBICTO):
					path.cubicTo(coords[0],
							coords[1],
							coords[2],
							coords[3],
							coords[4],
							coords[5]);
					break;
				case (PathIterator.SEG_CLOSE):
					path.close( );
					break;
				default:
					break;
			}
			pit.next( );
		}
		resourcesToDispose.add(path);
		return path;
	}

	/**
	 * Converts an AWT transform into the equivalent SWT transform.
	 * 
	 * @param awtTransform
	 *            the AWT transform.
	 * @return The SWT transform.
	 */
	private Transform toSwtTransform(AffineTransform awtTransform) {
		Transform t = new Transform(gc.getDevice( ));
		double[] matrix = new double[6];
		awtTransform.getMatrix(matrix);
		t.setElements((float) matrix[0],
				(float) matrix[1],
				(float) matrix[2],
				(float) matrix[3],
				(float) matrix[4],
				(float) matrix[5]);
		resourcesToDispose.add(t);
		return t;
	}

	/**
	 * Converts an SWT transform into the equivalent AWT transform.
	 * 
	 * @param swtTransform
	 *            the SWT transform.
	 * @return The AWT transform.
	 */
	private AffineTransform toAwtTransform(Transform swtTransform) {
		float[] elements = new float[6];
		swtTransform.getElements(elements);
		AffineTransform awtTransform = new AffineTransform(elements);
		return awtTransform;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#draw(java.awt.Shape)
	 */
	@Override
	public void draw(Shape shape) {
		Path path = toSwtPath(shape);
		gc.drawPath(path);
		SWTUtils.secureDispose(path);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#drawImage(java.awt.Image,
	 *      java.awt.geom.AffineTransform,
	 *      java.awt.image.ImageObserver)
	 */
	@Override
	public boolean drawImage(Image image, AffineTransform xform,
			ImageObserver obs) {
		// TODO Auto-generated method stub
		logClass.trace("Not implemented yet: drawImage(Image image, AffineTransform xform, ImageObserver obs)"
				+ LINE_SEPARATOR);

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#drawImage(java.awt.image.BufferedImage,
	 *      java.awt.image.BufferedImageOp, int, int)
	 */
	@Override
	public void drawImage(BufferedImage image, BufferedImageOp op, int x, int y) {
		org.eclipse.swt.graphics.Image im = new org.eclipse.swt.graphics.Image(gc.getDevice( ),
				convertToSWT(image));
		gc.drawImage(im, x, y);
		SWTUtils.secureDispose(im);
	}

	/**
	 * Draws an image at (x, y).
	 * 
	 * @param image
	 *            the image.
	 * @param x
	 *            the x-coordinate.
	 * @param y
	 *            the y-coordinate.
	 */
	public void drawImage(org.eclipse.swt.graphics.Image image, int x, int y) {
		gc.drawImage(image, x, y);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#drawRenderedImage(java.awt.image.RenderedImage,
	 *      java.awt.geom.AffineTransform)
	 */
	@Override
	public void drawRenderedImage(RenderedImage image, AffineTransform xform) {
		// TODO Auto-generated method stub
		logClass.trace("Not implemented yet: drawRenderedImage"
				+ LINE_SEPARATOR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#drawRenderableImage(
	 *      java.awt.image.renderable.RenderableImage,
	 *      java.awt.geom.AffineTransform)
	 */
	@Override
	public void drawRenderableImage(RenderableImage image, AffineTransform xform) {
		// TODO Auto-generated method stub
		logClass.trace("Not implemented yet: drawRenderableImage"
				+ LINE_SEPARATOR);

	}

	/**
	 * Draws a string on the receiver. note that to be consistent with
	 * the awt method, the y has to be modified with the ascent of the
	 * font.
	 * 
	 * @see java.awt.Graphics#drawString(java.lang.String, int, int)
	 */
	@Override
	public void drawString(String text, int x, int y) {
		float fm = gc.getFontMetrics( ).getAscent( );
		gc.drawString(text, x, (int) (y - fm), true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#drawString(java.lang.String, float,
	 *      float)
	 */
	@Override
	public void drawString(String text, float x, float y) {
		float fm = gc.getFontMetrics( ).getAscent( );
		gc.drawString(text, (int) x, (int) (y - fm), true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#drawString(
	 *      java.text.AttributedCharacterIterator, int, int)
	 */
	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		// TODO Auto-generated method stub
		logClass.trace("Not implemented yet: drawString(AttributedCharacterIterator iterator, int x, int y)"
				+ LINE_SEPARATOR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#drawString(
	 *      java.text.AttributedCharacterIterator, float, float)
	 */
	@Override
	public void drawString(AttributedCharacterIterator iterator, float x,
			float y) {
		// TODO Auto-generated method stub
		logClass.trace("Not implemented yet: drawString(AttributedCharacterIterator iterator, float x, float y)"
				+ LINE_SEPARATOR);

	}

	/**
	 * fill an arbitrary shape on the swt graphic composite with the
	 * current stroke and paint. note that for consistency with the
	 * awt method, it is needed to switch temporarily the foreground
	 * and background colors.
	 * 
	 * @see java.awt.Graphics2D#fill(java.awt.Shape)
	 */
	@Override
	public void fill(Shape shape) {
		Path path = toSwtPath(shape);
		switchColors( );
		this.gc.fillPath(path);
		switchColors( );
		SWTUtils.secureDispose(path);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#hit(java.awt.Rectangle,
	 *      java.awt.Shape, boolean)
	 */
	@Override
	public boolean hit(Rectangle rect, Shape text, boolean onStroke) {
		// TODO Auto-generated method stub
		logClass.trace("Not implemented yet: hit" + LINE_SEPARATOR);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#getDeviceConfiguration()
	 */
	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		// TODO Auto-generated method stub
		logClass.trace("Not implemented yet: getDeviceConfiguration"
				+ LINE_SEPARATOR);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#setComposite(java.awt.Composite)
	 */
	@Override
	public void setComposite(Composite comp) {
		// TODO Auto-generated method stub
		logClass.trace("Not implemented yet: setComposite" + LINE_SEPARATOR);
	}

	/**
	 * Set the paint associated with the swt graphic composite.
	 * 
	 * @see java.awt.Graphics2D#setPaint(java.awt.Paint)
	 */
	@Override
	public void setPaint(Paint paint) {
		if (paint instanceof Color) {
			setColor((Color) paint);
		}
		else {
			throw new RuntimeException("Can only handle 'Color' at present.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#setStroke(java.awt.Stroke)
	 */
	@Override
	public void setStroke(Stroke stroke) {
		if (stroke instanceof BasicStroke) {
			BasicStroke bs = (BasicStroke) stroke;
			// linewidth
			gc.setLineWidth((int) bs.getLineWidth( ));

			// line join
			switch (bs.getLineJoin( )) {
				case BasicStroke.JOIN_BEVEL:
					gc.setLineJoin(SWT.JOIN_BEVEL);
					break;
				case BasicStroke.JOIN_MITER:
					gc.setLineJoin(SWT.JOIN_MITER);
					break;
				case BasicStroke.JOIN_ROUND:
					gc.setLineJoin(SWT.JOIN_ROUND);
					break;
			}

			// line cap
			switch (bs.getEndCap( )) {
				case BasicStroke.CAP_BUTT:
					gc.setLineCap(SWT.CAP_FLAT);
					break;
				case BasicStroke.CAP_ROUND:
					gc.setLineCap(SWT.CAP_ROUND);
					break;
				case BasicStroke.CAP_SQUARE:
					gc.setLineCap(SWT.CAP_SQUARE);
					break;
			}

			// set the line style to solid by default
			gc.setLineStyle(SWT.LINE_SOLID);

			// apply dash style if any
			float[] dashes = bs.getDashArray( );
			if (dashes != null) {
				int[] swtDashes = new int[dashes.length];
				for (int i = 0; i < swtDashes.length; i++) {
					swtDashes[i] = (int) dashes[i];
				}
				gc.setLineDash(swtDashes);
			}
		}
		else {
			throw new RuntimeException("Can only handle 'Basic Stroke' at present.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#setRenderingHint(java.awt.RenderingHints.Key,
	 *      java.lang.Object)
	 */
	@Override
	public void setRenderingHint(Key hintKey, Object hintValue) {
		// TODO Auto-generated method stub
		logClass.trace("Not implemented yet: setRenderingHint" + LINE_SEPARATOR);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#getRenderingHint(java.awt.RenderingHints.Key)
	 */
	@Override
	public Object getRenderingHint(Key hintKey) {
		// TODO Auto-generated method stub
		logClass.trace("Not implemented yet: getRenderingHint" + LINE_SEPARATOR);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#setRenderingHints(java.util.Map)
	 */
	@Override
	public void setRenderingHints(Map hints) {
		// TODO Auto-generated method stub
		logClass.trace("Not implemented yet: setRenderingHints"
				+ LINE_SEPARATOR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#addRenderingHints(java.util.Map)
	 */
	@Override
	public void addRenderingHints(Map hints) {
		// TODO Auto-generated method stub
		logClass.trace("Not implemented yet: addRenderingHints"
				+ LINE_SEPARATOR);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#getRenderingHints()
	 */
	@Override
	public RenderingHints getRenderingHints() {
		// TODO Auto-generated method stub
		logClass.trace("Not implemented yet: getRenderingHints"
				+ LINE_SEPARATOR);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#translate(int, int)
	 */
	@Override
	public void translate(int x, int y) {
		Transform swtTransform = new Transform(gc.getDevice( ));
		gc.getTransform(swtTransform);
		swtTransform.translate(x, y);
		gc.setTransform(swtTransform);
		SWTUtils.secureDispose(swtTransform);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#translate(double, double)
	 */
	@Override
	public void translate(double tx, double ty) {
		translate((int) tx, (int) ty);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#rotate(double)
	 */
	@Override
	public void rotate(double theta) {
		Transform swtTransform = new Transform(gc.getDevice( ));
		gc.getTransform(swtTransform);
		swtTransform.rotate((float) Math.toDegrees(theta));
		gc.setTransform(swtTransform);
		SWTUtils.secureDispose(swtTransform);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#rotate(double, double, double)
	 */
	@Override
	public void rotate(double theta, double x, double y) {
		// TODO Auto-generated method stub
		logClass.trace("Not implemented yet: rotate(double theta, double x, double y)"
				+ LINE_SEPARATOR);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#scale(double, double)
	 */
	@Override
	public void scale(double scaleX, double scaleY) {
		Transform swtTransform = new Transform(gc.getDevice( ));
		gc.getTransform(swtTransform);
		swtTransform.scale((float) scaleX, (float) scaleY);
		gc.setTransform(swtTransform);
		SWTUtils.secureDispose(swtTransform);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#shear(double, double)
	 */
	@Override
	public void shear(double shearX, double shearY) {
		Transform swtTransform = new Transform(gc.getDevice( ));
		gc.getTransform(swtTransform);
		Transform shear = new Transform(gc.getDevice( ),
				1f,
				(float) shearX,
				(float) shearY,
				1f,
				0,
				0);
		swtTransform.multiply(shear);
		gc.setTransform(swtTransform);
		SWTUtils.secureDispose(swtTransform);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#transform(java.awt.geom.AffineTransform)
	 */
	@Override
	public void transform(AffineTransform Tx) {
		Transform swtTransform = new Transform(gc.getDevice( ));
		gc.getTransform(swtTransform);
		swtTransform.multiply(toSwtTransform(Tx));
		gc.setTransform(swtTransform);
		SWTUtils.secureDispose(swtTransform);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#setTransform(java.awt.geom.AffineTransform)
	 */
	@Override
	public void setTransform(AffineTransform Tx) {
		gc.setTransform(toSwtTransform(Tx));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#getTransform()
	 */
	@Override
	public AffineTransform getTransform() {
		Transform swtTransform = new Transform(gc.getDevice( ));
		gc.getTransform(swtTransform);
		resourcesToDispose.add(swtTransform);
		return toAwtTransform(swtTransform);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#getPaint()
	 */
	@Override
	public Paint getPaint() {
		return SWTUtils.toAwtColor(gc.getForeground( ));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#getComposite()
	 */
	@Override
	public Composite getComposite() {
		// TODO Auto-generated method stub
		logClass.trace("Not implemented yet: getComposite" + LINE_SEPARATOR);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#setBackground(java.awt.Color)
	 */
	@Override
	public void setBackground(Color color) {
		// TODO do we need this? gc.getBackground().dispose();
		gc.setBackground(SWTUtils.toSwtColor(gc.getDevice( ), color));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#getBackground()
	 */
	@Override
	public Color getBackground() {
		return SWTUtils.toAwtColor(gc.getBackground( ));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#getStroke()
	 */
	@Override
	public Stroke getStroke() {
		BasicStroke bs;
		try {
			bs = new BasicStroke(gc.getLineWidth( ),
					gc.getLineCap( ),
					gc.getLineJoin( ));
		}
		catch (IllegalArgumentException e) {
			bs = new BasicStroke( );
		}
		return bs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#clip(java.awt.Shape)
	 */
	@Override
	public void clip(Shape s) {
		Path path = toSwtPath(s);
		gc.setClipping(path);
		SWTUtils.secureDispose(path);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#getFontRenderContext()
	 */
	@Override
	public FontRenderContext getFontRenderContext() {
		FontRenderContext fontRenderContext = new FontRenderContext(new AffineTransform( ),
				true,
				true);
		return fontRenderContext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#drawGlyphVector(java.awt.font.GlyphVector,
	 *      float, float)
	 */
	@Override
	public void drawGlyphVector(GlyphVector g, float x, float y) {
		// TODO Auto-generated method stub
		logClass.trace("Not implemented yet: drawGlyphVector" + LINE_SEPARATOR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics#create()
	 */
	@Override
	public Graphics create() {
		// TODO Auto-generated method stub
		logClass.trace("Not implemented yet: create" + LINE_SEPARATOR);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics#getColor()
	 */
	@Override
	public Color getColor() {
		return SWTUtils.toAwtColor(gc.getForeground( ));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics#setColor(java.awt.Color)
	 */
	@Override
	public void setColor(Color color) {
		// TODO do we need this? gc.getForeground().dispose();
		gc.setForeground(SWTUtils.toSwtColor(gc.getDevice( ), color));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics#setPaintMode()
	 */
	@Override
	public void setPaintMode() {
		// TODO Auto-generated method stub
		logClass.trace("Not implemented yet: setPaintMode" + LINE_SEPARATOR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics#setXORMode(java.awt.Color)
	 */
	@Override
	public void setXORMode(Color color) {
		// TODO Auto-generated method stub
		logClass.trace("Not implemented yet: setXORMode" + LINE_SEPARATOR);
	}

	/**
	 * Returns the font in form of an awt font created with the
	 * parameters of the font of the swt graphic composite.
	 * 
	 * @see java.awt.Graphics#getFont()
	 */
	@Override
	public Font getFont() {
		// retrieve the swt font description in an os indept way
		FontData[] fontData = gc.getFont( ).getFontData( );
		// create a new awt font with the appropiate data
		return SWTUtils.toAwtFont(gc.getDevice( ), fontData[0], true);
	}

	/**
	 * Set the font swt graphic composite from the specified awt font.
	 * Be careful that the newly created swt font must be disposed
	 * separately.
	 * 
	 * @see java.awt.Graphics#setFont(java.awt.Font)
	 */
	@Override
	public void setFont(Font font) {
		org.eclipse.swt.graphics.Font swtFont = new org.eclipse.swt.graphics.Font(gc.getDevice( ),
				SWTUtils.toSwtFontData(gc.getDevice( ), font, true));
		resourcesToDispose.add(swtFont);
		gc.setFont(swtFont);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics#getFontMetrics(java.awt.Font)
	 */
	@Override
	public FontMetrics getFontMetrics(Font font) {
		return SWTUtils.DUMMY_PANEL.getFontMetrics(font);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics#getClipBounds()
	 */
	@Override
	public Rectangle getClipBounds() {
		org.eclipse.swt.graphics.Rectangle clip = gc.getClipping( );
		return new Rectangle(clip.x, clip.y, clip.width, clip.height);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics#clipRect(int, int, int, int)
	 */
	@Override
	public void clipRect(int x, int y, int width, int height) {
		org.eclipse.swt.graphics.Rectangle clip = gc.getClipping( );
		clip.intersects(x, y, width, height);
		gc.setClipping(clip);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics#setClip(int, int, int, int)
	 */
	@Override
	public void setClip(int x, int y, int width, int height) {
		gc.setClipping(x, y, width, height);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics#getClip()
	 */
	@Override
	public Shape getClip() {
		// TODO Auto-generated method stub
		logClass.trace("Not implemented yet: getClip" + LINE_SEPARATOR);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics#setClip(java.awt.Shape)
	 */
	@Override
	public void setClip(Shape clip) {
		if (clip == null) {
			return;
		}
		Path clipPath = toSwtPath(clip);
		gc.setClipping(clipPath);
		SWTUtils.secureDispose(clipPath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics#copyArea(int, int, int, int, int, int)
	 */
	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		// TODO Auto-generated method stub
		logClass.trace("Not implemented yet: copyArea" + LINE_SEPARATOR);
	}

	/**
	 * Draws a line on the swt graphic composite.
	 * 
	 * @see java.awt.Graphics#drawLine(int, int, int, int)
	 */
	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		gc.drawLine(x1, y1, x2, y2);
	}

	/**
	 * Fill a rectangle area on the swt graphic composite. The
	 * <code>fillRectangle</code> method of the <code>GC</code>
	 * class uses the background color so we must switch colors.
	 * 
	 * @see java.awt.Graphics#fillRect(int, int, int, int)
	 */
	@Override
	public void fillRect(int x, int y, int width, int height) {
		this.switchColors( );
		gc.fillRectangle(x, y, width, height);
		this.switchColors( );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics#clearRect(int, int, int, int)
	 */
	@Override
	public void clearRect(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		logClass.trace("Not implemented yet: clearRect" + LINE_SEPARATOR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics#drawRoundRect(int, int, int, int, int,
	 *      int)
	 */
	@Override
	public void drawRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		// TODO Auto-generated method stub
		logClass.trace("Not tested yet: drawRoundRect" + LINE_SEPARATOR);
		gc.drawRoundRectangle(x, y, width, height, arcWidth, arcHeight);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics#fillRoundRect(int, int, int, int, int,
	 *      int)
	 */
	@Override
	public void fillRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		// TODO Auto-generated method stub
		logClass.trace("Not implemented yet: fillRoundRect" + LINE_SEPARATOR);
		switchColors( );
		gc.fillRoundRectangle(x, y, width, height, arcWidth, arcHeight);
		switchColors( );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics#drawOval(int, int, int, int)
	 */
	@Override
	public void drawOval(int x, int y, int width, int height) {
		gc.drawOval(x, y, width, height);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics#fillOval(int, int, int, int)
	 */
	@Override
	public void fillOval(int x, int y, int width, int height) {
		switchColors( );
		gc.fillOval(x, y, width, height);
		switchColors( );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics#drawArc(int, int, int, int, int, int)
	 */
	@Override
	public void drawArc(int x, int y, int width, int height, int arcStart,
			int arcAngle) {
		// TODO Auto-generated method stub
		logClass.trace("Not tested yet: drawArc" + LINE_SEPARATOR);
		gc.drawArc(x, y, width, height, arcStart, arcAngle);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics#fillArc(int, int, int, int, int, int)
	 */
	@Override
	public void fillArc(int x, int y, int width, int height, int arcStart,
			int arcAngle) {
		switchColors( );
		gc.fillArc(x, y, width, height, arcStart, arcAngle);
		switchColors( );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics#drawPolyline(int[], int[], int)
	 */
	@Override
	public void drawPolyline(int[] xPoints, int[] yPoints, int npoints) {
		// TODO Auto-generated method stub
		logClass.trace("Not tested yet: drawPolyline" + LINE_SEPARATOR);
		gc.drawPolyline(toSingleArray(xPoints, yPoints));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics#drawPolygon(int[], int[], int)
	 */
	@Override
	public void drawPolygon(int[] xPoints, int[] yPoints, int npoints) {
		// TODO Auto-generated method stub
		logClass.trace("Not test yet: drawPolygon" + LINE_SEPARATOR);
		gc.drawPolygon(toSingleArray(xPoints, yPoints));
	}

	private static int[] toSingleArray(int[] xPoints, int[] yPoints) {
		int arr[] = new int[xPoints.length + yPoints.length];
		for (int i = 0, x = 0; i < xPoints.length && i < yPoints.length; i++, x += 2) {
			arr[x] = xPoints[i];
			arr[x + 1] = yPoints[i];
		}
		return arr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics#fillPolygon(int[], int[], int)
	 */
	@Override
	public void fillPolygon(int[] xPoints, int[] yPoints, int npoints) {
		switchColors( );

		gc.fillPolygon(toSingleArray(xPoints, yPoints));

		switchColors( );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics#drawImage(java.awt.Image, int, int,
	 *      java.awt.image.ImageObserver)
	 */
	@Override
	public boolean drawImage(Image image, int x, int y, ImageObserver observer) {
		// TODO Auto-generated method stub
		logClass.trace("Not implemented yet: drawImage(Image image, int x, int y, ImageObserver observer)"
				+ LINE_SEPARATOR);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, int,
	 *      int, java.awt.image.ImageObserver)
	 */
	@Override
	public boolean drawImage(Image image, int x, int y, int width, int height,
			ImageObserver observer) {
		// TODO Auto-generated method stub
		logClass.trace("Not implemented yet: (Image image, int x, int y, int width, int height,ImageObserver observer)"
				+ LINE_SEPARATOR);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics#drawImage(java.awt.Image, int, int,
	 *      java.awt.Color, java.awt.image.ImageObserver)
	 */
	@Override
	public boolean drawImage(Image image, int x, int y, Color bgcolor,
			ImageObserver observer) {
		// TODO Auto-generated method stub
		logClass.trace("Not implemented yet: drawImage(Image image, int x, int y, Color bgcolor,ImageObserver observer)"
				+ LINE_SEPARATOR);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, int,
	 *      int, java.awt.Color, java.awt.image.ImageObserver)
	 */
	@Override
	public boolean drawImage(Image image, int x, int y, int width, int height,
			Color bgcolor, ImageObserver observer) {
		// TODO Auto-generated method stub
		logClass.trace("Not implemented yet: drawImage(Image image, int x, int y, int width, int height,"
				+ "Color bgcolor, ImageObserver observer)" + LINE_SEPARATOR);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, int,
	 *      int, int, int, int, int, java.awt.image.ImageObserver)
	 */
	@Override
	public boolean drawImage(Image image, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		// TODO Auto-generated method stub
		logClass.trace("Not implemented yet: drawImage(Image image, int dx1, int dy1, int dx2, int dy2,"
				+ "int sx1, int sy1, int sx2, int sy2, ImageObserver observer)"
				+ LINE_SEPARATOR);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, int,
	 *      int, int, int, int, int, java.awt.Color,
	 *      java.awt.image.ImageObserver)
	 */
	@Override
	public boolean drawImage(Image image, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, Color bgcolor,
			ImageObserver observer) {
		// TODO Auto-generated method stub
		logClass.trace("Not implemented yet: drawImage(Image image, int dx1, int dy1, int dx2, int dy2,"
				+ "int sx1, int sy1, int sx2, int sy2, Color bgcolor,"
				+ "ImageObserver observer)" + LINE_SEPARATOR);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Graphics#dispose()
	 */
	@Override
	public void dispose() {
		if (SWTUtils.secureDispose(gc) && isANewGC) {
			gc.dispose( );
		}
		for (Resource res : resourcesToDispose) {
			SWTUtils.secureDispose(res);
			/*
			 * Comment this in to see how many resources have never
			 * been disposed before. Watch the memory usage when not
			 * disposing!
			 */
			// System.err.println("DISPOSED!!!!!!!!!!");
		}
	}

	private static ImageData convertToSWT(BufferedImage bufferedImage) {
		if (bufferedImage.getColorModel( ) instanceof DirectColorModel) {
			DirectColorModel colorModel = (DirectColorModel) bufferedImage.getColorModel( );
			PaletteData palette = new PaletteData(colorModel.getRedMask( ),
					colorModel.getGreenMask( ),
					colorModel.getBlueMask( ));
			ImageData data = new ImageData(bufferedImage.getWidth( ),
					bufferedImage.getHeight( ),
					colorModel.getPixelSize( ),
					palette);
			WritableRaster raster = bufferedImage.getRaster( );
			int[] pixelArray = new int[3];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					raster.getPixel(x, y, pixelArray);
					int pixel = palette.getPixel(new RGB(pixelArray[0],
							pixelArray[1],
							pixelArray[2]));
					data.setPixel(x, y, pixel);
				}
			}
			return data;
		}
		else if (bufferedImage.getColorModel( ) instanceof IndexColorModel) {
			IndexColorModel colorModel = (IndexColorModel) bufferedImage.getColorModel( );
			int size = colorModel.getMapSize( );
			byte[] reds = new byte[size];
			byte[] greens = new byte[size];
			byte[] blues = new byte[size];
			colorModel.getReds(reds);
			colorModel.getGreens(greens);
			colorModel.getBlues(blues);
			RGB[] rgbs = new RGB[size];
			for (int i = 0; i < rgbs.length; i++) {
				rgbs[i] = new RGB(reds[i] & 0xFF,
						greens[i] & 0xFF,
						blues[i] & 0xFF);
			}
			PaletteData palette = new PaletteData(rgbs);
			ImageData data = new ImageData(bufferedImage.getWidth( ),
					bufferedImage.getHeight( ),
					colorModel.getPixelSize( ),
					palette);
			data.transparentPixel = colorModel.getTransparentPixel( );
			WritableRaster raster = bufferedImage.getRaster( );
			int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					raster.getPixel(x, y, pixelArray);
					data.setPixel(x, y, pixelArray[0]);
				}
			}
			return data;
		}
		return null;
	}
}
