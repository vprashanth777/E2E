package com.java;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.java.TestType.DataFields;
import com.java.objects.ResultDetails;

public class DBKeywords {

	
	
	
	TestType tt;

	public DBKeywords(TestType tt) {
		this.tt = tt;
	}



	private ResultDetails resultDetails = new ResultDetails();
	
	
	
	/**
	 * Method to verify the webtable content against SQL query resultset operation
	 */
	
	public ResultDetails verifytablecontent(SeleniumDriver driver,WebDriver webdriver, String field, String value, String fieldName)
	{
		String MismatchData = "";
	
		try {

			if(field.equals("")){
				String warn = " Field is Empty ";
				System.out.println(warn);
			//	webdriver.log.error(warn);
				resultDetails.setErrorMessage(warn);
				return resultDetails;
			}
			
			if(field.length() < 4 ){
				String warn = " Field is Not specified in expected Format";
				System.out.println(warn);
			//	webdriver.log.error(warn);
				resultDetails.setErrorMessage(warn);
				return resultDetails;
			}
			
			if(fieldName.equalsIgnoreCase(""))
				fieldName = field;
			
			String fieldType = field.substring(0,3);
			DataFields dfs = DataFields.valueOf(fieldType);

			field = field.substring(3);
			value = tt.driver.utils.getValue(value);
			
			String[] datavalue  = value.split(":");
			
			switch(dfs){
			
				case TBL:
									
					if(datavalue.length <= 3){
						String temp =  " Invalid format of Data Value. \n It  should be <row_start>:<row_end>:>sql query>";
						resultDetails.setErrorMessage(temp);
					//	driver.log.error(temp);
						System.out.println(temp);
						return resultDetails;
					}
					
					Connection conn = null;
					String query = datavalue[3];
					
					Properties miscProps = tt.driver.miscProps;
					String url = miscProps.getProperty("appDB_URL");
					String jdbcDriver = miscProps.getProperty("appDB_driver");
					String userName = miscProps.getProperty("appDB_userName");
					String password = miscProps.getProperty("appDB_password");
					String dbName = miscProps.getProperty("app_DatabaseName");

					Class.forName(jdbcDriver);
					conn = DriverManager.getConnection(url + dbName, userName, password);
					Statement statement;
					if (url.contains("sqlserver"))
						statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,	ResultSet.CONCUR_READ_ONLY);
					else
						statement = conn.createStatement();

					ResultSet resultSet = statement.executeQuery(query);
                    System.out.println("Resultset" + resultSet.toString());
					
					int startRow=Integer.parseInt(datavalue[0]);
					int endRow=Integer.parseInt(datavalue[1]);
					if (endRow < startRow)
						endRow = startRow;
					else if (endRow == startRow)
						endRow = startRow+1;
					else
						endRow = startRow+endRow-1;
					
					int i=1;
					int j=1;
					String loc="//table[" + field + "]//tr[" + startRow +"]/td[" + j + "]";
					boolean status=true;
					
					//Looping through the table content comparing with SQL query resultset
					for(i=Integer.parseInt(datavalue[0]);i<=endRow;i++)
					{
						j=1;
						resultSet.absolute(i-1);
						System.out.println("------------------------");						
						int intColumnCnt = Integer.parseInt(datavalue[2]);
						int actColCnt  = webdriver.findElements(By.xpath("//table[" + field + "]//tr[" + i +"]//td")).size();
						System.out.println("Row "+i+" Column Count : "+actColCnt);
						
						if (intColumnCnt > actColCnt)
							intColumnCnt = actColCnt;
						
						while(j<=intColumnCnt)
						{							
							loc="//table[" + field + "]//tr[" + i +"]/td[" + j + "]";
							//System.out.println("DB data:"+resultSet.getString(j));
							//System.out.println("Table:"+webdriver.findElement(By.xpath(loc)).getText());							
							int actualRow = i-1;							
							//System.out.println(webdriver.findElement(By.xpath(loc)).getText());
							//System.out.println(resultSet.getString(j));
							if(webdriver.findElement(By.xpath(loc)).getText().equals(resultSet.getString(j))) {								
								resultDetails.setFlag(true);
								String temp = "Row : "+actualRow+" Column : "+j+" , UI Value : ["+webdriver.findElement(By.xpath(loc)).getText()+"] == DB Value : ["+resultSet.getString(j)+"]";
								//System.out.println(temp);
								driver.log.info(temp);
							} else {
								status=false;
								String temp = "Row : "+actualRow+" Column : "+j+" , UI Value : ["+webdriver.findElement(By.xpath(loc)).getText()+"] != DB Value : ["+resultSet.getString(j)+"]";
								//System.out.println(temp);								
								driver.log.info(temp);
								MismatchData = MismatchData +"\n Data mismatched in table "+fieldName+" @ Row : "+actualRow+ " Column : "+j+ " Expected : "+resultSet.getString(j)+" Actual : "+webdriver.findElement(By.xpath(loc)).getText();								
							}
							j++;
						}
					}
					
					if (!status) {						
						resultDetails.setFlag(false);						
						resultDetails.setErrorMessage(MismatchData+"\n");						
						/*String comment = MismatchData+"\n\n";
						if(driver.hMap.containsKey("comment_excel_write")){
							comment = comment + "\n" + driver.hMap.get("comment_excel_write");
						}
						driver.hMap.put("comment_excel_write",comment);*/						
						driver.log.error(MismatchData);
						return resultDetails;
					} else {
						resultDetails.setFlag(true);
					}
					//Case statement break 
					break;
				}
			}
			catch(Exception e)
			{
				System.out.println("Error: "+e.getMessage());
				e.printStackTrace();
				resultDetails.setFlag(false);
			}
		return resultDetails;
						
						
			/*	while (resultSet.next()) 
				{	
					while(true)
					{
					if(webdriver.findElement(By.xpath(loc)).getText().equals(resultSet.getString(j)))
					{
						resultDetails.setFlag(true);
					}
					else
					{
						status=false;							
						resultDetails.setFlag(false);
						break;
					}
					j++;
					loc = "//table[" + field + "]//tr[" + i +"]//td [" + j + "]";
					}
					i++;
				}
					
					if(resultSet.getString(i)

					if (rows == Integer.parseInt(value)) {
						resultDetails.setFlag(true);
						break;
					} else {
						resultDetails.setFlag(false);
						resultDetails.setErrorMessage("No of rows are not the same as expected");
						break;
					}
				}
			
			click = datavalue[3].equalsIgnoreCase("click");
			
			if(click && datavalue.length <= 3){
				String temp =  " Invalid format of Data Value. \n It  should be row_start:col_no:text_to_match:click:column_to_be_clicked";
				resultDetails.setErrorMessage(temp);
				driver.log.error(temp);
				System.out.println(temp);
				return;
			}
			
			datavalue[2] = getValue(datavalue[2]);
			try{
				WebDriverWait wait = new WebDriverWait(webdriver, 5);
				wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//table[" + field + "]//tr")));
				}
				catch(Exception e)
				{						
				}
			
			int rowcount = webdriver.findElements(By.xpath("//table[" + field + "]//tr")).size();
			System.out.println("No. of rows  = " + rowcount);
				
			int start_row = Integer.parseInt(datavalue[0]);
						
			if(start_row > rowcount){
				driver.log.error(" Required no. of rows are not present. Required :: " +start_row + " Present  ::" + rowcount);
				resultDetails.setErrorMessage(" Required no. of rows are not present. Required :: " +start_row + " Present  ::" + rowcount);
				return;
			}
			
			if(rowcount!=0){
			
				int ecnt = webdriver.findElements(By.xpath("//table[" + field + "]//tr[" + start_row + "]//td")).size();
				
				for(int i = start_row; i <= rowcount; i++){
				
					int acnt = webdriver.findElements(By.xpath("//table[" + field + "]//tr[" + i + "]//td")).size();
					
					if(ecnt > acnt ){
						continue;
					}
					
					String loc = "//table[" + field + "]//tr[" + i +"]//td [" + datavalue[1] + "]";
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
								
						loc = "//table[" + field + "]//tr[" + i +"]//td[" + datavalue[click_on] + "]";
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
						return;
					}
				}
					String temp =  " No data found for table " + fieldName;
					resultDetails.setErrorMessage(temp);
					System.out.println(temp);
					return;
				}else{
					String temp =  " No rows found for table " + fieldName;
					resultDetails.setErrorMessage(temp);
					System.out.println(temp);
				}
			break;*/
	}	
	
	/**
	 * Method to update DB as per query of user
	 */
	
	public ResultDetails updateDB(SeleniumDriver driver,String fieldText) {
		
		Connection conn = null;
		
		if(fieldText.length() < 4){
			String temp  = " FieldText is not as expected :: QRYquery";
			driver.log.error(temp);
			System.out.println(temp);
			resultDetails.setErrorMessage(temp);
			return resultDetails;
		}
		
		resultDetails.setComment(" Updating database using query specified ");
		
		try {

			Properties miscProps = driver.miscProps;
			String url = miscProps.getProperty("appDB_URL");
			String jdbcDriver = miscProps.getProperty("appDB_driver");
			String userName = miscProps.getProperty("appDB_userName");
			String password = miscProps.getProperty("appDB_password");
			String dbName = miscProps.getProperty("app_DatabaseName");
			Class.forName(jdbcDriver);
			conn = DriverManager.getConnection(url + dbName, userName, password);
			Statement statement = conn.createStatement();
			int rows = statement.executeUpdate(fieldText.substring(3));
            System.out.println(rows + " Rows Updated Successfully.");
            driver.log.info(" "+rows + " Rows Updated Successfully.");
            resultDetails.setFlag(true);
		}  catch (ClassNotFoundException e) {
			driver.log.error(" Unable to find the class required ");
			driver.log.error(e.getMessage());
			System.out.println(" Unable to find the class required ");
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
			String temp = " Exception while querying the Database  ";
			resultDetails.setFlag(false);
			resultDetails.setErrorMessage(temp);
			
			driver.log.error(temp);
			driver.log.error(e.getMessage());
			System.out.println(temp);
			System.out.println(e.getMessage());
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
			}
		}
		return resultDetails;
	}

	
	/**
	 * Method to verify the results of a query
	 */

	public ResultDetails verifyColumns(SeleniumDriver driver,String fieldText, String value) {

		HashMap<String, String> expectedValues = new HashMap<String, String>();

		Connection con = null;
		
		String columns[] = value.split(";");
		for (String col : columns)		{

			String value1[] = col.split("=");
			expectedValues.put(value1[0], value1[1]);
		}

		String query = fieldText.substring(3);

		resultDetails.setComment("Verifying column values in the table");
		driver.log.info(" Verifying column values in the table");
		
		try {

			Properties miscProps = driver.miscProps;
			String url = miscProps.getProperty("appDB_URL");
			String jdbcDriver = miscProps.getProperty("appDB_driver");
			String userName = miscProps.getProperty("appDB_userName");
			String password = miscProps.getProperty("appDB_password");
			String dbName = miscProps.getProperty("app_DatabaseName");

			Class.forName(jdbcDriver);
			con = DriverManager.getConnection(url + dbName, userName, password);
			Statement statement = con.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			Set<String> keys = expectedValues.keySet();
			String[] groupKeys = new String[100];

			int i = 0;

			for (String key : keys) {
				System.out.println(key.toString());
				groupKeys[i] = key.toString();
				i++;
			}

			int j = 0;
			int k = 0;

			boolean validate = true;

			outerloop:

			while (resultSet.next()) {

				j = 0;
				validate = true;
				while (true) {

					if (resultSet.getString(groupKeys[j]).equals(expectedValues.get(groupKeys[j]))) {
						driver.log.info(" Verifying column:'" + groupKeys[j]	+ "' with value '"	+ resultSet.getString(groupKeys[j]) + "'");
						System.out.println("Verifying column:'" + groupKeys[j]	+ "' with value '"	+ resultSet.getString(groupKeys[j]) + "'");
						j++;

						if (groupKeys[j] == null){
							break outerloop;
						}
					}

					else {
						System.out.println("Verifying column:'" + groupKeys[j]	+ "' with value '"	+ resultSet.getString(groupKeys[j]) + "'");
					
						k = j;
						k++;
						resultDetails.setErrorMessage(resultDetails	.getErrorMessage()+ " "	+ k	+ ".... Script failed while validating column:'"
										+ groupKeys[j]+ "' for value: '" + expectedValues.get(groupKeys[j])	+ "'");
						validate = false;
						driver.log.error(resultDetails	.getErrorMessage()+ " "	+ k	+ ".... Script failed while validating column:'"
								+ groupKeys[j]+ "' for value: '" + expectedValues.get(groupKeys[j])	+ "'");
						
						j++;
						if (groupKeys[j] == null){
							break;
						}
					}
				}
			}
			if (validate)
				resultDetails.setFlag(true);
			else
				resultDetails.setFlag(false);
		}

		catch (Exception e) {
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(e.getMessage());
			resultDetails.setFlag(false);

		} finally {
			if (con != null)
				try {
					con.close();
				} catch (Exception e) {
				}
		}
		return resultDetails;
	}


	
	public ResultDetails verifyduplicateValuesonPrimarykeys(SeleniumDriver driver,String value, String fieldName) {
		
		try
		{
			resultDetails.setComment(" Executing query to verify duplicate values on primary keys for table " + fieldName);
			
			if(value.equals("")){
				String temp = " Value is empty ";
				System.out.println(temp);
				driver.log.info(temp);
				resultDetails.setErrorMessage(temp);
				return resultDetails;
			}
			
			Properties miscProps = driver.miscProps;
			String url = miscProps.getProperty("appDB_URL");
			String jdbcDriver = miscProps.getProperty("appDB_driver");
			String userName = miscProps.getProperty("appDB_userName");
			String password = miscProps.getProperty("appDB_password");
			String dbName = miscProps.getProperty("app_DatabaseName");

		    Class.forName(jdbcDriver);
		    Connection conn = DriverManager.getConnection(url+dbName,userName,password);
		    Statement st=conn.createStatement();
		    try {
			    st.executeUpdate(value);
			    st.executeUpdate(value);
			    resultDetails.setFlag(false);
		    }
		    catch(Exception e) {
		    	resultDetails.setFlag(true);
		    	driver.log.error("Unable to insert duplicate value on primary key as expected ");
		    	System.out.println("Unable to insert duplicate value on primary key as expected ");
		    	System.out.println(e.getMessage());
		    }
		} catch (ClassNotFoundException e) {
			driver.log.error(" Unable to find the class required ");
			driver.log.error(e.getMessage());
			System.out.println(" Unable to find the class required ");
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
		} catch(Exception e){
			
			String temp = " Exception while inserting duplicate values on primaryy key ";
			resultDetails.setFlag(false);
			resultDetails.setErrorMessage(temp);
			driver.log.error(temp);
			driver.log.error(e.getMessage());
			System.out.println(temp);
			System.out.println(e.getMessage());
		}
		return resultDetails;
	}

	/**
	 * Method to store a value from database into hashmap
	 */
	
	public ResultDetails storeDBValue(SeleniumDriver driver,String query, String key, String fieldName) {
	
		try {
			
			resultDetails.setComment("Storing " + fieldName + " from DB with" + key);
			
			if(query.length() < 4 ){
				String temp = (" The DataFields must be specifed as QRYselect_query ");
				System.out.println(temp);
				driver.log.error(temp);
				resultDetails.setErrorMessage(temp);
				return resultDetails;	
			}
			
			String values [] = key.split("::");
			
			if(values.length !=2){
				String temp = (" Insufficient data values, For DBV format is column_heading::key");
				System.out.println(temp);
				driver.log.error(temp);
				resultDetails.setErrorMessage(temp);
				return resultDetails;
			}
			
			key = values[1];
			
			if(key.equals("")){
				String temp = (" Key value is empty ");
				System.out.println(temp);
				driver.log.error(temp);
				resultDetails.setErrorMessage(temp);
				return resultDetails;
			}
			
			if(values[0].equals("")){
				String temp = (" Column to be stored is empty ");
				System.out.println(temp);
				driver.log.error(temp);
				resultDetails.setErrorMessage(temp);
				return resultDetails;
			}
			
			query = query.substring(3);
			
			if(query.equals("")){
				String temp = (" Query is empty ");
				System.out.println(temp);
				driver.log.error(temp);
				resultDetails.setErrorMessage(temp);
				return resultDetails;
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
				return resultDetails;
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
		return resultDetails;
	}
	
	
	
	/**
	 * Method to verify thatcertain columns do not accept null values 
	 */
	
	public ResultDetails verifyNotNullConstraints(SeleniumDriver driver,String fieldText, String value, String fieldName){
		
		try {
		
			resultDetails.setComment(" Executing query to verify null values for " + fieldName);
			
			if(value.equals("")){
				String temp = " Value is empty ";
				System.out.println(temp);
				driver.log.info(temp);
				resultDetails.setErrorMessage(temp);
				return resultDetails;
			}
			
			Properties miscProps = driver.miscProps;
			String url = miscProps.getProperty("appDB_URL");
			String jdbcDriver = miscProps.getProperty("appDB_driver");
			String userName = miscProps.getProperty("appDB_userName");
			String password = miscProps.getProperty("appDB_password");
			String dbName = miscProps.getProperty("app_DatabaseName");
					    
		    Class.forName(jdbcDriver);
		    Connection conn = DriverManager.getConnection(url+dbName,userName,password);
		    Statement st=conn.createStatement();
		    
		    try {
		    	st.executeUpdate(value);
		    	driver.log.error(" User is bale to insert NULL values on " + fieldName);
		    	resultDetails.setFlag(false);
		    	resultDetails.setErrorMessage(" User is bale to insert NULL values on " + fieldName);
		    	System.out.println(" User is bale to insert NULL values on " + fieldName);
		    }
		    catch(Exception e) {
		    	resultDetails.setFlag(true);
		    	driver.log.error(" Unable to insert null value as expected");
		    	System.out.println(" Unable to insert null value as expected");
		    	System.out.println(e.getMessage());
		    }
		} catch (ClassNotFoundException e) {
			driver.log.error(" Unable to find the class required ");
			driver.log.error(e.getMessage());
			System.out.println(" Unable to find the class required ");
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
		} catch(Exception e) {
			driver.log.error(" Exception in verifying not null constraint on " + fieldName);
			driver.log.error(e.getMessage());
			resultDetails.setFlag(false);
			System.out.println(" Exception in verifying not null constraint on " + fieldName);
			resultDetails.setErrorMessage(" Exception in verifying not null constraint on " + fieldName);
			System.out.println(e.getMessage());
		}
		return resultDetails;
	}
	
	
	
	
	/**
	 *  Method to verify the primary keys in the table 
	 */
	
	public ResultDetails verifyPrimaryKeys(SeleniumDriver driver,String fieldText, String value, String fieldName) {
		
		if(fieldText.length() < 4){
			String temp = " DataField must be specified as QRYtableName ";
			System.out.println(temp);
			driver.log.error(temp);
			resultDetails.setErrorMessage(temp);
			return resultDetails;
		}
		
		if(value.equals("")){
			String temp = " Value specified is empty ";
			System.out.println(temp);
			driver.log.error(temp);
			resultDetails.setErrorMessage(temp);
			return resultDetails;
		}
		
		String tableName = fieldText.substring(3);
		
		resultDetails.setComment(" Verifying the primary keys of table " + tableName);
		
		String[] expectedValues = value.split(";");

		try {
			
			Properties miscProps = driver.miscProps;
			String url = miscProps.getProperty("appDB_URL");
			String jdbcDriver = miscProps.getProperty("appDB_driver");
			String userName = miscProps.getProperty("appDB_userName");
			String password = miscProps.getProperty("appDB_password");
			String dbName = miscProps.getProperty("app_DatabaseName");
			
		    Class.forName(jdbcDriver);
		    Connection conn = DriverManager.getConnection(url+dbName,userName,password);
		    DatabaseMetaData metaData = conn.getMetaData();
		  
		    ResultSet resultSet = metaData.getPrimaryKeys(dbName,"",tableName);
		  
		    int i=0;
		    int count=0;
            while (resultSet.next()) {
            	i=0;
            	System.out.println(resultSet.getString(4));
            	while(true) {
            		
            		if(resultSet.getString(4).equals(expectedValues[i])) {
            			driver.log.info(expectedValues[i]+" primay key is identified in the table");
            			System.out.println(expectedValues[i]+" primay key is identified in the table");
            			i++;
            			count++;
            			break;
            		}
            		i++;
            	}
            }
            if(count==expectedValues.length) {
            	 resultDetails.setFlag(true);
            	 return resultDetails;
            }
            else {
            	driver.log.info(" All the expected primary keys are not identified ");
            	System.out.println(" All the expected primary keys are not identified ");
            	resultDetails.setErrorMessage(" All the expected primary keys are not identified ");
            	resultDetails.setFlag(false);
            }
		} catch (ClassNotFoundException e) {
			driver.log.error(" Unable to find the class required ");
			driver.log.error(e.getMessage());
			System.out.println(" Unable to find the class required ");
			System.out.println(e.getMessage());
			resultDetails.setErrorMessage(" Unable to find the class required ");
			resultDetails.setFlag(false);
		} catch (SQLException e) {
			System.out.println(" SQLException while querying the Database ");
			System.out.println(e.getMessage());
			driver.log.error(" SQLException while querying the Database ");
			driver.log.error(e.getMessage());
			resultDetails.setErrorMessage(" SQLException while querying the Database ");
			resultDetails.setFlag(false);
		} catch(Exception e) {
			resultDetails.setFlag(false);
			driver.log.error(" Exception in verfying certain columns are primary keys ");
			driver.log.error(e.getMessage());
			System.out.println(" Exception in verfying certain columns are primary keys ");
			resultDetails.setErrorMessage(" Exception in verfying certain columns are primary keys ");
			System.out.println(e.getMessage());
		}
		return resultDetails;
	}
	
	
	
	/**
	 * Method to verify row count in DB upon firing a query
	 */

	public ResultDetails verifyRowCount(SeleniumDriver driver,String fieldText, String value) {

		Connection conn = null;
		String query = fieldText.substring(3);

		resultDetails.setComment(" Verifying that table contains expected no.of rows :: " +  value);
		driver.log.info(" Verifying that table contains expected no.of rows :: " +  value);
		
		try {

			Properties miscProps = driver.miscProps;
			String url = miscProps.getProperty("appDB_URL");
			String jdbcDriver = miscProps.getProperty("appDB_driver");
			String userName = miscProps.getProperty("appDB_userName");
			String password = miscProps.getProperty("appDB_password");
			String dbName = miscProps.getProperty("app_DatabaseName");

			Class.forName(jdbcDriver);
			conn = DriverManager.getConnection(url + dbName, userName, password);
			Statement statement;
			if (url.contains("sqlserver"))
				statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,	ResultSet.CONCUR_READ_ONLY);
			else
				statement = conn.createStatement();

			ResultSet resultSet = statement.executeQuery(query);

			while (resultSet.next()) {
				System.out.println(resultSet.last());
				System.out.println(resultSet.getRow());
				int rows = resultSet.getRow();

				if (rows == Integer.parseInt(value)) {
					resultDetails.setFlag(true);
					break;
				} else {
					resultDetails.setFlag(false);
					resultDetails.setErrorMessage("No of rows are not the same as expected");
					break;
				}
			}
		} catch (ClassNotFoundException e) {
			driver.log.error(" Unable to find the class required ");
			driver.log.error(e.getMessage());
			System.out.println(" Unable to find the class required ");
			System.out.println(e.getMessage());
			resultDetails.setErrorMessage(e.getMessage());
			resultDetails.setFlag(false);
		} catch (SQLException e) {
			driver.log.error(" SQLException while querying the Database");
			driver.log.error(e.getMessage());
			System.out.println("SQLException while querying the Database");
			System.out.println(e.getMessage());
			resultDetails.setErrorMessage(e.getMessage());
			resultDetails.setFlag(false);
		} catch (Exception e) {
			driver.log.error(" Exception while querying the Database");
			driver.log.error(e.getMessage());
			System.out.println("Exception while querying the Database");
			System.out.println(e.getMessage());
			resultDetails.setErrorMessage(e.getMessage());
			resultDetails.setFlag(false);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
			}
		}
		return resultDetails;
	}

	/**
	 * Method to verify the store procedure execution time
	 */

	public ResultDetails verifySPExecTime(SeleniumDriver driver,String fieldText, String value) {

		Connection conn = null;
		final int EMPTY = -1;

		driver.log.info(" Verifying that Store Procedure is executed with in " + value + " milli seconds");
		resultDetails.setComment(" Verifying that Store Procedure is executed with in " + value + " milli seconds");
		
		String storedProcedureName = fieldText.substring(3);
		driver.log.info(" Procedure Name :: "+ storedProcedureName);
		
		try {
			
			String expectedTime = storedProcedureName.split("::")[1];
			storedProcedureName = storedProcedureName.split("::")[0];

			Properties miscProps = driver.miscProps;
			String url = miscProps.getProperty("appDB_URL");
			String jdbcDriver = miscProps.getProperty("appDB_driver");
			String userName = miscProps.getProperty("appDB_userName");
			String password = miscProps.getProperty("appDB_password");
			String dbName = miscProps.getProperty("app_DatabaseName");

			Class.forName(jdbcDriver);
			conn = DriverManager
					.getConnection(url + dbName, userName, password);

			String[] test = value.split(",");
			String tt = "";

			for (int k = 0; k < test.length; k++) {
				tt = "?," + tt;
			}
			tt = tt.substring(0, tt.lastIndexOf(","));

			CallableStatement st = conn.prepareCall("{call "
					+ storedProcedureName + "(" + tt + ")}");

			int[] outputIndex = new int[20];

			Arrays.fill(outputIndex, EMPTY);
			int i = 0;

			for (String str : test) {
				if (str.contains("?")) {
					outputIndex[i] = Arrays.asList(test).indexOf(str);
					i++;
				} else {
					st.setString(Arrays.asList(test).indexOf(str) + 1, str);
				}
			}

			for (int j = 0; j < outputIndex.length; j++) {
				if (outputIndex[j] != -1) {
					int temp = outputIndex[j];
					temp++;
					st.registerOutParameter(temp, java.sql.Types.VARCHAR);
				}
			}

			long start_time = new Date().getTime();
			st.executeUpdate();
			long end_time = new Date().getTime();

			long taken = (end_time - start_time);

			int n = (int) (taken % 1000);
			taken = taken / 1000;

			String timeTaken = taken + "." + n;
			System.out.println("Time Taken : " + timeTaken + "seconds");

			double time_taken = Double.parseDouble(timeTaken);
			double expected_time = Double.parseDouble(expectedTime);
			if (time_taken <= expected_time)
				resultDetails.setFlag(true);
			else {
				driver.log.error(" Expected time :: "+ expected_time + " is lower than  " + time_taken);
				resultDetails.setFlag(false);
				resultDetails.setErrorMessage("Expected time :: "+ expected_time + " is lower than  " + time_taken);
			}
		} catch (ClassNotFoundException e) {
			driver.log.error(" Unable to find the class required ");
			driver.log.error(e.getMessage());
			System.out.println(" Unable to find the class required ");
			System.out.println(e.getMessage());
			resultDetails.setErrorMessage(e.getMessage());
			resultDetails.setFlag(false);
		} catch (SQLException e) {
			driver.log.error(" SQLException while querying the Database");
			driver.log.error(e.getMessage());
			System.out.println("SQLException while querying the Database");
			System.out.println(e.getMessage());
			resultDetails.setErrorMessage(e.getMessage());
			resultDetails.setFlag(false);
		} catch (Exception e) {
			driver.log.error(" Exception while querying the Database");
			driver.log.error(e.getMessage());
			System.out.println("Exception while querying the Database");
			System.out.println(e.getMessage());
			resultDetails.setErrorMessage(e.getMessage());
			resultDetails.setFlag(false);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
			}
		}
		return resultDetails;
	}

	
	
	/**
	 * Method to verify the execution of SP in DB
	 */

	public ResultDetails verifyStoredProcedure(SeleniumDriver driver,String fieldText, String value) {

		Connection conn = null;
		final int EMPTY = -1;
		String storedProcedureName = fieldText.substring(3);

		resultDetails.setComment("Verfying that the stored procedure " + storedProcedureName + " is executed ");
		driver.log.info(" Verfying that the stored procedure " + storedProcedureName + " is executed ");
		
		try {
			Properties miscProps = driver.miscProps;
			String url = miscProps.getProperty("appDB_URL");
			String jdbcDriver = miscProps.getProperty("appDB_driver");
			String userName = miscProps.getProperty("appDB_userName");
			String password = miscProps.getProperty("appDB_password");
			String dbName = miscProps.getProperty("app_DatabaseName");

			Class.forName(jdbcDriver);
			conn = DriverManager
					.getConnection(url + dbName, userName, password);

			String[] test = value.split(",");
			String tt = "";

			for (int k = 0; k < test.length; k++) {
				tt = "?," + tt;
			}
			tt = tt.substring(0, tt.lastIndexOf(","));

			CallableStatement st = conn.prepareCall("{call "
					+ storedProcedureName + "(" + tt + ")}");

			int[] outputIndex = new int[20];
			Arrays.fill(outputIndex, EMPTY);

			int i = 0;
			for (String str : test) {
				if (str.contains("?")) {
					outputIndex[i] = Arrays.asList(test).indexOf(str);
					i++;
				} else {
					st.setString(Arrays.asList(test).indexOf(str) + 1, str);
				}
			}

			for (int j = 0; j < outputIndex.length; j++) {
				if (outputIndex[j] != -1) {
					int temp = outputIndex[j];
					temp++;
					st.registerOutParameter(temp, java.sql.Types.VARCHAR);
				}
			}

			st.executeUpdate();
			driver.log.info(" The output of the called stored procedure is : " + st.getString(3));
			System.out.println("The output of the called stored procedure is : " + st.getString(3));
			resultDetails.setFlag(true);

		} catch (ClassNotFoundException e) {
			driver.log.error(" Unable to find the class required ");
			driver.log.error(e.getMessage());
			System.out.println(" Unable to find the class required ");
			System.out.println(e.getMessage());
			resultDetails.setErrorMessage(e.getMessage());
			resultDetails.setFlag(false);
		} catch (SQLException e) {
			driver.log.error(" SQLException while querying the Database");
			driver.log.error(e.getMessage());
			System.out.println("SQLException while querying the Database");
			System.out.println(e.getMessage());
			resultDetails.setErrorMessage(e.getMessage());
			resultDetails.setFlag(false);
		} catch (Exception e) {
			driver.log.error(" Exception while querying the Database");
			driver.log.error(e.getMessage());
			System.out.println("Exception while querying the Database");
			System.out.println(e.getMessage());
			resultDetails.setErrorMessage(e.getMessage());
			resultDetails.setFlag(false);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
			}
		}
		return resultDetails;
	}

}
