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
 * ReportDatabaseUtil.java
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
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import de.xirp.report.ReportDescriptor;
import de.xirp.util.Constants;
import de.xirp.util.Util;

/**
 * This class offers several methods for querying the database. Report
 * PDFs can be stored, a report can be read and the database can be
 * search for reports.
 * 
 * @author Matthias Gernand
 * @see de.xirp.report.ReportDescriptor
 */
public final class ReportDatabaseUtil {

	/**
	 * The logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(ReportDatabaseUtil.class);

	/**
	 * Stores the given
	 * {@link de.xirp.report.ReportDescriptor} to the
	 * database.
	 * 
	 * @param rd
	 *            The report descriptor to store.
	 * @return the id of the persisted report
	 * @see de.xirp.report.ReportDescriptor
	 */
	public static Long storePDF(ReportDescriptor rd) {
		Session session = DatabaseManager.getCurrentHibernateSession( );
		session.beginTransaction( );

		try {
			session.save(rd);
			session.getTransaction( ).commit( );
		}
		catch (HibernateException e) {
			session.getTransaction( ).rollback( );
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
		finally {
			if (session.isOpen( )) {
				session.close( );
			}
		}
		return rd.getId( );
	}

	/**
	 * Returns all robot names found in the existing stored reports.
	 * 
	 * @return A list of all robot names.
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getAvailableRobots() {
		Session session = DatabaseManager.getCurrentHibernateSession( );
		session.getTransaction( ).begin( );

		Query query = session.createQuery("SELECT DISTINCT report.robotName FROM ReportDescriptor report"); //$NON-NLS-1$
		List<String> names = query.list( );

		session.close( );
		return Collections.unmodifiableList(names);
	}

	/**
	 * Returns a {@link de.xirp.report.ReportDescriptor}
	 * for the given id.
	 * 
	 * @param id
	 *            The report id.
	 * @return The report descriptor.
	 * @see de.xirp.report.ReportDescriptor
	 */
	public static ReportDescriptor getReport(long id) {
		Session session = DatabaseManager.getCurrentHibernateSession( );
		session.getTransaction( ).begin( );

		Query query = session.createQuery("FROM ReportDescriptor as report WHERE report.id = ?"); //$NON-NLS-1$
		query.setLong(0, id);
		ReportDescriptor rd = (ReportDescriptor) query.uniqueResult( );

		session.close( );
		return rd;
	}

	/**
	 * Starts a search in the database and returns all report
	 * descriptors fitting the search parameters. The parameters are
	 * the given start and stop time and the robot name. If the robot
	 * name is <code>null</code> or empty the method will return the
	 * reports found in the specified time interval for all existing
	 * robots.
	 * 
	 * @param start
	 *            The start time of the search interval.
	 * @param stop
	 *            the stop time of the search interval.
	 * @param robotName
	 *            The robot name to search reports for.
	 * @return A list of report descriptors.
	 * @see de.xirp.report.ReportDescriptor
	 */
	@SuppressWarnings("unchecked")
	public static List<ReportDescriptor> searchForReports(Date start,
			Date stop, String robotName) {
		Session session = DatabaseManager.getCurrentHibernateSession( );
		session.getTransaction( ).begin( );

		Query query;
		if (!Util.isEmpty(robotName)) {
			query = session.createQuery("FROM ReportDescriptor as report WHERE report.creationTime > ? AND report.creationTime < ? AND report.robotName = ?"); //$NON-NLS-1$
			query.setLong(0, start.getTime( ));
			query.setLong(1, stop.getTime( ));
			query.setString(2, robotName);
		}
		else {
			query = session.createQuery("FROM ReportDescriptor as report WHERE report.creationTime > ? AND report.creationTime < ?"); //$NON-NLS-1$
			query.setLong(0, start.getTime( ));
			query.setLong(1, stop.getTime( ));
		}

		List<ReportDescriptor> names = query.list( );

		session.close( );
		return Collections.unmodifiableList(names);
	}
}
