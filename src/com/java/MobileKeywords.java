package com.java;


import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.MultiTouchAction;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidKeyCode;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.touch.TouchActions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.java.TestType.AndroidKeyEvents;
import com.java.objects.ResultDetails;

public class MobileKeywords {



	TestType tt;

	public MobileKeywords(TestType tt) {
		this.tt = tt;
	}

	private ResultDetails resultDetails = new ResultDetails();



	/**
	 * Method to click on Go Key In Keyboard of Android
	 * @return 
	 * @return 
	 */

	public ResultDetails androidKey(WebDriver webdriver,String value) {
		try {
			tt.driver.log.info(" clicking on Go Key In Keyboard of Android ");
			//resultDetails.setComment("Deleteing cookies & loading the URL  :: "	+ driver.appUrl);
			String keyval = tt.driver.utils.getValue(value);
			HashMap<String, Integer> keycodes = new HashMap<String, Integer>();
			keycodes.put("keycode", Integer.parseInt(keyval));
			((JavascriptExecutor)webdriver).executeScript("mobile: keyevent", keycodes);
			webdriver.manage().timeouts().implicitlyWait(120, TimeUnit.SECONDS);
			resultDetails.setFlag(true);
		} catch (Exception e) {
			tt.driver.log.error(" Encountered Exception while performing clickGoKey in Android  ");
			tt.driver.log.error(e.getMessage());
			resultDetails.setErrorMessage("Unable to click on Go Key in the Android Keyboard");
		}
		return resultDetails;
	}

	/**
	 * Method to Clear Cache of the native application and launch the Application
	 * @return 
	 */

	public ResultDetails clearCache() {
		try {
			tt.driver.log.info(" Deleteing Cache,data and relaunching the NativeAPP ");
			//resultDetails.setComment("Deleteing cookies & loading the URL  :: "	+ driver.appUrl);
			Runtime rt = Runtime.getRuntime();
			//androidProps.getProperty("MobileAppPackageName")
			//com.webmd.android
			Process proc = rt.exec("adb shell pm clear"+"app_pac");
			resultDetails.setFlag(true);
			return resultDetails;
		} catch (Exception e) {
			tt.driver.log.error(" Encountered Exception while performing clear Cache ");
			tt.driver.log.error(e.getMessage());
			resultDetails.setErrorMessage("Unable to clear cache & Relaunch the APP");
			return resultDetails;
		}
	}

	/**
	 * Method to Swipe the native application pages
	 * @return 
	 */
	public ResultDetails androidSwipe(WebDriver webdriver,String value) {
		try {
			//	(((AppiumDriver)((RemoteWebDriver)webdriver))).swipe(100, 500, 100, 100, 2);

			//	Thread.sleep(10000);
			tt.driver.log.info("Performing Swipe in the NativeAPP Pages");
			int j = Integer.parseInt(value);
			for(int i=0;i<=j;i++)
			{
				//System.out.println();
				JavascriptExecutor js = (JavascriptExecutor) webdriver;
				HashMap<String, Double> swipeObject = new HashMap<String, Double>();
				swipeObject.put("startX", 0.05);
				swipeObject.put("startY", 0.55);
				swipeObject.put("endX", 0.05);
				swipeObject.put("endY", 0.25);
				swipeObject.put("duration", 1.8);
				js.executeScript("mobile: swipe", swipeObject);
				/*	  (((AppiumDriver)((RemoteWebDriver)webdriver))).swipe(400, 800, 400, 200, 1000);
				 */

			}
			resultDetails.setFlag(true);
			return resultDetails;
		}
		catch (Exception e) {
			tt.driver.log.error(" Encountered Exception while performing Swipe ");
			tt.driver.log.error(e.getMessage());
			resultDetails.setErrorMessage("Unable to perform Swipe Pages of the APP");
			return resultDetails;
		}
	}



