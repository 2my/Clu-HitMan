package no.antares.clutil.hitman;

import org.apache.commons.lang.Validate;

public class ProcessControl {

	Process process	= null;
	private final String execStr;

	protected final Thread killer	= new Thread() {
		public void run() {
			kill();
	    }
	};

	public ProcessControl( String execStr ) {
		Validate.notNull( execStr, "ProcessControl( null )" );
		this.execStr = execStr;
	}
	public void start() {
		try {
			process = Runtime.getRuntime().exec( execStr );  
		} catch ( Exception e ) {
			throw new RuntimeException( "Error starting process: " + execStr, e);
		}
	}
	public void kill() {
		if ( process != null )
			process.destroy();
    	process	= null;
    }
	public void restart() {
		kill();
		start();
	}
}