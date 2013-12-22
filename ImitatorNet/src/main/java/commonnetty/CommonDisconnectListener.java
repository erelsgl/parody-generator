package commonnetty;

import org.apache.log4j.MDC;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DisconnectListener;

/**
 * A DataListener, extended with functionalities common to all BIU_NLP_NETTY servers.
 *
 * @author Erel Segal Halevi
 * @since 2013-01
 */
public class CommonDisconnectListener implements DisconnectListener {
	public CommonDisconnectListener(LoggerWithStack logger) {
		this.logger = logger;
	}

	@Override public void onDisconnect(SocketIOClient client) {
		MDC.put("client", client);  // this is the client that will accept the logs
		try {
			logger.info("Bye, client "+client.hashCode());
			onDisconnectSub(client);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * A subroutine of onDisconnect.
	 */
	public void onDisconnectSub(SocketIOClient client) throws Throwable {
		
	}
	
	private LoggerWithStack logger;
}
