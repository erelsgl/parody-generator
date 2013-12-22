package commonnetty;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.*;


/**
 * A class with the beginning issues common to all BIU_NLP_NETTY servers.
 *
 * @author Erel Segal HaleviigurationFileDuplicateKeyException, ConfigurationException, 
 * @since 2013-01
 */
public class CommonBeginner {
	public static void writeFile(File file, String contents) throws IOException {
		if (file == null)
			throw new IOException("got null file");
		if (file.isDirectory())
			throw new IOException(file + " is a directory");

		FileWriter writer = new FileWriter(file);
		writer.write(contents);
		writer.close();
	}

	public static void begin(String[] args, String clientFileName) throws IOException {
		if (args.length<3) {
			System.err.println("ARGUMENTS: <host-name> <port-number> <client-folder>");
			System.exit(1);
		}

		// make sure the ExceptionEvent class is found: 
		try {new DefaultExceptionEvent(null, null);} catch(Throwable ex) {}
		
		// write the port number to a Javascript file accessible to the client
		writeFile(new File(args[2]+File.separator+clientFileName+".port.js"), 
				"var serverport = "+args[1]+";");
		
//		URL log4jConfigurationUrl = Object.class.getResource("/log4j.properties");
//		System.out.println("log4j configuration: "+log4jConfigurationUrl);
//		PropertyConfigurator.configure(log4jConfigurationUrl);
	}
	
	public static void addSocketAppenderToLogger(String loggerName, Level level) {
		Logger.getLogger(loggerName).setLevel(level);
		Logger.getLogger(loggerName).addAppender(new SocketAppender());
		System.out.println(loggerName+"="+level+", socket");
	}
	
	public static void addConsoleAppenderToLogger(String loggerName, Level level) {
		Logger.getLogger(loggerName).setLevel(level);
		Logger.getLogger(loggerName).addAppender(new ConsoleAppender());
		System.out.println(loggerName+"="+level+", console");
	}
	
	public static void addSocketAndConsoleAppenderToLogger(String loggerName, Level level) {
		Logger.getLogger(loggerName).setLevel(level);
		Logger.getLogger(loggerName).addAppender(new SocketAppender());
		Logger.getLogger(loggerName).addAppender(new ConsoleAppender());
		System.out.println(loggerName+"="+level+", socket+console");
	}
	
}
