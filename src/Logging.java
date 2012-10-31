import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Logging {
	private String logType;
	
	
	public static enum LoggingLevel {
		INFO, WARNING, SEVERE;
	}

	public Logging(String componentName) {
		logType = componentName;
	}

	public void addLog(LoggingLevel logLevel, String loggingMessage) {	
		Logger moustaskLogger = Logger.getLogger(logType);
		moustaskLogger.setUseParentHandlers(false);
		try {
			FileHandler fileHandler = new FileHandler("moustaskLog.log", true);
			moustaskLogger.addHandler(fileHandler);		
			
			if (logLevel == LoggingLevel.INFO) {
				moustaskLogger.log(Level.INFO, loggingMessage);
			} else if (logLevel == LoggingLevel.WARNING) {
				moustaskLogger.log(Level.WARNING, loggingMessage);
			} else if (logLevel == LoggingLevel.SEVERE) {
				moustaskLogger.log(Level.SEVERE, loggingMessage);
			}
			fileHandler.close();
		}
		catch (IOException ex){
			moustaskLogger.log(Level.SEVERE, loggingMessage,ex);
		}
	}
}