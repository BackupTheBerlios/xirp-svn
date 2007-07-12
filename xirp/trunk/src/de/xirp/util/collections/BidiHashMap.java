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
 * BidiHashMap.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 25.03.2006:		Created by Rabea Gransberger.
 */
package de.xirp.util.collections;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of a Bidirectional {@link HashMap}.<br>
 * It's possible to get the values for keys and vice versa.<br>
 * The base of this BidiHashMap are two {@link HashMap}s which map
 * from key to value and vice versa.
 * 
 * @param <K>
 *            Type of the keys of the original map
 * @param <V>
 *            Type of the values of the original map
 * @author Rabea Gransberger
 */
public class BidiHashMap<K, V> extends HashMap<K, V> {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The reversed map
	 */
	private HashMap<V, K> reverseMap;

	/**
	 * Constructs an empty BidiHashMap with the default initial
	 * capacity (16) and the default load factor (0.75).
	 */
	public BidiHashMap() {
		super( );
		reverseMap = new HashMap<V, K>( );
	}

	/**
	 * Constructs an empty BidiHashMap with the specified initial
	 * capacity and the default load factor (0.75).
	 * 
	 * @param initialCapacity
	 *            the initial capacity.
	 */
	public BidiHashMap(int initialCapacity) {
		super(initialCapacity);
		reverseMap = new HashMap<V, K>(initialCapacity);
	}

	/**
	 * Constructs an empty BidiHashMap with the specified initial
	 * capacity and load factor.
	 * 
	 * @param initialCapacity
	 *            The initial capacity.
	 * @param loadFactor
	 *            The load factor.
	 */
	public BidiHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		reverseMap = new HashMap<V, K>(initialCapacity, loadFactor);
	}

	/**
	 * Constructs a new BidiHashMap with the same mappings as the
	 * specified Map. The HashMap is created with default load factor
	 * (0.75) and an initial capacity sufficient to hold the mappings
	 * in the specified Map.
	 * 
	 * @param m
	 *            the map whose mappings are to be placed in this map.
	 */
	public BidiHashMap(Map<? extends K, ? extends V> m) {
		super(m.size( ));
		reverseMap = new HashMap<V, K>(m.size( ));
		putAll(m);
	}

	/**
	 * @see java.util.HashMap#clone()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Object clone() {
		return new BidiHashMap(this);
	}

	/**
	 * Removes the values for the key from the original
	 * {@link HashMap} and removes the keys for the value from the
	 * reversed {@link HashMap}.<br>
	 * So the key is removed fully.
	 * 
	 * @param key
	 *            The key to remove with it's values
	 * @return the removed value
	 * @see java.util.HashMap#remove(java.lang.Object)
	 */
	@Override
	public V remove(Object key) {
		V value = super.remove(key);
		reverseMap.remove(value);
		return value;
	}

	/**
	 * Removes the keys for the value from the reversed
	 * {@link HashMap} and removes the values for the key from the
	 * original {@link HashMap}.<br>
	 * So the value is removed fully.
	 * 
	 * @param value
	 *            The value to remove with it's keys
	 * @return The removed key
	 * @see java.util.HashMap#remove(java.lang.Object)
	 */
	public K removeValue(Object value) {
		K key = reverseMap.remove(value);
		this.remove(key);
		return key;
	}

	/**
	 * Copies all of the mappings from the specified map to this map
	 * and reverse map.<br>
	 * These mappings will replace any mappings that this map had for
	 * any of the keys currently in the specified map.
	 * 
	 * @param map
	 *            mappings to be stored in this map.
	 * @see java.util.HashMap#putAll(java.util.Map)
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		for (Map.Entry<? extends K, ? extends V> entry : map.entrySet( )) {
			put(entry.getKey( ), entry.getValue( ));
		}
	}

	/**
	 * Adds the key - value mapping to the original map and the value -
	 * key mapping to the reverses map.
	 * 
	 * @param key
	 *            the key to add
	 * @param value
	 *            the value to add
	 * @return the value that was added
	 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public V put(K key, V value) {
		V oldValue = super.put(key, value);
		reverseMap.remove(oldValue);
		reverseMap.put(value, key);
		return oldValue;
	}

	/**
	 * Gets the key associated with the given value from the reverses
	 * map.
	 * 
	 * @param value
	 *            value to get the key for
	 * @return key for the value
	 */
	public K getKey(V value) {
		return reverseMap.get(value);
	}

	/**
	 * Removes all mappings from both underlying maps.
	 * 
	 * @see java.util.HashMap#clear()
	 */
	@Override
	public void clear() {
		super.clear( );
		reverseMap.clear( );
	}

	/**
	 * Gets a collection of keys of this map.
	 * 
	 * @return collection of keys
	 */
	public Collection<K> keys() {
		return reverseMap.values( );
	}

	/**
	 * Gets the entry set for the reversed map.<br>
	 * The keys of the entries are the values of the original map.
	 * 
	 * @return an entry set
	 * @see java.util.HashMap#entrySet()
	 */
	public Set<java.util.Map.Entry<V, K>> valueEntrySet() {
		return reverseMap.entrySet( );
	}
}