	/**
	 * Method to Wait For Element of native application 
	 * @return 
	 */
	public ResultDetails androidWaitForElement(WebDriver webdriver,String field) {
		try {

			tt.driver.log.info(" Waiting for the Object to load in NativeAPP");
			//locator = WebDriverUtils.locatorToByObj(webdriver,getFieldFromRepo(tempField));
			//int a = Integer.parseInt(webdriver.findElement(WebDriverUtils.locatorToByObj(webdriver, field)).getSize());
			while(webdriver.findElements(WebDriverUtils.locatorToByObj(webdriver, field)).size()!=0)
			{
				Thread.sleep(5000);
			}
			resultDetails.setFlag(true);
			return resultDetails;
		}
		catch (Exception e) {
			tt.driver.log.error(" Encountered Exception while Waitng for Element to load ");
			tt.driver.log.error(e.getMessage());
			resultDetails.setErrorMessage("Unable to perform Waitng for Element to load in the APP");
			return resultDetails;
		}
	}


	/**
	 * Method to Scroll the native application pages
	 * @return 
	 */
	public ResultDetails androidScroll(String scrollTo) {
		try {

			tt.driver.log.info("Performing Scroll in the NativeAPP");
			/*JavascriptExecutor js = (JavascriptExecutor) webdriver;
			HashMap<String, Double> flickObject = new HashMap<String, Double>();
			WebElement TadnC = (new WebDriverWait(webdriver, 10))
					.until(ExpectedConditions.presenceOfElementLocated(By.tagName("android.widget.ScrollView")));
			//scroll down
			js = (JavascriptExecutor) webdriver;
			flickObject = new HashMap<String, Double>();
			flickObject.put("endX", (double) 0);
			flickObject.put("endY", (double) 0);
			flickObject.put("touchCount", (double) 2);
			js.executeScript("mobile: flick", flickObject);
*/
			tt.driver.appiumDriver.scrollTo(scrollTo);
			
				
			resultDetails.setFlag(true);
			return resultDetails;
		}
		catch (Exception e) {
			tt.driver.log.error(" Encountered Exception while performing Scroll ");
			tt.driver.log.error(e.getMessage());
			resultDetails.setErrorMessage("Unable to perform Scroll in the APP");
			return resultDetails;
		}
	}
	/**
	 * 
	 * @param webdriver
	 * @return
	 */
	public ResultDetails closeApp(WebDriver webdriver)

	{
		try{
			System.out.println("Closing the NativeApp");
			tt.driver.log.info("Closing the NativeApp");
			((AppiumDriver)((RemoteWebDriver)webdriver)).closeApp();
			resultDetails.setFlag(true);
			return resultDetails;
		}catch (Exception e) {
			System.out.println("Unable to close Native App");
			System.out.println(e.getMessage());
			tt.driver.log.error("Unable to close Native App");
			tt.driver.log.error(e.getMessage());
			return resultDetails;
		}

	}
	/**
	 * 
	 * @param webdriver
	 * @return
	 */
	public ResultDetails resetApp(WebDriver webdriver)
	{
		try{
			System.out.println("Resetting the Native App");
			tt.driver.log.info("Resetting the Native App");
			((AppiumDriver)((RemoteWebDriver)webdriver)).resetApp();
			resultDetails.setFlag(true);
			return resultDetails;
		}catch (Exception e) {
			System.out.println("Unable to reset the Native App");
			System.out.println(e.getMessage());
			tt.driver.log.error("Unable to reset the Native App");
			tt.driver.log.error(e.getMessage());
			return resultDetails;
		}
	}

	/**
	 * Method to remove the native app
	 * @param webdriver
	 * @param value (application package name)
	 */
	public ResultDetails removeApp(WebDriver webdriver,String value)
	{//value app package name
		try{
			System.out.println("Removing the Native App");
			tt.driver.log.info("Removing the Native App");
			((AppiumDriver)((RemoteWebDriver)webdriver)).removeApp(value);
			resultDetails.setFlag(true);
			return resultDetails;
		}catch (Exception e) {
			System.out.println("Unable to remove Native App");
			System.out.println(e.getMessage());
			tt.driver.log.error("Unable to remove Native App");
			tt.driver.log.error(e.getMessage());
			return resultDetails;
		}
	}


