/*
 * Copyright(c) 2014 TECHSERS(TM), Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of TECHSERS(TM).
 * Use is subject to license terms.
 */
package com.techsers.automation.linkedin;

import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;
import com.techsers.automation.linkedin.config.ConfigReader;
import com.techsers.automation.linkedin.config.FileReader;
import com.techsers.automation.linkedin.config.InputReader;
import com.techsers.automation.linkedin.config.PatternReader;
import com.techsers.automation.linkedin.config.ProxyReader;
import com.techsers.automation.linkedin.util.Fileutil;
import com.techsers.automation.linkedin.util.Logutil;
import com.techsers.automation.linkedin.vo.Configuration;
import com.techsers.automation.linkedin.vo.Input;
import com.techsers.automation.linkedin.vo.Proxies;

/**
 * This is the main class in the project and it operates based on three different input. They are,
 * 
 * <b>Configuration: The configuration parameter for successful bootstrapping. This should be in
 * XLSX format.
 * 
 * <b>Input: The input file which will have list of user specific information where the logged in
 * user to get connected via linked-in
 *  
 * <b>Pattern: The necessary operational parameter to ease the navigation (and change) in linked-in
 * at some extend 
 * 
 * <b>Proxy: The list of proxy which can be used by the program
 * 
 * @author mmuthukumaran
 *
 */
public class LIAutoInvite {

	/* FQCN */
	private static String FQCN = ConfigReader.class.getName();

	/* The WebDriver */ 
	private WebDriver driver = null;
	
	/* The user selected config file will be here*/
	private FileReader menuReader = null;
	
	/* Configuration parameter */
	private Configuration configuration = null;
	
	/* List of user details */
	private List<Input> inputList = null;	

	/* The object to hold list of patterns */	
	PatternReader patternReader = null;
	
	/* The singleton logger */
	private static Logutil logger = Logutil.getInstance();
	
	/* Useful constants */
	private static int DEFAULT_ELEMENT_WAIT_PERIOD 	= 40;
	private static int DEFAULT_STALE_ELEMENT_RETRY 	= 3;

	/** ERROR code **/
	private static final String RUNTIME_ERROR_ONE 		= "Option not available";
	private static final String RUNTIME_ERROR_TWO		= "Connect button not available or you might be already connected !";
	private static final String RUNTIME_ERROR_THREE		= "Unable to click hidden connect button";
	private static final String RUNTIME_ERROR_FOUR		= "Connect button could have been hidden";
	private static final String RUNTIME_ERROR_FIVE		= "Failed to send invite or you have captcha to answer";

	/** Report specific **/
	private static int totalSuccess 		= 0;
	private static int totalFailed 			= 0;
	private static int totalSkipped 		= 0;
	// START ---------- PRIVATE METHODS ---------- 

	/**
	 * Creates a driver for the caller. This should be the first method to call 
	 * for automation
	 */
	private void createDriver() {	
		// Create a new instance of the Firefox driver
		driver = new FirefoxDriver();
	}
	
	/**
	 * Creates driver for the caller and sets its capabilities in order to use the proxies. 
	 * This should be the first method to call for automation
	 */
	private void createDriver(Proxies proxies) {	
		// Create new instance of the Firefox driver
		DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
		org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
		proxy.setHttpProxy(proxies.getProxyIp() + ":" + proxies.getProxyPort());		
		desiredCapabilities.setCapability(CapabilityType.PROXY, proxy);
		driver = new FirefoxDriver(desiredCapabilities);
	}
	
	/**
	 * Call this method to stop the driver and hence closing browser, etc
	 */
	private void closeDriver() {
		if (driver != null) {
			try {
				driver.close();
				holdon(5000);
				driver.quit();
			} catch (Exception e) {
				// Sometime selenium bails out warning
				// eat that for better user experience
			}
			driver = null;
		}
	}
	
