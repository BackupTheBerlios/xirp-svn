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
 * XPListItem.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 22.10.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.custom;

import com.swtplus.widgets.PListItem;
import com.swtplus.widgets.list.TitleAndDescListStrategy;

import de.xirp.ui.widgets.custom.XPList.ListType;
import de.xirp.util.I18n;
import de.xirp.util.II18nHandler;

/**
 * This widget is enabled with on-the-fly translation and on-the-fly
 * color change capabilities.
 * 
 * @author Matthias Gernand
 */
public class XPListItem extends PListItem {

	/**
	 * The text key.
	 */
	private String textKey;
	/**
	 * The text args.
	 */
	private Object[] objects;
	/**
	 * The {@link de.xirp.util.II18nHandler handler}.
	 * 
	 * @see de.xirp.util.II18nHandler
	 */
	private II18nHandler handler;
	/**
	 * The parent list.
	 */
	private XPList parent;
	/**
	 * The description key.
	 */
	private String descriptionKey;
	/**
	 * The description args.
	 */
	private Object[] descriptionObjects;

	/**
	 * Constructs a new list item. This constructor should be used in
	 * application-UI environment.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 * @see com.swtplus.widgets.PListItem
	 */
	public XPListItem(XPList parent, int style) {
		super(parent, style);
		this.handler = I18n.getGenericI18n( );
		this.parent = parent;
	}

	/**
	 * Constructs a new list item. This constructor should be used in
	 * plugin-UI environment, because the
	 * {@link de.xirp.util.II18nHandler handler} is
	 * needed for translations.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 * @param handler
	 *            The handler.
	 * @see com.swtplus.widgets.PListItem
	 * @see de.xirp.util.II18nHandler
	 */
	public XPListItem(XPList parent, int style, II18nHandler handler) {
		super(parent, style);
		this.handler = handler;
		this.parent = parent;
	}

	/**
	 * Overridden setText method. This method gets the translation of
	 * the given string and sets the new text in the actual language.
	 * 
	 * @param key
	 *            key used for translation
	 * @param objects
	 *            arguments used for translation
	 */
	public void setTextForLocaleKey(String key, Object... objects) {
		textKey = key;
		this.objects = objects;
		if (handler != null) {
			super.setText(handler.getString(textKey, objects));
		}
	}

	/**
	 * This method must be called from the list for each of its items
	 * to make sure the texts can be translated on-the-fly. Workaround
	 * for buggy SWTPlus.
	 */
	protected void localeChanged() {
		if (textKey != null) {
			if (objects != null) {
				setTextForLocaleKey(textKey, objects);
			}
			else {
				setTextForLocaleKey(textKey);
			}
		}
		if (descriptionKey != null) {
			if (descriptionObjects != null) {
				setDescriptionForLocaleKey(descriptionKey, descriptionObjects);
			}
			else {
				setDescriptionForLocaleKey(descriptionKey);
			}
		}
	}

	/**
	 * Returns the description of the item.
	 * 
	 * @return The description.
	 */
	public String getDescription() {
		if (parent.getType( ) == ListType.TITLE_AND_DESC) {
			return (String) this.getData(TitleAndDescListStrategy.DESCRIPTION);
		}
		else {
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * Sets the description of the item.
	 * 
	 * @param description
	 *            The description to set.
	 */
	public void setDescription(String description) {
		if (parent.getType( ) == ListType.TITLE_AND_DESC) {
			this.setData(TitleAndDescListStrategy.DESCRIPTION, description);
		}
	}

	/**
	 * Sets the description using the given key and args.
	 * 
	 * @param key
	 *            The description key.
	 * @param objects
	 *            The arguments.
	 */
	public void setDescriptionForLocaleKey(String key, Object... objects) {
		descriptionKey = key;
		this.descriptionObjects = objects;
		if (handler != null) {
			if (parent.getType( ) == ListType.TITLE_AND_DESC) {
				String string = handler.getString(descriptionKey, objects);
				this.setData(TitleAndDescListStrategy.DESCRIPTION, string);
			}
		}
	}
}
