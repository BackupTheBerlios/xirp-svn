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
 * DatabaseManager.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   Rabea Gransberger [rgransberger AT web.de]
 *
 * Changes
 * -------
 * 10.02.2007:		Created by Matthias Gernand.
 */
package de.xirp.db;

import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

import de.xirp.managers.AbstractManager;
import de.xirp.managers.ManagerException;
import de.xirp.report.ReportDescriptor;
import de.xirp.settings.PropertiesManager;
import de.xirp.util.Constants;
import de.xirp.util.Version;

/**
 * This manager provides methods for plugin developers to execute SQL and
 * HQL (Hibernate Query Language) queries on the database. Internally this 
 * manager initializes and controls the database and Hibernate.
 * 
 * @author Matthias Gernand
 * @author Rabea Gransberger
 */
public final class DatabaseManager extends AbstractManager {

	/**
	 * The logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(DatabaseManager.class);
	/**
	 * The driver to use.
	 */
	private static String driver = ""; //$NON-NLS-1$
	/**
	 * The hibernate session factory.
	 */
	private static SessionFactory sessionFactory;

	/**
	 * Constructs a new instance of the manager.
	 * <br><br>
	 * The manager is initialized on startup. Never
	 * call this on your own. Use the statically provided
	 * methods.
	 * 
	 * @throws InstantiationException if an instance already exists.
	 */
	public DatabaseManager() throws InstantiationException {
		super( );
	}

	/**
	 * Prints a {@link java.sql.SQLException} to the log panel.
	 * This method prints all exceptions inside the given
	 * exception.
	 * 
	 * @param e
	 * 			The exception to print completely.
	 */
	public static void printSQLError(SQLException e) {
		while (e != null) {
			logClass.error("Error " + e.getMessage( ) //$NON-NLS-1$
					+ Constants.LINE_SEPARATOR, e);
			e = e.getNextException( );
		}
	}

