/* ProcessOut.java
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
package no.antares.clutil.hitman.process;

import java.io.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/** Captures System out and err from a process and routes them to logger info().
 * @author tommy skodje
 */
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
