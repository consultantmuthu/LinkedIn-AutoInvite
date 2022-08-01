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
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.techsers.automation.linkedin.util.Logutil;
import com.techsers.automation.linkedin.vo.Configuration;
/**
 * @author mmuthukumaran
 *
 */
public class ConfigReader {

	private String FQCN = ConfigReader.class.getName();
	
	/**
	 * 
	 */
	public ConfigReader() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * This function is assuming that each input is present in individual row
	 * 
	 * @throws IOException 
	 * 
	 */
	public Configuration readConfiguration(FileReader fileReader) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(new File(fileReader.getConfigFilePath()));
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fileInputStream); 
		XSSFSheet sheet = xssfWorkbook.getSheetAt(0);
		Iterator<Row> ite = sheet.rowIterator();
		Configuration configuration = new Configuration();
		while(ite.hasNext()){
			Row row = ite.next();
			Iterator<Cell> cite = row.cellIterator();
			int position = 0;
			while(cite.hasNext()){
				Cell c = cite.next();
				Logutil.getInstance().log(FQCN, c.toString() + "\n");
				configuration.set(c.toString().trim(), position++);
			}			
		}
		fileInputStream.close();
		configuration.validate();
		return configuration;
	}
}
