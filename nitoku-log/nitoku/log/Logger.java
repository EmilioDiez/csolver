/*
 *  Javascript Constraint Solver (CSolver) Copyright (c) 2016, 
 *  Emilio Diez,All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 */

package nitoku.log;

import java.util.logging.Handler;
import java.util.logging.Level;

import com.google.gwt.core.shared.GWT;


/**
 * 
 * A Logger that will output to System console or Browser console
 * 
 * It uses elemental to print to Js Console. It uses DevelopmentModeLogHandler
 * to print to GWTTestCase system console
 *
 *
 * LIMITATIONS: - Can't configure the log level for JRE VM - Logging out for
 * GWTTest case only logs out severe level messages - Only way to know that we
 * are in a GWTestCase is extending GWTLoggerTestCase
 * 
 * @author emilio
 *
 */
public class Logger {

	/**
	 * 
	 * This flag need to be set to true with selenium tests and false
	 * to run production javascript
	 * 
	 */
	public final static boolean DEV_MODE = true;
	public final static String APP_VERSION = "1.0.13.1";
	
	public final static String PRODUCTION_NITOKU_PUBLIC_TEAM_ID = "92bd5drkexzkgbbk";
	
	//245tanda2.public
	public final static String DEV_NITOKU_PUBLIC_TEAM_ID = "dqp512rohyezepw8";
	
	
	static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("Nitoku");
	
	private static Boolean hasBeenInitialized = false;
	public static boolean errorSent = false;
	
	private static IServer server;
	
	/**
	 * For general output of logging information. You may use string
	 * substitution and additional arguments with this method.
	 */
	private static void out(Level level, Class c, String... message) {

		initializeClient();

		if (!logger.isLoggable(level)) {
			return;
		}

		if (GWT.isClient()) {

			clientOut(level, c, message);
			//save this on the LogList
			
		} else {
			// system log
			// Server Logging
			for (String arg : message) {
				logger.log(level, getMessage(level, c, arg));
			}
		}

	}

	private static void clientOut(Level level, Class c, String... args) {

		initializeClient();

		if (!logger.isLoggable(level)) {
			return;
		}

		for (String arg : args) {
			logger.log(level, "############################## "+ getMessage(level, c, arg));
			if(level == Level.SEVERE){
				if(!errorSent){
					//use a javascript function, don't use the worker,
					if(server != null){
						server.sendError("Nitoku - GWT: " + "[ " + level.getName() + " ] " + arg);	
					}
					errorSent = true;
				}
			}
		}

	}
	
	public static void setServer(IServer _server){
		server = _server; 
	}
	
	private static void initializeClient() {

		if (hasBeenInitialized) {
			return;
		}

		
		if (GWT.isClient()) {

			//logList = new LinkedList<String>();
					
			// Dev Mode and GWTTests
			Handler handler = new CSolverLogHandler();
			//Handler handler = new ConsoleLogHandler();
			logger.addHandler(handler);

			// Get configuration level from property file
			String name = System.getProperty("logLevel");
			if (name != null) {
				logger.setLevel(Level.parse(name));
			} else {
				logger.setLevel(Level.INFO);
			}


			String isJreDebugMode = System.getProperty("jre.debugMode");
			//if is not ENABLED only SEVERE messages will get logged
			if(isJreDebugMode == null){
				logger.setLevel(Level.INFO);
				hasBeenInitialized = true;
				return;
			}
			
			if (isJreDebugMode.equals("ENABLED")) {

				//logger.info(getMessage(Level.INFO, Logger.class, 
				//		"Logger has been initialized"));
			
			} else {
				
				logger.severe(getMessage(Level.SEVERE, Logger.class,
						"Logger has not been configured, " 
							+ "enable jre.debugMode in your gwt.xml file"));
			}

		} else {
			//server log level can not be set on files, need to be set here on code
			//TODO: Make this configurable
			logger.setLevel(Level.INFO);
			//logger.info(getMessage(Level.INFO, Logger.class, 
			//		"Logger has been initialized"));
		}
		
		hasBeenInitialized = true;

	}

	private static String getMessage(Level level, Class c, String message) {

		// if (c != null){
		return "[ " + level.getName() + " ] [ " + c.getSimpleName() + " ] " + message;
		// }

		// return "[ " + level.getName() + " ] " + arg;
	}

	public static Level getLevel() {
		initializeClient();
		return logger.getLevel();
	}

	public static void setLevel(Level level) {
		logger.setLevel(level);
	}

	/**
	 * @deprecated use finer(Class c, String message) instead
	 * @param string
	 */
	public static void finer(String... message) {
		finer(Logger.class, message);

	}

	public static void finer(Class c, String... message) {
		out(Level.FINER, c, message);
	}

	/**
	 * Deprecated use {@link nitoku.log.Logger#finest(Class, String...)}
	 * instead
	 * 
	 * @deprecated
	 * @param message
	 */
	public static void info(String... message) {
		info(Logger.class, message);
	}

	public static void info(Class c, String... message) {
		out(Level.INFO, c, message);
	}

	/**
	 * Deprecated use {@link nitoku.log.Logger#fine(Class, String...)} instead
	 * 
	 * @deprecated
	 * @param message
	 */
	public static void fine(String... message) {
		fine(Logger.class, message);

	}

	public static void fine(Class c, String... message) {
		out(Level.FINE, c, message);
	}

	/**
	 * Deprecated use {@link nitoku.log.Logger#finest(Class, String...)}
	 * instead
	 * 
	 * @deprecated
	 * @param message
	 */
	public static void finest(String... message) {
		finest(Logger.class, message);
	}

	public static void finest(Class c, String... message) {
		out(Level.FINEST, c, message);
	}

	public static boolean isLoggable(Level level) {
		return logger.isLoggable(level);
	}

	/**
	 * Deprecated use {@link nitoku.log.Logger#config(Class, String...)}
	 * instead
	 * 
	 * @deprecated
	 * @param message
	 */
	public static void config(String... message) {
		config(Logger.class, message);
	}

	public static void config(Class c, String... message) {
		out(Level.CONFIG, c, message);
	}

	public static void severe(Class c, String... message) {
		out(Level.SEVERE, c, message);
	}

	public static void error(Class c, Exception e) {
		if (GWT.isClient()) {
			// No point on trying to print the stacktrace on client side?
			clientOut(Level.SEVERE, c, e.getMessage());
		} else {
			e.printStackTrace();
		}
		// out(Level.SEVERE, c, e.getMessage());
	}

	public static void error(Class c, Exception e, String... message) {
		if (GWT.isClient()) {
			clientOut(Level.SEVERE, c, message);
		} else {
			logger.log(Level.SEVERE, message.toString(), e);
			//.out.println(message);
		}
		error(c, e);
	}

	public static void warning(Class c, String... message) {
		out(Level.WARNING, c, message);
	}
}
