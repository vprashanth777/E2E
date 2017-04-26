package com.java;

import java.io.*;
import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.*;
import java.util.*;

import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;
import com.java.importnexport.*;
import com.java.objects.*;
//import com.opera.core.systems.OperaDriver;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.openqa.selenium.*;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
//import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.xhtmlrenderer.pdf.ITextRenderer;

/**
 * This class is used to run the Tests based on the configData provided.
 * Implements Runnable Interface for multi threading
 */

@SuppressWarnings({"deprecation","unchecked"})
public class FunctionalDriver implements Runnable {
	
	private String app;
	private String browser;
	private String baseURL;
	private ConfigDetails confDtls;
	
	private int total = 0;
	private int passed = 0;
	private boolean createpdf = false;  
	private boolean sendMail = false;
	private boolean closeBrowserSessions = false;
	private boolean isGridEnabled = false;
	private boolean clearcookies = true;
	
	public Logger log;
	
	private Date d;
	private WebDriver webdriver;
	private String className = "";
	private String strScreenshotName;
	private String current_method_exec = "";
	private String htmlFile,pdfFile,excelFile;
	private Properties miscprops = new Properties();
	private String screenShotdir = "..\\ScreenShots\\";
	private ArrayList<String> methodNames = new ArrayList<String>();
	public HashMap<String,String> hMap = new HashMap<String,String>();
	private ArrayList<List<String>> html = new ArrayList<List<String>>();
	private String timeStamp = "";
	private SimpleDateFormat sdf = new SimpleDateFormat("MMddyy_HHmmss");
	private HashMap<String, Properties> setup_TestData = new HashMap<String, Properties>();
	private HashMap<String, Integer> setup_TestDataCounter = new HashMap<String, Integer>();
	

	/**
	 * Constructor of Class which takes config object as parameter 
	 */
	
	public FunctionalDriver(ConfigDetails config) {
		confDtls = config;
	}

	/**
	 * Method to read the properties from properties file
	 */
	
	private void readProperties(){
		
		ImportProperties xml = new ImportProperties("..//Properties//"+confDtls.getAppName()+".xml",log);
		miscprops.putAll(xml.setProperties("common"));
		miscprops.putAll(xml.setProperties("misc"));
		
		sendMail = Boolean.parseBoolean(miscprops.getProperty("sendMailReport", "true"));
		log.info(" Property :: sendMail -> " + sendMail);
		
		createpdf = Boolean.parseBoolean(miscprops.getProperty("createpdf", "false"));
		log.info(" Property :: createpdf -> " + createpdf);
		
		closeBrowserSessions = Boolean.parseBoolean(miscprops.getProperty("closeBrowserSessions","false"));
		log.info(" Property :: closeBrowserSessions -> " + closeBrowserSessions);
		
		screenShotdir = miscprops.getProperty("screenShotdir","..\\ScreenShots\\");
		log.info(" Property :: screenShotdir -> " + screenShotdir);
		
		isGridEnabled = Boolean.parseBoolean(miscprops.getProperty("useGrid","false"));
		log.info(" Property :: isGridEnabled -> " + isGridEnabled);
		
		clearcookies = Boolean.parseBoolean(miscprops.getProperty("clearCookies","true"));
		log.info(" Property :: clearcookies -> " + clearcookies);
		if(!screenShotdir.endsWith("\\")){
			screenShotdir = screenShotdir  + "\\";
		}
		
		if(sendMail)
			miscprops.putAll(xml.setProperties("email"));
	}

	/**
	 * Method to read config details from config Object
	 */
	
	private void getConfigData() {

		
		baseURL = confDtls.getScriptPath();
		log.info(" URL is "+baseURL);
		
		className = confDtls.getTestDataSource();
		
		if(!className.contains(".")){
			className = "tests." + className;
		}
		log.info(" Class required for execution " + className);
		
		browser = confDtls.getBrowser();
		log.info(" Browser :: "+ browser);
		
		String temp = confDtls.getTestCases();
		
		if(temp.contains(".") || temp.contains("*")){
		
			try{
				
				Class<Object> cobj = (Class<Object>) Class.forName(className);
				Method mobjarray[] = cobj.getDeclaredMethods();

				for (Method mobj : mobjarray ) {
					String str = mobj.getName();
					if (str.equals(""))
						continue;
					else if(str.startsWith("test_") && str.matches(temp))
						methodNames.add(str);
				}
			}catch(Exception e){
				log.error(" Exception while retrieving the method names matching with regex ");
				log.error(e.getMessage());
				System.out.println(" Exception while retrieving the method names matching with regex ");
				System.out.println(e.getMessage());
				RunTest.configrowCount++;
				RunTest.exit_status = 1;
				Thread.currentThread().stop();
			}
		}
		else {
			String[] arrNames = temp.split(",");
			for (String str : arrNames) {
				if (str.equals(""))
					continue;
				else if(str.startsWith("test_"))
					methodNames.add(str);
			}
		}
	}

	/**
	 * Method to invoke browser
	 */
	
