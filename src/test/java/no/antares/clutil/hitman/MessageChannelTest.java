package no.antares.clutil.hitman;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.*;

public class MessageChannelTest {
	int sensibleWait	= 20;

	int port	= 3333;
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
		Thread.sleep( sensibleWait );
		assertThat( response, is( "" ) );
		assertThat( received.size(), is( 1 ) );
		assertThat( received.remove().message, is( msg ) );
	}

	@Test public void send_no_server() throws Exception {
		String msg	= MockDeadLine.messageExpiringIn( 1 ).message;
		String response	= MessageChannel.send( port, msg );
		Thread.sleep( sensibleWait );
		assertThat( response, is( "" ) );
		assertThat( received.size(), is( 0 ) );
	}

	@Test public void send_PING() throws Exception {
		String msg	= Message.Semafor.PING.msgStart;
		channelThread.start();
		String response	= MessageChannel.send( port, msg );
		Thread.sleep( sensibleWait );
		assertThat( response, is( "PONG" ) );
		assertThat( received.size(), is( 1 ) );
		assertThat( received.remove().message, is( msg ) );
	}

}
