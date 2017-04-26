package com.java;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import org.apache.log4j.Logger;

import javax.xml.parsers.*;
import javax.xml.transform.TransformerException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import testlink.api.java.client.*;

/**
 * Class to read the list of testcases to be executed
 */

@SuppressWarnings({"deprecation","unchecked"})
public class ReadTestCaseIDs {
	
	public Properties miscProps;
	private ArrayList<Integer> testCaseIDS = new ArrayList<Integer>();
	private Logger log;
	
	/**
	 * Constructor to read the properties for TestLink 
	 */
	
	public ReadTestCaseIDs(Properties miscProps,Logger log) {
		this.miscProps = miscProps;
		this.log = log;
	}
	
	/**
	 *  Method to query the TestLink Database and get the list of Testcases based on the testplan name provided
	 */
	
	public ArrayList<Integer> readfromTestLink() {
		
		log.info(" Reading from testlink ");
		
		String devKey = miscProps.getProperty("APIKEY");
		log.info(" DevKey :: " + devKey);

		String url = miscProps.getProperty("tcm_URL");
		log.info(" URL :: " + url);
		
		String project = miscProps.getProperty("projectName");
		log.info(" Project :: " + project);
		
		String testPlan = miscProps.getProperty("projectTestPlanName");
		log.info(" TestPlan :: " + testPlan);
		
		try {

			TestLinkAPIClient testlinkAPIClient = new TestLinkAPIClient(devKey, url);

			if(!testlinkAPIClient.isConnected){
				log.warn(" Unable to connect to testlink ");
				System.out.println("Unable to connect to TestLink");
				log.warn(" Stopping th thread ");
				RunTest.configrowCount++;
				Thread.currentThread().stop();
			}
			
			TestLinkAPIResults result = testlinkAPIClient.getCasesForTestPlan(project, testPlan);
			Map<Object,Object> map = result.getData(0);
			Object objArray[] = map.entrySet().toArray();
			
			try {
				for(Object obj : objArray){
					String  testcaseTitle =(String)((HashMap <Object,Object>)((Object [])((Map.Entry<Object,Object>)obj).getValue())[0]).get("name");
					log.info(" Read Test case :: " + testcaseTitle);
					String tcid = testcaseTitle.split(":")[0];
					if(miscProps.getProperty("prefix").length()==0)
						testCaseIDS.add(Integer.parseInt(tcid));
					else
					{
						testCaseIDS.add(Integer.parseInt(tcid.substring(miscProps.getProperty("prefix").length())));
					}
				}
			}catch(Exception e){
				System.out.println("Exception while retrieving cases from testlink ");
				log.error(" Exception while retrieving testcases from testlink ");
				System.out.println(e.getMessage());
				log.error(e.getMessage());
			}
		
		}catch(Exception e){
			System.out.println("Encountered Exception while communicating with testLink \n " + e.getMessage());
			log.error(" Exception while retrieving testcases from testlink ");
			
			System.out.println(e.getMessage());
			log.error(e.getMessage());
			
			System.out.println("Error to retrieve test case details for testplan ::" + testPlan);
			RunTest.configrowCount++;
			Thread.currentThread().stop();
		}
			Collections.sort(testCaseIDS);
			return testCaseIDS;
	}

	/**
	 * Method to query the testcases which are read in database based on feature Name
	 */
	
