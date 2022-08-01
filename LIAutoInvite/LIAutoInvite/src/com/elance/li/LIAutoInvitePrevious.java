package com.elance.li;

/*import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;*/
import com.google.common.base.Function;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.io.*;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class LIAutoInvitePrevious {



    /*public static void lininvrun(String emailLogin,
                                 String password,

                                 String location,
                                 Integer offsetInt,
                                 Integer countInt, Integer timeout) throws IOException {
        //input file
        String inputFile = location + "input.csv";

        //skip header of input file
        offsetInt = offsetInt+1;

        // Create a new instance of the Firefox driver

        WebDriver driver = new FirefoxDriver();

        // And now use this to visit linkedin
        driver.get("https://www.linkedin.com/uas/login?goback=.nmp_*1_*1_*1_*1_*1_*1_*1_*1_*1_*1&trk=hb_signin");


        // Find the text input element by its name  and password
        WebElement element = driver.findElement(By.name("session_key"));
        WebElement element2 = driver.findElement(By.name("session_password"));
        WebElement element3 = driver.findElement(By.name("signin"));

        // Enter the info of login and password
        element.sendKeys(emailLogin);
        element2.sendKeys(password);

        // Now submit the form. WebDriver will find the form for us from the element
        element3.submit();


        // linkedin is rendered dynamically with JavaScript.
        // Wait for the page to load, timeout after 10 seconds
        WebElement textbox = fluentWait(By.id("profile-sub-nav"), driver);


        //a counter to be used in the iteration
        Integer cnt = 0;

        //preparing the output files
        CSVWriter writerConnected = new CSVWriter(new FileWriter(location + "Profiles_Connected_.csv", true));
        CSVWriter writerBulk = new CSVWriter(new FileWriter(location + "Profiles_ToBulkConnect_.csv", true));

        //iterate through the input list of profiles
        CSVReader readerInit = new CSVReader(new FileReader(inputFile));
        List<String[]> myEntries = readerInit.readAll();
        for (String[] myentry : myEntries) {

            //only invite the specific range that has been provided in command line
            cnt++;
            if (cnt <= offsetInt) {
                continue;
            }
            if (cnt > countInt + offsetInt) {
                break;
            }

            //get all info from csv
            String id = myentry[0].trim();
            String fullname = myentry[1].trim();
            String firstname = myentry[2].trim();
            String lastname = myentry[3].trim();
            String link = myentry[4].trim();
            String email = myentry[5].trim();
            String message = myentry[6].trim();


            System.out.println("Loading site: " + link);
            //load site
            driver.get(link);
            //wait for site to load
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException ee) {
                ee.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            //initialize html parser
            String html_contentJobs = driver.getPageSource();
            Document docJob = Jsoup.parse(html_contentJobs);


            //Element connectButton = docJob.select("div.profile-actions a.button-primary").first();
            Element profileView = docJob.select("div.profile-overview").first();


            Boolean connectButtonAvailable = false;
            //find if in the main page of the profile there is an option to connect this profile
            connectButtonAvailable = profileView.html().contains("Connect");

            if (!connectButtonAvailable) {

                System.out.println("connect button not available");

                //write and flush to the file
                writerBulk.writeNext(myentry);

                writerBulk.flush();

            } else {
                //connectButtonAvailable = true;
                System.out.println("connect button IS available");


            }


            if (connectButtonAvailable) {

                WebElement elmnt = driver.findElement(By.linkText("Connect"));


                //custom javascript click... otherwise it fails...
                //click on connect
                JavascriptExecutor executor = (JavascriptExecutor) driver;
                executor.executeScript("arguments[0].click();", elmnt);


                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException ee) {
                    ee.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                WebElement elmnt2 = null;
                try {
                	elmnt2 = driver.findElement(By.id("selector-iweOther-reason-iweReconnect"));
                } catch (NoSuchElementException nsee) {
                	
                }
                
                // elmnt2.click();
                WebElement emailwb = null;
                WebElement elmnt3 = null;
                if (elmnt2 != null) {
                	executor.executeScript("arguments[0].click();", elmnt2);
                	emailwb = driver.findElement(By.id("other-iweOther-reason-iweReconnect"));                	
                	elmnt3 = driver.findElement(By.id("greeting-iweReconnect"));
                } else {
                	// use the email that we got from the csv
                	emailwb = driver.findElement(By.id("emailAddress-invitee-invitation"));
                	elmnt3 = driver.findElement(By.id("greeting-invitation"));
                }
                //other-iweOther-reason-iweReconnect
                emailwb.sendKeys(email);
                
                //send the message from the csv
                elmnt3.clear();
                elmnt3.sendKeys(message);

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ee) {
                    ee.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                
                //send invite
                WebElement elmnt4 = driver.findElement(By.className("btn-primary"));
                executor.executeScript("arguments[0].click();", elmnt4);

                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException ee) {
                    ee.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                Boolean error = false;


                //get page of output after the invite was sent
                String html_contentJobs2 = driver.getPageSource();
                Document docJob2 = Jsoup.parse(html_contentJobs2);


                Element errorElement = docJob2.select("div.error strong").first();

                if (errorElement != null) {
                    error = true;
                }

                String[] arr;
                arr = new String[8];
                arr[0] = id;
                arr[1] = fullname;
                arr[2] = firstname;
                arr[3] = lastname;
                arr[4] = link;
                arr[5] = email;
                arr[6] = message;

                //write all the info to the output file along with the error
                writerConnected.writeNext(arr);

                writerConnected.flush();

            }
        }
        writerBulk.close();
                writerConnected.close();

        System.out.println("exiting....");


        //Close the browser
        driver.quit();
        System.exit(0);


    }

    public static WebElement fluentWait(final By locator, WebDriver driver) {
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(10, TimeUnit.SECONDS)
                .pollingEvery(5, TimeUnit.SECONDS)
                .ignoring(NoSuchElementException.class);

        WebElement foo = wait.until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver driver) {
                return driver.findElement(locator);
            }
        });

        return foo;
    }

    *//**
     * Main function of application
     *
     * @param args
     *//*
    public static void main(String[] args) {

        //get all info that is required for the program to run
        System.out.println("Enter LinkedIn id: ");

        Scanner sc = new Scanner(System.in);

        String email = sc.nextLine();

        System.out.println("Enter LinkedIn password: ");
        String password = sc.nextLine();

        System.out.println("Enter input file folder location: ");
        String location = sc.nextLine();

        System.out.println("Enter offset (default is 0): ");
        String offset = sc.nextLine();

        System.out.println("Enter number of invites to be sent starting from offset: ");
        String count = sc.nextLine();

        System.out.println("Enter timeout (default is 5000): ");
        String timeout = sc.nextLine();

        Integer offsetInt = Integer.parseInt(offset);
        Integer countInt = Integer.parseInt(count);
        Integer timeoutInt = Integer.parseInt(timeout);

        System.out.println("Program executing. ");

        System.out.println(email);
        System.out.println(password);
        System.out.println(location);
        try {
            lininvrun(email, password, location, offsetInt, countInt, timeoutInt);
        } catch (IOException e) {
            System.out.println("Location should end in slash and should contain input.csv");
            System.exit(1);
        }
        System.exit(0);
    }*/
}