	/**
	 * 
	 * @param webdriver
	 * @return
	 */
	public ResultDetails launchApp(WebDriver webdriver)
	{
		try{
			System.out.println("Launching the Native App");
			tt.driver.log.info("Launching the Native App");
			((AppiumDriver)((RemoteWebDriver)webdriver)).launchApp();
			resultDetails.setFlag(true);
			return resultDetails;
		}catch (Exception e) {
			System.out.println("Unable to Launch Native App");
			System.out.println(e.getMessage());
			tt.driver.log.error("Unable to Launching Native App");
			tt.driver.log.error(e.getMessage());
			return resultDetails;
		}
	}

	/**To perform Android KeyEvents
	 * @param WebDriver
	 * @param value for specifying KeyEvent details
	 * 
	 */
	/*public ResultDetails androidKeyEvent(WebDriver webdriver,String value)
	{
		try{
			System.out.println("Performing Android KeyEvent:"+value);
			tt.driver.log.info("Performing Android KeyEvent:"+value);
			AndroidKeyEvents events= AndroidKeyEvents.valueOf(value.toUpperCase());

			switch(events)
			{
			case HOME:
				((AppiumDriver)((RemoteWebDriver)webdriver)).sendKeyEvent(AndroidKeyCode.HOME);
				break;
			case ENTER:
				((AppiumDriver)((RemoteWebDriver)webdriver)).sendKeyEvent(AndroidKeyCode.ENTER);
				break;
			case MENU:
				((AppiumDriver)((RemoteWebDriver)webdriver)).sendKeyEvent(AndroidKeyCode.MENU);
				break;
			case DEL:
				((AppiumDriver)((RemoteWebDriver)webdriver)).sendKeyEvent(AndroidKeyCode.DEL);
				break;	
			case BACK:
				((AppiumDriver)((RemoteWebDriver)webdriver)).sendKeyEvent(AndroidKeyCode.BACK);
				break;
			case BACKSPACE:
				((AppiumDriver)((RemoteWebDriver)webdriver)).sendKeyEvent(AndroidKeyCode.BACKSPACE);
				break;
			case SPACE:
				((AppiumDriver)((RemoteWebDriver)webdriver)).sendKeyEvent(AndroidKeyCode.SPACE);
				break;
			case SETTINGS:
				((AppiumDriver)((RemoteWebDriver)webdriver)).sendKeyEvent(AndroidKeyCode.SETTINGS);
				break;
			default:
				((AppiumDriver)((RemoteWebDriver)webdriver)).sendKeyEvent(Integer.parseInt(value));
			}
			resultDetails.setFlag(true);
			return resultDetails;
		}catch (Exception e) {
			System.out.println("Unable to Perform Android KeyEvent:"+value);
			System.out.println(e.getMessage());
			tt.driver.log.error("Unable to Perform Android KeyEvent:"+value);
			tt.driver.log.error(e.getMessage());
			return resultDetails;
		}
	}*/

	
	/**
	 * 
	 * @param webdriver
	 * @return
	 */
	public ResultDetails switchToWebView(WebDriver webdriver)
	{
		try{

			System.out.println("Switching to WebView");
			tt.driver.log.info("Switching to WebView");
			boolean webViewFound = false;
			Set<String> contextNames = ((AppiumDriver)((RemoteWebDriver)webdriver)).getContextHandles();
			for (String contextName : contextNames) {
				System.out.println(contextName);
				if (contextName.contains("WEBVIEW")){
					webViewFound=true;
					((AppiumDriver)((RemoteWebDriver)webdriver)).context(contextName);
				}
			}
			if(!webViewFound)
			{
				System.out.println("Could not found WebView in the page");
				System.out.println("Found: "+contextNames+" in the page");
				tt.driver.log.error("Could not found WebView in the page");
				tt.driver.log.error("Found: "+contextNames+" in the page");
				return resultDetails;
			}
			resultDetails.setFlag(true);
			return resultDetails;
		}catch(Exception e)
		{
			System.out.println("Unable to Switch to WebView");
			System.out.println(e.getMessage());
			tt.driver.log.info("Unable to Switch to WebView");
			tt.driver.log.error(e.getMessage());
			return resultDetails;
		}
	}

