package no.antares.clutil.hitman;


/** HitMan starts an external process, then listens for "HIT ME" messages on port, 
 * terminates process after deadline (that may be renewed).
 * @author tommy skodje
 */
class HitMan {
	private static final int ticksPerSecond	= 1000;

	private final ProcessControl process;

	private final DeadLine deadLine	= new DeadLine() {
		void expired() {
			process.restart();
		}
	};

	/** Set up deadLine checker and start external process */
	protected static void runHitMan( int port, String command ) {
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
