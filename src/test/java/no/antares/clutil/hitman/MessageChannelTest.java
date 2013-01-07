/* MessageChannelTest.java
   Copyright 2012 Tommy Skodje

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

import static org.doxla.eventualj.Eventually.eventually;
import static org.doxla.eventualj.EventuallyMatchers.willBe;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MessageChannelTest {
	int sensibleWait	= 20;

	int port	= 9999;
	ConcurrentLinkedQueue<Message> received	= new ConcurrentLinkedQueue<Message>();

	boolean done	= false;
	Thread channelThread	= new Thread( "ChannelThread" ) {
		public void run() {
			MessageChannel channel	= MessageChannel.openInbound( port );
			while ( ! done )
				received.add( channel.waitForNextMessage() );
			channel.close();
		}
	};

	@Before public void setUp() throws Exception {
		done	= false;
	}

	@After public void tearDown() throws Exception {
		done	= true;
		// channelThread.interrupt();
	}

	@Test public void send_EXPIRY() throws Exception {
		String msg	= MockDeadLine.messageExpiringIn( 1 ).message;
		channelThread.start();
		String response	= MessageChannel.send( port, msg );
		assertThat( response, is( "" ) );
		assertThat( eventually( received ).size(), willBe( 1 ) );
		assertThat( eventually( this ).getMsg(), willBe( msg ) );
	}

	@Test public void send_no_server() throws Exception {
		String msg	= MockDeadLine.messageExpiringIn( 1 ).message;
		String response	= MessageChannel.send( port, msg );
		assertThat( response, is( "" ) );
		assertThat( eventually( received ).size(), willBe( 0 ) );
	}

	@Test public void send_PING() throws Exception {
		String msg	= Message.Semafor.PING.msgStart;
		channelThread.start();
		String response	= MessageChannel.send( port, msg );
		assertThat( response, is( "PONG" ) );
		assertThat( eventually( received ).size(), willBe( 1 ) );
		assertThat( eventually( this ).getMsg(), willBe( msg ) );
	}
	public String getMsg() {
		Message msg	= received.remove();
		if ( msg != null )
			return msg.message;
		return null;
	}

}
