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
 * DatapoolQueue.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [TODO: email];
 * Contributor(s):   
 *
 * Changes
 * -------
 * 06.06.2007:		Created by Rabea Gransberger.
 */
package de.xirp.io.comm.data;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.xirp.util.collections.MultiValueHashMap;

/**
 * Queue for received datapool messages. The queue preserves the order
 * of the incoming messages and handles dropping of messages.
 * 
 * @author Rabea Gransberger
 */
class DatapoolQueue {

	/**
	 * Order of the queue with the keys of the received messages
	 */
	private ConcurrentLinkedQueue<String> order = new ConcurrentLinkedQueue<String>( );
	/**
	 * The messages of the queue. There might be more than one message
	 * for one key. In this case the key can be found more than once
	 * in the order list.
	 */
	private MultiValueHashMap<String, DatapoolMessage> data = new MultiValueHashMap<String, DatapoolMessage>( );

	/**
	 * Adds the given message to this queue.<br>
	 * If the queue contains already a message for the messages key
	 * and the message is dropable, the old message is replaced with
	 * this one, while preserving the old order.<br>
	 * Otherwise the message is just added as last one.
	 * 
	 * @param m
	 *            the message to add
	 * @return <code>true</code> if the message was added to this
	 *         queue
	 */
	public synchronized boolean add(DatapoolMessage m) {
		if (m.isMayDrop( ) && order.contains(m.getKey( ))) {
			// replace
			data.remove(m.getKey( ));
			data.put(m.getKey( ), m);
		}
		else {
			order.add(m.getKey( ));
			data.put(m.getKey( ), m);
		}

		return true;
	}

	/**
	 * Gets the number of queued messages.
	 * 
	 * @return the number of queued messages
	 */
	public synchronized int size() {
		return order.size( );
	}

	/**
	 * Adds the given message to the queue.
	 * 
	 * @param message
	 *            the message to add
	 * @return <code>true</code> if the message was added to this
	 *         queue
	 */
	public synchronized boolean offer(DatapoolMessage message) {
		return add(message);
	}

	/**
	 * Retrieves but does not remove the first message of this queue.
	 * 
	 * @return the first message of the queue or <code>null</code>
	 *         if the queue is empty
	 * @see java.util.Queue#peek()
	 */
	public synchronized DatapoolMessage peek() {
		if (!isEmpty( )) {
			return data.get(order.peek( )).get(0);
		}
		return null;
	}

	/**
	 * Retrieves and removes the first message of this queue.
	 * 
	 * @return the first message of the queue or <code>null</code>
	 *         if the queue is empty
	 * @see java.util.Queue#poll()
	 */
	public synchronized DatapoolMessage poll() {
		if (!isEmpty( )) {
			String key = order.poll( );
			List<DatapoolMessage> list = data.get(key);
			DatapoolMessage message = list.get(0);
			data.remove(key, message);
			return message;
		}
		return null;
	}

	/**
	 * Clears this queue. This means that all messages are removed.
	 */
	public synchronized void clear() {
		order.clear( );
		data.clear( );
	}

	/**
	 * Checks if this queue is empty.
	 * 
	 * @return <code>true</code> if this queue is empty (contains no
	 *         messages).
	 */
	public synchronized boolean isEmpty() {
		return order.isEmpty( );
	}

}
