/*
 * Copyright(c) 2014 TECHSERS(TM), Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of TECHSERS(TM).
 * Use is subject to license terms.
 */
package com.techsers.automation.linkedin.vo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
public class Input {

	public static String SUCCESS = "SUCCESS";
	public static String FAILED = "FAILED";
	public static String UNKNOWN_REASON = "UNKNOWN";
	
	private String FQCN = Input.class.getName();
	private boolean isHeaderRecord = Boolean.FALSE;
	private String filepath = null;

	private int no 					= -1;
	private String fullName			= null;
	private String firstName		= null;
	private String lastName			= null;
	private String profileLink		= null;
	private String email			= null;
	private String message			= null;
	private String title			= null;
	private String company			= null;
	private String phone		 	= null;
	private String address			= null;
	private String city				= null;
	private String state			= null;
	private String zip				= null;
	private String country			= null;
	private String colleague		= null;
	private String classmate		= null;
	private String business			= null;

	private String status			= null;
	private String reason			= null;
	private int rowIndex			= -1;
	
	enum Header {
		NO("NO"), FULL_NAME("FULL_NAME"), FIRST_NAME("FIRST_NAME"), LAST_NAME("LAST_NAME"),
		PROFILE_LINK("PROFILE_LINK"), EMAIL("EMAIL"), MESSAGE("MESSAGE"), TITLE("TITLE"), COMPANY("COMPANY"),
		PHONE("PHONE"), ADDRESS("ADDRESS"), CITY("CITY"), STATE("STATE"), ZIP("ZIP"),COUNTRY("COUNTRY"),
		COLLEAGUE("COLLEAGUE"), CLASSMATE("CLASSMATE"), BUSINESS("BUSINESS"), STATUS("STATUS"), REASON("REASON"); 			

		String contant;
		// Position should be unnecessary after introducing row and column index
		// TODO remove it later
		int position, rowIndex, columnIndex;
		String value;
		Header(String contant) {
			this.contant = contant;
			this.position = -1;
			this.rowIndex = -1;
			this.columnIndex = -1;
		}

		public String getContant() {
			return contant;
		}

		public void setPosition(int position, int rowIndex, int columnIndex) {
			this.position = position;
			this.rowIndex = rowIndex;
			this.columnIndex = columnIndex;
		}

		public int getCoumnIndex() {
			return this.columnIndex;
		}
		
		public static Header fromString(String value) {
			if (value != null) {
				for (Header header : Header.values()) {
					if (value.equalsIgnoreCase(header.getContant())) {
						return header;
					}
				}
			}
			return null;
		}
		
		public static Header fromPosition(int position) {
			for (Header header : Header.values()) {
				if (position == header.position) {
					return header;
				}
			}			
			return null;
		}
		
		public void set(Header header, Input input) {
			switch(header) {
			case NO:
				if (header.value != null)
					input.setNo((int)Float.parseFloat(header.value));
				break;
			case FULL_NAME:
				input.setFullName(header.value);
				break;
			case FIRST_NAME:
				input.setFirstName(header.value);
				break;
			case LAST_NAME:
				input.setLastName(header.value);
				break;
			case PROFILE_LINK:
				input.setProfileLink(header.value);
				break;
			case EMAIL:
				input.setEmail(header.value);
				break;
			case MESSAGE:
				input.setMessage(header.value);
				break;
			case TITLE:
				input.setTitle(header.value);
				break;
			case COMPANY:
				input.setCompany(header.value);
				break;
			case PHONE:
				input.setPhone(header.value);
				break;
			case ADDRESS:
				input.setAddress(header.value);
				break;
			case CITY:
				input.setCity(header.value);
				break;
			case STATE:
				input.setState(header.value);
				break;
			case ZIP:
				input.setZip(header.value);
				break;
			case COUNTRY:
				input.setCountry(header.value);
				break;
			case COLLEAGUE:
				input.setColleague(header.value);
				break;
			case CLASSMATE:
				input.setClassmate(header.value);
				break;
			case BUSINESS:
				input.setBusiness(header.value);
				break;
			case STATUS:
				input.setStatus(header.value, 1);
				break;
			case REASON:
				input.setReason(header.value);
				break;
			}
		}
		
		public void set(Input input, String value, int position, int rowIndex, int columnIndex) {
			for (Header header : Header.values()) {
				if (position == header.position) {
					header.value = value;
					input.setRowIndex(rowIndex);
					set(header, input);
					break;
				}
			}		
		}
	};

	public Input(String filepath) {
		this.filepath = filepath;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;		
	}

	public int getRowIndex() {
		return this.rowIndex;
	}
	
	public void set(String value, int position, int rowIndex, int columnIndex) {
		Header header = Header.fromString(value);
		if (header != null) {
			header.setPosition(position, rowIndex, columnIndex);
			Logutil.getInstance().log(FQCN, value + " position is " + header.position);
			isHeaderRecord = Boolean.TRUE;
		} else {
			header = Header.fromPosition(position);
			header.set(this, value, position, rowIndex, columnIndex);
		}
	}

	/**
	 * @return the no
	 */
	public int getNo() {
		return no;
	}

