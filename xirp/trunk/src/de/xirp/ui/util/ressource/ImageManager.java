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
 * ImageManager.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   Matthias Gernand [matthias.gernand AT gmx.de]
 *
 * Changes
 * -------
 * 04.06.2006:		Created by Rabea Gransberger.
 */
package de.xirp.ui.util.ressource;

import java.io.File;
import java.util.Map;

import javolution.util.FastMap;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;

import de.xirp.managers.AbstractManager;
import de.xirp.managers.ManagerException;
import de.xirp.plugin.IPlugable;
import de.xirp.ui.util.SWTUtil;
import de.xirp.util.Constants;

/**
 * Manages the resources for images in the application.
 * 
 * @author Rabea Gransberger
 * @author Matthias Gernand
 */
public final class ImageManager extends AbstractManager {

	/**
	 * Enumeration of the images which are used in the application,
	 * and can be used in plugins.
	 */
	public enum SystemImage {
		/**
		 * 
		 */
		ADD_REMOVE,
		/**
		 * 
		 */
		APPEARANCE,
		/**
		 * 
		 */
		ARROW_UP_SMALL,
		/**
		 * 
		 */
		ARROW_DOWN_SMALL,
		/**
		 * 
		 */
		ARROW_LEFT_SMALL,
		/**
		 * 
		 */
		ARROW_RIGHT_SMALL,
		/**
		 * 
		 */
		ARROW_BACK,
		/**
		 * 
		 */
		BATTERY,
		/**
		 * 
		 */
		CONNECTED,
		/**
		 * 
		 */
		CONNECTING,
		/**
		 * 
		 */
		DATABASE,
		/**
		 * 
		 */
		DATABASE_START_RECORD,
		/**
		 * 
		 */
		DATABASE_STOP_RECORD,
		/**
		 * 
		 */
		DONE,
		/**
		 * 
		 */
		GAMEPAD_EDIT,
		/**
		 * 
		 */
		PROFILE_EDIT,
		/**	
		 * 
		 */
		VIEW_EDIT,
		/**
		 * 
		 */
		FILE_OPEN,
		/**
		 * 
		 */
		FILE_SAVE_AS,
		/**
		 * 
		 */
		ARROW_FORWARD,
		/**
		 * 
		 */
		GENERAL,
		/**
		 * 
		 */
		HOME,
		/**
		 * 
		 */
		INFO,
		/**
		 * 
		 */
		GAMEPAD_BIG,
		/**
		 * 
		 */
		GAMEPAD_SMALL,
		/**
		 * 
		 */
		KEYS,
		/**
		 * 
		 */
		WORLD,
		/**
		 * 
		 */
		MAGNIFY,
		/**
		 * 
		 */
		MINIMIZE,
		/**
		 * 
		 */
		MINUS,
		/**
		 * 
		 */
		GAMEPAD_NEW,
		/**
		 * 
		 */
		PROFILE_NEW,
		/**
		 * 
		 */
		VIEW_NEW,
		/**
		 * 
		 */
		NOT_DONE,
		/**
		 * 
		 */
		NOT_CONNECTED,
		/**
		 * 
		 */
		PAUSE,
		/**
		 * 
		 */
		PAUSE_DISABLED,
		/**
		 * 
		 */
		PLUS,
		/**
		 * 
		 */
		PREFERENCES,
		/**
		 * 
		 */
		PROFILE,
		/**
		 * 
		 */
		PROFILE_SMALL,
		/**
		 * 
		 */
		PLUGIN,
		/**
		 * 
		 */
		QUESTION,
		/**
		 * 
		 */
		QUESTION_BIG,
		/**
		 * 
		 */
		QUIT,
		/**
		 * 
		 */
		ROBOT_HEAD,
		/**
		 * 
		 */
		ROBOT_HEAD_BIG,
		/**
		 * 
		 */
		RUNNING,
		/**
		 * 
		 */
		NOT_RUNNING,
		/**
		 * 
		 */
		RUNNING_UNKNOWN,
		/**
		 * 
		 */
		SAVE,
		/**
		 * 
		 */
		SEARCH,
		/**
		 * 
		 */
		START,
		/**
		 * 
		 */
		START_DISABLED,
		/**
		 * 
		 */
		START_STOP,
		/**
		 * 
		 */
		STOP,
		/**
		 * 
		 */
		STOP_DISABLED,
		/**
		 * 
		 */
		TRAY,
		/**
		 * 
		 */
		WINDOW,
		/**
		 * 
		 */
		MALE,
		/**
		 * 
		 */
		FEMALE,
		/**
		 * 
		 */
		UNDEF_GENDER,
		/**
		 * 
		 */
		MAIL,
		/**
		 * 
		 */
		SEND_MAIL,
		/**
		 * 
		 */
		CONTACTS,
		/**
		 * 
		 */
		DELETE,
		/**
		 * 
		 */
		PRINT,
		/**
		 * 
		 */
		FORWARD_MAIL,
		/**
		 * 
		 */
		ATTACHMENT,
		/**
		 * 
		 */
		ADD_CONTACT,
		/**
		 * 
		 */
		DONE_SMALL,
		/**
		 * 
		 */
		NOT_DONE_SMALL,
		/**
		 * 
		 */
		UNKNOWN_SMALL,
		/**
		 * 
		 */
		WARNING_SMALL,
		/**
		 * 
		 */
		DIALOG_WARNING,
		/**
		 * 
		 */
		DIALOG_QUESTION,
		/**
		 * 
		 */
		DIALOG_ERROR,
		/**
		 * 
		 */
		ABOUT,
		/**
		 * 
		 */
		REAL_MODE,
		/**
		 * 
		 */
		VIRTUAL_MODE,
		/**
		 * 
		 */
		SPEECH,
		/**
		 * 
		 */
		CHART,
		/**
		 * 
		 */
		CHART_SMALL,
		/**
		 * 
		 */
		ADD,
		/**
		 * 
		 */
		REMOVE,
		/**
		 * 
		 */
		IMAGE,
		/**
		 * 
		 */
		PDF,
		/**
		 * 
		 */
		CSV,
		/**
		 * 
		 */
		TESTERBOT,
		/**
		 * 
		 */
		NEW_ROBOT,
		/**
		 * 
		 */
		EDIT_ROBOT,
		/**
		 * 
		 */
		EDIT_COMM_SPEC,
		/**
		 * 
		 */
		NEW_COMM_SPEC,
		/**
		 * 
		 */
		ABSOLUTE,
		/**
		 * 
		 */
		RELATIVE;

