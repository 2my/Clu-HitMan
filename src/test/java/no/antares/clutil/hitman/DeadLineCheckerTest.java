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

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class DeadLineCheckerTest {
	MockDeadLine deadLine	= new MockDeadLine();

	@Test public void oneOff() throws Exception {
		deadLine.reset();
		DeadLineChecker sut	= DeadLineChecker.oneOff( deadLine );
		Thread.sleep( 2 );
		assertThat( deadLine.checkCalls, is( 0 ) );

		sut.startInMillis( 1 );
		Thread.sleep( 3 );
		assertThat( deadLine.checkCalls, is( 1 ) );
	}

	@Test public void periodical() throws Exception {
		deadLine.reset();
		DeadLineChecker sut	= DeadLineChecker.periodical( deadLine, 1 );
		Thread.sleep( 2 );
		assertThat( deadLine.checkCalls, is( 0 ) );

		sut.startInMillis( 1 );
		Thread.sleep( 3 );
		assertThat( deadLine.checkCalls, greaterThan( 1 ) );
	}

}