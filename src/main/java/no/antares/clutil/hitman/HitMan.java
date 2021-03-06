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

import java.net.ConnectException;
import java.util.Timer;
import java.util.TimerTask;

import no.antares.clutil.hitman.process.ProcessControl;
import no.antares.clutil.hitman.process.ProcessControlRuntime;

import org.apache.log4j.Logger;


/** HitMan starts an external process, then listens for "HIT ME" messages on port, 
 * terminates process after deadline (that may be extended).
 * @author tommy skodje
 */
@SuppressWarnings("deprecation")
public class HitMan {
	private static final int ticksPerSecond	= 1000;
	private static final Logger logger	= Logger.getLogger( HitMan.class.getName() );

	private final ProcessControl process;
	private final Timer restarter;

	private final DeadLine restartAtExpiry;
	private final TimerTask shutDownAll	= new TimerTask() {
		public void run() {
			process.shutDownAll();
		}
	};
	private final Thread shutDownProcess	= new Thread( "shutDownProcess" ) {
		public void run() {
			process.kill();
	  }
	};

	/** If HitMan is listening on port, should return status and command. */
	public static String ping( int port ) {
		String message	= Message.Semafor.PING.msgStart;
		try {
			return MessageChannel.send( port, message );
		} catch (ConnectException e) {
			return "ERROR connecting to localhost " + port;
		} catch (Throwable e) {
			return "ERROR sending " + message + " to localhost:" + port;
		}
	}

	/** Set up deadLine checker and start external process - rather naive implementation, prefer method runHitMan with ProcessControlProcessBuilder. */
	public static void runHitMan( int port, String command, int periodInSeconds ) {
		int periodInMillis	= periodInSeconds * ticksPerSecond;
		int defaultTimeOutMillis	= 2 * periodInMillis;
		runHitMan( MessageChannel.openInbound( port ), new ProcessControlRuntime( command ), periodInMillis, defaultTimeOutMillis );
	}

	/** Set up deadLine checker and start external process, preferred method */
	public static void runHitMan( MessageChannel channel, ProcessControl pc, int periodInMillis, long defaultTimeOutMillis ) {
		try {
			HitMan hitMan	= new HitMan( pc, periodInMillis, defaultTimeOutMillis );
			hitMan.messageLoop( channel );
		} finally {
			channel.close();
		}
	}

	/** Start external process and deadLine checker */
	protected HitMan( ProcessControl processControl, int periodInMillis, long defaultTimeOutMillis ) {
		restartAtExpiry	= new DeadLine( defaultTimeOutMillis ) {
			void expired() {
				process.restart();
			}
		};
		restarter	= DeadLineChecker.periodical( restartAtExpiry, periodInMillis ).startInMillis( 2 * periodInMillis );
		process	= processControl;
		Runtime.getRuntime().addShutdownHook( shutDownProcess );
		process.start();
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
				( new Timer( "shutDownAll" ) ).schedule( shutDownAll, message.waitMillis() + extraWait );
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
