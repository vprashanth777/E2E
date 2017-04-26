
package com.java.importnexport;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xerces.dom.AttributeMap;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.java.SeleniumDriver;
import com.java.objects.TestDataDetails;

import Exception.FilloException;
import Fillo.Connection;
import Fillo.Fillo;
import Fillo.Recordset;


/**
 * This java program is used to read the data from a Excel sheet or Database or
 * XML file
 */

public class ImportTestDataDetails {

	private SeleniumDriver driver;
	private String testCaseId;

	/**
	 * Constructor of the class which takes SeleniumDriver class as a parameter
	 * 
	 * @param driver
	 */

	public ImportTestDataDetails(SeleniumDriver driver) {
		this.driver = driver;
	}

	/**
	 * Method which returns the Test Data steps as an arrayList
	 * 
	 * @param testCaseId
	 * @return
	 */

	public HashMap<Integer, TestDataDetails> readTestData(String testCaseId) {

		this.testCaseId = testCaseId;
		HashMap<Integer, TestDataDetails> testData = new HashMap<Integer, TestDataDetails>();

		String fileName = driver.confDtls.getTestDataSource().toLowerCase();

		if (driver.isDBUsed)
			testData = readFromDB();

		else if (fileName.contains(".xml"))
			testData = readfromXML(fileName);

		else
			testData = readfromExcel(fileName);

		return testData;
	}

	/**
	 * Method used to read testcase details from XML Takes the testcaseID of
	 * test case to be executed as a parameter Returns the testCase steps as a
	 * HashMap using TestDatadetails object
	 */

