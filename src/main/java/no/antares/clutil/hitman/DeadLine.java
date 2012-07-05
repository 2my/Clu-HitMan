package no.antares.clutil.hitman;


/**
 * @author tommy skodje
*/
abstract class DeadLine {
	// deadLine is mutable and all access should be synchronized
	private static final long FAR_FUTURE	= Long.MAX_VALUE;
	private long deadLine	= FAR_FUTURE;
	private int[] deadLineMonitor	= {};

	/** Implementor decides what to do when expired */
	abstract void expired();

	/** Builder of DeadLine - construct and start with checkAtFixedRate().startIn() */
	DeadLineChecker checkAtFixedRate( long periodInMillis ) {
		return new DeadLineChecker( this, periodInMillis );
	}

	/** Extend timeout */
	protected void extend( Message message ) {
		synchronized ( deadLineMonitor ) {
			// wide block - because we want to spare a client that talks to us
			deadLine	= message.deadLine();
		}
	}

	protected void check() {
		if ( System.currentTimeMillis() <= deadLine )
        	return;
    	synchronized ( deadLineMonitor ) {
            if ( System.currentTimeMillis() <= deadLine )
            	return;
        	deadLine	= FAR_FUTURE;
    	}
       	expired();
	}

}
