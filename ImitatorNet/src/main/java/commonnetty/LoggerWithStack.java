package commonnetty;

import java.util.*;
import org.apache.log4j.*;

/**
 * A {@link Logger} with a stack of actions. Allows to log the start and end of each action.
 * 
 * @note This logger is not thread safe, so it must not be static! There should be a different stack-logger in each thread. 
 *
 * @author Erel Segal Halevi
 * @since 2012
 */
public class LoggerWithStack {
	private LoggerWithStack(Logger logger) {
		this.logger = logger;
		actionTitles = new Stack<String>();
		actionStartMillis = new Stack<Long>();
	}
	
	public static LoggerWithStack getLogger (String name) {
		return new LoggerWithStack(Logger.getLogger(name));
	}
	
	public static LoggerWithStack getLogger (Class<?> clazz) {
		return new LoggerWithStack(Logger.getLogger(clazz));
	}
	
	private Stack<String> actionTitles;
	private String popTitle() {
		try {
			return actionTitles.pop();
		} catch (EmptyStackException ex) {
			return "[anonymous action]";
		}
	}
	
	private Stack<Long> actionStartMillis;
	private String popTimeDifference() {
		try {
			return String.valueOf(System.currentTimeMillis()-actionStartMillis.pop()) + " ms";
		} catch (EmptyStackException ex) {
			return "[unknown time]";
		}
	}
	
	
	public void startAction(String title) {
		actionTitles.push(title);
		actionStartMillis.push(System.currentTimeMillis());
		logger.info("[startAction] "+title);
		//System.err.println(title+" start"); 
	}

	public void endAction() {
		if (logger.isInfoEnabled()) 
			logger.info("[endAction: "+popTimeDifference()+"] "+popTitle());
	}

	public <T> T endAction(String shortResult, T result) {
		if (logger.isInfoEnabled()) { 
			if (result!=null)
				logger.debug(result);
			logger.info("[endAction: "+popTimeDifference()+"] "+popTitle()+" result: "+shortResult);
		}
		return result;
	}

	public void errorInAction(Throwable t) {
		logger.error("[endAction: ERROR: "+popTimeDifference()+"] "+popTitle()+"\n"/*+Utils.stackTraceToString(t)*/);
	}
	
	
	
	/**
	 * @param message
	 * @see org.apache.log4j.Logger#trace(java.lang.Object)
	 */
	public void trace(Object message) {
		logger.trace(message);
	}

	/**
	 * @param message
	 * @param t
	 * @see org.apache.log4j.Logger#trace(java.lang.Object, java.lang.Throwable)
	 */
	public void trace(Object message, Throwable t) {
		logger.trace(message, t);
	}

	/**
	 * @return
	 * @see org.apache.log4j.Logger#isTraceEnabled()
	 */
	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	/**
	 * @param message
	 * @see org.apache.log4j.Category#debug(java.lang.Object)
	 */
	public void debug(Object message) {
		logger.debug(message);
	}

	/**
	 * @param message
	 * @param t
	 * @see org.apache.log4j.Category#debug(java.lang.Object, java.lang.Throwable)
	 */
	public void debug(Object message, Throwable t) {
		logger.debug(message, t);
	}

	/**
	 * @param message
	 * @see org.apache.log4j.Category#error(java.lang.Object)
	 */
	public void error(Object message) {
		logger.error(message);
	}

	/**
	 * @param message
	 * @param t
	 * @see org.apache.log4j.Category#error(java.lang.Object, java.lang.Throwable)
	 */
	public void error(Object message, Throwable t) {
		logger.error(message, t);
	}

	/**
	 * @param message
	 * @see org.apache.log4j.Category#fatal(java.lang.Object)
	 */
	public void fatal(Object message) {
		logger.fatal(message);
	}

	/**
	 * @param message
	 * @param t
	 * @see org.apache.log4j.Category#fatal(java.lang.Object, java.lang.Throwable)
	 */
	public void fatal(Object message, Throwable t) {
		logger.fatal(message, t);
	}

	/**
	 * @return
	 * @see org.apache.log4j.Category#getEffectiveLevel()
	 */
	public Level getEffectiveLevel() {
		return logger.getEffectiveLevel();
	}

	/**
	 * @return
	 * @see org.apache.log4j.Category#getName()
	 */
	public final String getName() {
		return logger.getName();
	}

	/**
	 * @return
	 * @see org.apache.log4j.Category#getParent()
	 */
	public final Category getParent() {
		return logger.getParent();
	}

	/**
	 * @return
	 * @see org.apache.log4j.Category#getLevel()
	 */
	public final Level getLevel() {
		return logger.getLevel();
	}

	/**
	 * @param message
	 * @see org.apache.log4j.Category#info(java.lang.Object)
	 */
	public void info(Object message) {
		logger.info(message);
	}

	/**
	 * @param message
	 * @param t
	 * @see org.apache.log4j.Category#info(java.lang.Object, java.lang.Throwable)
	 */
	public void info(Object message, Throwable t) {
		logger.info(message, t);
	}

	/**
	 * @return
	 * @see org.apache.log4j.Category#isDebugEnabled()
	 */
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	/**
	 * @param level
	 * @return
	 * @see org.apache.log4j.Category#isEnabledFor(org.apache.log4j.Priority)
	 */
	public boolean isEnabledFor(Priority level) {
		return logger.isEnabledFor(level);
	}

	/**
	 * @return
	 * @see org.apache.log4j.Category#isInfoEnabled()
	 */
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	/**
	 * @param priority
	 * @param message
	 * @param t
	 * @see org.apache.log4j.Category#log(org.apache.log4j.Priority, java.lang.Object, java.lang.Throwable)
	 */
	public void log(Priority priority, Object message, Throwable t) {
		logger.log(priority, message, t);
	}

	/**
	 * @param priority
	 * @param message
	 * @see org.apache.log4j.Category#log(org.apache.log4j.Priority, java.lang.Object)
	 */
	public void log(Priority priority, Object message) {
		logger.log(priority, message);
	}

	/**
	 * @param callerFQCN
	 * @param level
	 * @param message
	 * @param t
	 * @see org.apache.log4j.Category#log(java.lang.String, org.apache.log4j.Priority, java.lang.Object, java.lang.Throwable)
	 */
	public void log(String callerFQCN, Priority level, Object message,
			Throwable t) {
		logger.log(callerFQCN, level, message, t);
	}

	/**
	 * @param additive
	 * @see org.apache.log4j.Category#setAdditivity(boolean)
	 */
	public void setAdditivity(boolean additive) {
		logger.setAdditivity(additive);
	}

	/**
	 * @param level
	 * @see org.apache.log4j.Category#setLevel(org.apache.log4j.Level)
	 */
	public void setLevel(Level level) {
		logger.setLevel(level);
	}

	/**
	 * @param message
	 * @see org.apache.log4j.Category#warn(java.lang.Object)
	 */
	public void warn(Object message) {
		logger.warn(message);
	}

	/**
	 * @param message
	 * @param t
	 * @see org.apache.log4j.Category#warn(java.lang.Object, java.lang.Throwable)
	 */
	public void warn(Object message, Throwable t) {
		logger.warn(message, t);
	}



	private Logger logger;
}
