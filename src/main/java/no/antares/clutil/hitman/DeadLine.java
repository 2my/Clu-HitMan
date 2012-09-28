/* DeadLine.java
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


/** A deadline may expire or be extended
 * @author tommy skodje
*/
abstract class DeadLine {
	private final long defaultTimeOut;
	// deadLine is mutable and all access should be synchronized
	private long deadLine;
	private int[] deadLineMonitor	= {};

	public DeadLine(long defaultTimeOut) {
		this.defaultTimeOut = defaultTimeOut;
		deadLine	= System.currentTimeMillis() + defaultTimeOut;
    	if ( deadLine < 0 )	// overflow
    		deadLine	= Long.MAX_VALUE;
	}

	/** Implementor decides what to do when expired */
	abstract void expired();

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
        	deadLine	= System.currentTimeMillis() + defaultTimeOut;
        	if ( deadLine < 0 )	// overflow
        		deadLine	= Long.MAX_VALUE;
    	}
       	expired();
	}

}
