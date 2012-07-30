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
	public static final String DEADLINE_SEMAFOR	= "HIT ME IN ";

	public static final Message EMPTY	= new Message( "" );

	private static final int ticksPerSecond	= 1000;

	final String message;

	/** Parses argument and interpretes message */
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
