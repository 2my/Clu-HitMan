/* DeadLineCheckerTest.java
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

import static org.doxla.eventualj.Eventually.eventually;
import static org.doxla.eventualj.EventuallyMatchers.willBe;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class DeadLineCheckerTest {
	MockDeadLine deadLine	= new MockDeadLine( Long.MAX_VALUE );

	@Test public void oneOff() throws Exception {
		deadLine.reset();
		DeadLineChecker sut	= DeadLineChecker.oneOff( deadLine );
		assertThat( eventually( deadLine ).checkCalls(), willBe( 0 ) );

		sut.startInMillis( 1 );
		assertThat( eventually( deadLine ).checkCalls(), willBe( 1 ) );
	}

	@Test public void periodical() throws Exception {
		deadLine.reset();
		DeadLineChecker sut	= DeadLineChecker.periodical( deadLine, 1 );
		assertThat( eventually( deadLine ).checkCalls(), willBe( 0 ) );

		sut.startInMillis( 1 );
		assertThat( eventually( deadLine ).checkCalls(), willBe( greaterThan( 1 ) ) );
	}

}
