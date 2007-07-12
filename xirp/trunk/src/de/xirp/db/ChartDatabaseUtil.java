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
 * ChartDatabaseUtil.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 14.03.2007:		Created by Matthias Gernand.
 */
package de.xirp.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.ListUtils;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * This class provides methods for querying the database.
 * {@link de.xirp.db.Observed} can be saved to and read
 * from the database.
 * 
 * @author Matthias Gernand
 * @see de.xirp.db.Observed
 */
public class ChartDatabaseUtil {

	// private static final Logger logClass =
	// Logger.getLogger(ChartDatabaseUtil.class);

	/**
	 * Returns all {@link de.xirp.db.Observed} for the
	 * given record and key name.
	 * 
	 * @param record
	 *            The record to look in.
	 * @param keyname
	 *            The name of the observed key.
	 * @return A list with all <code>Observed</code> for the record
	 *         and key.
	 * @see de.xirp.db.Observed
	 */
	@SuppressWarnings("unchecked")
	public static List<Observed> getObservedList(Record record, String keyname) {
		Session session = DatabaseManager.getCurrentHibernateSession( );
		session.getTransaction( ).begin( );

		Query query = session.createQuery("FROM Observed as obs WHERE obs.record = ? and obs.observedKey = ?"); //$NON-NLS-1$
		query.setEntity(0, record);
		query.setString(1, keyname);
		List<Observed> obs = query.list( );

		session.close( );
		return Collections.unmodifiableList(obs);
	}

	/**
	 * Returns all {@link de.xirp.db.Observed} for the
	 * given record, key name and time interval.
	 * 
	 * @param record
	 *            The record to look in.
	 * @param keyname
	 *            The name of the observed key.
	 * @param startTime
	 *            The start time of the time interval.
	 * @param stopTime
	 *            the stop tine of the time interval.
	 * @return A list with all <code>Observed</code> for the record,
	 *         key and time interval.
	 * @see de.xirp.db.Observed
	 */
	@SuppressWarnings("unchecked")
	public static List<Observed> getObservedList(Record record, String keyname,
			long startTime, long stopTime) {
		Session session = DatabaseManager.getCurrentHibernateSession( );
		session.getTransaction( ).begin( );

		Query query = session.createQuery("FROM Observed as obs WHERE obs.record = ? and obs.observedKey = ? and obs.timestamp >= ? and obs.timestamp <= ?"); //$NON-NLS-1$
		query.setEntity(0, record);
		query.setString(1, keyname);
		query.setLong(2, startTime);
		query.setLong(3, stopTime);
		List<Observed> obs = query.list( );

		session.close( );
		return Collections.unmodifiableList(obs);
	}

	/**
	 * Returns all available robot names assocciated with a record.
	 * 
	 * @return A list with all available robot names.
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getAvailableRobots() {
		Session session = DatabaseManager.getCurrentHibernateSession( );
		session.getTransaction( ).begin( );

		Query query = session.createQuery("SELECT DISTINCT record.robotName FROM Record record"); //$NON-NLS-1$
		List<String> names = query.list( );

		session.close( );
		return Collections.unmodifiableList(names);
	}

	/**
	 * Returns all key names for a given record id.
	 * 
	 * @param id
	 *            The record id.
	 * @return A list with the available keys for this record.
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getObservedKeysForRecordID(long id) {
		Session session = DatabaseManager.getCurrentHibernateSession( );
		session.getTransaction( ).begin( );

		Query query = session.createQuery("SELECT DISTINCT obs.observedKey FROM Observed as obs WHERE obs.record = ?"); //$NON-NLS-1$
		query.setLong(0, new Long(id));
		List<String> keys = query.list( );

		session.close( );
		return Collections.unmodifiableList(keys);
	}

	/**
	 * Returns all {@link de.xirp.db.Observed} for the
	 * given record id.
	 * 
	 * @param id
	 *            The record id.
	 * @return A list with all <code>Observed</code> for the record.
	 * @see de.xirp.db.Observed
	 */
	@SuppressWarnings("unchecked")
	public static List<Observed> getObservedListForRecordID(long id) {
		Session session = DatabaseManager.getCurrentHibernateSession( );
		session.getTransaction( ).begin( );

		Query query = session.createQuery("FROM Observed as obs WHERE obs.record = ?"); //$NON-NLS-1$
		query.setLong(0, new Long(id));
		List<Observed> keys = query.list( );

		session.close( );
		return Collections.unmodifiableList(keys);
	}

