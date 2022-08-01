/*
 * Copyright(c) 2014 TECHSERS(TM), Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of TECHSERS(TM).
 * Use is subject to license terms.
 */
package com.techsers.automation.linkedin.vo;

import com.techsers.automation.linkedin.config.ConfigReader;
import com.techsers.automation.linkedin.util.Logutil;

/**
 * VO classs used by {@link ConfigReader#readConfiguration(com.techsers.automation.linkedin.config.FileReader)}
 * to populate all required configuration parameters. Following are the list of configuration required,
 *  
 *  
 * @author mmuthukumaran
 *
 */
public class Configuration {

	private String FQCN = Configuration.class.getName();

	/* Few default constant */
	private static final int DEFAULT_OFFSET 				= 1;
	private static final int DEFAULT_COUNT 					= 1;
	private static final int DEFAULT_TIMEOUT				= 5000;
	private static final int DEFAULT_PROXY_MAX_THRESHOLD 	= -1;

	
	/* Mandatory input parameters */
	private String email = null;
	private String password = null;   	
	private String inputFileNameWithLocation = null;	

	
	/* Optional input parameters */
	private int offset = DEFAULT_OFFSET;
	private int count = DEFAULT_COUNT;
	private int timeout = DEFAULT_TIMEOUT;
	private int proxyMaxThreshold = DEFAULT_PROXY_MAX_THRESHOLD;

	enum Header {
		EMAIL("EMAIL"), PASSWORD("PASSWORD"), OFFSET("OFFSET"), COUNT("COUNT"), TIMEOUT("TIMEOUT"), 
		INPUT_FILE_NAME_WITH_LOCATION("INPUT_FILE_NAME_WITH_LOCATION"), PROXY_MAX_THRESHOLD("PROXY_MAX_THRESHOLD");

		String contant;
		int position;
		String value;
		Header(String contant) {
			this.contant = contant;
			this.position = -1;
		}

		public String getContant() {
			return contant;
		}

		public void setPosition(int position) {
			this.position = position;
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
		
		public void set(Header header, Configuration configuration) {
			switch(header) {
				case EMAIL:
						configuration.setEmail(header.value);
						break;
				case PASSWORD:
						configuration.setPassword(header.value);
						break;
				case OFFSET:
						try {
							configuration.setOffset((int)Float.parseFloat(header.value));
						} catch (Exception e) {
							configuration.setOffset(DEFAULT_OFFSET);
						}
						break;
				case COUNT:
						try {
							configuration.setCount((int)Float.parseFloat(header.value));
						} catch (Exception e) {
							configuration.setCount(DEFAULT_COUNT);
						}
						break;
				case TIMEOUT:
						try {
							configuration.setTimeout((int)Float.parseFloat(header.value));
						} catch (Exception e) {
							configuration.setTimeout(DEFAULT_TIMEOUT);
						}
						break;
				case INPUT_FILE_NAME_WITH_LOCATION:
						configuration.setInputFileNameWithLocation(header.value);
						break;
				case PROXY_MAX_THRESHOLD:
						try {
							configuration.setProxyMaxThreshold((int)Float.parseFloat(header.value));
						} catch (Exception e) {
							configuration.setProxyMaxThreshold(DEFAULT_PROXY_MAX_THRESHOLD);
						}
			}
		}
		
		public void set(Configuration configuration, String value, int position) {
			for (Header header : Header.values()) {
				if (position == header.position) {
					header.value = value;
					set(header, configuration);
					break;
				}
			}		
		}
	};

	public Configuration() {
		// TODO Auto-generated constructor stub
	}

	public void set(String value, int position) {
		Header header = Header.fromString(value);
		if (header != null) {
			header.setPosition(position);
		} else {
			header = Header.fromPosition(position);
			if (header != null) {
				header.set(this, value, position);
			} else {
				Logutil.getInstance().config(FQCN, value + " is an unknown header ");
			}
		}
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
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @param offset the offset to set
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return the timeout
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	
	/**
	 * @return the inputFileNameWithLocation
	 */
	public String getInputFileNameWithLocation() {
		return inputFileNameWithLocation;
	}

	/**
	 * @param inputFileNameWithLocation the inputFileNameWithLocation to set
	 */
	public void setInputFileNameWithLocation(String inputFileNameWithLocation) {
		this.inputFileNameWithLocation = inputFileNameWithLocation;
	}

	/**
	 * @return the proxyMaxThreshold
	 */
	public int getProxyMaxThreshold() {
		return proxyMaxThreshold;
	}

	/**
	 * @param proxyMaxThreshold the proxyMaxThreshold to set
	 */
	public void setProxyMaxThreshold(int proxyMaxThreshold) {
		this.proxyMaxThreshold = proxyMaxThreshold;
	}

	/**
	 * The validation function which checks for some mandatory input parameter 
	 */
	public void validate() {
		if (email == null) {
			throw new IllegalArgumentException(Header.EMAIL.contant + " is mandatory which is missing in config file"); 
		}
		if (password == null) {
			throw new IllegalArgumentException(Header.PASSWORD.contant + " is mandatory which is missing in config file"); 
		}
		if (inputFileNameWithLocation == null) {
			throw new IllegalArgumentException(Header.INPUT_FILE_NAME_WITH_LOCATION.contant + " is mandatory which is missing in config file");
		}
	}
}