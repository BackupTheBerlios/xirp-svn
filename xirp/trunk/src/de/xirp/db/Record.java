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
 * Record.java
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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

/**
 * This bean represents a recording of values sent to or 
 * received from a robot. There are two possible ways of 
 * recording the observed- and the raw-mode.
 * <br><br>
 * In the observed-mode only received key/value pairs are persisted to 
 * a database. In the raw-mode all transferred data is persisted as-is
 * to a database.
 * <br><br>
 * <b>Note:</b> raw-recording is not yet supported.
 * <br><br>
 * The class is annotated with annotations for persisting Java beans.
 * This class is persisted to a database using Hibernate.
 * 
 * @author Matthias Gernand
 *
 * @see javax.persistence
 */
@Entity
@Table(name = "RECORDS")//$NON-NLS-1$
public final class Record {

	/**
	 * The id of the entry in the database, generated automatically.
	 */
	private long id;
	/**
	 * The start time of the record.
	 */
	private long start;
	/**
	 * The stop time of the record.
	 */
	private long stop;
	/**
	 * The name of the record.
	 */
	private String name;
	/**
	 * The robot anme for which this recording was done.
	 */
	private String robotName;
	/**
	 * A comment on this recording.
	 */
	private String comment;
	/**
	 * The list of recorded key/value pairs.
	 */
	private List<Observed> observed = new ArrayList<Observed>( );

	/**
	 * Constructs a new default {@link de.xirp.db.Record}
	 * object. Must be declared public because Hibernate needs this.
	 */
	public Record() {

	}

	/**
	 * Constructs a new {@link de.xirp.db.Record} object 
	 * using the given robot name.
	 * @param robotName
	 */
	public Record(String robotName) {
		this.robotName = robotName;
	}

	/**
	 * Returns the comment for this record.
	 * 
	 * @return The comment.
	 */
	@Column(name = "RECORD_COMMENT")//$NON-NLS-1$
	public String getComment() {
		return comment;
	}

	/**
	 * Returns the if of the record.
	 * 
	 * @return The id of the record.
	 */
	@Id
	@GeneratedValue
	@Column(name = "RECORD_ID")//$NON-NLS-1$
	public long getId() {
		return id;
	}

	/**
	 * Returns the name of the record.
	 * 
	 * @return The name of the record.
	 */
	@Column(name = "RECORD_NAME")//$NON-NLS-1$
	public String getName() {
		return name;
	}

	/**
	 * Returns the name of the associated robot.
	 * 
	 * @return The name of the robot.
	 */
	@Column(name = "RECORD_ROBOTNAME")//$NON-NLS-1$
	public String getRobotName() {
		return robotName;
	}

	/**
	 * Returns the start time in milliseconds.
	 * 
	 * @return The start time.
	 */
	@Column(name = "RECORD_START_TIME")//$NON-NLS-1$
	public long getStart() {
		return start;
	}

	/**
	 * Returns the stop time in milliseconds.
	 * 
	 * @return The stop time.
	 */
	@Column(name = "RECORD_STOP_TIME")//$NON-NLS-1$
	public long getStop() {
		return stop;
	}

	/**
	 * Sets the comment for this record.
	 * 
	 * @param comment
	 * 			The comment to set.
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Sets the id of the record. This <b>must</b> be private
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
	 * Sets the name of the record.
	 * 
	 * @param name 
	 * 			The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the robot name of the record.
	 * 
	 * @param robotName 
	 * 			The robotName to set.
	 */
	public void setRobotName(String robotName) {
		this.robotName = robotName;
	}

	/**
	 * Sets the start time in milliseconds.
	 * 
	 * @param start 
	 * 			The start time to set.
	 */
	public void setStart(long start) {
		this.start = start;
	}

	/**
	 * Sets the stop time in milliseconds.
	 * 
	 * @param stop 
	 * 			The stop time to set.
	 */
	public void setStop(long stop) {
		this.stop = stop;
	}

	/**
	 * Returns the {@link de.xirp.db.Observed} recorded 
	 * by this record.
	 * 
	 * @return The list of observed key/value pairs.
	 * 
	 * @see de.xirp.db.Observed
	 */
	@OneToMany(mappedBy = "record", fetch = FetchType.LAZY, cascade = CascadeType.ALL)//$NON-NLS-1$
	@OrderBy("timestamp")//$NON-NLS-1$
	@JoinColumn(name = "RECORD_ID")//$NON-NLS-1$
	public List<Observed> getObserved() {
		return observed;
	}

	/**
	 * Sets the list of {@link de.xirp.db.Observed} to this record.
	 * This is private because only Hibernate uses this method 
	 * with refection.
	 * 
	 * @param observed 
	 * 			The list of observed key/value pairs.
	 * 
	 * @see de.xirp.db.Observed
	 */
	@SuppressWarnings("unused")//$NON-NLS-1$
	private void setObserved(List<Observed> observed) {
		this.observed = observed;
	}

	/**
	 * Adds an {@link de.xirp.db.Observed} to the list of observed
	 * key/value pairs.
	 * 
	 * @param observed
	 * 			The object to add.
	 * 
	 * @see de.xirp.db.Observed
	 */
	public void addObserved(Observed observed) {
		observed.setRecord(this);
		this.observed.add(observed);
	}
}
