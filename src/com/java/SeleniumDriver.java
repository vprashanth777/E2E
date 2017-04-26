package com.java;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.TTCCLayout;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
//import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import com.java.importnexport.ExportDetailedLogs;
import com.java.importnexport.ExportTestResults;
import com.java.importnexport.ImportTestDataDetails;
import com.java.importnexport.*;
import com.java.objects.ConfigDetails;
import com.java.objects.ResultDetails;
import com.java.objects.TestDataDetails;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
//import com.opera.core.systems.OperaDriver;

/**
 * This class is used to run the Tests based on the configData provided.
 * Implements Runnable Interface for multi threading
 */

@SuppressWarnings("deprecation")
public class SeleniumDriver implements Runnable {


	public HSSFSheet sheet;
	public HSSFWorkbook hwb;
	public String appUrl = null;
	public String browserType = null;
	public Properties miscProps = new Properties();

	//Updated by Sreenivas HR
	public Properties androidProps= new Properties();

	private String platformErrors = " Encountered unknown Errors";
	private int current_execution;
	public String PNRFileName = "";

	public Logger log;

	public Document document=null;
	public OutputStream file=null;
	public long  sleepcounter;
	public boolean reportFailureAsBug = false;
	public boolean updateResultsDB = false;
	public boolean isGridEnabled = false;
	public boolean executeFailedCases = false;
	public Integer failedCaseExecutionLoopCount =0; 
	public boolean sendMailReport = true;
	public boolean clearCookies = true;
	public boolean closeBrowserSessions = true;
	public boolean steplevelscreenshot = false;
	public boolean useObjectRepo = true;
	public boolean mergeReports = false;
	public String screenShotdir = "..\\ScreenShots\\";

	public boolean isTestLinkRequired = false;
	public boolean createEvidence = false;
	public boolean detailedLog = false;
	public boolean isDBUsed = false;
	public boolean inLoop = false;
	public boolean inExecute = false;
	public boolean isBrowserCrashed=false;
	public boolean reportPriority = false;
	public boolean reportReq = false;
	public boolean createpdf = false;
	public boolean unableToTakeScreenshot=false;
	private SimpleDateFormat scrShot = new SimpleDateFormat("MMddyy_HHmmss");
	private String scrShotDir = new SimpleDateFormat("MMddyy").format(new Date());
	private String strScreenshotName = "";

	private ArrayList<Integer> failed = new ArrayList<Integer>();
	public ArrayList<Integer> testCases = new ArrayList<Integer>();
	public HashMap<Integer, String> TestCaseDetails = new HashMap<Integer, String>();
	public HashMap<Integer, String> TestCaseExecutionDetails = new HashMap<Integer, String>();
	public HashMap<String,String> TestCasePriority = new HashMap<String,String>();
	public HashMap<String,String> TestCaseReq = new HashMap<String,String>();
	public HashMap<String,Integer> testDataCounter = new HashMap<String,Integer>(); 
	public HashMap<String, String> FailedCaseScreenShot = new HashMap<String, String>();
	public HashMap<String,String> multireport = new HashMap<String,String>();
	public Properties objRepo = new Properties();

	private int[] ReportCounters = new int[3];
	public int roundOfExecution = 1;

	public HashMap<String, String> hMap = new HashMap<String, String>();
	public HashMap<String, String> parameterDetails = new HashMap<String, String>();


	private WebDriver webdriver;
	public AppiumDriver appiumDriver;

	public ConfigDetails confDtls;
	public ImportTestDataDetails impxl;
	private ReportResultToTestlink testLink = null;

	public HashMap<String, Properties> setup_TestData = new HashMap<String, Properties>();
	public HashMap<String, Integer> setup_TestDataCounter = new HashMap<String, Integer>();

	public Properties setup_data = new Properties();
	GetDetails details=new GetDetails(this);
	public  Utils utils=new Utils(this);

	public int testCase=0;
	public int currentTestCase=0;
	public String strErrorMsg = "";
	public String arrCon[] = new String[3];//condition
	public ExportTestResults exportResultsExcel = new ExportTestResults(this);
	public TemplateGenerator htmlTemplate = new TemplateGenerator(this);
	//public IntegrateReports integrateReports = new IntegrateReports(this);
	public ArrayList<String> resultList;
	public long startTime;
	public long endTime;

	/**
	 * Constructor of te Class which takes the ConfigDetails object as a Parameter
	 */

	public SeleniumDriver(ConfigDetails config) {
		confDtls = config;
	}


	/**
	 * Method used to invoke the corresponding browser based on Test Data
	 */

	private void invokeBrowser() {

		try{

			startTime = System.currentTimeMillis();

			if (isGridEnabled) {
				log.info(" Selenium Grid is used to invoke browser ");
				DriverObject driverObj = new DriverObject(browserType, miscProps,log);
				webdriver = driverObj.createWebDriver();
			} 
			else {


				if(closeBrowserSessions){
					log.info(" All the browser sessions are closed ");
					closeBrowser(browserType);
				}
				if(browserType.equalsIgnoreCase("ANDROIDNATIVEAPP")){// Block Updated by Sreenivas HR
					//System.out.println("Mobile App Package Name === "+androidProps.getProperty("MobileAppPackageName"));
					log.info("Mobile App Package Name === "+androidProps.getProperty("MobileAppPackageName"));
					//System.out.println("Mobile App Activity Name === "+androidProps.getProperty("MobileAppActivityName"));
					log.info("Mobile App Activity Name === "+androidProps.getProperty("MobileAppActivityName"));
					System.out.println("Platform:"+androidProps.getProperty("AndroidAutomationPlatform"));
					log.info("Platform === "+androidProps.getProperty("AndroidAutomationPlatform"));
					System.out.println("Android Version:"+androidProps.getProperty("AndroidVersion"));
					log.info("Android Version === "+androidProps.getProperty("AndroidVersion"));

					DesiredCapabilities capabilities = new DesiredCapabilities().android();
					capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "");
					capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "");
					capabilities.setCapability(MobileCapabilityType.APP,androidProps.getProperty("ApkPath"));
					capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 180);


					try {
						appiumDriver = new AndroidDriver(new URL(appUrl),capabilities);
						webdriver=appiumDriver;
					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					appiumDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

				}else if(browserType.equalsIgnoreCase("ANDROIDCHROME")){

					DesiredCapabilities capabilities = new DesiredCapabilities();


					capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME,"Android");
					capabilities.setCapability(MobileCapabilityType.BROWSER_NAME,"Chrome"); 
					capabilities.setCapability(MobileCapabilityType.DEVICE_NAME,"0123456789ABCDEF");
					capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION,"4.4");


					try{
						webdriver = new RemoteWebDriver(new URL(appUrl),capabilities);
					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}else if (browserType.equalsIgnoreCase("IE8")	|| browserType.equalsIgnoreCase("IE6")|| browserType.equalsIgnoreCase("IE")){
					log.info(" IE browser is invoked");

					System.setProperty("webdriver.ie.driver", System.getProperty("user.dir")+"\\IEDriverServer.exe");
					webdriver = new InternetExplorerDriver();
				} else if (browserType.equalsIgnoreCase("firefox") || browserType.equalsIgnoreCase("ff")) {
					log.info(" Firefox browser is invoked");
					
					System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir")+"\\geckodriver.exe");
//					FirefoxProfile firefoxProfile = new FirefoxProfile();
//					firefoxProfile.setEnableNativeEvents(true);
					webdriver = new FirefoxDriver();

				} else if (browserType.equalsIgnoreCase("chrome")) {
					log.info(" Chrome browser is invoked");
					System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"\\chromedriver.exe");