	private HashMap<Integer, TestDataDetails> readfromXML(String fileName) {

		driver.log.info(" Reading TestData for test case " + testCaseId + " from XML ");

		fileName = "../TestInputs/" + fileName;
		driver.log.info(" FileName is :: " + fileName);

		DocumentBuilderFactory docbuilderfactory = DocumentBuilderFactory.newInstance();
		Document document = null;
		try {
			DocumentBuilder docbuilder = docbuilderfactory.newDocumentBuilder();
			document = docbuilder.parse(fileName);
		} catch (ParserConfigurationException e) {
			driver.log.info(" Encountered ParserConfigurationException ");
			driver.log.error(e.getMessage());
			System.out.println("Encountered error in creating the XML Parser");
			System.out.println(e.getMessage());
			driver.stop();
		} catch (SAXException e) {

			driver.log.info(" Encountered  SAXException ");
			driver.log.error(e.getMessage());

			System.out.println("Error encountered in reading the XML document");
			System.out.println(e.getMessage());
			driver.stop();
		} catch (IOException e) {

			driver.log.info(" Encountered IOException ");
			driver.log.error(e.getMessage());

			System.out.println("Encountered IO Stream Application Error when reading the TestData file");
			System.out.println(e.getMessage());
			driver.stop();
		}

		int count = 0;
		String title = "TestCase ID : " + driver.confDtls.getPrefix()+testCaseId + " : Title Not available ";
		int testcaseid = 0;
		driver.log.info(" Reading Test Case Title ");
		try {

			testcaseid = Integer.parseInt(testCaseId);
			System.out.println("Reading Data for TestCase :: "  + driver.confDtls.getPrefix()+ testcaseid);
		} catch (Exception e) {
			title = "Template case ::  " + testCaseId;
			System.out.println("Reading Data for Template Case :: " +testCaseId);
		}

		if(testcaseid != 0){

			try {
				String xpath = "//testcasemaster/testcase[@ID='"+ driver.confDtls.getPrefix()+ testCaseId+"']";
				NodeList testConfig = org.apache.xpath.XPathAPI.selectNodeList(document, xpath);
				count = testConfig.getLength();

				int rows = 0;
				while (rows < count) {
					Node row = testConfig.item(rows);
					if (row.getNodeType() == Node.ELEMENT_NODE) {
						title = row.getTextContent().trim();
						break;
					}
					rows++;
				}
			} catch (TransformerException e) {
				System.out.println("Transformer Exception while reading the testdata");
				System.out.println(e.getMessage());
				driver.stop();
			}

			driver.log.info(" Read Title :: " + title);
			driver.TestCaseDetails.put(testcaseid,title);
		}

		if(driver.useObjectRepo && driver.objRepo.isEmpty()){
			try{

				driver.log.info(" Reading Object Repository "); 

				NodeList parameterList = org.apache.xpath.XPathAPI.selectNodeList(document,"//objectrepo//object");
				count = parameterList.getLength();

				for (int i = 0; i < count; i++) {

					if (parameterList.item(i).getNodeType() == Node.ELEMENT_NODE) {

						AttributeMap map = (AttributeMap) parameterList.item(i).getAttributes();
						int attrCount = map.getLength();
						int j = 0;
						Properties props = new Properties();
						while (j < attrCount) {
							props.put(map.item(j).getNodeName().toLowerCase().trim(), map.item(j).getNodeValue().trim());
							j++;
						}
						String obj_name = props.getProperty("objname").toLowerCase();
						String obj_loc = props.getProperty("objloc");

						driver.objRepo.put(obj_name,obj_loc);
					}
				}
			}catch(Exception e){

				driver.log.error(" Encountered Exception while reading Object Repository "); 
				driver.log.error(" "+e.getMessage()); 
				System.out.println("Exception while loading the object repository");
				System.out.println(e.getMessage());
			}
			driver.useObjectRepo = false;
		}

		if (driver.reportReq && driver.TestCaseReq.isEmpty()) {
			try {

				driver.log.info(" Reading Requirement ID's ");

				NodeList parameterList = org.apache.xpath.XPathAPI.selectNodeList(document,"//testcasemaster/testcase");
				count = parameterList.getLength();

				for (int i = 0; i < count; i++) {

					if (parameterList.item(i).getNodeType() == Node.ELEMENT_NODE) {

						AttributeMap map = (AttributeMap) parameterList.item(i).getAttributes();
						int attrCount = map.getLength();
						int j = 0;
						Properties props = new Properties();
						while (j < attrCount) {
							props.put(map.item(j).getNodeName().toLowerCase().trim(), map.item(j).getNodeValue().trim());
							j++;
						}
						String req_name = props.getProperty("requirement","All");
						String tc_id = props.getProperty("id");

						String req = "";
						if(driver.TestCaseReq.containsKey(req_name)){
							req = driver.TestCaseReq.get(req_name);
						}
						driver.TestCaseReq.put(req_name,req + tc_id + ",");
					}
				}
			} catch (Exception e) {

				driver.log.error(" Encountered Exception while reading Requirement ID's"); 
				driver.log.error(e.getMessage()); 

				System.out.println("Exception while reading the requirement value");
				System.out.println(e.getMessage());
				driver.stop();
			}
		}	

		if (driver.reportPriority && driver.TestCasePriority.isEmpty()) {
			try {

				driver.log.info(" Reading Priority Values ");

				NodeList parameterList = org.apache.xpath.XPathAPI.selectNodeList(document,"//testcasemaster/testcase");
				count = parameterList.getLength();

				for (int i = 0; i < count; i++) {

					if (parameterList.item(i).getNodeType() == Node.ELEMENT_NODE) {

						AttributeMap map = (AttributeMap) parameterList.item(i).getAttributes();
						int attrCount = map.getLength();
						int j = 0;
						Properties props = new Properties();
						while (j < attrCount) {
							props.put(map.item(j).getNodeName().toLowerCase().trim(), map.item(j).getNodeValue().trim());
							j++;
						}
						String priority_name = props.getProperty("priority","P2");
						String tc_id = props.getProperty("id");

						String priority = "";
						if(driver.TestCasePriority.containsKey(priority_name)){
							priority = driver.TestCasePriority.get(priority_name);
						}
						driver.TestCasePriority.put(priority_name,priority + tc_id + ",");
					}
				}
			} catch (Exception e) {

				driver.log.error(" Encountered Exception while reading Priority Values");
				driver.log.error(e.getMessage());
				System.out.println("Exception while reading the Priority value");
				System.out.println(e.getMessage());
				driver.stop();
			}
		}

		if (driver.parameterDetails.isEmpty()) {
			try {

				driver.log.info(" Reading Parameter Values ");

				NodeList parameterList = org.apache.xpath.XPathAPI.selectNodeList(document,"//testdatafile/testdata/parameter");
				count = parameterList.getLength();
				for (int i = 0; i < count; i++) {
					if (parameterList.item(i).getNodeType() == Node.ELEMENT_NODE) {
						AttributeMap map = (AttributeMap) parameterList.item(i).getAttributes();
						int attrCount = map.getLength();
						int j = 0;
						Properties props = new Properties();
						while (j < attrCount) {
							props.put(map.item(j).getNodeName().toLowerCase().trim(), map.item(j).getNodeValue().trim());
							j++;
						}
						String param_name = props.getProperty("name").toLowerCase();

						if(!driver.testDataCounter.containsKey(param_name)){
							driver.testDataCounter.put(param_name,1);
						}else{
							int n = driver.testDataCounter.get(param_name);
							driver.testDataCounter.put(param_name,n+1);
						}

						String param_value = props.getProperty("value", "");
						String indx = props.getProperty("index", "1");
						driver.log.info(param_name + "#" +indx + " --> " + param_value );
						driver.parameterDetails.put(param_name + indx,param_value);
					}
				}
			} catch (Exception e) {

				driver.log.error(" Encountered Exception while reading Parameter Values");
				driver.log.error(e.getMessage());

				System.out.println("Exception while reading the parameter data");
				System.out.println(e.getMessage());
				driver.stop();
			}
		}

		if(driver.setup_TestData.isEmpty()){
			try {

				driver.log.info(" Reading Values for setuop test data ");

				NodeList parameterList = org.apache.xpath.XPathAPI.selectNodeList(document, "//testdatafile/templateData/parameter");
				count = parameterList.getLength();

				String temp = "";

				for (int i = 0; i < count; i++) {

					if (parameterList.item(i).getNodeType() == Node.ELEMENT_NODE) {

						AttributeMap map = (AttributeMap) parameterList.item(i).getAttributes();
						int attrCount = map.getLength();
						int j = 0;

						Properties props = new Properties();
						while (j < attrCount) {
							props.put(map.item(j).getNodeName().toLowerCase().trim(), map.item(j).getNodeValue().trim());
							j++;
						}

						temp = props.getProperty("templatecase_name");
						props.remove("templatecase_name");

						int keycount = 1 ;

						if(driver.setup_TestDataCounter.containsKey(temp))
							keycount = driver.setup_TestDataCounter.get(temp);

						driver.setup_TestDataCounter.put(temp,keycount+1);

						driver.setup_TestData.put(temp+"#"+keycount, props);

					}
				}
			} catch (Exception e) {

				driver.log.error(" Encountered Exception while reading Template TestData ");
				driver.log.error(e.getMessage());

				System.out.println("Exception while reading the Template testdata");
				System.out.println(e.getMessage());
				driver.stop();
			}
		}

		HashMap<Integer, TestDataDetails> testData = new HashMap<Integer, TestDataDetails>();

		try {

			driver.log.info(" Reading Test Case Steps ");

			NodeList testStepList = org.apache.xpath.XPathAPI.selectNodeList(document, "//datasheet/testcase[@ID='" +driver.confDtls.getPrefix()+ testCaseId+ "']/action");

			count = testStepList.getLength();
			int priority = 1;

			for (int i = 0; i < count; i++) {

				TestDataDetails tdd = new TestDataDetails();
				Node action = testStepList.item(i);

				if (action.getNodeType() == Node.ELEMENT_NODE) {
					AttributeMap map = (AttributeMap) action.getAttributes();

					int attrCount = map.getLength();
					int j = 0;
					Properties props = new Properties();
					while (j < attrCount) {
						props.put(map.item(j).getNodeName().toLowerCase().trim(), map.item(j).getNodeValue().trim());
						j++;
					}
					tdd = parseProps(props);
					tdd.setTestCaseTitle(title);
					tdd.setTestCaseID(testCaseId);
					tdd.setTestDataID(priority);
					testData.put(priority, tdd);
					priority++;
				}
			}
		} catch (Exception e) {

			driver.log.error(" Encountered Exception while reading testdata for given case ");
			driver.log.error(e.getMessage());

			System.out.println("Exception while reading the testdata for given case");
			System.out.println(e.getMessage());
		}
		return testData;
	}

