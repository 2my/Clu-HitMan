/* ProcessControlImpl.java
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

import java.io.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/** Start and kill an external process.
 * @author tommy skodje
 */
public class ProcessControlImpl implements ProcessControl {
	private static final Logger logger	= Logger.getLogger( ProcessControlImpl.class.getName() );

	Process process	= null;
	private final String execStr;
	private ProcessOut procOut	= null;

	/** Setup process to control with argument being a command-line string. */
	public ProcessControlImpl( String execStr ) {
		logger.trace( "ProcessControlImpl() " + execStr );
		Validate.notNull( execStr, "ProcessControlImpl( null )" );
		this.execStr = execStr;
	}

	/** @see no.antares.clutil.hitman.ProcessControl#start() */
	@Override public void start() {
		logger.info( "start() " + execStr );
		if ( process != null )
			return;
		try {
			// TODO: use ProcessBuilder, http://stackoverflow.com/questions/3468987/executing-another-application-from-java
			process = Runtime.getRuntime().exec( execStr );
			procOut	= new ProcessOut( process );
			procOut.start();
		} catch ( Throwable e ) {
			logger.fatal( "start(): " + execStr, e );
			throw new RuntimeException( "Error starting process: " + execStr, e);
		}
		logger.info( "start() process started: " + (process != null) );
	}

	/** @see no.antares.clutil.hitman.ProcessControl#kill() */
	@Override public void kill() {
		logger.warn( "kill()" );
		try {
			if ( procOut != null ) {
				procOut.done();
				procOut	= null;
			}
			if ( process != null )
				process.destroy();
		} catch ( Throwable e ) {
			logger.fatal( "kill(): " + execStr, e );
			throw new RuntimeException( "Error killing process: " + execStr, e);
		}
    	process	= null;
    }

	/** @see no.antares.clutil.hitman.ProcessControl#restart() */
	@Override public void restart() {
		kill();
		start();
	}

	/** @see no.antares.clutil.hitman.ProcessControl#shutDownAll() */
	@Override public void shutDownAll() {
		kill();
		System.exit( 0 );
	}

	private class ProcessOut extends Thread {
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
	};

	private static void waitHalfASecond() {
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

}