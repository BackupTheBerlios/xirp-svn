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
 * RecordDatabaseUtil.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 11.02.2007:		Created by Matthias Gernand.
 */
package de.xirp.db;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import de.xirp.util.Constants;

/**
 * This class provides methods for querying the database. Records can
 * be saved and loaded form the database.
 * 
 * @author Matthias Gernand
 * @see de.xirp.db.Record
 */
public class RecordDatabaseUtil {

	private static final Logger logClass = Logger.getLogger(RecordDatabaseUtil.class);

	/**
	 * Returns all available records for the given robot name.
	 * 
	 * @param robotName
	 *            The robot name.
	 * @return A list with all available records for the robot.
	 * @see de.xirp.db.Record
	 */
	@SuppressWarnings("unchecked")
	public static List<Record> getAvailableRecordsForRobot(String robotName) {
		Session session = DatabaseManager.getCurrentHibernateSession( );
		session.getTransaction( ).begin( );

		Query query = session.createQuery("FROM Record as record WHERE record.robotName = ?"); //$NON-NLS-1$
		query.setString(0, robotName);
		List<Record> records = query.list( );

		session.close( );
		return Collections.unmodifiableList(records);
	}

	/**
	 * This method returns all records existing in the database.
	 * 
	 * @return A list with all available records.
	 * @see de.xirp.db.Record
	 */
	@SuppressWarnings("unchecked")
	public static List<Record> getAllRecords() {
		Session session = DatabaseManager.getCurrentHibernateSession( );
		session.getTransaction( ).begin( );

		Query query = session.createQuery("FROM Record as record"); //$NON-NLS-1$
		List<Record> records = query.list( );

		session.close( );
		return Collections.unmodifiableList(records);
	}

	/**
	 * Saves the given {@link de.xirp.db.Record} to the
	 * database.
	 * 
	 * @param record
	 *            The record to save.
	 * @see de.xirp.db.Record
	 */
	public static void persistRecord(Record record) {
		Session session = DatabaseManager.getCurrentHibernateSession( );
		session.getTransaction( ).begin( );

		try {
			session.save(record);
			session.getTransaction( ).commit( );
		}
		catch (HibernateException e) {
			session.getTransaction( ).rollback( );
			logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
					+ Constants.LINE_SEPARATOR, e);
		}
		finally {
			if (session.isOpen( )) {
				session.close( );
			}
		}
	}
}
