package no.antares.clutil.hitman.process;

import java.io.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


class ProcessOut extends Thread {
	private static final Logger logger	= Logger.getLogger( ProcessOut.class.getName() );

	private boolean done	= false;
	private final BufferedReader in, err;
	ProcessOut( Process process2log ) {
		super( "ProcessOut" );
		this.in = new BufferedReader( new InputStreamReader( process2log.getInputStream() ) );
		this.err = new BufferedReader( new InputStreamReader( process2log.getErrorStream() ) );
	}
	public void done() {
		done	= true;
	}
	public void run() {
		while ( ! done ) {
			try {
				if ( in.ready() ) {
					String line	= in.readLine();
					if ( ! StringUtils.isBlank( line ) )
						logger.info( "System.out:" + line );
				}
				if ( err.ready() ) {
					String line	= err.readLine();
					if ( ! StringUtils.isBlank( line ) )
						logger.info( "System.err:" + line );
				}
				waitHalfASecond();
			} catch ( IOException ioe ) {
			}
		}
		close( in );
		close( err );
	}

	private void waitHalfASecond() {
		try {
			Thread.sleep( 500 );
		} catch ( Throwable t ) {
		}
	}
	private void close( Reader r) {
		try {
			r.close();
		} catch ( IOException ioe ) {
		}
	}
};
