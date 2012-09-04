/* HitMan.java
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

import org.apache.log4j.Logger;


/** HitMan starts an external process, then listens for "HIT ME" messages on port, 
 * terminates process after deadline (that may be extended).
 * @author tommy skodje
 */
public class HitMan {
	private static final int ticksPerSecond	= 1000;
	private static final Logger logger	= Logger.getLogger( HitMan.class.getName() );

	private final ProcessControl process;
	private final Timer restarter;

	private final DeadLine restartAtExpiry	= new DeadLine() {
		void expired() {
			process.restart();
		}
	};
	private final TimerTask shutDownAll	= new TimerTask() {
		public void run() {
			process.shutDownAll();
		}
	};
	private final Thread shutDownProcess	= new Thread() {
		public void run() {
			process.kill();
	    }
	};

	/** Set up deadLine checker and start external process */
	public static void runHitMan( int port, String command, int periodInSeconds ) {
		runHitMan( MessageChannel.openInbound( port ), new ProcessControlImpl( command ), periodInSeconds * ticksPerSecond );
	}

	/** Set up deadLine checker and start external process */
	public static void runHitMan( MessageChannel channel, ProcessControl pc, int periodInMillis ) {
		try {
			HitMan hitMan	= new HitMan( pc, periodInMillis );
			hitMan.messageLoop( channel );
		} finally {
			channel.close();
		}
	}

	/** Start external process and deadLine checker */
	protected HitMan( ProcessControl processControl, int periodInMillis ) {
		process	= processControl;
		Runtime.getRuntime().addShutdownHook( shutDownProcess );
		process.start();
		restarter	= DeadLineChecker.periodical( restartAtExpiry, periodInMillis ).startInMillis( 2 * periodInMillis );
	}

	/** Process messages on channel  */
	protected void messageLoop( MessageChannel channel ) {
		boolean terminated	= false;
		while ( ! terminated ) {
			Message message	= channel.waitForNextMessage();
			logger.trace( "messageLoop() got " + message );
			if ( message.isExtension() )
				restartAtExpiry.extend( message );
			if ( message.isTermination() ) {
				int extraWait	= 100;
				( new Timer() ).schedule( shutDownAll, message.waitMillis() + extraWait );
				restarter.cancel();
				return;
			}
		}
	}

	public static void main(String[] args) throws Exception {
		runHitMan( 5555, "/Applications/TextWrangler.app/Contents/MacOS/TextWrangler", 5 );
		// MessageChannel.send( 5555, "HIT ME IN 2" );
	}

}
