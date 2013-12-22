package commonnetty;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;

/**
 * A simple socket.io server, for testing. Supports a single service - echo.
 *
 * @author Erel Segal the Levite
 * @since 2013-05
 */
public class EchoServer {
	public static void main(String[] args) throws Exception {
		if (args.length<2)
			throw new IllegalArgumentException("SYNTAX: EchoServer [host] [port]");
		final CommonSocketIOServer networkServer = new CommonSocketIOServer(args);
		networkServer.addEventListener("echo", String.class, new CommonDataListener<String>(logger) {
			@Override public void onDataSub(SocketIOClient client, String request, AckRequest ackRequest) throws Throwable{
				client.sendEvent("echo", request+" "+request);
			}
		});
		networkServer.start();
	}

	private static String thisClassName = Thread.currentThread().getStackTrace()[1].getClassName();
	private static final LoggerWithStack logger = LoggerWithStack.getLogger(thisClassName);
}
