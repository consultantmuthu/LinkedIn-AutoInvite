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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.techsers.automation.linkedin.util.Logutil;
import com.techsers.automation.linkedin.vo.Configuration;
import com.techsers.automation.linkedin.vo.Input;

/**
 * 
 * @author mmuthukumaran
 *
 */
public class InputReader {

	private String FQCN = InputReader.class.getName();

	private int MINIMUM_COLUMN_COUNT = 20;
	
	/**
	 * 
	 */
	public InputReader() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * This function is assuming that each input is present in individual row
	 * 
	 * @throws IOException 
	 * 
	 */
	public List<Input> readInput(String filepath) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(new File(filepath));
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fileInputStream); 
		XSSFSheet sheet = xssfWorkbook.getSheetAt(0);
		Iterator<Row> ite = sheet.rowIterator();
		List<Input> inputList = new LinkedList<Input>();
		while(ite.hasNext()){
			Row row = ite.next();
			Iterator<Cell> cite = row.cellIterator();
			Input input = new Input(filepath);
			int position = 0;
			boolean isEmpty = Boolean.TRUE;
			int lastColumn = Math.max(row.getLastCellNum(), MINIMUM_COLUMN_COUNT);
			// while(cite.hasNext()){
			for (int cn = 0; cn < lastColumn; cn++) {
				// Cell c = cite.next();
				Cell c = row.getCell(cn, Row.RETURN_BLANK_AS_NULL);
				// if (c == null || c.toString().equals("") && !cite.hasNext()) break;
				// if (c.getCellType() != Cell.CELL_TYPE_BLANK) isEmpty = Boolean.FALSE;
				if (c != null) {
					if (c.getCellType() != Cell.CELL_TYPE_BLANK) isEmpty = Boolean.FALSE;
					// System.out.println(c.getRowIndex() + " && " + c.getColumnIndex());
					Logutil.getInstance().log(FQCN, c.toString() + "\n");
					input.set(c.toString().trim(), position, c.getRowIndex(), c.getColumnIndex());
				} else {
					input.set(null, position, row.getRowNum(), cn);
				}
				position++;				
			}
			if (!isEmpty)
				inputList.add(input);
		}
		fileInputStream.close();
		return inputList;
	}

	public void validate(Configuration configuration, List<Input> inputList) {		
		// Input file should not be empty and it should be more than one row		
		if (inputList == null || inputList.size() <= 1) {
			throw new IllegalArgumentException("Input file has no data to process");
		}
		
		if (configuration.getCount() >= inputList.size()) {
			throw new IllegalArgumentException("Configuration COUNT is not matching with input size");
		}
			
		if (configuration.getOffset() >= inputList.size()) {
			throw new IllegalArgumentException("Configuration OFFSET is not matching with input size");
		}
		
		for (Input input : inputList) {
			input.validate();
		}
	}
}
