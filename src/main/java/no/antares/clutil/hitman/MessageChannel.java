package no.antares.clutil.hitman;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

class MessageChannel implements Closeable {
	private final ServerSocket serverSocket;
	private static final String HOST	= "localhost";

	/**  */
	protected static MessageChannel openInbound( int port ) {
		try {
			return new MessageChannel( new ServerSocket( port ) );
		} catch (IOException e) {
			throw new RuntimeException("Could not listen on port: " + port, e);
		}
	}

	protected static void send( int port, String message ) throws IOException {
        Socket kkSocket = null;
        PrintWriter out = null;
        try {
            kkSocket = new Socket( HOST, port );
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
            System.err.println( "Couldn't get I/O for the connection to: " + HOST );
        } finally {
            close( out );
            close( kkSocket );
        }
    }

	private MessageChannel( ServerSocket serverSocket ) {
		this.serverSocket	= serverSocket;
	}

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
	public void close() {
		try {
			if ( serverSocket != null)
				serverSocket.close();
		} catch (IOException ioe) {
		}
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
