/*
 * Copyright(c) 2014 TECHSERS(TM), Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of TECHSERS(TM).
 * Use is subject to license terms.
 */
package com.techsers.automation.linkedin.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;


/**
 * The functions to perform file operations are available in this class
 * 
 * @author mmuthukumaran
 *
 */
public class Fileutil {

	private static final String FQCN = Fileutil.class.getName();

	public Fileutil() {}


	/**
	 * Helper function to create temporary file with given <code>content</code>
	 * 
	 * @param content - The content to put inside the temporary file
	 */
	public static void writeIntoTempFile(String content) {
		String time = String.valueOf(System.currentTimeMillis());		
		File temp;
		try {
			temp = java.io.File.createTempFile(time, ".txt");
			BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
		    bw.write(content);
		    bw.close();
		    Logutil.getInstance().print("Created temporary file " + temp.getAbsolutePath());
		} catch (IOException e) {
			Logutil.getInstance().error(FQCN, e.getMessage());
		} 		
	}
	
	/**
	 * Helper function to read value of the given <code>key</code> from given
	 * <code>propertyFile</code>
	 * 
	 * @param propertyFile - The input property file
	 * @param key - The key to read
	 * @return - The value for the <code>key</code>
	 * 
	 * @throws IOException - Due to access issue in <code>propertyFile</code> and/or if there is no <code>key</code> 
	 */
	public static String getStringProperty(String propertyFile, String key) throws IOException {
		Properties properties = new Properties();
		FileInputStream fileInputStream = new FileInputStream(propertyFile); 
		properties.load(fileInputStream);
		return (String)properties.get(key);
	}
	
	/**
	 * Helper function to write <code>key</code> and <code>value</code> in the given <code>propertyFile</code>
	 *  
	 * @param propertyFile - The input property file
	 * @param key - The key to write
	 * @param value - The value of the <code>key</code>
	 * 
	 * @throws IOException - Due to access issue in <code>propertyFile</code> 
	 */
	public static void putStringProperty(String propertyFile, String key, String value) throws IOException {
		Properties properties = new Properties();
		
		// The key and value could be list of k,v later on
		properties.setProperty(key, value); 
		File file = new File(propertyFile);
		OutputStream outputStream = new FileOutputStream(file);
		properties.store(outputStream, "The default configuration file location used by LIAutoInvite program");		
	}
	
	/**
	 * Helper function to delete given file
	 *  
	 * @param filename - The name of the file to delete
	 * 
	 * @return - TRUE if success; FALSE otherwise
	 */
	public static boolean deleteFile(String filename) {
		File file = new File(filename);
		if (file.exists()) {
			file.delete();
			return true;
		}
		return false;
	}
}