	public ArrayList<Integer> readfromDB(String testcase){
	
		log.info(" Reading testcase id's from DB for "  + testcase);
		
		String temp[]  = testcase.substring(3).split(",");
		String where = "";
		
		String query = "Select TCid from TestCaseMaster";
				
		if(testcase.startsWith("fe:")){
			for(String fe : temp)
				where = where + " Module='" + fe + "' OR ";		
		}
		
		if(testcase.startsWith("tt:")){	
			for(String tt : temp)
				where = where + " TestType='" + tt + "' OR ";
		}
	
		if(testcase.startsWith("rq:")){
			for(String rq : temp)
				where = where + " Requirement='" + rq + "' OR ";
		}
		
		if(testcase.startsWith("pr:")){
			for(String pr : temp)
				where = where + " Priority='" + pr + "' OR ";
		}
		
		if(where.length()!=0){
			where = where.substring(0,where.length()-3);
			query = query + " where  ( " + where + ")";
			query = query + " AND AppName='"+miscProps.getProperty("appName")+"'";
		} else{
			query = query + " where AppName='"+miscProps.getProperty("appName")+"'";
		}
		
		String url = miscProps.getProperty("url");
		log.info(" Database URL :: " + url);
		
		String jdbcDriver = miscProps.getProperty("driver");
		log.info(" JDBC Driver :: " + jdbcDriver);
		
		String userName = miscProps.getProperty("userName");
		log.info(" DB UserName :: "+ userName);
		
		String password = miscProps.getProperty("password");
		log.info(" DB Password :: "+password);
		
		String dbName = miscProps.getProperty("dbName");
		log.info(" DB Name :: " +dbName );
		
		try {

			Class.forName(jdbcDriver);
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			Statement stmt = conn.createStatement();
			
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next())
				testCaseIDS.add(Integer.parseInt(rs.getString(1)));
			
		} catch (ClassNotFoundException e) {
			
			log.error(" Encountered ClassNotFoundException ");
			log.error(e.getMessage());
			
			System.out.println("Unable to load the JDBC Class");
			System.out.println(e.getMessage());
			RunTest.configrowCount++;
			Thread.currentThread().stop();	
		} catch (SQLException e) {
			
			log.error(" Encountered SQLException ");
			log.error(e.getMessage());
			
			System.out.println("Encountered SQL Exception in querying the database for TestCase ID's");
			System.out.println(e.getMessage());
			RunTest.configrowCount++;
			Thread.currentThread().stop();
		}catch (Exception e) {
			
			log.error(" Encountered Unknown Exception ");
			log.error(e.getMessage());
						
			System.out.println("Encountered Exception in querying the database for TestCase ID's");
			System.out.println(e.getMessage());
			RunTest.configrowCount++;
			Thread.currentThread().stop();
		}
		Collections.sort(testCaseIDS);
		return testCaseIDS;
	}

	/**
	 * Method to read the test cases from XML File based on featureName
	 * @param prefix 
	 */
	
	public ArrayList<Integer> readfromXML(String testset,String fileName, String prefix){
	
		log.info(" Reading from XML DataSource :: " + fileName);
		
		fileName = "../TestInputs/"+ fileName;
		String temp[]  = testset.substring(3).split(",");
		String or = "";
		
		String xpath = "//testcasemaster/testcase";
		
		if(testset.toLowerCase().startsWith("fe:")){
			for(String fe : temp)
				or = or + " @Module='"+fe+"' or";	
		}
		
		else if( testset.toLowerCase().startsWith("tt:")){
			for(String tt : temp)
				or = or + " @TestType='"+tt+"' or";
		}
		
		else if( testset.toLowerCase().startsWith("rq:")){
			for(String req : temp)
				or = or + " @Requirement='"+req+"' or";
		}
		
		else if( testset.toLowerCase().startsWith("pr:")){
			for(String pr : temp)
				or = or + " @Priority='"+pr+"' or";
		}
		
		if(or.length()!=0){
			or = or.substring(0,or.length()-3);
			xpath = xpath + "[" + or + "]";
		}
		
		DocumentBuilderFactory docbuilderfactory = DocumentBuilderFactory.newInstance();
		Document document = null;
		
		try {
			DocumentBuilder docbuilder = docbuilderfactory.newDocumentBuilder();
			document = docbuilder.parse(fileName);
		} catch (ParserConfigurationException e) {
			log.error(" Encountered Parser Configuration Exception ");
			log.error(e.getMessage());
			System.out.println("Encountered error in creating the XML Parser");
			System.out.println(e.getMessage());
			RunTest.configrowCount++;
			Thread.currentThread().stop();
		}catch (SAXException e) {
			log.error(" Encountered SAX Exception ");
			log.error(e.getMessage());
			System.out.println("Error encountered in reading the XML document");
			System.out.println(e.getMessage());
			RunTest.configrowCount++;
			Thread.currentThread().stop();
		} catch (IOException e) {
			log.error(" Encountered IOException ");
			log.error(e.getMessage());
			System.out.println("Encountered IO Stream Application Error when reading the testdata file for test case id's");
			System.out.println(e.getMessage());
			RunTest.configrowCount++;
			Thread.currentThread().stop();
		}catch (Exception e) {
			log.error(" Encountered Unknown Eception ");
			log.error(e.getMessage());
			System.out.println("Encountered unknown Error when reading the testdata file for test case id's");
			System.out.println(e.getMessage());
			RunTest.configrowCount++;
			Thread.currentThread().stop();
		}
		
		try{
		
			NodeList testConfig = org.apache.xpath.XPathAPI.selectNodeList(document, xpath);
			int count = testConfig.getLength();

			if(count == 0){
				log.warn(" Zero Testcases are specified as part of tets case master ");
				System.out.println("No test cases are present as part of the Test Master");
				RunTest.configrowCount++;
				Thread.currentThread().stop();
			}
			
			int rows = 0;
			
			while(rows < count){
				
				Node row = testConfig.item(rows);
				if (row.getNodeType() == Node.ELEMENT_NODE){
					int testcaseid;
						if(row.getAttributes().getNamedItem("ID").getTextContent().toString().matches("^([A-Z||a-z]).*")&&prefix.length()!=0)
					      testcaseid = Integer.parseInt(row.getAttributes().getNamedItem("ID").getTextContent().toString().substring(prefix.length()).replace(".0",""));
					  else
						   testcaseid = Integer.parseInt(row.getAttributes().getNamedItem("ID").getTextContent());
					testCaseIDS.add(testcaseid);
				}
				rows++;
			}
		}catch(TransformerException e){
			
			log.error(" Encountered Transformer Exception ");
			log.error(e.getMessage());
			
			System.out.println("Can't find nodes with tag <testConfig> : Encountered TransformerException");
			System.out.println(e.getMessage());
		}catch(Exception e){
			
			log.error(" Encountered unknown exception ");
			log.error(e.getMessage()); 
			System.out.println("Encountered  Unknown Exception");
			System.out.println(e.getMessage());
		}
		Collections.sort(testCaseIDS);
		return testCaseIDS;
	}
	
	/**
	 * Method to read the test cases from XML File based on featureName
	 * @param prefix 
	 */
	
	public ArrayList<Integer> readfromExcel(String testcase,String fileName, String prefix){
	
		log.info(" Reading from Excel Datasource :: " + fileName);
		
		fileName = "../TestInputs/"+ fileName;
		
		String temp[]  = testcase.substring(3).split(",");
		String where = "";
		
		String query = "Select TCid from [TestCaseMaster$]";
				
		if(testcase.startsWith("fe:")){
			for(String fe : temp)
				where = where + " Module='" + fe + "' OR ";		
		}
		
		if(testcase.startsWith("tt:")){	
			for(String req : temp)
				where = where + " TestType='" + req + "' OR ";
		}
	
		if(testcase.startsWith("rq:")){
			for(String req : temp)
				where = where + " Requirement='" + req + "' OR ";
		}
		
		if(testcase.startsWith("pr:")){
			for(String req : temp)
				where = where + " Priority='" + req + "' OR ";
		}
		
		if(where.length()!=0){
			where = where.substring(0,where.length()-3);
			query = query + " where " + where;
		}
		
		try {
			
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			Connection conn = DriverManager.getConnection("jdbc:odbc:Driver={Microsoft Excel Driver (*.xls)};DBQ="+fileName+";DriverID=22;READONLY=false","","");
		    Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery(query);
		 
			while(rs.next()){
				String str = rs.getString(1);
				if(str != null)
				{
					if(str.matches("^([A-Z||a-z]).*")&&prefix.length()!=0)
					
						  testCaseIDS.add(Integer.parseInt(str.substring(prefix.length()).replace(".0","")));
					
					else
					
					testCaseIDS.add(Integer.parseInt(str.replace(".0","")));
				
				}
					
				else
					break;
			}
		} catch (ClassNotFoundException e) {
			log.error(" Encountered ClassNotFoundException ");
			log.error(e.getMessage());
			System.out.println("Unable to load the JDBC Class");
			System.out.println(e.getMessage());
			RunTest.configrowCount++;
			Thread.currentThread().stop();
		} catch (SQLException e) {
			log.error(" Encountered SQLException ");
			log.error(e.getMessage());
			System.out.println("Encountered SQL Exception in querying the TestCaseMaster for TestCaseId's");
			System.out.println(e.getMessage());
			RunTest.configrowCount++;
			Thread.currentThread().stop();
		}catch (Exception e) {
			
			log.error(" Encountered unknown Exception ");
			log.error(e.getMessage());
			System.out.println("Encountered unknown Exception in querying the TestCaseMaster for TestCaseId's");
			System.out.println(e.getMessage());
		}
		Collections.sort(testCaseIDS);
		return testCaseIDS;
	}
}