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
 * TextToSpeechManager.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 05.03.2007:		Created by Matthias Gernand.
 * 15.03.2007:		Added support for mbrola voices.
 */
package de.xirp.speech;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

import de.xirp.managers.AbstractManager;
import de.xirp.managers.ManagerException;
import de.xirp.settings.PropertiesManager;
import de.xirp.ui.event.VoiceChangedEvent;
import de.xirp.ui.event.VoiceChangedListener;
import de.xirp.ui.util.ApplicationManager;
import de.xirp.util.Constants;
import de.xirp.util.I18n;
import de.xirp.util.Util;

/**
 * This manager offers static method for "speaking"
 * a given string or translation key.<br><br>
 * The text to speech work is done by the FreeTTS
 * framework. This manager includes support for
 * MBROLA voices.
 * 
 * @author Matthias Gernand 
 * 
 * @see com.sun.speech.freetts
 * 
 * TODO: german language
 */
public final class TextToSpeechManager extends AbstractManager {

	/**
	 * The logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(TextToSpeechManager.class);
	/**
	 * The default {@link com.sun.speech.freetts.Voice voice}.
	 * 
	 * @see com.sun.speech.freetts.Voice
	 */
	private static final String KEVIN = "kevin"; //$NON-NLS-1$
	/**
	 * The {@link com.sun.speech.freetts.Voice voice} to use.
	 * 
	 * @see com.sun.speech.freetts.Voice
	 */
	private static Voice voice;
	/**
	 * The voice {@link com.sun.speech.freetts.VoiceManager manager}.
	 * 
	 * @see com.sun.speech.freetts.VoiceManager
	 */
	private static VoiceManager voiceManager;

	/**
	 * Constructs a new manager.
	 * <br><br>
	 * The manager is initialized on startup. Never
	 * call this on your own. Use the statically provided
	 * methods.
	 *  
	 * @throws InstantiationException if an instance already exists.
	 */
	public TextToSpeechManager() throws InstantiationException {
		super( );
	}

	/**
	 * Speaks the given text.
	 * 
	 * @param text
	 * 			The text to speak.
	 * 
	 * @see com.sun.speech.freetts.Voice
	 */
	public static void speak(String text) {
		if (voice != null && PropertiesManager.isEnableTTS( )) {
			// voice.getOutputQueue( ).removeAll( ); //FIXME TODO
			voice.speak(text);
		}
	}

	/**
	 * Speaks the translation of the given text key.
	 * 
	 * @param key
	 * 			The key to translate and speak.
	 * @param objects
	 * 			The var args for the 
	 * 			{@link de.xirp.util.I18n#getString(String, Object[])} 
	 * 			method.
	 * @see com.sun.speech.freetts.Voice
	 */
	public static void speakLocaleKey(String key, Object... objects) {
		speak(I18n.getString(key, objects));
	}

	/**
	 * Initializes the FreeTTS voice system and the default voice.
	 * If another voice is set in the properties this one will be used.
	 * The support for MBROLA voices is enabled and the necessary
	 * initialization is done for that.
	 * <br><br>
	 * This method is called from the
	 * {@link de.xirp.managers.ManagerFactory} on
	 * startup. Do not call it on your own.
	 * 
	 * @see de.xirp.managers.AbstractManager#start()
	 * @see com.sun.speech.freetts.Voice
	 * @see com.sun.speech.freetts.VoiceManager
	 */
	@Override
	protected void start() throws ManagerException {
		super.start( );

		ApplicationManager.addVoiceChangedListener(new VoiceChangedListener( ) {

			public void voiceChanged(VoiceChangedEvent event) {
				String voiceName = event.getVoiceName( );
				if (voiceManager.contains(voiceName)) {
					Voice aux = voiceManager.getVoice(voiceName);
					aux.allocate( );
					voice.deallocate( );
					voice = aux;
				}
				else {
					logClass.warn(I18n.getString("TextToSpeechManager.log.voiceInvalid", voiceName) + Constants.LINE_SEPARATOR); //$NON-NLS-1$
				}
			}

		});

		System.setProperty("mbrola.base", "./voice"); //$NON-NLS-1$ //$NON-NLS-2$
		String voiceName = PropertiesManager.getTTSVoice( );
		if (Util.isEmpty(voiceName)) {
			voiceName = KEVIN;
		}

		try {
			/* The VoiceManager manages all the voices for FreeTTS. */
			voiceManager = VoiceManager.getInstance( );
			if (!voiceManager.contains(voiceName)) {
				voiceName = KEVIN;
			}
			voice = voiceManager.getVoice(voiceName);

			/* Allocates the resources for the voice. */
			voice.allocate( );

			if (PropertiesManager.isEnableTTS( )) {
				Thread t = new Thread( ) {

					@Override
					public void run() {
						speak("Hello, my name is Xirp'."); //$NON-NLS-1$
					}
				};
				t.start( );
			}
		}
		catch (RuntimeException e) {
			logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
					+ Constants.LINE_SEPARATOR, e);
		}
	}

	/**
	 * Stops the FreeTTS voice system and cleans up allocated
	 * resources.
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
		try {
			/* Clean up. */
			voice.getOutputQueue( ).removeAll( );
			voice.endBatch( );
			voice.deallocate( );
		}
		catch (NullPointerException e) {
			logClass.trace("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
	}

	/**
	 * Returns all available voices including FreeTTS and MBLORA
	 * voices.
	 * 
	 * @return An unmodifiable list with the voice names.
	 */
	public static List<String> getAvailableVoices() {
		List<String> voices = new ArrayList<String>( );
		for (Voice v : voiceManager.getVoices( )) {
			voices.add(v.getName( ));
		}
		return Collections.unmodifiableList(voices);
	}

	/**
	 * Returns whether a voice is available or not.
	 * 
	 * @param voice
	 * 			The voice name to check.
	 * @return A <code>boolean</code><br>
	 * 			<code>true</code>: the voice is available.<br>
	 * 			<code>false</code>: the voice is not available.<br>
	 */
	public static boolean isVoiceAvailable(String voice) {
		return voiceManager.contains(voice);
	}
}
