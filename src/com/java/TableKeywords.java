package com.java;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import com.java.objects.ResultDetails;
import java.util.function.Function;

public class TableKeywords {


	public enum DataFields {
		TTL, TXT, XPH, RDB, CHK, COB, SLB, BTN, IMG, LNK, CNF, MSG, GET, TBL, ALT, CBS
	};
	public enum OrderBy {
		ASC,DSC
	};
	
	TestType tt;

	public TableKeywords(TestType tt) {
		this.tt = tt;
	}


	private ResultDetails resultDetails = new ResultDetails();
	
	
	


	/**
	 * Method to verify the column value of a table based on another column value
	 * @param driver 
	 * @return 
	 */
	
	public ResultDetails columnDataCheck(SeleniumDriver driver, WebDriver webdriver, String field,String value, String fieldName) {
		
		if(field.length() < 4){
			String warn = " Locator is not specified in required format ";
			System.out.println(warn);
			driver.log.warn(warn);
			resultDetails.setErrorMessage(warn);
			return resultDetails;
		}
		
		String fieldType = field.substring(0,3);
		
		field = field.substring(3);
		field=tt.driver.utils.getValue(field);
		value = tt.driver.utils.getValue(value);
		String [] dataValues;
		
		try{
			
			DataFields dfs = DataFields.valueOf(fieldType);
			
			switch(dfs){
			
				case TBL:
					
					String tblField="//table[" + field + "]";
					if(field.startsWith("//"))
					{
						tblField=field;
					}
					
					
					if(fieldName.equalsIgnoreCase("")){
					
					}
						fieldName = field;
					
					resultDetails.setComment("Verifying value of one column based on other column in " + fieldName);
							
					dataValues = value.split(":");
					
					if(dataValues.length <= 3){
						String temp =  " Invalid format of Data Value. \n It should be Row_start:col_no:data:target_col_no>:Expected_value ";
						resultDetails.setErrorMessage(temp);
						driver.log.error(temp);
						System.out.println(temp);
						return resultDetails;
					}
				
					String row_start = dataValues[0];
					String column_no = dataValues[1];
					String column_data = tt.driver.utils.getValue(dataValues[2]);
					String column_no_tbv = dataValues[3];
					String column_data_tbv =tt.driver.utils.getValue(dataValues[4]);
					
					try{
					WebDriverWait wait = new WebDriverWait(webdriver, 5);
					wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(tblField+"//tr")));
					//	Thread.sleep(6000);
					}
					catch(Exception e)
					{						
					}
					int rowCount = webdriver.findElements(By.xpath(tblField+"//tr")).size();
					
					if(rowCount == 0){
						driver.log.error(" No Rows found for table in " + field);
						resultDetails.setErrorMessage(" No Rows found for table in " + field);
						return resultDetails;
					}
					
					int	start_row = Integer.parseInt(row_start);
					
					int ecnt = webdriver.findElements(By.xpath(tblField+"//tr[" + start_row + "]//td")).size();
					
					for(int i = start_row; i <= rowCount; i++){
						
						int acnt = webdriver.findElements(By.xpath(tblField+"//tr[" + i + "]//td")).size();
						
						if(ecnt > acnt ){
							continue;
						}
						
						String loc = tblField+"//tr[" + i + "]//td[" + column_no + "]";
						By locator = WebDriverUtils.locatorToByObj(webdriver, loc);
						
						if(locator == null ){
							driver.log.error("Unable to find element " + loc );
							resultDetails.setErrorMessage("Unable to find element " + loc );
							return resultDetails;
						}
						
						String text = webdriver.findElement(locator).getText();
						
						if(text.equalsIgnoreCase(column_data)){
							
							loc = tblField+"//tr[" + i + "]//td[" + column_no_tbv + "]";
							locator = WebDriverUtils.locatorToByObj(webdriver, loc);
							text = webdriver.findElement(locator).getText();
							
							if(text.equalsIgnoreCase(column_data_tbv)){
								driver.hMap.put(column_data, i+"");
								resultDetails.setFlag(true);
								return resultDetails;
							}
						}
					}
					driver.log.error(" Unable to find data for fieldName " + fieldName);
					resultDetails.setErrorMessage(" Unable to find data for fieldName " + fieldName);
				break;
				
				case XPH:
					
					String fields[] = field.split("::");

					if (fields.length < 3) {
						String warn = " Field Locators are not specified in expected manner ";
						System.out.println(warn);
						driver.log.warn(warn);
						resultDetails.setErrorMessage(warn);
						return resultDetails;
					}

					dataValues = value.split(":");

					if (dataValues.length < 3) {
						String warn = " DataValues are not specified in expected manner ";
						System.out.println(warn);
						driver.log.error(warn);
						resultDetails.setErrorMessage(warn);
						return resultDetails;
					}

					String parent = fields[0];
					String tbvsrc = fields[1];
					String tbvdest = fields[2];

					int start_div = Integer.parseInt(dataValues[0]);
					String tbvsrcdata = dataValues[1];
					String tbvdestdata = dataValues[2];

					int divCount = webdriver.findElements(By.xpath(parent)).size();

					if (divCount == 0 || divCount < start_div ) {
						String warn = " No. of elements found are zero (or) less than required ";
						System.out.println(warn);
						driver.log.error(warn);
						resultDetails.setErrorMessage(warn);
						return resultDetails;
					}

					for (int i = start_div; i <= divCount; i++) {

						String loc = parent + "[" + i + "]" + tbvsrc;

						By locator = WebDriverUtils.locatorToByObj(webdriver, loc);

						if (locator == null) {
							driver.log.info(" Unable to find element " + loc);
							System.out.println("Unable to find element " + loc);
							continue;
						}

						String text = webdriver.findElement(locator).getText();

						if (text.equalsIgnoreCase(tbvsrcdata)) {
							
							System.out.println(text);
							
							loc = parent + "[" + i + "]" + tbvdest;
							locator = WebDriverUtils.locatorToByObj(webdriver, loc);
							text = webdriver.findElement(locator).getText();

							if (text.equalsIgnoreCase(tbvdestdata)) {
								resultDetails.setFlag(true);
								return resultDetails;
							}
						}
					}
					break;
			}
			
		}catch(IllegalArgumentException e){
			driver.log.error(" For Column Data Check , data field is of type TBL");
			resultDetails.setErrorMessage(" For Column Data Check , data field is of type TBL");
			return resultDetails;
		}catch(Exception e){
			driver.log.error(" Exception in executing Column Data Check");
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(" Exception in executing Column Data Check");
			System.out.println(e.getMessage());
			return resultDetails;
		}
		return resultDetails;
	}


	
	
	
	
	/**
	 * Method to verify the order of given data
	 */

	public ResultDetails verifySort(SeleniumDriver driver,WebDriver webdriver, String field, String value, String fieldName) {
		
		try{
			
			String[] actual = new String[1];
			String loc = "";
			int arrLength = 0;
			
			String order = "ASC";
			String format = "STR";
			
			String fieldType = field.substring(0, 3).toUpperCase();
			
			field = field.substring(3);
			
			value = tt.driver.utils.getValue(value);
			field = tt.getFieldFromRepo(field);
			field=tt.driver.utils.getValue(field);
	
			String[]  dataValues = value.split(":");
			
			int starting_row = 1;
			
			DataFields dfs = DataFields.valueOf(fieldType);
			
			switch(dfs){
			
				case TBL:
					String tblField="//table[" + field + "]";
					if(field.startsWith("//"))
					{
						tblField=field;
					}
					if(dataValues.length <= 3){
						String temp =  " Invalid format of Data Value. \n It  should be start_row_no:col_no:order_by:format";
						resultDetails.setErrorMessage(temp);
						System.out.println(temp);
						driver.log.error(temp);
						return resultDetails ;
					}
					
					starting_row = Integer.parseInt(dataValues[0]);
					String column = dataValues[1];
					order = dataValues[2];
					format = dataValues[3];
					loc = tblField+"//tr";
					try{
						WebDriverWait wait = new WebDriverWait(webdriver, 5);
						wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(tblField+"//tr")));
						//Thread.sleep(6000);
						}
						catch(Exception e)
						{						
						}
					int count = webdriver.findElements(By.xpath(loc)).size();
					
					if ( count < starting_row ){
						resultDetails.setErrorMessage(" Insufficient number of rows  - " + count + " than expected " + starting_row);
						driver.log.error(" Insufficient number of rows  - " + count + " than expected " + starting_row);
						return resultDetails ;
					}
					actual = new String[count-starting_row+1];
					
					int ecnt = webdriver.findElements(By.xpath(tblField+"//tr[" + starting_row + "]//td")).size();
					
					for( int i = starting_row; i <= count; i++){
						
						int acnt = webdriver.findElements(By.xpath(tblField+"//tr[" + i + "]//td")).size();
						
						if(ecnt > acnt ){
							continue;
						}
						
						loc = tblField+"//tr[" + i + "]//td["+ column + "]"; 
						actual[arrLength++] = (webdriver.findElement(By.xpath(loc)).getText()).trim(); 
					}
					
					break;
			
				case XPH:
			
					String [] dataFields = field.split("::");
					
					if(dataFields.length <= 1){
						String temp =  " Invalid format of Data Field . \n It  should be Parent::Child";
						resultDetails.setErrorMessage(temp);
						driver.log.error(temp);
						System.out.println(temp);
						return resultDetails ;
					}
					
					if(dataValues.length <= 2){
						String temp =  " Invalid format of Data Value. \n It  should be start_no:order_by:format";
						resultDetails.setErrorMessage(temp);
						driver.log.error(temp);
						System.out.println(temp);
						return resultDetails ;
					}
					starting_row = Integer.parseInt(dataValues[0]);
					order = dataValues[1];
					format = dataValues[2];
					loc = dataFields[0];
					
					int count_xpath = webdriver.findElements(By.xpath(loc)).size();

					actual = new String[count_xpath-starting_row+1];

					for( int i = starting_row;i <= count_xpath; i++){
						loc = dataFields[0]+ "[" +i+ "]" + dataFields[1]; 
						actual[arrLength++] = webdriver.findElement(By.xpath(loc)).getText().trim(); 
					}
					break;
			}
			
			String [] expected = new String [actual.length];
			expected  = Arrays.copyOf(actual,actual.length);

			OrderBy order_by = OrderBy.valueOf(order.toUpperCase());
					
			switch(order_by) {
			
			case ASC:
				
				if (format.equalsIgnoreCase("STR")) {
					Arrays.sort(expected);
					for (int i=0; i<actual.length; i++) {
						System.out.println(actual[i]+" "+expected[i]);
						if(!actual[i].equalsIgnoreCase(expected[i])){
							driver.log.error(" Undefined Order at value " + actual[i]);
							resultDetails.setErrorMessage(" Undefined Order at value " + actual[i]);
							return resultDetails ;
						}
					}
				} else if (format.equalsIgnoreCase("NUM")) {
					try {
						int numExpected[] = new int[expected.length];
						
						for (int i=0; i<actual.length; i++)
							numExpected[i] = Integer.parseInt(expected[i]);
					
						for(int i=0; i<actual.length-1; i++) {
							if(numExpected[i] > numExpected[i+1]){
								driver.log.error(" Undefined Order at value " + actual[i]);
								resultDetails.setErrorMessage(" Undefined Order at value " + actual[i]);
								return resultDetails ;
							}
						}
					} catch(NumberFormatException e) {
						resultDetails.setFlag(false);
						resultDetails.setErrorMessage(e.getMessage());
						
						driver.log.error(" You have given Invalid Number");
						System.out.println("You have given Invalid Number");
						return resultDetails ;
					}
				}else if (format.toUpperCase().startsWith("DATE")) {
					try {
						
						format = format.substring(4);
						
						if(format.equalsIgnoreCase("")){
							format ="MMddyy";
						}
						SimpleDateFormat sdf = new SimpleDateFormat(format);
											
						for(int i=0; i<actual.length-1; i++) {
							
							Date d1 = sdf.parse(expected[i]);
							Date d2 = sdf.parse(expected[i+1]);
							int res = d1.compareTo(d2);
							
							if(res > 0 ){
								driver.log.error(" Undefined Order at value " + actual[i]);
								resultDetails.setErrorMessage(" Undefined Order at value " + actual[i]);
								return resultDetails ;
							}
						}
					} catch(Exception e) {
						resultDetails.setFlag(false);
						driver.log.error(" Exception while verfying order by date");
						resultDetails.setErrorMessage(e.getMessage());
						System.out.println("Exception while verfying order by date");
						return resultDetails ;
					}
				}
					break;
					
			case DSC:
				
				if (format.equalsIgnoreCase("STR")) {
					Arrays.sort(expected);
					
					int j=0;
					
					for (int i=actual.length-1; i>=0; i--) {
					
						System.out.println(actual[j]+" "+expected[i]);
						if(!actual[j].equalsIgnoreCase(expected[i])){
							driver.log.error(" Undefined Order at value " + actual[i]);
							resultDetails.setErrorMessage(" Undefined Order at value " + actual[i]);
							return resultDetails ;
						}
						j++;
					}
				} else if (format.equalsIgnoreCase("NUM")) {
					try {
						int numExpected[] = new int[expected.length];
						
						for (int i=0; i<actual.length; i++)
							numExpected[i] = Integer.parseInt(expected[i]);
					
						for(int i=0; i<actual.length-1; i++) {
							if(numExpected[i] < numExpected[i+1]){
								driver.log.error(" Undefined Order at value " + actual[i]);
								resultDetails.setErrorMessage(" Undefined Order at value " + actual[i]);
								return resultDetails ;
							}
						}
					} catch(Exception e) {
						resultDetails.setFlag(false);
						resultDetails.setErrorMessage(e.getMessage());
						driver.log.error(" You have given Invalid Number");
						System.out.println("You have given Invalid Number");
						return resultDetails ;
					}
				}else if (format.toUpperCase().startsWith("DATE")) {
					try {
						
						format = format.substring(4);
						
						if(format.equalsIgnoreCase("")){
							format ="MMddyy";
						}
						
						SimpleDateFormat sdf = new SimpleDateFormat(format);
											
						for(int i=0; i<actual.length-1; i++) {
							
							Date d1 = sdf.parse(expected[i]);
							Date d2 = sdf.parse(expected[i+1]);
							int res = d1.compareTo(d2);
							
							if(res < 0 ){
								driver.log.error(" Undefined Order at value " + actual[i]);
								resultDetails.setErrorMessage(" Undefined Order at value " + actual[i]);
								return resultDetails ;
							}
						}
					} catch(Exception e) {
						resultDetails.setFlag(false);
						resultDetails.setErrorMessage(e.getMessage());
						
						driver.log.error(" Exception while verfying order by date");
						System.out.println("Exception while verfying order by date");
						return resultDetails ;
					}
				}
					
				break;
			
			}
			
			resultDetails.setFlag(true);
			
		}catch(Exception e){
			driver.log.error(" Exception in executing Verifyorder on "  + fieldName);
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(" Exception in executing Verifyorder on "  + fieldName);
			System.out.println(e.getMessage());
			return resultDetails ;
		}
		return resultDetails;	
	}

	
	/**
	 * Method to perform a verify operation on particular cell in table
	 */
	
	public ResultDetails verifyTableData(SeleniumDriver driver,WebDriver webdriver, String field, String value, String fieldName) {
		
		try {

			if(field.equals("")){
				String warn = " Field is Empty ";
				System.out.println(warn);
				driver.log.error(warn);
				resultDetails.setErrorMessage(warn);
				return resultDetails ;
			}
			
			if(field.length() < 4 ){
				String warn = " Field is Not specified in expected Format";
				System.out.println(warn);
				driver.log.error(warn);
				resultDetails.setErrorMessage(warn);
				return resultDetails ;
			}
		
			resultDetails.setComment("Verifying whether "+ fieldName + " exists within specified table");

			if(fieldName.equalsIgnoreCase(""))
				fieldName = field;
			
			String fieldType = field.substring(0,3);

			DataFields dfs = DataFields.valueOf(fieldType);
			
			boolean click = false;
			field = field.substring(3);
			field=tt.driver.utils.getValue(field);
			value = tt.driver.utils.getValue(value);
			
			String[] datavalue  = value.split(":");
			
			switch(dfs){
			
				case TBL:
									
					if(datavalue.length <= 3){
						String temp =  " Invalid format of Data Value. \n It  should be row_start:col_no:text_to_match:check";
						resultDetails.setErrorMessage(temp);
						driver.log.error(temp);
						System.out.println(temp);
						return resultDetails ;
					}
					String tblField="//table[" + field + "]";
					if(field.startsWith("//"))
					{
						tblField=field;
					}
					
					click = datavalue[3].equalsIgnoreCase("click");
					
					if(click && datavalue.length <= 3){
						String temp =  " Invalid format of Data Value. \n It  should be row_start:col_no:text_to_match:click:column_to_be_clicked";
						resultDetails.setErrorMessage(temp);
						driver.log.error(temp);
						System.out.println(temp);
						return resultDetails ;
					}
					
					datavalue[2] = tt.driver.utils.getValue(datavalue[2]);
					try{
						WebDriverWait wait = new WebDriverWait(webdriver, 5);
						wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(tblField+"//tr")));
						//Thread.sleep(6000);
						}
						catch(Exception e)
						{						
						}
					
					int rowcount = webdriver.findElements(By.xpath(tblField+"//tr")).size();
					System.out.println("No. of rows  = " + rowcount);
						
					int start_row = Integer.parseInt(datavalue[0]);
								
					if(start_row > rowcount){
						driver.log.error(" Required no. of rows are not present. Required :: " +start_row + " Present  ::" + rowcount);
						resultDetails.setErrorMessage(" Required no. of rows are not present. Required :: " +start_row + " Present  ::" + rowcount);
						return resultDetails ;
					}
					
					if(rowcount!=0){
					
						int ecnt = webdriver.findElements(By.xpath(tblField+"//tr[" + start_row + "]//td")).size();
						
						for(int i = start_row; i <= rowcount; i++){
						
							int acnt = webdriver.findElements(By.xpath(tblField+"//tr[" + i + "]//td")).size();
							
							if(ecnt > acnt ){
								continue;
							}
							
							String loc = tblField+"//tr[" + i +"]//td [" + datavalue[1] + "]";
							String actual = webdriver.findElement(By.xpath(loc)).getText();
					
							boolean res = actual.equalsIgnoreCase(datavalue[2]);
					
							if(res){
								resultDetails.setFlag(true);
								resultDetails.setComment("Expected value :: " + actual + "found at row " + i);
								driver.hMap.put(datavalue[2], i+"");
								if(click){
									int click_on = 4;
									if(datavalue.length !=5){
										click_on= 1;
									}
										
								loc = tblField+"//tr[" + i +"]//td[" + datavalue[click_on] + "]";
									try{
										webdriver.findElement(By.xpath(loc+"//*")).click();
										
										System.out.println("Clicked on child element");
										resultDetails.setFlag(true);
									}catch(Exception e){
										try{
											webdriver.findElement(By.xpath(loc)).click();
											System.out.println("Clicked on TD element");
											resultDetails.setFlag(true);
										}catch(Exception e1){
											resultDetails.setFlag(false);
											resultDetails.setErrorMessage(" Unable to find element to be clicked ");
										}
									}
								}
								return resultDetails ;
							}
						}
							String temp =  " No data found for table " + fieldName;
							resultDetails.setErrorMessage(temp);
							System.out.println(temp);
							return resultDetails ;
						}else{
							String temp =  " No rows found for table " + fieldName;
							resultDetails.setErrorMessage(temp);
							System.out.println(temp);
						}
					break;
					
				case XPH:
					
					if(datavalue.length <= 2){
						String temp =  " Invalid format of Data Value. \n It  should be row_start:text_to_match:check";
						resultDetails.setErrorMessage(temp);
						System.out.println(temp);
						return resultDetails ;
					}
					
					click = datavalue[2].equalsIgnoreCase("click");
					
					datavalue[1] = tt.driver.utils.getValue(datavalue[1]);
					
					String dataFields[] = field.split("::");
					
					
					if(click && dataFields.length <= 2){
						String temp =  " Invalid format of Data Field. It should be XPHparent::child::child_to_be_clicked";
						resultDetails.setErrorMessage(temp);
						System.out.println(temp);
						return resultDetails ;
					}
					
					if(dataFields.length <= 1){
						String temp =  " Invalid format of Data Field. It should be XPHparent::child";
						resultDetails.setErrorMessage(temp);
						System.out.println(temp);
						return resultDetails ;
					}
					
					int divcount = webdriver.findElements(By.xpath(dataFields[0])).size();
					System.out.println("No. of rows  = " + divcount);
					
					int start_div = Integer.parseInt(datavalue[0]);
								
					if(start_div > divcount){
						String temp = " Required no. of rows are not present. Required :: " +start_div + " Present  ::" + divcount;
						resultDetails.setErrorMessage(temp);
						driver.log.error(temp);
						System.out.println(temp);
						return resultDetails ;
					}
					
					if(divcount!=0){
						
						for(int i = start_div; i <= divcount; i++){
						
							String loc = dataFields[0]+ "[" + i +"]" + dataFields[1];
							String actual = webdriver.findElement(By.xpath(loc)).getText();
					
							boolean res = actual.equalsIgnoreCase(datavalue[1]);
					
							if(res){
								
								resultDetails.setFlag(true);
								
								resultDetails.setComment("Expected value :: " + actual + "found at row " + i);
								
								driver.hMap.put(datavalue[1], i+"");
								
								if(click){
										
									loc = dataFields[0]+ "[" + i +"]" + dataFields[2];
									try{
										webdriver.findElement(By.xpath(loc)).click();
										System.out.println("Clicked on child element");
										driver.log.info(" Clicked on child element");
										resultDetails.setFlag(true);
									}catch(Exception e){
										try{
											webdriver.findElement(By.xpath(loc)).click();
											System.out.println("Clicked on TD element");
											driver.log.info(" Clicked on TD element");
											resultDetails.setFlag(true);
										}catch(Exception e1){
											resultDetails.setFlag(false);
											driver.log.error(" Unable to find element to be clicked ");
											resultDetails.setErrorMessage(" Unable to find element to be clicked ");
										}
									}
								}
								return resultDetails ;
							}
						}
							String temp =  " No data found for " + fieldName;
							resultDetails.setErrorMessage(temp);
							driver.log.info(temp);
							System.out.println(temp);
							return resultDetails ;
						}else{
							String temp =  " No rows found for " + fieldName;
							resultDetails.setErrorMessage(temp);
							driver.log.error(temp);
							System.out.println(temp);
						}
					break;
			}
			

			
		}catch(IllegalArgumentException e){
			driver.log.error(" For VERIFYTABLEDATA , data field is of type TBL,XPH");
			resultDetails.setErrorMessage(" For VERIFYTABLEDATA , data field is of type TBL,XPH");
			return resultDetails ;
		}catch(Exception e){
			resultDetails.setErrorMessage(" Exception in executing VERIFYTABLEDATA");
			
			driver.log.error(" Exception in executing VERIFYTABLEDATA");
			driver.log.error(e.getMessage());
			System.out.println(e.getMessage());
			return resultDetails ;
		}
		return resultDetails;		
	}

	
	
	/**
	 * Method to verify that whether a table cell exists with given value / not
	 */

	public ResultDetails verifyTableRowExists(SeleniumDriver driver,WebDriver webdriver, String field,String value, String fieldName) {
		
		if(fieldName.equalsIgnoreCase(""))
			fieldName = field;
		
		resultDetails.setComment("Verifying whether "+ fieldName + " exists within specified table");
		
		String fieldType = field.substring(0,3);
		
		field = field.substring(3);
		field=tt.driver.utils.getValue(field);
		value =tt.driver.utils.getValue(value);
		
		String[] dataValue  = value.split(":");
		
		if(dataValue.length <= 3){
			String temp =  " Invalid format of Data Values. \n It  should be row_start_no:col_no:text_to_match:true/false";
			resultDetails.setErrorMessage(temp);
			driver.log.error(temp);
			System.out.println(temp);
			return resultDetails;
		}
		
		int row_no = Integer.parseInt(dataValue[0]);
		String column_no = dataValue[1];
		String data = tt.driver.utils.getValue(dataValue[2]);
		
		value = dataValue[3];
		boolean status = false;
		
		if(value.equalsIgnoreCase("true"))
			status = true;
				
		try{
			
			DataFields dfs = DataFields.valueOf(fieldType);
			
			switch(dfs){
			
				case TBL:
					
					String tblField="//table[" + field + "]";
					if(field.startsWith("//"))
					{
						tblField=field;
					}
					
					String loc = tblField+"//tr";
										
					By locator = WebDriverUtils.locatorToByObj(webdriver, loc);
						
					if(locator == null ){
						System.out.println(" Unable to find element " + fieldName );
						driver.log.error(" Unable to find element " + fieldName );
						resultDetails.setErrorMessage("Unable to find element " + fieldName );
						return resultDetails;
					}
					try{
						WebDriverWait wait = new WebDriverWait(webdriver, 5);
						wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(tblField+"//tr")));
						//Thread.sleep(6000);
						}
						catch(Exception e)
						{						
						}
					int cnt = webdriver.findElements(locator).size();
					
					if(cnt < row_no){
						driver.log.error(" Insufficient number of rows");
						resultDetails.setErrorMessage("Insufficient number of rows");
						return resultDetails;
					}
					
					boolean result = false;
					
					int ecnt = webdriver.findElements(By.xpath(loc + "[" + row_no + "]//td")).size();
					
					for ( int i = row_no;i<=cnt;i++){
					
						int acnt = webdriver.findElements(By.xpath(loc + "[" + i + "]//td")).size();
						
						if(ecnt > acnt ){
							continue;
						}
						
						String subloc = loc + "[" + i + "]//td[" + column_no + "]";
						String text = webdriver.findElement(By.xpath(subloc)).getText();
						result = text.equalsIgnoreCase(data);
						if(result){
							break;
						}
					}
					String msg = status ? "Data '" + data + "' not found " : " Data '" + data + "' Found ";
					if(result != status){
						driver.log.error( msg + " which is not expected on "+ fieldName);
						resultDetails.setErrorMessage( msg + " which is not expected on "+ fieldName);
						return resultDetails ;
					}
					
					resultDetails.setFlag(true);
				break;
			}
			
		}catch(IllegalArgumentException e){
			driver.log.error(" For VERIFYTABLEROW , data field is of type TBL");
			resultDetails.setErrorMessage(" For VERIFYTABLEROW , data field is of type TBL");
			return resultDetails ;
		}catch(Exception e){
			driver.log.error(" Exception in executing VERIFYTABLEROW");
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(" Exception in executing VERIFYTABLEROW");
			System.out.println(e.getMessage());
			return resultDetails ;
		}
		return resultDetails;
	}

	
	
	/**
	 * Method to verify a particular cell value
	 */
	
	public ResultDetails verifyValueInRow(SeleniumDriver driver,WebDriver webdriver, String fieldText, String value, String fieldName) {
		
		String fieldType = fieldText.substring(0, 3).toUpperCase();
		String field = fieldText.substring(3);
		field=tt.driver.utils.getValue(field);

		if (fieldName.equalsIgnoreCase(""))
			fieldName = field;

		field = tt.getFieldFromRepo(field);

		By locator;

		try {

			DataFields dfs = DataFields.valueOf(fieldType);

			switch (dfs) {

			case TBL:

				
				String actual = "";

				field = tt.getFieldFromRepo(field);
				String expected = value.trim();
				String tblField="//table[" + field + "]";
				if(field.startsWith("//"))
				{
					tblField=field;
				}
				resultDetails.setComment("Verifying value :: " + actual+ " with " + value);
					driver.log.info(" Verifying value :: " + actual+ " with " + value);
				String[] tempValues = value.split(":");
				
				String row = (tempValues[0]);
				
				if (row.toUpperCase().startsWith("HMV")){
				
					String temp = row.substring(3);
					
					int index = temp.indexOf("-");
					
					String changeby = "0";
					
					if(index != -1 ){
						changeby = temp.substring(index);
						temp = temp.substring(0,index);
					}
					
					index = temp.indexOf("+");
					
					if(index != -1 ){
						changeby = temp.substring(index);
						temp = temp.substring(0,index);
					}
					
					int changeval = Integer.parseInt(changeby);
					row = (Integer.parseInt(driver.hMap.get(temp)) + changeval ) + ""; 
				}
				
				int col = Integer.parseInt(tempValues[1]);

				value = tt.driver.utils.getValue(value.substring((tempValues[0] + tempValues[1]).length()+2));
				
				field = tblField+"//tr[" + row + "]/td[" + col	+ "]";
				try{
					WebDriverWait wait = new WebDriverWait(webdriver, 5);
					wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(tblField+"//tr")));
					//Thread.sleep(6000);
					}
					catch(Exception e)
					{						
					}
				locator = WebDriverUtils.locatorToByObj(webdriver, field);

				if (locator == null) {
					driver.log.error(" Unable to locate element ::"	+ fieldName);
					resultDetails.setErrorMessage("Unable to locate element ::"	+ fieldName);
					return resultDetails ;
				}
				expected = value.trim();
				actual = webdriver.findElement(locator).getText().trim();
				driver.log.info(" Verifying value :: " + actual	+ " with " + value);
				resultDetails.setComment("Verifying value :: " + actual	+ " with " + value);

				boolean res = actual.equals(expected);

				if (res)
					resultDetails.setFlag(true);
				else {
					String temp = " Value Mismatch . Expected '" + expected + "' . But Found '" + actual + "'";
					System.out.println(temp);
					resultDetails.setErrorMessage(temp);
				}

				break;
			}
		} catch (IllegalArgumentException e) {
			resultDetails.setErrorMessage("For verifyvalueinrow the field type argument must not be " + fieldType);
		} catch (Exception e) {
			resultDetails.setErrorMessage(" Error in executing verifyvalueinrow keyword on  "	+ fieldName);
		}
		return resultDetails;
	}


	
	

	
}