		/**
		 * Gets the filename for this system image
		 * 
		 * @return the filename for this system image
		 */
		protected String getFilename() {
			switch (this) {
				case ADD:
					return "add.png"; //$NON-NLS-1$
				case REMOVE:
					return "remove.png"; //$NON-NLS-1$
				case ADD_REMOVE:
					return "add_remove.png"; //$NON-NLS-1$
				case APPEARANCE:
					return "appearance.png"; //$NON-NLS-1$
				case ARROW_UP_SMALL:
					return "arrow_up_small.png"; //$NON-NLS-1$
				case ARROW_DOWN_SMALL:
					return "arrow_down_small.png"; //$NON-NLS-1$
				case ARROW_LEFT_SMALL:
					return "arrow_left_small.png"; //$NON-NLS-1$
				case ARROW_RIGHT_SMALL:
					return "arrow_right_small.png"; //$NON-NLS-1$
				case ARROW_BACK:
					return "back.png"; //$NON-NLS-1$
				case BATTERY:
					return "batt.png"; //$NON-NLS-1$
				case CONNECTED:
					return "connected.png"; //$NON-NLS-1$
				case CONNECTING:
					return "connecting.png"; //$NON-NLS-1$
				case DATABASE:
					return "database.png"; //$NON-NLS-1$
				case DATABASE_START_RECORD:
					return "record_db.png"; //$NON-NLS-1$
				case DATABASE_STOP_RECORD:
					return "stop_record_db.png"; //$NON-NLS-1$
				case DONE:
					return "done.png"; //$NON-NLS-1$
				case GAMEPAD_EDIT:
					return "editjoypad.png"; //$NON-NLS-1$
				case PROFILE_EDIT:
					return "editprofile.png"; //$NON-NLS-1$
				case VIEW_EDIT:
					return "editview.png"; //$NON-NLS-1$
				case FILE_OPEN:
					return "fileopen.png"; //$NON-NLS-1$
				case FILE_SAVE_AS:
					return "filesaveas.png"; //$NON-NLS-1$
				case ARROW_FORWARD:
					return "forward.png"; //$NON-NLS-1$
				case GENERAL:
					return "general.png"; //$NON-NLS-1$
				case TRAY:
					return "tray.png"; //$NON-NLS-1$
				case HOME:
					return "home.png"; //$NON-NLS-1$
				case INFO:
					return "info.png"; //$NON-NLS-1$
				case GAMEPAD_BIG:
					return "joypad_big.png"; //$NON-NLS-1$
				case GAMEPAD_SMALL:
					return "joypad_small.png"; //$NON-NLS-1$
				case KEYS:
					return "keybindings.png"; //$NON-NLS-1$
				case WORLD:
					return "language.png"; //$NON-NLS-1$
				case MAGNIFY:
					return "magnify.png"; //$NON-NLS-1$
				case MINIMIZE:
					return "minimize.png"; //$NON-NLS-1$
				case MINUS:
					return "minus.png"; //$NON-NLS-1$
				case GAMEPAD_NEW:
					return "newjoypad.png"; //$NON-NLS-1$
				case PROFILE_NEW:
					return "newprofile.png"; //$NON-NLS-1$
				case VIEW_NEW:
					return "newview.png"; //$NON-NLS-1$
				case NOT_DONE:
					return "notdone.png"; //$NON-NLS-1$
				case NOT_CONNECTED:
					return "not_connected.png"; //$NON-NLS-1$
				case PAUSE:
					return "pause.png"; //$NON-NLS-1$
				case PAUSE_DISABLED:
					return "pause_disabled.png"; //$NON-NLS-1$
				case PLUS:
					return "plus.png"; //$NON-NLS-1$
				case PREFERENCES:
					return "preferences.png"; //$NON-NLS-1$
				case PROFILE:
					return "profiles.png"; //$NON-NLS-1$
				case PROFILE_SMALL:
					return "profiles_small.png"; //$NON-NLS-1$
				case PLUGIN:
					return "puzzle.png"; //$NON-NLS-1$
				case QUESTION:
					return "question.png"; //$NON-NLS-1$
				case QUESTION_BIG:
					return "question_big.png"; //$NON-NLS-1$
				case QUIT:
					return "quit.png"; //$NON-NLS-1$
				case NOT_RUNNING:
					return "not_running.png"; //$NON-NLS-1$
				case ROBOT_HEAD:
					return "robot_head.png"; //$NON-NLS-1$
				case RUNNING:
					return "running.png"; //$NON-NLS-1$
				case RUNNING_UNKNOWN:
					return "running_unknown.png"; //$NON-NLS-1$
				case SAVE:
					return "save.png"; //$NON-NLS-1$
				case SEARCH:
					return "search.png"; //$NON-NLS-1$
				case START:
					return "start.png"; //$NON-NLS-1$
				case START_DISABLED:
					return "start_disabled.png"; //$NON-NLS-1$
				case START_STOP:
					return "start_stop.png"; //$NON-NLS-1$
				case STOP:
					return "stop.png"; //$NON-NLS-1$
				case STOP_DISABLED:
					return "stop_disabled.png"; //$NON-NLS-1$
				case WINDOW:
					return "window.png"; //$NON-NLS-1$
				case MALE:
					return "male.png"; //$NON-NLS-1$
				case FEMALE:
					return "female.png"; //$NON-NLS-1$
				case UNDEF_GENDER:
					return "undefined_gender.png"; //$NON-NLS-1$		
				case MAIL:
					return "mail.png"; //$NON-NLS-1$
				case SEND_MAIL:
					return "send_mail.png"; //$NON-NLS-1$
				case CONTACTS:
					return "contacts.png"; //$NON-NLS-1$	
				case DELETE:
					return "delete.png"; //$NON-NLS-1$	
				case PRINT:
					return "print.png"; //$NON-NLS-1$			
				case FORWARD_MAIL:
					return "forward_mail.png"; //$NON-NLS-1$	
				case ATTACHMENT:
					return "attach.png"; //$NON-NLS-1$	
				case ADD_CONTACT:
					return "add_contact.png"; //$NON-NLS-1$	
				case DONE_SMALL:
					return "done_small.png"; //$NON-NLS-1$	
				case NOT_DONE_SMALL:
					return "not_done_small.png"; //$NON-NLS-1$	
				case UNKNOWN_SMALL:
					return "unknown_small.png"; //$NON-NLS-1$	
				case WARNING_SMALL:
					return "warning_small.png"; //$NON-NLS-1$	
				case DIALOG_WARNING:
					return "dialog_warning.png"; //$NON-NLS-1$	
				case DIALOG_QUESTION:
					return "dialog_question.png"; //$NON-NLS-1$	
				case DIALOG_ERROR:
					return "dialog_error.png"; //$NON-NLS-1$
				case ABOUT:
					return "about.png"; //$NON-NLS-1$	
				case REAL_MODE:
					return "real_mode.png"; //$NON-NLS-1$	
				case VIRTUAL_MODE:
					return "virtual_mode.png"; //$NON-NLS-1$	
				case SPEECH:
					return "speech.png"; //$NON-NLS-1$	
				case CHART:
					return "chart.png"; //$NON-NLS-1$	+
				case CHART_SMALL:
					return "chart_small.png"; //$NON-NLS-1$			
				case IMAGE:
					return "image.png"; //$NON-NLS-1$		
				case PDF:
					return "pdf.png"; //$NON-NLS-1$		
				case CSV:
					return "csv.png"; //$NON-NLS-1$		
				case TESTERBOT:
					return "testerbot.png"; //$NON-NLS-1$
				case ROBOT_HEAD_BIG:
					return "testerbot.png"; //$NON-NLS-1$
				case NEW_ROBOT:
					return "new_robot.png"; //$NON-NLS-1$
				case EDIT_ROBOT:
					return "edit_robot.png"; //$NON-NLS-1$
				case NEW_COMM_SPEC:
					return "new_commspec.png"; //$NON-NLS-1$
				case EDIT_COMM_SPEC:
					return "edit_commspec.png"; //$NON-NLS-1$	
				case ABSOLUTE:
					return "absolute.png"; //$NON-NLS-1$	
				case RELATIVE:
					return "relative.png"; //$NON-NLS-1$						
				default:
					return ""; //$NON-NLS-1$
			}
		}

