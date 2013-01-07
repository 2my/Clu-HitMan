/* MockDeadLine.java
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

public class MockDeadLine extends DeadLine {
	public MockDeadLine( long initialTimeOut ) {
		super( initialTimeOut );
	}

	boolean expiredCalled	= false;
	int checkCalls	= 0;

	static Message messageExpiringIn( int nSeconds ) {
		return new Message( Message.Semafor.DEADLINE.inSeconds( nSeconds ) );
	}
	static Message messageTerminateIn( int nSeconds ) {
		return new Message( Message.Semafor.TERMINATE.inSeconds( nSeconds ) );
	}

	public int checkCalls() {
		return checkCalls;
	}
	void reset() {
		expiredCalled	= false;
		checkCalls	= 0;
	}

	@Override void expired() {
		expiredCalled	= true;
	}

	@Override protected void check() {
		checkCalls++;
		super.check();
	}

}
