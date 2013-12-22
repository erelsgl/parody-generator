package commonnetty;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import com.corundumstudio.socketio.SocketIOClient;

/**
 * Send log messages to web clients.
 * 
 * @author Erel Segal Halevi
 * @since 2013-01-01
 */
public class SocketAppender extends AppenderSkeleton {

	/**
	 * Encapsulates a log message sent from the server to the client
	 */
	static class LoggingMessage {
		public String message;
		public String sender;
		public String thread;
		public int depth;
		public LoggingMessage(String message, String sender, String threadName, int depth) {
			this.message = message;
			this.sender = sender;
			this.depth = depth;
			this.thread = threadName;
		}
		@Override public String toString() {
			return "LoggingMessage [message=" + message + ", sender=" + sender
					+ ", depth=" + depth + "]";
		}
	}
	
	@Override public boolean requiresLayout() {
		return false;
	}

	@Override protected void append(LoggingEvent event) {
		SocketIOClient client = (SocketIOClient)event.getMDC("client");
		String eventLevel = event.getLevel().toString().toLowerCase();
		if (client!=null) {
			String loggerName = event.getLoggerName();
			if (loggerName.equals("clientlogger"))
				loggerName="Server";
			else {
				int iLastDot = loggerName.lastIndexOf(".");
				if (iLastDot>0)
					loggerName = loggerName.substring(iLastDot+1);
			}
			String threadName = event.getThreadName();
			int depth = Thread.currentThread().getStackTrace().length;
			client.sendEvent(eventLevel, new LoggingMessage(event.getMessage().toString(), loggerName, threadName, depth));
		}
	}

	@Override public void close() {
		// nothing to close
	}
}
