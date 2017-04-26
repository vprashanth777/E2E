package com.java;

import java.awt.Robot;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.java.objects.ResultDetails;

public class AppTestType {

	Robot robot;
	String folderStructure = System.getProperty("user.dir");
	String downloadFilepath = folderStructure + "/../Reports";
	String autoItfilespath = folderStructure + "/../Framework";
	static int auto = 0;
	File file;

	public enum AppKeyWords {
       
		JCLICK,		
		VERIFYPRODUCT,
		VERIFYPRICE
		
	
	
		


	};

	TestType tt;

	public AppTestType(TestType tt) {
		this.tt = tt;
	}

	Properties properties;
	public ResultDetails resultDetails = new ResultDetails();

	public ResultDetails performAction(WebDriver webdriver, String fieldText,
			String value, String action, String fieldName) {

		try {

			AppKeyWords keys = AppKeyWords.valueOf(action.toUpperCase());

			switch (keys) {

			case JCLICK:

				Jclick(webdriver,fieldText);
				
				resultDetails.setFlag(true);
				break;
				
			
			
				
			case VERIFYPRODUCT:
				verifyproduct(webdriver, fieldText, value, action, fieldName);
				break;	
				
            case VERIFYPRICE:
				verifyprice(webdriver, fieldText, value, action, fieldName);
				break;
			}
		} catch (IllegalArgumentException e) {
			resultDetails.setErrorMessage(" Unable to find keyword " + action
					+ "in AppTestType");
			System.out.println(e.getMessage());
		} catch (Exception e) {
			resultDetails.setErrorMessage(" Exception in executing the action "
					+ action);
			System.out.println(e.getMessage());
		}
		return resultDetails;
	}
	private void Jclick(WebDriver webdriver, String fieldText) {
		// TODO Auto-generated method stub
		String field = fieldText.substring(3, fieldText.length());
		try{
			WebElement ele = webdriver.findElement(By.xpath(field));
			JavascriptExecutor js = (JavascriptExecutor) webdriver;
			js.executeScript("arguments[0].click();", ele);
			resultDetails.setFlag(true);
	
		}
		catch(Exception e)
		{
			e.printStackTrace();
			resultDetails.setFlag(false);
		
		}
				
	}

	
	
	
	

		
         private void verifyproduct(WebDriver webdriver, String fieldText, String value, String action, String fieldName) {
		

		
		try{
			  
			  String productname=tt.driver.hMap.get(value);
			  System.out.println(productname);
              String product;
			  int n=productname.length();
		      String prodname=productname.substring(0,n-4 );
			  System.out.println(prodname);
		      product= webdriver.findElement(By.xpath("//h1[text()='VR One Plus Virtual Reality Glasses']")).getText();
			  if(product.contains(prodname))
	 	        {
	 		      System.out.println("product Specification page is opened");
				  resultDetails.setFlag(true);
	             }
	 	         else
	 	        {
	 		      System.out.println("product Specification page was not opened");
	 		      resultDetails.setFlag(false);
	 	        }
			
		}catch(Exception e){
			e.printStackTrace();
			resultDetails.setFlag(false);
		}
	}

         private void verifyprice(WebDriver webdriver, String fieldText, String value, String action, String fieldName) {
		

		
		try{
			String quantitynum=value;
			String singlecost= webdriver.findElement(By.id("catalog_price")).getText(); 
	        int a=singlecost.length();
	        float quantnum=Float.parseFloat(quantitynum);
		    String singcost=singlecost.substring(1,a);
		    float scost = Float.parseFloat(singcost); 
		    String multiplecost= webdriver.findElement(By.xpath("//td[@class='cart-box-item-total col-sm-2 col-xs-3']")).getText();
		    int b=multiplecost.length();
		    String mulcost=multiplecost.substring(1,b);
		    float mcost = Float.parseFloat(mulcost);
		    float totalcost=scost*quantnum;
			System.out.println(totalcost);
			System.out.println(mcost);
		    if(totalcost==mcost)
		     {
			System.out.println("price has been updated");
			resultDetails.setFlag(true);
		     }
		    else
		     {
			 System.out.println("price has not been updated");
			 resultDetails.setFlag(false);
		     }
			  
			  
			
		}catch(Exception e){
			e.printStackTrace();
			resultDetails.setFlag(false);
		}
	}	
	
}
	