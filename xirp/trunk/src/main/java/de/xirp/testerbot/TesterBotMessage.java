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
 * TesterBotMessage.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 26.04.2007:		Created by Matthias Gernand.
 */
package de.xirp.testerbot;

import java.util.Random;

/**
 * Creator for random text messages.
 * 
 * @author Matthias Gernand
 *
 */
public class TesterBotMessage extends AbstractValueCreator {

	/**
	 * A {@link java.util.Random} object.
	 */
	private static final Random RND = new Random( );

	/**
	 * Enumeration holding constants for several different
	 * states the robot can encounter. Every constant must
	 * implement a method which return the "speakable" name of
	 * the constant, because this strings are used by the
	 * TTS system.
	 * 
	 * @author Matthias Gernand
	 *
	 */
	private enum Artifact {
		/**
		 * A victim.
		 */
		VICTIM {

			@Override
			public String getSpeakableName() {
				return "a victim";
			}
		},
		/**
		 * A obstacle.
		 */
		OBSTACLE {

			@Override
			public String getSpeakableName() {
				return "an obstacle";
			}
		},
		/**
		 * An abyss.
		 */
		ABYSS {

			@Override
			public String getSpeakableName() {
				return "an abyss";
			}
		},
		/**
		 * An other robot.
		 */
		OTHER_BOT {

			@Override
			public String getSpeakableName() {
				return "another robot";
			}
		},
		/**
		 * Bio hazard material.
		 */
		BIO_HAZARD {

			@Override
			public String getSpeakableName() {
				return "a bio-hazard object";
			}
		},
		/**
		 * Chemical hazard material.
		 */
		CHEMICAL_HAZARD {

			@Override
			public String getSpeakableName() {
				return "a chemical hazardous object";
			}
		},
		/**
		 * Radiation.
		 */
		RADIATION {

			@Override
			public String getSpeakableName() {
				return "a radiant object";
			}
		};

		/**
		 * Returns the "speakable" name of the constant.
		 * The returned string is inserted in a complete sentence, 
		 * which is sent as "message from the robot" and can be
		 * read by the TTS system.
		 * 
		 * @return The speakable name.
		 */
		public abstract String getSpeakableName();
	}

	/**
	 * Constructs a new creator for text messages.
	 * 
	 * @param runTime
	 *            The total time span for the creator.
	 */
	public TesterBotMessage(long runTime) {
		super(runTime);
	}

	/**
	 * Returns a random message. The sentence is generated
	 * only in english at the moment.
	 * 
	 * @param elapsedTime
	 * 			The elapsed time in milliseconds.
	 * @return The constructed sentence.
	 * 
	 * @see de.xirp.testerbot.AbstractValueCreator#calculate(long)
	 */
	@Override
	protected String calculate(long elapsedTime) {
		int cnt = 0;
		int abyssCounter = 0;
		if (elapsedTime % 10 == 0) {
			cnt++;
			if (cnt == 1) {
				cnt = 0;
				int i = RND.nextInt(Artifact.values( ).length);
				Artifact a = Artifact.values( )[i];
				String text = "Encountered " + a.getSpeakableName( );
				if (a == Artifact.OTHER_BOT) {
					text = text + ". Negotiating with robot. Completed.";
				}
				else if (a == Artifact.ABYSS) {
					abyssCounter++;
					text = text + ".";
					if (abyssCounter == 7) {
						text = text
								+ " If you look long into an abyss. The abyss also looks into you.";
						abyssCounter = 0;
					}
				}
				else {
					text = text + ".";
				}
				return text;
			}
		}
		return "";
	}
}