	/**
	 * @param no the no to set
	 */
	public void setNo(int no) {
		this.no = no;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the profileLink
	 */
	public String getProfileLink() {
		return profileLink;
	}

	/**
	 * @param profileLink the profileLink to set
	 */
	public void setProfileLink(String profileLink) {
		this.profileLink = profileLink;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the company
	 */
	public String getCompany() {
		return company;
	}

	/**
	 * @param company the company to set
	 */
	public void setCompany(String company) {
		this.company = company;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the zip
	 */
	public String getZip() {
		return zip;
	}

	/**
	 * @param zip the zip to set
	 */
	public void setZip(String zip) {
		this.zip = zip;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the colleague
	 */
	public String getColleague() {
		return colleague;
	}

	/**
	 * @param colleague the colleague to set
	 */
	public void setColleague(String colleague) {
		this.colleague = colleague;
	}

	/**
	 * @return the classmate
	 */
	public String getClassmate() {
		return classmate;
	}

	/**
	 * @param classmate the classmate to set
	 */
	public void setClassmate(String classmate) {
		this.classmate = classmate;
	}

	/**
	 * @return the business
	 */
	public String getBusiness() {
		return business;
	}

	/**
	 * @param business the business to set
	 */
	public void setBusiness(String business) {
		this.business = business;
	}

	/**
	 * @return the errorReason
	 */
	public String getReason() {
		return reason == null ? FAILED : reason;
	}
	
	/**
	 * Set errorReason
	 * 
	 * @param errorReason
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Internal function 
	 * 
	 * @param status
	 * @param mode
	 */
	private void setStatus(String status, int mode) {
		this.status = status;
	}
	
	/**
	 * In addition to set the status in the object it also sets the status in the
	 * input file in the context
	 * 
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;	
		int columnIndex = Header.STATUS.getCoumnIndex();
		if (columnIndex >= 0) {
			XSSFWorkbook xssfWorkbook = null;
			FileInputStream fileInputStream = null;
			try {
				fileInputStream = new FileInputStream(new File(filepath));
				xssfWorkbook = new XSSFWorkbook(fileInputStream); 
				XSSFSheet worksheet = xssfWorkbook.getSheetAt(0);

				Cell cell = worksheet.getRow(rowIndex).getCell(columnIndex, Row.CREATE_NULL_AS_BLANK);
				columnIndex = Header.REASON.getCoumnIndex();
				if (SUCCESS.equalsIgnoreCase(status)) {
					cell.setCellValue(SUCCESS);					
					cell = worksheet.getRow(rowIndex).getCell(columnIndex, Row.CREATE_NULL_AS_BLANK);
					cell.setCellValue("");
				} else if (FAILED.equalsIgnoreCase(status)) {
					cell.setCellValue(FAILED);
					cell = worksheet.getRow(rowIndex).getCell(columnIndex, Row.CREATE_NULL_AS_BLANK);
					cell.setCellValue(getReason());
				} else {
					cell.setCellValue(status);
					cell = worksheet.getRow(rowIndex).getCell(columnIndex, Row.CREATE_NULL_AS_BLANK);
					cell.setCellValue(UNKNOWN_REASON);
				}											
			} catch (FileNotFoundException e) {
				Logutil.getInstance().exception(FQCN, e);
			}  catch (IOException e) {
				Logutil.getInstance().exception(FQCN, e);
			} finally {
				if (fileInputStream != null) {
					FileOutputStream  fileOutputStream = null;
					try {
						fileInputStream.close();
						fileOutputStream = new FileOutputStream(new File(filepath));
						xssfWorkbook.write(fileOutputStream);
					} catch (IOException e) {
						Logutil.getInstance().exception(FQCN, e);
					} finally {
						if (fileOutputStream != null)
							try {
								fileOutputStream.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					}
				}
			}
		}
	}

	/**
	 * General validation to check for mandatory fields
	 */
	public void validate() {
		if (!this.isHeaderRecord && (getProfileLink() == null || getEmail() == null || getMessage() == null)) {
			if (getEmail() == null) {
				// If email is null then we should have either one of the
				// following should be not null
				if ((getColleague() == null || getColleague().isEmpty()) && 
						(getClassmate() == null || getClassmate().isEmpty()) &&  
						(getBusiness() == null || getBusiness().isEmpty())) {
					throw new IllegalArgumentException("Profile Link | Email | Message is null in input file");
				} else {
					return;
				}
			} else {
				throw new IllegalArgumentException("Profile Link | Email | Message is null in input file");
			}
		}
		
	}
	
	/**
	 * The function will identify whether the object can be processed to send invite or not.
	 * At any point of time this function should be called before even attempting to navigate
	 * to profile link.
	 * 
	 * @return TRUE - It can be processed; FALSE - O/wise
	 */
	public boolean canProcess() {
		boolean canProcess = Boolean.FALSE;
		if (this.isHeaderRecord) {
			canProcess = Boolean.TRUE;
		} else {
			if ((colleague != null && !colleague.isEmpty()) && ((classmate == null || classmate.isEmpty()) && (business == null || business.isEmpty()))) {
				canProcess = Boolean.TRUE;
				return canProcess;
			} else if (classmate != null && !classmate.isEmpty() && (colleague == null || colleague.isEmpty()) && (business == null || business.isEmpty())) {
				canProcess = Boolean.TRUE;
				return canProcess;
			} if (business != null && !business.isEmpty() && (colleague == null || colleague.isEmpty()) && (classmate == null || classmate.isEmpty())) {
				canProcess = Boolean.TRUE;
				return canProcess;
			} else if (email != null && !email.isEmpty()) {
				canProcess = Boolean.TRUE;
				return canProcess;
			} else {
				setReason("Email and (Colleague or Classmate or Business) can not be null"); 
				canProcess = Boolean.TRUE;
			}
		} 			
		return canProcess;
	}
}