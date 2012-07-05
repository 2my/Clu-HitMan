package no.antares.clutil.hitman;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author tommy skodje
*/
class DeadLineChecker {
	final long periodInMillis;
	final DeadLine deadLine;

	protected DeadLineChecker( DeadLine deadLine, long periodInMillis ) {
		this.periodInMillis = periodInMillis;
		this.deadLine = deadLine;
	}

	void startIn( long initialDelayInMillis ) {
		final TimerTask killerTask	= new TimerTask() {
	        public void run() {
	        	deadLine.check();
	        }
	    };
		Timer timer = new Timer();
		timer.scheduleAtFixedRate( killerTask, initialDelayInMillis, this.periodInMillis );
	}

}
