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
 * Contact.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 18.02.2007:		Created by Matthias Gernand.
 */
package de.xirp.mail;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map.Entry;

import de.xirp.util.I18n;
import de.xirp.util.collections.BidiHashMap;

/**
 * This class represents a contact. A object of this class contains
 * information about a person. It contains the e-mail address, the
 * name, phone number, gender and nick name of the person.
 * 
 * @author Matthias Gernand
 */
public final class Contact implements Serializable, Comparable<Contact> {

	/**
	 * The serial version UID of this serializable
	 */
	private static final long serialVersionUID = -8399648352134239585L;
	/**
	 * Short for "not available"
	 */
	public static final String NOT_AVAILABLE = I18n.getString("Contact.text.notAvailableShort"); //$NON-NLS-1$
	/**
	 * The first name of the person.
	 */
	private String firstName = NOT_AVAILABLE;
	/**
	 * The last name of the person.
	 */
	private String lastName = NOT_AVAILABLE;
	/**
	 * The nick name of the person.
	 */
	private String nickName = NOT_AVAILABLE;
	/**
	 * The department the person is working in.
	 */
	private String department = NOT_AVAILABLE;
	/**
	 * The phone number of the person.
	 */
	private String phone = NOT_AVAILABLE;
	/**
	 * The mail address of the person.
	 */
	private String mail = NOT_AVAILABLE;
	/**
	 * The gender of the person.
	 */
	private Gender gender = Gender.UNDEFINED;

	/**
	 * This enum contains constants for indicating the gender of a
	 * person.
	 * 
	 * @author Matthias Gernand
	 */
	public enum Gender {
		/**
		 * The person is male.
		 */
		MALE,
		/**
		 * The person is female.
		 */
		FEMALE,
		/**
		 * The gender is undefined. ;o)
		 */
		UNDEFINED;

		/**
		 * Bidi-map holding the translation keys for each gender.
		 */
		private static BidiHashMap<String, String> map = new BidiHashMap<String, String>( );
		static {
			map.put(MALE.name( ), "Contact.enum.Gender.male"); //$NON-NLS-1$
			map.put(FEMALE.name( ), "Contact.enum.Gender.female"); //$NON-NLS-1$
			map.put(UNDEFINED.name( ), "Contact.enum.Gender.undefined"); //$NON-NLS-1$
		}

		/**
		 * Returns the translated name of the gender.
		 * 
		 * @return The gender text.
		 */
		public String localeName() {
			return I18n.getString(map.get(this.name( )));
		}

		/**
		 * Returns the
		 * {@link de.xirp.mail.Contact.Gender} for the
		 * translated gender name.
		 * 
		 * @param translation
		 *            The translated gender name, f.e. male.
		 * @return The corresponding gender.
		 */
		public static Gender getGenderForTranslatedKey(String translation) {
			for (Entry<String, String> e : map.entrySet( )) {
				if (I18n.getString(e.getValue( )).equalsIgnoreCase(translation)) {
					return Gender.valueOf(e.getKey( ));
				}
			}
			return UNDEFINED;
		}

		/**
		 * Returns the keys for all 3 gender enum constants.
		 * 
		 * @return The name keys.
		 */
		public static Collection<String> localeNameKeys() {
			return map.values( );
		}
	}

	/**
	 * Returns the department of the
	 * {@link de.xirp.mail.Contact}.
	 * 
	 * @return The department
	 */
	public String getDepartment() {
		return department;
	}

	/**
	 * Sets the department of the
	 * {@link de.xirp.mail.Contact}.
	 * 
	 * @param department
	 *            The department to set.
	 */
	public void setDepartment(String department) {
		this.department = department;
	}

	/**
	 * Returns the first name of the
	 * {@link de.xirp.mail.Contact}.
	 * 
	 * @return The first name.
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Sets the first name of the
	 * {@link de.xirp.mail.Contact}.
	 * 
	 * @param firstName
	 *            The first name to set.
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Returns the last name of the
	 * {@link de.xirp.mail.Contact}.
	 * 
	 * @return The last name.
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Sets the last name of the
	 * {@link de.xirp.mail.Contact}.
	 * 
	 * @param lastName
	 *            The last name to set.
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Returns the mail address of the
	 * {@link de.xirp.mail.Contact}.
	 * 
	 * @return The mail address.
	 */
	public String getMail() {
		return mail;
	}

