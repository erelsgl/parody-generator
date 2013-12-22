package commonnetty;

import org.apache.log4j.MDC;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.ConnectListener;

/**
 * A DataListener, extended with functionalities common to all BIU_NLP_NETTY servers.
 *
 * @author Erel Segal Halevi
 * @since 2013-01
 */
public class CommonConnectListener implements ConnectListener {
	public CommonConnectListener(LoggerWithStack logger) {
		this.logger = logger;
		//this.callerClassName = Thread.currentThread().getStackTrace()[3].getClassName();
	}

	@Override public void onConnect(SocketIOClient client) {
		MDC.put("client", client);  // this is the client that will accept the logs
		try {
			logger.info("Welcome, client "+client.hashCode());
			onConnectSub(client);
		} catch (Throwable ex) {
			client.sendEvent("exception", ex);
			//Logger.getLogger("clientlogger").error(ExceptionUtil.getStackTrace(ex));
			ex.printStackTrace();
		}
	}

	/**
	 * A subroutine of onConnect.
	 */
	public void onConnectSub(SocketIOClient client) throws Throwable {
		
	}
	
	private LoggerWithStack logger;
}