	/**
	 * To Perform lock screen operation 
	 * @param webdriver
	 * @param value
	 * @return
	 */
	public ResultDetails lock(WebDriver webdriver, String value)
	{
		try{
			System.out.println("Performing screen lock for: "+value+" seconds");
			tt.driver.log.info("Performing screen lock for: "+value+" seconds");
			((AppiumDriver)((RemoteWebDriver)webdriver)).lockScreen(Integer.parseInt(value));
			resultDetails.setFlag(true);
			return resultDetails;

		}catch (Exception e) {
			System.out.println("Unable to lock screen");
			System.out.println(e.getMessage());
			tt.driver.log.error("Unable to lock screen");
			tt.driver.log.error(e.getMessage());
			return resultDetails;
		}
	}

	/**
	 * 
	 * @param webdriver
	 * @param value 
	 * @return
	 */
	public ResultDetails installApp(WebDriver webdriver, String value)
	{
		try{

			System.out.println("Installing "+value+" application");
			tt.driver.log.info("Installing "+value+" application");

			((AppiumDriver)((RemoteWebDriver)webdriver)).installApp(value);
			resultDetails.setFlag(true);
			return resultDetails;
		}catch (Exception e) {
			System.out.println("Unable to installApp");
			System.out.println(e.getMessage());
			tt.driver.log.error("Unable to installApp");
			tt.driver.log.error(e.getMessage());
			return resultDetails;
		}
	}

	/**
	 * 
	 * @param webdriver
	 * @param value
	 * @return
	 */
	public ResultDetails isAppInstalled(WebDriver webdriver, String value)
	{
		try{

			boolean isAppInstalled=((AppiumDriver)((RemoteWebDriver)webdriver)).isAppInstalled(value);
			if(isAppInstalled)
				resultDetails.setFlag(true);
			return resultDetails;
		}
		catch(Exception e)
		{
			System.out.println("Unable to perform isAppInstalled");
			System.out.println(e.getMessage());
			tt.driver.log.error("Unable to installApp");
			tt.driver.log.error(e.getMessage());
			return resultDetails;
		}
	}
	
	public ResultDetails tap(WebDriver webdriver, String fieldText)
	{
		try{
			//String [] tempValues = value.split(",");
			//int start = Integer.parseInt(tempValues[0]);
			//int stop = Integer.parseInt(tempValues[1]);
			
			WebElement element= webdriver.findElement(WebDriverUtils.locatorToByObj(webdriver, fieldText));
            //MultiTouchAction multiTouch = new MultiTouchAction((MobileDriver) webdriver);*/
			
			TouchAction touchAction = new TouchAction((MobileDriver) webdriver);
			touchAction.tap(element).perform();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return resultDetails;
	}
	
	public ResultDetails longPress(WebDriver webdriver, String fieldText)
	{
		try{
			//String [] tempValues = value.split(",");
			//int start = Integer.parseInt(tempValues[0]);
			//int stop = Integer.parseInt(tempValues[1]);
			
			WebElement element= webdriver.findElement(WebDriverUtils.locatorToByObj(webdriver, fieldText));
            //MultiTouchAction multiTouch = new MultiTouchAction((MobileDriver) webdriver);*/
			
			TouchAction touchAction = new TouchAction((MobileDriver) webdriver);
			touchAction.tap(element).perform();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return resultDetails;
	}
	
	public ResultDetails keyboardWrite(WebDriver webdriver, String fieldText,String value)
	{try{
		JavascriptExecutor jsExecutor = (JavascriptExecutor) webdriver;
		webdriver.findElement(WebDriverUtils.locatorToByObj(webdriver, fieldText)).clear();
		String script = "var vKeyboard = target.frontMostApp().keyboard();"+"vKeyboard.setInterKeyDelay(0.1);"+ "vKeyboard.typeString(\"" + value + "\");";
		jsExecutor.executeScript(script);
		resultDetails.setFlag(true);
		return resultDetails;
	}
	catch(Exception e)
	{
		e.printStackTrace();
		resultDetails.setFlag(false);
		return resultDetails;
	}
	
	}




}



