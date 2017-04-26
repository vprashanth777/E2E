package com.java;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;



import com.thoughtworks.selenium.*;;

public class WebDriverUtils {

	public static By findElement(WebDriver driver, String field){
		
		By locator = null;
		
		if(field.startsWith("id=")){
			try{
				field = field.substring(3);
				driver.findElement(By.id(field));
				locator = By.id(field);
			}catch(Exception e){
				System.out.println(" Unable to find element having id " + field);
			}
			return locator;
		}
		else if(field.startsWith("name=")){
			try{
				field = field.substring(5);
				driver.findElement(By.name(field));
				locator = By.name(field);
			}catch(Exception e){
				System.out.println(" Unable to find element having name " + field);
			}
			return locator;
		}
		else if(field.startsWith("xpath=") || field.startsWith("//")){
			try{
				field = field.substring(5);
				driver.findElement(By.xpath(field));
				locator = By.xpath(field);
			}catch(Exception e){
				System.out.println(" Unable to find element having xpath " + field);
			}
			return locator;
		}
		return locator;
	}
/*
 *  this method makes the driver wait until the element is present
 */
	
	public static WebElement getWebElement(WebDriver webdriver,
			final By byLocator, int seconds) throws Exception {
		WebElement webElement = null;
		System.out.println("Waiting for element - " + byLocator.toString()
				+ " - to present.....");
		try {
			webElement = (new WebDriverWait(webdriver, seconds)).until(new ExpectedCondition<WebElement>()
			{
						public WebElement apply(WebDriver d) 
						{
							return d.findElement(byLocator);
						}
					});

		} catch (Exception e) {
			System.out.println("Timed-out waiting for element - "
					+ byLocator.toString() + " - to present.");
		}
		return webElement;

//		 try {
//				Thread.sleep(6000);
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//		return webElement;
}

	/*
	 * This method if for find the element based its property
	 */
	public static By locatorToByObj(WebDriver webdriver, String locator) {
		//Updated By Sreenivas HR
		//webdriver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		
		if (locator.startsWith("css="))
			locator = locator.substring(4, locator.length());

		if (locator.startsWith("class="))
			locator = locator.substring(6, locator.length());

		if (locator.startsWith("xpath=")) {
			locator = locator.substring(6, locator.length());
		}

		if (locator.startsWith("//")) {
			try {
				webdriver.findElement(By.xpath(locator));
				return By.xpath(locator);
			} catch (Throwable e) {}
		}

		try {
			webdriver.findElement(By.id(locator));
			return By.id(locator);
		} catch (Throwable e) {}

		try {
			webdriver.findElement(By.linkText(locator));
			return By.linkText(locator);
		} catch (Throwable e) {}

		try {
			webdriver.findElement(By.cssSelector(locator));
			return By.cssSelector(locator);
		} catch (Throwable e) {}

		try {
			webdriver.findElement(By.name(locator));
			return By.name(locator);
		} catch (Throwable e) {}

		try {
			webdriver.findElement(By.className(locator));
			return By.className(locator);
		} catch (Throwable e) {}

		try {
			webdriver.findElement(By.xpath("//a[contains(text(),'" + locator+ "')]")); // this is for objects without linkText. for eg: Home button
			return By.xpath("//a[contains(text(),'" + locator + "')]");
		} catch (Throwable e) {}

		try {
			webdriver.findElement(By.linkText(locator)); 
			return By.linkText(locator);
		} catch (Throwable e) {}


		try {
			webdriver.findElement(By.partialLinkText(locator)); 
			return By.partialLinkText(locator);
		} catch (Throwable e) {} 
		return null;
	}
/*
 *  waits for an element to be present and returns true if the element is
 * present, else returns false
 */
	public static boolean isElementPresent(WebDriver webdriver,
			final By byLocator, int seconds) throws Exception {

		if ((new WebDriverWait(webdriver, seconds))
				.until(new ExpectedCondition<WebElement>() {
					public WebElement apply(WebDriver d) {
						return d.findElement(byLocator);
					}
				}) == null)
			return false;
		else
			return true;
		
//		 try {
//				Thread.sleep(6000);
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//		 return false;
	}
/*
 *  returns true if the element is present, else returns false
 */
	
	public static boolean isElementPresent(WebDriver webdriver, String locator) {

		if ((locatorToByObj(webdriver, locator) != null) && (webdriver.findElement(locatorToByObj(webdriver, locator)).isDisplayed()))

			return true;
		else
			return false;

	}
/*
 *  Places mouse pointer over an element
 */
	
	public static void mouseOver(WebDriver webdriver, String locator)
			throws Exception {
		WebElement myElement = webdriver.findElement(locatorToByObj(webdriver,
				locator));
		Actions builder = new Actions(webdriver);
		builder.moveToElement(myElement).build().perform();
		Thread.sleep(3000);
	}

	/*
	 * Returns the message of the alert/confirmation box
	 * Note: Web Driver handles both alert and confirmation boxes in same way 
	 * 
	 */
	
	public static String getAlert(WebDriver webdriver) {
		String alertText;

		Alert alert = webdriver.switchTo().alert();
		// Get the text from the alert/confirmation
		alertText = alert.getText();

		return alertText;
	}

	/*
	 *  Returns the selected label of a combo box/list
	 */
	public static String getSelectedLabel(WebDriver webdriver, String locator) {
		Select select = new Select(webdriver.findElement(locatorToByObj(
				webdriver, locator)));
		return select.getFirstSelectedOption().getText();
	}

