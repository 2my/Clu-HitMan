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


/** HitMan starts an external process, then listens for "HIT ME" messages on port, 
 * terminates process after deadline (that may be extended).
 * @author tommy skodje
 */
public class HitMan {
	private static final int ticksPerSecond	= 1000;

	private final ProcessControl process;

	private final DeadLine deadLine	= new DeadLine() {
		void expired() {
			process.restart();
		}
	};

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

	/** Start external process and deadLine checker */
	private HitMan( String command ) {
		process	= new ProcessControl( command );
		Runtime.getRuntime().addShutdownHook( process.killer );

		process.start();

		deadLine.checkAtFixedRate( 15 * ticksPerSecond ).startIn( 15 * ticksPerSecond );
	}

	/** Process messages on channel  */
	private void messageLoop( MessageChannel channel ) {
		boolean stopped = false;
		while ( ! stopped ) {
			Message message	= channel.waitForNextMessage();
			if ( message.isExtension() ) {
				deadLine.extend( message );
			}
		}
	}

}