		/**
		 * Gets a description for this system image
		 * 
		 * @return the description of this system image
		 */
		protected String getDescription() {
			switch (this) {
				case ADD_REMOVE:
					return "add and remove icon in one"; //$NON-NLS-1$
				case APPEARANCE:
					return "green, red, blue cubes on each other"; //$NON-NLS-1$
				case ARROW_UP_SMALL:
					return "very small arrow down"; //$NON-NLS-1$
				case ARROW_DOWN_SMALL:
					return "very small arrow up"; //$NON-NLS-1$
				case ARROW_BACK:
					return "blue arrow pointing to the left"; //$NON-NLS-1$
				case BATTERY:
					return "icon showing a battery"; //$NON-NLS-1$
				case CONNECTED:
					return "icon for established connection"; //$NON-NLS-1$
				case CONNECTING:
					return "icon when establishing connection"; //$NON-NLS-1$
				case DATABASE:
					return "yellow icon showing three stacked slices"; //$NON-NLS-1$
				case DATABASE_START_RECORD:
					return "red filled circle over db icon"; //$NON-NLS-1$
				case DATABASE_STOP_RECORD:
					return "stop icon over db icon"; //$NON-NLS-1$
				case DONE:
					return "green filled circle"; //$NON-NLS-1$
				case GAMEPAD_EDIT:
					return "gamepad icon with toothed wheel"; //$NON-NLS-1$
				case PROFILE_EDIT:
					return "profile icon with toothed wheel"; //$NON-NLS-1$
				case VIEW_EDIT:
					return "window icon with toothed wheel"; //$NON-NLS-1$
				case FILE_OPEN:
					return "standard file open icon in blue"; //$NON-NLS-1$
				case FILE_SAVE_AS:
					return "standard file save as icon in blue"; //$NON-NLS-1$
				case ARROW_FORWARD:
					return "blue arrow pointing to the right"; //$NON-NLS-1$
				case GENERAL:
					return "small gray toothed wheel"; //$NON-NLS-1$
				case TRAY:
					return "stylish blue icon showing a thumb up"; //$NON-NLS-1$
				case HOME:
					return "chinese house"; //$NON-NLS-1$
				case INFO:
					return "lighted yellow lamp"; //$NON-NLS-1$
				case GAMEPAD_BIG:
					return "big gamepad icon"; //$NON-NLS-1$
				case GAMEPAD_SMALL:
					return "small gamepad icon"; //$NON-NLS-1$
				case KEYS:
					return "some keys"; //$NON-NLS-1$
				case WORLD:
					return "icon showing the earth"; //$NON-NLS-1$
				case MAGNIFY:
					return "icon showing small and big marked window"; //$NON-NLS-1$
				case MINIMIZE:
					return "icon showing small marked and big window"; //$NON-NLS-1$
				case MINUS:
					return "blue minus sign"; //$NON-NLS-1$
				case GAMEPAD_NEW:
					return "gamepad icon with a plus"; //$NON-NLS-1$
				case PROFILE_NEW:
					return "profile icon with a plus"; //$NON-NLS-1$
				case VIEW_NEW:
					return "window icon with a plus"; //$NON-NLS-1$
				case NOT_DONE:
					return "red filled circle"; //$NON-NLS-1$
				case NOT_CONNECTED:
					return "icon showing a light gray plug connector"; //$NON-NLS-1$
				case PAUSE:
					return "standard pause icon"; //$NON-NLS-1$
				case PAUSE_DISABLED:
					return "disabled pause icon"; //$NON-NLS-1$
				case PLUS:
					return "blue plus sign"; //$NON-NLS-1$
				case PREFERENCES:
					return "icon with two slices and a toothed wheel"; //$NON-NLS-1$
				case PROFILE:
					return "profile icon (paper with lines)"; //$NON-NLS-1$
				case PROFILE_SMALL:
					return "small profile icon (paper with lines)"; //$NON-NLS-1$
				case PLUGIN:
					return "puzzle icon used for plugins"; //$NON-NLS-1$
				case QUESTION:
					return "blue-gray 3D question mark"; //$NON-NLS-1$
				case QUESTION_BIG:
					return "big blue-gray 3D question mar"; //$NON-NLS-1$
				case QUIT:
					return "standard quit icon in red"; //$NON-NLS-1$
				case NOT_RUNNING:
					return "red circle"; //$NON-NLS-1$
				case ROBOT_HEAD:
					return "A robot head"; //$NON-NLS-1$
				case RUNNING:
					return "small filled green circle"; //$NON-NLS-1$
				case RUNNING_UNKNOWN:
					return "small filled blue circle"; //$NON-NLS-1$
				case SAVE:
					return "standard save icon in blue"; //$NON-NLS-1$
				case START:
					return "Icon indicating a search operation"; //$NON-NLS-1$
				case SEARCH:
					return "standard start icon (green)"; //$NON-NLS-1$
				case START_DISABLED:
					return "disabled start icon"; //$NON-NLS-1$
				case START_STOP:
					return "start and stop icon in one"; //$NON-NLS-1$
				case STOP:
					return "standard stop icon (red cube)"; //$NON-NLS-1$
				case STOP_DISABLED:
					return "disabled stop icon"; //$NON-NLS-1$
				case WINDOW:
					return "blue icon for windows"; //$NON-NLS-1$
				default:
					return ""; //$NON-NLS-1$
			}
		}

	}

