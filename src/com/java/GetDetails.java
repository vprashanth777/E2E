package com.java;

import java.util.Properties;

import com.java.importnexport.ImportProperties;

public class GetDetails {

	
	SeleniumDriver sd;

	public GetDetails(SeleniumDriver sd) {
		this.sd = sd;
	}
	
	
	/**
	 * The Method which is used to read the required properties for Property File
	 */
	
	void getProperties() {

		String strPropsFile = sd.confDtls.getProperties();
			
		System.out.println("Reading properties from Property File :" + strPropsFile);
		sd.log.info("Reading properties from Property File :" + strPropsFile);
		
		ImportProperties xml = new ImportProperties(strPropsFile,sd.log);
		
		sd.log.info(" Reading the properties for App details ");
		Properties props = xml.setProperties("appDetails");
		sd.htmlTemplate.logo=props.getProperty("logo", "http://www.valuelabs.com/wp-content/themes/valuelabs/images/valuelabs-logo.gif");
		sd.miscProps.setProperty("appName", sd.confDtls.getAppName());
		sd.miscProps.putAll(props);
		
		sd.log.info(" Reading the common properties");
		props = xml.setProperties("common");
		sd.miscProps.putAll(props);

		sd.log.info(" Reading the misc properties");
		props = xml.setProperties("misc");
		sd.miscProps.putAll(props);
		
		//Code Updated By Sreenivas HR
		sd.log.info(" Reading the Application Android properties");
		props = xml.setProperties("android");
		sd.androidProps.putAll(props);
		
		sd.log.info(" Reading the Application Database properties");
		props = xml.setProperties("appDataDB");
		sd.miscProps.putAll(props);
		
		sd.miscProps.setProperty("execlog", "NOTSET");
		
		/*//Code Updated By Sreenivas HR
				sd.isUseMobile = Boolean.parseBoolean(sd.miscProps.getProperty("useAndroid","false"));
				sd.log.info(" Property :: isUseMobile -> " + sd.isUseMobile);*/		
		sd.miscProps.setProperty("prefix", sd.confDtls.getPrefix());
		
		sd.isGridEnabled = Boolean.parseBoolean(sd.miscProps.getProperty("useGrid","false"));
		sd.log.info(" Property :: isGridEnabled -> " + sd.isGridEnabled);
		
		sd.reportFailureAsBug = Boolean.parseBoolean(sd.miscProps.getProperty("reportFailureAsBug", "false"));
		sd.log.info(" Property :: reportFailureAsBug -> " + sd.reportFailureAsBug);
		
		sd.executeFailedCases = Boolean.parseBoolean(sd.miscProps.getProperty("executeFailedCases", "false"));
		sd.log.info(" Property :: executeFailedCases -> " + sd.executeFailedCases );
		
		sd.failedCaseExecutionLoopCount=Integer.parseInt(sd.miscProps.getProperty("noOfIterations", "1"));
		sd.log.info(" Property :: noOfIterations -> " + sd.failedCaseExecutionLoopCount );
		
		sd.sendMailReport = Boolean.parseBoolean(sd.miscProps.getProperty("sendMailReport", "false"));
		sd.log.info(" Property :: sendMailReport -> " + sd.sendMailReport);
		
		sd.updateResultsDB = Boolean.parseBoolean(sd.miscProps.getProperty("updateResultsDB","false"));
		sd.log.info(" Property :: updateResultsDB -> " + sd.updateResultsDB);
		
		sd.reportPriority = Boolean.parseBoolean(sd.miscProps.getProperty("reportPriority","false"));
		sd.log.info(" Property :: reportPriority -> " + sd.reportPriority);
		
		sd.reportReq = Boolean.parseBoolean(sd.miscProps.getProperty("reportReq","false"));
		sd.log.info(" Property :: reportReq -> " + sd.reportReq);
		
		sd.clearCookies = Boolean.parseBoolean(sd.miscProps.getProperty("clearCookies","true"));
		sd.log.info(" Property :: clearCookies -> " + sd.clearCookies);
		
		sd.closeBrowserSessions = Boolean.parseBoolean(sd.miscProps.getProperty("closeBrowserSessions","true"));
		sd.log.info(" Property :: closeBrowserSessions -> " +sd.closeBrowserSessions);
		
		sd.detailedLog = Boolean.parseBoolean(sd.miscProps.getProperty("detailedLogs","false"));
		sd.log.info(" Property :: detailedLog -> " + sd.detailedLog);
		
		sd.steplevelscreenshot = Boolean.parseBoolean(sd.miscProps.getProperty("steplevelscreenshot","false"));
		sd.log.info(" Property :: steplevelscreenshot -> " + sd.steplevelscreenshot);
		
		sd.createEvidence = Boolean.parseBoolean(sd.miscProps.getProperty("createEvidence","false"));
		sd.log.info(" Property :: createEvidence -> " + sd.createEvidence);
		
		//useObjectRepo = Boolean.parseBoolean(miscProps.getProperty("useObjectRepo","false"));
		sd.useObjectRepo = Boolean.parseBoolean(sd.miscProps.getProperty("useObjectRepo","true"));
		sd.log.info(" Property :: useObjectRepo -> " + sd.useObjectRepo);
		
		sd.screenShotdir = sd.miscProps.getProperty("screenShotdir",System.getProperty("user.dir")+"\\..\\ScreenShots\\");
		sd.log.info(" Property :: screenShotdir -> " + sd.screenShotdir);
		
		sd.createpdf = Boolean.parseBoolean(sd.miscProps.getProperty("createpdf","false"));
		sd.log.info(" Property :: createpdf -> " +sd.createpdf);
		
		sd.mergeReports = Boolean.parseBoolean(sd.miscProps.getProperty("mergeReports","false"));
		sd.log.info(" Property :: mergeReports -> " + sd.mergeReports);
		
		sd.log.info(" Property :: mergeReports -> " +sd.mergeReports);
		
	/*	if(sd.mergeReports){
			String timeStamp  = sd.hMap.get("TimeStamp");
			if(RunTest.multireport.containsKey(sd.confDtls.getAppName())){
				timeStamp  = timeStamp + ";" + RunTest.multireport.get(sd.confDtls.getAppName());
			}
			RunTest.multireport.put(sd.confDtls.getAppName(),timeStamp);
		}*/
		
		if(!sd.screenShotdir.endsWith("\\")){
			sd.screenShotdir = sd.screenShotdir  + "\\";
		}
		
		sd.log.info(" Screenshots directory is "+sd.screenShotdir);
		
		if( sd.createEvidence){
			sd.steplevelscreenshot = true;
		}
		
		if (sd.reportFailureAsBug) {
			sd.log.info(" Reading details of bugtracker tool ");
			props = xml.setProperties("BugTrackerDetails");
			sd.miscProps.putAll(props);
		}

		if (sd.isGridEnabled) {
			sd.log.info(" Reading details of selenium grid ");
			props = xml.setProperties("seleniumGrid");
			sd.miscProps.putAll(props);
		}

		if (sd.sendMailReport) {
			sd.log.info(" Reading details of email configuration ");
			props = xml.setProperties("email");
			sd.miscProps.putAll(props);
		}
		
		String dataSource = sd.confDtls.getTestDataSource();
		
		sd.log.info(" Data Source is " + dataSource);
		
		if (dataSource.equalsIgnoreCase("DB") || dataSource.equalsIgnoreCase("database")){
			sd.isDBUsed = true;
		}
		
		if(sd.updateResultsDB || sd.isDBUsed || sd.detailedLog){
			sd.log.info(" Reading the detils of testdata DB ");
			props = xml.setProperties("testdataDB");
			sd.miscProps.putAll(props);
		}
		
		if(sd.detailedLog)
			sd.updateResultsDB = true;
		
		if (sd.confDtls.getTestCases().toLowerCase().startsWith("tp:")) {
			sd.log.info(" Reading details of TCMTool ");
			sd.isTestLinkRequired = true;
			props = xml.setProperties("TCMTool");
			sd.miscProps.putAll(props);
		}
	}

	
	/**
	 * Method to get various properties from Config Object
	 */
	
