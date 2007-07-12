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
 * ATEManager.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 07.04.2007:		Created by Matthias Gernand.
 */
package de.xirp.ate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.xirp.managers.AbstractManager;
import de.xirp.managers.ManagerException;
import de.xirp.util.Constants;
import de.xirp.util.FutureRelease;
import de.xirp.util.serialization.ObjectDeSerializer;
import de.xirp.util.serialization.ObjectSerializer;
import de.xirp.util.serialization.SerializationException;

/**
 * This manager provides several methods for managing the whole
 * algorithm test environment. <br>
 * <br>
 * This is alpha API, may be removed in the future or is planed to be
 * integrated in version 3.0.0.
 * 
 * @author Matthias Gernand
 */
@FutureRelease(version = "3.0.0")
public final class ATEManager extends AbstractManager {

	/**
	 * The logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(ATEManager.class);
	/**
	 * A map holding varibles used in the generated code.
	 */
	private static Map<String, Double> variables = new HashMap<String, Double>( );
	/**
	 * A map holding constants used in the generated code.
	 */
	private static Map<String, Double> constants = new HashMap<String, Double>( );
	/**
	 * A set holding the class names of the compiled code.
	 */
	private static Set<String> mazeJavaClasses = new HashSet<String>( );
	/**
	 * A list of listeners.
	 */
	private static List<ATEListener> listeners = new ArrayList<ATEListener>( );
	/**
	 * The currently active maze.
	 */
	private static Maze currentMaze = new Maze(10, 10);

	/**
	 * Constructs a new manager instance. <br>
	 * <br>
	 * The manager is initialized on startup. Never call this on your
	 * own. Use the statically provided methods.
	 * 
	 * @throws InstantiationException
	 *             is thrown when an instance already exists.
	 */
	public ATEManager() throws InstantiationException {
		super( );
	}

