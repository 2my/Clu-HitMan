package no.antares.clutil.hitman;

import org.apache.commons.cli.*;

/**
 * @author tommy skodje
*/
class CommandLineOptions {
	final Integer portNo;
	final String message;
	final String command;

	private final Options options = new Options();

	protected CommandLineOptions( String[] args ) {
		Option help = addOption( "help", "print this message" );
		Option portArg = addOption( "port", "port to bind to", "port" );
		Option commandArg = addOption( "cmd", "command (process) to run", "command" );
		Option messageArg = addOption( "msg", "message to send", "message" );

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
		message = cmd.getOptionValue( messageArg.getOpt(), null );
		command = cmd.getOptionValue( commandArg.getOpt(), null );
	}

	protected void printHelp( String startCommand ) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( startCommand, options);
	}

	@Override public String toString() {
		return "CommandLineOptions [portNo=" + portNo + ", signal=" + message + ", command=" + command + "]";
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