	private void invokeBrowser() {

		try {
		
			if (isGridEnabled) {
				DriverObject driverObj = new DriverObject(browser, miscprops,log);
				webdriver = driverObj.createWebDriver();
			} else {
			
				if(closeBrowserSessions)
					closeBrowser(browser);
				if (browser.equalsIgnoreCase("IE8")
						|| browser.equalsIgnoreCase("IE6")
						|| browser.equalsIgnoreCase("IE")) {
					webdriver = new InternetExplorerDriver();
				} else if (browser.equalsIgnoreCase("firefox")
						|| browser.equalsIgnoreCase("ff")) {
					FirefoxProfile firefoxProfile = new FirefoxProfile();
					firefoxProfile.setEnableNativeEvents(true);
					webdriver = new FirefoxDriver(firefoxProfile);
				} else if (browser.equalsIgnoreCase("chrome")) {
					webdriver = new ChromeDriver();
				} else if (browser.equalsIgnoreCase("safari")) {
					webdriver = new SafariDriver();
				} else if (browser.equalsIgnoreCase("opera")) {
					//webdriver = new OperaDriver();
				} /*else if (browser.equalsIgnoreCase("html")) {
					webdriver = new HtmlUnitDriver(true);
				}*/else {
					System.out.println(" Can't find the desired browser \n Using firefox");
					FirefoxProfile firefoxProfile = new FirefoxProfile();
					firefoxProfile.setEnableNativeEvents(true);
					webdriver = new FirefoxDriver(firefoxProfile);
					browser = "firefox";
					confDtls.setBrowser(browser);
				}
			}
		} catch (Exception e) {
			log.info("Can't Create Driver Object. Stopping the thread");
			log.info(e.getMessage());
			System.out.println("Can't Create Driver Object. Stopping the thread");
			System.out.println(e.getMessage());
			RunTest.configrowCount++;
			RunTest.exit_status = 1;
			tearDown();
			Thread.currentThread().stop();
		}
	}

	/**
	 * Method to run the tests
	 */

	private void testInitiation()  {

		log.info(" Loading URL of page ");
		webdriver.get(baseURL);
		
		if (!confDtls.getBrowser().equalsIgnoreCase("ANDROID"))
			webdriver.manage().window().maximize();

		log.info(" Clearing cookies and reloading URL");
		webdriver.manage().deleteAllCookies();
		webdriver.get(baseURL);
		
		int i = 1;
		long start = 0;
		
		exportExcelHeader();
		List<String> result = new ArrayList<String>();
		
		System.out.println("----------------------------------------");
		
		for (String methodName : methodNames) {
		
			current_method_exec = methodName;
			log.info(" Method to execute :: "+current_method_exec);
			
			GeneralLibrary runner = new GeneralLibrary();
			
			try{
			if(clearcookies){
				log.info(" Clearing cookies and reloading the URL ");
				webdriver.manage().deleteAllCookies();
				webdriver.get(baseURL);
				webdriver.manage().deleteAllCookies();
			}
				webdriver.get(baseURL);
			}catch(Exception e){
				log.error(" Unable to clear the cookies and navigate to home Page");
				log.error(e.getMessage());
				System.out.println(" Unable to clear the cookies and navigate to home Page");
			}

			String errMsg = "";
			
			try {
				result = new ArrayList<String>();
				start = 0;
				
				result.add(i+"");
				result.add(className);
				result.add(methodName);

				String tctitle = className + " - " + methodName;
				System.out.println("Executing Test Case - " + tctitle);

				Class<Object> cobj = (Class<Object>) Class.forName(className);
				log.info(" Loaded CLass to JVM ");
				
				log.info(" Created Instance to class ");
				runner = (GeneralLibrary) cobj.newInstance();
				
				log.info(" Functional Driver set ");
				runner.setFunctionalDriver(this);
				
				log.info(" Webdriver set ");
				runner.setWebDriver(webdriver);
				
				log.info(" Retrieving Method Name ");
				Method mobj = cobj.getDeclaredMethod(methodName);
				
				log.info(" Hmap Set ");
				start = new Date().getTime();
				runner.hMap.putAll(hMap);
				
				log.info(" Method Invoked");
				mobj.invoke(runner);

			} catch (ClassNotFoundException e) {
				errMsg = " Class " + className + ": not found";
				System.out.println(errMsg);
				log.error(errMsg);
				log.error(e.getMessage());
				runner.fail(errMsg);
				RunTest.exit_status = 1;
				System.out.println(e.getMessage());
			} catch (NoSuchMethodException e) {
				errMsg = " Method " + methodName + " : not found";
				runner.fail(errMsg);
				log.error(errMsg);
				log.error(e.getMessage());
				System.out.println(errMsg);
				RunTest.exit_status = 1;
				System.out.println(e.getMessage());
			} catch (NullPointerException e) {
				errMsg = " Null Pointer Exception ";
				System.out.println(errMsg);
				log.error(errMsg);
				log.error(e.getMessage());
				runner.fail(errMsg);
				RunTest.exit_status = 1;
				System.out.println(e.getMessage());
			}catch (Exception e) {
				Throwable t = e.getCause();
				int stk = t.getStackTrace().length - e.getStackTrace().length;
				StackTraceElement main = t.getStackTrace()[stk-1];
				errMsg = "Exception at " + main.getLineNumber() + " :: " + t.getClass();
				runner.fail(errMsg);
				log.error(errMsg);
				log.error(e.getMessage());
				System.out.println(errMsg);
				RunTest.exit_status = 1;
			} finally {
				
				long end = new Date().getTime();
				log.info(" Completed Execution ");
				
				errMsg = runner.getResult();
				hMap.putAll(runner.hMap);
				if(runner.isPassed()){
					log.info(" Test Case Passed");
					passed++;
					result.add("Pass");
				} else {
					log.info(" Test Case Failed. Taking Screen shot ");
					takeScreenShot();
					runner.onFail();
					result.add("Fail");
					errMsg = errMsg + ". ScreenShot " + strScreenshotName;	
				}
				
				result.add(errMsg);
				System.out.println("----------------------------------------");
				result.add(new Date().toString());
				if(start != 0){
					end = end - start;
					start = end/1000;
					end = end%1000;
					result.add(start+"."+end);
				} else
					result.add("0.0");

				exportExcelRows(result);
				errMsg = errMsg.replace(". ScreenShot " + strScreenshotName,"");
				result.add(4, errMsg);
				html.add(result);
				i++;
			}
		}
		
		exportExcelSummary();
		generateHTML();
		if(createpdf)
			createPDF();
	}

