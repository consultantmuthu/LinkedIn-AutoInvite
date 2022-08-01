/*
 * Copyright(c) 2014 TECHSERS(TM), Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of TECHSERS(TM).
 * Use is subject to license terms.
 */
package com.techsers.automation.linkedin.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.techsers.automation.linkedin.util.Logutil;

/**
 * 
 * @author mmuthukumaran
 *
 */
public class PatternReader {

	private String FQCN = PatternReader.class.getName();
	
	private static Map<String, String> patternCache = new HashMap<String, String>();
	
	/**
	 * Default constructor
	 */
	public PatternReader() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * This function will read Selenium pattern configured in the file for a given
	 * <code>key</code> and return the same. The values are also stored in the cache
	 * for faster retrieval during subsequent access
	 * 
	 * @throws IOException - Throws exception if there is a issue in file path, etc 
	 * 
	 */
	public String readPattern(String filepath, String key) throws IOException {
		String patternValue = patternCache.get(key);
		if (patternValue == null) {
			FileInputStream fileInputStream = new FileInputStream(new File(filepath));
			XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fileInputStream); 
			XSSFSheet sheet = xssfWorkbook.getSheetAt(1);
			Iterator<Row> ite = sheet.rowIterator();
			while(ite.hasNext()){
				Row row = ite.next();
				Iterator<Cell> cite = row.cellIterator();
				while(cite.hasNext()){
					Cell c = cite.next();
					Logutil.getInstance().log(FQCN, c.toString() + "\n");
					if (c.toString().equalsIgnoreCase(key)) {
						c = cite.next();
						patternValue = c.toString();
						patternCache.put(key, patternValue);
						break;
					} else {
						// Let us just add into cache if the ask is for different key
						if (cite.hasNext())							
							patternCache.put(c.toString(), cite.next().toString());
					}
				}			
			}
			fileInputStream.close();
		}
		return patternValue;
	}
	
	/**
	 * This function will return key for a given value from the hash map which stores
	 * all the pattern from the configuration file. Please note that the hashmap should
	 * be 1:1 map which means configuration should not have same value with two different key.
	 * Anyway we are not making sure 1:1 while adding value into map. Hence caller should
	 * make sure they check for null return
	 *  
	 * @param value -The value to look for 
	 * @return - The key for the value
	 * 
	 * Refer alternate {@link http://docs.guava-libraries.googlecode.com/git/javadoc/com/google/common/collect/BiMap.html}
	 * which does almost the same thing
	 */
	public String getKeyByValue(String value) {
	    for (Entry<String,String> entry : patternCache.entrySet()) {
	        if (value.equals(entry.getValue())) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}
}
