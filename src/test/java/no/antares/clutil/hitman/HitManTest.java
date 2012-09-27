/* HitManTest.java
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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import no.antares.clutil.hitman.process.ProcessControl;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class HitManTest {

	MessageChannel channel	= mock( MessageChannel.class );

	int nStarts	= 0;
	int nKills	= 0;
	int nTerminations	= 0;
	ProcessControl pc	= new ProcessControl() {
		@Override public void start() {
			nStarts++;
		}
		@Override public void kill() {
			nKills++;
		}
		@Override public void restart() {
			kill();
			start();
		}
		@Override public void shutDownAll() {
			nTerminations++;
		}
	};

	private static int hitManCheckPeriodMillis	= 1;
	private static int hitManRampUp	= 20;	// takes some time to get going
	private static int severalHitManPeriods	= 20;	// so that hitMan checks deadLine more than once
	private static int hitManExtraWaitAndThenSome	= 150;

	@Test public void test() throws Exception {
		when( channel.waitForNextMessage() ).thenReturn( MockDeadLine.messageExpiringIn( 100 ) );
		Thread msgLoop	= new Thread( "HitMan" ) {
			public void run() {
				HitMan.runHitMan( channel, pc, hitManCheckPeriodMillis, Long.MAX_VALUE );
			}
		};
		msgLoop.start();
		Thread.sleep( hitManRampUp );
		assertThat( nStarts, is( 1 ) );
		assertThat( nKills, is( 0 ) );

		when( channel.waitForNextMessage() ).thenAnswer( expireWaitNoExpire );
		makeSureHitManCouldCheck();
		assertThat( nStarts, is( 2 ) );
		assertThat( nKills, is( 1 ) );
		assertThat( nTerminations, is( 0 ) );

		when( channel.waitForNextMessage() ).thenAnswer( terminateWaitNoExpire );
		Thread.sleep( hitManExtraWaitAndThenSome ); 
		assertThat( nStarts, is( 2 ) );
		assertThat( nKills, is( 1 ) );
		assertThat( nTerminations, is( 1 ) );
	}

	private void makeSureHitManCouldCheck() throws InterruptedException {
		Thread.sleep( severalHitManPeriods + 2 );
	}

	Answer<Message> expireWaitNoExpire	= waitBetween(
			MockDeadLine.messageExpiringIn( -1 ),
			MockDeadLine.messageExpiringIn( 100 )
		);

	Answer<Message> terminateWaitNoExpire	= waitBetween(
			MockDeadLine.messageTerminateIn( 0 ),
			MockDeadLine.messageExpiringIn( 100 )
		);

	Answer<Message> waitBetween( final Message msg1, final Message msg2 ) {
		return new Answer<Message>() {
			int calls	= 0;
			@Override public Message answer(InvocationOnMock invocation) throws Throwable {
				calls++;
				if ( calls < 2 )
					return msg1;
				Thread.sleep( severalHitManPeriods );
				return msg2;
			}
		};
	};
}
