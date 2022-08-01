package com.techsers.automation.linkedin;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class TestInvite {

	private static String url = "https://www.linkedin.com/people/invite?from=profile&key=311674732&firstName=Anusha&lastName=Byreddy&authToken=8RzW&authType=name&connectionParam=member_desktop_profile_top-card-primary&csrfToken=ajax%3A6534592058400666101&goback=%2Enpv_311674732_*1_*1_name_8RzW_*1_*1_*1_*1_*1_*1_*1_*1_*1_*1_*1_*1_*1_*1_*1_*1_*1_*1_*1_*1_*1_*1_*1_*1_*1_*1_*1_*1_*1_*1_*1_nmp*4pymk*4name_*1";
	
	public TestInvite() {
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WebDriver driver = new FirefoxDriver();
		driver.get(url);		
	}

}
