package com.hit.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class MMULogger {
	public final static String DEFAULT_FILE_NAME = "./logs/log.txt";
	private static FileHandler handler;
	private static MMULogger _instance = null;
	private static Object _lock = new Object();
	private static Object _lock2 = new Object();
	private static OnlyMessageFormatter formatter;
	
	
	// Private default constructor for creating a handler and setting the handler format.
	private MMULogger() {
		formatter = new OnlyMessageFormatter();
		setHandler();
	}
	
	
	private  static void setHandler() {
		try {
			File logFile = new File(DEFAULT_FILE_NAME);
			if(logFile.exists()) {
				logFile.delete();
			}
			handler = new FileHandler(DEFAULT_FILE_NAME);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
		handler.setFormatter(formatter);
	}
	
	
	// getInstance method for implementing single tone pattern.
	// Return:
	//  	instance of MMULogger class.
	public static MMULogger getInstance() {
		
		if(_instance == null) {
			synchronized(_lock) {
				if(_instance == null) {
					_instance = new MMULogger();
				}
			}
		}
		if(handler == null) {
			synchronized(_lock2) {
				if(handler == null) {					
					setHandler();
				}
			}
		}
		return _instance;
	}
	
	// This method used for writing a log record with specified level.
	public synchronized void write(String command, Level level) {
		handler.publish(new LogRecord(level, command));
		// handler.close();
	}
	
	public void close() {
		if(handler != null) {
			handler.close();
			handler = null;
		}
	}
	
	// Nested formatter class for handler.
	public class OnlyMessageFormatter extends Formatter {

		public OnlyMessageFormatter() {
			super();
		}
		
		@Override
		public String format(final LogRecord record) {
			return record.getMessage();
		}
		
	}
}