	/**
	 * Returns all {@link de.xirp.db.Observed} for the
	 * given record.
	 * 
	 * @param record
	 *            The record.
	 * @return A list with all <code>Observed</code> for the record.
	 * @see de.xirp.db.Observed
	 */
	@SuppressWarnings("unchecked")
	public static List<Observed> getObservedListForRecord(Record record) {
		return getObservedListForRecordID(record.getId( ));
		// Session session =
		// DatabaseManager.getCurrentHibernateSession( );
		// session.getTransaction( ).begin( );
		//		
		// Query query = session.createQuery("FROM Observed as obs
		// WHERE obs.record = ?"); //$NON-NLS-1$
		// query.setEntity(0, record);
		// List<Observed> keys = query.list( );
		//		
		// session.close( );
		// return Collections.unmodifiableList(keys);
	}

	/**
	 * Returns all key names for a given record.
	 * 
	 * @param record
	 *            The record.
	 * @return A list with the available keys for this record.
	 * @see de.xirp.db.Observed
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getObservedKeysForRecord(Record record) {
		return getObservedKeysForRecordID(record.getId( ));
		// Session session =
		// DatabaseManager.getCurrentHibernateSession( );
		// session.getTransaction( ).begin( );
		//		
		// Query query = session.createQuery("SELECT DISTINCT
		// obs.observedKey FROM Observed as obs WHERE obs.record =
		// ?"); //$NON-NLS-1$
		// query.setEntity(0, record);
		// List<String> keys = query.list( );
		//		
		// session.close( );
		// return Collections.unmodifiableList(keys);
	}

	/**
	 * Returns the keys that all of the given records have in common.
	 * 
	 * @param records
	 *            The records.
	 * @return A list with the common keys.
	 * @see de.xirp.db.Observed
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getCommonObservedKeysForRecordList(
			List<Record> records) {
		List<String> commonKeys = new ArrayList<String>( );

		List<List<String>> lists = new ArrayList<List<String>>( );

		Session session = DatabaseManager.getCurrentHibernateSession( );
		session.getTransaction( ).begin( );

		Query query;
		for (Record record : records) {
			query = session.createQuery("SELECT DISTINCT obs.observedKey FROM Observed as obs WHERE obs.record = ?"); //$NON-NLS-1$
			query.setEntity(0, record);
			lists.add(query.list( ));
		}
		session.close( );

		commonKeys = lists.get(0);
		for (List<String> list : lists) {
			commonKeys = ListUtils.intersection(commonKeys, list);
		}

		return Collections.unmodifiableList(commonKeys);
	}

	/**
	 * Returns a map from string to long for the given record and key
	 * array. The map contains the count of occurences for each given
	 * key name. If the flag <code>relative</code> is
	 * <code>true</code> the map contains the percentage of the
	 * occurences for each given key name.
	 * 
	 * @param record
	 *            The record.
	 * @param keys
	 *            The keys to use.
	 * @param relative
	 *            A flag indicating if relative values should be used.
	 * @return A map with key->value pairs.
	 * @see de.xirp.db.Observed
	 */
	public static Map<String, Long> getValuesForRecord(Record record,
			String[] keys, boolean relative) {
		Session session = DatabaseManager.getCurrentHibernateSession( );
		session.getTransaction( ).begin( );

		Map<String, Long> map = new HashMap<String, Long>( );

		Query query;
		Long onePercent = null;
		if (relative) {
			query = session.createQuery("SELECT COUNT(obs.value) FROM Observed as obs WHERE obs.record = ?"); //$NON-NLS-1$
			query.setEntity(0, record);
			onePercent = (Long) query.uniqueResult( );
			onePercent /= 100;
		}

		query = session.createQuery("SELECT COUNT(obs.value) FROM Observed as obs WHERE obs.record = ? and obs.observedKey = ?"); //$NON-NLS-1$
		query.setEntity(0, record);
		for (String key : keys) {
			query.setString(1, key);
			Long val = (Long) query.uniqueResult( );
			if (!relative) {
				map.put(key, val);
			}
			else {
				map.put(key, val / onePercent);
			}
		}

		session.close( );
		return Collections.unmodifiableMap(map);
	}

	/**
	 * Returns all {@link de.xirp.db.Observed} from the
	 * given record for a single key.
	 * 
	 * @param record
	 *            The record.
	 * @param key
	 *            The key to use.
	 * @return A list with all <code>Observed</code>.
	 * @see de.xirp.db.Observed
	 */
	public static List<Observed> getObservedForKey(Record record, String key) {
		List<Observed> observed = new ArrayList<Observed>( );

		for (Observed o : record.getObserved( )) {
			if (o.getObservedKey( ).equals(key)) {
				observed.add(o);
			}
		}

		return Collections.unmodifiableList(observed);
	}
}
