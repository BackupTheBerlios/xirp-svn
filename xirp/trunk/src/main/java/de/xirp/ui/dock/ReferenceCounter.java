package de.xirp.ui.dock;

/*********************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others. All rights
 * reserved. This program and the accompanying materials are made
 * available under the terms of the Common Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ********************************************************************/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A ReferenceCounter is used to reference counting objects. Each
 * object is identified by a unique ID. Together they form an ID -
 * value pair. An object is added to the counter by calling #put(id,
 * object). From this point on additional refs can be made by calling
 * #addRef(id) or #removeRef(id).
 */
class ReferenceCounter {

	/**
	 * 
	 */
	private Map<Object, RefRec> mapIdToRec = new HashMap<Object, RefRec>(11);

	/**
	 * Capture the information about an object.<br>
	 */
	public class RefRec {

		/**
		 * 
		 */
		public Object id;
		/**
		 * 
		 */
		public Object value;
		/**
		 * 
		 */
		private int refCount;

		/**
		 * @param id
		 * @param value
		 */
		public RefRec(Object id, Object value) {
			this.id = id;
			this.value = value;
			addRef( );
		}

		/**
		 * @return Object
		 */
		public Object getId() {
			return id;
		}

		/**
		 * @return Object
		 */
		public Object getValue() {
			return value;
		}

		/**
		 * @return int
		 */
		public int addRef() {
			++refCount;
			return refCount;
		}

		/**
		 * @return int
		 */
		public int removeRef() {
			--refCount;
			return refCount;
		}

		/**
		 * @return int
		 */
		public int getRef() {
			return refCount;
		}

		/**
		 * @return boolean
		 */
		public boolean isNotReferenced() {
			return (refCount <= 0);
		}
	}

	/**
	 * Creates a new counter.<br>
	 */
	public ReferenceCounter() {
		super( );
	}

	/**
	 * Adds one reference to an object in the counter.<br>
	 * 
	 * @param id
	 *            is a unique ID for the object.
	 * @return int<br>
	 *         the new ref count
	 */
	public int addRef(Object id) {
		RefRec rec = mapIdToRec.get(id);
		if (rec == null) {
			return 0;
		}
		return rec.addRef( );
	}

	/**
	 * Returns the object defined by an ID. If the ID is not found<br>
	 * <code>null</code> is returned.<br>
	 * 
	 * @param id
	 * @return Object<br>
	 *         The object or <code>null</code>
	 */
	public Object get(Object id) {
		RefRec rec = mapIdToRec.get(id);
		if (rec == null) {
			return null;
		}
		return rec.getValue( );
	}

	/**
	 * Returns a complete list of the keys in the counter.<br>
	 * 
	 * @return Set<br>
	 *         A Set containing the ID for each.
	 */
	public Set<Object> keySet() {
		return mapIdToRec.keySet( );
	}

	/**
	 * Adds an object to the counter for counting and gives it an<br>
	 * initial ref count of 1.<br>
	 * 
	 * @param id
	 *            is a unique ID for the object.
	 * @param value
	 *            is the object itself.
	 */
	public void put(Object id, Object value) {
		RefRec rec = new RefRec(id, value);
		mapIdToRec.put(id, rec);
	}

	/**
	 * Removes one reference from an object in the counter. If the ref<br>
	 * count drops to 0 the object is removed from the counter<br>
	 * completely.<br>
	 * 
	 * @param id
	 *            is a unique ID for the object.
	 * @return int<br>
	 *         The new ref count
	 */
	public int removeRef(Object id) {
		RefRec rec = mapIdToRec.get(id);
		if (rec == null) {
			return 0;
		}
		int newCount = rec.removeRef( );
		if (newCount <= 0) {
			mapIdToRec.remove(id);
		}
		return newCount;
	}

	/**
	 * Returns a complete list of the values in the counter.<br>
	 * 
	 * @return List<br>
	 *         A Collection containing the values.
	 */
	public List<Object> values() {
		int size = mapIdToRec.size( );
		ArrayList<Object> list = new ArrayList<Object>(size);
		for(RefRec rec : mapIdToRec.values( )){
			list.add(rec.getValue( ));
		}
		return list;
	}
}
