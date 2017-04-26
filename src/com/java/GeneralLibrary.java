package com.java;

import java.util.HashMap;

import org.openqa.selenium.WebDriver;

public class GeneralLibrary {

	protected WebDriver webdriver;
	private String result ="";
	private boolean isPassed = true;
	private FunctionalDriver fdriver;
	public HashMap<String,String> hMap = new HashMap<String,String>();
	
	public void setFunctionalDriver(FunctionalDriver fdriver){
		this.fdriver = fdriver;
	}
	
	public void setWebDriver(WebDriver webdriver) {
		this.webdriver = webdriver;
	}
	
	public WebDriver getWebDriver() {
		return webdriver;
	}

	public String getResult(){
		return result;
	}
	
	public void fail(String msg){
		result = msg;
		isPassed = false;
	}
	
	public void pass(String msg){
		result = msg;
		isPassed = true;
	}

	public boolean isPassed(){
		return isPassed;
	}
	
	public String getValue(String value) {
		return fdriver.getValue(value);
	}
	
	public void sleep(int n){
		try{
			Thread.sleep(n);
		}catch(Exception e){
			System.out.println(" Unable to perform wait");
		}
	}

	public void takescreenshot(){
		fdriver.takeScreenShot();
	}
	
	public void onFail(){
		System.out.println(" No actions are defined to execute on test failure ");
	}
}