	/**
	 * The Logger for this class.
	 */
	private static Logger logClass = Logger.getLogger(ImageManager.class);

	/**
	 * Base path for all icons
	 */
	private static String BASE_PATH = Constants.SYSTEM_ICON_DIR;

	/**
	 * Base path for all plugin icons
	 */
	public static final String PLUGIN_BASE_PATH = Constants.ICON_DIR
			+ File.separator + "plugins"; //$NON-NLS-1$

	/**
	 * Information about all system images
	 */
	private static Map<SystemImage, ImageInfo> systemImageInfo = new FastMap<SystemImage, ImageInfo>( ).setShared(true);

	/**
	 * Map for images which were requested with their full absolute
	 * path
	 */
	private static Map<String, Image> fullPathImages = new FastMap<String, Image>( ).setShared(true);

	/**
	 * Prefix for images which are for an file extension
	 */
	private static final String EXTENSION_PREFIX = "extension_"; //$NON-NLS-1$
	/**
	 * Object used for synchronization when creating new images
	 */
	private static Object lock = new Object( );

	/**
	 * Creates a new manager for images. The manager is initialized on
	 * startup. Never call this on your own. Use the statically
	 * provided methods.
	 * 
	 * @throws InstantiationException
	 *             if an instance of this manager does already exist
	 */
	public ImageManager() throws InstantiationException {
		super( );
	}

