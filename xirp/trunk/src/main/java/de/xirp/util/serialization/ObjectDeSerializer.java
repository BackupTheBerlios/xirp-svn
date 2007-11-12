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
 * ObjectDeSerializer.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 18.02.2007:		Created by Matthias Gernand.
 */
package de.xirp.util.serialization;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;

import org.apache.log4j.Logger;

import de.xirp.util.Constants;
import de.xirp.util.I18n;

/**
 * This class may de-serialize an object of a specific type from a
 * file.
 * 
 * @author Matthias Gernand
 */
public class ObjectDeSerializer {

	/**
	 * The logger of this class
	 */
	private static final Logger logClass = Logger.getLogger(ObjectDeSerializer.class);

	/**
	 * Reads an object of the given type form the given file.<br>
	 * <br>
	 * Example:<br>
	 * <code>ObjectDeSerializer.<String>getObject(stringFile)</code>
	 * 
	 * @param <O>
	 *            the type of the object which should be read.
	 * @param objectFile
	 *            the file from which the object should be read
	 * @return the object or
	 *         <code>null<code> if the given file was <code>null</code>.
	 * @throws SerializationException if there's an read error from the given file
	 * @throws FileNotFoundException if the given file was not found
	 */
	public static <O extends Serializable> O getObject(File objectFile)
			throws SerializationException, FileNotFoundException {

		if (objectFile != null) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(objectFile);
				return ObjectDeSerializer.<O> getObject(fis);
			}
			catch (FileNotFoundException e) {
				throw new FileNotFoundException(I18n.getString("ObjectDeSerializer.exception.fileNotFound")); //$NON-NLS-1$
			}
			finally {
				if (fis != null) {
					try {
						fis.close( );
					}
					catch (IOException e) {
						logClass.error("Error: " + e.getMessage( )//$NON-NLS-1$
								+ Constants.LINE_SEPARATOR, e);
					}
				}
			}
		}
		return null;
	}

	/**
	 * Reads an object of the given type form the given file.<br>
	 * <br>
	 * Example:<br>
	 * <code>ObjectDeSerializer.<String>getObject(stringFile)</code>
	 * 
	 * @param <O>
	 *            the type of the object which should be read.
	 * @param input
	 *            the input stream the object should be read
	 * @return the object or
	 *         <code>null<code> if the given file was <code>null</code>.
	 * @throws SerializationException if there's an read error from the given file
	 */
	@SuppressWarnings("unchecked")
	public static <O extends Serializable> O getObject(InputStream input)
			throws SerializationException {

		if (input != null) {
			ObjectInputStream inStream = null;
			try {
				inStream = new ObjectInputStream(input);
				O o = (O) inStream.readObject( );
				return o;
			}
			catch (StreamCorruptedException e) {
				throw new SerializationException(I18n.getString("ObjectDeSerializer.exception.corruptDatastream")); //$NON-NLS-1$
			}
			catch (EOFException e) {
				throw new SerializationException(I18n.getString("ObjectDeSerializer.exception.unexpectedEOF")); //$NON-NLS-1$
			}
			catch (IOException e) {
				logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
						+ Constants.LINE_SEPARATOR, e);
			}
			catch (ClassNotFoundException e) {
				logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
						+ Constants.LINE_SEPARATOR, e);
			}
			finally {
				if (inStream != null) {
					try {
						inStream.close( );
					}
					catch (IOException e) {
						logClass.error("Error: " + e.getMessage( )//$NON-NLS-1$
								+ Constants.LINE_SEPARATOR, e);
					}
				}
			}
		}
		else {
			throw new SerializationException(I18n.getString("ObjectDeSerializer.exception.inputFileNull")); //$NON-NLS-1$
		}
		return null;
	}
}
