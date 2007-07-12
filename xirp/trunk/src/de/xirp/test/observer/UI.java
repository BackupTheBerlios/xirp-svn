package de.xirp.test.observer;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

/**
 * Example for the Observer Pattern
 * @author Rabea Gransberger
 *
 */
public class UI extends JFrame implements Observer { //imlement Observer!

	/**
	 * 
	 */
	private static final long serialVersionUID = 4635868371818258864L;
	/**
	 * The label which shows the data
	 */
	private JLabel valueLabel;

	/**
	 * A Userinterface which shows the given data
	 * @param data
	 */
	public UI(Data data) {
		init( );
		//add this ui to the data to observe
		data.addObserver(this);
	}

	/**
	 * Init the ui
	 */
	private void init() {
		setTitle("Observer Pattern Example"); //$NON-NLS-1$
		this.setSize(300, 300);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		//add a label displaying the data of the ui
		valueLabel = new JLabel(""); //$NON-NLS-1$
		this.getContentPane( ).add(valueLabel);
	}

	/** 
	 * Update the UI when the data has changed
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable obs, Object arg1) {
		if (obs instanceof Data) {
			Data dat = (Data) obs;
			valueLabel.setText(Long.toString(dat.getValue( )));
		}
	}

	public static void main(String[] args) {
		//Create some data and the user interface
		//for displaying the data
		final Data data = new Data( );
		final UI ui = new UI(data);
		ui.setVisible(true);

		//Start a thread which changes the data
		//the ui will update accordingly
		Thread thread = new Thread("UpdateThread") { //$NON-NLS-1$

			/* (non-Javadoc)
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() {
				while (ui.isShowing( )) {
					data.setValue(System.currentTimeMillis( ));
					try {
						Thread.sleep(1000);
					}
					catch (InterruptedException e) {
					}
				}
			}
		};
		thread.start( ); //start not run!!!
	}
}
