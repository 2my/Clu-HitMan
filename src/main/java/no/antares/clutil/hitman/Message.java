package no.antares.clutil.hitman;

import org.apache.commons.lang.StringUtils;

public class Message {
	public static final String DEADLINE_SEMAFOR	= "HIT ME IN ";

	public static final Message EMPTY	= new Message( "" );

	private static final int ticksPerSecond	= 1000;

	final String message;

	protected Message(String message) {
		this.message = message;
	}

	protected boolean isExtension() {
		if ( StringUtils.isBlank( this.message ) )
			return false;
		return this.message.startsWith( DEADLINE_SEMAFOR );
	}

	protected long deadLine() {
		String nSeconds	= this.message.replace( DEADLINE_SEMAFOR, "" );
		int seconds2wait	= Integer.parseInt( nSeconds );
		return System.currentTimeMillis() + ( seconds2wait * ticksPerSecond );
	}


}