	/**
	 * Perform user login
	 * 
	 * @throws IOException
	 */
	private void login() throws IOException {
		String filepath = menuReader.getConfigFilePath();
		
		// And now use this to visit linkedin
		String url = patternReader.readPattern(filepath, "BASE_URL");
		String email = configuration.getEmail();
		logger.print("Going to login to linked-in using email " + email);
		
		driver.get(url);
		WebElement element = driver.findElement(By.name(patternReader.readPattern(filepath, "LOGIN_EMAIL_BOX")));
		WebElement element2 = driver.findElement(By.name(patternReader.readPattern(filepath, "PASSWORD_BOX")));
		WebElement element3 = driver.findElement(By.name(patternReader.readPattern(filepath, "SIGNIN_BTN")));

		// Enter the info of login and password
		element.sendKeys(email);
		element2.sendKeys(configuration.getPassword());

		// Now submit the form. WebDriver will find the form for us from the element
		element3.submit();

	}

	/**
	 * Perform user logout; We should be able to use mouseOverandClick(...) but it is not 
	 * working. I need to find out, why?  
	 * 
	 * @throws IOException
	 */
	private void logout() throws IOException {
		String filepath = menuReader.getConfigFilePath();
		
		WebElement hoverElement = driver.findElement(By.xpath(patternReader.readPattern(filepath, "OPEN_LOGOUT_DROPDOWN")));
		
		Actions builder = new Actions(driver);
		builder.moveToElement(hoverElement).build().perform();
				
		try {
			// WebElement clickElement = driver.findElement(By.linkText("Sign Out"));
			WebElement clickElement = driver.findElement(By.cssSelector(patternReader.readPattern(filepath, "LOGOUT")));
			// clickElement.click();
			builder.click(clickElement);
		} catch (Exception e) {
			logger.error(FQCN, e.getMessage());
		}
		
		logger.print("Logout successful");
	}
	
	/**
	 * It puts the function caller into wait state until it finds the element given 
	 * in the <code>locator</code> or timeout which ever occurs first
	 *  
	 * @param locator - The input selenium specific element locator
	 * @return - none
	 */
	private void wait(final By locator) {
		WebDriverWait wait = new WebDriverWait(driver, DEFAULT_ELEMENT_WAIT_PERIOD);
		wait.until(
				new Function<WebDriver, WebElement>() { 
					public WebElement apply(WebDriver driver) { 
						try {
							// Print progress so that user will know the program is 
							// not in hung state
							logger.printprogress();
							return driver.findElement(locator);
						} catch (NoSuchElementException notFound) {
							return null;
						}
					}
				});
	}

	/**
	 * This function will move mouse to an element <code>hoverElement</code> and calling click on 
	 * element <code>clickButton</code>. Pretty useful to enable hidden elements in many sites, etc
	 *  
	 * @param hoverElement - The element to move the mouse over
	 * @param connectButton - The elment to click after mouse over
	 */
	private void mouseOverandClick(WebElement hoverElement, WebElement clickButton) {
		Actions builder = new Actions(driver);
		builder.moveToElement(hoverElement).perform();
		builder.click(clickButton);
	}

	/**
	 * Helper function to write the page source into temporary file
	 */
	@SuppressWarnings("unused")
	private void writePageSource() {
		String htmlContent = driver.getPageSource();
		Fileutil.writeIntoTempFile(htmlContent);		
	}
	
