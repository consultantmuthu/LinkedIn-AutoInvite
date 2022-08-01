/*
 * Copyright(c) 2015 TECHSERS(TM), Inc. All Rights Reserved.
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
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.techsers.automation.linkedin.util.Logutil;
import com.techsers.automation.linkedin.vo.Proxies;

/**
 * 
 * @author mmuthukumaran
 *
 */
public class ProxyReader {

	private String FQCN = ProxyReader.class.getName();

	private int MINIMUM_COLUMN_COUNT = 2;
	
	/**
	 * Default constructor
	 */
	public ProxyReader() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * This function is assuming that each input is present in individual row
	 * 
	 * @throws IOException 
	 * 
	 */
	public List<Proxies> readInput(FileReader fileReader) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(new File(fileReader.getConfigFilePath()));
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fileInputStream); 
		XSSFSheet sheet = xssfWorkbook.getSheetAt(2);
		Iterator<Row> ite = sheet.rowIterator();
		List<Proxies> inputList = new ArrayList<Proxies>();
		while(ite.hasNext()){
			Row row = ite.next();
			Iterator<Cell> cite = row.cellIterator();
			Proxies input = new Proxies();
			int position = 0;
			boolean isEmpty = Boolean.TRUE;
			int lastColumn = Math.max(row.getLastCellNum(), MINIMUM_COLUMN_COUNT);
			/*while(cite.hasNext()){
				Cell c = cite.next();
				if (c == null || c.toString().equals("") && !cite.hasNext()) break;
				if (c.getCellType() != Cell.CELL_TYPE_BLANK) isEmpty = Boolean.FALSE;
				Logutil.getInstance().log(FQCN, c.toString() + "\n");
				input.set(c.toString().trim(), position++);
			}*/
			for (int cn = 0; cn < lastColumn; cn++) {
				// Cell c = cite.next();
				Cell c = row.getCell(cn, Row.RETURN_BLANK_AS_NULL);
				// if (c == null || c.toString().equals("") && !cite.hasNext()) break;
				// if (c.getCellType() != Cell.CELL_TYPE_BLANK) isEmpty = Boolean.FALSE;
				if (c != null) {
					if (c.getCellType() != Cell.CELL_TYPE_BLANK) isEmpty = Boolean.FALSE;
					// System.out.println(c.getRowIndex() + " && " + c.getColumnIndex());
					Logutil.getInstance().log(FQCN, c.toString() + "\n");
					input.set(c.toString().trim(), position);
				} else {
					input.set(null, position);
				}
				position++;				
			}
			if (!isEmpty) {
				inputList.add(input);
				input.reset();
			}
		}
		fileInputStream.close();
		return inputList;
	}
}
