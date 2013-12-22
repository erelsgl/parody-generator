package commonnetty;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;


/**
 * A SocketIOServer, extended with functionalities common to all BIU_NLP_NETTY servers.
 *
 * @author Erel Segal Halevi
 * @since 2013-01
 */
public class CommonSocketIOServer extends SocketIOServer {
	protected static Configuration configurationFromCommandLineArgs(String[] args) {
		Configuration config = new Configuration();
		config.setHostname(args[0]);
		config.setPort(Integer.valueOf(args[1]));
		return config;
	}

	public CommonSocketIOServer(String[] args) {
		this(configurationFromCommandLineArgs(args));
	}

	public CommonSocketIOServer(Configuration configuration) {
		super(configuration);

		// Kill all threads related to this client
		this.addEventListener("abort", Void.class, new DataListener<Void>() {
			@SuppressWarnings("deprecation")
			@Override public void onData(SocketIOClient client, Void data, AckRequest ackSender) {
				MDC.put("client", client.hashCode());
				logger.info("client "+client.hashCode()+" asks to abort "+threads.size()+" threads");
				for (Thread thread: threads) {
					if (thread.isAlive() && !thread.equals(Thread.currentThread())) {
						thread.interrupt(); // stop waiting for locks.
						thread.stop(); // release the reentrant locks
						logger.info("  "+thread+" "+thread.getId()+" interrupted!");
					}
				}
				Logger.getLogger("clientlogger").info("aborted "+threads.size()+" threads");
				threads.clear();
			}
		});
	}

	public <T> void addEventListener(String eventName, Class<T> eventClass, CommonDataListener<T> listener) {
		listener.setEvent(eventName);
		listener.setServer(this);
		super.addEventListener(eventName, eventClass, listener);
	}

	public void addThread(Thread thread) {
		threads.add(thread);
	}
	
	@Override public void start() {
		logger.info(Thread.currentThread().getStackTrace()[2].getClassName()+" listening on "+config.getHostname()+":"+config.getPort());
		super.start();
	}

	final protected List<Thread> threads = new ArrayList<Thread>();

	private static String thisClassName = Thread.currentThread().getStackTrace()[1].getClassName();
	private static final Logger logger = Logger.getLogger(thisClassName);
}