	/**
	 * Method to create a PDF File 
	 */
	
	private void createPDF() {
		
		log.info("Creating PDF Report ");
		
		try{
			Thread.sleep(2000);
			String url = new File(htmlFile).toURI().toURL().toString();
			OutputStream os = new FileOutputStream(pdfFile);  
			ITextRenderer renderer = new ITextRenderer();
		    renderer.setDocument(url);      
		    renderer.layout();
		    renderer.createPDF(os);        
		    os.close();
		    Thread.sleep(2000);
		}catch(Exception e){
			log.error(" Unable to create PDF File");
			log.error(e.getMessage());
			System.out.println(" Unable to create PDF File");
			e.printStackTrace();
			createpdf = false;
		}
		
	}

	/**
	 * Method to run as part of thread 
	 */
	
	public void run() {

		try {
			
			d = new Date();
			timeStamp = sdf.format(d);
			app = confDtls.getAppName();
			
			hMap.put("TimeStamp", timeStamp);
			
			String name = "..//TestReports//"+ confDtls.getAppName() + "//Logs//Log - " + hMap.get("TimeStamp") + ".log";
			
			FileAppender fa = new FileAppender(new PatternLayout("[%-5p][%9d] - %m%n"),name,false);
			fa.activateOptions();
			log = Logger.getLogger(name);
			log.addAppender(fa);
			
			htmlFile = "..//TestReports//" + app + "//" + "Functional Test Results_" + timeStamp +".html";
			pdfFile = "..//TestReports//" + app + "//" + "Functional Test Results_" + timeStamp +".pdf";
			excelFile = "..//TestReports//" + app + "//" + "Functional Test Results_" + timeStamp +".xls";
			
			log.info(" Reading Properties");
			readProperties();
			
			log.info(" Reading Configuration Data ");
			getConfigData();
			
			log.info(" Reading Configuration Data ");
			readTestdata();
			
			System.out.println(className);
			System.out.println(methodNames);
			
			total = methodNames.size();
			
			if(methodNames.isEmpty()){
				log.error(" No Methods are found to execute as specified");
				System.out.println(" No Methods are found to execute as specified");
				RunTest.configrowCount++;
				Thread.currentThread().stop();
			}
			
			log.info(" Invoking Browser ");
			invokeBrowser();
			
			log.info(" Initiating running the test cases ");
			testInitiation();
			
			log.info(" Competed execution. Closing browser ");
			tearDown();
			if(sendMail)
				sendMail();
			RunTest.configrowCount++;
		} catch (Exception e) {
			log.error(" Encountered Unknoen Exception while running Functional Scripts  ");
			log.error(e.getMessage());
			RunTest.configrowCount++;
			RunTest.exit_status = 1;
			tearDown();
		}
	}
	
	/**
	 * Method to read the test data from the file
	 */
	
	private void readTestdata() {
		
		log.info(" Reading Test Data ");
		
		String setUpQuery = "Select * from [Setup_TestData$]";
		
		try{
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			Connection conn = DriverManager.getConnection("jdbc:odbc:Driver={Microsoft Excel Driver (*.xls)};DBQ=..\\Testinputs\\"+ app + ".xls;DriverID=22;READONLY=false", "", "");
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(setUpQuery);
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			
			while (rs.next()) {

				Properties props = new Properties();
				String name =  rs.getString(2).trim().toLowerCase();
				int i = 3 ;
				while(i <= count){
					String key_value = rs.getString(i);
					if(key_value == null )
						break;
					int n = key_value.indexOf(":=");
					if( n <= 1)
						System.out.println(" Invalid format specified for " + key_value);
					else{
						String key = key_value.substring(0,n).toLowerCase();
						String value = key_value.substring(n+2);
						props.put(key, value);
					}
					i++;
				}

				int keycount = 0 ;
				if(setup_TestDataCounter.containsKey(name))
					keycount = setup_TestDataCounter.get(name);
				keycount = keycount + 1;

				setup_TestDataCounter.put(name,keycount);
				setup_TestData.put(name+"#"+keycount, props);
			}
				rs.close();
		}catch(Exception e){
			log.info(" Unbale to Load Test Data");
			log.info(e.getMessage());
			System.out.println(" Unbale to Load Test Data");
		}
	}

	/**
	 * Method to close the browser 
	 */
	
	private void tearDown() {
		if (webdriver != null)
			webdriver.quit();
	}
	
	/**
	 * Method to export the result summary to excel
	 */
	
