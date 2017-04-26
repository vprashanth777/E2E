package com.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

//import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.java.objects.ResultDetails;
import com.java.objects.TestDataDetails;

public class TestType {

	public SeleniumDriver driver;
	public TestDataDetails tdd;
	private ResultDetails resultDetails = new ResultDetails();

	public TestType(SeleniumDriver driver) {
		this.driver = driver;
	}
	TableKeywords tblKeywords=new TableKeywords(this);
	DBKeywords dbKeywords=new DBKeywords(this);
	MobileKeywords mbKeywords= new MobileKeywords(this);
	RestCalls rstCalls= new RestCalls(this);

	public enum SelectFields {
		SLB, RDB, COB, FRM
	};

	public enum CheckFields {
		CHK
	};

	public enum EnterFields {
		TXT, BTN, EDT
	};

	public enum WindowFields {
		TTL
	};

	public enum ClickFields {
		LNK, BTN, CNF, ALT, IMG, XPH ,JSC
	};

	public enum WaitForFields {
		IMG, TTL, BTN, LNK, COB, MSG, TXT, XPH
	};

	public enum StoreFields {
		TXT, COB, XPH, LNK, BTN, TBL, DBV, STV
	};

	public enum DataFields {
		TTL, TXT, XPH, RDB, CHK, COB, SLB, BTN, IMG, LNK, CNF, MSG, GET, TBL, ALT, CBS
	};

	public enum OrderBy {
		ASC,DSC
	};
	public enum AndroidKeyEvents{
		HOME,ENTER,MENU,DEL,BACKSPACE,SETTINGS,SPACE,BACK
	}
	public enum ActionTypes {

		CHECK, CLEARENTER, CLEARSESSION, CLICK, CLICKANDWAIT, CLOSEWINDOW, ENTER, CLEAR ,
		GOBACK, ISDISABLED, ISENABLED, KEYPRESS, MOUSEOVER, OPENURL, SELECT, SELECTANDWAIT,
		SELECTWINDOW, STOREATTRIBUTE, STOREVALUE, UNCHECK, VERIFY, VERIFYCONTINUE, VERIFYATTRIBUTE,
		VERIFYNOTPRESENT, WAITFORELEMENT,DRAGANDDROP,REFRESH,VERIFYOBJECT, 

		EXECUTETESTCASE, STORECOMMENTS, WAITTIME,EXPORTTOEXCEL,

		COLUMNDATACHECK, VERIFYSORT, VERIFYTABLEDATA, VERIFYTABLEROWEXIST, VERIFYVALUEINROW,

		DB_UPDATE, DB_VERIFYCOLUMNS, DB_VERIFYDUPLICATEVALUESONPRIMARYKEY, DB_VERIFYNOTNULLCONSTRAINT,
		DB_VERIFYPRIMARYKEYS, DB_VERIFYROWSCOUNT, DB_VERIFYSPEXECUTIONTIME, DB_VERIFYSTOREDPROCEDURE, DB_VERIFYTABLECONTENT,

		ANDROIDKEY,CLEARCACHE,ANDROIDSWIPE,ANDROIDWAITFOR,ANDROIDSCROLL,TAP,KEYBOARDWRITE,ANDROIDKEYEVENT,

		RESTPOST, RESTDELETE, RESTPUT, RESTGET,POSTUSERSESSION,POSTSEARCHCOMBINEDPRODUCTS,GETCOMBINEDPRODUCTRESULT,PUTCROSSSELLHOTELACTIVATION,PUTFLIGHTSELECTION,PUTSELECTHOTELROOM,POSTSELECTPACKAGE, 
	};

	/**
	 * Method to call the specified action based on the parameter
	 */

