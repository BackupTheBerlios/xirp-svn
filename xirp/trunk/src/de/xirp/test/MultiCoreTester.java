package de.xirp.test;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

public class MultiCoreTester {

	private static final int THREADS = ManagementFactory.getOperatingSystemMXBean( )
			.getAvailableProcessors( ) * 5;
	private static CountDownLatch ct = new CountDownLatch(THREADS);
	private static AtomicLong total = new AtomicLong( );

	public static void main(String[] args) throws InterruptedException {
		long elapsedTime = System.nanoTime( );
		for (int i = 0; i < THREADS; i++) {
			Thread thread = new Thread( ) {

				@Override
				public void run() {
					total.addAndGet(measureThreadCpuTime( ));
					ct.countDown( );
				}
			};
			thread.start( );
		}
		ct.await( );
		elapsedTime = System.nanoTime( ) - elapsedTime;
		System.out.println("Total elapsed time " + elapsedTime); //$NON-NLS-1$
		System.out.println("Total thread CPU time " + total.get( )); //$NON-NLS-1$
		double factor = total.get( );
		factor /= elapsedTime;
		System.out.printf("Factor: %.2f%n", factor); //$NON-NLS-1$
	}

	private static long measureThreadCpuTime() {
		ThreadMXBean tm = ManagementFactory.getThreadMXBean( );
		long cpuTime = tm.getCurrentThreadCpuTime( );
		for (int i = 0; i < 1000 * 1000 * 1000; i++) {
			// keep ourselves busy for a while ...
		}
		cpuTime = tm.getCurrentThreadCpuTime( ) - cpuTime;
		System.out.println(Thread.currentThread( ) + ": cpuTime = " + cpuTime); //$NON-NLS-1$
		return cpuTime;
	}
}