	/**
	 * Sets the mail address of the
	 * {@link de.xirp.mail.Contact}.
	 * 
	 * @param mail
	 *            The mail address to set.
	 */
	public void setMail(String mail) {
		this.mail = mail;
	}

	/**
	 * Returns the nick name of the
	 * {@link de.xirp.mail.Contact}.
	 * 
	 * @return The nick name.
	 */
	public String getNickName() {
		return nickName;
	}

	/**
	 * Sets the nick name of the
	 * {@link de.xirp.mail.Contact}.
	 * 
	 * @param nickName
	 *            The nick name to set.
	 */
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	/**
	 * Returns the phone number of the
	 * {@link de.xirp.mail.Contact}.
	 * 
	 * @return The phone number.
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * Sets the phone number of the
	 * {@link de.xirp.mail.Contact}.
	 * 
	 * @param phone
	 *            The phone number to set.
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * Compares on {@link de.xirp.mail.Contact} to
	 * another. The contacts are compared by the full name. The full
	 * name is created by concatenating: <br>
	 * <br>
	 * <code>lastName + ", " + firstName</code>. <br>
	 * <br>
	 * The this string are compared.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Contact contact) {
		String name = lastName + ", " + firstName; //$NON-NLS-1$
		return name.compareTo(contact.getLastName( ) + ", " //$NON-NLS-1$
				+ contact.getFirstName( ));
	}

	/**
	 * Returns the {@link de.xirp.mail.Contact.Gender} of
	 * the {@link de.xirp.mail.Contact}.
	 * 
	 * @return The gender.
	 * @see de.xirp.mail.Contact.Gender
	 */
	public Gender getGender() {
		return gender;
	}

	/**
	 * Sets the {@link de.xirp.mail.Contact.Gender} of
	 * the {@link de.xirp.mail.Contact}.
	 * 
	 * @param gender
	 *            The gender to set.
	 * @see de.xirp.mail.Contact.Gender
	 */
	public void setGender(Gender gender) {
		this.gender = gender;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass( ).getSimpleName( ) + " {" + getLastName( ) + ", " //$NON-NLS-1$ //$NON-NLS-2$
				+ getFirstName( ) + ", " + getMail( ) + "}"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result +
				((department == null) ? 0 : department.hashCode( ));
		result = PRIME * result +
				((firstName == null) ? 0 : firstName.hashCode( ));
		result = PRIME * result + ((gender == null) ? 0 : gender.hashCode( ));
		result = PRIME * result +
				((lastName == null) ? 0 : lastName.hashCode( ));
		result = PRIME * result + ((mail == null) ? 0 : mail.hashCode( ));
		result = PRIME * result +
				((nickName == null) ? 0 : nickName.hashCode( ));
		result = PRIME * result + ((phone == null) ? 0 : phone.hashCode( ));
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass( ) != obj.getClass( )) {
			return false;
		}
		final Contact other = (Contact) obj;
		if (department == null) {
			if (other.department != null) {
				return false;
			}
		}
		else if (!department.equals(other.department)) {
			return false;
		}
		if (firstName == null) {
			if (other.firstName != null) {
				return false;
			}
		}
		else if (!firstName.equals(other.firstName)) {
			return false;
		}
		if (gender == null) {
			if (other.gender != null) {
				return false;
			}
		}
		else if (!gender.equals(other.gender)) {
			return false;
		}
		if (lastName == null) {
			if (other.lastName != null) {
				return false;
			}
		}
		else if (!lastName.equals(other.lastName)) {
			return false;
		}
		if (mail == null) {
			if (other.mail != null) {
				return false;
			}
		}
		else if (!mail.equals(other.mail)) {
			return false;
		}
		if (nickName == null) {
			if (other.nickName != null) {
				return false;
			}
		}
		else if (!nickName.equals(other.nickName)) {
			return false;
		}
		if (phone == null) {
			if (other.phone != null) {
				return false;
			}
		}
		else if (!phone.equals(other.phone)) {
			return false;
		}
		return true;
	}
}