	/**
	 * Creates a new image from the given path.
	 * 
	 * @param path
	 *            path of the image
	 * @return a new image
	 */
	private static Image getImageCopy(String path) {
		Image img = null;
		try {
			img = new Image(Display.getDefault( ), path);
		}
		catch (SWTException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR); //$NON-NLS-1$
		}
		catch (SWTError e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR); //$NON-NLS-1$
		}
		catch (IllegalArgumentException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR); //$NON-NLS-1$
		}
		return img;
	}

	/**
	 * Gets an image for the given path. Creating a new image is
	 * synchronized.
	 * 
	 * @param path
	 *            the absolute path to the image
	 * @return the image or <code>null</code> if there's no image
	 *         for the given path
	 */
	private static Image getImage(String path) {
		Image img = fullPathImages.get(path);
		if (!SWTUtil.swtAssert(img)) {
			synchronized (lock) {
				// double check to prevent creating an image
				// which does exist now, because
				// the current thread had to wait
				// at the lock while another thread created the image
				img = fullPathImages.get(path);
				if (SWTUtil.swtAssert(img)) {
					return img;
				}
				img = getImageCopy(path);
				if (img != null) {
					// dispose any old listed images
					// This should not happen due to synchronization
					// but we really don't want to have memory leaks
					Image old = fullPathImages.put(path, img);
					SWTUtil.secureDispose(old);
				}
			}
		}
		return img;
	}

	/**
	 * Gets a new image for the given path. You have to dispose this
	 * image on your own. It's not disposed automatically when the
	 * application exits.
	 * 
	 * @param path
	 *            the absolute path to the image
	 * @return a new image for the given path
	 */
	public static Image getFullPathImageCopy(String path) {
		return getImageCopy(path);
	}

	/**
	 * Gets an image for the given path. If the images does already
	 * exist, the image is returned, otherwise it's created. This icon
	 * is used by other resources also and is disposed when the
	 * application exits. Leave the icon as it is. Don't draw on it or
	 * dispose it by yourself.
	 * 
	 * @param path
	 *            the absolute path to the image
	 * @return the image
	 */
	public static Image getFullPathImage(String path) {
		return getImage(path);
	}

	/**
	 * Gets the icon for the given system image. This icon is used by
	 * other resources also and is disposed when the application
	 * exits. Leave the icon as it is. Don't draw on it or dispose it
	 * by yourself.
	 * 
	 * @param type
	 *            the system image to get
	 * @return an system image
	 */
	public static Image getSystemImage(SystemImage type) {
		return getImage(BASE_PATH + File.separator + type.getFilename( ));
	}

	/**
	 * Gets an copy of the given system image, which is not disposed
	 * automatically and not used by any other resources.
	 * 
	 * @param type
	 *            the system image to get
	 * @return a copy of the given system image
	 */
	public static Image getSystemImageCopy(SystemImage type) {
		return getImageCopy(BASE_PATH + File.separator + type.getFilename( ));
	}

	/**
	 * Gets the information about the given system image, containing a
	 * description and size of the image
	 * 
	 * @param type
	 *            the system image to get the information for
	 * @return information about the given system image
	 */
	public static ImageInfo getSystemImageInfo(SystemImage type) {
		ImageInfo info = systemImageInfo.get(type);
		if (info == null) {
			Image img = getSystemImage(type);
			if (img != null) {
				info = new ImageInfo(type,
						type.getDescription( ),
						new Point(img.getImageData( ).width,
								img.getImageData( ).height));
				systemImageInfo.put(type, info);
			}
		}
		return info;
	}

	/**
	 * Gets an icon of the given name for the given plugin (s main
	 * class). This icon is used by other resources also and is
	 * disposed when the application exits. Leave the icon as it is.
	 * Don't draw on it or dispose it by yourself.
	 * 
	 * @param mainClass
	 *            the mainClass of the plugin
	 * @param name
	 *            the relative path of the plugin icon
	 * @return the image for the given name or <code>null</code> if
	 *         no image is found
	 */
	public static Image getPluginImage(String mainClass, String name) {
		return getImage(getPluginImagePath(mainClass, name));
	}

	/**
	 * Gets the path to an image of a plugin of the given name.
	 * 
	 * @param mainClass
	 *            the main class of the plugin
	 * @param name
	 *            the name of the image with extension
	 * @return the absolute path to the plugin's image.
	 */
	public static String getPluginImagePath(String mainClass, String name) {
		return PLUGIN_BASE_PATH + File.separator + mainClass + File.separator
				+ name;
	}

	/**
	 * Gets a copy of the plugin icon. This image is not used by any
	 * other resources and you have to dispose it, if it's not used
	 * any more.
	 * 
	 * @param mainClass
	 *            the mainClass of the plugin
	 * @param name
	 *            the relative path of the plugin icon
	 * @return the image for the given name or <code>null</code> if
	 *         no image is found
	 */
	public static Image getPluginImageCopy(String mainClass, String name) {
		return getImageCopy(PLUGIN_BASE_PATH + File.separator + mainClass
				+ File.separator + name);
	}

	/**
	 * Gets an icon of the given name for the given plugin (s main
	 * class). This icon is used by other resources also and is
	 * disposed when the application exits. Leave the icon as it is.
	 * Don't draw on it or dispose it by yourself.
	 * 
	 * @param clazz
	 *            the mainClass of the plugin
	 * @param name
	 *            the relative path of the plugin icon
	 * @return the image for the given name or <code>null</code> if
	 *         no image is found
	 */
	public static Image getPluginImage(Class<?> clazz, String name) {
		return getPluginImage(clazz.getName( ), name);
	}

	/**
	 * Gets the path to an image of a plugin of the given name.
	 * 
	 * @param clazz
	 *            the main class of the plugin
	 * @param name
	 *            the name of the image with extension
	 * @return the absolute path to the plugin's image.
	 */
	public static String getPluginImagePath(Class<?> clazz, String name) {
		return getPluginImagePath(clazz.getName( ), name);
	}

	/**
	 * Gets a copy of the plugin icon. This image is not used by any
	 * other resources and you have to dispose it, if it's not used
	 * any more.
	 * 
	 * @param clazz
	 *            the mainClass of the plugin
	 * @param name
	 *            the relative path of the plugin icon
	 * @return the image for the given name or <code>null</code> if
	 *         no image is found
	 */
	public static Image getPluginImageCopy(Class<?> clazz, String name) {
		return getPluginImageCopy(clazz.getName( ), name);
	}

	/**
	 * Gets an icon of the given name for the given plugin. This icon
	 * is used by other resources also and is disposed when the
	 * application exits. Leave the icon as it is. Don't draw on it or
	 * dispose it by yourself.
	 * 
	 * @param plugin
	 *            the plugin
	 * @param name
	 *            the relative path of the plugin icon
	 * @return the image for the given name or <code>null</code> if
	 *         no image is found
	 */
	@SuppressWarnings("unchecked")
	public static Image getPluginImage(IPlugable plugin, String name) {
		return getPluginImage(plugin.getInfo( ).getMainClass( ), name);
	}

	/**
	 * Gets the path to an image of a plugin of the given name.
	 * 
	 * @param plugin
	 *            the plugin
	 * @param name
	 *            the name of the image with extension
	 * @return the absolute path to the plugin's image.
	 */
	@SuppressWarnings("unchecked")
	public static String getPluginImagePath(IPlugable plugin, String name) {
		return getPluginImagePath(plugin.getInfo( ).getMainClass( ), name);
	}

	/**
	 * Gets a copy of the plugin icon. This image is not used by any
	 * other resources and you have to dispose it, if it's not used
	 * any more.
	 * 
	 * @param plugin
	 *            the plugin
	 * @param name
	 *            the relative path of the plugin icon
	 * @return the image for the given name or <code>null</code> if
	 *         no image is found
	 */
	@SuppressWarnings("unchecked")
	public static Image getPluginImageCopy(IPlugable plugin, String name) {
		return getPluginImageCopy(plugin.getInfo( ).getMainClass( ), name);
	}

	/**
	 * Gets an image for the default program used for viewing a file
	 * of the given file extension. For example calling this method
	 * with {@literal "pdf"} may give you the icon of the adobe
	 * acrobat reader if this is the default application for viewing
	 * PDFs.<br>
	 * <br>
	 * This icon is also used by other resources and is disposed when
	 * the application exits. Leave the icon as it is. Don't draw on
	 * it or dispose it by yourself.
	 * 
	 * @param extension
	 *            the extension of the file
	 * @return the icon of the default program used for opening a file
	 *         with the given extension or <code>null</code> if no
	 *         program for the given extension was found or if this
	 *         program does not have an icon.
	 */
	public static Image getImageForExtension(String extension) {

		String key = EXTENSION_PREFIX + extension;
		Image img = fullPathImages.get(key);
		if (SWTUtil.swtAssert(img)) {
			return img;
		}

		Program prog = Program.findProgram(extension);
		if (prog != null) {
			ImageData imageData = prog.getImageData( );
			if (imageData != null) {
				img = new Image(Display.getDefault( ), imageData);
				fullPathImages.put(key, img);
				return img;
			}
		}
		return null;
	}

	/**
	 * Gets an scaled image for the default program used for viewing a
	 * file of the given file extension. For example calling this
	 * method with {@literal "pdf"} may give you the icon of the adobe
	 * acrobat reader if this is the default application for viewing
	 * PDFs.<br>
	 * <br>
	 * This icon is also used by other resources and is disposed when
	 * the application exits. Leave the icon as it is. Don't draw on
	 * it or dispose it by yourself.
	 * 
	 * @param extension
	 *            the extension of the file
	 * @param width
	 *            the desired width of the resulting image
	 * @param height
	 *            the desired height of the resulting image
	 * @return the icon of the default program used for opening a file
	 *         with the given extension or <code>null</code> if no
	 *         program for the given extension was found or if this
	 *         program does not have an icon.
	 */
	public static Image getImageForExtension(String extension, int width,
			int height) {
		String key = EXTENSION_PREFIX + width + "_" + height + "_" + extension; //$NON-NLS-1$ //$NON-NLS-2$
		Image img = fullPathImages.get(key);
		if (SWTUtil.swtAssert(img)) {
			return img;
		}
		Program prog = Program.findProgram(extension);
		if (prog != null) {
			ImageData imageData = prog.getImageData( );
			if (imageData != null) {
				img = new Image(Display.getDefault( ),
						imageData.scaledTo(width, height));
				fullPathImages.put(key, img);
				return img;
			}
		}
		return null;
	}

	/**
	 * Flips the image at the horizontal or vertical axis.
	 * 
	 * @param srcData
	 *            the data of the image
	 * @param vertical
	 *            <code>true</code> for vertical, <code>false</code>
	 *            for horizontal axis
	 * @return the changed image data
	 */
	public static ImageData flipImage(ImageData srcData, boolean vertical) {
		int bytesPerPixel = srcData.bytesPerLine / srcData.width;
		int destBytesPerLine = srcData.width * bytesPerPixel;
		byte[] newData = new byte[srcData.data.length];
		for (int srcY = 0; srcY < srcData.height; srcY++) {
			for (int srcX = 0; srcX < srcData.width; srcX++) {
				int destX = 0, destY = 0, destIndex = 0, srcIndex = 0;
				if (vertical) {
					destX = srcX;
					destY = srcData.height - srcY - 1;
				}
				else {
					destX = srcData.width - srcX - 1;
					destY = srcY;
				}
				destIndex = (destY * destBytesPerLine)
						+ (destX * bytesPerPixel);
				srcIndex = (srcY * srcData.bytesPerLine)
						+ (srcX * bytesPerPixel);
				System.arraycopy(srcData.data,
						srcIndex,
						newData,
						destIndex,
						bytesPerPixel);
			}
		}
		// destBytesPerLine is used as scanlinePad to ensure that no
		// padding is required
		return new ImageData(srcData.width,
				srcData.height,
				srcData.depth,
				srcData.palette,
				destBytesPerLine,
				newData);
	}

	/**
	 * Rotates the image in the given direction.
	 * 
	 * @param srcData
	 *            the data of the image
	 * @param direction
	 *            direction for rotation which may be one of
	 *            {@link SWT#DOWN}, {@link SWT#LEFT} or
	 *            {@link SWT#RIGHT}.
	 * @return the changed image data
	 */
	public static ImageData rotateImage(ImageData srcData, int direction) {
		int bytesPerPixel = srcData.bytesPerLine / srcData.width;
		int destBytesPerLine = (direction == SWT.DOWN) ? srcData.width
				* bytesPerPixel : srcData.height * bytesPerPixel;
		byte[] newData = new byte[(direction == SWT.DOWN) ? srcData.height
				* destBytesPerLine : srcData.width * destBytesPerLine];
		int width = 0, height = 0;
		for (int srcY = 0; srcY < srcData.height; srcY++) {
			for (int srcX = 0; srcX < srcData.width; srcX++) {
				int destX = 0, destY = 0, destIndex = 0, srcIndex = 0;
				switch (direction) {
					case SWT.LEFT: // left 90 degrees
						destX = srcY;
						destY = srcData.width - srcX - 1;
						width = srcData.height;
						height = srcData.width;
						break;
					case SWT.RIGHT: // right 90 degrees
						destX = srcData.height - srcY - 1;
						destY = srcX;
						width = srcData.height;
						height = srcData.width;
						break;
					case SWT.DOWN: // 180 degrees
						destX = srcData.width - srcX - 1;
						destY = srcData.height - srcY - 1;
						width = srcData.width;
						height = srcData.height;
						break;
				}
				destIndex = (destY * destBytesPerLine)
						+ (destX * bytesPerPixel);
				srcIndex = (srcY * srcData.bytesPerLine)
						+ (srcX * bytesPerPixel);
				System.arraycopy(srcData.data,
						srcIndex,
						newData,
						destIndex,
						bytesPerPixel);
			}
		}
		// destBytesPerLine is used as scanlinePad to ensure that no
		// padding is required
		return new ImageData(width,
				height,
				srcData.depth,
				srcData.palette,
				destBytesPerLine,
				newData);
	}

	/**
	 * Zooms the image with the given factor.
	 * 
	 * @param src
	 *            the data of the image
	 * @param zoomFactor
	 *            the factor for zooming
	 * @return the zoomed image data
	 */
	public static ImageData zoomImage(ImageData src, double zoomFactor) {
		double newWidth = src.width * zoomFactor;
		double newHeight = src.height * zoomFactor;

		if (newWidth < 1) {
			newWidth = 1;
		}
		if (newHeight < 1) {
			newHeight = 1;
		}
		return src.scaledTo((int) newWidth, (int) newHeight);
	}

	/**
	 * Zooms the given image and returns a new image. The old image
	 * may be disposed by this method if requested.
	 * 
	 * @param src
	 *            the image to zoom
	 * @param zoomFactor
	 *            the factor for zooming
	 * @param disposeOld
	 *            <code>true</code> if the old image should be
	 *            disposed
	 * @return the zoomed image
	 */
	public static Image zoomImage(Image src, double zoomFactor,
			boolean disposeOld) {
		ImageData newData = ImageManager.zoomImage(src.getImageData( ),
				zoomFactor);
		Image newImage = new Image(src.getDevice( ), newData);
		if (disposeOld) {
			src.dispose( );
		}
		return newImage;
	}

	/**
	 * Does nothing at the moment.
	 * 
	 * @see de.xirp.managers.AbstractManager#start()
	 */
	@Override
	protected void start() throws ManagerException {
		super.start( );
	}

	/**
	 * Disposes all images, which were registered at this image
	 * manager.<br>
	 * 
	 * @see de.xirp.managers.AbstractManager#stop()
	 */
	@Override
	protected void stop() throws ManagerException {
		super.stop( );
		for (Image image : fullPathImages.values( )) {
			SWTUtil.secureDispose(image);
		}
		fullPathImages.clear( );
	}
}
