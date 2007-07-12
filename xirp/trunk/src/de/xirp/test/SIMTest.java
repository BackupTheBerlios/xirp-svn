package de.xirp.test;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;

import de.xirp.ui.util.SWTUtil;

/**
 * Test Connection to the USARSim (UT2004)
 * 
 * @author Rabea Gransberger
 * 
 */
public class SIMTest {

	/**
	 * The Logger for this class.
	 */
	private static Logger logClass = Logger.getLogger(SIMTest.class);
	private Socket socket;
	private DataInputStream fromServer;
	private PrintStream toServer;
	private boolean connected = false;
	private Combo comboRobot;
	private Text textLocation;
	private float speed = 1.1f;
	private Pattern patternSTA = Pattern
			.compile("STA \\{Time (.*)\\} \\{Location (.*),(.*),(.*)\\} \\{Orientation (.*),(.*),(.*)\\} \\{Velocity (.*),(.*),(.*)\\} \\{LightToggle (.*)\\} \\{LightIntensity (.*)\\} \\{Battery (.*)\\}"); //$NON-NLS-1$
	private Text textBattery;
	private Text textLightInt;
	private Text textLight;
	private Text textVelo;
	private Text textOrientation;
	private Text textLoc;
	private Text textTime;

	public SIMTest(Shell shell) {
		init(shell);
	}

