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
 * ConcurrentMultiValueHashMap.java
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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javolution.util.FastTable;

/**
 * A {@link MultiValueHashMap} implementation which is backed by a
 * {@link java.util.concurrent.ConcurrentHashMap} using
 * {@link FastTable}s for the value lists and therefor allows
 * concurrent access.
 * 
 * @param <K>
 *            Type of the keys for this {@link java.util.HashMap}
 * @param <V>
 *            Type of the values for this {@link java.util.HashMap}
 * @author Rabea Gransberger
 */
public class ConcurrentMultiValueHashMap<K, V> {

	/**
	 * HashMap with Vector as Values
	 */
	private ConcurrentHashMap<K, FastTable<V>> map;

	/**
	 * Construct a new empty MultiValueHashMap
	 * 
	 * @see java.util.concurrent.ConcurrentHashMap#ConcurrentHashMap()
	 */
	public ConcurrentMultiValueHashMap() {
		map = new ConcurrentHashMap<K, FastTable<V>>( );
	}

	/**
	 * Construct a new empty map with the given initial capacity.
	 * 
	 * @param initialCapacity
	 *            the initial capacity. The implementation performs
	 *            internal sizing to accommodate this many elements.
	 * @see java.util.concurrent.ConcurrentHashMap#ConcurrentHashMap(int)
	 */
	public ConcurrentMultiValueHashMap(int initialCapacity) {
		map = new ConcurrentHashMap<K, FastTable<V>>(initialCapacity);
	}

	/**
	 * Construct a new empty map with the given initial capacity.s
	 * 
	 * @param initialCapacity
	 *            the initial capacity. The implementation performs
	 *            internal sizing to accommodate this many elements.
	 * @param loadFactor
	 *            the load factor threshold, used to control resizing.
	 *            Resizing may be performed when the average number of
	 *            elements per bin exceeds this threshold.
	 * @param concurrencyLevel
	 *            the estimated number of concurrently updating
	 *            threads. The implementation performs internal sizing
	 *            to try to accommodate this many threads.
	 * @see java.util.concurrent.ConcurrentHashMap#ConcurrentHashMap(int,
	 *      float, int)
	 */
	public ConcurrentMultiValueHashMap(int initialCapacity, float loadFactor,
			int concurrencyLevel) {
		map = new ConcurrentHashMap<K, FastTable<V>>(initialCapacity,
				loadFactor,
				concurrencyLevel);
	}

	/**
	 * Construct a map filling it with the given map.
	 * 
	 * @param map
	 *            the map which provides the data for the new map
	 * @see java.util.concurrent.ConcurrentHashMap#ConcurrentHashMap(java.util.Map)
	 */
	public ConcurrentMultiValueHashMap(
			Map<? extends K, ? extends FastTable<V>> map) {
		this.map = new ConcurrentHashMap<K, FastTable<V>>(map);
	}

	/**
	 * Returns true if this map contains no key-value mappings. This
	 * implementation returns size() == 0.
	 * 
	 * @return true if this map contains no key-value mappings.
	 */
	public boolean isEmpty() {
		return map.isEmpty( );
	}

	/**
	 * Legacy method testing if some key maps into the specified value
	 * in this table. This method is identical in functionality to
	 * containsValue(java.lang.Object), and exists solely to ensure
	 * full compatibility with class Hashtable, which supported this
	 * method prior to introduction of the Java Collections framework.
	 * 
	 * @param key
	 *            a key to search for.
	 * @return true if and only if some key maps to the value argument
	 *         in this table as determined by the equals method; false
	 *         otherwise.
	 */
	public boolean contains(K key) {
		return map.contains(key);
	}

	/**
	 * Checks if the map contains the given value for the given key.
	 * 
	 * @param key
	 *            key of the mapping
	 * @param value
	 *            value whose presence in this map at the given key is
	 *            to be tested.
	 * @return <code>true</code> if this map contains a value for
	 *         the given key
	 */
	public boolean containsValue(K key, V value) {
		if (map.containsKey(key)) {
			return map.get(key).contains(value);
		}
		return false;
	}

	/**
	 * Returns an iterator of the values in this table.
	 * 
	 * @return an iterator of the values in this table.
	 */
	public Iterator<FastTable<V>> elements() {
		return map.values( ).iterator( );
	}

	/**
	 * Returns an iterator of the keys in this table.
	 * 
	 * @return an iterator of the keys in this table.
	 */
	public Iterator<K> keys() {
		return map.keySet( ).iterator( );
	}

	/**
	 * Returns a set view of the keys contained in this map. The set
	 * is backed by the map, so changes to the map are reflected in
	 * the set, and vice-versa. The set supports element removal,
	 * which removes the corresponding mapping from this map, via the
	 * Iterator.remove, Set.remove, removeAll, retainAll, and clear
	 * operations. It does not support the add or addAll operations.
	 * The view's returned iterator is a "weakly consistent" iterator
	 * that will never throw ConcurrentModificationException, and
	 * guarantees to traverse elements as they existed upon
	 * construction of the iterator, and may (but is not guaranteed
	 * to) reflect any modifications subsequent to construction.
	 * 
	 * @return a set view of the keys contained in this map.
	 */
	public Set<K> keySet() {
		return map.keySet( );
	}

	/**
	 * Copies all of the mappings from the specified map to this one.
	 * These mappings replace any mappings that this map had for any
	 * of the keys currently in the specified map.
	 * 
	 * @param map
	 *            Mappings to be stored in this map.
	 */
	public void putAll(Map<? extends K, ? extends FastTable<V>> map) {
		this.map.putAll(map);
	}

