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
 * ObjectSerializer.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 12.02.2007:		Created by Matthias Gernand.
 */
package de.xirp.util.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * This class allows to serialize and deep copy an object of the given
 * type.
 * 
 * @author Matthias Gernand
 */
public final class ObjectSerializer {

	/**
	 * Creates a deep copy of the given object.
	 * 
	 * @param <O>
	 *            the type of the object
	 * @param obj
	 *            the object to copy
	 * @return the copied object
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static <O extends Serializable> O deepCopy(O obj) throws IOException {
		// create ObjectOutputStream
		ByteArrayOutputStream bufOutStream = new ByteArrayOutputStream( );
		ObjectOutputStream outStream = new ObjectOutputStream(bufOutStream);
		// save object to byte-Array
		outStream.writeObject(obj);
		byte[] bytes = bufOutStream.toByteArray( );
		try {
			outStream.close( );
		}
		catch (IOException e) {
			// nothing
		}
		try {
			bufOutStream.close( );
		}
		catch (IOException e) {
			// nothing
		}

		// create ObjectInputStream
		ByteArrayInputStream bufInStream = new ByteArrayInputStream(bytes);
		ObjectInputStream inStream = new ObjectInputStream(bufInStream);
		// read object back
		O deepCopy = null;
		try {
			deepCopy = (O) inStream.readObject( );
		}
		catch (ClassNotFoundException e1) {
			// will not occur because we know that the object exist
		}

		try {
			inStream.close( );
		}
		catch (IOException e) {
			// nothing
		}

		try {
			bufInStream.close( );
		}
		catch (IOException e) {
			// nothing
		}

		return deepCopy;
	}

	/**
	 * Writes the given object to the given file.
	 * 
	 * @param <O>
	 *            the type of the object which should be written
	 * @param obj
	 *            the object to write to a file
	 * @param writeTo
	 *            the file to write to
	 * @throws IOException
	 *             if an error occurs when writing the object
	 */
	public static <O extends Serializable> void writeToDisk(O obj, File writeTo)
			throws IOException {
		FileOutputStream fos = new FileOutputStream(writeTo);
		ObjectOutputStream outStream = new ObjectOutputStream(fos);
		outStream.writeObject(obj);
		try {
			outStream.close( );
		}
		catch (IOException e) {
			// nothing
		}
		try {
			fos.close( );
		}
		catch (IOException e) {
			// nothing
		}
	}
}
