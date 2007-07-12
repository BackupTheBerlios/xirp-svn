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
 * Observed.java
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

/**
 * This bean holds informations about a observed key/value pair.
 * The key is one of the available sensor datapool keys. The value is
 * the observed value at a timestamp. The observed key/value pair is
 * associated with a {@link de.xirp.db.Record} under
 * which it was created.
 * <br><br>
 * The class is annotated with annotations for persisting Java beans.
 * This class is persisted to a database using Hibernate.
 * 
 * @author Matthias Gernand
 *
 * @see javax.persistence
 */
@Entity
@Table(name = "OBSERVED_VALUES")//$NON-NLS-1$
public final class Observed {

	/**
	 * The id of the entry in the database, generated automatically.
	 */
	private long id;
	/**
	 * The name of the key.
	 */
	private String observedKey;
	/**
	 * The recorded value.
	 */
	private Double value;
	/**
	 * The timestamp when the value was recorded.
	 */
	private long timestamp;
	/**
	 * The record under which this observation was done.
	 */
	private Record record;

	/**
	 * Constructs a new default {@link de.xirp.db.Observed} 
	 * object. Must be declared public because Hibernate needs this.
	 */
	public Observed() {

	}

	/**
	 * Constructs a new {@link de.xirp.db.Observed} object 
	 * from the given timestamp, key name and value.
	 * 
	 * @param timestamp
	 * 			The timestamp when the recording was done.
	 * @param observedKey
	 * 			The name of the key to observe.
	 * @param value
	 * 			The recorded value.
	 */
	public Observed(long timestamp, String observedKey, Double value) {
		this.timestamp = timestamp;
		this.observedKey = observedKey;
		this.value = value;
	}

	/**
	 * Returns the name of the key.
	 * 
	 * @return The name of the key.
	 * 			
	 */
	@Column(name = "OBSERVED_KEY")//$NON-NLS-1$
	public String getObservedKey() {
		return observedKey;
	}

	/**
	 * Returns the id of the observation.
	 * 
	 * @return The id.
	 */
	@Id
	@GeneratedValue
	@Column(name = "OBSERVED_VALUE_ID")//$NON-NLS-1$
	public long getId() {
		return id;
	}

	/**
	 * Returns the timestamp when the recording was done.
	 * 
	 * @return The timestamp.
	 */
	@Column(name = "OBSERVED_TIMESTAMP")//$NON-NLS-1$
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Returns the recorded value.
	 * 
	 * @return The value.
	 */
	@Column(name = "OBSERVED_VALUE")//$NON-NLS-1$
	public Double getValue() {
		return value;
	}

	/**
	 * Sets the name of the key to observe.
	 * 
	 * @param observedKey 
	 * 			The name of the key.
	 */
	public void setObservedKey(String observedKey) {
		this.observedKey = observedKey;
	}

	/**
	 * Sets the id of the observation. This <b>must</b> be private
	 * because Hibernate automatically sets the id when persisting
	 * the bean to the datasbase. This method is then called by
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
	 * Sets the timestamp when the recording occured.
	 * 
	 * @param timestamp
	 * 			The timestamp to set.
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Sets the recorded value.
	 * 
	 * @param value
	 * 			The value to set.
	 */
	public void setValue(Double value) {
		this.value = value;
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
	 * Sets the assocciation beween observation and recording.
	 * 
	 * @param record 
	 * 			The record to assocciation with the observation.
	 */
	@SuppressWarnings("unused")//$NON-NLS-1$
	protected void setRecord(Record record) {
		this.record = record;
	}
}