	private void exportExcelSummary() {
	
		log.info(" Exporting Test Summary to excel Sheet ");
		
		try {
			InputStream inputStream = new FileInputStream(excelFile);
			POIFSFileSystem fs = new POIFSFileSystem(inputStream);
			FileOutputStream fileOut = new FileOutputStream(excelFile);
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(0);
			sheet.setAutobreaks(false);
			
			int rows; // No of rows
			rows = sheet.getPhysicalNumberOfRows();
			
			for (int ii = 0; ii <= 3; ii++) {

				HSSFRow row = sheet.createRow(rows + ii + 1);
				HSSFCell cell;
				HSSFRichTextString str;
				switch (ii) {
				case 0:
					cell = row.createCell(1);
					cell.setCellValue("Browser Tested");
					cell = row.createCell(2);
					str = new HSSFRichTextString(confDtls.getBrowser());
					cell.setCellValue(str);
					break;
				case 1:
					cell = row.createCell(1);
					cell.setCellValue("Total Cases Executed");
					cell = row.createCell(2);
					str = new HSSFRichTextString(total+"");
					cell.setCellValue(str);
					break;
				case 2:
					cell = row.createCell(1);
					cell.setCellValue("Total Cases Passed");
					cell = row.createCell(2);
					str = new HSSFRichTextString(passed+"");
					cell.setCellValue(str);
					break;
				case 3:
					cell = row.createCell(1);
					cell.setCellValue("Total Cases Failed");
					cell = row.createCell(2);
					str = new HSSFRichTextString((total-passed)+"");
					cell.setCellValue(str);
					break;
				}
			}
			wb.write(fileOut);
			fileOut.close();
		}catch(Exception e){
			log.error(" Unable to export test summary to excel sheet");
			log.error(e.getMessage());
			System.out.println(" Unable to export test summary to excel sheet");
		}
		
	}

	/**
	 * Method to export column headers to excel
	 */
	