	/**
	 * 
	 */
	private void init(Shell shell) {
		SWTUtil.setGridLayout(shell, 2, false);

		Label labelHost = new Label(shell, SWT.NONE);
		labelHost.setText("Host"); //$NON-NLS-1$
		SWTUtil.setGridData(labelHost, true, false, SWT.LEFT, SWT.CENTER, 1, 1);

		Text textHost = new Text(shell, SWT.NONE);
		textHost.setText("localhost"); //$NON-NLS-1$
		SWTUtil.setGridData(textHost, true, false, SWT.FILL, SWT.CENTER, 1, 1);

		Label labelPort = new Label(shell, SWT.NONE);
		labelPort.setText("Port"); //$NON-NLS-1$
		SWTUtil.setGridData(labelPort, true, false, SWT.LEFT, SWT.CENTER, 1, 1);

		Text textPort = new Text(shell, SWT.NONE);
		textPort.setText("3000"); //$NON-NLS-1$
		SWTUtil.setGridData(textPort, true, false, SWT.FILL, SWT.CENTER, 1, 1);

		final Button buttonConnect = new Button(shell, SWT.PUSH);
		buttonConnect.setText("Connect"); //$NON-NLS-1$
		buttonConnect.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (connected) {
					disconnect( );
					if (!connected) {
						buttonConnect.setText("Connect"); //$NON-NLS-1$
					}
				}
				else {
					connect( );
					if (connected) {
						buttonConnect.setText("Disconnect"); //$NON-NLS-1$
					}
				}
			}
		});
		SWTUtil.setGridData(buttonConnect, true, false, SWT.CENTER, SWT.CENTER,
				2, 1);

		Label labelRobot = new Label(shell, SWT.NONE);
		labelRobot.setText("Robot"); //$NON-NLS-1$
		SWTUtil
				.setGridData(labelRobot, true, false, SWT.LEFT, SWT.CENTER, 1,
						1);

		comboRobot = new Combo(shell, SWT.READ_ONLY);
		comboRobot.add("P2AT"); //$NON-NLS-1$
		comboRobot.add("P2DX"); //$NON-NLS-1$
		comboRobot.add("ATRVJr"); //$NON-NLS-1$
		comboRobot.add("Rover"); //$NON-NLS-1$
		comboRobot.add("USARBc"); //$NON-NLS-1$
		comboRobot.add("USARCar"); //$NON-NLS-1$
		comboRobot.add("Papagoose"); //$NON-NLS-1$
		comboRobot.add("Tarantula"); //$NON-NLS-1$
		comboRobot.add("Zerg"); //$NON-NLS-1$
		comboRobot.add("Talon"); //$NON-NLS-1$

		comboRobot.setText(comboRobot.getItem(0));
		SWTUtil
				.setGridData(comboRobot, true, false, SWT.FILL, SWT.CENTER, 1,
						1);

		Label labelLocation = new Label(shell, SWT.NONE);
		labelLocation.setText("Location"); //$NON-NLS-1$
		SWTUtil.setGridData(labelLocation, true, false, SWT.LEFT, SWT.CENTER,
				1, 1);

		textLocation = new Text(shell, SWT.NONE);
		textLocation.setText("4.5, 1.9, 1.8"); //$NON-NLS-1$
		SWTUtil.setGridData(textLocation, true, false, SWT.FILL, SWT.CENTER, 1,
				1);

		final Button insertRobot = new Button(shell, SWT.PUSH);
		insertRobot.setText("Spawn Robot"); //$NON-NLS-1$
		insertRobot.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				insertRobot( );
			}
		});
		SWTUtil.setGridData(insertRobot, true, false, SWT.CENTER, SWT.CENTER,
				2, 1);

		// Robot Control
		Composite control = new Composite(shell, SWT.NONE);
		SWTUtil.setGridLayout(control, 3, true);
		SWTUtil.setGridData(control, true, false, SWT.CENTER, SWT.CENTER, 1, 1);

		Button forward = new Button(control, SWT.PUSH);
		forward.setText("F"); //$NON-NLS-1$
		forward.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				drive(speed, speed);
			}
		});
		SWTUtil.setGridData(forward, true, false, SWT.CENTER, SWT.CENTER, 3, 1);

		Button left = new Button(control, SWT.PUSH);
		left.setText("L"); //$NON-NLS-1$
		left.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				drive(-speed, speed);
			}
		});
		SWTUtil.setGridData(left, true, false, SWT.CENTER, SWT.CENTER, 1, 1);

		Button none = new Button(control, SWT.PUSH);
		none.setText(" "); //$NON-NLS-1$
		none.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				drive(0, 0);
			}
		});
		SWTUtil.setGridData(none, true, false, SWT.CENTER, SWT.CENTER, 1, 1);

		Button right = new Button(control, SWT.PUSH);
		right.setText("R"); //$NON-NLS-1$
		right.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				drive(speed, -speed);
			}
		});
		SWTUtil.setGridData(right, true, false, SWT.CENTER, SWT.CENTER, 1, 1);

		Button backward = new Button(control, SWT.PUSH);
		backward.setText("B"); //$NON-NLS-1$
		backward.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				drive(-speed, -speed);
			}
		});
		SWTUtil
				.setGridData(backward, true, false, SWT.CENTER, SWT.CENTER, 3,
						1);

		// Camera Control
		Composite camera = new Composite(shell, SWT.NONE);
		SWTUtil.setGridLayout(camera, 3, true);
		SWTUtil.setGridData(camera, true, false, SWT.CENTER, SWT.CENTER, 1, 1);

		Button forwardCamera = new Button(camera, SWT.PUSH);
		forwardCamera.setText("F"); //$NON-NLS-1$
		SWTUtil.setGridData(forwardCamera, true, false, SWT.CENTER, SWT.CENTER,
				3, 1);

		Button leftCamera = new Button(camera, SWT.PUSH);
		leftCamera.setText("L"); //$NON-NLS-1$
		SWTUtil.setGridData(leftCamera, true, false, SWT.CENTER, SWT.CENTER, 1,
				1);

		Button noneCamera = new Button(camera, SWT.PUSH);
		noneCamera.setText(" "); //$NON-NLS-1$
		SWTUtil.setGridData(noneCamera, true, false, SWT.CENTER, SWT.CENTER, 1,
				1);

		Button rightCamera = new Button(camera, SWT.PUSH);
		rightCamera.setText("R"); //$NON-NLS-1$
		SWTUtil.setGridData(rightCamera, true, false, SWT.CENTER, SWT.CENTER,
				1, 1);

		Button backwardCamera = new Button(camera, SWT.PUSH);
		backwardCamera.setText("B"); //$NON-NLS-1$
		SWTUtil.setGridData(backwardCamera, true, false, SWT.CENTER,
				SWT.CENTER, 3, 1);

		// Labels showing state from server
		Label label = new Label(shell, SWT.NONE);
		label.setText("Time"); //$NON-NLS-1$
		SWTUtil.setGridData(label, true, false, SWT.LEFT, SWT.CENTER, 1, 1);

		textTime = new Text(shell, SWT.NONE);
		SWTUtil.setGridData(textTime, true, false, SWT.FILL, SWT.CENTER, 1, 1);

		label = new Label(shell, SWT.NONE);
		label.setText("Location"); //$NON-NLS-1$
		SWTUtil.setGridData(label, true, false, SWT.LEFT, SWT.CENTER, 1, 1);

		textLoc = new Text(shell, SWT.NONE);
		SWTUtil.setGridData(textLoc, true, false, SWT.FILL, SWT.CENTER, 1, 1);

		label = new Label(shell, SWT.NONE);
		label.setText("Orientation"); //$NON-NLS-1$
		SWTUtil.setGridData(label, true, false, SWT.LEFT, SWT.CENTER, 1, 1);

		textOrientation = new Text(shell, SWT.NONE);
		SWTUtil.setGridData(textOrientation, true, false, SWT.FILL, SWT.CENTER,
				1, 1);

		label = new Label(shell, SWT.NONE);
		label.setText("Velocity"); //$NON-NLS-1$
		SWTUtil.setGridData(label, true, false, SWT.LEFT, SWT.CENTER, 1, 1);

		textVelo = new Text(shell, SWT.NONE);
		SWTUtil.setGridData(textVelo, true, false, SWT.FILL, SWT.CENTER, 1, 1);

		label = new Label(shell, SWT.NONE);
		label.setText("Light On?"); //$NON-NLS-1$
		SWTUtil.setGridData(label, true, false, SWT.LEFT, SWT.CENTER, 1, 1);

		textLight = new Text(shell, SWT.NONE);
		SWTUtil.setGridData(textLight, true, false, SWT.FILL, SWT.CENTER, 1, 1);

		label = new Label(shell, SWT.NONE);
		label.setText("Light Intensity"); //$NON-NLS-1$
		SWTUtil.setGridData(label, true, false, SWT.LEFT, SWT.CENTER, 1, 1);

		textLightInt = new Text(shell, SWT.NONE);
		SWTUtil.setGridData(textLightInt, true, false, SWT.FILL, SWT.CENTER, 1,
				1);

		label = new Label(shell, SWT.NONE);
		label.setText("Battery"); //$NON-NLS-1$
		SWTUtil.setGridData(label, true, false, SWT.LEFT, SWT.CENTER, 1, 1);

		textBattery = new Text(shell, SWT.NONE);
		SWTUtil.setGridData(textBattery, true, false, SWT.FILL, SWT.CENTER, 1,
				1);
	}

	/**
	 * @param i
	 * @param j
	 */
	private void drive(float left, float right) {
		toServer.format("DRIVE {Left %f} {Right %f}\r\n", left, right); //$NON-NLS-1$
		toServer.flush( );
	}

	private void insertRobot() {
		if (!connected)
			return;

		String bot = comboRobot.getText( );
		String location = textLocation.getText( );
		toServer.format("INIT {ClassName USARBot.%s} {Location %s}\r\n", bot, //$NON-NLS-1$
				location);
		toServer.flush( );
	}

	private void connect() {
		try {
			// establish connection to Gamebots
			socket = new Socket("localhost", 3000); //$NON-NLS-1$
			fromServer = new DataInputStream(socket.getInputStream( ));
			toServer = new PrintStream(socket.getOutputStream( ));

			// receive server output
			Thread thread = new Thread( ) { //$NON-NLS-1$

				@Override
				public void run() {
					// Run the Thread until receiving
					// was disabled or Comm disconnected
					try {
						while (connected) {
							String strg = fromServer.readLine( );
							received(strg);
						}
					}
					catch (IOException e) {
						logClass.error("Error: " + e.getMessage( ) + "\n", e); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
			};
			thread.setPriority(Thread.MIN_PRIORITY);
			// Start Receiving Thread
			thread.start( );
			logClass.debug("running receiving\n"); //$NON-NLS-1$

			connected = true;
		}
		catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			logClass.error("Error: " + e.getMessage( ) + "\n", e); //$NON-NLS-1$ //$NON-NLS-2$
			connected = false;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			logClass.error("Error: " + e.getMessage( ) + "\n", e); //$NON-NLS-1$ //$NON-NLS-2$
			connected = false;
		}
	}

	private void disconnect() {
		try {
			fromServer.close( );
			toServer.close( );
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			logClass.error("Error: " + e.getMessage( ) + "\n", e); //$NON-NLS-1$ //$NON-NLS-2$
		}
		try {
			socket.close( );
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			logClass.error("Error: " + e.getMessage( ) + "\n", e); //$NON-NLS-1$ //$NON-NLS-2$
		}
		connected = false;
	}

	private void received(String strg) {
		// logClass.debug(strg);
		if (strg.indexOf("SEN") != -1) { //$NON-NLS-1$

		}
		// STA {Time 143.92} {Location 4.5285,1.8963,83.2144}
		// {Orientation 0.0393,0.3052,0.0070} {Velocity
		// 0.0000,-0.0000,1.8391} {LightToggle False} {LightIntensity
		// 0} {Battery 3558}
		else if (strg.indexOf("STA") != -1) { //$NON-NLS-1$
			Matcher m = patternSTA.matcher(strg);
			if (m.matches( )) {
				final double time = parseDouble(m.group(1));
				final Triple location = new Triple(parseDouble(m.group(2)),
						parseDouble(m.group(3)), parseDouble(m.group(4)));
				final Triple orientation = new Triple(parseDouble(m.group(5)),
						parseDouble(m.group(6)), parseDouble(m.group(7)));
				final Triple velocity = new Triple(parseDouble(m.group(8)),
						parseDouble(m.group(9)), parseDouble(m.group(10)));
				final String light = m.group(11);
				final int intensity = parseInt(m.group(12));
				final int battery = parseInt(m.group(13));

				Display.getDefault( ).asyncExec(new Runnable( ) {

					public void run() {
						textTime.setText(Double.toString(time));
						textLoc.setText(location.toString( ));
						textOrientation.setText(orientation.toString( ));
						textVelo.setText(velocity.toString( ));
						textLight.setText(light);
						textLightInt.setText(Integer.toString(intensity));
						textBattery.setText(Integer.toString(battery));
					}
				});
			}
		}
		else if (strg.indexOf("MIS") != -1) { //$NON-NLS-1$

		}
		else {

		}
	}

	public static int parseInt(String strg) {
		try {
			return Integer.parseInt(strg);
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}

	public static double parseDouble(String strg) {
		try {
			return Double.parseDouble(strg);
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}

	public static void main(String[] args) {
		BasicConfigurator.configure( );
		// String strg = "STA {Time 143.92} {Location
		// 4.5285,1.8963,83.2144} {Orientation 0.0393,0.3052,0.0070}
		// {Velocity 0.0000,-0.0000,1.8391} {LightToggle False}
		// {LightIntensity 0} {Battery 3558}";
		// Pattern patternSTA = Pattern.compile( "STA \\{Time (.*)\\}
		// \\{Location (.*),(.*),(.*)\\} \\{Orientation
		// (.*),(.*),(.*)\\} \\{Velocity (.*),(.*),(.*)\\}
		// \\{LightToggle (.*)\\} \\{LightIntensity (.*)\\} \\{Battery
		// (.*)\\}");
		// Matcher m = patternSTA.matcher( strg );
		// if(m.matches( )){
		// double time = parseDouble(m.group(1));
		// Triple location = new
		// Triple(parseDouble(m.group(2)),parseDouble(m.group(3)),parseDouble(m.group(4)));
		// Triple orientation = new
		// Triple(parseDouble(m.group(5)),parseDouble(m.group(6)),parseDouble(m.group(7)));
		// Triple velocity = new
		// Triple(parseDouble(m.group(8)),parseDouble(m.group(9)),parseDouble(m.group(10)));
		// String light = m.group( 11 );
		// int intensity = parseInt(m.group( 12 ));
		// int battery = parseInt(m.group( 13 ));
		//			
		// for(int i = 0; i < m.groupCount( ); i++){
		// String g = m.group(i);
		// System.out.println(g);
		// }
		// }

		Display display = new Display( );
		Shell shell = new Shell(display);

		SIMTest sim = new SIMTest(shell);

		shell.setSize(500, 500);
		shell.open( );

		while (!shell.isDisposed( )) {
			if (!display.readAndDispatch( ))
				display.sleep( );
		}
		display.dispose( );

		// try {
		// //establish connection to Gamebots
		// Socket myClient = new Socket("localhost", 3000);
		// final DataInputStream input = new DataInputStream(myClient
		// .getInputStream( ));
		// PrintStream output = new
		// PrintStream(myClient.getOutputStream( ));
		//			
		// //create robot in game
		// String bot = "Zerg";
		// String location = "4.5, 1.9, 1.8";
		// output.format("INIT {ClassName USARBot.%s} {Location
		// %s}\r\n", bot,
		// location);
		// output.flush( );
		//
		// //receive server output
		// Thread thread = new Thread( ) { //$NON-NLS-1$
		//
		// @Override
		// public void run() {
		// // Run the Thread until receiving
		// // was disabled or Comm disconnected
		// try {
		// do {
		// String strg = input.readLine( );
		// logClass.debug(strg);
		// } while (input.available( ) > 0);
		// }
		// catch (IOException e) {
		// // TODO Auto-generated catch block
		// logClass.error("Error: " + e.getMessage( ) + "\n", e);
		// }
		// }
		// };
		// thread.setPriority(Thread.MIN_PRIORITY);
		// // Start Receiving Thread
		// thread.start( );
		// logClass.debug("running receiving\n");
		//
		// //Drive and turn light on
		// float lSpeed = 1.1f;
		// float rSpeed = 1.1f;
		// output.format("DRIVE {Left %f} {Right %f}\r\n", lSpeed,
		// rSpeed);
		// output.flush( );
		//
		// output.format("DRIVE {Light True}\r\n");
		// output.flush( );
		//
		// while (true) {
		// ;
		// }
		// }
		// catch (UnknownHostException e) {
		// // TODO Auto-generated catch block
		// logClass.error("Error: " + e.getMessage( ) + "\n", e);
		// }
		// catch (IOException e) {
		// // TODO Auto-generated catch block
		// logClass.error("Error: " + e.getMessage( ) + "\n", e);
		// }
	}
}
