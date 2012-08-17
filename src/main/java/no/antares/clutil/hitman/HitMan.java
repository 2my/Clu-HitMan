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


/** HitMan starts an external process, then listens for "HIT ME" messages on port, 
 * terminates process after deadline (that may be extended).
 * @author tommy skodje
 */
public class HitMan {
	private static final int ticksPerSecond	= 1000;

	/** Set up deadLine checker and start external process */
	public static void runHitMan( int port, String command ) {
		MessageChannel channel	= MessageChannel.openInbound( port );
		try {
			HitMan hitMan	= new HitMan( command );
			hitMan.messageLoop( channel );
		} finally {
			channel.close();
		}
	}

	private final ProcessControl process;
	private final Timer restarter;

	private final DeadLine restartAtExpiry	= new DeadLine() {
		void expired() {
			process.restart();
		}
	};
	private final TimerTask shutdownAtExpiry	= new TimerTask() {
		public void run() {
			process.kill();
			System.exit( 0 );
		}
	};

	/** Start external process and deadLine checker */
	private HitMan( String command ) {
		process	= new ProcessControl( command );
		Runtime.getRuntime().addShutdownHook( process.killer );

		process.start();

		restarter	= DeadLineChecker.periodical( restartAtExpiry, 5 * ticksPerSecond ).startInMillis( 5 * ticksPerSecond );
	}

	/** Process messages on channel  */
	private void messageLoop( MessageChannel channel ) {
		boolean terminated	= false;
		while ( ! terminated ) {
			Message message	= channel.waitForNextMessage();
			if ( message.isExtension() )
				restartAtExpiry.extend( message );
			if ( message.isTermination() ) {
				( new Timer() ).schedule( shutdownAtExpiry, message.waitMillis() + 100 );
				restarter.cancel();
				return;
			}
		}
	}
	public static void main(String[] args) throws Exception {
		runHitMan( 5555, "/Applications/TextWrangler.app/Contents/MacOS/TextWrangler" );
		// MessageChannel.send( 5555, "HIT ME IN 2" );
	}

}
