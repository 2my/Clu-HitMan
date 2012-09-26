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

	@Test public void test() {
		MockDeadLine sut	= new MockDeadLine( Long.MAX_VALUE );

		// initially infinite deadline
		sut.reset();
		sut.check();
		assertThat( sut.expiredCalled , is( false ) );

		// set expired
		sut	= new MockDeadLine( 100 );
		sut.reset();
		sut.extend( MockDeadLine.messageExpiringIn( -1 ) );
		sut.check();
		assertThat( sut.expiredCalled , is( true ) );

		// was expiry reset (ref 100 ms default above)?
		sut.reset();
		sut.check();
		assertThat( sut.expiredCalled , is( false ) );

		// set future expiry (after past to test overwrite)
		sut.reset();
		sut.extend( MockDeadLine.messageExpiringIn( -1 ) );
		sut.extend( MockDeadLine.messageExpiringIn( 1 ) );
		sut.check();
		assertThat( sut.expiredCalled , is( false ) );

		// see that default employed (should timeout immediately after first timeout)
		sut	= new MockDeadLine( -1 );
		sut.reset();
		sut.extend( MockDeadLine.messageExpiringIn( -1 ) );
		sut.check();
		assertThat( sut.expiredCalled , is( true ) );
		sut.reset();
		sut.check();
		assertThat( sut.expiredCalled , is( true ) );
	}


}
