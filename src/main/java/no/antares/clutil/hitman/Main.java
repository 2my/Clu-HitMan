package no.antares.clutil.hitman;

import org.apache.commons.lang.StringUtils;

/** 3 functions based on command line arguments
 * ( port+message ): send message to hitMan (on port)
 * ( port+cmd ): start hitMan (on port)
 * ( no port ): print help / usage

java -jar "HitMan-1.0-SNAPSHOT-jar-with-dependencies.jar" -port 5555 -msg "HIT ME IN 5"
java -jar "HitMan-1.0-SNAPSHOT-jar-with-dependencies.jar" -port 5555 -cmd "C:\Program Files\Internet Explorer\iexplore.exe"
 * @author tommy skodje
 */
public class Main {

	/** 3 functions based on command line arguments
	 * ( port+message ): send message to hitMan (on port)
	 * ( port+cmd ): start hitMan (on port)
	 * ( no port ): print help / usage
	*/
	public static void main(String[] args) throws Exception {
		CommandLineOptions options	= new CommandLineOptions( args );
		if ( options.portNo != null ) {
			System.err.println( options.toString() );
			if ( ! StringUtils.isBlank( options.command ) ) {
				HitMan.runHitMan( options.portNo, options.command );
			}
			if ( ! StringUtils.isBlank( options.message ) ) {
				MessageChannel.send( options.portNo, options.message );
			}
		} else {
			options.printHelp( "java -jar HitMan.jar" );
		}
	}

}
