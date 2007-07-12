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
 * XConnectionProvider.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 23.03.2007:		Created by Matthias Gernand.
 */
package de.xirp.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.hibernate.HibernateException;
import org.hibernate.connection.ConnectionProvider;

import de.xirp.util.I18n;

/**
 * This class is a database connection pool implementation using the
 * Apache Commons Pool and DBCP APIs. <br>
 * <br>
 * It implements the
 * {@link org.hibernate.connection.ConnectionProvider} interface from
 * the Hibernate API. So the connection pool can provide its
 * connections to Hibernate.
 * 
 * @author Matthias Gernand
 * @see org.apache.commons.dbcp.PoolingDataSource
 * @see org.apache.commons.pool.impl.GenericObjectPool
 * @see org.hibernate.connection.ConnectionProvider
 */
public class XConnectionProvider implements ConnectionProvider {

	private static final int MIN_IDLE = 5;
	private static final int MAX_ACTIVE = 10;
	private static final int EVICTION_TIME = 500;

	private static DataSource ds;
	private static GenericObjectPool connectionPool;
	private static DriverManagerConnectionFactory connectionFactory;
	@SuppressWarnings("unused")
	private static PoolableConnectionFactory poolableConnectionFactory;
	private static PoolingDataSource dataSource;

	/**
	 * The connection pool is closed by this method.
	 * 
	 * @throws HibernateException
	 *             if an exception occurs while closing the pool.
	 * @see org.hibernate.connection.ConnectionProvider#close()
	 * @see org.apache.commons.pool.impl.GenericObjectPool
	 */
	public void close() throws HibernateException {
		try {
			connectionPool.close( );
		}
		catch (Exception e) {
			throw new HibernateException(e);
		}
	}

	/**
	 * The given {@link java.sql.Connection}> is closed.
	 * 
	 * @throws SQLException
	 *             if an exception occurs while closing the
	 *             connection.
	 * @see org.hibernate.connection.ConnectionProvider#closeConnection(java.sql.Connection)
	 * @see java.sql.Connection
	 */
	public void closeConnection(Connection conn) throws SQLException {
		conn.close( );
	}

	/**
	 * The given {@link java.util.Properties} are used to configure
	 * the connection pool. The pool is initialized.
	 * 
	 * @see org.hibernate.connection.ConnectionProvider#configure(java.util.Properties)
	 */
	public void configure(Properties props) throws HibernateException {
		initPool(props.getProperty("hibernate.connection.url"), //$NON-NLS-1$
				props.getProperty("hibernate.connection.username"), //$NON-NLS-1$
				props.getProperty("hibernate.connection.password")); //$NON-NLS-1$
	}

	/**
	 * This methos initializes the connection pool using the given
	 * URL, user name and password.
	 * 
	 * @param url
	 *            The db URL.
	 * @param user
	 *            The db user.
	 * @param password
	 *            The users password.
	 */
	private void initPool(String url, String user, String password) {

		connectionPool = new GenericObjectPool( );
		connectionFactory = new DriverManagerConnectionFactory(url,
				user,
				password);
		poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory,
				connectionPool,
				null,
				null,
				false,
				true);
		dataSource = new PoolingDataSource(connectionPool);

		dataSource.setAccessToUnderlyingConnectionAllowed(false);

		connectionPool.setMaxActive(MAX_ACTIVE);
		connectionPool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_GROW);
		connectionPool.setMinIdle(MIN_IDLE);
		connectionPool.setTimeBetweenEvictionRunsMillis(EVICTION_TIME);
		connectionPool.setTestOnBorrow(true);
		connectionPool.setTestWhileIdle(false);
		connectionPool.setTestOnReturn(false);

		ds = dataSource;
	}

	/**
	 * Returns a {@link java.sql.Connection} from the pool.
	 * 
	 * @throws SQLException
	 *             if something went wrong getting a connection.
	 * @see org.hibernate.connection.ConnectionProvider#getConnection()
	 * @see java.sql.Connection
	 */
	public Connection getConnection() throws SQLException {
		if (ds != null) {
			Connection c = ds.getConnection( );
			/*
			 * logClass.debug("Active: " +
			 * connectionPool.getNumActive( ) + "\n");
			 * logClass.debug("Idle: " + connectionPool.getNumIdle( ) +
			 * "\n");
			 */
			return c;
		}
		else {
			throw new SQLException(I18n.getString("XConnectionProvider.exception.datasourceNotAvailable")); //$NON-NLS-1$
		}
	}

	/**
	 * This implementation does not support aggressive release.
	 * 
	 * @see org.hibernate.connection.ConnectionProvider#supportsAggressiveRelease()
	 */
	public boolean supportsAggressiveRelease() {
		return false;
	}

	/**
	 * Returns the number of idle connection in the pool.
	 * 
	 * @return The number of idle connections.
	 * @see org.apache.commons.pool.impl.GenericObjectPool#getNumIdle()
	 */
	protected int getNumIdle() {
		return connectionPool.getNumIdle( );
	}

	/**
	 * Returns the number of active connections in the pool.
	 * 
	 * @return The number of active connections.
	 * @see org.apache.commons.pool.impl.GenericObjectPool#getNumActive()
	 */
	protected int getNumActive() {
		return connectionPool.getNumActive( );
	}
}
