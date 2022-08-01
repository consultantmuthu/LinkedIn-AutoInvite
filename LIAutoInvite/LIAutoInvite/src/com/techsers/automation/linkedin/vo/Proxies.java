/*
 * Copyright(c) 2015 TECHSERS(TM), Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of TECHSERS(TM).
 * Use is subject to license terms.
 */
package com.techsers.automation.linkedin.vo;

import com.techsers.automation.linkedin.util.Logutil;
import com.techsers.automation.linkedin.vo.Input.Header;

public class Proxies {

	private String FQCN = Proxies.class.getName();

	private String proxyIp, proxyPort;
	private boolean isHeaderRecord = Boolean.FALSE;
	
	public Proxies() {
		// TODO Auto-generated constructor stub
	}

	enum Header {
		PROXY_IP("PROXY_IP"), PROXY_PORT("PROXY_PORT"); 			

		String value;
		int position;
		Header(String value) {
			this.value = value;
			this.position = -1;
		}

		public String getValue() {
			return this.value;
		}
		
		public void setPosition(int position) {
			this.position = position;
		}
		
		public static Header fromString(String value) {
			if (value != null) {
				for (Header header : Header.values()) {
					if (value.equalsIgnoreCase(header.toString())) {
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
		
		public void set(Header header, Proxies proxy) {
			switch(header) {
				case PROXY_IP:
					proxy.setProxyIp(header.value);
					break;
				case PROXY_PORT:
					proxy.setProxyPort(header.value);
					break;
			}
		}
		
		public void set(Proxies proxy, String value, int position) {
			for (Header header : Header.values()) {
				if (position == header.position) {
					header.value = value;
					set(header, proxy);
					break;
				}
			}		
		}

		public static void reset() {			
			for (Header header : Header.values()) {
				// System.out.println(header.toString());
			}
			
		}
	}

	/**
	 * @return the proxyIp
	 */
	public String getProxyIp() {
		return proxyIp;
	}

	/**
	 * @param proxyIp the proxyIp to set
	 */
	public void setProxyIp(String proxyIp) {
		this.proxyIp = proxyIp;
	}

	/**
	 * @return the proxyPort
	 */
	public String getProxyPort() {
		return proxyPort;
	}

	/**
	 * @param proxyPort the proxyPort to set
	 */
	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	};
	
	public void set(String value, int position) {
		Header header = Header.fromString(value);
		if (header != null) {
			header.setPosition(position);
			Logutil.getInstance().log(FQCN, value + " position is " + header.position);
			isHeaderRecord = Boolean.TRUE;
		} else {
			header = Header.fromPosition(position);
			header.set(this, value, position);
		}
	}
	
	public void reset() {
		Header.reset();
	}
}
