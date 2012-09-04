/* DeadLineTest.java
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

public class DeadLineTest {
	MockDeadLine sut	= new MockDeadLine();

	@Test public void test() {
		// initially infinite deadline
		sut.reset();
		sut.check();
		assertThat( sut.expiredCalled , is( false ) );

		// set expired
		sut.reset();
		sut.extend( MockDeadLine.messageExpiringIn( -1 ) );
		sut.check();
		assertThat( sut.expiredCalled , is( true ) );

		// was expiry reset?
		sut.reset();
		sut.check();
		assertThat( sut.expiredCalled , is( false ) );

		// set future expiry (after past to test overwrite)
		sut.reset();
		sut.extend( MockDeadLine.messageExpiringIn( -1 ) );
		sut.extend( MockDeadLine.messageExpiringIn( 1 ) );
		sut.check();
		assertThat( sut.expiredCalled , is( false ) );
	}


}