	/**
	 * Compiles the given Java {@link java.io.File} to the
	 * corresponding byte code class files.
	 * 
	 * @param javaFile
	 *            The file containing the Java code.
	 */
	public static void compile(File javaFile) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler( );
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null,
				null,
				null);

		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Collections.singletonList(javaFile));
		compiler.getTask(new PrintWriter(System.out),
				fileManager,
				null,
				null,
				null,
				compilationUnits).call( );

		try {
			fileManager.close( );
		}
		catch (IOException e) {
			logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
					+ Constants.LINE_SEPARATOR, e);
		}
	}

	/**
	 * Sets a variable which is used when generating the Java code.
	 * 
	 * @param name
	 *            The name of the variable.
	 * @param value
	 *            The value of the variable.
	 */
	public static void setVariable(String name, double value) {
		variables.put(name, value);
	}

	/**
	 * Build Java code from the given code fragment, the variables and
	 * constants. The resulting Java code is written to the file named
	 * in the given file name.
	 * 
	 * @param code
	 *            The code fragment from the ui.
	 * @param fileName
	 *            The file name where generated code is written to.
	 */
	public static void buildCode(String code, String fileName) {
		String className = extractClassName(fileName);
		StringBuilder sb = new StringBuilder( );

		sb.append("public final class ").append(className).append(" {"); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append(Constants.LINE_SEPARATOR);
		sb.append(Constants.LINE_SEPARATOR);

		for (Entry<String, Double> e : constants.entrySet( )) {
			sb.append("\tprivate static final double "); //$NON-NLS-1$
			sb.append(e.getKey( ));
			sb.append(" = "); //$NON-NLS-1$
			sb.append(e.getValue( ));
			sb.append(";"); //$NON-NLS-1$
			sb.append(Constants.LINE_SEPARATOR);
		}

		sb.append(Constants.LINE_SEPARATOR);

		for (Entry<String, Double> e : variables.entrySet( )) {
			sb.append("\tprivate double "); //$NON-NLS-1$
			sb.append(e.getKey( ));
			sb.append(" = "); //$NON-NLS-1$
			sb.append(e.getValue( ));
			sb.append(";"); //$NON-NLS-1$
			sb.append(Constants.LINE_SEPARATOR);
		}

		sb.append(code);
		sb.append(Constants.LINE_SEPARATOR);
		sb.append("}"); //$NON-NLS-1$

		// System.out.println(sb);

		File java = new File(Constants.MAZE_CODE_DIR, fileName);

		try {
			FileUtils.writeStringToFile(java, sb.toString( ), null);
			ATEManager.compile(java);
			mazeJavaClasses.add(extractClassName(fileName));
			informListeners( );
		}
		catch (IOException e) {
			logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
					+ Constants.LINE_SEPARATOR, e);
		}
	}

	/**
	 * Utility method which extracts a class name from a Java file
	 * name.
	 * 
	 * @param fileName
	 *            The file name of the java file.
	 * @return {@link java.lang.String} containing the class name.
	 */
	private static String extractClassName(String fileName) {
		String className = fileName.split("\\.")[0]; //$NON-NLS-1$
		return className;
	}

	/**
	 * Executes the a the <code>algorithm( )</code> method from a
	 * given class, which is indicated by is name.
	 * 
	 * @param className
	 *            The class to execute the method from.
	 */
	public static void execute(String className) {
		File clazz = new File(Constants.MAZE_CODE_DIR);
		try {
			URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] {clazz.toURI( )
					.toURL( )});
			Class<?> forName = Class.forName(className, true, classLoader);
			Method declaredMethod = forName.getDeclaredMethod("algorithm", new Class[0]); //$NON-NLS-1$
			declaredMethod.invoke(forName.newInstance( ), new Object[0]);
		}
		catch (Exception e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
	}

	/**
	 * Saved the current maze to the given {@link java.io.File}.
	 * 
	 * @param writeTo
	 *            The file to write the maze to.
	 */
	public static void saveCurrentMaze(File writeTo) {
		saveMaze(currentMaze, writeTo);
	}

	/**
	 * Saves the given {@link de.xirp.ate.Maze} to the
	 * given {@link java.io.File}.
	 * 
	 * @param maze
	 *            The maze to save.
	 * @param writeTo
	 *            The file to write the maze to.
	 */
	private static void saveMaze(Maze maze, File writeTo) {
		try {
			ObjectSerializer.<Maze> writeToDisk(maze, writeTo);
		}
		catch (IOException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
	}

	/**
	 * Loads the given maze file to the
	 * {@link de.xirp.ate.Maze} data structure.
	 * 
	 * @param mazeFile
	 *            The file name of the maze to load.
	 * @see de.xirp.util.serialization.ObjectDeSerializer
	 */
	public static void loadMaze(File mazeFile) {
		Maze maze = null;
		try {
			maze = ObjectDeSerializer.<Maze> getObject(mazeFile);
			setCurrentMaze(maze);
			fireMazeChanged( );
		}
		catch (FileNotFoundException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
		catch (SerializationException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
		// Construct a default maze when erros occur
		maze = new Maze(10, 10);
		setCurrentMaze(maze);
		fireMazeChanged( );
	}

	/**
	 * Sets a constant which is used when generating the Java code.
	 * 
	 * @param name
	 *            The name of the constant.
	 * @param value
	 *            The value of the constant.
	 */
	public static void setConstant(String name, double value) {
		constants.put(name, value);
	}

	/**
	 * Returns all existing Java class files.
	 * 
	 * @return <code>Set&lt;String&gt;</code> containing all Java
	 *         classes.
	 */
	public static Set<String> getMazeJavaClasses() {
		return Collections.unmodifiableSet(mazeJavaClasses);
	}

	/**
	 * Add a listener to the list if listeners.
	 * 
	 * @param listener
	 *            The listener to add.
	 * @see de.xirp.ate.ATEListener
	 */
	public static void addATEListener(ATEListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removed the given listener form the list of listeners.
	 * 
	 * @param listener
	 *            The listener to remove.
	 * @see de.xirp.ate.ATEListener
	 */
	public static void removeATEListener(ATEListener listener) {
		listeners.remove(listener);
	}

	/**
	 * This method is called when the list of classes has changed. The
	 * listerns are informed of the class list change.
	 */
	private static void informListeners() {
		for (ATEListener l : listeners) {
			l.classListChanged( );
		}
	}

	/**
	 * Sets the current {@link Maze}.
	 * 
	 * @param currentMaze
	 *            The current maze to set.
	 */
	public static void setCurrentMaze(Maze currentMaze) {
		ATEManager.currentMaze = currentMaze;
	}

	/**
	 * Informs the listerns of a change of the current maze.
	 */
	private static void fireMazeChanged() {
		for (ATEListener l : listeners) {
			l.mazeChanged(currentMaze);
		}
	}

	/**
	 * Starts the {@link de.xirp.ate.ATEManager}. The
	 * existing class files are read and added to the set of class
	 * names. Finally the nessessary listerns are registered. <br>
	 * <br>
	 * This method is called from the
	 * {@link de.xirp.managers.ManagerFactory} on
	 * startup. Do not call it on your own.
	 * 
	 * @see de.xirp.managers.AbstractManager#start()
	 */
	@Override
	protected void start() throws ManagerException {
		super.start( );
		File dir = new File(Constants.MAZE_CODE_DIR);
		FilenameFilter filter = new FilenameFilter( ) {

			public boolean accept(File dir, String name) {
				return name.endsWith(".class"); //$NON-NLS-1$
			}

		};
		for (File file : dir.listFiles(filter)) {
			mazeJavaClasses.add(extractClassName(file.getName( )));
		}

		currentMaze.addMazeListener(new MazeListener( ) {

			public void mazeModified(Maze maze) {

			}

		});
	}

	/**
	 * Does nothing. This method is called from the
	 * {@link de.xirp.managers.ManagerFactory} on
	 * shutdown. Do not call it on your own.
	 * 
	 * @see de.xirp.managers.AbstractManager#stop()
	 */
	@Override
	protected void stop() throws ManagerException {
		super.stop( );
	}
}