	public ResultDetails performAction(WebDriver webdriver, String fieldText,String value, String actionType, String fieldName,TestDataDetails result) {

		try {

			driver.log.info(" Executing " + actionType.toUpperCase());

			ActionTypes actTypes = ActionTypes.valueOf(actionType.toUpperCase());

			switch (actTypes) {

			case EXPORTTOEXCEL:
				exportToExcel(webdriver,result);
				break;

			case CLEARSESSION:
				clearSession(webdriver);
				break;

			case WAITTIME:
				waitTime(value);
				break;

			case OPENURL:
				openURL(webdriver, fieldText);
				break;

			case WAITFORELEMENT:
				waitForElement(webdriver, fieldText, value, fieldName);
				break;

			case VERIFY:
				verify(webdriver, fieldText, value, fieldName);
				break;

			case VERIFYNOTPRESENT:
				verifynotpresent(webdriver, fieldText, value, fieldName);
				break;

			case VERIFYCONTINUE:

				verifyContinue(webdriver, fieldText, value, fieldName);
				break;

			case SELECT:
				select(webdriver, fieldText, value, fieldName);
				sleep(1000);
				break;

			case SELECTANDWAIT:
				select(webdriver, fieldText, value, fieldName);
				WebDriverUtils.waitForPageToLoad(webdriver, "20000");
				break;

			case CLICK:
				click(webdriver, fieldText, fieldName);
				sleep(1000);
				break;

			case CLICKANDWAIT:
				click(webdriver, fieldText, fieldName);
				WebDriverUtils.waitForPageToLoad(webdriver, value);
				break;

			case CHECK:

				checkOrUncheck(webdriver, fieldText, value, fieldName, true);
				break;

			case UNCHECK:
				checkOrUncheck(webdriver, fieldText, value, fieldName, false);
				break;

			case ENTER:
				enter(webdriver, fieldText, value, fieldName);
				break;

			case CLEARENTER:
				clearEnter(webdriver, fieldText, value, fieldName);
				break;
			case CLEAR:
				clear(webdriver,fieldText,value,fieldName);
				break;

			case MOUSEOVER:
				mouseOver(webdriver, fieldText, fieldName);
				break;

			case SELECTWINDOW:

				selectWindow(webdriver, fieldText, value);
				break;

			case CLOSEWINDOW:

				closeWindow(webdriver, fieldText, value);
				break;

			case GOBACK:
				goBack(webdriver);
				break;

			case ISDISABLED:
				//
				isEnabledorDisabled(webdriver, fieldText, fieldName, false);
				break;

			case ISENABLED:
				isEnabledorDisabled(webdriver, fieldText, fieldName, true);
				break;

			case STOREVALUE:
				storeValue(webdriver, fieldText, value, fieldName);
				break;

			case STORECOMMENTS:
				storeComments(webdriver, fieldText, value, fieldName);
				break;

			case KEYPRESS:
				keypress(webdriver, fieldText, value, fieldName);
				break;

			case STOREATTRIBUTE:
				storeAttribute(webdriver, fieldText, value, fieldName);
				break;

			case VERIFYATTRIBUTE:
				verifyAttribute(webdriver, fieldText, value, fieldName);
				break;

			case EXECUTETESTCASE:
				executetestcase(webdriver, fieldText, value);
				break;

			case DRAGANDDROP:
				dragAndDrop(webdriver, fieldText, fieldName);
				break;
			case REFRESH:
				try{
					webdriver.navigate().refresh();
					Thread.sleep(2000);
					resultDetails.setFlag(true);
				}catch(Exception e){
					resultDetails.setFlag(false);
					resultDetails.setErrorMessage("Unable to refresh the page");
					resultDetails.setErrorMessage(e.getMessage());
					driver.log.error("Unable to refresh the page");
					driver.log.error(e.getMessage());
					System.out.println("Unable to refresh the page");
				}

				break;
			case VERIFYOBJECT:
				verifyObject(webdriver,fieldText,value,fieldName);
				break;

			case COLUMNDATACHECK:
				resultDetails=tblKeywords.columnDataCheck(driver,webdriver, fieldText, value, fieldName);
				break;

			case VERIFYTABLEROWEXIST:
				resultDetails=tblKeywords.verifyTableRowExists(driver,webdriver, fieldText, value, fieldName);
				break;

			case VERIFYVALUEINROW:
				resultDetails=tblKeywords.verifyValueInRow(driver,webdriver,fieldText,value,fieldName);
				break;

			case VERIFYTABLEDATA:
				resultDetails=tblKeywords.verifyTableData(driver,webdriver,fieldText,value,fieldName);
				break;

			case VERIFYSORT:
				resultDetails=tblKeywords.verifySort(driver,webdriver, fieldText, value, fieldName);
				break;

			case DB_UPDATE:
				resultDetails=dbKeywords.updateDB(driver, fieldText);
				break;

			case DB_VERIFYCOLUMNS:
				resultDetails=dbKeywords.verifyColumns(driver,fieldText, value);
				break;

			case DB_VERIFYDUPLICATEVALUESONPRIMARYKEY:
				resultDetails=dbKeywords.verifyduplicateValuesonPrimarykeys( driver,value, fieldName);
				break;

			case DB_VERIFYNOTNULLCONSTRAINT:
				resultDetails=dbKeywords.verifyNotNullConstraints(driver,fieldText, value, fieldName);
				break;

			case DB_VERIFYPRIMARYKEYS:
				resultDetails=dbKeywords.verifyPrimaryKeys(driver,fieldText, value, fieldName);
				break;

			case DB_VERIFYROWSCOUNT:
				resultDetails=dbKeywords.verifyRowCount(driver,fieldText, value);
				break;

			case DB_VERIFYSPEXECUTIONTIME:
				resultDetails=dbKeywords.verifySPExecTime(driver,fieldText, value);
				break;

			case DB_VERIFYSTOREDPROCEDURE:
				resultDetails=dbKeywords.verifyStoredProcedure(driver,fieldText, value);
				break;
			case DB_VERIFYTABLECONTENT :
				resultDetails=dbKeywords.verifytablecontent(driver,webdriver,fieldText, value,fieldName);
				break;

			case ANDROIDKEY:
				resultDetails=mbKeywords.androidKey(webdriver,value);
				break;
			/*case ANDROIDKEYEVENT:
				resultDetails=mbKeywords.androidKeyEvent(webdriver, value);
				break;*/
			case CLEARCACHE:
				resultDetails=mbKeywords.clearCache();
				break;
			case ANDROIDSWIPE:
				resultDetails=mbKeywords.androidSwipe(webdriver,value);
				break;
			case ANDROIDSCROLL:
				resultDetails=mbKeywords.androidScroll(fieldText);
				break;
			case ANDROIDWAITFOR:
				resultDetails=mbKeywords.androidWaitForElement(webdriver,value);
				break;
			case TAP:
				resultDetails=mbKeywords.tap(webdriver, fieldText);
				break;
			case KEYBOARDWRITE:
				resultDetails=mbKeywords.keyboardWrite(webdriver, fieldText, value);
				break;

			case RESTGET:
				resultDetails=rstCalls.restGet(webdriver,fieldText,value,fieldName);

				//restGet(webdriver,fieldText,value,fieldName);
				//resultDetails=rstCalls.getRest(fieldText,value);
				break;

			case RESTPOST:
				resultDetails = rstCalls.restPost(webdriver, fieldText, value, fieldName);
				break;

			case RESTPUT:
				resultDetails = rstCalls.restPut(webdriver, fieldText, value, fieldName);
				break;

			case RESTDELETE:
				resultDetails = rstCalls.restDelete(webdriver, fieldText, value, fieldName);
				break;
			}
		} catch (IllegalArgumentException e) {
			try {
				//e.printStackTrace();
				driver.log.info(" Fetching from user defined keywords ");
				AppTestType apptt = new AppTestType(this);

				resultDetails = apptt.performAction(webdriver, fieldText,value, actionType, fieldName);
			} catch (Exception sub) {
				driver.log.error(" Exception while trying to execute user defined keyword ");
				driver.log.error(sub.getMessage());
				resultDetails.setErrorMessage(" Exception " + sub.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			driver.log.error(" Encountered Exception while trying to execute ");
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(" Exception " + e.getMessage());
		}

		return resultDetails;
	}

	@SuppressWarnings("deprecation")
    public ResultDetails exportToExcel(WebDriver webdriver,TestDataDetails tDetails) {
           int Row_Num=0;
           String PNR = "";
           String NumOfMiles = "";
           String Taxes = "";
           String NumOfPersons = ""; 
           String CartID = "";
           WebDriverWait wait = new WebDriverWait(webdriver, 60);
           wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@class='confirm-number']")));
           
//           try {
//			Thread.sleep(6000);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}

           WebElement element;
           //PNR number
           element = webdriver.findElement(By.xpath("//span[@class='confirm-number']"));
           PNR  = element.getText();

           //Number of Miles
           element = webdriver.findElement(By.xpath("//table[@class='purchase-summary']//tbody[@class='line-item-row containter-total']/tr[1]/td[@class='price']"));
           NumOfMiles  = element.getText();

           //Taxes
           element = webdriver.findElement(By.xpath("//table[@class='purchase-summary']//tbody[@class='line-item-row containter-total']/tr[2]/td[@class='price']"));
           Taxes  = element.getText();

           //Number of persons
           
           try{
                  int numOfTravPlusTaxeInfo = webdriver.findElements(By.xpath("//div[@class='details-container purchase-summary-wrapper']/table[1]/tbody[1]//table[@class='purchase-summary']/tbody[1]/tr")).size();
                  System.out.println("numOfTravPlusTaxeInfo: "+numOfTravPlusTaxeInfo);
                  int TaxeInfo = webdriver.findElements(By.xpath("//div[@class='details-container purchase-summary-wrapper']/table[1]/tbody[1]//table[@class='purchase-summary']/tbody[1]/tr/td/a")).size();
                  System.out.println("TaxeInfo: "+TaxeInfo);
                  int numOfTravels = numOfTravPlusTaxeInfo - TaxeInfo;
                  System.out.println("numOfTravels: "+numOfTravels);
                  
//                NumOfPersons = "";
                  String IndivTravelDtl="";
                  for(int i=1;i<=numOfTravels;i++)
                  {
                        IndivTravelDtl = webdriver.findElement(By.xpath("//div[@class='details-container purchase-summary-wrapper']/table[1]/tbody[1]//table[@class='purchase-summary']/tbody[1]/tr["+i+"]/td")).getText();
                        
                        System.out.println("IndivTravelDtl: "+IndivTravelDtl);
                        
                        if(i!=numOfTravels)
                               NumOfPersons = NumOfPersons + IndivTravelDtl + " &";
                        else
                               NumOfPersons = NumOfPersons + IndivTravelDtl;
                  }
                  
                  System.out.println("NumOfPersons: "+NumOfPersons);
                  
           }
           catch(Exception e)
           {
                  
           }
           
//         element = webdriver.findElement(By.xpath("//table[@class='purchase-summary']//td[@class='traveler']"));
//         NumOfPersons  = element.getText();

           //CartID
           element = webdriver.findElement(By.xpath("//a[@id='change-language-pos']"));
           CartID  = element.getAttribute("data-cartid");


      

           try
           {
                   //Get the number of rows that are already filled
                  //                     export_ResultExcell(Row_Num,PNR,Origin,Destination,Date) ;
        		Row_Num = driver.sheet.getPhysicalNumberOfRows();
        		
        		if(Row_Num==0){
        	   
	        	   HSSFRow rowhead= driver.sheet.createRow((short)0);
	               rowhead.createCell((short) 0).setCellValue("TestCaseID");
	               rowhead.createCell((short) 1).setCellValue("TestCaseTitle");
	               rowhead.createCell((short) 2).setCellValue("PNR");
	               rowhead.createCell((short) 3).setCellValue("NumOfMiles");
	               rowhead.createCell((short) 4).setCellValue("Taxes");
	               rowhead.createCell((short) 5).setCellValue("TraverllerInfo");
	               rowhead.createCell((short) 6).setCellValue("CartID");
        		}
        	   
        		else{
        			
        			//HSSFRow row = sheet.createRow((short)Row_Num);
                  HSSFRow row= driver.sheet.createRow((short)Row_Num);
                  
                  TestDataDetails test = new TestDataDetails();

                  //row.createCell((short) 0).setCellValue(testCaseID);
                  row.createCell((short) 0).setCellValue(tDetails.getTestCaseID());
                  row.createCell((short) 1).setCellValue(tDetails.getTestCaseTitle());
                  row.createCell((short) 2).setCellValue(PNR);
                  row.createCell((short) 3).setCellValue(NumOfMiles);
                  row.createCell((short) 4).setCellValue(Taxes);
                  row.createCell((short) 5).setCellValue(NumOfPersons);
                  row.createCell((short) 6).setCellValue(CartID);
        		}
                  FileOutputStream fileOut = new FileOutputStream(driver.PNRFileName);
                  driver.hwb.write(fileOut);
                  fileOut.close();
                  resultDetails.setFlag(true);
           }
           catch(Exception e)
           {
                  e.printStackTrace();
                  resultDetails.setFlag(false);
           }
           return resultDetails;

    }

	public ResultDetails restGet(WebDriver webdriver, String fieldText,
			String value, String fieldName) {
		// TODO Auto-generated method stub



		try
		{
			DefaultHttpClient httpClient = new DefaultHttpClient();
			//System.out.println(fieldText.split("\\|\\|")[0]);
			String[] TempField = fieldText.split("\\|\\|");
			String [] TempValue = value.split("\\|\\|");
			String ContentType = TempField[0];
			String URL = TempField[1];
			//String Message = TempField[2];
			String CookieInfo = TempField[2];

			String StatusCode = TempValue[0];
			String resposeBody = TempValue[1];
			String GetResponse="";

			Set<Cookie> allCookies = webdriver.manage().getCookies();
			//System.out.println(allCookies);

			Iterator<Cookie> iterator = allCookies.iterator();
			HttpGet getRequest = new HttpGet(URL);
			//StringEntity input = new StringEntity(Message);
			//input.setContentType(ContentType);
			//getRequest.setEntity(input);


			if(CookieInfo.equalsIgnoreCase("ALL") ){
				String cookies="";
				while(iterator.hasNext())
				{

					Cookie cookie1 =iterator.next();
					cookies=cookies+cookie1.getName()+"="+cookie1.getValue()+";";

				}
				getRequest.addHeader("Cookie",cookies);
			}
			HttpResponse response = httpClient.execute(getRequest);

			if (! Integer.toString(response.getStatusLine().getStatusCode()).equals(StatusCode.trim()) )
			{
				driver.log.error("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());	
				resultDetails.setFlag(false);
				resultDetails.setErrorMessage("Status code not matched Expected:"+StatusCode+" Actual:"+response.getStatusLine().getStatusCode());
				return resultDetails;
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			String output;
			//String PostResponse="";
			System.out.println("Output from Server:");
			driver.log.info("Output from Server:");
			while ((output = br.readLine()) != null) {
				//System.out.println(output);
				driver.log.info(output);
				GetResponse=GetResponse+output;

			}
			if(GetResponse.trim().equalsIgnoreCase(resposeBody.trim())||GetResponse.trim().toLowerCase().contains(resposeBody.trim().toLowerCase()))
			{
				System.out.println("Verified");
				resultDetails.setFlag(true);

			}
			else{
				resultDetails.setFlag(false);
				/*resultDetails.setErrorMessage("response not matched Expected:"+resposeBody+" Actual:"+PostResponse);
				driver.log.error("response not matched Expected:"+resposeBody+" Actual:"+PostResponse);*/
			}

			httpClient.getConnectionManager().shutdown();


		}

		catch (MalformedURLException e) {

			resultDetails.getErrorMessage();
			/*resultDetails.setErrorMessage("response not matched Expected:"+resposeBody+" Actual:"+GetResponse);
			driver.log.error("response not matched Expected:"+resposeBody+" Actual:"+GetResponse);*/

		} catch (IOException e) {

			resultDetails.getErrorMessage();
			/*resultDetails.setErrorMessage("response not matched Expected:"+resposeBody+" Actual:"+GetResponse);
				driver.log.error("response not matched Expected:"+resposeBody+" Actual:"+GetResponse);*/

		}
		return resultDetails;



	}

	/**
	 * Method to verify element presence in the page
	 */
	private void verifyObject(WebDriver webdriver, String fieldText,
			String value, String fieldName) {

		if(fieldText.length() < 3 ){
			driver.log.error(" DataFields are not in expected format ");
			String warn = " DataFields are not in expected format ";
			resultDetails.setErrorMessage(warn);
			System.out.println(warn);
			return;
		}

		String fieldType = fieldText.substring(0, 3);

		value=driver.utils.getValue(value);

		try {

			DataFields sdf = DataFields.valueOf(fieldType.toUpperCase());

			switch (sdf) {

			case BTN:
			case LNK:
			case TXT:

				By locator = WebDriverUtils.locatorToByObj(webdriver, value);
				if(locator==null)

				{
					resultDetails.setErrorMessage(value+" Element not found);" );
					driver.log.error(" " +value+" Element not found");
					System.out.println(" "+value+"Element not found");
					return;

				}
				boolean res = (WebDriverUtils.isElementPresent(webdriver,value));
				if (res)
					resultDetails.setFlag(true);
				else {
					driver.log.error(" Element " + fieldName	+ " not found");
					resultDetails.setErrorMessage(" Element " + fieldName	+ " not found");
					return;
				}
				break;

			}
		}catch(Exception e)
		{
			resultDetails.setFlag(false);
			resultDetails.setErrorMessage("Unable to find the element:"+e.getMessage() );
			driver.log.error("Unable to find the element:"+e.getMessage() );
			System.out.println("Unable to find the element:"+e.getMessage() );
		}



	}

	/**
	 * Method to select a value from Dropdown
	 */

	public void select(WebDriver webdriver, String fieldText, String value,	String fieldName) {

		if(fieldText.length() < 3 ){
			driver.log.error(" DataFields are not in expected format ");
			String warn = " DataFields are not in expected format ";
			resultDetails.setErrorMessage(warn);
			System.out.println(warn);
			return;
		}

		String fieldType = fieldText.substring(0, 3);
		String field = fieldText.substring(3);

		field = getFieldFromRepo(field);

		if (!fieldType.equalsIgnoreCase("FRM") && field.equalsIgnoreCase("")) {
			driver.log.error(" The DataFied is empty after specifying it's type ");
			System.out.println(" The DataFied is empty after specifying it's type ");
			resultDetails.setErrorMessage(" The DataFied is empty after specifying it's type ");
			return;
		}

		if (!value.equalsIgnoreCase("RND")) {
			value = driver.utils.getValue(value);
		}

		if (!fieldType.equalsIgnoreCase("RDB") && value.equalsIgnoreCase("")) {
			driver.log.error(" DataValue specified is empty ");
			System.out.println(" DataValue specified is empty ");
			resultDetails.setErrorMessage(" DataValue specified is empty ");
			return;
		}

		if (fieldName.equalsIgnoreCase(""))
			fieldName = field;

		By locator = WebDriverUtils.locatorToByObj(webdriver, field);

		if (!fieldType.equalsIgnoreCase("FRM") && locator == null) {
			driver.log.error("Element  " + fieldName + " is not present");
			resultDetails.setErrorMessage("Element  " + fieldName + " is not present");
			return;
		}

		try {

			SelectFields sdf = SelectFields.valueOf(fieldType.toUpperCase());

			switch (sdf) {

			case SLB:
			case COB:

				resultDetails.setComment(" Selecting " + value + " in " + fieldName);

				int index = -1;

				Select select = null;

				try {
					select = new Select(webdriver.findElement(locator));
				} catch (Exception e) {
					driver.log.error(" Exception in creating select object for "+ fieldName);
					System.out.println(" Encountered Exception in creating select object for "+ fieldName);
					resultDetails.setErrorMessage(" Exception in creating select object for "+ fieldName);
					System.out.println(e.getCause().getMessage());
					return;
				}

				if (value.toLowerCase().startsWith("js:")){

					value = value.substring(3);

					try { 
						driver.log.info(" Selecting Dropdown value " + value + " using Java Script ");
						String ln = "\n";
						field = webdriver.findElement(locator).getAttribute("id");
						System.out.println("Field ID : "+field); 

						String javascript = " javascript:var s = document.getElementById('"+field+"');"	+ ln +
								"for (i = 0; i< s.options.length; i++){" + ln + 
								" if (s.options[i].text.trim().toUpperCase() == '"+value.toUpperCase()+"'){" +  ln +
								" s.options[i].selected = true;" +   ln +
								" s.click();" +  ln +
								" if (s.onchange) {" +  ln +
								" s.onchange();" + ln +
								" }" +  ln +
								" break;" +  ln +
								" }" +   ln +
								"}"; 
						System.out.println("Java Script : "+ javascript); 
						((JavascriptExecutor) webdriver).executeScript(javascript);
						resultDetails.setFlag(true);
						sleep(2000);
						return;
					} catch(Exception e) { 
						driver.log.error(" Encountered Exception while selecting dropdown value using javascript");
						resultDetails.setErrorMessage("Exception while selecting dropdown value using javascript");
						System.out.println("Exception occured in select : "+e.getMessage());
						return;
					}
				}

				if (value.equalsIgnoreCase("RND")) {
					driver.log.info(" Selecting a RANDOM Value from dropdown ");
					int size = select.getOptions().size();
					while (index <= 1)
						index = (int) (Math.random() * size);
				} else if (value.toLowerCase().startsWith("index=")) {
					index = Integer.parseInt(value.substring(6));
					driver.log.info(" Selecting value with index " + index + " from dropdown ");
				}

				select = new Select(webdriver.findElement(locator));

				if (index != -1)
					select.selectByIndex(index);
				else {
					if (value.startsWith("value=")) {
						value = value.substring(6);
						select.selectByValue(value);
						driver.log.info(" Selecting value " +value + " from dropdown ");
					} else {
						select.selectByVisibleText(value);
						driver.log.info(" Selecting " + value + " from dropdown ");
					}
				}
				resultDetails.setFlag(true);
				break;

			case RDB:
				driver.log.info(" Selecting the radio button "+ fieldName);
				resultDetails.setComment(" Selecting radio button " + fieldName);
				locator = WebDriverUtils.locatorToByObj(webdriver, field);
				webdriver.findElement(locator).click();
				resultDetails.setFlag(true);
				break;

			case FRM:
				driver.log.info(" Selecting frame " + fieldName);
				resultDetails.setComment(" Selecting frame  " + fieldName);
				selectFrame(webdriver, value, fieldName);
				break;
			}
		} catch (IllegalArgumentException e) {
			driver.log.error(" For Select datafield must be COB, SLB, RDB, FRM");
			driver.log.error(e.getMessage());
			System.out.println(e.getMessage());
			resultDetails.setErrorMessage(" For Select datafields must be of type :: COB, SLB, RDB, FRM ");
		} catch (Exception e) {
			driver.log.error(" Encountered Exception while executing select on " + fieldName);
			driver.log.error(e.getMessage());
			System.out.println(e.getMessage());
			resultDetails.setErrorMessage("Exception while executing select on "+ fieldName);
		}
	}

	/**
	 * Method to type or enter into a DataField
	 */

	public void enter(WebDriver webdriver, String fieldText, String value, String fieldName) {

		if(fieldText.length() < 4 ) {
			driver.log.error(" Datafield are not in expected format ");
			resultDetails.setErrorMessage(" DataFields are not in expected format ");
			System.out.println(" DataFields are not in expected format ");
			return;
		}

		String fieldType = fieldText.substring(0, 3);
		String field = fieldText.substring(3);
		field = getFieldFromRepo(field);

		if(field.equals("")) {
			driver.log.error(" Locator specified returned empty value ");
			resultDetails.setErrorMessage(" Locator specified returned empty value ");
			System.out.println(" Locator specified returned empty value ");
			return;
		}

		value = driver.utils.getValue(value);
		if(value.equals("")) {
			driver.log.error(" Value specified returned empty value ");
			resultDetails.setErrorMessage(" value specified returned empty value ");
			System.out.println(" value specified returned empty value ");
			return;
		}

		if (fieldName.equalsIgnoreCase(""))
			fieldName = field;

		driver.log.info(" Typing into "+ fieldName+ " : " + value);
		resultDetails.setComment("Typing into " + fieldName + " : " + value);

		By locator = WebDriverUtils.locatorToByObj(webdriver, field);

		if (locator == null) {
			driver.log.error(" Element "+ fieldName + " not found");
			resultDetails.setErrorMessage(" Element " + fieldName + " not found ");
			return;
		}

		try {
			EnterFields edf = EnterFields.valueOf(fieldType.toUpperCase());

			switch (edf) {

			case TXT:
				driver.log.info(" Typing into text field ");
				webdriver.findElement(locator).sendKeys(value);
				resultDetails.setFlag(true);
				break;

			case BTN:
				driver.log.info(" Typing into button ");
				webdriver.findElement(locator).sendKeys(value);
				resultDetails.setFlag(true);
				break;

			case EDT:
				driver.log.info(" Typing into edit box ");
				webdriver.findElement(locator).click();
				webdriver.findElement(locator).sendKeys(value);
				resultDetails.setFlag(true);
				break;
			}
		} catch (IllegalArgumentException e) {
			driver.log.error(" For ENTER , data field must be TXT, BTN or EDT ");
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage("For ENTER action type the data field should be TXT, EDT, BTN ");
		} catch (Exception e) {
			driver.log.error(" Encountered Exception while executing enter on "+ fieldName);
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(" Error in executing ENTER on "	+ fieldName);
		}
	}

	/**
	 * Method to delete cookies and load the Application URL
	 */

	public void clearSession(WebDriver webdriver) {
		try {
			driver.log.info(" Deleteing cookies and reloading the URL ");
			resultDetails.setComment("Deleteing cookies & loading the URL  :: "	+ driver.appUrl);
			webdriver.manage().deleteAllCookies();
			webdriver.get(driver.appUrl);
			webdriver.manage().deleteAllCookies();
			webdriver.get(driver.appUrl);
			resultDetails.setFlag(true);
		} catch (Exception e) {
			driver.log.error(" Encountered Exception while performing clear session  ");
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage("Unable to delete cookies & Reload the page");
		}
	}

	/**
	 * Method to wait for given amount of time
	 */

	public void waitTime(String value) {

		driver.log.info(" Sleeping for specified amount of time ");
		String sleepval = driver.utils.getValue(value);
		int sleeptime = 20000;
		try {
			sleeptime = Integer.parseInt(sleepval);
		} catch (Exception e) {

		}
		driver.sleepcounter = driver.sleepcounter + sleeptime;
		sleep(sleeptime);
		driver.log.info(" Waited for "+ sleeptime + " milliseconds ");
		resultDetails.setComment("Sleeping for " + sleeptime + " ms");
		resultDetails.setFlag(true);
	}

	/**
	 * Method to open the desired URL
	 */

	public void openURL(WebDriver webdriver, String URL) {

		URL = driver.utils.getValue(URL);

		if (URL.contains("$baseURL"))
			URL = URL.replace("$baseURL", driver.hMap.get("baseURL"));

		if(URL.equalsIgnoreCase("")){
			driver.log.error (" Datafield is empty ");
			System.out.println(" Datafield is empty ");
			resultDetails.setErrorMessage(" DataField is empty ");
			return;
		}

		System.out.println("Open URL :: " + URL);
		driver.log.info(" Navigating to url " + URL);
		resultDetails.setComment("Opening the url: " + URL);

		try {
			webdriver.get(URL);
			resultDetails.setFlag(true);
		} catch (Exception e) {
			driver.log.error( "Encountered Exception when trying to open URL");
			driver.log.error(e.getMessage());
			System.out.println("Encountered Exception when trying to open URL \n " + e.getMessage());
			resultDetails.setErrorMessage("Encountered Exception when trying to open URL :: " + URL);
		}
		WebDriverUtils.waitForPageToLoad(webdriver, "50000");
	}

	/**
	 * Method to check or uncheck
	 */

	private void checkOrUncheck(WebDriver webdriver, String fieldText,String value, String fieldName, boolean check) {

		driver.log.info(" Performing Check / Uncheck Operation ");
		if(fieldText.length() < 4){
			String warn = " The locator must be specified as CHK locator ";
			System.out.println(warn);
			driver.log.error(warn);
			resultDetails.setErrorMessage(warn);
			return;
		}

		String fieldType = fieldText.substring(0, 3);

		String field = fieldText.substring(3);

		field = getFieldFromRepo(field);

		if(field.equals("")){
			String warn = " The locator value is empty ";
			System.out.println(warn);
			driver.log.error(warn);
			resultDetails.setErrorMessage(warn);
			return;
		}

		if (fieldName.equalsIgnoreCase(""))
			fieldName = field;

		resultDetails.setComment(" Clicking on Combo Box " + fieldName);
		driver.log.info(" Clicking on Combo Box " + fieldName);
		By locator = WebDriverUtils.locatorToByObj(webdriver, field);

		if (locator == null) {
			driver.log.error(" Unable to locate element :: "	+ fieldName);
			resultDetails.setErrorMessage(" Unable to locate element :: "	+ fieldName);
			return;
		}

		try {

			CheckFields cdf = CheckFields.valueOf(fieldType.toUpperCase());

			switch (cdf) {

			case CHK:

				boolean isChecked = webdriver.findElement(locator).isSelected();

				if (isChecked != check) {	
					webdriver.findElement(locator).click();
				}

				isChecked = webdriver.findElement(locator).isSelected();

				if (isChecked != check) {

					driver.log.info(" Using JavaScript to perform check / uncheck operation ");
					String id = webdriver.findElement(locator).getAttribute("id");

					String Script = "javascript:document.getElementById('"+id+"').click();";

					System.out.println("Java Script : "+ Script); 
					driver.log.info(" Java Script : "+ Script);

					((JavascriptExecutor) webdriver).executeScript(Script);

				}
				resultDetails.setFlag(true);

				break;
			}
		} catch (IllegalArgumentException e) {
			driver.log.error(" For check/uncheck action type the data field should be CHK ");
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(" For check/uncheck action type the data field should be CHK ");
		} catch (Exception e) {
			driver.log.error(" Unable to perform Check / uncheck on "	+ fieldName);
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(" Unable to perform Check / uncheck on "	+ fieldName);
		}
	}

	/**
	 * Method to simulate Keypress Event
	 */

	private void keypress(WebDriver webdriver, String fieldText, String value, String fieldName) {

		driver.log.info(" Performing keypress event ");
		value = driver.utils.getValue(value).toUpperCase();
		if(value.equals("")) {
			String warn = " Value for key to be pressed is empty ";
			System.out.println(warn);
			driver.log.error(warn);
			resultDetails.setErrorMessage(warn);
			return;
		}

		fieldText = getFieldFromRepo(fieldText);

		if(fieldText.equals("")){
			String warn = " Field Locator for key to be pressed is empty ";
			System.out.println(warn);
			driver.log.error(warn);
			resultDetails.setErrorMessage(warn);
			return;
		}

		if (fieldName.equalsIgnoreCase(""))
			fieldName = fieldText;

		resultDetails.setComment(" Sending key " + value + " on to the field " + fieldName);
		driver.log.info(" Sending key " + value + " on to the field " + fieldName);
		try {
			value = Keys.valueOf(value).toString();

		} catch (Exception e) {
			driver.log.error(" Can't get Key for the value :: "	+ value);
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(" Can't get Key for the value :: "	+ value);
			return;
		}

		By locator = WebDriverUtils.locatorToByObj(webdriver, fieldText);

		if (locator == null) {
			System.out.println("Element " + fieldName + " not found");
			driver.log.error(" Element " + fieldName + " not found");
			resultDetails.setErrorMessage("Element " + fieldName + " not found");
			return;
		}

		try {
			webdriver.findElement(locator).sendKeys(value);
			resultDetails.setFlag(true);
		} catch (Exception e) {
			driver.log.error(" Exception while performing KeyPress Event on " + fieldName);
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage("Exception while performing KeyPress Event on " + fieldName);
		}
	}

	/**
	 * Method to store attribute of given element in a desired key-value pair
	 */

	private void storeAttribute(WebDriver webdriver, String fieldText, String key, String fieldName) {

		int n = fieldText.lastIndexOf("@");

		driver.log.info(" Storing the attribute " + fieldText + " in the key " + key);
		resultDetails.setComment("Storing the attribute " + fieldText + " in the key " + key);

		if (n <= 0) {
			String warn = " Invalid type for locator is specified . Use locator@attribute ";
			System.out.println(warn);
			resultDetails.setErrorMessage(warn);
			driver.log.error(warn);
			return;
		}

		String field = fieldText.substring(0, n);
		field = getFieldFromRepo(field);

		if(field.equals("")){
			String warn = " The locator is returned empty ";
			System.out.println(warn);
			resultDetails.setErrorMessage(warn);
			driver.log.error(warn);
			return;
		}

		String attribute = fieldText.substring(n + 1);

		if (key.equalsIgnoreCase("")) {
			driver.log.error(" Key specified should not be empty ");
			resultDetails.setErrorMessage(" Key specified should not be empty ");
			return;
		}

		if (attribute.equalsIgnoreCase("")) {
			driver.log.error(" Attribute specified should not be empty");
			resultDetails.setErrorMessage(" Attribute specified should not be empty");
			return;
		}

		By locator = WebDriverUtils.locatorToByObj(webdriver, field);

		if (fieldName.equalsIgnoreCase(""))
			fieldName = field;

		if (locator == null) {
			driver.log.error(" Element " + fieldName + " not found");
			resultDetails.setErrorMessage("Element " + fieldName + " not found");
			return;
		}

		try {

			String strValue = webdriver.findElement(locator).getAttribute(attribute);
			driver.hMap.put(key, strValue);
			strValue = "@"+attribute+"='"+strValue+"'";
			driver.hMap.put(key+"_attribute", strValue);
			resultDetails.setComment("Storing the attribute " + strValue + " in the key " + key);
			driver.log.info(" Storing the attribute " + strValue + " in the key " + key);
			System.out.println("Key:: " + key + "  Value:: " + strValue);
			driver.log.info("Key:: " + key + "  Value:: " + strValue);


			resultDetails.setFlag(true);

		} catch (Exception e) {
			driver.log.error(" Exception in executing storeAttribute on " + fieldName);
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(" Exception in executing storeAttribute on " + fieldName);
		}
	}

	/**
	 * Method to verify Attribute with given value
	 */

	private void verifyAttribute(WebDriver webdriver, String fieldText, String value, String fieldName) {

		int n = fieldText.lastIndexOf("@");

		if (n <= 0) {
			driver.log.error(" Invalid type for locator is specified . Use locator@attribute ");
			resultDetails.setErrorMessage(" Invalid type for locator is specified . Use locator@attribute ");
			return;
		}

		driver.log.info(" Verifying the attribute " + fieldText	+ " with value " + value);
		resultDetails.setComment("Verifying the attribute " + fieldText	+ " with value " + value);

		if (fieldName.equalsIgnoreCase(""))
			fieldName = fieldText;

		value = driver.utils.getValue(value);
		fieldText = getFieldFromRepo(fieldText);

		String field = fieldText.substring(0, n);
		String attribute = fieldText.substring(n + 1);

		field = getFieldFromRepo(field);

		if (field.equalsIgnoreCase("")) {
			driver.log.error(" Field specified should not be empty");
			resultDetails.setErrorMessage(" Field specified should not be empty");
			return;
		}

		if (value.equalsIgnoreCase("")) {
			driver.log.error(" Value specified should not be empty");
			resultDetails.setErrorMessage(" Value specified should not be empty");
			return;
		}

		if (attribute.equalsIgnoreCase("")) {
			driver.log.error(" Attribute specified should not be empty");
			resultDetails.setErrorMessage(" Attribute specified should not be empty");
			return;
		}

		By locator = WebDriverUtils.locatorToByObj(webdriver, field);

		if (locator == null) {
			driver.log.error(" Element " + fieldName + " not found");
			resultDetails.setErrorMessage("Element " + fieldName + " not found");
			return;
		}

		try {

			String strValue = webdriver.findElement(locator).getAttribute(attribute);
			resultDetails.setComment("Verifying that Attribute " + value + " same as " + strValue);

			boolean res = value.equalsIgnoreCase(strValue);
			if (res) {
				resultDetails.setFlag(true);
				return;
			} else {
				driver.log.error(" Attribute " + value	+ " is not same as " + strValue);
				resultDetails.setErrorMessage("Attribute " + value	+ " is not same as " + strValue);
			}

		} catch (Exception e) {
			driver.log.error(" Exception in executing Verify Attribute");
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(" Exception in executing Verify Attribute");
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Method to store the given value in a HashMap
	 */

	private void storeValue(WebDriver webdriver, String fieldText, String key, String fieldName) {

		if(fieldText.length() < 4){
			String warn = " DataField is not in expected format ";
			System.out.println(warn);
			driver.log.error(warn);
			resultDetails.setErrorMessage(warn);
			return;
		}

		String fieldType = fieldText.substring(0, 3).toUpperCase();
		String field = fieldText.substring(3);

		if (fieldName.equalsIgnoreCase(""))
			fieldName = field;

		field = getFieldFromRepo(field);

		boolean isDbv = fieldType.equalsIgnoreCase("DBV") ;
		boolean isTBL = fieldType.equalsIgnoreCase("TBL") ;
		boolean isSTV = fieldType.equalsIgnoreCase("STV");
		if(field.equals("") && !isDbv){
			String warn = " DataField has returned empty locator ";
			System.out.println(warn);
			resultDetails.setErrorMessage(warn);
			driver.log.error(warn);
			return;
		}

		By locator = WebDriverUtils.locatorToByObj(webdriver, field);

		resultDetails.setComment("Storing the Attribute value for :: "	+ fieldName);

		if (key.equalsIgnoreCase("")) {
			driver.log.error(" Key value is empty ");
			resultDetails.setErrorMessage(" Key value is empty ");
			return;
		}


		if (locator == null && !(isDbv || isTBL || isSTV)) {
			driver.log.error(" Unable to locate element :: "	+ fieldName);
			resultDetails.setErrorMessage(" Unable to locate element :: "	+ fieldName);
			return;
		}

		String val = "";

		try {
			StoreFields sfs = StoreFields.valueOf(fieldType);
			switch (sfs) {

			case STV:
				System.out.println("Storing value :: "+ field + " in key " + key);
				resultDetails.setComment("Storing value :: "+ field + " in key " + key);
				driver.log.info(" Storing value :: "+ field + " in key " + key);
				driver.hMap.put(key, field);
				resultDetails.setFlag(true);
				break;

			case TXT:
			case COB:

				val = webdriver.findElement(locator).getAttribute("value");
				resultDetails.setComment("Storing the Attribute value for "	+ fieldName + "  Value  -> " + val);
				System.out.println("Storing the Attribute value for " + fieldName + "  Value  -> " + val);
				driver.log.info(" Storing the Attribute value for " + fieldName + "  Value  -> " + val);
				driver.hMap.put(key, val);
				resultDetails.setFlag(true);
				break;

			case XPH:
			case LNK:
			case BTN:
				val = webdriver.findElement(locator).getText();
				resultDetails.setComment("Storing the value for "	+ fieldName + "  Value  -> " + val);
				driver.log.info(" Storing the value for " + fieldName + "  Value  -> " + val);
				System.out.println("Storing the value for " + fieldName + "  Value  -> " + val);
				driver.hMap.put(key, val);
				resultDetails.setFlag(true);
				break;

			case DBV:
				storeDBValue(fieldText,key,fieldName);
				break;

			case TBL:

				String strVal = "";
				try {

					String [] tempValues = key.split(":");
					int row = Integer.parseInt(tempValues[0]);
					int col = Integer.parseInt(tempValues[1]);
					key = tempValues[2];

					field = "//table[" + field + "]//tr[" + row + "]/td[" + col	+ "]";

					locator = WebDriverUtils.locatorToByObj(webdriver, field);

					if (locator == null) {
						driver.log.error(" Unable to locate element :: "	+ fieldName);
						resultDetails.setErrorMessage(" Unable to locate element :: "	+ fieldName);
						return;
					}

					strVal = webdriver.findElement(locator).getText();
					resultDetails.setComment("Storing value :: "+ strVal + " in key " + key);
					driver.log.info(" Storing value :: "+ strVal + " in key " + key);
					driver.hMap.put(key, strVal);
					resultDetails.setFlag(true);
				} catch (Exception e) {
					driver.log.error(" Error occured while storing the value for " + fieldName);
					driver.log.error(e.getMessage());
					resultDetails.setErrorMessage(" Error occured while storing the value for " + fieldName);
				}
				resultDetails.setComment(" Storing the cell value " + strVal	+ " for " + fieldName);
				driver.log.info(" Storing the cell value " + strVal	+ " for " + fieldName);
			}
		} catch (IllegalArgumentException e) {
			driver.log.error(" For Storevalue, Datafields must be TXT COB LNK XPH TBL ");
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(" For Storevalue, Datafields must be TXT COB LNK XPH TBL ");
		} catch (Exception e) {
			driver.log.error(" Exception in executing store value ");
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(" Exception in executing store value ");
		}
	}

	/**
	 * Method to store the User defined Comments
	 */

	public void storeComments(WebDriver webdriver,String field,String value, String fieldName) {

		if(field.length() < 4){
			String warn = " Field is not provided as expected ";
			System.out.println(warn);
			driver.log.error(warn);
			resultDetails.setErrorMessage(warn);
			return;
		}

		if(value.equals("")){
			String warn = " Value is empty ";
			System.out.println(warn);
			driver.log.error(warn);
			resultDetails.setErrorMessage(warn);
			return;
		}

		String fieldType =  field.substring(0, 3);

		field = field.substring(3);

		field = getFieldFromRepo(field);

		if (fieldName.equalsIgnoreCase(""))
			fieldName = field;

		if(fieldType.equalsIgnoreCase("HMV")) {
			if(driver.hMap.containsKey(field)){
				resultDetails.setFlag(true);
				String temp = "Comment --> " + value + " :: " + driver.hMap.get(field);
				System.out.println(temp);
				driver.log.info(temp);
				resultDetails.setComment(temp);
			}else{
				driver.log.error(" Unable to find " + field + " in hashmap");
				resultDetails.setErrorMessage(" Unable to find " + field + " in hashmap");
				return;
			}
		}
		else if(fieldType.equalsIgnoreCase("TXT")) {

			field = getFieldFromRepo(field);
			By locator = WebDriverUtils.locatorToByObj(webdriver, field);

			if(locator == null){
				driver.log.error(" Unable to find " + fieldName );
				resultDetails.setErrorMessage(" Unable to find " + fieldName );
				return;
			} else{
				try {				
					String temp =  "Comment --> " + value + " :: " + webdriver.findElement(locator).getAttribute("value");
					resultDetails.setFlag(true);
					resultDetails.setComment(temp);
				} catch (Exception e) {
					driver.log.error(" Error occured while storing the value form :: "+ fieldName);
					driver.log.error(e.getMessage());
					resultDetails.setErrorMessage("Error occured while storing the value form :: "+ fieldName);
				}
			}
		}

		String comment = resultDetails.getComment().replace("Comment -->" , "");

		if(resultDetails.getFlag()){
			if(driver.hMap.containsKey("comment_excel_write")){
				comment = comment + "\n" + driver.hMap.get("comment_excel_write");
			}
			driver.hMap.put("comment_excel_write",comment);
		}
	}

	/**
	 * Method to verify the different components
	 */

	private void verify(WebDriver webdriver, String fieldText, String value,String fieldName) {

		if(fieldText.length() < 3){
			resultDetails.setErrorMessage(" DataField is not as expected ");
			driver.log.error(" DataField is not as expected ");
			System.out.println(" DataField is not as expected ");
			return;
		}

		String fieldType = fieldText.substring(0, 3).toUpperCase();
		String field = fieldText.substring(3);

		if (fieldName.equalsIgnoreCase(""))
			fieldName = field;

		value = driver.utils.getValue(value);

		if(value.equals("")&&!fieldType.equalsIgnoreCase("TXT")){
			String warn = " Value Field is empty ";
			System.out.println(warn);
			driver.log.error(warn);
			resultDetails.setErrorMessage(warn);
			return;
		}

		field = getFieldFromRepo(field);
		By locator=null;
		System.out.println(":"+field+":"+field.length()+":"+field.equals(""));
		if(field!=null&&!field.equals(""))
			locator = WebDriverUtils.locatorToByObj(webdriver, field);

		try {

			DataFields dfs = DataFields.valueOf(fieldType);

			switch (dfs) {

			case TTL:
				resultDetails.setComment("Verifying the  window Title: " + value);
				try {
					String actual = webdriver.getTitle();
					String expected = value.trim();

					boolean res = actual.equals(expected);

					if (res)
						resultDetails.setFlag(true);
					else {
						String temp = " Title Mismatch . Expected '" + expected	+ "' . But Found '" + actual + "'";
						System.out.println(temp);
						driver.log.error(temp);
						resultDetails.setErrorMessage(temp);
						return;
					}

				} catch (Exception e) {
					driver.log.error(" Exception in verifying the title ");
					driver.log.error(e.getMessage());
					System.out.println(" Exception in verifying the title ");
					System.out.println(e.getMessage());
					resultDetails.setErrorMessage(" Exception in verifying the title ");
				}
				break;

			case TXT:
				resultDetails.setComment("Verifying the Text " + value + " in field " + fieldName);

				if(field.equals("")){
					String warn = " Locator for TXT is empty ";
					System.out.println(warn);
					driver.log.error(warn);
					resultDetails.setErrorMessage(warn);
					return;
				}

				if (locator == null) {
					resultDetails.setErrorMessage(" Unable to find Element " + fieldName);
					driver.log.error(" Unable to find Element " + fieldName);
					return;
				}

				try {

					String actual = webdriver.findElement(locator).getAttribute("value").trim();
					String expected = value.trim();

					boolean res = actual.equalsIgnoreCase(expected);

					if (res)
						resultDetails.setFlag(true);
					else {
						String temp = " Text Mismatch . Expected '" + expected	+ "' . But Found '" + actual + "'";
						System.out.println(temp);
						resultDetails.setErrorMessage(temp);
						driver.log.error(temp);
						return;
					}
				} catch (Exception e) {
					driver.log.error(" Exception in verifying the Text");
					driver.log.error(e.getMessage());
					System.out.println("Exception in verifying the Text");
					System.out.println(e.getMessage());
					resultDetails.setErrorMessage(" Exception in verifying the text ");
				}
				break;

			case CHK:
				try{
					if(field.equals("")){
						String warn = " Locator for CHK is empty ";
						System.out.println(warn);
						resultDetails.setErrorMessage(warn);
						driver.log.error(warn);
						return;
					}

					if (locator == null) {
						driver.log.error(" Unable to find Element "+ fieldName);
						resultDetails.setErrorMessage(" Unable to find Element "+ fieldName);
						return;
					}

					String attrVal = "";

					if (value.indexOf(":") == -1) {
						value = "value:" + value; 
					}

					String dataValues[] = value.split(":");
					attrVal = webdriver.findElement(locator).getAttribute(dataValues[0]); 

					System.out.println("Attribute value : "+ attrVal);
					System.out.println("Expected Value : " + dataValues[1]);

					boolean status = attrVal.equals(dataValues[1]);

					if(status){
						resultDetails.setFlag(true);
						return;
					}else{
						resultDetails.setErrorMessage(" Check box "+ fieldName + "is not in expected state : " + dataValues[1] );
						driver.log.error(" Check box "+ fieldName + "is not in expected state : " + dataValues[1] );
						return;
					}
				} catch (Exception e) {
					driver.log.error("Exception in Executing verify on " + fieldName);
					driver.log.error(e.getMessage());
					resultDetails.setFlag(false);
					resultDetails.setErrorMessage("Exception in Executing verify on " + fieldName);
				}

				break;

			case XPH:
			case RDB:
				try {

					if(field.equals("")){
						String warn = " Locator for XPH is empty ";
						System.out.println(warn);
						resultDetails.setErrorMessage(warn);
						driver.log.error(warn);
						return;
					}

					resultDetails.setComment("Verifying the value " + value	+ " at " + fieldName);
					driver.log.info("Verifying the value " + value	+ " at " + fieldName);

					if (locator == null) {
						driver.log.error(" Unable to find Element "+ fieldName);
						resultDetails.setErrorMessage(" Unable to find Element "+ fieldName);
						return;
					}

					String actual = webdriver.findElement(locator).getText();
					String expected = value.trim();
					System.out.println("text == " + actual);
					System.out.println("value == " + expected);
					boolean res = actual.contains(expected);
					System.out.println("res == " + res);

					if (res)
						resultDetails.setFlag(true);
					else {

						actual = webdriver.findElement(locator).getAttribute("value");
						res = actual.equals(expected);
						if (res)
							resultDetails.setFlag(true);
						else {
							String temp = " Value Mismatch . Expected '" + expected + "' . But Found '" + actual + "'";
							System.out.println(temp);
							driver.log.error(temp);
							resultDetails.setErrorMessage(temp);
						}
					}
				} catch (Exception e) {
					driver.log.error(" Exception in executing verify on "+ fieldName);
					driver.log.error(e.getMessage());
					resultDetails.setErrorMessage("Exception in executing verify on "+ fieldName);
				}
				break;


			case COB:
				try {
					if(field.equals("")){
						String warn = " Locator for COB is empty ";
						System.out.println(warn);
						driver.log.error(warn);
						resultDetails.setErrorMessage(warn);
						return;
					}
					if (locator == null) {
						driver.log.error(" Unable to find Element "+ fieldName);
						resultDetails.setErrorMessage(" Unable to find Element "+ fieldName);
						return;
					}
					System.out.println("Selected:"+WebDriverUtils.getSelectedLabel(webdriver, field));	
					if(WebDriverUtils.getSelectedLabel(webdriver, field).toString().equals(value))
					{
						resultDetails.setFlag(true);
						resultDetails.setErrorMessage("");
					}
					else
					{
						resultDetails.setFlag(false);
						resultDetails.setErrorMessage("Mismatched Expected::"+value+" Actual::"+WebDriverUtils.getSelectedLabel(webdriver, field).toString());
						driver.log.error("Mismatched Expected::"+value+" Actual::"+WebDriverUtils.getSelectedLabel(webdriver, field).toString());
						System.out.println("Mismatched Expected::"+value+" Actual::"+WebDriverUtils.getSelectedLabel(webdriver, field).toString());
					}


				} catch (Throwable e) {
					resultDetails.setFlag(false);
					resultDetails.setErrorMessage("ComboBox not found :" + value);

				}
				break;
			case SLB:
				try {

					if(field.equals("")){
						String warn = " Locator for SLB is empty ";
						System.out.println(warn);
						driver.log.error(warn);
						resultDetails.setErrorMessage(warn);
						return;
					}

					ArrayList<String> arr = new ArrayList<String>();
					arr = parse(value, "|");

					if (locator == null) {
						driver.log.error(" Unable to find Element "+ fieldName);
						resultDetails.setErrorMessage(" Unable to find Element "+ fieldName);
						return;
					}

					String[] options = WebDriverUtils.getSelectedOptions(webdriver, field);

					int size = options.length;

					resultDetails.setComment("Verifying the Selected options  of "+ fieldName);

					if (arr.size() == size) {

						for (int i = 0; i < size; i++) {

							System.out.println("..options= " + options[i]);

							try {
								boolean res = options[i].equalsIgnoreCase(arr.get(i));
								if (res) {
									resultDetails.setFlag(true);
									continue;
								} else {
									resultDetails.setFlag(false);
									driver.log.error(" Selected options  of "+ fieldName+ " mismatch at "+ options[i]+ " &  "+ arr.get(i));
									resultDetails.setErrorMessage("Selected options  of "+ fieldName+ " mismatch at "+ options[i]+ " &  "+ arr.get(i));
									return;
								}
							} catch (Exception e) {
								driver.log.error(" Options mismatch with expected result  ::"+ arr.get(i)+ " in field"+ fieldName);
								driver.log.error(e.getMessage());
								System.out.println("Options mismatch with expected result  ::"+ arr.get(i));
								resultDetails.setErrorMessage("Options mismatch with expected result  ::"+ arr.get(i)+ " in field"+ fieldName);
								return;
							}
						}
					}

					else {

						options = WebDriverUtils.getAvailableOptions(webdriver, field);
						size = options.length;

						int count = 0;
						for (int j = 0; j < arr.size(); j++) {
							for (int i = 0; i < size; i++) {
								if (arr.get(j).equalsIgnoreCase(options[i])) {
									count++;
									System.out.println("Test data found in options:: "+ count);
									break;
								}
							}
						}

						if (count > 0 && count == arr.size()) {
							resultDetails.setFlag(true);
						} else {
							resultDetails.setFlag(false);
							driver.log.error(" ComboBox values does not Match with expected result");
							resultDetails.setErrorMessage("ComboBox values does not Match with expected result");
						}
					}
				} catch (Exception e) {
					driver.log.error(" Exception in executing verify on select "	+ fieldName);
					driver.log.error(e.getMessage());
					resultDetails.setErrorMessage(" Exception in executing verify on select "	+ fieldName);
				}
				break;

			case BTN:
			case IMG:

				value = getFieldFromRepo(value);

				resultDetails.setComment(" Verifying whether " + fieldName + "is present ");

				locator = WebDriverUtils.locatorToByObj(webdriver, value);

				if (locator == null) {
					driver.log.error(" Unable to find Element " + fieldName);
					resultDetails.setErrorMessage(" Unable to find Element " + fieldName);
					return;
				}

				try {

					boolean res = (WebDriverUtils.isElementPresent(webdriver,value));
					if (res)
						resultDetails.setFlag(true);
					else {
						driver.log.error(" Element " + fieldName	+ " not found");
						resultDetails.setErrorMessage(" Element " + fieldName	+ " not found");
						return;
					}

				} catch (Exception e) {
					driver.log.error(" Unable to find Element "+ fieldName);
					driver.log.error(e.getMessage());
					resultDetails.setErrorMessage(" Unable to find Element "+ fieldName);
					return;
				}

				break;

			case LNK:

				resultDetails.setComment(" Verifying the link " + fieldName);
				int count = field.length();

				if ((count > 6)	&& (field.substring(count - 5).equalsIgnoreCase("@href"))) {

					try {

						locator = WebDriverUtils.locatorToByObj(webdriver,field.substring(0, count - 5));
						if (locator == null) {
							driver.log.error(" Unable to find Element " + fieldName);
							resultDetails.setErrorMessage(" Unable to find Element " + fieldName);
							return;
						}

						String attr = webdriver.findElement(locator).getAttribute("href");

						System.out.println(" Attribute value : " + attr);
						System.out.println(" Parameter value : " + value);
						if (attr.equalsIgnoreCase(value)) {
							resultDetails.setFlag(true);
						} else {
							driver.log.error(fieldName	+ " attribute value NOT matched. Expected : "+ (value) + " Actual: " + attr);
							resultDetails.setErrorMessage(fieldName	+ " attribute value NOT matched. Expected : "+ (value) + " Actual: " + attr);
						}
					} catch (Exception e) {
						driver.log.error(" Error occured while getting the attribute value of :: "+ fieldName);
						driver.log.error(e.getMessage());
						resultDetails.setErrorMessage("Error occured while getting the attribute value of :: "+ fieldName);
					}
					break;
				} else {
					try {

						boolean res = (WebDriverUtils.isElementPresent(webdriver,getFieldFromRepo(value)));
						if (res)
							resultDetails.setFlag(true);
						else {
							driver.log.error(" Element "+ fieldName + " not found");
							resultDetails.setErrorMessage(" Element "+ fieldName + " not found");
							return;
						}

					} catch (Exception e) {
						driver.log.error(" Link with id '" + value+ "' doesn't exist");
						driver.log.error(e.getMessage());
						resultDetails.setErrorMessage("Link with id '" + value+ "' doesn't exist");
						System.out.println("Link '" + fieldName+ "' doesn't exist");
					}
				}
				break;

			case ALT:
			case CNF:
				try {

					resultDetails.setComment("Verifying the Confirmation box "+ fieldName + " is present");

					boolean res = value.equalsIgnoreCase(WebDriverUtils.getAlert(webdriver));
					if (res)
						resultDetails.setFlag(true);
					else {
						driver.log.error(" Confirmation box "+ fieldName + " not found");
						resultDetails.setErrorMessage(" Confirmation box "+ fieldName + " not found");
						return;
					}
				} catch (Exception e) {
					driver.log.error(" Confirmation box not found");
					driver.log.error(e.getMessage());
					System.out.println("Confirmation box not found");
					resultDetails.setErrorMessage(" Confirmation box " + fieldName + " not found ");
				}
				break;

			case MSG:
				try {
					resultDetails.setComment(" Verifying that Message "+ fieldName + " is present ");
					System.out.println("value :: " + value);

					boolean res = webdriver.getPageSource().toLowerCase().contains(value.toLowerCase());
					if (res)
						resultDetails.setFlag(true);
					else {
						driver.log.error(" Message " + fieldName + " not found");
						resultDetails.setErrorMessage(" Message " + fieldName + " not found");
						return;
					}

					break;

				} catch (Exception e) {
					driver.log.error(" Text :: +" + value + "   :: not found");
					driver.log.error(e.getMessage());
					System.out.println("Text :: +" + value + "   :: not found");
					resultDetails.setErrorMessage("Text :: +" + value + "   :: not found");
				}
				break;

			case GET:
				try {

					resultDetails.setComment("Verifying that " + fieldName + " value / Text is present in HashMap");

					String tempField = "";
					fieldType = "";

					if (value.length() > 3) {
						tempField = value.substring(3);
						fieldType = value.substring(0, 3);
					}

					locator = WebDriverUtils.locatorToByObj(webdriver,getFieldFromRepo(tempField));

					if (locator == null) {
						driver.log.error(" Unable to find Element "+ fieldName);
						resultDetails.setErrorMessage(" Unable to find Element "+ fieldName);
						return;
					}

					if (fieldType.equalsIgnoreCase("TXT")) {
						value = webdriver.findElement(locator).getAttribute("value");
						resultDetails.setComment("Verifying that Text " + value + " is present in HashMap");
						driver.log.info(" Verifying that Text " + value + " is present in HashMap");

					} else if (fieldType.equalsIgnoreCase("LNK")) {
						value = webdriver.findElement(locator).getText();
						driver.log.info(" Verifying that Link " + value	+ " is present in HashMap");
						resultDetails.setComment("Verifying that Link " + value	+ " is present in HashMap");
					} else if (fieldType.equalsIgnoreCase("XPH")) {
						value = webdriver.findElement(locator).getText();
						driver.log.info(" Verifying that Element text :: "+ value + " is present in HashMap");
						resultDetails.setComment("Verifying that Element text :: "+ value + " is present in HashMap");
					} else {
						driver.log.error(" In Data values fields must be  TXT , LNK, XPH while using GET" + fieldName);
						resultDetails.setErrorMessage(" In Data values fields must be  TXT , LNK, XPH while using GET" + fieldName);
						return;
					}

					boolean res = driver.hMap.get(field).equalsIgnoreCase(value);

					if (res)
						resultDetails.setFlag(true);
					else {
						driver.log.error((" Value " + fieldName+ " not found in HashMap"));
						resultDetails.setErrorMessage(" Value " + fieldName+ " not found in HashMap");
						return;
					}

				} catch (Exception e) {
					driver.log.error(" Actual Value :: " + value+ "   Expected Value ::" + driver.hMap.get(field));
					driver.log.error(e.getMessage());
					System.out.println("Actual Value :: " + value+ "   Expected Value ::" + driver.hMap.get(field));
					resultDetails.setErrorMessage("Actual Value :: " + value + "   Expected Value ::" + driver.hMap.get(field));
				}
				break;

			case TBL:

				String actual = "";

				value = driver.utils.getValue(value);
				field = getFieldFromRepo(field);
				String expected = value.trim();

				try {

					resultDetails.setComment(" Verifying value :: " + actual+ " with " + value);
					driver.log.error(" Verifying value :: " + actual+ " with " + value);

					String[] tempValues = value.split(":");
					int row = Integer.parseInt(tempValues[0]);
					int col = Integer.parseInt(tempValues[1]);

					value = driver.utils.getValue(value.substring((tempValues[0] + tempValues[1]).length()+2));

					field = "//table[" + field + "]//tr[" + row + "]/td[" + col	+ "]";

					locator = WebDriverUtils.locatorToByObj(webdriver, field);

					if (locator == null) {
						driver.log.error(" Unable to locate element ::"	+ fieldName);
						resultDetails.setErrorMessage("Unable to locate element ::"	+ fieldName);
						return;
					}
					expected = value.trim();
					actual = webdriver.findElement(locator).getText().trim();
					resultDetails.setComment("Verifying value :: " + actual	+ " with " + value);

					boolean res = actual.equals(expected);

					if (res)
						resultDetails.setFlag(true);
					else {
						String temp = " Value Mismatch . Expected '" + expected	+ "' . But Found '" + actual + "'";
						System.out.println(temp);
						driver.log.error(temp);
						resultDetails.setErrorMessage(temp);
						return;
					}

				} catch (Exception e) {
					driver.log.error(" Error occured while verifying the value for "+ fieldName);
					driver.log.error(e.getMessage());
					resultDetails.setErrorMessage("Error occured while verifying the value for "+ fieldName);
				}

				break;
			}
		} catch (IllegalArgumentException e) {

			driver.log.error(" For verify type the field type argument must not be " + fieldType);
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage("For verify type the field type argument must not be " + fieldType);
		} catch (Exception e) {
			driver.log.error(" Error in executing verify keyword on  "+ fieldName);
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(" Error in executing verify keyword on  "+ fieldName);
		}
	}

	/**
	 *  Method to verify the different components that are not to be present
	 */

	private void verifynotpresent(WebDriver webdriver, String fieldText,String value, String fieldName) {

		if(fieldText.length() < 3){
			String warn = " Locator is not in expected format ";
			System.out.println(warn);
			driver.log.error(warn);
			resultDetails.setErrorMessage(warn);
			return;
		}

		value = driver.utils.getValue(value);

		if(value.equals("")){
			String warn = " Value is empty ";
			System.out.println(warn);
			driver.log.error(warn);
			resultDetails.setErrorMessage(warn);
			return;
		}

		String fieldType = fieldText.substring(0, 3).toUpperCase();
		String field = fieldText.substring(3);

		if (fieldName.equalsIgnoreCase(""))
			fieldName = field;

		try {

			DataFields dfs = DataFields.valueOf(fieldType);
			switch (dfs) {

			case MSG:
				try {

					resultDetails.setComment("Verifying that " + value	+ " is not present");
					boolean isPresent = webdriver.getPageSource().toLowerCase()	.contains(value.toLowerCase().trim());

					if (isPresent) {
						driver.log.error(" Text :: " + value + " :: found which is NOT expected.");
						resultDetails.setErrorMessage("Text :: " + value + " :: found which is NOT expected.");
						return;
					}

					resultDetails.setFlag(true);

				} catch (Exception e) {
					driver.log.error(" Unable to Execute VERIFYNOTPRESENT on MSG");
					driver.log.error(e.getMessage());
					resultDetails.setErrorMessage("Unable to Execute VERIFYNOTPRESENT on MSG");
				}
				break;

			case XPH:
				try {
					driver.log.info(" Verifying that text " + value + " is not present");
					resultDetails.setComment("Verifying that text " + value + " is not present");
					field = getFieldFromRepo(field);

					if(field.equals("")){
						String warn = " Field is empty ";
						System.out.println(warn);
						driver.log.error(warn);
						resultDetails.setErrorMessage(warn);
						return;
					}

					By loc = WebDriverUtils.locatorToByObj(webdriver, field);

					if (loc == null) {
						driver.log.error(" Element " + fieldName + " not found");
						resultDetails.setErrorMessage(" Element " + fieldName + " not found");
						return;
					}

					String text = webdriver.findElement(loc).getText();

					if (text.equalsIgnoreCase(value)) {
						driver.log.error(" Text " + value + " is present");
						resultDetails.setErrorMessage(" Text " + value + " is present");
						return;
					}

					else{

						try{
							text = webdriver.findElement(loc).getAttribute("value");
							if (text.equalsIgnoreCase(value)) {
								driver.log.error(" Text " + value + " is present");
								resultDetails.setErrorMessage(" Text " + value + " is present");
								return;
							}
						}catch(Exception e){
						}
					}

					resultDetails.setFlag(true);

				} catch (Exception e) {
					driver.log.error(" Object " + fieldName + " is present, which is not as expected");
					driver.log.error(e.getMessage());
					resultDetails.setErrorMessage("Object " + fieldName + " is present, which is not as expected");
				}
				break;

			case BTN:
			case LNK:
				try {
					driver.log.info(" Verifying that object " + fieldName + " is not present");
					resultDetails.setComment("Verifying that object " + fieldName + " is not present");
					value = getFieldFromRepo(value);
					boolean res = WebDriverUtils.isElementPresent(webdriver, value);
					if (res) {
						driver.log.error(" Element " + fieldName + " is present");
						resultDetails.setErrorMessage(" Element " + fieldName + " is present");
						return;
					}
					resultDetails.setFlag(true);

				} catch (Exception e) {
					driver.log.error(" Object " + fieldName + " is present, which is not as expected");
					driver.log.error(e.getMessage());
					resultDetails.setErrorMessage("Object " + fieldName + " is present, which is not as expected");
				}
				break;

			case CBS:
				try {
					driver.log.info(" Verifying that object " + fieldName + " is not present");
					resultDetails.setComment("Verifying that object " + fieldName + " is not present");
					value = driver.utils.getValue(value);
					field = getFieldFromRepo(field);

					By loc = WebDriverUtils.locatorToByObj(webdriver, field);

					if (loc == null) {
						driver.log.error(" Element " + fieldName + " not found");
						resultDetails.setErrorMessage(" Element " + fieldName + " not found");
						return;
					}

					String [] options = WebDriverUtils.getAvailableOptions(webdriver, field);

					for(String str : options ){
						if(value.equals(str)){
							driver.log.error(" Element " + value + " found in field " + field);
							resultDetails.setErrorMessage(" Element " + value + " found in field " + field);
							return;
						}
					}

					resultDetails.setFlag(true);

				} catch (Exception e) {
					driver.log.error("Object " + fieldName + " is present, which is not as expected");
					driver.log.error(e.getMessage());
					resultDetails.setErrorMessage("Object " + fieldName + " is present, which is not as expected");
				}
				break;
			default:
				driver.log.error(" For VERIFYNOTPRESENT field type must be MSG ");
				resultDetails.setErrorMessage(" For VERIFYNOTPRESENT field type must be MSG ");
				break;
			}

		} catch (IllegalArgumentException e) {
			driver.log.error(" For VERIFYNOTPRESENT field type must be MSG, BTN, LNK ");
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(" For VERIFYNOTPRESENT field type must be MSG, BTN, LNK ");
		} catch (Exception e) {
			driver.log.error(" Unable to execute VERIFYNOTPRESENT ");
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(" Unable to execute VERIFYNOTPRESENT ");
		}
	}

	/**
	 * Method to sleep for specified amount of time in MS
	 */

	public void sleep(int n) {
		try {
			Thread.sleep(n);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Method to get Values from HashMap / TestData
	 *//*

	public String getValue(String value) {

		String tempValue = value;

		if (tempValue.toLowerCase().startsWith("key:")) {

			tempValue = tempValue.substring(4).toLowerCase();

			if(driver.inExecute && !tempValue.contains("::")){
				tempValue = driver.setup_data.getProperty(tempValue,tempValue);
				driver.log.info("value = " + tempValue);
				System.out.println("value = " + tempValue);
				return tempValue;
			}

			else {
				String index = "1";
				String val[] = tempValue.split("::"); 

				if(val[1].contains("#")){
					int n = val[1].indexOf("#");
					String temp = val[1].substring(0, n);
					index = (val[1].substring(n + 1));
					val[1] = temp;
				}

				tempValue = driver.setup_TestData.get(val[0].toLowerCase()+"#"+index).getProperty(val[1],val[1]);
			}
			driver.log.info(" value = " + tempValue);
			System.out.println(" value = " + tempValue);
			return tempValue;
		}

		if ( tempValue.toLowerCase().endsWith("#rnd")) {

			tempValue = tempValue.substring(3,tempValue.length()-4);
			if(driver.testDataCounter.containsKey(tempValue)){
				int n = 1;
				int count = driver.testDataCounter.get(tempValue);
				n = (int)( count * Math.random()) % count;
				if(n == 0 ){
					n = count;
				}
				tempValue = tempValue + n;
			}
		}

		if ( tempValue.toLowerCase().endsWith("#auto")) {

			tempValue = tempValue.substring(3,tempValue.length()-5);
			int n = 1;

			if(driver.inLoop && driver.testDataCounter.containsKey(tempValue)){

				n = Integer.parseInt(driver.hMap.get("LOOPCOUNTER"));
				int count = driver.testDataCounter.get(tempValue);
				n = n % count;
				if(n == 0)
					n = count;
			}
			tempValue = tempValue + n ;
		}

		if (driver.parameterDetails.containsKey(tempValue)) {
			tempValue = driver.parameterDetails.get(tempValue);
			System.out.println(" value = " + tempValue);
			driver.log.info(" value = " + tempValue);
			return tempValue;
		}

		if (tempValue.length() >= 3) {

			String tempValueType = tempValue.substring(0, 3);
			tempValue = value.substring(3);

			if (tempValueType.equalsIgnoreCase("RND")) {

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
			} else if (tempValueType.equalsIgnoreCase("HMV")) {
				tempValue = driver.hMap.get(tempValue);
			}else if (tempValueType.equalsIgnoreCase("dt:")||tempValueType.equalsIgnoreCase("dt|")) {

				if (tempValue.indexOf("#") == -1) {
					tempValue = tempValue + "1";
				}

				tempValue = tempValue.replace("#", "").toLowerCase();

				if (driver.parameterDetails.containsKey(tempValue))
					tempValue = driver.parameterDetails.get(tempValue);
			} else if (tempValueType.startsWith("d:")) {
				tempValue = getDate(value.substring(2));
			} else if(tempValueType.startsWith("DECPT:"))
			{
				try {
					//tempValue = Utils.getdecrypt("ezeon8547",value.substring(5));
				} catch(Exception e){
					System.out.println();
				}

			}else {
				tempValue = value;
			}
		}
		driver.log.info(" value = " + tempValue);
		System.out.println(" value = " + tempValue);
		return tempValue;
	}

	  *//**
	  * Method to get required Date
	  */	

	public String getDate(String value) {

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

			driver.hMap.put("strDate", reqDate);
			return reqDate;

		} else if (tempValues[0].toLowerCase().startsWith("HMV")) {

			reqDate = driver.hMap.get(tempValues[0].substring(3));
			try {
				today = (Date) sdf.parse(reqDate);
			} catch (ParseException e) {
				System.out.println("Unable to parse Date :: " + reqDate);
				today = new Date();
			}
			cal.setTime(today);
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
			System.out.println(" Required date : " + reqDate);
		}

		driver.hMap.put("strDate", reqDate);
		return reqDate;
	}

	/**
	 * Method to perform click operation
	 */

	public void click(WebDriver webdriver, String fieldText, String fieldName) {

		try {

			if(fieldText.length() < 3 ){
				String warn = " The locator is not specified as expected ";
				System.out.println(warn);
				driver.log.error(warn);
				resultDetails.setErrorMessage(warn);
				return;
			}

			String fieldType = fieldText.substring(0, 3);
			String field = fieldText.substring(3);

			field = getFieldFromRepo(field);

			if(field.startsWith("HMV")){
				field = driver.hMap.get(field.substring(3));
			}

			boolean fieldIsNotAltCnf = !(fieldType.equalsIgnoreCase("CNF") || fieldType.equalsIgnoreCase("ALT")) ;

			if(field.equals("") && fieldIsNotAltCnf){

				String warn = " The locator value is returned empty ";
				System.out.println(warn);
				driver.log.error(warn);
				resultDetails.setErrorMessage(warn);
				return;
			}

			if (fieldName.equalsIgnoreCase(""))
				fieldName = field;

			By locator = WebDriverUtils.locatorToByObj(webdriver, field);

			resultDetails.setComment(" Clicking on field " + fieldName);

			if (locator == null && fieldIsNotAltCnf ) {
				driver.log.error(" Unable to find element "+ fieldName);
				resultDetails.setErrorMessage(" Unable to find element "+ fieldName);
				return;
			}

			ClickFields cdf = ClickFields.valueOf(fieldType.toUpperCase());

			switch (cdf) {

			case LNK:
			case BTN:
			case XPH:
			case IMG:
				try {
					driver.log.info(" Clicking on field " + fieldName);

					webdriver.findElement(locator).click();
					resultDetails.setFlag(true);
				} catch (Exception e) {
					driver.log.error(" Unable to perform click on " + fieldName);
					driver.log.error(e.getMessage());
					resultDetails.setErrorMessage(" Unable to perform click on " + fieldName);
				}
				break;

			case CNF:
				try {
					driver.log.info(" Clicking on Confirmation box");
					Alert alert = webdriver.switchTo().alert();
					if (field.equalsIgnoreCase("CANCEL"))
						alert.dismiss();
					else
						alert.accept();
					resultDetails.setFlag(true);
				} catch (Exception e) {
					driver.log.error(" Unable to click Confirmation box " + fieldName);
					driver.log.error(e.getMessage());
					resultDetails .setErrorMessage(" Unable to click Confirmation box " + fieldName);
				}
				break;

			case ALT:
				try {
					driver.log.info(" Accepting the alert ");
					Alert alert = webdriver.switchTo().alert();
					alert.accept();
					resultDetails.setFlag(true);
				} catch (Exception e) {
					driver.log.error(" Unable to accept alert  ");
					driver.log.error(e.getMessage());
					resultDetails.setErrorMessage(" Unable to click Alert " + fieldName);
				}
				break;

			case JSC:

				field = webdriver.findElement(locator).getAttribute("id");
				System.out.println("field ID := "+field); 
				driver.log.info(" Using javascript to click on element "+ fieldName);
				try { 
					String Script = "javascript:document.getElementById('"+field+"').click();"; 
					System.out.println("Java Script : "+ Script); 
					((JavascriptExecutor) webdriver).executeScript(Script); 
					resultDetails.setFlag(true); 
				} catch (Exception e) {
					driver.log.error(" Exception Occured while executing the Javascript : "+ e.getMessage());
					driver.log.error(e.getMessage());
					System.out.println(" Exception Occured while executing the Javascript : "+ e.getMessage()); 
					resultDetails.setErrorMessage(" Unable to click the object " + fieldName); 
				}
				break;
			}
		} catch (IllegalArgumentException e) {
			driver.log.error(" For click action type the data field should be LNK, BTN, CNF, LNK, IMG, XPH or JSC ");
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(" For click action type the data field should be LNK, BTN, CNF, LNK, IMG, XPH or JSC ");
		} catch (Exception e) {
			driver.log.error(" Error in performing click on "+ fieldName);
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage( " Error in performing click on "+ fieldName);
		}

	}

	/**
	 * Method to parse tokens separated by delimiters
	 */

	public ArrayList<String> parse(String data, String delimiter) {

		ArrayList<String> dataValuesTokens = new ArrayList<String>();

		StringTokenizer st = new StringTokenizer(data, delimiter);

		while (st.hasMoreElements()) {
			dataValuesTokens.add(st.nextToken());
		}

		return dataValuesTokens;
	}
	/**
	 * Method to clear & type into a text field
	 */

	public void clear(WebDriver webdriver, String fieldText, String value, String fieldName) {

		try {

			if(fieldText.length() < 4){
				String warn = " The locator must be specified as TXT locator or EDT locator  ";
				System.out.println(warn);
				driver.log.error(warn);
				resultDetails.setErrorMessage(warn);
				return;
			}



			if(!(fieldText.substring(0,3).equals("TXT")^fieldText.substring(0,3).equals("EDT"))){
				String warn = " The locator must not be specified as type "+fieldText.substring(0,3)+" locator for clear ";
				System.out.println(warn);
				driver.log.error(warn);
				resultDetails.setErrorMessage(warn);
				return;
			}

			if(getFieldFromRepo(fieldText.substring(3)).equals("")){
				String warn = " The locator specified is empty ";
				System.out.println(warn);
				driver.log.error(warn);
				resultDetails.setErrorMessage(warn);
				return;
			}

			By locator = WebDriverUtils.locatorToByObj(webdriver,getFieldFromRepo(fieldText.substring(3)));

			if (locator == null) {
				driver.log.error(" Element " + fieldName+ " not found ");
				resultDetails.setErrorMessage(" Element " + fieldName+ " not found ");
				return;
			}

			webdriver.findElement(locator).clear();
			resultDetails.setFlag(true);

		} catch (Exception e) {
			driver.log.error(" Unable to clear field :: "	+ fieldName);
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(" Unable to clear field :: "	+ fieldName);
			return;
		}


	}

	/**
	 * Method to clear & type into a text field
	 */

	public void clearEnter(WebDriver webdriver, String fieldText, String value, String fieldName) {

		try {

			if(fieldText.length() < 4){
				String warn = " The locator must be specified as TXT locator or EDT locator  ";
				System.out.println(warn);
				driver.log.error(warn);
				resultDetails.setErrorMessage(warn);
				return;
			}

			value = driver.utils.getValue(value);

			if(value.equals("")){
				String warn = " The value specified is empty ";
				System.out.println(warn);
				driver.log.error(warn);
				resultDetails.setErrorMessage(warn);
				return;
			}

			if(fieldText.substring(0,3).equals("BTN")){
				String warn = " The locator must not be specified as type BTN locator for clearenter ";
				System.out.println(warn);
				driver.log.error(warn);
				resultDetails.setErrorMessage(warn);
				return;
			}

			if(getFieldFromRepo(fieldText.substring(3)).equals("")){
				String warn = " The locator specified is empty ";
				System.out.println(warn);
				driver.log.error(warn);
				resultDetails.setErrorMessage(warn);
				return;
			}

			By locator = WebDriverUtils.locatorToByObj(webdriver,getFieldFromRepo(fieldText.substring(3)));

			if (locator == null) {
				driver.log.error(" Element " + fieldName+ " not found ");
				resultDetails.setErrorMessage(" Element " + fieldName+ " not found ");
				return;
			}

			webdriver.findElement(locator).clear();

		} catch (Exception e) {
			driver.log.error(" Unable to clear field :: "	+ fieldName);
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(" Unable to clear field :: "	+ fieldName);
			return;
		}

		enter(webdriver, fieldText, value, fieldName);
	}

	/**
	 * Method to perform verifyContinue
	 */

	private void verifyContinue(WebDriver webdriver, String fieldText, String value, String fieldName) {

		driver.log.info(" Performing verify Continue ");
		verify(webdriver, fieldText, value, fieldName);
		String tempwarn = resultDetails.getWarningMessage();
		String temperror = resultDetails.getErrorMessage();
		resultDetails.setWarningMessage(tempwarn + "   " + temperror);
		resultDetails.setFlag(true);
	}

	/**
	 * Method to perform a mouse hover operation on the given Element
	 */

	public void mouseOver(WebDriver webdriver, String fieldText,String fieldName) {

		if (fieldName.equalsIgnoreCase(""))
			fieldName = fieldText;
		driver.log.info(" Performing mouse hover on " + fieldName);
		resultDetails.setComment(" Performing mouse hover on " + fieldName);

		fieldText = getFieldFromRepo(fieldText);

		if(fieldText.equals("")){
			String warn = " The Field Locator to perform mouseover is specified empty ";
			System.out.println(warn);
			driver.log.error(warn);
			resultDetails.setErrorMessage(warn);
			return;
		}

		By locator = WebDriverUtils.locatorToByObj(webdriver, fieldText);

		if (locator == null) {
			driver.log.error(" Element :: " + fieldName	+ " is not found ");
			resultDetails.setErrorMessage(" Element :: " + fieldName	+ " is not found ");
			return;
		}

		try {

			WebElement element = webdriver.findElement(locator);
			Actions builder = new Actions(webdriver);
			builder.moveToElement(element).build().perform();

			resultDetails.setFlag(true);

		} catch (Exception e) {
			driver.log.error(" Unable to perform mouse over on " + fieldName);
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(" Unable to perform mouse over on " + fieldName);
			return;
		}
	}

	/**
	 * Method to perform goBack operation in the browser
	 */

	public void goBack(WebDriver webdriver) {

		resultDetails.setComment(" Performing go back operation in the browser");
		driver.log.info(" Performing go back operation in the browser");
		try {
			webdriver.navigate().back();
			WebDriverUtils.waitForPageToLoad(webdriver, "10000");
			resultDetails.setFlag(true);
		} catch (Exception e) {
			driver.log.error(" Exception in executing GOBACK");
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(" Exception in executing GOBACK");
		}
	}

	/**
	 * Method to wait for a particular element
	 */

	private void waitForElement(WebDriver webdriver, String field, String waittime, String fieldName) {
		driver.log.info(" Performing wait for element ");
		if(field.length() < 4){
			System.out.println(" DataField value is not as expected ");
			driver.log.error(" DataField value is not as expected ");
			resultDetails.setErrorMessage(" DataField value is not as expected ");
			return;
		}

		String fieldType = field.substring(0, 3);
		field = field.substring(3);

		if (fieldName.equalsIgnoreCase(""))
			fieldName = field;

		field = getFieldFromRepo(field);

		if(field.equalsIgnoreCase("")){
			driver.log.error(" Field provided is empty ");
			System.out.println(" Field provided is empty ");
			resultDetails.setErrorMessage(" Field provided is empty ");
			return;
		}

		int seconds = 60;

		try {
			if (!waittime.equalsIgnoreCase("")) {
				seconds = Integer.parseInt(waittime);
				seconds = seconds / 1000;
			}
		} catch (Exception e) {

			driver.log.warn(e.getMessage());
			String warn = " Encountered exception while handling waititme :: " + waittime + " Using default";
			driver.log.warn(warn);
			System.out.println(warn);
		}

		resultDetails.setComment("Waiting for " + fieldName);
		driver.log.info(" Waiting for " + fieldName);
		try {

			WaitForFields wff = WaitForFields.valueOf(fieldType.toUpperCase());

			switch (wff) {

			case BTN:
			case IMG:
			case TXT:
			case COB:
			case XPH:
			case LNK:

				resultDetails.setComment("Waiting for the element " + fieldName);

				while (seconds >= 1) {
					try {

						if (WebDriverUtils.isElementPresent(webdriver, field)) {
							resultDetails.setFlag(true);
							return;
						}
					} catch (Exception e) {}
					seconds--;
					sleep(1000);
				}

				System.out.println(fieldName + "   :: Element not Found");
				resultDetails.setErrorMessage(fieldName	+ "   :: Element not Found");
				driver.log.error(fieldName	+ "   :: Element not Found");

				break;

			case TTL:

				resultDetails.setComment("Waiting for the title to be " + field);
				driver.log.info(" Waiting for the title to be " + field);
				field = driver.utils.getValue(field);

				if(field.equalsIgnoreCase("")){
					driver.log.error(" Title provided is empty ");
					resultDetails.setErrorMessage(" Title provided is empty ");
					return;
				}

				while (seconds >= 1) {
					if (field.equalsIgnoreCase(webdriver.getTitle())) {
						resultDetails.setFlag(true);
						return;
					}
					sleep(1000);
					seconds--;
				}
				driver.log.error(fieldName + "   :: Page with title not Found");
				System.out.println(fieldName + "   :: Page with title not Found");
				resultDetails.setErrorMessage(fieldName + "   :: Page with title not Found");

				break;

			case MSG:

				resultDetails.setComment("Waiting for the Message to appear " + field.toLowerCase() + " upto " + waittime	+ " seconds");

				field = driver.utils.getValue(field);

				if(field.equalsIgnoreCase("")){
					driver.log.error(" Text provided is empty ");
					resultDetails.setErrorMessage(" Text provided is empty ");
					return;
				}

				while (seconds >= 1) {
					if (webdriver.getPageSource().toLowerCase().contains(field.toLowerCase())) {
						resultDetails.setFlag(true);
						return;
					}
					sleep(1000);
					seconds--;
				}
				driver.log.error(fieldName + "   :: Text not Found");
				System.out.println(fieldName + "   :: Text not Found");
				resultDetails.setErrorMessage(fieldName	+ "   :: Text not Found");
				break;
			}
		} catch (IllegalArgumentException e) {
			driver.log.error(" For Waititme action type the data field should not be " + fieldType);
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage("For Waititme action type the data field should not be " + fieldType);
		} catch (Exception e) {
			driver.log.error(" Encountered Exception in executeing waitfor on " +  fieldName);
			driver.log.error(e.getMessage());
			resultDetails .setErrorMessage("Encountered Exception in executeing waitfor on " +  fieldName);
		}
	}

	/**
	 * Method to verify whether an element is Enabled / Disabled
	 */

	private void isEnabledorDisabled(WebDriver webdriver, String fieldText, String fieldName, boolean enabled) {

		if(fieldText.length() < 4 ){
			String warn = " DataFields are not in expected format ";
			System.out.println(warn);
			driver.log.error(warn);
			resultDetails.setErrorMessage(warn);
			return;
		}

		String fieldType = fieldText.substring(0, 3);

		String field = fieldText.substring(3, fieldText.length());

		if (fieldName.equalsIgnoreCase(""))
			fieldName = field;

		field = getFieldFromRepo(field);

		if(field.equals("")){
			String warn = " Field Locator returned empty value ";
			System.out.println(warn);
			driver.log.error(warn);
			resultDetails.setErrorMessage(warn);
			return;
		}

		By locator = WebDriverUtils.locatorToByObj(webdriver, field);

		String msg = enabled ? " Enabled" : " Disabled";

		driver.log.info(" Verifying that " + fieldName + " is " + msg);
		resultDetails.setComment("Verifying that " + fieldName + " is " + msg);

		if (locator == null) {
			driver.log.error(" Unable to locate element ::"	+ fieldName);
			resultDetails.setErrorMessage("Unable to locate element ::"	+ fieldName);
			return;
		}

		try {
			DataFields dfs = DataFields.valueOf(fieldType.toUpperCase());
			switch (dfs) {

			case XPH:
			case BTN:
			case IMG:
				boolean isEnabled = webdriver.findElement(locator).isEnabled();

				if (isEnabled != enabled) {
					driver.log.error(" Element : " + fieldName + " is not " + msg);
					resultDetails.setErrorMessage(" Element : " + fieldName + " is not " + msg);
					return;
				}
				resultDetails.setFlag(true);
				break;
			}
		} catch (IllegalArgumentException e) {
			driver.log.error(" Data Fields for Disabled / Enabled must be XPH");
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(" Data Fields for Disabled / Enabled must be XPH");
		} catch (Exception e) {
			driver.log.error(" Unable to verify whether " + fieldName + " is Enabled / Disabled");
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(" Unable to verify whether " + fieldName + " is Enabled / Disabled");
		}
	}

	/**
	 * Method to Select a window based on title / Index
	 */

	public void selectWindow(WebDriver webdriver, String fieldText, String value) {

		try {

			value = driver.utils.getValue(value);

			driver.log.info(" Selecting window with title " + value);
			resultDetails.setComment("Selecting window with title " + value);

			WindowFields wff = WindowFields.valueOf(fieldText.toUpperCase());

			switch (wff) {

			case TTL:

				sleep(5000);

				if ( value.equalsIgnoreCase("") ) {
					driver.log.info(" Selecting default window ");
					resultDetails.setComment(" Selecting default window ");
					webdriver.switchTo().window(webdriver.getWindowHandle());
					resultDetails.setFlag(true);
					return;
				}

				if (!WebDriverUtils.selectWindow(webdriver, value)) {
					driver.log.error(" Title not found " + value);
					resultDetails.setErrorMessage(" Title not found " + value);
					return;
				}

				webdriver.manage().window().maximize();
				resultDetails.setFlag(true);

				break;
			}
		} catch (IllegalArgumentException e) {
			driver.log.error(" For Select Window Field Types must be TTL");
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(" For Select Window Field Types must be TTL");
		} catch (Exception e) {
			driver.log.error(" Exception in executing SELECTWINDOW ");
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(" Exception in executing SELECTWINDOW ");
		}
	}

	/**
	 * Method to close a window based on Title
	 */

	public void closeWindow(WebDriver webdriver, String fieldText, String value) {
		driver.log.info(" Performing close window operation");
		value = driver.utils.getValue(value);

		if (value.equalsIgnoreCase("")) {
			driver.log.error(" Window title is not defined ");
			resultDetails.setErrorMessage(" Window title is not defined ");
			return;
		}

		resultDetails.setComment(" Closing window with title " + value);

		String parentWindow = webdriver.getTitle();

		try {

			resultDetails.setComment("Closing window with title " + value);

			WindowFields wff = WindowFields.valueOf(fieldText.toUpperCase());

			switch (wff) {

			case TTL:

				WebDriverUtils.selectWindow(webdriver, value);
				webdriver.close();
				WebDriverUtils.selectWindow(webdriver, parentWindow);

				webdriver.manage().window().maximize();
				resultDetails.setFlag(true);

				break;
			}
		} catch (IllegalArgumentException e) {
			driver.log.error(" For Closewindow FieldTypes must be TTL");
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(" For Closewindow FieldTypes must be TTL");
		} catch (Exception e) {
			driver.log.error(" Exception in executing CLOSEWINDOW ");
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(" Exception in executing CLOSEWINDOW ");
		}
	}

	/**
	 * Method to get the field form Repository
	 */

	public String getFieldFromRepo(String field) {

		String obj = field.toLowerCase();

		if (obj.startsWith("obj:")) {

			String temp = obj.substring(4);

			obj = driver.objRepo.getProperty(temp,temp);

			System.out.println(" Field = " + obj);
			driver.log.info(" Field = " + obj);
			return obj;
		}

		System.out.println(" Field = " + field);
		return field;
	}

	/**
	 * Method to select a frame by it's name / index / id
	 */

	private void selectFrame(WebDriver webdriver, String value, String fieldName) {

		int index = -1;
		sleep(3000);


		resultDetails.setComment("Selecting frame with value : " + value);
		driver.log.info(" Selecting frame with value : " + value);

		if (value.equalsIgnoreCase("")) {
			driver.log.error(" Frame id / name / index is not specified");
			resultDetails.setErrorMessage(" Frame id / name / index is not specified");
			return;
		}

		if (value.toLowerCase().startsWith("index=")) {
			try {
				System.out.println(" Index = " + value.substring(6));
				index = Integer.parseInt(value.substring(6));
			} catch (Exception e) {
				driver.log.error(" Invalid index is specified");
				driver.log.error(e.getMessage());
				resultDetails.setErrorMessage(" Invalid index is specified");
				return;
			}
		}

		if(value.startsWith("//")||value.startsWith("css=")||value.startsWith("xpath="))
		{
			WebElement ele=null;
			if(!value.startsWith("css="))
				ele=webdriver.findElement(By.xpath(value));
			else
				ele=webdriver.findElement(By.cssSelector(value));

			webdriver.switchTo().frame(ele);
			resultDetails.setFlag(true);
			return;

		}


		try {

			if (index != -1) {
				driver.log.info(" Selecting frame with index " + index);
				resultDetails.setComment(" Selecting frame with index " + index);
				webdriver.switchTo().frame(index);
				resultDetails.setFlag(true);
				return;
			}
			driver.log.info(" Selecting frame with id / name " + value);
			resultDetails.setComment(" Selecting frame with id / name " + value);
			webdriver.switchTo().frame(value);
			resultDetails.setFlag(true);
			return;

		} catch (NoSuchFrameException e) {
			driver.log.error(" No such frame found" + fieldName);
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(" No such frame found" + fieldName);
		} catch (Exception e) {
			driver.log.error(" Exception in executing SELECTFRAME on " + fieldName);
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(" Exception in executing SELECTFRAME on " + fieldName);
		}
	}

	/**
	 * Method to perform execute test case
	 */

	public void executetestcase(WebDriver webdriver, String fieldText,String value) {

		int testcaseid = -1;

		if (driver.inExecute) {
			driver.log.error(" Already in a execute function. Can't proceed further");
			resultDetails.setErrorMessage("Already in a execute function. Can't proceed further");
			return;
		}

		if(driver.inLoop){
			driver.log.error(" Already in a Loop. Can't proceed further");
			resultDetails.setErrorMessage("Already in a Loop. Can't proceed further");
			return;
		}

		driver.inExecute = true;

		String tcid[] = value.split(":");

		HashMap<Integer, TestDataDetails> TestData = new HashMap<Integer, TestDataDetails>();
		TestData = driver.impxl.readTestData(tcid[0]);

		try {
			testcaseid = Integer.parseInt(tcid[0]);
		} catch (Exception e) {}


		if (fieldText.toLowerCase().startsWith("setup")) {

			String temp = fieldText.substring(5);

			int n = 1;

			if (temp.startsWith("#RND")) {
				while (true) {
					n = (int) (Math.random() * (driver.setup_TestDataCounter.get(tcid[0].toLowerCase())));
					if (n != 0)
						break;
				}
			} else {
				temp = temp.substring(1);
				n = Integer.parseInt(temp);
			}

			driver.setup_data = driver.setup_TestData.get(tcid[0].toLowerCase() +"#"+n);
		}

		if (testcaseid == -1) {
			int to = TestData.size();
			int from = 1;
			if(tcid.length == 2){

				String range[] = tcid[1].split("-");

				if (range[1].equalsIgnoreCase("End"))
					to = TestData.size();
				else
					to = Integer.parseInt(range[1]);

				from = Integer.parseInt(range[0]);
			}
			resultDetails.setWarningMessage(" Executing Setup TestCase :: " + tcid[0]);
			driver.log.info(" Executng "+ tcid[0]+ " from " + from + " to " + to);
			resultDetails = driver.executeSteps(from, to, 1, TestData,resultDetails);
		}

		else {

			try {
				String range[] = tcid[1].split("-");
				int to = TestData.size();
				if (range[1].equalsIgnoreCase("End"))
					to = TestData.size();
				else
					to = Integer.parseInt(range[1]);

				int from = Integer.parseInt(range[0]);

				resultDetails.setWarningMessage(" Executing TestCase :: "+ tcid[0] + " from " + from + " to " + to);
				driver.log.info(" Executng "+ tcid[0]+ " from " + from + " to " + to);
				resultDetails = driver.executeSteps(from, to, 1, TestData,resultDetails);
			} catch (Exception e) {
				driver.log.error(" Execute Test case failed.");
				driver.log.error(e.getMessage());
				System.out.println("exception value : " + e.getMessage());
				resultDetails.setFlag(false);
				resultDetails.setErrorMessage("Execute Test case failed.");
			}
		}
	}








	/**
	 * Method to store a value from database into hashmap
	 */

	private void storeDBValue(String query, String key, String fieldName) {

		try {

			resultDetails.setComment("Storing " + fieldName + " from DB with" + key);

			if(query.length() < 4 ){
				String temp = (" The DataFields must be specifed as QRYselect_query ");
				System.out.println(temp);
				driver.log.error(temp);
				resultDetails.setErrorMessage(temp);
				return;	
			}

			String values [] = key.split("::");

			if(values.length !=2){
				String temp = (" Insufficient data values, For DBV format is column_heading::key");
				System.out.println(temp);
				driver.log.error(temp);
				resultDetails.setErrorMessage(temp);
				return;
			}

			key = values[1];

			if(key.equals("")){
				String temp = (" Key value is empty ");
				System.out.println(temp);
				driver.log.error(temp);
				resultDetails.setErrorMessage(temp);
				return;
			}

			if(values[0].equals("")){
				String temp = (" Column to be stored is empty ");
				System.out.println(temp);
				driver.log.error(temp);
				resultDetails.setErrorMessage(temp);
				return;
			}

			query = query.substring(3);

			if(query.equals("")){
				String temp = (" Query is empty ");
				System.out.println(temp);
				driver.log.error(temp);
				resultDetails.setErrorMessage(temp);
				return;
			}

			Properties miscProps = driver.miscProps;
			String url = miscProps.getProperty("appDB_URL",null);
			String jdbcDriver = miscProps.getProperty("appDB_driver",null);
			String userName = miscProps.getProperty("appDB_userName",null);
			String password = miscProps.getProperty("appDB_password",null);
			String dbName = miscProps.getProperty("app_DatabaseName",null);

			Class.forName(jdbcDriver);
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			Statement st=conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			String msg = null;

			while(rs.next()){
				msg = rs.getString(values[0]);
				break;
			}

			if(msg == null){
				String temp = (" Null Value is read for " + fieldName + " from DB ");
				System.out.println(temp);
				driver.log.error(temp);
				resultDetails.setErrorMessage(temp);
				return;
			}

			resultDetails.setComment("Storing " + msg + " from DB with" + key );
			driver.hMap.put(key, msg);
			resultDetails.setFlag(true);

		} catch (ClassNotFoundException e) {
			driver.log.error(" Unable to find the class required  ");
			driver.log.error(e.getMessage());
			//System.out.println(" SQLException while querying the Database ");
			System.out.println(e.getMessage());
			resultDetails.setErrorMessage(" Unable to find the class required ");
			resultDetails.setFlag(false);
		} catch (SQLException e) {
			driver.log.error(" SQLException while querying the Database ");
			driver.log.error(e.getMessage());
			System.out.println(" SQLException while querying the Database ");
			System.out.println(e.getMessage());
			resultDetails.setErrorMessage(" SQLException while querying the Database ");
			resultDetails.setFlag(false);
		} catch (Exception e) {
			String temp = " Exception while performing storevalue for DBV  ";
			resultDetails.setFlag(false);
			driver.log.error(temp);
			resultDetails.setErrorMessage(temp);
			driver.log.error(e.getMessage());
			System.out.println(temp);
			System.out.println(e.getMessage());
		}
	} 
	/**
	 * Method to click on Go Key In Keyboard of Android
	 *//*

	public void androidKey(WebDriver webdriver,String value) {
		try {
			driver.log.info(" clicking on Go Key In Keyboard of Android ");
			//resultDetails.setComment("Deleteing cookies & loading the URL  :: "	+ driver.appUrl);
			String keyval = driver.utils.getValue(value);
			HashMap<String, Integer> keycodes = new HashMap<String, Integer>();
			keycodes.put("keycode", Integer.parseInt(keyval));
            ((JavascriptExecutor)webdriver).executeScript("mobile: keyevent", keycodes);
            webdriver.manage().timeouts().implicitlyWait(120, TimeUnit.SECONDS);
            resultDetails.setFlag(true);
		} catch (Exception e) {
			driver.log.error(" Encountered Exception while performing clickGoKey in Android  ");
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage("Unable to click on Go Key in the Android Keyboard");
		}
	}*/
	/*	
	 *//**
	 * Method to Clear Cache of the native application and launch the Application
	 *//*

	public void clearCache() {
		try {
			driver.log.info(" Deleteing Cache,data and relaunching the NativeAPP ");
			//resultDetails.setComment("Deleteing cookies & loading the URL  :: "	+ driver.appUrl);
			Runtime rt = Runtime.getRuntime();
			//androidProps.getProperty("MobileAppPackageName")
			//com.webmd.android
			Process proc = rt.exec("adb shell pm clear"+"app_pac");
			resultDetails.setFlag(true);
		} catch (Exception e) {
			driver.log.error(" Encountered Exception while performing clear Cache ");
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage("Unable to clear cache & Relaunch the APP");
			return;
		}
	}*/

	/**
	 * Method to Swipe the native application pages
	 *//*
	public void androidSwipe(WebDriver webdriver,String value) {
	try {
		Thread.sleep(10000);
		driver.log.info("Performing Swipe in the NativeAPP Pages");
		int j = Integer.parseInt(value);
	  for(int i=0;i<=j;i++)
      {
    	JavascriptExecutor js = (JavascriptExecutor) webdriver;
      	HashMap<String, Double> swipeObject = new HashMap<String, Double>();
      	swipeObject.put("startX", 0.95);
      	swipeObject.put("startY", 0.5);
      	swipeObject.put("endX", 0.05);
      	swipeObject.put("endY", 0.5);
      	swipeObject.put("duration", 1.8);
      	js.executeScript("mobile: swipe", swipeObject);

      }
	  resultDetails.setFlag(true);
	}
	catch (Exception e) {
		driver.log.error(" Encountered Exception while performing Swipe ");
		driver.log.error(e.getMessage());
		resultDetails.setErrorMessage("Unable to perform Swipe Pages of the APP");
		return;
	}
	}



	  *//**
	  * Method to Wait For Element of native application 
	  *//*
	public void androidWaitForElement(WebDriver webdriver,String field) {
	try {

		driver.log.info(" Waiting for the Object to load in NativeAPP");
		//locator = WebDriverUtils.locatorToByObj(webdriver,getFieldFromRepo(tempField));
		//int a = Integer.parseInt(webdriver.findElement(WebDriverUtils.locatorToByObj(webdriver, field)).getSize());
		while(webdriver.findElements(WebDriverUtils.locatorToByObj(webdriver, field)).size()!=0)
		 {
			 Thread.sleep(5000);
		 }
	  resultDetails.setFlag(true);
	}
	catch (Exception e) {
		driver.log.error(" Encountered Exception while Waitng for Element to load ");
		driver.log.error(e.getMessage());
		resultDetails.setErrorMessage("Unable to perform Waitng for Element to load in the APP");
		return;
	}
	}


	   *//**
	   * Method to Scroll the native application pages
	   *//*
	public void androidScroll(WebDriver webdriver) {
	try {

		driver.log.info("Performing Scroll in the NativeAPP");
		JavascriptExecutor js = (JavascriptExecutor) webdriver;
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

	  resultDetails.setFlag(true);
	}
	catch (Exception e) {
		driver.log.error(" Encountered Exception while performing Scroll ");
		driver.log.error(e.getMessage());
		resultDetails.setErrorMessage("Unable to perform Scroll in the APP");
		return;
	}
	}*/

	/**
	 * Method to perform dragAndDrop operation
	 */

	public void dragAndDrop(WebDriver webdriver, String fieldText, String fieldName){

		String locators[]=fieldText.split(";");	

		resultDetails.setComment(" Performing drag and drop action over " + fieldName);

		if(locators.length !=2 ){
			String temp = " Invalid no. of locators specfied. Format is src;dest ";
			System.out.println(temp);
			driver.log.error(temp);
			resultDetails.setErrorMessage(temp);
			resultDetails.setFlag(false);
			return;
		}

		locators[0] = getFieldFromRepo(locators[0]);
		locators[1] = getFieldFromRepo(locators[1]);

		if(locators[0].equals(locators[1])){
			String temp = " Source and destination points are same ";
			System.out.println(temp);
			driver.log.error(temp);
			resultDetails.setErrorMessage(temp);
			resultDetails.setFlag(false);
			return;
		}

		if(locators[0].equals("") || locators[1].equals("")){
			String temp = " One of the target fields are resolved to be empty ";
			System.out.println(temp);
			driver.log.error(temp);
			resultDetails.setErrorMessage(temp);
			resultDetails.setFlag(false);
			return;
		}

		By src = WebDriverUtils.locatorToByObj(webdriver, locators[0]);

		if(src == null ){
			driver.log.error("Source element " + locators[0] +" not found");
			resultDetails.setErrorMessage("Source element " + locators[0] +" not found");
			resultDetails.setFlag(false);
			return;
		}

		By destn = WebDriverUtils.locatorToByObj(webdriver, locators[1]);

		if(destn == null ){
			driver.log.error("Destination element " + locators[1] +" not found");
			resultDetails.setErrorMessage("Destination element " + locators[1] +" not found");
			resultDetails.setFlag(false);
			return;
		}

		try {
			new Actions(webdriver).dragAndDrop(webdriver.findElement(src),webdriver.findElement(destn));
			resultDetails.setFlag(true);
		}catch(Exception e) {
			driver.log.error(" Exception while performing drag and drop operation");
			resultDetails.setErrorMessage(" Exception while performing drag and drop operation");
			System.out.println(e.getMessage());
		}
	}
}