					webdriver = new ChromeDriver();
				} else if (browserType.equalsIgnoreCase("safari")) {
					log.info(" Safari browser is invoked");
					webdriver = new SafariDriver();
				} else if (browserType.equalsIgnoreCase("opera")) {
					log.info(" Opera browser is invoked");
					//webdriver = new OperaDriver();
				}/*else if (browserType.equalsIgnoreCase("html")) {
					log.info(" HTML Unit browser is invoked");
					webdriver = new HtmlUnitDriver(true);
				}*/else{
					System.out.println(" Can't find the desired browser \n Using firefox");
					log.warn(" Can't find the desired browser. Using firefox");
					FirefoxProfile firefoxProfile = new FirefoxProfile();
					firefoxProfile.setEnableNativeEvents(true);
					webdriver = new FirefoxDriver(firefoxProfile);
					browserType = "firefox";
					hMap.put("Browser", browserType);
					confDtls.setBrowser(browserType);
				}
			}
		}catch(Exception e){
			log.error(" Can't innvoke the browser ");
			log.error(e.getMessage());
			System.out.println("Can't Create Driver Object. Stopping the thread");
			System.out.println(e.getMessage());
			e.printStackTrace();
			RunTest.configrowCount++;
			Thread.currentThread().stop();
		}
	}

	/**
	 * Method to close the browser after completing execution of all the test
	 * cases
	 */

	private void tearDown() {
		if (webdriver != null) {
			endTime = System.currentTimeMillis();
			log.info(" Quitting the web browser ");
			webdriver.quit();
		}
	}

	/**
	 * Method implemented as part of thread to call the required actions in
	 * order - getProperties() - getConfigData() - invokeBrowser() -
	 * testInitiation() - tearDown()
	 */

	public void run() {
		try{


			hMap.put("TimeStamp", scrShot.format(new Date()));

			String name = "..//TestReports//"+ confDtls.getAppName() + "//Logs//Log - " + hMap.get("TimeStamp") + ".log";

			FileAppender fa = new FileAppender(new PatternLayout("[%-5p][%9d] - %m%n"),name,false);
			fa.activateOptions();
			log = Logger.getLogger(name);
			log.addAppender(fa);

			System.out.println(" Log file created with time stamp :: " + hMap.get("TimeStamp"));

			log.info(" Reading Properties from Property File ");
			details.getProperties();

			log.info(" Reading Config Data ");
			details.getConfigData();

			log.info(" Invoking Browser ");
			invokeBrowser(); 

			log.info(" Ready to execute the tests ");
			//method creation for report
			generateReports();

			testInitiation();
			while(executeFailedCases)
				runFailedCases();


/*
			if(!mergeReports){			
				// Export the Test summary report and Build HTML report
				ReportCounters[1] = ReportCounters[0] - ReportCounters[2];
				htmlTemplate.buildTemplate(ReportCounters[0], ReportCounters[1],ReportCounters[2]);
				// Send Email report
				if(sendMailReport){
					EmailTestReport etp = new EmailTestReport(this);
					etp.postMail(ReportCounters, browserType, appUrl);

				}
			}
*/
			tearDown();

			RunTest.configrowCount++;

		}
		catch(Exception e)
		{



			if(e.equals(null)&&e.getMessage().contains("Error communicating with the remote browser. It may have died"))
			{//up on browser crash 
				unableToTakeScreenshot=true;
				strScreenshotName=" Unable to capture the screen shot: Browser crashed";
				System.out.println(e.getMessage());
				System.out.println("-------------BROWSER CRASHED -------------");
				log.error("BROWSER CRASHED @ run ");
				log.error(e.getMessage());
				strErrorMsg="Browser Crashed";

				resultList.add(strErrorMsg );
				resultList.add((new java.util.Date()).toString());
				TestCaseExecutionDetails.put(current_execution,"FAIL" + strErrorMsg);
				hMap.put("time_End", new Date().getTime()+"");
				exportResult();

				if(browserType.equalsIgnoreCase("ANDROIDNATIVEAPP")){// Block Updated by Sreenivas HR
					System.out.println("Mobile App Package Name === "+androidProps.getProperty("MobileAppPackageName"));
					log.info("Mobile App Package Name === "+androidProps.getProperty("MobileAppPackageName"));
					System.out.println("Mobile App Activity Name === "+androidProps.getProperty("MobileAppActivityName"));
					log.info("Mobile App Activity Name === "+androidProps.getProperty("MobileAppActivityName"));
					System.out.println("Platform:"+androidProps.getProperty("AndroidAutomationPlatform"));
					log.info("Platform === "+androidProps.getProperty("AndroidAutomationPlatform"));
					System.out.println("Android Version:"+androidProps.getProperty("AndroidVersion"));
					log.info("Android Version === "+androidProps.getProperty("AndroidVersion"));

					DesiredCapabilities capabilities = new DesiredCapabilities().android();
					capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "");
					capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android Emulator");
					capabilities.setCapability(MobileCapabilityType.APP,androidProps.getProperty("ApkPath"));
					capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 180);


					try {
						appiumDriver = new AndroidDriver(new URL(appUrl),capabilities);
						webdriver=appiumDriver;
					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					appiumDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

				}else if(browserType.equalsIgnoreCase("ANDROIDCHROME")){

					DesiredCapabilities capabilities = new DesiredCapabilities();

					capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME,"Android");
					capabilities.setCapability(MobileCapabilityType.BROWSER_NAME,"Chrome"); 
					capabilities.setCapability(MobileCapabilityType.DEVICE_NAME,"0123456789ABCDEF");
					capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION,"4.4");


					try{
						webdriver = new RemoteWebDriver(new URL(appUrl),capabilities);
					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else if (browserType.equalsIgnoreCase("IE8")	|| browserType.equalsIgnoreCase("IE6")|| browserType.equalsIgnoreCase("IE")){
					log.info(" IE browser is invoked");

					System.setProperty("webdriver.ie.driver", System.getProperty("user.dir")+"\\IEDriverServer.exe");
					webdriver = new InternetExplorerDriver();
				} else if (browserType.equalsIgnoreCase("firefox") || browserType.equalsIgnoreCase("ff")) {
					log.info(" Firefox browser is invoked");
					
					System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir")+"\\geckodriver.exe");
//					FirefoxProfile firefoxProfile = new FirefoxProfile();
//					firefoxProfile.setEnableNativeEvents(true);
					webdriver = new FirefoxDriver();

				} else if (browserType.equalsIgnoreCase("chrome")) {
					log.info(" Chrome browser is invoked");
					System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"\\chromedriver.exe");

					webdriver = new ChromeDriver();
				} else if (browserType.equalsIgnoreCase("safari")) {
					log.info(" Safari browser is invoked");
					webdriver = new SafariDriver();
				} else if (browserType.equalsIgnoreCase("opera")) {
					log.info(" Opera browser is invoked");
					//webdriver = new OperaDriver();
				}/*else if (browserType.equalsIgnoreCase("html")) {
					log.info(" HTML Unit browser is invoked");
					webdriver = new HtmlUnitDriver(true);
				}*/else{
					System.out.println(" Can't find the desired browser \n Using firefox");
					log.warn(" Can't find the desired browser. Using firefox");
					FirefoxProfile firefoxProfile = new FirefoxProfile();
					firefoxProfile.setEnableNativeEvents(true);
					webdriver = new FirefoxDriver(firefoxProfile);
					browserType = "firefox";
					hMap.put("Browser", browserType);
					confDtls.setBrowser(browserType);
				}
				webdriver.manage().window().maximize();
				try {


					testCase=currentTestCase+1;
					//ReportCounters[0]=ReportCounters[0]+1;
					//ReportCounters[2]=ReportCounters[2]+1;
					testInitiation();
					//resultList=null;

					while(executeFailedCases)
					{
						resultList = new ArrayList<String>();
						runFailedCases();
					}
					/*if(!mergeReports){			
						// Export the Test summary report and Build HTML report
						ReportCounters[1] = ReportCounters[0] - ReportCounters[2];
						htmlTemplate.buildTemplate(ReportCounters[0], ReportCounters[1],ReportCounters[2]);
						// Send Email report
						if(sendMailReport){
							EmailTestReport etp = new EmailTestReport(this);
							etp.postMail(ReportCounters, browserType, appUrl);

						}
					}*/

					tearDown();

					RunTest.configrowCount++;
					Thread.currentThread().stop();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else{
				System.out.println("Error:"+e.getMessage());
				stop();
			}
		}
	}



	public void generateReports() {
		// TODO Auto-generated method stub
		log.info(" Getting the Log Execution Value ");
		new ExportDetailedLogs(this).getMaxLog();

		//public String strErrorMsg = "";

		//	String arrCon[] = new String[3];
		arrCon[0] = ""; arrCon[2] = ""; arrCon[1] = "";

		impxl = new ImportTestDataDetails(this);

		//ExportTestResults exportResultsExcel = new ExportTestResults(this);
		exportResultsExcel.headings=0;
		exportResultsExcel.exportExcelHeader();

		//TemplateGenerator htmlTemplate = new TemplateGenerator(this);

		if (isTestLinkRequired && roundOfExecution == 1) {
			testLink = new ReportResultToTestlink(this);
			testLink.createBuild();
		}

	}


	/**
	 * Method to re-execute the failed test cases once
	 */

	private void runFailedCases() throws Exception {

		testCases.clear();
		TestCaseDetails = new LinkedHashMap<Integer, String>();
		TestCasePriority = new HashMap<String, String>();
		TestCaseExecutionDetails = new HashMap<Integer, String>();
		hMap.put("TimeStamp", scrShot.format(new Date()));
		int count = failed.size();

		if (executeFailedCases && count >= 1  ) {
			RunTest.exit_status = 1;
			miscProps.setProperty("execlog","NOTSET");
			ReportCounters = new int[3];
			hMap.put("htmlFile",scrShot.format(new Date()));
			if(!confDtls.getBrowser().contains("NATIVEAPP"))
			{
				webdriver.manage().deleteAllCookies();

				webdriver.get(hMap.get("URL"));
			}
			System.out.println("Failed Cases :  " + confDtls.getPrefix()+failed);
			int i = 0;
			while (i < count) {
				testCases.add(failed.get(i));
				i++;
			}
			confDtls.setTestCasesToBeExecuted(testCases);
			failed.clear();
			failedCaseExecutionLoopCount--;
			if(failedCaseExecutionLoopCount==0)
				executeFailedCases = false;
			roundOfExecution = roundOfExecution +1;
			log.info(" Run Failed Cases");

			String name = "..//TestReports//"+ confDtls.getAppName() + "//Logs//Log - " + hMap.get("TimeStamp") + ".log";

			FileAppender fa = new FileAppender(new PatternLayout("[%-5p][%9d] - %m%n"),name,false);
			fa.activateOptions();
			log = Logger.getLogger(name);
			log.addAppender(fa);
			generateReports();
			testCase=0;

			testInitiation();

		}
		else
			executeFailedCases = false;
	}

	/**
	 * Function to close all the instances of browser selected
	 */

	private void closeBrowser(String imageName) {

		if (imageName.equalsIgnoreCase("IE8") || imageName.equalsIgnoreCase("IE6") || imageName.equalsIgnoreCase("IE"))
			imageName = "iexplore.exe";
		else if (imageName.equalsIgnoreCase("FF") || imageName.equalsIgnoreCase("Firefox"))
			imageName = "firefox.exe";
		else if (imageName.equalsIgnoreCase("CHROME"))
			imageName = "chrome.exe";
		else if (imageName.equalsIgnoreCase("SAFARI"))
			imageName = "Safari.exe";
		Runtime r = Runtime.getRuntime();
		Process p = null;
		String listCommand = "tasklist /FI \"IMAGENAME eq " + imageName+ "\" /NH /FO CSV";
		String killCommand = "taskkill /f /im " + imageName + " /t";
		try {
			p = r.exec(listCommand);
		} catch (Exception e) {}
		try {
			BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			if (err.readLine() == null) {
				r.exec(killCommand);
				Thread.sleep(5000);
			}
		} catch (Exception e) {}
	}

	/**
	 * Method to perform actions as part of Test Data Details
	 */

	public ResultDetails executeTestStep(TestDataDetails tdd){

		ResultDetails resultDetails = new ResultDetails();

		System.out.println("Test case being Executed: "	+ confDtls.getPrefix()+tdd.getTestCaseID() + " :: Step - " + tdd.getTestDataID());
		String testCaseTitle = tdd.getTestCaseTitle();
		String dataFields = tdd.getDataFields().trim();
		String dataValues = tdd.getDataValues().trim();
		String fieldName = tdd.getFieldName().trim();

		System.out.println("dataFields : " + dataFields);
		System.out.println("dataValues : " + dataValues);

		TestType testType = new TestType(this);

		System.out.println("<In Set values of the page>");

		System.out.println("{Data Fields & Data Values Exist}");
		System.out.println("--------------------------setting values-----------------------------");
		String actionType = tdd.getActionType();
		System.out.println("Action Type : " + actionType);

		if (actionType == null){
			log.warn(" Action field is empty ");
			resultDetails.setErrorMessage("Action Field is Empty");
		}
		else{

			log.info(" Action :: " + actionType);
			log.info(" DataFields :: "+ dataFields);
			log.info(" DataValues :: " + dataValues);
			log.info(" FieldName :: " + fieldName);

			resultDetails = testType.performAction(webdriver,dataFields, dataValues, actionType,fieldName,tdd);

			log.info(" RESULT " + resultDetails.getFlag());
			System.out.println(" RESULT " + resultDetails.getFlag());
		}
		return resultDetails;
	}




	/**
	 * This method is to read the testdata from excel sheet, submit them to the
	 * browser and writing the results to an excel sheet.
	 * @throws InterruptedException 
	 * @throws MalformedURLException 
	 */
	public String getTestCaseName(TestDataDetails tdd){
		//TestDataDetails tdd = new TestDataDetails();
		String testTitle = tdd.getTestCaseTitle();
		System.out.println(testTitle);
		return testTitle;
	}
	public String getTestCaseNumber(TestDataDetails tdd){
		//TestDataDetails tdd = new TestDataDetails();
		String testCaseID = tdd.getTestCaseID();
		System.out.println(testCaseID);
		return testCaseID;

	}


	private void testInitiation() throws InterruptedException, MalformedURLException {

		try{
			/*Condition Updated by Sreenivas HR*/
			if(!confDtls.getBrowser().equalsIgnoreCase("ANDROIDNATIVEAPP"))
				webdriver.get(appUrl);
		}catch(Exception e)
		{
			System.out.println("Error:"+e.getMessage());
			log.error("Error:"+e.getMessage());
		}
		/* Code Updated By Sreenivas HR*/
		if(!(confDtls.getBrowser().equalsIgnoreCase("ANDROID") ||confDtls.getBrowser().equalsIgnoreCase("ANDROIDNATIVEAPP")))
		{
			webdriver.manage().window().maximize();
			webdriver.manage().deleteAllCookies();
			webdriver.get(appUrl);
		}

		for (int i = testCase; i < testCases.size(); i++) {

			currentTestCase=i;
			hMap.remove("comment_excel_write");

			log.info(" Executing Test Case " + confDtls.getPrefix()+testCases.get(i));

			HashMap<Integer, TestDataDetails> testData = new HashMap<Integer, TestDataDetails>();
			testData = impxl.readTestData(testCases.get(i).toString());
			hMap.put("current_execution",testCases.get(i).toString());
			hMap.remove("pdf");

			current_execution = testCases.get(i);
			String strWarningMessage= "";
			boolean casefound = false;
			resultList = new ArrayList<String>();

			hMap.put("time_Start", new Date().getTime()+"");
			

			sleepcounter = 0;

			for (int k = 1; k <= testData.size(); k++) {

				log.info(" Executing Test Case " + confDtls.getPrefix()+testCases.get(i) + " :: Step - " + k);

				TestDataDetails tdd = new TestDataDetails();
				casefound = true;
				inLoop = false;
				inExecute = false;

				tdd = (TestDataDetails) testData.get(k);

				if(tdd == null){
					log.warn(" Step - "+ k + " is empty ");
					System.out.println(" Test Step :: " + k + " not found \n Skipping to next available step");
					continue;
				}


				if (tdd.getBrowserType().equalsIgnoreCase("COMMON") || tdd.getBrowserType().toLowerCase().indexOf(browserType.toLowerCase()) != -1) {

					resultList = new ArrayList<String>();	

					ResultDetails resultDetails = new ResultDetails();
					arrCon[0] = ""; arrCon[2] = ""; arrCon[1] = "";
					if(tdd.getActionType().equalsIgnoreCase("EXECUTETESTCASE")){
						hMap.put("executeTestCase_startTime" +
								"", new Date().getTime()+"");
					}
					hMap.put("testStep_startTime" +
							"", new Date().getTime()+"");
					resultDetails = executeTestStep(tdd);
					hMap.put("testStep_endTime" +
							"", new Date().getTime()+"");

					inExecute = false;
					inLoop = false;

					String condition = tdd.getCondition();

					if(condition == null){
						condition = "";
					}

					condition = condition.toLowerCase();

					if(detailedLog){
						ExportDetailedLogs exportLogs = new ExportDetailedLogs(this);
						exportLogs.exportResultsToSQL(tdd,resultDetails);
					}

					if(tdd.getActionType() != null ){
						if(steplevelscreenshot || (condition.contains("scrshot")))
							takeScreenShot_Detail(tdd,resultDetails);
					}

					resultList.add(confDtls.getPrefix().toString()+tdd.getTestCaseID());//@
					resultList.add(tdd.getTestDataID()+"");
					resultList.add(tdd.getTestCaseTitle());

					condition = condition.replace("scrshot","");

					if (!condition.equalsIgnoreCase("")) {
						condition =  condition.toUpperCase(); 
						System.out.println(" Condition :: " + condition);
						arrCon = condition.split(":");
					}

					if(arrCon[0].indexOf("IF") != -1 && tdd.getActionType().equalsIgnoreCase("VERIFY")&& arrCon.length == 3){

						log.info(" IF condition is used " + condition);

						System.out.println(" ? P A S S ? ");

						int from = k;
						int pos = 2;
						if(resultDetails.getFlag())
							pos = 1;

						if(arrCon.length == 3){
							try{
								if(!arrCon[pos].equalsIgnoreCase("NEXT"))
									from = Integer.parseInt(arrCon[pos]) - 1;
							} catch (Exception ex) {
								System.out.println("Invalid step number." + arrCon[pos]);
							}
						}

						resultList.add("PASS");
						resultList.add("");
						resultList.add((new java.util.Date()).toString());
						k = from;
						log.info(" Skipping execution to step " + k);
						continue;
					}

					if(resultDetails.getFlag() && arrCon[0].indexOf("LOOP") != -1 && arrCon.length == 3){

						log.info(" LOOP condition is used " + condition);
						int from = k + 1;
						int to = k + 2;
						String warnMsg = resultDetails.getWarningMessage() + " \n Executing Loop at " + k + " :: "+ tdd.getTestCaseID();
						try{
							resultDetails = new ResultDetails();

							String steps[] = arrCon[1].toUpperCase().split("-");

							if (!steps[0].equalsIgnoreCase("NEXT"))
								from = Integer.parseInt(arrCon[1].split("-")[0]);

							to = Integer.parseInt(arrCon[1].split("-")[1]);

							int lcnt = Integer.parseInt(arrCon[2]);
							inLoop = true;
							resultDetails.setWarningMessage(warnMsg);
							log.info(" Executing Loop from " + from + " to " + to + " :: " + lcnt + " times ");
							resultDetails = executeSteps(from,to,lcnt,testData,resultDetails);
							inLoop = false;
						} catch (NumberFormatException e) {
							log.error(" Invalid step number in LOOP :: " + condition);
							log.error(e.getMessage());
							System.out.println("Invalid step number in LOOP :: " + condition);
							resultDetails.setWarningMessage(warnMsg + "\n Invalid step number in LOOP :: " + condition);
						}catch(NullPointerException e){
							log.error(" Null pointer Exception :: " + e.getMessage());
							log.error(e.getMessage());
							resultDetails.setWarningMessage("Null pointer Exception :: " + e.getMessage());
							System.out.println(warnMsg + "\n Null pointer Exception :: " + e.getMessage());
						}catch(Exception e){
							log.error(" Encountered Unknown Exception :: " + e.getMessage());
							log.error(e.getMessage());
							resultDetails.setWarningMessage("Unknown Exception :: " + e.getMessage());
							System.out.println(warnMsg + "\n Unknown Exception :: " + e.getMessage());
						}
					}

					if (resultDetails.getFlag()) {

						log.info(" P A S S ");
						System.out.println("?   P A S S   ?");
						resultList.add("Pass");
						TestCaseExecutionDetails.put(current_execution,"PASS");
						String temp = resultDetails.getWarningMessage().trim();
						if (temp!=null && !temp.equalsIgnoreCase("")) {
							strWarningMessage = strWarningMessage + "\n" + "!! Warning !! Step No. :: " + tdd.getTestDataID()
									+ " Message ::  " + resultDetails.getWarningMessage();
						}
						resultList.add(strWarningMessage);
						resultList.add((new java.util.Date()).toString());
					}else{	

						RunTest.exit_status = 1;
						log.error(" F A I L ");
						System.out.println("?   F A I L   ?");
						resultList.add("Fail");
						failed.add(testCases.get(i));
						takeScreenShot(tdd);

						log.info(" Executing onFail Procedure ");
						try {
							AppTestType apt = new AppTestType(new TestType(this));
							Method mobj = Class.forName("com.java.AppTestType").getMethod(confDtls.getAppName(),WebDriver.class);
							mobj.invoke(apt,webdriver);
						} catch (Exception e) {
							System.out.println(" Can't Execute on fail command");
							log.error( " Can't Execute on fail command ");
							log.error(e.getMessage());
						}

						System.out.println("failedCases = " + failed );

						strErrorMsg = "Test case failed at Step No. :: " + tdd.getTestDataID();
						if (!resultDetails.getErrorMessage().equalsIgnoreCase(""))
							strErrorMsg = strErrorMsg + " Error Message ::  " + resultDetails.getErrorMessage();
						else
							strErrorMsg = strErrorMsg + platformErrors;
						if(!strScreenshotName.contains("Unable to capture the screen")&&strScreenshotName.contains(" "))
						{
							String scnptath=strScreenshotName.substring(strScreenshotName.indexOf("\\")-2);
							scnptath=scnptath.replace(" ", "%20");

							FailedCaseScreenShot.put(tdd.getTestCaseID(), scnptath);
						}
						else if(strScreenshotName.contains("Unable to capture the screen"))
						{
							FailedCaseScreenShot.put(tdd.getTestCaseID(), strScreenshotName);
						}
						else
							FailedCaseScreenShot.put(tdd.getTestCaseID(), strScreenshotName.substring(strScreenshotName.indexOf("\\")-2));

						/*if(resultList.size()<6)
						{*/
						resultList.add(strErrorMsg + strScreenshotName);
						resultList.add((new java.util.Date()).toString());
						TestCaseExecutionDetails.put(current_execution,"FAIL" + strErrorMsg);
						/*TestType testType = new TestType(this);
						testType.exportToExcel(webdriver,resultList);*/
						//}
						break;
					}
				}
			}

			hMap.put("time_End", new Date().getTime()+"");
			if(createEvidence && casefound){
				try{

					log.info(" Adding Status Table at the end of PDF Evidence ");
					Font head  = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
					Font subHead = new Font(Font.TIMES_ROMAN, 12, Font.BOLDITALIC);
					Font subCon = new Font(Font.TIMES_ROMAN, 12, Font.NORMAL);

					document.add(new Paragraph("\n"));
					document.add(new Paragraph("\n"));

					PdfPTable table = new PdfPTable(5);
					table.setWidthPercentage(100);

					PdfPCell c1 = new PdfPCell(new Phrase("Test Case Details",head));
					c1.setColspan(5);
					c1.setBackgroundColor(Color.GRAY);
					c1.setPadding(10);
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(c1);

					c1 = new PdfPCell(new Phrase("Test Case ID",subHead));
					c1.setPadding(8);c1.setColspan(1);
					c1.setBackgroundColor(Color.LIGHT_GRAY);
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(c1);

					c1 = new PdfPCell(new Phrase(current_execution+"",subCon));
					c1.setPadding(8);c1.setColspan(4);
					c1.setHorizontalAlignment(Element.ALIGN_LEFT);
					table.addCell(c1);

					c1 = new PdfPCell(new Phrase("Test Case Title",subHead));
					c1.setPadding(8);c1.setColspan(1);
					c1.setBackgroundColor(Color.LIGHT_GRAY);
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(c1);

					c1 = new PdfPCell(new Phrase(TestCaseDetails.get(current_execution),subCon));
					c1.setPadding(8);c1.setColspan(4);
					c1.setHorizontalAlignment(Element.ALIGN_LEFT);
					table.addCell(c1);

					c1 = new PdfPCell(new Phrase("Status",subHead));
					c1.setPadding(8);c1.setColspan(1);
					c1.setBackgroundColor(Color.LIGHT_GRAY);
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(c1);

					boolean res = resultList.get(3).equalsIgnoreCase("Pass");
					String statmsg = resultList.get(4);
					unableToTakeScreenshot=statmsg.contains("Unable to capture the screen shot");
					//int tempindex = statmsg.indexOf(". Screen Shot : ");

					Font subHead1 = new Font(subHead);
					subHead1.setColor(res ? Color.GREEN : Color.RED);
					c1 = new PdfPCell(new Phrase(res ? "Pass" : unableToTakeScreenshot ?statmsg: statmsg.substring(0, statmsg.indexOf(". Screen Shot : ")),subHead1));
					c1.setPadding(8);c1.setColspan(4);
					c1.setHorizontalAlignment(Element.ALIGN_LEFT);
					table.addCell(c1);

					document.add(table);

					document.close();
					file.close();
				}catch(Exception e){
					log.error(" Unable to add Status table at the end of PDF Evidence ");
				}
				document  = null;
				file = null;
			}


			if (!casefound){
				TestCaseExecutionDetails.put(testCases.get(i),"SKIPPED");
				java.util.List<String> exportResult = new ArrayList<String>();
				exportResult.add(confDtls.getPrefix()+current_execution+"");
				exportResult.add("");
				exportResult.add(TestCaseDetails.get(current_execution)+"");
				exportResult.add("Skipped");
				exportResult.add(" No Test Steps Available");
				exportResult.add((new java.util.Date()).toString());
				exportResultsExcel.exportExcelRows(exportResult);
				continue;
			}

			if (casefound) {
				exportResult();

				if(!confDtls.getBrowser().contains("NATIVEAPP")){
					try {
						if(clearCookies){
							webdriver.manage().deleteAllCookies();
							webdriver.get(appUrl);
							webdriver.manage().deleteAllCookies();
						}

						webdriver.get(appUrl);
					} catch (Throwable e) {

						//System.out.println("exception occured : " + e.getMessage());

						System.out.println("Unable perform any operations on the browser, So quitting and reopening the browser instance");
						log.error("Unable perform any operations on the browser, So quitting and reopening the browser instance @ clear cookies");
						log.error(e.getMessage());

						try{
							webdriver.quit();
						}catch(Exception ee){}
						if(browserType.equalsIgnoreCase("ANDROIDNATIVEAPP")){// Block Updated by Sreenivas HR
							System.out.println("Mobile App Package Name === "+androidProps.getProperty("MobileAppPackageName"));
							log.info("Mobile App Package Name === "+androidProps.getProperty("MobileAppPackageName"));
							System.out.println("Mobile App Activity Name === "+androidProps.getProperty("MobileAppActivityName"));
							log.info("Mobile App Activity Name === "+androidProps.getProperty("MobileAppActivityName"));
							System.out.println("Platform:"+androidProps.getProperty("AndroidAutomationPlatform"));
							log.info("Platform === "+androidProps.getProperty("AndroidAutomationPlatform"));
							System.out.println("Android Version:"+androidProps.getProperty("AndroidVersion"));
							log.info("Android Version === "+androidProps.getProperty("AndroidVersion"));
							//System.out.println(app_pac);
							//System.out.println("adb shell pm clear"+" "+ app_pac);
							//Runtime rt = Runtime.getRuntime();

							//	Process proc = rt.exec("adb shell pm clear"+" "+ app_pac+"");		

							DesiredCapabilities capabilities = new DesiredCapabilities().android();
							/*	capabilities.setCapability("device", "Android");
						capabilities.setCapability(CapabilityType.BROWSER_NAME, "Android");
						capabilities.setCapability(CapabilityType.VERSION, "\""+ androidProps.getProperty("AndroidVersion")+"\"");
						capabilities.setCapability(CapabilityType.PLATFORM, "\""+androidProps.getProperty("AndroidAutomationPlatform")+"\"");
						capabilities.setCapability("app",androidProps.getProperty("ApkPath") );
						capabilities.setCapability("app-package",androidProps.getProperty("MobileAppPackageName"));
						capabilities.setCapability("app-activity",androidProps.getProperty("MobileAppActivityName"));
						capabilities.setCapability("newCommandTimeout", 180);
							 */
							capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "");
							capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android Emulator");
							capabilities.setCapability(MobileCapabilityType.APP,androidProps.getProperty("ApkPath"));
							capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 180);


							try {
								appiumDriver = new AndroidDriver(new URL(appUrl),capabilities);
								webdriver=appiumDriver;
							} catch (MalformedURLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							appiumDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

						}else if(browserType.equalsIgnoreCase("ANDROIDCHROME")){

							DesiredCapabilities capabilities = new DesiredCapabilities();

							/*	capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "chrome");
							capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
							capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android Emulator");
							capabilities.setCapability(MobileCapabilityType.APP,"chrome");
							capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 180);*/

							capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME,"Android");
							capabilities.setCapability(MobileCapabilityType.BROWSER_NAME,"Chrome"); 
							capabilities.setCapability(MobileCapabilityType.DEVICE_NAME,"0123456789ABCDEF");
							capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION,"4.4");


							try{
								webdriver = new RemoteWebDriver(new URL(appUrl),capabilities);
							} catch (MalformedURLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						} else if (browserType.equalsIgnoreCase("IE8") || browserType.equalsIgnoreCase("IE6") || browserType.equalsIgnoreCase("IE")){
							log.info(" IE browser is invoked");

							System.setProperty("webdriver.ie.driver", System.getProperty("user.dir")+"\\IEDriverServer.exe");
							webdriver = new InternetExplorerDriver();
						} else if (browserType.equalsIgnoreCase("firefox") || browserType.equalsIgnoreCase("ff")) {
							log.info(" Firefox browser is invoked");
							FirefoxProfile firefoxProfile = new FirefoxProfile();
							firefoxProfile.setEnableNativeEvents(true);
							webdriver = new FirefoxDriver(firefoxProfile);
						} else if (browserType.equalsIgnoreCase("chrome")) {
							log.info(" Chrome browser is invoked");
							System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"\\chromedriver.exe");

							webdriver = new ChromeDriver();
						} else if (browserType.equalsIgnoreCase("safari")) {
							log.info(" Safari browser is invoked");
							webdriver = new SafariDriver();
						} else if (browserType.equalsIgnoreCase("opera")) {
							log.info(" Opera browser is invoked");
							//webdriver = new OperaDriver();
						}/*else if (browserType.equalsIgnoreCase("html")) {
							log.info(" HTML Unit browser is invoked");
							webdriver = new HtmlUnitDriver(true);
						}*/else{
							System.out.println(" Can't find the desired browser \n Using firefox");
							log.warn(" Can't find the desired browser. Using firefox");
							FirefoxProfile firefoxProfile = new FirefoxProfile();
							firefoxProfile.setEnableNativeEvents(true);
							webdriver = new FirefoxDriver(firefoxProfile);
							browserType = "firefox";
							hMap.put("Browser", browserType);
							confDtls.setBrowser(browserType);
						}
						webdriver.manage().window().maximize();
						webdriver.get(hMap.get("URL"));
						//break;
					}
				}
			}
		}

		/*	if(roundOfExecution>0){
		// Export the Test summary report and Build HTML report
		ReportCounters[1] = ReportCounters[0] - ReportCounters[2];
		htmlTemplate.buildTemplate(ReportCounters[0], ReportCounters[1],ReportCounters[2],startTime,endTime);

		// Send Email report
		if(sendMailReport){
			EmailTestReport etp = new EmailTestReport(this);
			etp.postMail(ReportCounters, browserType, appUrl);
		}
		}*/

		java.util.List<String> resultSummary = new ArrayList<String>();
		resultSummary.add(browserType);
		resultSummary.add(Integer.toString(testCases.size()));
		resultSummary.add(Integer.toString(ReportCounters[1]));
		resultSummary.add(Integer.toString(ReportCounters[2]));
		exportResultsExcel.exportTestSummary(resultSummary);
		//----------
	}


	/**
	 * Method to take screen shot when ever a test case is failed
	 * 
	 * @param tdd
	 */

	public void takeScreenShot(TestDataDetails tdd) {

		log.info(" Taking Screen Shot for failed Case ");

		String name = screenShotdir + confDtls.getAppName();

		createDir(name,"Application Name");

		name = name + "\\" + scrShotDir;

		createDir(name,"TimeStamp");

		strScreenshotName = name + "\\" + confDtls.getPrefix()+tdd.getTestCaseID() + "_" + tdd.getTestDataID()+ "_" + scrShot.format(new Date()) + ".png";

		try{//Code Updated by Sreenivas HR
			if(browserType.equalsIgnoreCase("ANDROIDNATIVEAPP")){
				WebDriver augmentedDriver = new Augmenter().augment(webdriver);
				File f = ((TakesScreenshot) augmentedDriver).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(f, new File(strScreenshotName));
				f.delete();}
			else{
				File f = ((TakesScreenshot) webdriver).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(f, new File(strScreenshotName));
				f.delete();}

			log.info(" Screenshot name :: " + strScreenshotName);
			strScreenshotName = ". Screen Shot : " + strScreenshotName;
		}catch(Exception e){
			if(e.getMessage().contains("not reachable")||e.getMessage().contains("Error communicating with the remote browser. It may have died"))
			{//up on browser crash 
				unableToTakeScreenshot=true;
				strScreenshotName=" Unable to capture the screen shot: Browser crashed";
				System.out.println(e.getMessage());
				System.out.println("-------------BROWSER CRASHED -------------");
				log.error("BROWSER CRASHED @ takeScreenShot");
				log.error(e.getMessage());

				if(resultList.size()==0)
				{
					resultList.add(confDtls.getPrefix().toString()+tdd.getTestCaseID());//@
					resultList.add(tdd.getTestDataID()+"");
					resultList.add(tdd.getTestCaseTitle());
					resultList.add("Fail");

				}
				strErrorMsg = "Test case failed at Step No. :: " + tdd.getTestDataID()+" Error Message ::Browser Crashed";
				resultList.add(strErrorMsg );
				resultList.add((new java.util.Date()).toString());
				TestCaseExecutionDetails.put(current_execution,"FAIL" + strErrorMsg);
				hMap.put("time_End", new Date().getTime()+"");
				if(createEvidence ){
					try{

						log.info(" Adding Status Table at the end of PDF Evidence ");
						Font head  = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
						Font subHead = new Font(Font.TIMES_ROMAN, 12, Font.BOLDITALIC);
						Font subCon = new Font(Font.TIMES_ROMAN, 12, Font.NORMAL);

						document.add(new Paragraph("\n"));
						document.add(new Paragraph("\n"));

						PdfPTable table = new PdfPTable(5);
						table.setWidthPercentage(100);

						PdfPCell c1 = new PdfPCell(new Phrase("Test Case Details",head));
						c1.setColspan(5);
						c1.setBackgroundColor(Color.GRAY);
						c1.setPadding(10);
						c1.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(c1);

						c1 = new PdfPCell(new Phrase("Test Case ID",subHead));
						c1.setPadding(8);c1.setColspan(1);
						c1.setBackgroundColor(Color.LIGHT_GRAY);
						c1.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(c1);

						c1 = new PdfPCell(new Phrase(current_execution+"",subCon));
						c1.setPadding(8);c1.setColspan(4);
						c1.setHorizontalAlignment(Element.ALIGN_LEFT);
						table.addCell(c1);

						c1 = new PdfPCell(new Phrase("Test Case Title",subHead));
						c1.setPadding(8);c1.setColspan(1);
						c1.setBackgroundColor(Color.LIGHT_GRAY);
						c1.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(c1);

						c1 = new PdfPCell(new Phrase(TestCaseDetails.get(current_execution),subCon));
						c1.setPadding(8);c1.setColspan(4);
						c1.setHorizontalAlignment(Element.ALIGN_LEFT);
						table.addCell(c1);

						c1 = new PdfPCell(new Phrase("Status",subHead));
						c1.setPadding(8);c1.setColspan(1);
						c1.setBackgroundColor(Color.LIGHT_GRAY);
						c1.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(c1);

						boolean res = resultList.get(3).equalsIgnoreCase("Pass");
						String statmsg = resultList.get(4);
						boolean unableToTakeScreenshot=statmsg.contains("Unable to capture the screen shot");
						//int tempindex = statmsg.indexOf(". Screen Shot : ");

						Font subHead1 = new Font(subHead);
						subHead1.setColor(res ? Color.GREEN : Color.RED);
						c1 = new PdfPCell(new Phrase(res ? "Pass" : unableToTakeScreenshot ?statmsg.substring(0, statmsg.indexOf("Unable to capture the screen shot:")): statmsg.substring(0, statmsg.indexOf(". Screen Shot : ")),subHead1));
						c1.setPadding(8);c1.setColspan(4);
						c1.setHorizontalAlignment(Element.ALIGN_LEFT);
						table.addCell(c1);

						document.add(table);

						document.close();
						file.close();
					}catch(Exception ee){
						log.error(" Unable to add Status table at the end of PDF Evidence ");
					}
					document  = null;
					file = null;
				}
				exportResult();

				if (browserType.equalsIgnoreCase("IE8")	|| browserType.equalsIgnoreCase("IE6")|| browserType.equalsIgnoreCase("IE")){
					log.info(" IE browser is invoked");

					System.setProperty("webdriver.ie.driver", System.getProperty("user.dir")+"\\IEDriverServer.exe");
					webdriver = new InternetExplorerDriver();
				}else if (browserType.equalsIgnoreCase("firefox") || browserType.equalsIgnoreCase("ff")) {
					log.info(" Firefox browser is invoked");
					
					System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir")+"\\geckodriver.exe");
//					FirefoxProfile firefoxProfile = new FirefoxProfile();
//					firefoxProfile.setEnableNativeEvents(true);
					webdriver = new FirefoxDriver();

				} else if (browserType.equalsIgnoreCase("chrome")) {
					log.info(" Chrome browser is invoked");
					System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"\\chromedriver.exe");
					webdriver = new ChromeDriver();
				} else if (browserType.equalsIgnoreCase("safari")) {
					log.info(" Safari browser is invoked");
					webdriver = new SafariDriver();
				} else if (browserType.equalsIgnoreCase("opera")) {
					log.info(" Opera browser is invoked");
					//webdriver = new OperaDriver();
				}/*else if (browserType.equalsIgnoreCase("html")) {
					log.info(" HTML Unit browser is invoked");
					webdriver = new HtmlUnitDriver(true);
				}*/else{
					System.out.println(" Can't find the desired browser \n Using firefox");
					log.warn(" Can't find the desired browser. Using firefox");
					FirefoxProfile firefoxProfile = new FirefoxProfile();
					firefoxProfile.setEnableNativeEvents(true);
					webdriver = new FirefoxDriver(firefoxProfile);
					browserType = "firefox";
					hMap.put("Browser", browserType);
					confDtls.setBrowser(browserType);
				}
				webdriver.manage().window().maximize();
				try {
					/*	java.util.List<String> exportResult = new ArrayList<String>();
				exportResult = resultList;
				ReportCounters[0] = ReportCounters[0] + 1;
				for (int ii = 0; ii < resultList.size(); ii = ii + 6) {
					if (resultList.get(ii + 3).equals("Fail")) {
						exportResult = resultList.subList(ii, ii + 6);
						ReportCounters[2] = ReportCounters[2] + 1;
						break;
					}
				}
				exportResultsExcel.exportExcelRows(exportResult);

				if(updateResultsDB){
					exportResultsExcel.exportResultsToSQL(exportResult);
				}*/

					testCase=currentTestCase+1;
					//ReportCounters[0]=ReportCounters[0]+1;
					//ReportCounters[2]=ReportCounters[2]+1;
					testInitiation();
					//resultList=null;

					while(executeFailedCases)
					{
						resultList = new ArrayList<String>();
						runFailedCases();
					}

					tearDown();

					RunTest.configrowCount++;
					Thread.currentThread().stop();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}


			else if(e.getMessage().contains("unexpected alert open"))
			{
				strScreenshotName=" Unable to capture the screen shot: Unexpected alert open";
				System.out.println("Unable to take the screen shot: "+e.getMessage());
				log.error("Unable to take the screen shot: "+e.getMessage());
			}
		}

	}

	public void takeScreenShot_Detail(TestDataDetails tdd,ResultDetails rd) {

		String action = tdd.getActionType();

		boolean validateAction = action.equalsIgnoreCase("waittime") || action.equalsIgnoreCase("EXECUTETESTCASE");

		if(!validateAction){

			String comment = tdd.getComments();

			if(comment == null)
				comment = "";

			String name = screenShotdir + confDtls.getAppName();

			createDir(name,"Application Name");

			name = name + "\\" + scrShotDir;
			createDir(name,"TimeStamp");

			name = name + "\\" +"Log - " +  miscProps.getProperty("execlog");
			createDir(name,"Log Name");

			name = name + "\\" +confDtls.getPrefix()+hMap.get("current_execution");
			createDir(name,"Test Case ID");

			try{	
				if(createEvidence && file == null){
					log.info(" Creating Evidence PDF File ");
					String pdfFile = ".pdf";
					document = new Document();
					file = new FileOutputStream(new File(name+pdfFile));
					hMap.put("pdf",name+pdfFile);
					PdfWriter.getInstance(document, file);

					Font head  = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
					Font subHead = new Font(Font.TIMES_ROMAN, 12, Font.BOLDITALIC);
					Font subCon = new Font(Font.TIMES_ROMAN, 12, Font.NORMAL);

					String defaultLogo = "http://www.valuelabs.com/wp-content/themes/valuelabs/images/valuelabs-logo.gif"; 
					defaultLogo = miscProps.getProperty("logo", defaultLogo);

					Image logo = Image.getInstance(defaultLogo);
					logo.setAlignment(Image.MIDDLE);
					logo.scaleToFit(120,50);
					Chunk chunk = new Chunk(logo,0,0);

					HeaderFooter footer, header;

					Phrase hphrase = new Phrase("\n");
					hphrase.add(chunk);
					hphrase.add(new Paragraph("\n"));
					header = new HeaderFooter(hphrase, false);
					header.setAlignment(Element.ALIGN_RIGHT);
					header.setBorder(Rectangle.BOTTOM);

					footer = new HeaderFooter(new Phrase(" Page - "),true);
					footer.setBorder(Rectangle.TOP);
					footer.setAlignment(Element.ALIGN_CENTER);

					document.setFooter(footer);
					document.setHeader(header);
					document.open();
					document.newPage();
					document.add(new Paragraph("\n"));

					PdfPTable table = new PdfPTable(5);
					table.setWidthPercentage(100);

					PdfPCell c1 = new PdfPCell(new Phrase("Test Case Details",head));
					c1.setPadding(10);c1.setColspan(5);
					c1.setBackgroundColor(Color.GRAY);
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(c1);

					c1 = new PdfPCell(new Phrase("Application",subHead));
					c1.setPadding(8);c1.setColspan(1);
					c1.setBackgroundColor(Color.LIGHT_GRAY);
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(c1);

					c1 = new PdfPCell(new Phrase(confDtls.getAppName(),subCon));
					c1.setPadding(8);c1.setColspan(4);
					c1.setHorizontalAlignment(Element.ALIGN_LEFT);
					table.addCell(c1);

					c1 = new PdfPCell(new Phrase("Browser",subHead));
					c1.setPadding(8);c1.setColspan(1);
					c1.setBackgroundColor(Color.LIGHT_GRAY);
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(c1);

					c1 = new PdfPCell(new Phrase(confDtls.getBrowser(),subCon));
					c1.setPadding(8);c1.setColspan(4);
					c1.setHorizontalAlignment(Element.ALIGN_LEFT);
					table.addCell(c1);

					c1 = new PdfPCell(new Phrase("Test Case ID",subHead));
					c1.setPadding(8);c1.setColspan(1);
					c1.setBackgroundColor(Color.LIGHT_GRAY);
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(c1);

					c1 = new PdfPCell(new Phrase(current_execution+"",subCon));
					c1.setPadding(8);c1.setColspan(4);
					c1.setHorizontalAlignment(Element.ALIGN_LEFT);
					table.addCell(c1);

					c1 = new PdfPCell(new Phrase("Test Case Title",subHead));
					c1.setPadding(8);c1.setColspan(1);
					c1.setBackgroundColor(Color.LIGHT_GRAY);
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(c1);

					c1 = new PdfPCell(new Phrase(TestCaseDetails.get(current_execution),subCon));
					c1.setPadding(8);c1.setColspan(4);
					c1.setHorizontalAlignment(Element.ALIGN_MIDDLE);
					table.addCell(c1);

				}
			}catch(Exception e){
				log.error(" Unable to create Evidence PDF File ");
				log.error(e.getMessage());
				System.out.println(e.getMessage());
				//----------------
			}

			String step =  rd.getComment();

			if(inLoop){
				name = name + "\\" +"Loop - " + hMap.get("LOOPCOUNTER");
				step = "Loop - " + hMap.get("LOOPCOUNTER") + " " + step;
			}

			if(inExecute){
				name = name +"\\" + "Execute - " + tdd.getTestCaseID();
				step = "Execute - " + tdd.getTestCaseID() + "  " + step;
			}

			step = "Step :: " + step + " \n Time Stamp :: " +( new Date().toString());
			String fileName = name +  "\\Step_" + tdd.getTestDataID()+ ".png";


			String pdfImgName = fileName;//temp.substring(0,temp.lastIndexOf("\\")) + fileName.substring(2);

			try{ 
				//System.out.println("Entering into Screens");
				if(browserType.equalsIgnoreCase("ANDROIDNATIVEAPP")){//Code Updated By Sreenivas HR
					WebDriver augmentedDriver = new Augmenter().augment(webdriver);
					File f = ((TakesScreenshot) augmentedDriver).getScreenshotAs(OutputType.FILE);
					FileUtils.copyFile(f, new File(fileName));
					f.delete();
				}
				else{File f = ((TakesScreenshot) webdriver).getScreenshotAs(OutputType.FILE);
				f = ((TakesScreenshot) webdriver).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(f, new File(fileName));
				f.delete();
				//System.out.println("Closing Screens");
				}
			}catch(Exception e)
			{
				if(e.getMessage().contains("not reachable")||e.getMessage().contains("Error communicating with the remote browser. It may have died"))
				{//up on browser crash 
					unableToTakeScreenshot=true;
					strScreenshotName=" Unable to capture the screen shot: Browser crashed";
					System.out.println(e.getMessage());
					System.out.println("-------------BROWSER CRASHED -------------");
					log.error("BROWSER CRASHED @ takeScreenShot_Detail");
					log.error(e.getMessage());
					failed.add(current_execution);
					if(resultList.size()==0)
					{
						resultList.add(confDtls.getPrefix().toString()+current_execution);//@
						resultList.add(tdd.getTestDataID()+"");
						resultList.add(tdd.getTestCaseTitle());
						resultList.add("Fail");

					}
					strErrorMsg = "Test case failed at Step No. :: " + tdd.getTestDataID()+" Error Message ::Browser Crashed";
					resultList.add(strErrorMsg );
					resultList.add((new java.util.Date()).toString());
					TestCaseExecutionDetails.put(current_execution,"FAIL" + strErrorMsg);
					hMap.put("time_End", new Date().getTime()+"");
					if(createEvidence ){
						try{

							log.info(" Adding Status Table at the end of PDF Evidence ");
							Font head  = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
							Font subHead = new Font(Font.TIMES_ROMAN, 12, Font.BOLDITALIC);
							Font subCon = new Font(Font.TIMES_ROMAN, 12, Font.NORMAL);

							document.add(new Paragraph("\n"));
							document.add(new Paragraph("\n"));

							PdfPTable table = new PdfPTable(5);
							table.setWidthPercentage(100);

							PdfPCell c1 = new PdfPCell(new Phrase("Test Case Details",head));
							c1.setColspan(5);
							c1.setBackgroundColor(Color.GRAY);
							c1.setPadding(10);
							c1.setHorizontalAlignment(Element.ALIGN_CENTER);
							table.addCell(c1);

							c1 = new PdfPCell(new Phrase("Test Case ID",subHead));
							c1.setPadding(8);c1.setColspan(1);
							c1.setBackgroundColor(Color.LIGHT_GRAY);
							c1.setHorizontalAlignment(Element.ALIGN_CENTER);
							table.addCell(c1);

							c1 = new PdfPCell(new Phrase(current_execution+"",subCon));
							c1.setPadding(8);c1.setColspan(4);
							c1.setHorizontalAlignment(Element.ALIGN_LEFT);
							table.addCell(c1);

							c1 = new PdfPCell(new Phrase("Test Case Title",subHead));
							c1.setPadding(8);c1.setColspan(1);
							c1.setBackgroundColor(Color.LIGHT_GRAY);
							c1.setHorizontalAlignment(Element.ALIGN_CENTER);
							table.addCell(c1);

							c1 = new PdfPCell(new Phrase(TestCaseDetails.get(current_execution),subCon));
							c1.setPadding(8);c1.setColspan(4);
							c1.setHorizontalAlignment(Element.ALIGN_LEFT);
							table.addCell(c1);

							c1 = new PdfPCell(new Phrase("Status",subHead));
							c1.setPadding(8);c1.setColspan(1);
							c1.setBackgroundColor(Color.LIGHT_GRAY);
							c1.setHorizontalAlignment(Element.ALIGN_CENTER);
							table.addCell(c1);

							boolean res = resultList.get(3).equalsIgnoreCase("Pass");
							String statmsg = resultList.get(4);
							unableToTakeScreenshot=statmsg.contains("Unable to capture the screen shot")||statmsg.contains("Browser Crashed");
							//int tempindex = statmsg.indexOf(". Screen Shot : ");

							Font subHead1 = new Font(subHead);
							subHead1.setColor(res ? Color.GREEN : Color.RED);

							c1 = new PdfPCell(new Phrase(res ? "Pass" : unableToTakeScreenshot ?statmsg: statmsg.substring(0, statmsg.indexOf(". Screen Shot : ")),subHead1));

							c1.setPadding(8);c1.setColspan(4);
							c1.setHorizontalAlignment(Element.ALIGN_LEFT);
							table.addCell(c1);

							document.add(table);

							document.close();
							file.close();
						}catch(Exception ee){
							log.error(" Unable to add Status table at the end of PDF Evidence ");
						}
						document  = null;
						file = null;
					}
					exportResult();



					if (browserType.equalsIgnoreCase("IE8")	|| browserType.equalsIgnoreCase("IE6")|| browserType.equalsIgnoreCase("IE")){
						log.info(" IE browser is invoked");

						System.setProperty("webdriver.ie.driver", System.getProperty("user.dir")+"\\IEDriverServer.exe");
						webdriver = new InternetExplorerDriver();
					} else if (browserType.equalsIgnoreCase("firefox") || browserType.equalsIgnoreCase("ff")) {
						log.info(" Firefox browser is invoked");
						
						System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir")+"\\geckodriver.exe");
//						FirefoxProfile firefoxProfile = new FirefoxProfile();
//						firefoxProfile.setEnableNativeEvents(true);
						webdriver = new FirefoxDriver();

					}else if (browserType.equalsIgnoreCase("chrome")) {
						log.info(" Chrome browser is invoked");
						System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"\\chromedriver.exe");

						webdriver = new ChromeDriver();
					} else if (browserType.equalsIgnoreCase("safari")) {
						log.info(" Safari browser is invoked");
						webdriver = new SafariDriver();
					} else if (browserType.equalsIgnoreCase("opera")) {
						log.info(" Opera browser is invoked");
						//webdriver = new OperaDriver();
					}/*else if (browserType.equalsIgnoreCase("html")) {
						log.info(" HTML Unit browser is invoked");
						webdriver = new HtmlUnitDriver(true);
					}*/else{
						System.out.println(" Can't find the desired browser \n Using firefox");
						log.warn(" Can't find the desired browser. Using firefox");
						FirefoxProfile firefoxProfile = new FirefoxProfile();
						firefoxProfile.setEnableNativeEvents(true);
						webdriver = new FirefoxDriver(firefoxProfile);
						browserType = "firefox";
						hMap.put("Browser", browserType);
						confDtls.setBrowser(browserType);
					}
					webdriver.manage().window().maximize();
					try {
						/*	java.util.List<String> exportResult = new ArrayList<String>();
					exportResult = resultList;
					ReportCounters[0] = ReportCounters[0] + 1;
					for (int ii = 0; ii < resultList.size(); ii = ii + 6) {
						if (resultList.get(ii + 3).equals("Fail")) {
							exportResult = resultList.subList(ii, ii + 6);
							ReportCounters[2] = ReportCounters[2] + 1;
							break;
						}
					}
					exportResultsExcel.exportExcelRows(exportResult);

					if(updateResultsDB){
						exportResultsExcel.exportResultsToSQL(exportResult);
					}*/

						testCase=currentTestCase+1;
						//ReportCounters[0]=ReportCounters[0]+1;
						//ReportCounters[2]=ReportCounters[2]+1;
						testInitiation();
						//resultList=null;

						while(executeFailedCases)
						{
							resultList = new ArrayList<String>();
							runFailedCases();
						}

						tearDown();

						RunTest.configrowCount++;
						Thread.currentThread().stop();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}


				else if(e.getMessage().contains("unexpected alert open"))
				{
					strScreenshotName="Unable to capture the screen shot: Unexpected alert open";
					System.out.println("Unable to take the screen shot: "+e.getMessage());
					log.error("Unable to take the screen shot: "+e.getMessage());
				}

			}
			/*try {
		//		f = ((TakesScreenshot) webdriver).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(f, new File(fileName));
				f.delete();
			} catch (IOException ioe) {	}
			 */
			if(createEvidence){
				try{
					Image image = Image.getInstance(pdfImgName);
					image.scaleToFit(500f,500f);
					image.scaleToFit(500f,500f);
					document.newPage();

					if(comment.toLowerCase().startsWith("req:")){
						document.add(new Paragraph("Requirement :: " + comment.substring(4)));
						document.add(new Paragraph(""));
					}
					document.add(new Paragraph(step));
					document.add(new Paragraph(""));
					document.add(image);
				}catch(Exception e){
					System.out.println(e.getMessage());
				}
			}
		}
	}

	public void createDir(String dirName,String errorMessage){
		File f = new File(dirName);
		try {
			if (!f.exists()) {
				f.mkdir();
				System.out.println("Directory Created :: " + errorMessage);
				log.info(" Directory Created :: " + errorMessage);
			}
		} catch (Throwable e) {
			log.error(" Unable to create directory with " + errorMessage);
			System.out.println("Unable to create directory with " + errorMessage);
		}
	}

	/**
	 * Method to perform execute steps
	 */

	public ResultDetails executeSteps(int start, int end, int loopcount, HashMap<Integer,TestDataDetails> testDataMap,ResultDetails resultDetails) {

		String warnMsg = resultDetails.getWarningMessage() ;

		String arrCon[] = new String[3];
		arrCon[0] = ""; arrCon[2] = ""; arrCon[1] = "";

		for (int i=1; i<=loopcount; i++) {

			hMap.put("LOOPCOUNTER", "" + i);

			for(int k=start;k<=end;k++){

				TestDataDetails tdd = (TestDataDetails) testDataMap.get(k);

				if(tdd == null){
					log.info(" Step - " + k + " Not found ");
					System.out.println(" Test Step :: " + k + " not found \n Skipping to next available step");
					continue;
				}

				log.info(" Executing case " + tdd.getTestCaseID() + " :: Step - " + k);

				if (tdd.getBrowserType().equalsIgnoreCase("COMMON") || tdd.getBrowserType().indexOf(browserType) != -1) {
					hMap.put("testStep_startTime" +
							"", new Date().getTime()+"");


					resultDetails = executeTestStep(tdd);
					hMap.put("testStep_endTime" +
							"", new Date().getTime()+"");
					String condition = tdd.getCondition();

					if(condition == null){
						condition = "";
					}

					condition = condition.toLowerCase();

					if(detailedLog){
						ExportDetailedLogs exportLogs = new ExportDetailedLogs(this);
						exportLogs.exportResultsToSQL(tdd,resultDetails);
					}

					if(tdd.getActionType() != null ){
						if(steplevelscreenshot || (condition.contains("scrshot")))
							takeScreenShot_Detail(tdd,resultDetails);

					}

					condition = condition.replace("scrshot","");

					if (!condition.equalsIgnoreCase("")) {
						condition =  condition.toUpperCase(); 
						System.out.println(" Condition :: " + condition);
						arrCon = condition.split(":");
					}
					else
						arrCon[0] ="";


					if(arrCon[0].toLowerCase().equalsIgnoreCase("IF") && tdd.getActionType().equalsIgnoreCase("VERIFY")){

						System.out.println(" ? P A S S ? ");
						int from = k;
						int pos = 2;
						if(resultDetails.getFlag())
							pos = 1;

						try{
							if(!arrCon[pos].equalsIgnoreCase("NEXT")&&!arrCon[pos].equalsIgnoreCase("END"))
								from = Integer.parseInt(arrCon[pos]) - 1;
							else if(arrCon[pos].equalsIgnoreCase("END"))
								from = end - 1;
						} catch (Exception ex) {
							System.out.println("Invalid step number." + arrCon[0].split(":")[1]);
						}
						k = from;
						continue;
					}

					if (!resultDetails.getFlag()) {

						System.out.println("?   F A I L   ?");
						takeScreenShot(tdd);

						String strErrorMsg = "Test case failed at Step No. :: " + tdd.getTestDataID();

						if (!resultDetails.getErrorMessage().equalsIgnoreCase("")) 
							strErrorMsg = strErrorMsg +  "  Error Message ::  " + resultDetails.getErrorMessage();
						else 
							strErrorMsg = strErrorMsg +"  " +  platformErrors;

						resultDetails.setFlag(false);

						if(inLoop){
							warnMsg = "In Loop at " + current_execution + " Round : " + i + resultDetails.getErrorMessage();
						}
						if(inExecute){
							warnMsg = "In Execute at " + current_execution + " : " + tdd.getTestCaseID() + resultDetails.getErrorMessage();
						}

						resultDetails.setErrorMessage(warnMsg);
						return resultDetails;	

					}else {
						System.out.println("?   P A S S   ?");

					}
				}
			}
		}
		resultDetails.setComment(warnMsg);
		resultDetails.setFlag(true);
		return resultDetails;
	}

	public void stop() {
		try{
			tearDown();
		}catch(Exception e){	
		}
		RunTest.exit_status = 1;
		RunTest.configrowCount++;
		Thread.currentThread().stop();
	}	
	public void  exportResult()
	{

		java.util.List<String> exportResult = new ArrayList<String>();
		exportResult = resultList;
		ReportCounters[0] = ReportCounters[0] + 1;
		for (int ii = 0; ii < resultList.size(); ii = ii + 6) {
			if (resultList.get(ii + 3).equals("Fail")) {
				exportResult = resultList.subList(ii, ii + 6);
				ReportCounters[2] = ReportCounters[2] + 1;
				break;
			}
		}
		if (isTestLinkRequired) {
			if (exportResult.get(3).equalsIgnoreCase("Pass") || (!executeFailedCases))
				testLink.writeTestResult(exportResult);
		}
		if (exportResult.get(3).equals("Fail") && reportFailureAsBug && (!executeFailedCases)) {
			try{
				/*if(miscProps.get("bt_tool").toString().equalsIgnoreCase(("JIRA")))
					{
						try {
							new JIRABugLog(miscProps,log).logBug(exportResult);
							//JIRABugLog.logBug(exportResult,miscProps);
						} catch (Exception e) {
							System.out.println("Unable to log the bug in JIRA");
							System.out.println("Error: "+e.getMessage());
							log.error(" Unable to log the bug in JIRA");
							log.error(" Error: "+e.getMessage());
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/

				if(miscProps.get("bt_tool").toString().equalsIgnoreCase(("JIRA")))
				{
					try {

						if(miscProps.get("bt_IssueType").toString().equalsIgnoreCase(("SUBTASK")))
						{

							//new JIRABugLog(miscProps,log).searchAndCreateSubTask(exportResult);
						}
						else
						{

							if(miscProps.get("bt_AllowDuplicates").toString().equalsIgnoreCase(("TRUE")))
							{
								new JIRABugLog(miscProps,log).logBug(exportResult);
							}
							else
							{
								new JIRABugLog(miscProps,log).logTestBugIfNotExists(exportResult);
							}
						}
					}
					catch(Exception e)
					{
						System.out.println("Error:"+e.getMessage());
						log.error(e.getMessage());

					}
				}
				else if(miscProps.get("bt_tool").toString().equalsIgnoreCase(("Bugzilla")))
				{
					try{
						new BugzillaBugLog(miscProps, log).logBug(exportResult);
					}
					catch(Exception e)
					{
						System.out.println("Unable to log bug in Bugzilla");
						System.out.println("Error: "+e.getMessage());
						log.error(" Unable to log bug in Bugzilla");
						log.error("Error: "+e.getMessage());
					}
				}
			}
			catch(Exception e)
			{
				try{
					new BugzillaBugLog(miscProps, log).logBug(exportResult);
				}
				catch(Exception ee)
				{
					System.out.println("Unable to log bug in Bugzilla");
					System.out.println("Error: "+ee.getMessage());
					log.error(" Unable to log bug in Bugzilla");
					log.error("Error: "+ee.getMessage());
				}
			}


		}
		exportResultsExcel.exportExcelRows(exportResult);

		if(updateResultsDB){
			exportResultsExcel.exportResultsToSQL(exportResult);
		}
	}

}