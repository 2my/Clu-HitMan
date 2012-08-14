/* MessageChannel.java
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

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/** Channel for receiving and sending Message(s).
 * @author tommy skodje
 */
public class MessageChannel implements Closeable {
	private final ServerSocket serverSocket;

	/** Send message to HitMan */
	public static void send( int port, String message ) throws IOException {
		send( "localhost", port, message );
	}

	/** Send message to HitMan (on localhost) */
	public static void send( String host, int port, String message ) throws IOException {
        Socket kkSocket = null;
        PrintWriter out = null;
        try {
            kkSocket = new Socket( host, port );
            out = new PrintWriter(kkSocket.getOutputStream(), true);
            out.println( message );
            /*
        	BufferedReader in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
            String fromServer;
            while ((fromServer = in.readLine()) != null) {
                System.out.println("Server: " + fromServer);
                if (fromServer.equals("Bye."))
                    break;
            }
            close( in );*/
        } catch (IOException e) {
            System.err.println( "Couldn't get I/O for the connection to: " + host );
        } finally {
            close( out );
            close( kkSocket );
        }
    }

	/** Open a channel - close when done */
	protected static MessageChannel openInbound( int port ) {
		try {
			return new MessageChannel( new ServerSocket( port ) );
		} catch (IOException e) {
			throw new RuntimeException("Could not listen on port: " + port, e);
		}
	}

	private MessageChannel( ServerSocket serverSocket ) {
		this.serverSocket	= serverSocket;
	}

	/** Close the channel */
	public void close() {
		try {
			if ( serverSocket != null)
				serverSocket.close();
		} catch (IOException ioe) {
		}
	}

	/** Blocks while waiting for message */
	protected Message waitForNextMessage() {
		Socket clientSocket = null;
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			clientSocket = serverSocket.accept();
			in = new BufferedReader(new InputStreamReader( clientSocket.getInputStream()));
			String inputLine	= in.readLine();
			if ( inputLine != null ) {
				return new Message( inputLine );
				/*
				if ( ! StringUtils.isBlank(outputLine) ) {
					out = new PrintWriter( clientSocket.getOutputStream(), true);
					out.println(outputLine);
					if (outputLine.equals("Bye."))
						stopped = true;
				}*/
			}
		} catch (IOException e) {
			System.err.println("Accept failed.");
		} finally {
			close( clientSocket );
			close( out );
			close( in );
		}
		return Message.EMPTY;
	}

	private static void close(Closeable s) {
		try {
			if (s != null)
				s.close();
		} catch (IOException ioe) {
		}
	}

	private static void close(Socket s) {
		try {
			if (s != null)
				s.close();
		} catch (IOException ioe) {
		}
	}

}
