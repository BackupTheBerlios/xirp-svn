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
 * Raw.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 21.03.2007:		Created by Matthias Gernand.
 */
package de.xirp.db;

import javax.persistence.*;

import de.xirp.util.FutureRelease;

/**
 * This bean represents a raw recording of a value/package/object
 * sent to and received from a robot. It is used to record the whole
 * communication between the application and a robot and persist it
 * to a database. 
 * <br><br>
 * The class is annotated with annotations for persisting Java beans.
 * This class is persisted to a database using Hibernate.
 * <br><br>
 * This is alpha API, may be removed in the future or
 * is planed to be integrated in version 3.0.0.
 * 
 * @author Matthias Gernand
 * 
 * @see javax.persistence
 */
@Entity
@Table(name = "RAW_VALUES")//$NON-NLS-1$
@FutureRelease(version = "3.0.0")
public final class Raw {

	/**
	 * The id of the entry in the database, generated automatically.
	 */
	private long id;
	/**
	 * The raw sent or received data.
	 */
	private Object rawData;
	/**
	 * The timestamp when the value was recorded.
	 */
	private long timestamp;
	/**
	 * The record under which this {@link de.xirp.db.Raw} was created.
	 */
	private Record record;

	/**
	 * Constructs a new default {@link de.xirp.db.Raw} object. Must be
	 * declared public because Hibernate needs this.
	 */
	public Raw() {

	}

	/**
	 * Constructs a new {@link de.xirp.db.Raw} object using the given
	 * timestamp and raw date object.
	 * 
	 * @param timestamp
	 * 		The timestamp when the recording was done.
	 * @param rawData
	 * 		The recored raw data.
	 */
	public Raw(long timestamp, Object rawData) {
		this.timestamp = timestamp;
		this.rawData = rawData;
	}

	/**
	 * Returns the id of the raw recording.
	 * 
	 * @return The id.
	 */
	@Id
	@GeneratedValue
	@Column(name = "RAW_VALUE_ID")//$NON-NLS-1$
	public long getId() {
		return id;
	}

	/**
	 * Returns the recored raw data object.
	 * 
	 * @return the raw data object.
	 */
	@Lob
	@Column(name = "RAW_DATA")//$NON-NLS-1$
	public Object getRawData() {
		return rawData;
	}

	/**
	 * Returns the timestamp.
	 * 
	 * @return The timestamp
	 */
	@Column(name = "RAW_TIMESTAMP")//$NON-NLS-1$
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Sets the id of the raw recording. This <b>must</b> be private
	 * because Hibernate automatically sets the id when persisting
	 * the bean to the database. This method is then called by
	 * reflection.
	 * 
	 * @param id 
	 * 			The id to set.
	 */
	@SuppressWarnings("unused")//$NON-NLS-1$
	private void setId(long id) {
		this.id = id;
	}

	/**
	 * Sets the raw data object.
	 * 
	 * @param rawData 
	 * 			The raw data to set.
	 */
	public void setRawData(Object rawData) {
		this.rawData = rawData;
	}

	/**
	 * Sets the timestamp.
	 * 
	 * @param timestamp 
	 * 			The timestamp to set.
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Returns the assocciated record.
	 * 
	 * @return The assocciated record.
	 */
	@ManyToOne
	@JoinColumn(name = "RECORD_ID")//$NON-NLS-1$
	public Record getRecord() {
		return record;
	}

	/**
	 * Sets the assocciation beween raw recording and 
	 * {@link de.xirp.db.Record}.
	 * 
	 * @param record 
	 * 			The record to assocciation with the raw recording.
	 */
	@SuppressWarnings("unused")//$NON-NLS-1$
	protected void setRecord(Record record) {
		this.record = record;
	}
}
