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

import static org.doxla.eventualj.Eventually.eventually;
import static org.doxla.eventualj.EventuallyMatchers.willBe;
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
	public int getNumStarts() { return nStarts; }
	public int getNumKills() { return nKills; }
	public int getNumTerminations() { return nTerminations; }
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

	@Test public void test() throws Exception {
		when( channel.waitForNextMessage() ).thenReturn( MockDeadLine.messageExpiringIn( 100 ) );
		Thread msgLoop	= new Thread( "HitMan" ) {
			public void run() {
				HitMan.runHitMan( channel, pc, hitManCheckPeriodMillis, Long.MAX_VALUE );
			}
		};
		msgLoop.start();
		assertThat( eventually( this ).getNumStarts(), willBe( 1 ) );
		assertThat( eventually( this ).getNumKills(), willBe( 0 ) );

		when( channel.waitForNextMessage() ).thenAnswer( expireWaitNoExpire );
		assertThat( eventually( this ).getNumStarts(), willBe( 2 ) );
		assertThat( eventually( this ).getNumKills(), willBe( 1 ) );
		assertThat( eventually( this ).getNumTerminations(), willBe( 0 ) );

		when( channel.waitForNextMessage() ).thenAnswer( terminateWaitNoExpire );
		assertThat( eventually( this ).getNumStarts(), willBe( 2 ) );
		assertThat( eventually( this ).getNumKills(), willBe( 1 ) );
		assertThat( eventually( this ).getNumTerminations(), willBe( 1 ) );
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
			final int severalHitManPeriods	= 20 * hitManCheckPeriodMillis;	// so that hitMan checks deadLine more than once
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
