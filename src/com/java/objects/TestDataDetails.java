package com.java.objects;

import java.io.UnsupportedEncodingException;

/**
 * This is to store each row details form Test Data Details Excel Sheet
 */
public class TestDataDetails {
	
	private String testCaseID;
	private String comments;
	private int testDataID;
	private String workingPage;
	private String dataFields;
	private String dataValues;
	private String actionType;
	private String testCaseTitle;
	private String browserType;
	private String fieldName;
	private String conditionType;
	
	private String defaults(String value,String defaultValue){
		if(value == null){
			value = defaultValue;
		}
		return value;
	}
	
	public String getTestCaseTitle() {
		return testCaseTitle;
	}
	
	public void setTestCaseTitle(String testCaseTitle) {
		this.testCaseTitle = testCaseTitle;
	}
	
	public String getDataFields() {
		return dataFields;
	}
	
	public void setDataFields(String dataFields) {
		this.dataFields = defaults(dataFields, "");
	}
	
	public String getDataValues() {
		return dataValues;
	}

	public void setDataValues(String dataValues)  {
	
		this.dataValues = defaults(dataValues,"");
		
	}
	
	public String getTestCaseID() {
		return testCaseID;
	}

	public void setTestCaseID(String testCaseID) {
		this.testCaseID = testCaseID;
	}

	public int getTestDataID() {
		return testDataID;
	}

	public void setTestDataID(int testDataID) {
		this.testDataID = testDataID;
	}

	public String getWorkingPage() {
		return workingPage;
	}

	public void setWorkingPage(String workingPage) {
		this.workingPage = defaults(workingPage,"Working Page");
	}
	
	public void setCondition(String conditionType) {
		this.conditionType = defaults(conditionType,"");
	}
	
	public String getCondition() {
		return conditionType;
	}
	
	public String getActionType() {
		return actionType;
	}
	
	public void setActionType(String actionType) {
		if(actionType != null)
			actionType = actionType.toUpperCase();
		this.actionType = actionType;
	}
	
	public String getBrowserType() {
		return browserType;
	}

	public void setBrowserType(String browserType) {
		this.browserType = defaults(browserType,"COMMON");
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = defaults(fieldName,"");
	}

	public void setComments(String comments) {
		this.comments = defaults(comments,"");
	}

	public String getComments() {
		return comments;
	}
}