	 void getConfigData() {

		String testCasesValue = sd.confDtls.getTestCases();

		if (sd.confDtls.getTestCasesToBeExecuted().size() == 0) {

			ReadTestCaseIDs readTCIDobj = new ReadTestCaseIDs(sd.miscProps,sd.log);
			String dataSource = sd.confDtls.getTestDataSource().toLowerCase();
			
			if (sd.isTestLinkRequired) {
				sd.miscProps.setProperty("projectTestPlanName",testCasesValue.substring(3));
				sd.testCases = readTCIDobj.readfromTestLink();
			}
			else if (sd.isDBUsed)
				sd.testCases = readTCIDobj.readfromDB(testCasesValue);
			else if (dataSource.contains(".xml"))
				sd.testCases = readTCIDobj.readfromXML(testCasesValue,dataSource,sd.confDtls.getPrefix());
			else 
				sd.testCases = readTCIDobj.readfromExcel(testCasesValue,dataSource,sd.confDtls.getPrefix());
			
			sd.confDtls.setTestCasesToBeExecuted(sd.testCases);
		}

		sd.testCases = sd.confDtls.getTestCasesToBeExecuted();

		sd.log.info(" TestCases to be executed " + sd.testCases);
		
		if(sd.testCases.size() == 0){
			sd.log.info(" Zero Test cases found to execute ");
			System.out.println("No Testcases found which are to be exceuted");
			RunTest.configrowCount++;
			Thread.currentThread().stop();
		}
		
		sd.log.info(" Application URL  :: "+ sd.appUrl);
		sd.appUrl = sd.confDtls.getScriptPath();
	
		sd.hMap.put("URL", sd.appUrl);
		System.out.println("url ===== " + sd.appUrl);
		
		sd.hMap.put("baseURL",sd.appUrl);
		
		try{
			int index = sd.appUrl.indexOf("//");
			String baseURL = sd.appUrl.substring(0,index+2)+ sd.appUrl.substring(index+2).split("/")[0];
			sd.hMap.put("baseURL",baseURL);
			sd.log.info(" Base URL for using $baseURL :: " + baseURL);
		}catch(Exception e){
			sd.log.warn(" Exception while trying to manipulate baseURL");
			sd.log.warn(e.getMessage());
			System.out.println(" Exception in manipulating the BaseURL");
			System.out.println(e.getMessage());
		}
		finally{
			System.out.println("BaseURL is :: "+ sd.hMap.get("baseURL"));
			sd.log.info(" Base URL :: " + sd.hMap.get("baseURL"));
		}
		sd.browserType = sd.confDtls.getBrowser().toUpperCase();
		sd.hMap.put("Browser", sd.browserType);
		
		sd.log.info(" Browser :: " + sd.browserType);
		System.out.println("Browser ===== " + sd.browserType);
	}

	
	
	
}
