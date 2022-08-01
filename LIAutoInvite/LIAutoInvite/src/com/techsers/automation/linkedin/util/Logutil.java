/*
 * Copyright(c) 2014 TECHSERS(TM), Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of TECHSERS(TM).
 * Use is subject to license terms.
 */
package com.techsers.automation.linkedin.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Use this class so that we can completely turn off/on logging in 
 * the application. 
 * 
 * @author mmuthukumaran
 *
 */
public class Logutil {

	private static boolean logEnabled = Boolean.FALSE;

	public static Logutil logutil = null;

	// The following are not thread safe and they are not 
	// required to be
	boolean isProgressPrintOn = Boolean.FALSE;
	String PORGRESS_MESSAGE = ".";
	String PORGRESS_DELAY = "*";
	
	/**
	 * Default constructor
	 */
	private Logutil() {}

	/**
	 * @param args - We are going to do nothing with it
	 */
	public static Logutil getInstance() {
		if (logutil == null) { 
			String setting = System.getProperty("verbose");
			if (setting != null && setting.trim().equalsIgnoreCase("1")) {
				logEnabled = Boolean.TRUE;
			}
			logutil = new Logutil();
		}
		return logutil;
	}

	/**
	 * Printing a message to let the user know about progress 
	 */
	public void printprogress() {
		isProgressPrintOn = Boolean.TRUE;
		System.out.print(PORGRESS_MESSAGE);
	}
	
	/**
	 * Use this function to let user know there is a force full delay 
	 */	
	public void printDelay() {
		isProgressPrintOn = Boolean.TRUE;
		System.out.print(PORGRESS_DELAY);
	}
	
	public void print(String message) {
		if (isProgressPrintOn) System.out.println("\n");
		isProgressPrintOn = Boolean.FALSE;
		System.out.println(message + "\n");		
	}
	
	public void log(String FQCN, String message) {		
		if (logEnabled) {
			if (isProgressPrintOn) System.out.println("\n");
			isProgressPrintOn = Boolean.FALSE;
			Logger logger = Logger.getLogger(FQCN);
			logger.setLevel(Level.INFO);
			logger.severe(message);
		}
	}
	
	public void error(String FQCN, String message) {
		if (isProgressPrintOn) System.out.println("\n");
		isProgressPrintOn = Boolean.FALSE;
		Logger logger = Logger.getLogger(FQCN);
		logger.setLevel(Level.SEVERE);
		logger.severe(message);
	}
	
	public void config(String FQCN, String message) {
		if (isProgressPrintOn) System.out.println("\n");
		isProgressPrintOn = Boolean.FALSE;
		Logger logger = Logger.getLogger(FQCN);
		logger.setLevel(Level.CONFIG);
		logger.severe(message);
	}
	
	public void exception(String FQCN, Throwable throwable) {
		if (isProgressPrintOn) System.out.println("\n");
		isProgressPrintOn = Boolean.FALSE;
		Writer result = new StringWriter();
		throwable.printStackTrace(new PrintWriter(result));

		Logger logger = Logger.getLogger(FQCN);
		logger.setLevel(Level.SEVERE);
		logger.severe(result.toString());
	}
}