	/**
	 * If the specified key is not already associated with a value,
	 * associate it with the given value. This is equivalent to<br>
	 * <code>
	 *if (!map.containsKey(key))
	 *  return map.put(key, value);
	 * else
	 *  return map.get(key);</code>
	 * Except that the action is performed atomically.
	 * 
	 * @param key
	 *            key with which the specified value is to be
	 *            associated.
	 * @param values
	 *            value to be associated with the specified key.
	 * @return previous value associated with specified key, or null
	 *         if there was no mapping for key.
	 */
	public List<V> putIfAbsent(K key, FastTable<V> values) {
		return map.putIfAbsent(key, values);
	}

	/**
	 * Removes the value from the key.
	 * 
	 * @param key
	 *            the key that needs to be removed.
	 * @param value
	 *            the value to be removed
	 * @return <code>true</code> if the value has been removed
	 */
	public boolean remove(K key, V value) {
		if (value == null || key == null) {
			return true;
		}
		if (map.containsKey(key)) {
			return map.get(key).remove(value);
		}
		return false;
	}

	/**
	 * Removes the value from the key.
	 * 
	 * @param value
	 *            the value to be removed
	 * @return true if the value has been removed
	 */
	public boolean remove(V value) {
		for (FastTable<V> keyValues : map.values( )) {
			keyValues.remove(value);
		}
		return true;
	}

	/**
	 * Returns the number of key-value mappings in this map. If the
	 * map contains more than {@link java.lang.Integer#MAX_VALUE}
	 * elements, returns {@link java.lang.Integer#MAX_VALUE}. This
	 * implementation returns entrySet().size().
	 * 
	 * @return the number of key-value mappings in this map.
	 */
	public int size() {
		return map.size( );
	}

	/**
	 * Adds the key - value mapping to this map.<br>
	 * The value is added to the list of values associated with this
	 * key even if it does already exist.
	 * 
	 * @param key
	 *            key of this mapping
	 * @param value
	 *            value of this mapping
	 */
	public void put(K key, V value) {
		if (value != null && key != null) {
			FastTable<V> values = map.get(key);
			if (values == null) {
				values = new FastTable<V>( );
			}
			if (!values.contains(value)) {
				values.add(value);
			}
			map.put(key, values);
		}
	}

	/**
	 * Gets the values for this key.
	 * 
	 * @param key
	 *            key of the mapping
	 * @return unmodifiable list of values. If the key was not in the
	 *         map the list is empty
	 */
	public List<V> get(K key) {
		if (key != null) {
			FastTable<V> ret = map.get(key);
			if (ret != null) {
				return ret.unmodifiable( );
			}
		}
		return new FastTable<V>(0);
	}

	/**
	 * Returns a collection view of the mappings contained in this
	 * map. Each element in the returned collection is a Map.Entry.
	 * The collection is backed by the map, so changes to the map are
	 * reflected in the collection, and vice-versa. The collection
	 * supports element removal, which removes the corresponding
	 * mapping from the map, via the Iterator.remove,
	 * Collection.remove, removeAll, retainAll, and clear operations.
	 * It does not support the add or addAll operations. The view's
	 * returned iterator is a "weakly consistent" iterator that will
	 * never throw ConcurrentModificationException, and guarantees to
	 * traverse elements as they existed upon construction of the
	 * iterator, and may (but is not guaranteed to) reflect any
	 * modifications subsequent to construction.
	 * 
	 * @return a collection view of the mappings contained in this
	 *         map.
	 */
	public Set<java.util.Map.Entry<K, FastTable<V>>> entrySet() {
		return map.entrySet( );
	}

	/**
	 * Checks if the given key is contained in this map.
	 * 
	 * @param key
	 *            the key to check
	 * @return <code>true</code> if the key is contained in this map
	 */
	public boolean containsKey(K key) {
		return map.containsKey(key);
	}

	/**
	 * Removes the mapping for this key from this map if present.
	 * 
	 * @param key
	 *            key whose mapping is to be removed from the map.
	 * @return previous value associated with specified key, or an
	 *         empty list if there was no mapping for key.
	 */
	public List<V> remove(K key) {
		FastTable<V> removed = map.remove(key);
		if (removed != null) {
			return removed.unmodifiable( );
		}
		return new FastTable<V>(0);
	}

	/**
	 * Removes all mappings from this map.
	 */
	public void clear() {
		map.clear( );
	}

	/**
	 * Returns a collection view of the values contained in this map.
	 * The collection is backed by the map, so changes to the map are
	 * reflected in the collection, and vice-versa. The collection
	 * supports element removal, which removes the corresponding
	 * mapping from this map, via the Iterator.remove,
	 * Collection.remove, removeAll, retainAll, and clear operations.
	 * It does not support the add or addAll operations. The view's
	 * returned iterator is a "weakly consistent" iterator that will
	 * never throw ConcurrentModificationException, and guarantees to
	 * traverse elements as they existed upon construction of the
	 * iterator, and may (but is not guaranteed to) reflect any
	 * modifications subsequent to construction.
	 * 
	 * @return a collection view of the values contained in this map.
	 */
	public List<V> values() {
		FastTable<V> v = new FastTable<V>( );
		for (FastTable<V> keyValues : map.values( )) {
			v.addAll(keyValues);
		}
		return v.unmodifiable( );
	}

}
