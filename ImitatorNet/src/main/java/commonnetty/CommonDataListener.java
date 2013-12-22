package commonnetty;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;

/**
 * A DataListener, extended with functionalities common to all BIU_NLP_NETTY servers.
 *
 * @author Erel Segal Halevi
 * @since 2013-01
 */
public abstract class CommonDataListener<T> implements DataListener<T> {

	public CommonDataListener(LoggerWithStack logger) {
		this.logger = logger;
		this.callerClassName = Thread.currentThread().getStackTrace()[3].getClassName().replaceAll(".*[.]", "");
	}
	
	public void setEvent(String event) {
		this.event = event;
	}
	
	public void setServer(CommonSocketIOServer server) {
		this.server = server;
	}

	@Override public void onData(SocketIOClient client, T request, AckRequest ackSender) {
		MDC.put("client", client);  // this is the client that will accept the logs
		String requestString = request.toString();
		int maxLength = 100;
		if (requestString.length()>maxLength)
			requestString = requestString.substring(0,maxLength);
		logger.info("client "+client.hashCode()+" requests "+event+": "+requestString);
		server.addThread(Thread.currentThread());
		try {
			Logger.getLogger("clientlogger").info(callerClassName+" received your request to "+event+(Logger.getLogger("clientlogger").isDebugEnabled()? "  ("+request+")": ""));
			onDataSub(client, request, ackSender);
		} catch (Throwable ex) {
			client.sendEvent("exception", ex);
			//Logger.getLogger("clientlogger").error(ExceptionUtil.getStackTrace(ex));
			ex.printStackTrace();
		}
	}
	
	public void simulateAnError() {
		logger.info("simulating an error");
		throw new Error("An awful error has occured!");
	}
	
	public void simulateALivelock() {
		logger.info("looping forever");
		for (;;);
	}

	/**
	 * A subroutine of onData.
	 */
	abstract public void onDataSub(SocketIOClient client, T request, AckRequest ackSender) throws Throwable;
	
	private String callerClassName;
	private LoggerWithStack logger;
	private String event;
	private CommonSocketIOServer server;
}
