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

/** Checks deadline periodically
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
