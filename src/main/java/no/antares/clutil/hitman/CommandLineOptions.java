/* CommandLineOptions.java
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

import org.apache.commons.cli.*;

/** Interpretes command line options for HitMan
 * @author tommy skodje
*/
class CommandLineOptions {
	final Integer portNo;
	final String signal;
	final String command;

	private final Options options = new Options();

	protected CommandLineOptions( String[] args ) {
		addOption( "help", "print this message" );
		Option portArg = addOption( "port", "port to bind to", "port" );
		Option signalArg = addOption( "cmd", "command (process) to run", "command" );
		Option messageArg = addOption( "sig", "signal to send", "signal" );

		CommandLineParser parser = new GnuParser();
		CommandLine cmd;
		try {
			cmd = parser.parse(options, args);
		} catch ( ParseException pe ) {
			throw new RuntimeException( "Error parsing" , pe );
		}

		String port	= cmd.getOptionValue( portArg.getOpt(), null );
		if ( port == null )
			portNo	= null;
		else
			portNo = Integer.valueOf( port );
		signal = cmd.getOptionValue( messageArg.getOpt(), null );
		command = cmd.getOptionValue( signalArg.getOpt(), null );
	}

	protected void printHelp( String startCommand ) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( startCommand, options);
	}

	@Override public String toString() {
		return "CommandLineOptions [portNo=" + portNo + ", signal=" + signal + ", command=" + command + "]";
	}

	private Option addOption( String opt, String description ) {
		Option option = new Option( opt, description );
		options.addOption(option);
		return option;
	}

	private Option addOption( String opt, String description, String argName ) {
		Option option = new Option( opt, true, description );
		option.setArgName(argName);
		options.addOption(option);
		return option;
	}

}