	/*
	 *  Returns the selected options of a combo box/list
	 */
	public static String[] getSelectedOptions(WebDriver webdriver,String locator) {
		
		Select select = new Select(webdriver.findElement(locatorToByObj(webdriver, locator)));
		
		List<WebElement> selectedOptions = select.getAllSelectedOptions();

		String [] selectOptions = new String[selectedOptions.size()];
		for (int i = 0; i < selectOptions.length; i++)
			selectOptions[i] = selectedOptions.get(i).getText();

		return selectOptions;
	}
	
	public static String[] getAvailableOptions(WebDriver webdriver,String locator) {
		
		Select select = new Select(webdriver.findElement(locatorToByObj(webdriver, locator)));
		
		List<WebElement> selectedOptions = select.getOptions();

		String [] selectOptions = new String[selectedOptions.size()];
		for (int i = 0; i < selectOptions.length; i++)
			selectOptions[i] = selectedOptions.get(i).getText();

		return selectOptions;
	}

	/*
	 *  Selects the given option based on visible text/label -- also works as
	 *   addSelection( )
	 */
	
	public static void select(WebDriver webdriver, String locator, String option) {
		Select select = new Select(webdriver.findElement(locatorToByObj(
				webdriver, locator)));
		select.selectByVisibleText(option);

	}

	// Selects the given option based on its index -- also works as
	// addSelection( )
	public static void selectByIndex(WebDriver webdriver, String locator,
			int option) {
		Select select = new Select(webdriver.findElement(locatorToByObj(
				webdriver, locator)));
		select.selectByIndex(option);
	}

	// Returns the value of an element
	public static String getValue(WebDriver webdriver, String locator) {
		return webdriver.findElement(locatorToByObj(webdriver, locator))
				.getAttribute("value");
	}

	// Returns the text content of a specific cell
	public static String getTable(WebDriver webdriver, String tableLocator,
			String rowNum, String colNum) {
		WebElement table = webdriver.findElement(locatorToByObj(webdriver,
				tableLocator));
		java.util.List<WebElement> tr_collection = table.findElements(By
				.xpath("//tr"));
		int row_num = 1;

		if (!tr_collection.isEmpty() && tr_collection.size() >= 1)
			for (WebElement trElement : tr_collection) {
				java.util.List<WebElement> td_collection = trElement
						.findElements(By.xpath("//tr[" + row_num + "]/td"));
				int col_num = 1;
				if (!td_collection.isEmpty() && td_collection.size() >= 1)
					for (WebElement tdElement : td_collection) {
						if ((Integer.toString(row_num).equalsIgnoreCase(rowNum))
								&& (Integer.toString(col_num)
										.equalsIgnoreCase(colNum)))
							return tdElement.getText();
						col_num++;
					}
				row_num++;
			}
		return null;
	}

	// To wait for the element to be present
	public static void waitForElementToPresent(WebDriver webdriver,
			final By byLocator, int seconds) throws Exception {

		if ((new WebDriverWait(webdriver, seconds))
				.until(new ExpectedCondition<WebElement>() {
					public WebElement apply(WebDriver d) {
						return d.findElement(byLocator);
					}
				}) == null)
			throw new Exception("Timed-out waiting for - "
					+ byLocator.toString() + " - element to present...");

//		 try {
//				Thread.sleep(6000);
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}

	}

	// Switches the focus to the window specified
	public static boolean selectWindow(WebDriver webdriver, String windowTitle) {
		WebDriver popup = null;
		Set<String> windowHandles = webdriver.getWindowHandles();
		for (int i = 0; i < windowHandles.size(); i++) {
			popup = webdriver.switchTo().window(
					windowHandles.toArray()[i].toString());
			/*String title=popup.getTitle().toString();
		
			
			title=title.replaceAll("[^a-zA-Z0-9]", "");

			windowTitle=windowTitle.replaceAll("[^a-zA-Z0-9]", "");*/
			
			if (popup.getTitle().toString().matches(windowTitle)||popup.getTitle().toString().replaceAll("[^a-zA-Z0-9]", "").matches(windowTitle.replaceAll("[^a-zA-Z0-9]", "")))
				return true;
		}
		return false;
	}

	// Waits for the page to load completely
	// timeOut should be in milliseconds
	public static void waitForPageToLoad(final WebDriver webdriver, String timeOut) {
		try {
			/*
			 * if (timeOut == "") timeOut = "20000";
			 */
		/*	Selenium sel = new WebDriverBackedSelenium(webdriver,
					webdriver.getCurrentUrl());
			sel.waitForPageToLoad("20000");*/
			ExpectedCondition<Boolean> expectation = new
					ExpectedCondition<Boolean>() {
					        public Boolean apply(WebDriver driver) {
					          return (((JavascriptExecutor) webdriver).executeScript("return document.readyState").equals("complete"));
					        }
					      };
					  
					   //  Wait<WebDriver> wait = new WebDriverWait(webdriver,30);
				
					      try {
					  
					              //wait.until(expectation);
					    	  Thread.sleep(6000);
					          
					      } catch(Throwable error) {
					          System.out.println("Page still loaing");    
					      }

		} catch (Exception e) {
			System.out.println("exception waitforpageload : " + e.getMessage());
		}

	}

}
