package no.antares.clutil.hitman;

import java.util.ArrayList;
import java.util.List;

public class HitManUtils {

	/** Parse a command-line command splitting it in parts. */
	public static List<String> options( String command ) {
		String[] parts	= command.split( " " );
		List<String> options	= new ArrayList<String>();
		StringBuffer quoted	= null;
		for ( int i = 0; i < parts.length; i++ ) {
			String part	= parts[i];
			if ( quoted != null ) {
				if ( part.endsWith( "\"" ) ) {
					quoted.append( " " ).append( part.substring( 0, part.length()-1 ) );
					options.add( quoted.toString() );
					quoted	= null;
				} else
					quoted.append( part );
			} else if ( part.startsWith( "\"" ) ) {
				quoted	= new StringBuffer();
				quoted.append( part.substring( 1 ) );
			} else
				options.add( part );
		}
		return options;
	}

}
