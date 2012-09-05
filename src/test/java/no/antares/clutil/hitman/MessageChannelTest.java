package no.antares.clutil.hitman;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Test;

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

	@Test public void send() throws Exception {
		String msg	= MockDeadLine.messageExpiringIn( 1 ).message;
		channelThread.start();
		String response	= MessageChannel.send( port, msg );
		Thread.sleep( sensibleWait );
		assertThat( response, is( "" ) );
		assertThat( received.size(), is( 1 ) );
		assertThat( received.remove().message, is( msg ) );

		msg	= Message.Semafor.PING.msgStart;
		response	= MessageChannel.send( port, msg );
		Thread.sleep( sensibleWait );
		assertThat( response, is( "PONG" ) );
		assertThat( received.size(), is( 1 ) );
		assertThat( received.remove().message, is( msg ) );
	}

}