	/**
	 * Method used to read testcase details from Excel Takes the testcaseID of
	 * test case to be executed as a parameter Returns the testCase steps as a
	 * HashMap using TestDatadetails object
	 * @throws FilloException 
	 */

	private HashMap<Integer, TestDataDetails> readfromExcel(String xlsFile) {

		driver.log.info(" Reading TestData for test case " + driver.confDtls.getPrefix()+testCaseId + " from Excel ");

		int testcaseid = 0;

		HashMap<Integer, TestDataDetails> testData = new HashMap<Integer, TestDataDetails>();
		String xlsPath = "../TestInputs/" + xlsFile;
		driver.log.info(" FileName is :: " + xlsFile);
		Fillo fillo=new Fillo();
		Connection connection;
		//connection = fillo.getConnection(xlsPath);
		String title = "TestCase ID : " + driver.confDtls.getPrefix()+testCaseId;
		String testDataQuery = "";
		if(driver.confDtls.getPrefix().toString().length()!=0)
		{
			testDataQuery = "Select * from DataSheet Where \"Test Case ID\" = '"	+ driver.confDtls.getPrefix()+testCaseId + "' Order by \"Test Step\"";
		}
		else
			testDataQuery = "Select * from DataSheet Where \"Test Case ID\" = "+testCaseId + " Order by \"Test Step\"";
		driver.log.info(" Reading Test Case Title ");
		try {
			testcaseid = Integer.parseInt(testCaseId);
		} catch (Exception e) {
			testDataQuery = "Select * from Template Where \"TestCase_Name\"='"+testCaseId + "'";
			title = "Template TestCase Name : "+testCaseId;
		}

		try {
			/*Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			Charset cs1 = Charset.forName("UTF-8");
			//Connection conn = DriverManager.getConnection("jdbc:odbc:Driver={Microsoft Excel Driver (*.xls)};DBQ="+ xlsPath + ";DriverID=22;READONLY=false;", "", "");
			Connection conn = fillo.getConnection(xlsPath);
			Statement stmt = conn.createStatement();*/
			connection = fillo.getConnection(xlsPath);
			
			if(testcaseid!=0){
				String titleQuery ="";
				if(driver.confDtls.getPrefix().toString().length()!=0)
				{
					titleQuery = "Select TCaseTitle from TestCaseMaster where TcId='"+ driver.confDtls.getPrefix()+testCaseId+"'";
				}
				else
				{
					titleQuery = "Select TCaseTitle from TestCaseMaster where TcId="+ testCaseId;
				}
				try{
					Recordset rs=connection.executeQuery(titleQuery);
					while (rs.next()) {
						String str = rs.getField("TcaseTitle").toString();
						if (str != null)
							title = str;
						break;
					}
					rs.close();
				} catch (Exception e) {
					System.out.println("Encountered Exception while querying sheet \"TestCaseMaster\" for Test Case titles");
					System.out.println(e.getMessage());
					System.out.println("Using default title :: " + title);
				}
				driver.log.info(" Read TestCase Title :: " + title);
				driver.TestCaseDetails.put(testcaseid,title);
			}

			if(driver.setup_TestData.isEmpty()){

				driver.log.info(" Reading Values for Template test data ");
				String setUpQuery = "Select * from Template_TestData";
				try{
					/*Statement stmt=null;
					
					  stmt = connection.createStatement();*/
					Recordset rs=connection.executeQuery(setUpQuery);
					
					
					//Issue ::Fillo.Recordset cannot be cast to java.sql.ResultSet
					
					//ResultSetMetaData rsmd = ((ResultSet) rs).getMetaData();
					//int count = rsmd.getColumnCount();
					
					ArrayList<String> str = rs.getFieldNames();
					System.out.println("Coulmns count:"+str.size());
					int count =str.size();
					
					while (rs.next()) {

						Properties props = new Properties();

						String name =  rs.getField("Templatecase_Name").toString().trim().toLowerCase();

						int i = 1 ;
						

						while(i <count){
							String key = rs.getField(i).name();
							
							
							String value = rs.getField(i).value();
							if(value!=null&&value!="Index"&&value!="Templatecase_Name")
								props.put(key, value);
							i++;
						}
						int keycount = 1 ;

						if(driver.setup_TestDataCounter.containsKey(name))
							keycount = driver.setup_TestDataCounter.get(name);

						driver.setup_TestDataCounter.put(name,keycount+1);

						driver.setup_TestData.put(name+"#"+keycount, props);
					}
					rs.close();
				}catch(Exception e){

					driver.log.error(" Encountered Exception while reading Template TestData ");
					System.out.println();
					System.out.println();
					driver.log.error(e.getMessage());

					System.out.println("Encountered Exception while querying sheet for Template TestData");
					System.out.println(e.getMessage());
				}
			}

			if (driver.reportReq && driver.TestCaseReq.isEmpty()) {

				driver.log.info(" Reading Requirement ID's ");

				String reqQuery = "Select TcId,Requirement from TestCaseMaster where tcID is not null";

				try{
					Recordset rs=connection.executeQuery(reqQuery);
					while (rs.next()) {

						String str = rs.getField("TCID").toString();
						if(str == null){
							break;
						}
						String tc_id = str.toLowerCase().replace(".0","");
						String req_name = rs.getField("Requirement").toString();
						String req = "";
						if(driver.TestCaseReq.containsKey(req_name)){
							req = driver.TestCaseReq.get(req_name);
						}
						driver.TestCaseReq.put(req_name,req + tc_id + ",");
					}
					rs.close();
				}catch(Exception e){
					driver.log.error(" Encountered Exception while reading Requirement ID's"); 
					driver.log.error(e.getMessage()); 

					System.out.println("Encountered Exception while querying sheet for Requirements");
					System.out.println(e.getMessage());
				}
			}

			if (driver.reportPriority && driver.TestCasePriority.isEmpty()) {
				driver.log.info(" Reading Priority Values ");
				String reqQuery = "Select TcId,Priority from TestCaseMaster where tcID is not null";

				try{
					Recordset rs=connection.executeQuery(reqQuery);

					while (rs.next()) {

						String str = rs.getField("TCID").toString();
						if(str == null){
							break;
						}

						String tc_id = str.toLowerCase().replace(".0","");
						String priority_name = rs.getField("Priority").toString();
						String priority = "";
						if(driver.TestCasePriority.containsKey(priority_name))
							priority = driver.TestCasePriority.get(priority_name);

						driver.TestCasePriority.put(priority_name,priority + tc_id + ",");
					}
					rs.close();
				}catch(Exception e){
					driver.log.error(" Encountered Exception while reading Priority Values");
					driver.log.error(e.getMessage());

					System.out.println("Encountered Exception while querying sheet for Priority");
					System.out.println(e.getMessage());
				}
			}

			if(driver.useObjectRepo && driver.objRepo.isEmpty()){

				driver.log.info(" Reading Object Repository "); 

				String objquery= "Select * from ObjectRepo";

				try {

					Recordset rs=connection.executeQuery(objquery);

					while (rs.next()) {
						String key = rs.getField("Object Name").toString();
						String value = rs.getField("Object Locator").toString();
						if(key == null)
							break;
						driver.objRepo.setProperty(key.toLowerCase(), value);
					}
					rs.close();
				} catch (Exception e) {
					driver.log.error(" Encountered Exception while reading Object Repository "); 
					System.out.println();
					driver.log.error(e.getMessage());
					System.out.println(e.getMessage());
					System.out.println("Encountered Exception while querying sheet ObjectRepo ");
					System.out.println(e.getMessage());
				}

				driver.useObjectRepo = false;
				System.out.println();
			}

			if (driver.parameterDetails.isEmpty()) {

				driver.log.info(" Reading Parameter Values ");

				String dataQuery = "Select * from TestData where ParameterName is not null";

				try{
					Recordset rs=connection.executeQuery(dataQuery);
					while (rs.next()) {

						String str = rs.getField("ParameterName").toString();
						if(str == null)
							break;

						String param_name = str.toLowerCase();
						if(!driver.testDataCounter.containsKey(param_name)){
							driver.testDataCounter.put(param_name,1);
						}else{
							int n = driver.testDataCounter.get(param_name);
							driver.testDataCounter.put(param_name,n+1);
						}
						String param_val = rs.getField("ParameterValue").toString();
						String index = rs.getField("index").toString();
						if (index == null)
							index = "1";
						index = index.replace(".0", "");
						String param_key = param_name + index;
						driver.parameterDetails.put(param_key, param_val);
					}
					rs.close();
				}catch(Exception e){

					driver.log.error(" Encountered Exception while reading Parameter Values");
					driver.log.error(e.getMessage());

					System.out.println("Encountered Exception while querying sheet for TestData");
					System.out.println(e.getMessage());
				}
			}

			driver.log.info(" Reading Test Case Steps ");


			Recordset result=connection.executeQuery(testDataQuery);
			while (result.next()) {

				TestDataDetails tdd = new TestDataDetails();

				tdd.setTestCaseID(testCaseId);
				tdd.setTestCaseTitle(title);
				tdd.setTestDataID(Integer.parseInt(result.getField("Test Step").replace(".0", "")));
				tdd.setWorkingPage(result.getField("Window"));
				tdd.setDataFields(result.getField("Data Fields"));



				tdd.setDataValues(result.getField("Data Values"));
				tdd.setActionType(result.getField("Action"));
				tdd.setCondition(result.getField("Condition"));
				tdd.setBrowserType(result.getField("Browser"));
				tdd.setFieldName(result.getField("Field Name"));
				tdd.setComments(result.getField("comments"));
				testData.put(tdd.getTestDataID(), tdd);
			}

			result.close();
			//stmt.close();
			connection.close();


		
	}
	catch (FilloException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	catch(Exception e){
		driver.log.error(" Encountered Exception while reading testdata for given case ");
		driver.log.error(e.getMessage());
		System.out.println("Encountered Exception while trying to retrieve details for test case :: " + driver.confDtls.getPrefix()+testCaseId);
		System.out.println(e.getMessage());
	}

	return testData;
}

/**
 * This is to read the details from DataSheet table in database
 */

private HashMap<Integer, TestDataDetails> readFromDB() {

	driver.log.info(" Reading TestData for test case " + testCaseId + " from Database ");

	HashMap<Integer, TestDataDetails> testData = new HashMap<Integer, TestDataDetails>();

	String dbName = driver.miscProps.getProperty("dbName");
	String password = driver.miscProps.getProperty("password");
	String userName = driver.miscProps.getProperty("userName");
	String jdbcDriver = driver.miscProps.getProperty("driver");
	String url = driver.miscProps.getProperty("url");
	String appName = driver.confDtls.getAppName();

	String titleQuery = "Select TCTitle from TestCaseMaster where TCid='"+ driver.confDtls.getPrefix()+testCaseId + "'"+" AND AppName='"+appName+"'";
	String query = "Select * FROM datasheet WHERE TestCaseID='"	+ driver.confDtls.getPrefix()+ testCaseId + "'"+" AND AppName='"+appName+"'";
	String title = "TestCase ID : " + testCaseId + " : Title Not available ";

	int testcaseid = 0;
	try {
		testcaseid = Integer.parseInt(testCaseId);
	} catch (Exception e) {
		title = "Template TestCase :: " + testCaseId;
	}

	driver.log.info(" Reading Test Case Title ");

	try {

		Class.forName(jdbcDriver);
		Connection conn = (Connection) DriverManager.getConnection(url + dbName,	userName, password);
		Statement stmt = ((java.sql.Connection) conn).createStatement();

		if(testcaseid!=0){
			try {
				ResultSet rs = stmt.executeQuery(titleQuery);
				while (rs.next()) {
					String str = rs.getString(1);
					if (str != null)
						title = str;
					break;
				}
				rs.close();
			} catch (Exception e) {
				System.out.println("Encountered Exception while querying DB for Test Case titles");
				System.out.println(e.getMessage());
				System.out.println("Using Default title :: " + title);
			}

			driver.log.info(" Read Title :: " + title);
			driver.TestCaseDetails.put(testcaseid, title);
		}	

		if (driver.reportReq && driver.TestCaseReq.isEmpty()) {

			driver.log.info(" Reading Requirement ID's ");

			String reqQuery = "Select TcId,Requirement from TestCaseMaster where AppName='"+appName+"'";
			try{
				ResultSet rs = stmt.executeQuery(reqQuery );
				while (rs.next()) {
					String tc_id = rs.getString(1).toLowerCase();
					String req_name = rs.getString(2);
					String req = "";
					if(driver.TestCaseReq.containsKey(req_name))
						req = driver.TestCaseReq.get(req_name);
					driver.TestCaseReq.put(req_name,req + tc_id + ",");
				}
				rs.close();
			}catch(Exception e){
				driver.log.error(" Encountered Exception while reading Requirement ID's"); 
				driver.log.error(e.getMessage()); 
				System.out.println("Encountered Exception while querying for Requirement");
				System.out.println(e.getMessage());
			}
		}

		if (driver.useObjectRepo && driver.objRepo.isEmpty()) {
			driver.log.info(" Reading Object Repository ");

			String objQuery = "Select * from ObjectRepo where AppName='"+appName+"'";
			try{
				ResultSet rs = stmt.executeQuery(objQuery);
				while (rs.next()) {
					String key = rs.getString(1).toLowerCase();
					String value = rs.getString(2);
					driver.objRepo.setProperty(key, value);
				}
				rs.close();
			}catch(Exception e){
				driver.log.error(" Encountered Exception while reading Object Repository "); 
				driver.log.error(e.getMessage()); 
				System.out.println("Encountered Exception while querying for Object Repository ");
				System.out.println(e.getMessage());
			}
			driver.useObjectRepo = false;
		}

		if (driver.reportPriority && driver.TestCasePriority.isEmpty()) {
			driver.log.info(" Reading Priority Values ");

			String reqQuery = "Select TcId,Priority from TestCaseMaster where AppName='"+appName+"'";
			try{
				ResultSet rs = stmt.executeQuery(reqQuery );
				while (rs.next()) {
					String tc_id = rs.getString(1).toLowerCase();
					String priority_name = rs.getString(2);
					String priority = "";
					if(driver.TestCasePriority.containsKey(priority_name))
						priority = driver.TestCasePriority.get(priority_name);
					driver.TestCasePriority.put(priority_name,priority + tc_id + ",");
				}
				rs.close();
			}catch(Exception e){
				driver.log.error(" Encountered Exception while reading Priority Values");
				driver.log.error(e.getMessage());

				System.out.println("Encountered Exception while querying for Priority ");
				System.out.println(e.getMessage());
			}
		}

		if (driver.parameterDetails.isEmpty()) {	
			driver.log.info(" Reading Parameter Values ");
			String dataQuery = "Select * from testdata where AppName='"+appName+"'";
			try{
				ResultSet rs = stmt.executeQuery(dataQuery);
				while (rs.next()) {
					String param_name = rs.getString("ParameterName").toLowerCase();
					if(!driver.testDataCounter.containsKey(param_name)){
						driver.testDataCounter.put(param_name,1);
					}else{
						int n = driver.testDataCounter.get(param_name);
						driver.testDataCounter.put(param_name,n+1);
					}
					String param_val = rs.getString("ParameterValue");
					String index = rs.getString("index");
					if (index == null)
						index = "1";
					String param_key = param_name + index;
					driver.parameterDetails.put(param_key, param_val);
				}
				rs.close();
			}catch(Exception e){
				driver.log.error(" Encountered Exception while reading Parameter Values");
				driver.log.error(e.getMessage());
				System.out.println("Encountered Exception when trying to read the test data");
				System.out.println(e.getMessage());
			}
		}

		if(driver.setup_TestData.isEmpty()){
			driver.log.info(" Reading Values for Template test data ");

			String setUpQuery = "Select * from Template_TestData where AppName='"+appName+"'";
			try{
				ResultSet rs = stmt.executeQuery(setUpQuery);
				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				while (rs.next()) {

					String temp = rs.getString(2).trim().toLowerCase();

					Properties props = new Properties();

					int i = 3 ;

					while(i <= count){
						String key = rsmd.getColumnLabel(i).toLowerCase();
						String value = rs.getString(i);
						props.put(key, value);
						i++;
					}

					int keycount = 1 ;

					if(driver.setup_TestDataCounter.containsKey(temp))
						keycount = driver.setup_TestDataCounter.get(temp);

					driver.setup_TestDataCounter.put(temp,keycount+1);

					driver.setup_TestData.put(temp+"#"+keycount, props);
				}
				rs.close();
			}catch(Exception e){
				driver.log.error(" Encountered Exception while reading Template Test Data ");
				driver.log.error(e.getMessage());

				System.out.println("Encountered Exception while querying Database for Template TestData");
				System.out.println(e.getMessage());
			}
		}

		driver.log.info(" Reading Test Case Steps ");

		ResultSet rs = stmt.executeQuery(query);

		while (rs.next()) {

			TestDataDetails tdd = new TestDataDetails();

			tdd.setTestCaseID(testCaseId);
			tdd.setTestCaseTitle(title);
			tdd.setTestDataID(Integer.parseInt(rs.getString("TestStep")));
			tdd.setWorkingPage(rs.getString("Window"));
			tdd.setDataFields(rs.getString("DataFields"));
			tdd.setDataValues(rs.getString("DataValues"));
			tdd.setActionType(rs.getString("Action"));
			tdd.setCondition(rs.getString("Condition"));
			tdd.setBrowserType(rs.getString("Browser"));
			tdd.setFieldName(rs.getString("FieldName"));
			tdd.setComments(rs.getString("Comments"));
			testData.put(tdd.getTestDataID(), tdd);

		}
		rs.close();
		stmt.close();
		conn.close();

	} catch (SQLException e) {

		driver.log.error(" Encountered SQLException while reading testdata for given case ");
		driver.log.error(e.getMessage());

		System.out.println("SQLException while querying DataBase for TestData");
		System.out.println(e.getMessage());
	} catch (ClassNotFoundException e) {

		driver.log.error(" Encountered ClassNotFoundException while reading testdata for given case ");
		driver.log.error(e.getMessage());

		System.out.println("JDBC driver class not found. Please add the required jar files to CLASSPATH ");
		System.out.println(e.getMessage());
		driver.stop();
	}catch (Exception e) {

		driver.log.error(" Encountered Exception while reading testdata for given case ");
		driver.log.error(e.getMessage());

		System.out.println("Exception while querying for data sheet");
		System.out.println(e.getMessage());
		driver.stop();
	}
	return testData;
}

/**
 * Method to parse Properties object to TestDataDetails
 * @throws UnsupportedEncodingException 
 */

private TestDataDetails parseProps(Properties props)  {

	TestDataDetails tdd = new TestDataDetails();
	tdd.setWorkingPage(props.getProperty("window", "Working Page"));
	tdd.setDataFields(props.getProperty("datafields", ""));
	tdd.setDataValues(props.getProperty("datavalues", ""));
	tdd.setActionType(props.getProperty("name"));
	tdd.setCondition(props.getProperty("condition", ""));
	tdd.setBrowserType(props.getProperty("browser", "common"));
	tdd.setFieldName(props.getProperty("fieldname", ""));
	tdd.setComments(props.getProperty("comment", ""));
	return tdd;

}

}