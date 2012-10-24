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
package no.antares.clutil.hitman.process;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/** Start and kill an external process given by working directory, program and argument list.
 * @author tommy skodje
 */
public class ProcessControlProcessBuilder implements ProcessControl {
	private static final Logger logger	= Logger.getLogger( ProcessControlProcessBuilder.class.getName() );

	Process process	= null;
	private final String[] programAndArguments;
	private final File workingDir;
	private ProcessOut procOut	= null;
	private final String fullCall;

	/** Setup process to control with arguments and working directory. */
	public ProcessControlProcessBuilder( File workingDirIn, String program, List<String> argumentsIn ) {
		logger.trace( "ProcessControlImpl() " + program );
		Validate.notNull( program, "ProcessControlImpl( null )" );

		this.workingDir = workingDirIn;

		if ( argumentsIn == null )
			programAndArguments	= new String[ 1 ];
		else {
			programAndArguments	= new String[ argumentsIn.size() + 1 ];
			int idx	= 1;
			for ( String argument: argumentsIn )
				programAndArguments[ idx++ ]	= argument;
		}
		programAndArguments[ 0 ]	= program;
		fullCall	= StringUtils.join( programAndArguments, " " );
	}

	/** @see no.antares.clutil.hitman.process.ProcessControl#start() */
	@Override public void start() {
		logger.info( "start() " + fullCall );
		if ( process != null )
			return;
		try {
			ProcessBuilder builder = new ProcessBuilder( programAndArguments );
			if ( this.workingDir != null )
				builder.directory( this.workingDir );
			process = builder.start();
			procOut	= new ProcessOut( process );
			procOut.start();
		} catch ( Throwable e ) {
			logger.fatal( "start(): " + fullCall, e );
			throw new RuntimeException( "Error starting process: " + fullCall, e);
		}
		logger.info( "start() process started: " + (process != null) );
	}

	/** @see no.antares.clutil.hitman.process.ProcessControl#kill() */
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
			logger.fatal( "kill(): " + fullCall, e );
			throw new RuntimeException( "Error killing process: " + fullCall, e);
		}
    	process	= null;
    }

	/** @see no.antares.clutil.hitman.process.ProcessControl#restart() */
	@Override public void restart() {
		kill();
		start();
	}

	/** @see no.antares.clutil.hitman.process.ProcessControl#shutDownAll() */
	@Override public void shutDownAll() {
		kill();
		System.exit( 0 );
	}

}