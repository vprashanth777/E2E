package com.java.objects;

import java.util.ArrayList;

/**
 * This java class is written as a simple bean used to store the testConfiguration
 */

public class ConfigDetails {
	
	private ArrayList<Integer> testCasesToBeExecuted = new ArrayList<Integer>();
	private String scriptPath;
	private String testDataSource = "";
	private String browser = "";
	private String properties;
	private String testCases="";
	private String appName;
	private boolean isFuncDriven = true;
	private String prefix="";

	/**
	 * @return Return the list of testCase id's to be executed as an ArrayList 
	 */
	
	public ArrayList<Integer> getTestCasesToBeExecuted() {
		return testCasesToBeExecuted;
	}
	
	/**
	 * @param testCasesToBeExecuted Contains list testCase id's to be executed as an ArrayList 
	 */
	
	public void setTestCasesToBeExecuted(ArrayList<Integer> testCasesToBeExecuted) {
		this.testCasesToBeExecuted = testCasesToBeExecuted;
	}
	
	/**
	 * @return Return the URL on which tests are executed
	 */
	
	public String getScriptPath() {
		return scriptPath;
	}
	
	/**
	 * @param scriptPath Contains the URL on which tests are executed
	 */
	
	public void setScriptPath(String scriptPath) {
		this.scriptPath = scriptPath;
	}
	
	/**
	 * @return Returns the name of test data source. Can be database (or) XML File (or) Excel File
	 */
	
	public String getTestDataSource() {
		return testDataSource;
	}
	
	/**
	 * @param testDataSource Contains the name of test data source. Can be database (or) XML File (or) Excel File
	 */
	
	public void setTestDataSource(String testDataSource) {
		this.testDataSource = testDataSource;
	}
	
	/**
	 * @return Returns the browser on which the tests are executed
	 */
	
	public String getBrowser() {
		return browser;
	}
	
	/**
	 * @param browser Contains the name of the browser on which tests are executed
	 */
	
	public void setBrowser(String browser) {
		this.browser = browser;
	}
	
	/**
	 * @return Returns the name of the property File
	 */
	
	public String getProperties() {
		return properties;
	}
	
	/**
	 * @param properties Contains the name of the property File
	 */
	
	public void setProperties(String properties) {
		this.properties = properties;
	}
	
	/**
	 * @return Returns the name of the testcases to be executed in terms of TestPlanName (or) FeatureName (or) All  
	 */
	
	public String getTestCases() {
		return testCases;
	}
	
	/**
	 * @param testCases Contains the name of the testcases to be executed in terms of TestPlanName (or) FeatureName (or) All
	 */
	
	public void setTestCases(String testCases) {
		this.testCases = testCases;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppName() {
		return appName;
	}

	public void setFuncDriven(boolean isFuncDriven) {
		this.isFuncDriven = isFuncDriven;
	}

	public boolean isFuncDriven() {
		return isFuncDriven;
	}
	
	public void setPrefix(String prefix)
	{
		this.prefix=prefix;
	}
	public String getPrefix()
	{
		return prefix;
	}
	
}