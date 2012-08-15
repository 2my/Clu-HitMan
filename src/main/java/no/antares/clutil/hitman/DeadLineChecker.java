/* DeadLineChecker.java
   Copyright 2012 Tommy Skodje (http://www.antares.no)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package no.antares.clutil.hitman;

import java.util.Timer;
import java.util.TimerTask;

/** Checks deadline periodically, or once
 * @author tommy skodje
*/
class DeadLineChecker {
	final long periodInMillis;
	final DeadLine deadLine;

	/** Periodical check */
	protected static DeadLineChecker periodical( DeadLine deadLine, long periodInMillis ) {
		return new DeadLineChecker( deadLine, periodInMillis );
	}
	/** One-time check */
	protected static DeadLineChecker oneOff( DeadLine deadLine ) {
		return new DeadLineChecker( deadLine, -1 );
	}

	private DeadLineChecker( DeadLine deadLine, long periodInMillis ) {
		this.periodInMillis = periodInMillis;
		this.deadLine = deadLine;
	}


	/** Start after initial delay */
	public Timer startInMillis( long initialDelayInMillis ) {
		final TimerTask killerTask	= new TimerTask() {
	        public void run() {
	        	deadLine.check();
	        }
	    };
		Timer timer = new Timer();
		if ( 0 < periodInMillis )
			timer.scheduleAtFixedRate( killerTask, initialDelayInMillis, this.periodInMillis );
		else
			timer.schedule( killerTask, initialDelayInMillis );
		return timer;
	}

}