	/**
	 * Blind wait function - Caller will have to wait for the configuration amount of
	 * seconds. 
	 */
	private void holdon() {
		if (configuration != null) {
			try {
				Thread.sleep(configuration.getTimeout());
			} catch (InterruptedException ee) {
				ee.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
		}
	}
	
	/**
	 * Wait for timeout to occur
	 */
	private void holdon(int timeout) {
		if (configuration != null) {
			try {
				Thread.sleep(timeout);
			} catch (InterruptedException ee) {
				ee.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
		}
	}

	/**
	 * Helper function to find out input in the list of Web Elements. It does the
	 * Comparison in the following order,
	 * <p>
	 * <li>Full string match<li>
	 * <li>Split input into words and match those words<li>
	 * 
	 * @param lists - List of elements
	 * @param input - Input to match with
	 * @return
	 */
	private String getMatchingOption(List<WebElement> lists, String input) {
		String matchWord = null;
		int maxWordCount = 0;
		
		for (WebElement webElement : lists) {
			if (webElement.getText().equalsIgnoreCase(input)) {
				return webElement.getText();
			} else {
				int wordCount = 0;
				String []inputs = input.split(" ");
				if (inputs != null) {
					int len = inputs.length;				
					for (int i = 0; i < len; i++) {
						String []options = webElement.getText().split(" ");
						if (options != null) {
							int olen = options.length;
							for (int j = 0; j < olen; j++) {
								if (inputs[i].equalsIgnoreCase(options[j])) {
									wordCount++;
								}
							}
						}
					}
				}
				if (wordCount > maxWordCount) {
					maxWordCount = wordCount;
					matchWord = webElement.getText();
				}
			}
		}
		return matchWord;
	}

	
	private String selectBCCOption(String input, String optionButton, String selectButton) {		
		WebElement elmnt2 = null;
		try {
			elmnt2 = driver.findElement(By.id(optionButton));				
		} catch (NoSuchElementException nsee) {
			return RUNTIME_ERROR_ONE;
		}

		if (elmnt2 != null) {
			elmnt2.click();
			wait(By.id(selectButton));
			WebElement selectElement = driver.findElement(By.id(selectButton));
			Select select = new Select(selectElement);
			List<WebElement> lists = select.getOptions();
			String option = getMatchingOption(lists, input);
			for (WebElement webElement : lists) {
				if (webElement.getText().equals(option)) {
					select.selectByVisibleText(option);
					break;
				}
			}
		}
		return null;
	}
	
	// END ---------- PRIVATE METHODS ----------
	
	
	// START ---------- PUBLIC METHODS ----------
	public boolean run(Input input) throws IOException {		

		boolean success = Boolean.FALSE;
		
		if (input.canProcess() ) {
			// Navigate to user home page
			driver.get(input.getProfileLink());

			//holdon();		
			wait(By.cssSelector(patternReader.readPattern(menuReader.getConfigFilePath(), "POST_PROFILE_LINK_CP")));

			@SuppressWarnings("unused")
			Boolean connectButtonAvailable = true;
			String configFile = menuReader.getConfigFilePath();

			// Direct connect button or hidden connect button 
			String connectXPath = patternReader.readPattern(configFile, "CONNECT_BTN");
			WebElement elmnt = null;
			try {
				elmnt = driver.findElement(By.xpath(connectXPath));
				elmnt.click();
			} catch (Exception enve) {
				connectButtonAvailable = false;
				if (elmnt != null) {
					logger.print(RUNTIME_ERROR_FOUR);
					WebElement hoverElement = driver.findElement(By.xpath(patternReader.readPattern(
							menuReader.getConfigFilePath(), "OPEN_SEND_IN_EMAIL_DROPDOWN")));
					try {
						mouseOverandClick(hoverElement,elmnt);
					} catch (Exception e) {
						input.setReason(RUNTIME_ERROR_THREE);
						return Boolean.FALSE;
					}
				} else {
					input.setReason(RUNTIME_ERROR_TWO);
					return Boolean.FALSE;
				}
			}

			// custom javascript click... otherwise it fails...
			// click on connect
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			int attempts = 0;
			do {
				try {
					executor.executeScript(patternReader.readPattern(configFile, "CUSTOM_JS_SCRIPT_1"), elmnt);
					break;
				} catch(StaleElementReferenceException e) {					
					if (attempts == 0) {
						logger.printDelay();
						holdon();
					}
				}
			} while(++attempts < DEFAULT_STALE_ELEMENT_RETRY);	

			WebElement emailwb = null;
			WebElement elmnt3 = null;

			String message = null;
			if (input.getColleague() != null && !input.getColleague().isEmpty()) {
				message = selectBCCOption(input.getColleague(), 
									patternReader.readPattern(configFile, "COLLEAGUE_BTN_BYID"),
									patternReader.readPattern(configFile, "COLLEAGUE_SELECT_BYID"));
				if (message != null)
					input.setReason(message);
			} else if (input.getClassmate() != null && !input.getClassmate().isEmpty()) {
				message = selectBCCOption(input.getClassmate(), 
									patternReader.readPattern(configFile, "CLASSMATE_BTN_BYID"),
									patternReader.readPattern(configFile, "CLASSMATE_SELECT_BYID"));
				if (message != null)
					input.setReason(message);
			} else if (input.getBusiness() != null && !input.getBusiness().isEmpty()) {
				message = selectBCCOption(input.getBusiness(), 
									patternReader.readPattern(configFile, "BUSINESS_BTN_BYID"),
									patternReader.readPattern(configFile, "BUSINESS_SELECT_BYID"));
				if (message != null)
					input.setReason(message);
			} else {
				WebElement elmnt2 = null;
				try {
					elmnt2 = driver.findElement(By.id(patternReader.readPattern(configFile, "OTHER_OPTION_BTN")));				
				} catch (NoSuchElementException nsee) {

				}

				if (elmnt2 != null) {
					elmnt2.click();
					executor.executeScript(patternReader.readPattern(configFile, "CUSTOM_JS_SCRIPT_1"), elmnt2);
					emailwb = driver.findElement(By.id(patternReader.readPattern(configFile, "OTHER_EMAIL_ADDRESS_BOX")));                	
					elmnt3 = driver.findElement(By.id(patternReader.readPattern(configFile, "OTHER_MESSAGE_BOX")));
				} else {
					emailwb = driver.findElement(By.id(patternReader.readPattern(configFile, "FRIENDS_EMAIL_BOX")));
				}
				emailwb.clear();
				emailwb.sendKeys(input.getEmail());
			}

			if (message != null && (input.getEmail() != null && !input.getEmail().isEmpty())) { // We may have got email id
				emailwb = driver.findElement(By.id(patternReader.readPattern(configFile, "FRIENDS_EMAIL_BOX")));
				emailwb.clear();
				emailwb.sendKeys(input.getEmail());
			}
			
			if (elmnt3 == null) {
				try {
					elmnt3 = driver.findElement(By.id(patternReader.readPattern(configFile, "OTHER_MESSAGE_BOX")));
				} catch (Exception e)  {
					elmnt3 = driver.findElement(By.id(patternReader.readPattern(configFile, "FRIENDS_GREETING")));
				}
			}			

			// send the message from the input
			elmnt3.clear();
			elmnt3.sendKeys(input.getMessage());				

			//send invite
			WebElement elmnt4 = driver.findElement(By.className(patternReader.readPattern(configFile, "INVITE_BTN")));
			elmnt4.click();
			
			// Take a breath
			holdon();

			//get page of output after the invite was sent
			String html_contentJobs2 = driver.getPageSource();
			Document docJob2 = Jsoup.parse(html_contentJobs2);

			Element errorElement = docJob2.select(patternReader.readPattern(
												configFile, "POST_INVITE_ERROR_CP")).first();
			if (errorElement != null && errorElement.toString().contains("sent.</strong>")) {
				success = Boolean.TRUE;
			} else {
				success = Boolean.FALSE;
				input.setReason(RUNTIME_ERROR_FIVE);
			}
		} 
		return success;
	}	

	public boolean start() {		

		// Invoke file open dialog for user to select a file from the system		
		menuReader = new FileReader();
		logger.print("Please select Linked-In Auto Invite config file by using dialog window");
		if (menuReader.selectFile() == false) {
			return false;
		}
		
		// Read the configuration
		// The <code>readConfiguration</code> does the validation automatically
		ConfigReader configReader = new ConfigReader();
		try {
			configuration = configReader.readConfiguration(menuReader);
		} catch (IllegalArgumentException e) {
			logger.error(FQCN, e.getMessage());
			return false; 
		} catch (Exception e) {
			Logutil.getInstance().exception(FQCN, e);
			return false;
		}

		logger.log(FQCN, "Configuration values are as follows: ");
		logger.log(FQCN, configuration.getEmail());
		logger.log(FQCN, configuration.getPassword());
		logger.log(FQCN, Integer.toString(configuration.getOffset()));
		logger.log(FQCN, Integer.toString(configuration.getCount()));
		logger.log(FQCN, Integer.toString(configuration.getTimeout()));
		logger.log(FQCN, configuration.getInputFileNameWithLocation());

		// Read the input file
		// Call the validate method to validate input
		logger.print("About to process " + configuration.getCount() + " records from input file at " + configuration.getInputFileNameWithLocation());

		InputReader inputReader = new InputReader();
		try {
			inputList = inputReader.readInput(configuration.getInputFileNameWithLocation());
			inputReader.validate(configuration, inputList);
		} catch (IOException e) {
			logger.error(FQCN, e.getMessage());
			return false;
		}

		// Read all pattern
		patternReader = new PatternReader();		
		
		int proxyMaxUsageCount = configuration.getProxyMaxThreshold(); 
		int proxyUsedCount = 0, length = -1;
		List<Proxies> proxyList = null;
		if (proxyMaxUsageCount > 0) {
			try {
				proxyList = new ProxyReader().readInput(menuReader);
			} catch (IOException e2) {
				logger.exception(FQCN, e2);
				return false;		
			} finally {
				if (proxyList == null || proxyList.size() < 2) {
					logger.print("Please configure proxies properly");
					return false;
				} else {
					length = proxyList.size();
				}
			}
		}
		Boolean loginOnce = Boolean.FALSE;
		int count = 1; 
		
		for (int i = configuration.getOffset(),j=0; j < configuration.getCount() && i < inputList.size(); i++, j++) {
			long startUserTimeInSeconds = (System.currentTimeMillis()/1000);
			Input input = inputList.get(i);
			String fullname = input.getFullName();
			String status = input.getStatus();
			// Fullname should be present and should not be an empty string
			// The existing record should not have success status in it
			if (fullname != null && !fullname.isEmpty() && 
					(status == null || status.isEmpty() || !Input.SUCCESS.equalsIgnoreCase(status))) {
				logger.print("Started processing to send invite for " + input.getFullName() + " (" + input.getNo() + ")");
				if (proxyMaxUsageCount > 0 && (proxyUsedCount == 0 || 
						proxyUsedCount >= proxyMaxUsageCount) && !loginOnce) {
					Proxies proxies = proxyList.get(count++);
					if (count >= length) count = 1;			

					// Create required webdriver
					createDriver(proxies);

					// Perform login and wait till login complete
					try {
						login();
						wait(By.id(patternReader.readPattern(menuReader.getConfigFilePath(), "POST_LOGIN_CP")));			
						logger.print("Login successful with Proxy IP " + proxies.getProxyIp() + " and port " + proxies.getProxyPort());
					} catch (IOException e1) {
						logger.exception(FQCN, e1);
						return false;
					}
					proxyUsedCount++;
				} else if (proxyMaxUsageCount < 0 && !loginOnce) {
					// Create required webdriver
					createDriver();

					// Perform login and wait till login complete
					try {
						login();
						wait(By.id(patternReader.readPattern(menuReader.getConfigFilePath(), "POST_LOGIN_CP")));			
						logger.print("Login successful");
					} catch (IOException e1) {
						logger.exception(FQCN, e1);
						return false;
					}
					loginOnce = Boolean.TRUE;
				} else if (proxyUsedCount > 0) proxyUsedCount++;


				logger.log(FQCN, "" + input.getNo());
				logger.log(FQCN, input.getFullName());
				logger.log(FQCN, input.getFirstName());
				logger.log(FQCN, input.getLastName());
				logger.log(FQCN, input.getProfileLink());
				logger.log(FQCN, input.getEmail());
				logger.log(FQCN, input.getMessage());
				logger.log(FQCN, input.getTitle());
				logger.log(FQCN, input.getCompany());
				logger.log(FQCN, input.getPhone());
				logger.log(FQCN, input.getAddress());
				logger.log(FQCN, input.getCity());
				logger.log(FQCN, input.getState());
				logger.log(FQCN, input.getZip());
				logger.log(FQCN, input.getCountry());

				boolean isSuccess = Boolean.FALSE;

				try {
					isSuccess = run(input);				
				} catch (IOException e) {
					Logutil.getInstance().exception(FQCN, e);
					input.setReason(e.getMessage());
					isSuccess = Boolean.FALSE;
				} catch (TimeoutException te) {
					Logutil.getInstance().exception(FQCN, te);
					input.setReason("Double Check profile link");
					isSuccess = Boolean.FALSE;
				} catch (NoSuchElementException nsee) {
					input.setStatus(Input.FAILED);
					input.setReason(nsee.getMessage());
					isSuccess = Boolean.FALSE;
				} finally {
					startUserTimeInSeconds = ((System.currentTimeMillis()/1000) - startUserTimeInSeconds);
					if (isSuccess) {
						input.setStatus(Input.SUCCESS);
						totalSuccess++;
						logger.print("Finished processing to send invite for " + input.getFullName() + " (" + input.getNo() + ") - took " + startUserTimeInSeconds + "s");
					} else {
						input.setStatus(Input.FAILED);						
						logger.print("Failed to send invite for " + input.getFullName() + " (" + input.getNo() + ") - took " + startUserTimeInSeconds + "s");
						totalFailed++;
						if (input.getReason().equalsIgnoreCase(RUNTIME_ERROR_FIVE)) {
							logger.print("Got stuck with send invite for " + input.getFullName() + " (" + input.getNo() + ") - hence skip rest and break");
							totalSkipped = totalSkipped + (configuration.getCount() - j - 1);
							break;
						}
					}
				}
			} else {
				totalSkipped++;
				if (fullname == null || fullname.isEmpty()) {
					logger.print("Missing fullname - Skipping row " + input.getRowIndex());					
				} else { 
					logger.print("Processed already - Skipping row " + input.getRowIndex());
				}
			}

			if (proxyMaxUsageCount > 0 && proxyUsedCount >= proxyMaxUsageCount && !loginOnce) {
				// Perform logout
				try {
					logout();
				} catch (IOException e) {
					Logutil.getInstance().exception(FQCN, e);
					// It should be safe to ignore this exception
					// There is no need to return false
				}
				closeDriver();
				proxyUsedCount = 0;
			}
			holdon();
		}		
		if (loginOnce || proxyUsedCount > 0) {
			// Perform logout
			try {
				logout();
				closeDriver();
			} catch (IOException e) {
				Logutil.getInstance().exception(FQCN, e);
				// It should be safe to ignore this exception
				// There is no need to return false
			}			
		}
		holdon();		
		return true;
	}
	
	public void end() {
		closeDriver();
	}

	// END ---------- PUBLIC METHODS ----------

	// ---------- MAIN METHODS ----------
	
    /**
     * The main function to kick start automation
     *
     * @param args - There is nothing useful coming in
     */
    public static void main(String[] args) {
    	LIAutoInvite liAutoInvite = new LIAutoInvite();
    	logger.print("Starting LIAutoInvite...");
    	long startUserTimeInSeconds = (System.currentTimeMillis()/1000);
    	try {
    		if (liAutoInvite.start()) {
    			liAutoInvite.end();
    			startUserTimeInSeconds = ((System.currentTimeMillis()/1000) - startUserTimeInSeconds);
    			logger.print("Finished LIAutoInvite with " + totalSuccess + " success, " + 
    							totalFailed + " failed, " + totalSkipped + " skipped - took " + startUserTimeInSeconds + "s");
    			System.exit(0);
    		} else {
    			System.exit(1); // Exit with error
    		}
    	} catch (Exception e) {
    		logger.exception(FQCN, e);
    		liAutoInvite.end();
    		System.exit(1);
    	}
    }
}