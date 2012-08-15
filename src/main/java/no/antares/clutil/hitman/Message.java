/* Message.java
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

import org.apache.commons.lang.StringUtils;

/** Message(s) understood by HitMan.
 * @author tommy skodje
 */
public class Message {
	public static enum Semafor {
		NONE( "" ),
		DEADLINE( "HIT ME IN " ),
		TERMINATE( "HIT US IN " )
		;

		private final String msgStart;

		private Semafor( String msgStart ) {
			this.msgStart	= msgStart;
		}

		public String messageAfterSemafor( String msg ) {
			return msg.replace( msgStart, "" );
		}
	}

	public static final Message EMPTY	= new Message( "" );

	private static final int ticksPerSecond	= 1000;

	final String message;
	final Semafor messageType;

	/** Parses argument and interpretes message */
	protected Message(String message) {
		this.message = message;
		if ( StringUtils.isBlank( this.message ) )
			messageType	= Semafor.NONE;
		else if ( this.message.startsWith( Semafor.DEADLINE.msgStart ) )
			messageType	= Semafor.DEADLINE;
		else if ( this.message.startsWith( Semafor.TERMINATE.msgStart ) )
			messageType	= Semafor.TERMINATE;
		else
			messageType	= Semafor.NONE;
	}

	protected boolean isExtension() {
		return Semafor.DEADLINE == this.messageType;
	}

	protected boolean isTermination() {
		return Semafor.TERMINATE == this.messageType;
	}

	protected long deadLine() {
		String nSeconds	= messageType.messageAfterSemafor( this.message );
		int seconds2wait	= Integer.parseInt( nSeconds );
		return ( seconds2wait * ticksPerSecond ) + System.currentTimeMillis();
	}


}
