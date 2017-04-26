package com.java;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import testlink.api.java.client.*;

/**
 *  Class to report the result of executed cases to test link
 */

public class ReportResultToTestlink {
	
	public String buildName;
	public TestLinkAPIClient result;
	public SeleniumDriver driver;
	public String DEVKEY;
	public String URL;
	public Object projectName, projectTestPlanName;
	
	/**
	 *  Constructor to read the test link properties and create the TestLink APi Object
	 */
	
	public ReportResultToTestlink(SeleniumDriver driver) {
		try{
			this.driver = driver;
			DEVKEY = driver.miscProps.getProperty("APIKEY");
			URL = driver.miscProps.getProperty("tcm_URL");
			projectName = driver.miscProps.getProperty("projectName");
			projectTestPlanName = driver.miscProps.getProperty("projectTestPlanName");
			result = new TestLinkAPIClient(DEVKEY,URL);
		}catch(Exception e){
			System.out.println("Unable to create the result object of testlink. \n Can't communicate with test link for this thread");
			driver.isTestLinkRequired = false;
		}
	}
	
	public void createBuild(){
		
		driver.log.info(" Creating Build for Test Link ");
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyy_HHmmss");	
	
		buildName = projectTestPlanName+dateFormat.format(cal.getTime());
		
		driver.log.info(" Build Name :: " + buildName);
		
		try {
			result.createBuild(projectName.toString(), projectTestPlanName.toString(), buildName, "Automation_Build");
		} catch (Exception e) {
			
			driver.log.error(" Exception while creating Build ");
			driver.log.error(e.getMessage());
			System.out.println("Unable to create build for testlink. \n Can't communicate with testlink for this thread");
			driver.isTestLinkRequired = false;
		}
	}
	
	/**
	 *  Method to write the test results to the TestLink 
	 */
	
	public void writeTestResult(List<String> testResult)  {
		
		System.out.println("Writing the result to testlink");
		driver.log.info(" Writing the result to testlink ");
		
		try {
			if (testResult.get(3).equalsIgnoreCase("Pass")) {
				System.out.println("Marking the test case " + testResult.get(0)
						+ ":" + testResult.get(2) + " as passed in round: "
						+ driver.roundOfExecution);
				result.reportTestCaseResult(
						projectName.toString(),
						projectTestPlanName.toString(),
						testResult.get(0) + ":" + testResult.get(2), buildName, null,
						TestLinkAPIResults.TEST_PASSED);
			} else {
				System.out.println("Marking the test case " + testResult.get(0)
						+ ":" + testResult.get(2) + " as failed in round: "
						+ driver.roundOfExecution);
				result.reportTestCaseResult(
						projectName.toString(),
						projectTestPlanName.toString(),
						testResult.get(0) + ":" + testResult.get(2), buildName, null,
						TestLinkAPIResults.TEST_FAILED);
			}
			System.out.println("Completed Writing the result to testlink successfully");
		}
		catch (Exception e) {
			
			driver.log.error(" Failed to mark status in TestLink ");
			driver.log.error(e.getMessage());
			System.out.println(" Failed to mark status in TestLink");
			System.out.println(e.getMessage());
		}
	
	}

}