	/**
	 * Initializes Hibernate to work with the currently selected database.
	 * 
	 * FIXME: If database changed in settings, init must be done again.
	 */
	private static void initDatabase() {
		Properties props = new Properties( );
		if (driver.equals(Constants.MYSQL_DRIVER)) {
			props.setProperty("hibernate.dialect", //$NON-NLS-1$
					"org.hibernate.dialect.MySQLDialect"); //$NON-NLS-1$
			// props.setProperty("hibernate.dialect",
			// "org.hibernate.dialect.MySQLInnoDBDialect");
			// props.setProperty("hibernate.dialect",
			// "org.hibernate.dialect.MySQLMyISAMDialect");
			props.setProperty("hibernate.connection.driver_class", driver); //$NON-NLS-1$
			props.setProperty("hibernate.connection.url", //$NON-NLS-1$
					getConnectionURL(driver));
			props.setProperty("hibernate.connection.username", //$NON-NLS-1$
					PropertiesManager.getDbUser( ));
			props.setProperty("hibernate.connection.password", //$NON-NLS-1$
					PropertiesManager.getDbPassword( ));
		}
		else if (driver.equals(Constants.HSQLDB_DRIVER)) {
			props.setProperty("hibernate.connection.driver_class", //$NON-NLS-1$
					"org.hsqldb.jdbcDriver"); //$NON-NLS-1$
			props.setProperty("hibernate.connection.url", //$NON-NLS-1$
					getConnectionURL(driver));
			props.setProperty("hibernate.connection.username", "sa"); //$NON-NLS-1$ //$NON-NLS-2$
			props.setProperty("hibernate.connection.password", ""); //$NON-NLS-1$ //$NON-NLS-2$
			props.setProperty("hibernate.dialect", //$NON-NLS-1$
					"org.hibernate.dialect.HSQLDialect"); //$NON-NLS-1$
		}

		// hibernate specific props
		props.setProperty("hibernate.current_session_context_class", "thread"); //$NON-NLS-1$ //$NON-NLS-2$
		props.setProperty("hibernate.cache.provider_class", //$NON-NLS-1$
				"org.hibernate.cache.NoCacheProvider"); //$NON-NLS-1$
		props.setProperty("hibernate.connection.provider_class", //$NON-NLS-1$
				XConnectionProvider.class.getName( ));
		props.setProperty("hibernate.hbm2ddl.auto", "update"); //$NON-NLS-1$ //$NON-NLS-2$

		if (Version.DEVELOPMENT) {
			props.setProperty("hibernate.show_sql", "true"); //$NON-NLS-1$ //$NON-NLS-2$
			props.setProperty("hibernate.format_sql", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else {
			props.setProperty("hibernate.show_sql", "false"); //$NON-NLS-1$ //$NON-NLS-2$
			props.setProperty("hibernate.format_sql", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		// init hibernate
		try {
			sessionFactory = new AnnotationConfiguration( ).addAnnotatedClass(ReportDescriptor.class)
					.addAnnotatedClass(Record.class)
					// .addAnnotatedClass(Raw.class)
					.addAnnotatedClass(Observed.class)
					.mergeProperties(props)
					.buildSessionFactory( );
		}
		catch (Throwable e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
	}

	/**
	 * Returns the current hibernate session.
	 * 
	 * @return A hibernate session.
	 */
	protected static Session getCurrentHibernateSession() {
		return sessionFactory.getCurrentSession( );
	}

	/**
	 * Returns the connection URL for the given driver name.
	 * 
	 * @param driver
	 * 			The db driver name.
	 * @return The connection string.
	 */
	private static String getConnectionURL(String driver) {
		StringBuilder buf = new StringBuilder(256);
		if (driver.equals(Constants.MYSQL_DRIVER)) {
			buf.append("jdbc:mysql://"); //$NON-NLS-1$
			buf.append(PropertiesManager.getDbIP( ));
			buf.append(":");//$NON-NLS-1$
			buf.append(PropertiesManager.getDbPort( ));
			buf.append("/");//$NON-NLS-1$
			buf.append(Constants.DATABASE_NAME);
			buf.append("?createDatabaseIfNotExist=true");//$NON-NLS-1$
			buf.append("&dumpQueriesOnException=true");//$NON-NLS-1$
			// log slow queries only if we have a level <= debug
			if (!logClass.getEffectiveLevel( ).isGreaterOrEqual(Level.INFO)) {
				buf.append("&logSlowQueries=true");//$NON-NLS-1$
			}
		}
		else if (driver.equals(Constants.HSQLDB_DRIVER)) {
			buf.append("jdbc:hsqldb:file:"); //$NON-NLS-1$
			buf.append(Constants.DATABASE_DIR + "/"); //$NON-NLS-1$
			buf.append(Constants.DATABASE_NAME);
		}
		return buf.toString( );
	}

	/**
	 * Executes a SQL query on the database. This is the only method to
	 * get access to the database.
	 * 
	 * @param preparedQuery
	 * 			The prepared query.
	 * @param args
	 * 			The query args.
	 * @throws SQLException 
	 */
	public static void exceuteSQLQuery(String preparedQuery, Object... args) {
		checkQuery(preparedQuery);
		logClass.info("Info: executing SQL queries by plugins is not implemented at the moment."
				+ Constants.LINE_SEPARATOR);
	}

	/**
	 * Executes a SQL update on the database. 
	 * 
	 * @param preparedQuery
	 * 			The prepared query.
	 * @param args
	 * 			The query args.
	 * @throws SQLException 
	 */
	public static void exceuteSQLUpdate(String preparedQuery, Object... args) {
		checkQuery(preparedQuery);
		logClass.info("Info: executing SQL updates by plugins is not implemented at the moment."
				+ Constants.LINE_SEPARATOR);
	}
	
	/**
	 * @param preparedQuery 
	 * TODO
	 */
	private static void checkQuery(String preparedQuery) {
		String del = "DELETE";
		String alt = "ALTER";
		String drop = "DROP";
		if (preparedQuery.contains(del)) {
			throw new RuntimeException("DELETE not allowed!");
		}
		if (preparedQuery.contains(alt)) {
			throw new RuntimeException("ALTER not allowed!");
		}
		if (preparedQuery.contains(drop)) {
			throw new RuntimeException("DROP not allowed!");
		}
	}

	/**
	 * Executes a HQL query on the database.
	 * 
	 * @param preparedQuery
	 * 			The prepared query.
	 * @param args
	 * 			The query args.
	 */
	public static void exceuteHQLQuery(String preparedQuery, Object... args) {
		//TODO:
		logClass.info("Info: executing HQL queries by plugins is not implemented at the moment."
				+ Constants.LINE_SEPARATOR);
	}

	/**
	 * Executes a HQL update on the database.
	 * 
	 * @param preparedQuery
	 * 			The prepared query.
	 * @param args
	 * 			The query args.
	 */
	public static void exceuteHQLUpdate(String preparedQuery, Object... args) {
		//TODO:
		logClass.info("Info: executing HQL updates by plugins is not implemented at the moment."
				+ Constants.LINE_SEPARATOR);
	}
	
	/**
	 * Initializes the manager and loads the db driver.
	 * <br><br>
	 * This method is called from the
	 * {@link de.xirp.managers.ManagerFactory} on
	 * startup. Do not call it on your own.
	 * 
	 * @see de.xirp.managers.AbstractManager#start()
	 */
	@Override
	protected void start() throws ManagerException {
		super.start( );
		driver = PropertiesManager.getDefaultDriver( );
		try {
			Class.forName(driver);
		}
		catch (ClassNotFoundException e) {
			throw new ManagerException(e);
		}
		initDatabase( );
	}

	/**
	 * Does nothing.
	 * <br><br>
	 * This method is called from the
	 * {@link de.xirp.managers.ManagerFactory} on
	 * shutdown. Do not call it on your own.
	 * 
	 * @see de.xirp.managers.AbstractManager#stop()
	 */
	@Override
	protected void stop() throws ManagerException {
		super.stop( );
	}
}
