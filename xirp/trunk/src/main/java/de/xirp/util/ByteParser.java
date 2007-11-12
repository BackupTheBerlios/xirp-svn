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
 * ByteParser.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 31.03.2006:		Created by Rabea Gransberger.
 */
package de.xirp.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Parser for reading data out of a byte array.
 * 
 * @author Rabea Gransberger
 */
public class ByteParser {

	/**
	 * The Logger of this class
	 */
	// private static Logger log = Logger.getLogger(ByteParser.class);
	/**
	 * The byte buffer used for parsing
	 */
	private ByteBuffer buffer;

	/**
	 * Constructs a new Parser for the given data.
	 * 
	 * @param data
	 *            Received Data to parse
	 */
	public ByteParser(byte[] data) {
		buffer = ByteBuffer.wrap(data);
	}

	/**
	 * Construct a new parser for the given data and byte order.
	 * 
	 * @param data
	 *            Received Data to parse
	 * @param order
	 *            the byte order (little or big endian)
	 */
	protected ByteParser(byte[] data, ByteOrder order) {
		buffer = ByteBuffer.wrap(data).order(order);
	}

	/**
	 * Reads the next short from the data.
	 * 
	 * @return Read short
	 */
	public short getNextShort() {
		return buffer.getShort( );
	}

	/**
	 * Reads the next int from the data.
	 * 
	 * @return Read int
	 */
	public int getNextInt() {
		return buffer.getInt( );
	}

	/**
	 * Reads the next float from the data.
	 * 
	 * @return Read float
	 */
	public float getNextFloat() {
		return buffer.getFloat( );
	}

	/**
	 * Reads the next double from the data.
	 * 
	 * @return Read double
	 */
	public double getNextDouble() {
		return buffer.getDouble( );
	}

	/**
	 * Reads the next long from the data.
	 * 
	 * @return Read long
	 */
	public long getNextLong() {
		if(buffer.remaining() >= Long.SIZE/Byte.SIZE){
			return buffer.getLong( );
		}
		else {
			return -1;
		}
	}

	/**
	 * Reads the next byte from the data.
	 * 
	 * @return read byte
	 */
	public byte getNextByte() {
		return buffer.get( );
	}

	/**
	 * Reads the next unsigned byte from the data. Note that short is
	 * returned because byte is unable to contain values greater 127.
	 * 
	 * @return read byte
	 */
	public short getNextUnsignedByte() {
		short b = getNextByte( );
		return (short) (b & 0xff);
	}

	/**
	 * Reads the next string from the data.
	 * 
	 * @param length
	 *            length of the String to read
	 * @return Read string without terminating \0
	 */
	public String getNextString(int length) {
		int newLength = length;
		byte[] data = buffer.array( );
		int offset = buffer.position( );
		if (data[offset + length - 1] == 0) {
			newLength -= 1;
		}

		String strg = Conversion.byteArrayToString(getSubArray(data,
				offset,
				newLength));
		setOffset(offset + length);
		return strg;
	}

	/**
	 * Reads the next string from the data.
	 * 
	 * @return Read string without terminating \0
	 */
	public String getNextString() {
		// using fast direct call instead of
		// getNextString(length)
		return getNextString(getRemainingLength( ));
	}

	/**
	 * Gets the whole String representation of the data no matter what
	 * has already been read.
	 * 
	 * @return String representation of data
	 */
	public String getString() {
		return Conversion.debugString(buffer.array( ));
	}

	/**
	 * Returns the remaining length of the data to read.
	 * 
	 * @return remaining length that is the length of the whole array
	 *         minus the actual reading position
	 */
	public int getRemainingLength() {
		return buffer.remaining( );
	}

	/**
	 * Is there more data to read?
	 * 
	 * @return <code>true</code> more data available
	 */
	public boolean hasNext() {
		return buffer.hasRemaining( );
	}

	/**
	 * Resets the offset for reading data.<br>
	 * Next call to getNextX will return start of data.
	 */
	public void reset() {
		buffer.rewind( );
	}

	/**
	 * Gets the remaining unread data from the array.
	 * 
	 * @return Byte array with unread data
	 */
	public byte[] getRemainingData() {
		// not using sub array because direct call should
		// be faster buffer.
		return buffer.slice( ).array( );
	}

	/**
	 * Gets a sub-array of the data.
	 * 
	 * @param data
	 *            the byte array for which a sub array should be get
	 * @param start
	 *            Starting position in the array
	 * @param length
	 *            Length of the returned array
	 * @return byte array as sub-array of the data
	 * @throws ArrayIndexOutOfBoundsException
	 *             Thrown when the data does not contain the bytes
	 *             requested
	 */
	private byte[] getSubArray(byte[] data, int start, int length)
			throws ArrayIndexOutOfBoundsException {
		// int end = start + length;

		byte[] newArray = new byte[length];
		// for (int i = start, cnt = 0; i < start + length; i++) {
		// newArray[cnt++] = data[i];
		// }
		System.arraycopy(data, start, newArray, 0, length);
		return newArray;
	}

	/**
	 * Gets part of the array on which this parser works.
	 * 
	 * @param start
	 *            start position from which data should be returned
	 * @param length
	 *            the length of the data which should be returned.
	 * @return byte array with the copied data
	 * @throws ArrayIndexOutOfBoundsException
	 *             Thrown when the data does not contain the bytes
	 *             requested
	 */
	public byte[] getSubArray(int start, int length)
			throws ArrayIndexOutOfBoundsException {
		return getSubArray(buffer.array( ), start, length);
	}

	/**
	 * Returns the whole data of this parser.
	 * 
	 * @return Byte array with data of this frame
	 */
	public byte[] getData() {
		return buffer.array( );
	}

	/**
	 * Sets the offset, from which on the data of the original array
	 * is read.
	 * 
	 * @param newOffset
	 *            the offset to set
	 */
	public void setOffset(int newOffset) {
		this.buffer.position(newOffset);
	}

	/**
	 * Gets the offset from which on the data of the original array is
	 * read.
	 * 
	 * @return the current offset
	 */
	public int getOffset() {
		return buffer.position( );
	}

}