	private void exportExcelHeader() {

		log.info(" Exporting Header for Excel ");
		
		try {
			
			FileOutputStream fileOut = null;
			try {
				File f = new File("..//TestReports//" + confDtls.getAppName());
				if (!f.exists())
					f.mkdir();
			} catch (Exception e) {
				System.out.println("Unable to create DIR for Testresults Application");
			}

			System.out.println("OUT FILE : " + excelFile);

			HSSFWorkbook wb = new HSSFWorkbook();
			wb.createSheet("Test Result");
			fileOut = new FileOutputStream(excelFile);
			
			HSSFSheet sheet = wb.getSheetAt(0);
			sheet.setAutobreaks(false);

			HSSFRow row = sheet.createRow((short) 0);
			HSSFFont font = wb.createFont();
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			HSSFCellStyle cellStyle = wb.createCellStyle();
			cellStyle.setFont(font);

			row.createCell(0).setCellStyle(cellStyle);
			row.createCell(1).setCellStyle(cellStyle);
			row.createCell(2).setCellStyle(cellStyle);
			row.createCell(3).setCellStyle(cellStyle);
			row.createCell(4).setCellStyle(cellStyle);
			row.createCell(5).setCellStyle(cellStyle);
			row.createCell(6).setCellStyle(cellStyle);

			row.createCell(0).setCellValue("S. No");
			row.createCell(1).setCellValue("Test Class");
			row.createCell(2).setCellValue("Test Function");
			row.createCell(3).setCellValue("Result(P / F)");
			row.createCell(4).setCellValue("Error Message");
			row.createCell(5).setCellValue("Time Stamp");
			row.createCell(6).setCellValue("Time Taken (in Seconds)");

			wb.write(fileOut);
			fileOut.close();
		} catch (Exception e) {
			log.error(" Unable to create headers in Excel report");
			log.error(e.getMessage());
			System.out.println(" Unable to create headers in Excel report");
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Method to export details of each and every testcase after execution
	 */
	
	private void exportExcelRows(List<String> result) {
	
		log.info(" Exporting Test Result to excel ");
		
		try {

			InputStream inputStream = new FileInputStream(excelFile);
			POIFSFileSystem fs = new POIFSFileSystem(inputStream);
			FileOutputStream fileOut = new FileOutputStream(excelFile);
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(0);
			sheet.setAutobreaks(false);
			
			int rows;
			rows = sheet.getPhysicalNumberOfRows();

			HSSFRow row = sheet.createRow(rows);

			for (int i = 0; i < 7; i++) {
				sheet.setColumnWidth(2,(256 * 13));
				sheet.setColumnWidth(1, (256 * 25));
				sheet.setColumnWidth(2, (256 * 14));
				sheet.setColumnWidth(3, (256 * 30));
				sheet.setColumnWidth(4, (256 * 28));
				sheet.setColumnWidth(5, (256 * 28));
				sheet.setColumnWidth(6, (256 * 28));
				HSSFCell cell = row.createCell(i);
				HSSFRichTextString str = new HSSFRichTextString(result.get(i).toString());
				cell.setCellValue(str);
			}
			wb.write(fileOut);
			fileOut.close();
		} catch(Exception e){
			log.error("Exception occured while experting results list to excel ");
			log.error(e.getMessage());
			System.out.println("Exception occured while experting results list to excel ");
			System.out.println(e.getMessage());
		} 
	}

	/**
	 * Method to generate the html file after execution of all the test cases
	 */
	
	private void generateHTML(){
		
		log.info(" Generating HTML report ");
		
		String testType = "General";
		String strChart = passed + "," + (total-passed);
		String chartDimensions = "";
		String chartMaxHeight = "";
		
		if (total < 10) {
			chartDimensions = "0|5|10";
			chartMaxHeight = "10";
		} else if ((total >= 10) && (total < 20)) {
			chartDimensions = "0|5|10|15|20";
			chartMaxHeight = "20";
		} else if ((total >= 20) && (total < 50)) {
			chartDimensions = "0|10|20|30|40|50";
			chartMaxHeight = "50";
		} else if ((total >= 50) && (total < 100)) {
			chartDimensions = "0|20|40|60|80|100";
			chartMaxHeight = "100";
		} else if ((total >= 100) && (total < 200)) {
			chartDimensions = "0|40|80|120|160|200";
			chartMaxHeight = "200";
		} else if ((total >= 200) && (total < 300)) {
			chartDimensions = "0|50|100|150|200|250|300";
			chartMaxHeight = "300";
		} else if ((total >= 300) && (total < 400)) {
			chartDimensions = "0|80|160|240|320|400";
			chartMaxHeight = "400";
		} else if ((total >= 400) && (total < 500)) {
			chartDimensions = "0|100|200|300|400|500";
			chartMaxHeight = "500";
		} else if ((total >= 500) && (total < 800)) {
			chartDimensions = "0|160|320|480|640|800";
			chartMaxHeight = "800";
		} else if ((total >= 800) && (total < 1000)) {
				chartDimensions = "0|200|400|600|800|1000";
				chartMaxHeight = "1000";
		} else {
			System.out.println("Error: Invalid Chart Scale");
		}

		Date d = new Date();
		try {
			d = new SimpleDateFormat("MMddyy_HHmmss").parse(timeStamp);
		} catch (Exception e) {}
		
		String ln = "\n";

		String headContent = "<html> "
			+ ln + " <head>" + ln + " <style>" + ln + "	td.header {" + ln + " background-color:#3399FF;border-top:0px solid #333333;border-bottom:1px dashed #000000;"
			+ ln + " }" + " td.testDetails { " + ln	+ " background-color:#3399FF;border-top:5px solid #3399FF;border-bottom:1px dashed #000000;" + ln + " }" + ln 
			+ " span.testDetails {" + ln + " font-size:12px;font-weight:bold;color:#000000;line-height:200%;font-family:verdana;text-decoration:none;" + ln + "}" + ln
			+ "td.execDetails { " + ln + " background-color:#3399FF;border-top:5px solid #3399FF;border-bottom:0px dashed #000000;"	+ ln + "}" + ln + " span.execDetails" +
			" {" + ln + " font-size:12px;font-weight:bold;color:#000000;line-height:200%;font-family:verdana;text-decoration:none;"	+ ln + "}" + ln + "span.pass { " + ln +
			" font-size: 14px;font-weight:bold;line-height:100%;color:#00FF00;font-family:arial; " + ln	+ "	}" + ln + " span.fail { " + ln +" font-size: 14px;font-weight:" +
			"bold;color:#FF0000;line-height:100%;font-family:arial; " + ln	+ " } "	+ ln+ " span.title { " + " font-size: 14px;font-weight:normal;color:#000000;line-height:100%;" +
			"font-family:arial; "+ ln + " } "+ ln + "div.status { " + ln + "width:40em;text-wrap:none;"+ ln + "}"+ ln + " td.reqDetails { " + ln + " font-size:12px;font-weight:bold;" +
			"color:#000000;line-height:100%;font-family:verdana;text-decoration:none; "	+ ln + " } " + ln + "</style> " + ln + " </head> "	+ ln + "<body leftmargin=\"0\" marginwidth=\"0\"" +
			" topmargin=\"0\" marginheight=\"0\" offset=\"0\" bgcolor='#FFFFFF'>";

		String logo = "http://valuelabs.com/templates/valuelabs/images/valuelabs-logo.gif";
		logo = miscprops.getProperty("logo", logo);
		
		String header = "<div id=\"header\"> " + ln	+ " <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">" + ln + " <tr> " + ln + "<td align=\"left\" valign=\"middle\" " +
			"class=\"header\"> " + ln + "<img id=\"editableImg1\"" + " src=\"" + logo + "\" height=\"60px\" width=\"250px\" BORDER=\"0\" align=\"center\" />" + ln + "</td>" + ln +
			"<td align=\"left\"" + " valign=\"middle\" class=\"header\">" + ln + "<span style=\"font-size:14px;font-weight:bold;color:#000000;line-height:200%;font-family:verdana;" +
			"text-decoration:none;\">" + ln + "FUNCTIONAL AUTOMATION TEST RESULTS" + ln + "</span>" + ln + "</td>" + ln + " </tr>" + ln + "</table>" + ln + "</div>";

		String testDetails = "<div id=\"testDetails\">" + ln + "<table width=\"100%\" cellpadding=\"3\" cellspacing=\"0\"> " + ln + "<tr> " + ln + " <td align=\"left\"" +
			" valign=\"middle\" colspan=\"2\" class=\"testDetails\"> " + ln + "<span class=\"testDetails\">" + ln + " Date &amp; Time : " + d.toString() + ln + "</span>" + ln + "</td>" +
			ln + "<td align=\"left\" valign=\"middle\" class=\"testDetails\">" + ln + "<span  class=\"testDetails\">" + ln + "Test Type : " + testType + ln + " </span> " + ln
			+ " </td> " + ln + "<td align=\"left\" " + "valign=\"middle\" class=\"testDetails\"> " + ln + "<span  class=\"testDetails\"> " + ln + "Application :" +
			" <font color=\"#FFFFFF\">" + app + " </font> " + ln + " </span>" + ln + " </td> " + ln + " </tr>" + ln + "<tr> " + ln + "<td align=\"left\" valign=\"middle\" " +
			"class=\"execDetails\">" + ln + "<span class=\"execDetails\">" + ln + "Test Cases Executed : " + total + "</span>" + ln + "</td>" + ln + "<td align=\"left\" valign=\"middle\"" +
			" class=\"execDetails\">"+ ln + "<span class=\"execDetails\">" + ln + "Passed : " + passed + "</span>" + ln + "</td>" + ln + "<td align=\"left\" " +
			"valign=\"middle\" class=\"execDetails\">" + ln + "<span class=\"execDetails\">" + ln + "Failed :" + (total - passed) + "</span>"
			+ ln + "</td>" + ln + "<td align=\"left\" valign=\"middle\" class=\"execDetails\">" + ln + "<span class=\"execDetails\">" + ln +
			"Browser: " + browser + "</span>" + ln + "</td>" + ln + "</tr>" + ln + "</table>" + ln + "</div> <br/>";

		String graph = "<div id=\"graph\"  style=\"padding-left:10px\" > " + ln + "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"" +
			" bgcolor='#FFFFFF'> " + ln + "<tr> " + ln + "<td bgcolor=\"#FFFFFF\" valign=\"top\" width=\"99%\">" + ln + "<img id=\"graph\" " +
			"src=\"http://chart.apis.google.com/chart?" + "cht=bvg&amp;chs=350x175&amp;chd=t:" + strChart + "&amp;chds=0," + chartMaxHeight + 
			"&amp;chxt=x,y&amp;chxs=0,000000,12|1,000000,12&amp;chco=00FF00|FF0000|FFFF00&amp;chbh=50,0,20&amp;" + "chxl=0:|Passed|Failed|1:|" +
			chartDimensions + "&amp;chg=25,16.667,2,5&amp;chtt=Total+Test+Cases+=+" + total + "&amp;chts=000000,15\" BORDER=\"0\" align=\"left\" />"
			+ ln+ "</td>" + ln + "</tr>" + ln + "</table>" + ln + "</div>" + ln + "<br/>";

		String genDetails = "<div id=\"genDetails\"  style=\"padding-left:10px\" >" + ln + "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" " +
			"bgcolor='#FFFFFF'>" + "<tr>" + ln + "<td>" + ln + "<span style=\"font-size:20px;font-weight:bold;color:#000000;font-family:arial;line-height:110%;\">"
			+ ln + "General Details" + ln + "</span>" + ln + "</td>" + ln + " </tr>" + ln + "<tr>" + ln + "<td>" + ln + "<span style=\"font-size:12px;font-weight:bold;" +
			"color:#000000;font-family:arial;" + "line-height:110%;\">" + ln + "URL : " + "</span>" + ln + "<a href=\"" + baseURL + "\" style=\"font-size:12px;color:#0000FF;" +
			"line-height:150%;font-family:trebuchet ms;\">" + baseURL + "</a> " + ln + "</td>" + ln + " </tr>" + ln + "</table>" + ln + "</div>";

		String testCaseDetails = "<div id=\"testcaseDetails\" style=\"padding-left:15px\">"	+ ln + " <p> " + "<span style=\"font-size: 15px;font-weight:bold;color:#000000;" +
			"font-family:arial;\">Items Tested:</span> </p>" + ln + "<table width=\"750px\" border=\"1px\" cellpadding=\"5px\" cellspacing=\"0px\" >" + ln + "<tr>" + ln +
			"<td colspan=\"5\"> <h2 style=\"font-size:12px;font-weight:bold;color:#000000;line-height:150%;font-family:verdana;text-decoration:none;\" align=\"middle\"> " +
			" Functional Report </h2> " + ln + "</td>" + ln + "</tr>" + ln + "<tr>" + ln + "<td align =\"middle\" class=\"reqDetails\">S.no</td>" + ln + " <td align =\"middle\" "+
			" class=\"reqDetails\"> Class Name  </td>" + ln + "<td align =\"center\" class=\"reqDetails\"> Function Name  </td>" + ln + "<td align =\"middle\" class=\"reqDetails\">" +
			" Status </td>" + ln + "<td align =\"middle\" class=\"reqDetails\"> Error Message</td>" + ln + " </tr>";

		try {
						
			for(List<String> res : html){
					
				String style = "fail";
				
				String tcid = res.get(0);
				String cname = res.get(1);
				String fname = res.get(2);
				String status = res.get(3);
				String errMsg = res.get(4);
				
				if(status.equals("Pass"))
					style = "pass";
					
				String str = "<tr>" + ln + "<td align =\"middle\">" + ln + "<span class=\""+style+"\">" + tcid + "</span>"+ln+"</td>"
					+ ln + " <td align =\"middle\" > " + cname + "</td> " + ln + " <td align =\"center\" >" + fname + " </td> "
					+ ln + " <td align =\"middle\" > " + ln + "<span class=\""+style+"\">" + status + " </span></td> " + ln + " <td align =\"middle\" ><div class=\"status\">" + errMsg + "</div></td>"
					+ ln + " </tr>" ;
				
				testCaseDetails = testCaseDetails + str;
					
				}
				testCaseDetails = testCaseDetails + "</table>" + ln + "</div>" + ln + "<br/>";
			} catch (Exception e) {
				log.error(" Exception while creating html text for Test results ");
				log.error(e.getMessage());
				System.out.println(e.getMessage());
				testCaseDetails = "";
			}

			BufferedWriter out = null;
			
			try {
				out = new BufferedWriter(new FileWriter(htmlFile));
				out.write(headContent);
				out.write(header);
				out.write(testDetails);
				out.write(graph);
				out.write(genDetails);
				out.write(testCaseDetails);
				out.write("</body>" + ln + "</html>");
			
			} catch (Exception e) {
				log.error(" Exception while generating html report");
				log.error(e.getMessage());
				System.err.println(" Exception while generating html report");
				System.out.println(e.getMessage());
			}finally{
				try{
					out.close();
				}catch(Exception e){}
			}
		}
	
	/**
	 * Message to send mail as per user requirement
	 */
	
	private void sendMail(){
		
		log.info(" Sending Mail ");
		
		Message msg;
		
		String smtpHostName = miscprops.getProperty("SMTP_HOST_NAME");
		String recipient = miscprops.getProperty("recipients");
		String from = miscprops.getProperty("from");
		String subject = miscprops.getProperty("subject","Automation Test Results") + " -- Functional Reports";
		String message = miscprops.getProperty("message");
		String port = miscprops.getProperty("SMTP_PORT");		

		String countersText = "<table border=3 cellpadding=8>"+
		"<tr><td><b> Browser Executed		</b></td><td> "+ confDtls.getBrowser() + " </td></tr>" +
		"<tr><td><b> URL					</b></td><td> "+ confDtls.getScriptPath() + " </td></tr>" +
		"<tr><td><b> Total Cases Executed 	</b></td><td> "+ total  + " </td></tr>" +
		"<tr><td><b> Total Cases Passed		</b></td><td> "+ passed + " </td></tr>" +
		"<tr><td><b> Total Cases Failed		</b></td><td> "+  (total - passed)+ " </td></tr>" + "</table> ";
		
		Properties newprops = new Properties();
		newprops.put("mail.smtp.host", smtpHostName);
		newprops.setProperty("mail.port", port);		
		
		Session session = Session.getDefaultInstance(newprops, null);
		
		message = message.replace("&&Counters&&",countersText);
		
		try{
		
			msg = new MimeMessage(session);
		
			InternetAddress addressFrom = new InternetAddress(from);
			msg.setFrom(addressFrom);
		
			String[] recipients = recipient.split(";");
			InternetAddress[] addressTo =new InternetAddress[recipients.length];
			for (int i = 0; i < recipients.length; i++)
				addressTo[i] = new InternetAddress(recipients[i]);
		
		
			msg.setRecipients(Message.RecipientType.TO, addressTo);
			Date d = new SimpleDateFormat("MMddyy_HHmmss").parse(timeStamp);
				
			msg.setSubject(subject +" - "+ d.toString() );
		
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(message, "text/html");
		
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			
			messageBodyPart = new MimeBodyPart();
			DataSource excel= new FileDataSource(excelFile);
			messageBodyPart.setDataHandler(new DataHandler(excel));
			messageBodyPart.setFileName(excel.getName());
			multipart.addBodyPart(messageBodyPart);
			
			messageBodyPart = new MimeBodyPart();
			DataSource html= new FileDataSource(htmlFile);
			messageBodyPart.setDataHandler(new DataHandler(html));
			messageBodyPart.setFileName(html.getName());
			multipart.addBodyPart(messageBodyPart);
			
			if (createpdf){
				messageBodyPart = new MimeBodyPart();
				DataSource pdf= new FileDataSource(pdfFile);
				messageBodyPart.setDataHandler(new DataHandler(pdf));
				messageBodyPart.setFileName(pdf.getName());
				multipart.addBodyPart(messageBodyPart);
			}
				
			String testData = miscprops.getProperty("attachment1","$$");
			if(!testData.equalsIgnoreCase("$$")) {
				messageBodyPart = new MimeBodyPart();
				DataSource tData = new FileDataSource("..//TestInputs//"+testData);
				messageBodyPart.setDataHandler(new DataHandler(tData));
				messageBodyPart.setFileName(testData);
				multipart.addBodyPart(messageBodyPart);
			}
		
			String checklist = miscprops.getProperty("attachment2","$$");
		
			if(!checklist.equalsIgnoreCase("$$")) {
				messageBodyPart = new MimeBodyPart();		
				DataSource tChecklist = new FileDataSource("..//TestInputs//"+checklist);
				messageBodyPart.setDataHandler(new DataHandler(tChecklist));
				messageBodyPart.setFileName(checklist);
				multipart.addBodyPart(messageBodyPart);
			}
		
					
			msg.setSentDate(new Date());
			msg.setContent(multipart);
			System.out.println("Sending Email...");
			Transport.send(msg);
			System.out.println("Report E-mail Sent.");
			}catch(Exception e){
				log.info(message);
				log.info(message);
				System.out.println("Unable to send mail");
				System.out.println(e.getMessage());
			}
	}

	/**
	 * Method to create a directory
	 */
	
	public void createDir(String dirName,String errorMessage){
		File f = new File(dirName);
		try {
			if (!f.exists()) {
				f.mkdir();
				System.out.println("Directory Created :: " + errorMessage);
			}
		} catch (Throwable e) {
			System.out.println("Unable to create directory with " + errorMessage);
		}
	}
	
	/**
	 * Method to take screen shot
	 */
	
	public void takeScreenShot(){
		
		log.info(" Taking Screen Shot");
		
		String name = screenShotdir + confDtls.getAppName();
		createDir(name,"Application Name");
		
		name = name + "\\"  + (new SimpleDateFormat("MMddyy").format(d));
		createDir(name,"Date");
		
		name = name + "\\" + className;
		createDir(name,"Class Name");
		
		strScreenshotName = name + "\\" + current_method_exec + "_" +  sdf.format(new Date()) + ".png";
		File f = ((TakesScreenshot) webdriver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(f, new File(strScreenshotName));
			f.delete();
		} catch (IOException ioe) {
			log.error(" Unable to create Screenshot");
			log.error(ioe.getMessage());
			System.out.println(ioe.getMessage());
		}
	}
	
	public String getValue(String value) {
		
		String tempValue = value;
		
		if(tempValue.toLowerCase().startsWith("key:")) {
			
			tempValue = tempValue.substring(4);
			
			String index = "1";
			String val[] = tempValue.split("::"); 
			String mName = current_method_exec;
			tempValue = val[0];
		
			if(val.length == 2){
				mName = val[0];
				tempValue = val[1];
			}
		
			if(mName.startsWith("test_")){
				mName = mName.substring(5).toLowerCase();
			}
			if(tempValue.toUpperCase().endsWith("#RND")){

				tempValue = tempValue.substring(0,tempValue.length()-4);
				int n = 0;
				while(n==0)
					n = (int) ( Math.random() * setup_TestDataCounter.get(mName));
				index = n+"";
				
			} else if (tempValue.contains("#")) {
			
				int n = tempValue.lastIndexOf("#");
				index = tempValue.substring(n+1);
				tempValue = tempValue.substring(0,n);

			}
			
			if(setup_TestData.containsKey(mName.toLowerCase()+"#"+index)) {
				Properties props = setup_TestData.get(mName.toLowerCase()+"#"+index);
				tempValue = props.getProperty(tempValue, tempValue);
			}
		} else if(tempValue.toLowerCase().startsWith("rnd")) {
			
			int n = 5;
			
			if(tempValue.startsWith(":")){
				tempValue = tempValue.substring(1);
				n = tempValue.indexOf(":");
				if( n != -1){
					String temp = tempValue.substring(n+1);
					n = Integer.parseInt(tempValue.substring(0, n));
					tempValue = temp;
				}else{
					n = Integer.parseInt(tempValue);
					tempValue = "";
				}
			}
			
			for(int i=0;i<n;i++){
				String random = (int) (Math.random() * 9) + "";
				tempValue = tempValue + random;
			}
		} else if(tempValue.toLowerCase().startsWith("d:")) {
			
			String dateFormat = "MM/dd/yyyy";
			
			int index = value.indexOf(":format:");
			
			if(index!= -1){
				dateFormat = value.substring(index+8);
				value = value.substring(0,index);
			}
			
			System.out.println("Date Format : " + dateFormat);
			System.out.println("Date specified : " +value);
			
			String[] tempValues = value.split(":");
			
			String reqDate = "";

			DateFormat sdf = new SimpleDateFormat(dateFormat);
			Date today = new Date();

			Calendar cal = Calendar.getInstance();
			cal.setTime(today);

			if (tempValues[0].equalsIgnoreCase("currentdate")) {

				reqDate = sdf.format(today);
				System.out.println("Current Date  = " + reqDate);

			} else if (tempValues[0].equalsIgnoreCase("effectivedate")) {

				cal.set(Calendar.DAY_OF_MONTH, 1);

				reqDate = sdf.format((Date) cal.getTime());
				System.out.println("Effective Date = " + reqDate);

			} else if (tempValues[0].equalsIgnoreCase("monthend")) {

				cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));  
				reqDate = sdf.format((Date) cal.getTime());
				System.out.println("Month End Date = " + reqDate);
				
				if (tempValues.length == 3) {

					int changeby = Integer.parseInt(tempValues[2]);
					
					if (tempValues[1].equals("M"))
						cal.add(Calendar.MONTH, changeby);

					else if (tempValues[1].equals("d"))
						cal.add(Calendar.DATE, changeby);

					else if (tempValues[1].equals("y"))
						cal.add(Calendar.YEAR, changeby);

					cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));  
					reqDate = sdf.format(cal.getTime());
					System.out.println("Required date : " + reqDate);
					
				}
				
				hMap.put("strDate", reqDate);
				return reqDate;

			} 
			
			if (tempValues.length == 1)
				return reqDate;

			int changeby = Integer.parseInt(tempValues[2]);

			if (tempValues.length == 3) {

				if (tempValues[1].equals("M"))
					cal.add(Calendar.MONTH, changeby);

				else if (tempValues[1].equals("d"))
					cal.add(Calendar.DATE, changeby);

				else if (tempValues[1].equals("y"))
					cal.add(Calendar.YEAR, changeby);

				reqDate = sdf.format(cal.getTime());
				System.out.println("Required date : " + reqDate);
			}

			hMap.put("strDate", reqDate);
			return reqDate;

		}
		
		log.info("value = " + tempValue);
		System.out.println("value = " + tempValue);
		return tempValue;
		
	}
	
	public void sleep(int n){
		
		log.info(" Sleep for "+ n +" ms");
		
		try{
			Thread.sleep(n);
		}catch(Exception e){
			System.out.println(" Unable to perform wait");
		}
	}
	
	private void closeBrowser(String imageName) {

		log.info(" Closing Browser ");
		
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

}