package com.java.importnexport;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;


import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

import com.java.objects.ConfigDetails;

/**
 * This Java class is to read valid test configurations from an XML / Excel sheet
 */

public class ImportConfigDetails {

	private String configFile;
	//private Logger log;
	public String prefix="";
	/**
	 * Constructor of class which takes name of ConfigFile from TestInputs directory as parameter 
	 * @param configFile
	 */
	
	public ImportConfigDetails(String configFile){
		this.configFile = configFile;
	}
	
	/**
	 * Method to return the valid Test Configurations as ArrayList 
	 * @return
	 */
	
	public ArrayList<ConfigDetails> readConfigData(){
		
		if(configFile.contains(".xml"))
			return readFromXML();
	
		if(configFile.contains(".xls"))
			return readFromExcel();
		
		if(configFile.contains("DB"))
			return readFromDB();
		
		
		return new ArrayList<ConfigDetails>(); 
	}
	
	/**
	 * This method is to read testConfigurations from the XML File and return the valid in an ArrayList Object
	 * @param configFile The name of the XML file which is in TestInputs Folder
	 * @return An ArrayList List object which has valid TestConfigurations 
	 */
	
	private ArrayList<ConfigDetails> readFromXML(){
		
		configFile = "../TestInputs/"+ configFile;
		
		DocumentBuilderFactory docbuilderfactory = DocumentBuilderFactory.newInstance();
		Document document = null;
		try {
			DocumentBuilder docbuilder = docbuilderfactory.newDocumentBuilder();
			document = docbuilder.parse(configFile);
		} catch (ParserConfigurationException e) {
			System.out.println("Encountered error in creating the XML Parser");
			System.exit(1);
		}catch (SAXException e) {
			System.out.println("Error encountered in reading the xml document");
			System.out.println(e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Encountered IO Stream Application Error when reading the property file");
			System.out.println(e.getMessage());
			System.exit(1);
		}
	
		ArrayList<ConfigDetails> configRows = new ArrayList<ConfigDetails> ();

		try{
			NodeList testConfig = org.apache.xpath.XPathAPI.selectNodeList(document, "//testConfig[@run='true']");
			int count = testConfig.getLength();
			if(count == 0){
				System.out.println("No test configurations Specified");
				System.exit(1);
			}
			int rows = 0;
			System.out.println("Configuration & execution List");
			while(rows < count){
				System.out.println("--------------------------------");
				Node row = testConfig.item(rows);
				NodeList childs = row.getChildNodes();
				
				Properties props = new Properties();

				int childCount = childs.getLength();
				for (int i = 0; i < childCount; i++) {
					if (childs.item(i).getNodeType() == Node.ELEMENT_NODE){
						String key = childs.item(i).getNodeName().trim();
						String value = childs.item(i).getTextContent().trim();
						props.setProperty(key,value);
					}
				
				}
				rows = rows + 1;
				System.out.println(" Details of Config Row : " + rows);
				ConfigDetails confDtls = parseProperties(props);
				if(validate(confDtls))	
					configRows.add(confDtls);
				System.out.println();
				
			}
		}catch(TransformerException e){
			System.out.println("Can't find nodes with tag <testConfig> : Encountered TransformerException");
			System.out.println(e.getMessage());
			System.exit(1);
		}
		return configRows;
	}
	
	/**
	 * This method takes a Property object from readFromXML() and coverts it to a ConfigDetails object
	 * @param props The property object which contains key-value pairs of test Configurations 
	 * @return Returns a parsed object of type ConfigDetails
	 */

	private ConfigDetails parseProperties(Properties props){
		
		ConfigDetails cnfdtl = new ConfigDetails();
		
		cnfdtl.setScriptPath(props.getProperty("URL"));
		cnfdtl.setBrowser(props.getProperty("browser", ""));
		cnfdtl.setTestDataSource(props.getProperty("testDataSource",""));
		cnfdtl.setProperties("..//properties//" + props.getProperty("Application")+".xml");
		cnfdtl.setAppName(props.getProperty("Application"));
		String str = props.getProperty("testcases","");
	
		
		try{
			 Document document;
			 DocumentBuilderFactory docbuilderfactory = DocumentBuilderFactory.newInstance();
	 	     docbuilderfactory = DocumentBuilderFactory.newInstance();
					
			 DocumentBuilder  docbuilder = docbuilderfactory.newDocumentBuilder();
			 document = docbuilder.parse(cnfdtl.getProperties().toString());
					
				NodeList propertyNodes = null;
				propertyNodes = org.apache.xpath.XPathAPI.selectNodeList(document, "//" + "appDetails");
				
				if (propertyNodes.getLength() != 0) {

					Node nodes = propertyNodes.item(0);

					if (nodes.getNodeType() == Node.ELEMENT_NODE) {

						NodeList childs = nodes.getChildNodes();

						int count = childs.getLength();

						for (int i = 0; i < count; i++)
							if (childs.item(i).getNodeType() == Node.ELEMENT_NODE) {
								String key = childs.item(i).getNodeName().trim();
								String value = childs.item(i).getTextContent().trim();
							
								if(key.equalsIgnoreCase("prefix")&&value.length()!=0)
								{
									System.out.println("Setting prefix:"+value);
									prefix=value;
									cnfdtl.setPrefix(prefix);
									
								}
							}
					}
				} 
				}catch(Exception e)
				{
					
				}
		if (str.toLowerCase().startsWith("fe:")){
			cnfdtl.setTestCases(str);
			System.out.println("Test cases for Module "+ str.substring(3) + "  are executed");
		}
		else if (str.toLowerCase().startsWith("tt:")){
			cnfdtl.setTestCases(str);
			System.out.println("Test cases for Test Type  "+ str.substring(3) + "  are executed");
		}else if (str.toLowerCase().startsWith("rq:")){
			cnfdtl.setTestCases(str);
			System.out.println("Testcases for the Requirement "+ str.substring(3)+ "  are executed");
		}else if (str.toLowerCase().startsWith("tp:")){
			cnfdtl.setTestCases(str);
			System.out.println("Testcases for the test Plan "+ str.substring(3)+ "  are executed");
		}else if (str.toLowerCase().startsWith("pr:")){
			cnfdtl.setTestCases(str);
			System.out.println("Testcases for the Priority "+ str.substring(3)+ "  are executed");
		}
		else if (str.equalsIgnoreCase("all") || str.equalsIgnoreCase("")){
			cnfdtl.setTestCases("all");
			System.out.println("All the test cases are executed");
		}else if(str.contains(".") || str.contains("*")){
			cnfdtl.setTestCases(str);
			cnfdtl.setFuncDriven(true);
			System.out.println("Testcases matching regex " + str + " are executed");
		}else if(str.matches("^([A-Z||a-z]).*")&& prefix.length()==0){
			cnfdtl.setTestCases(str);
			cnfdtl.setFuncDriven(true);
			System.out.println("Testcases for the Functions "+ str + "  are executed");
		}
		else{
			ArrayList<Integer> testCases = new ArrayList<Integer>();
			testCases = parseTokens(str,cnfdtl);
			System.out.print("TEST CASES ARE HERE : ----- ");
			cnfdtl.setTestCasesToBeExecuted(testCases);
			System.out.println(cnfdtl.getPrefix()+testCases);
		}
		return cnfdtl;
	}
	
	/**
	 * This method is to parse the tokens in String format to an ArrayList of type Integer 
	 * @param str This parameter contains the tokens to be parsed
	 * @param cnfdtl 
	 * @return An ArrayList List object which has testcaseID's
	 */
	
	private ArrayList<Integer> parseTokens(String str, ConfigDetails cnfdtl) {

	ArrayList<Integer> testCases = new ArrayList<Integer>();
	
	if(!Character.isDigit(str.charAt(0)))
	{ 
		//System.out.println("Setting the prefix: "+str.substring(0, 3));
		
		
		//cnfdtl.setPrefix(str.substring(0, 3));
		str=str.replace(prefix, "");
		if(str.matches(".*([A-Z||a-z]).*"))
		{
			System.out.println("Invalid format");
			System.out.println(str);
			System.out.println("Invalid format in 'testcases' tag in config file, please provide unic 'Prefix' ");
			System.exit(1);
		}
		
	}
	
			
	String[] tokens = str.split(",");
	
	for (int i = 0; i < tokens.length; i++) {
		if(!tokens[i].contains("-"))
			System.out.println("Tokens: " + cnfdtl.getPrefix()+tokens[i]);
		else
			System.out.println("Tokens: " + cnfdtl.getPrefix()+tokens[i].substring(0, tokens[i].indexOf("-"))+"-"+cnfdtl.getPrefix()+tokens[i].substring(tokens[i].indexOf("-")+1,tokens[i].length()));
		if (!tokens[i].contains("-"))
		{
		//	System.out.println(tokens[i]);
			if(!testCases.contains(Integer.parseInt(tokens[i])))
			{
				testCases.add(Integer.parseInt(tokens[i]));
				
			}
			
		}
		else {
			
			String[] range = tokens[i].split("-");
			int f = Integer.parseInt(range[0]);
			int t = Integer.parseInt(range[1]);
			if(!testCases.contains(f))
			{
				testCases.add(f);
			}
		
			while (f != t) {
				f = f + 1;
				if(!testCases.contains(f))
				{
					testCases.add(f);
					
				}
			
			}
		}
	}
		return testCases;
	}
	
	/**
	 * This method is to read testConfigurations from the Excel File and return the valid in an ArrayList Object
	 * @param configFile The name of the ExcelL file which is in TestInputs Folder
	 * @return An ArrayList List object which has valid TestConfigurations 
	 */
	
	private ArrayList<ConfigDetails>  readFromExcel() {

		ArrayList<ConfigDetails>  configRows = new ArrayList<ConfigDetails> ();

		String xlsPath = "../TestInputs/" + configFile;

		System.out.println("Config File Path : " + xlsPath);

		String sql = "Select \"Application\",\"Test Cases to be Executed\",\"Application URL\", \"Browser Type\",\"TestData\"  from [Sheet1$]";

		try {

			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			Connection conn = DriverManager.getConnection("jdbc:odbc:Driver={Microsoft Excel Driver (*.xls)};DBQ="+ xlsPath + ";DriverID=22;READONLY=false;useUnicode=true&characterEncoding=UTF-8;", "", "");
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);

			System.out.println("Configuration & execution List");
			int row = 1;
			
			while (rs.next()) {
				System.out.println("--------------------------------");
				ConfigDetails confDtls = new ConfigDetails();
				
				String str = rs.getString(1);
				
				if(str == null)
					continue;
				
				System.out.println(" Details of Config Row : " + row);
				confDtls.setAppName(str);
				confDtls.setProperties("../properties/" + str + ".xml");
				confDtls.setScriptPath(rs.getString(3));
				confDtls.setBrowser(rs.getString(4));
				confDtls.setTestDataSource(rs.getString(5));
				
				str = rs.getString(2);
				
				if (str.toLowerCase().startsWith("fe:")){
					confDtls.setTestCases(str);
					System.out.println("Test cases for Module "+ str.substring(3) + "  are executed");
				}
				else if (str.toLowerCase().startsWith("tt:")){
					confDtls.setTestCases(str);
					System.out.println("Test cases for TestType "+ str.substring(3) + "  are executed");
				}else if (str.toLowerCase().startsWith("rq:")){
					confDtls.setTestCases(str);
					System.out.println("Test cases for requirement "+ str.substring(3) + "  are executed");
				}else if (str.toLowerCase().startsWith("pr:")){
					confDtls.setTestCases(str);
					System.out.println("Testcases for the Priority "+ str.substring(3)+ "  are executed");
				}else if (str.equalsIgnoreCase("all") || str.equalsIgnoreCase("")){
					confDtls.setTestCases("all");
					System.out.println("All the test cases are executed");
				}
				else if (str.startsWith("tp:")){
					confDtls.setTestCases(str);
					System.out.println("Testcases for the test Plan "+ str.substring(3)+ "  are executed");
				}else if(str.contains(".") || str.contains("*")){
					confDtls.setTestCases(str);
					confDtls.setFuncDriven(true);
					System.out.println("Testcases matching the regex " + str + " are executed ");
				} else if(str.matches(".*([A-Z||a-z]).*")){
					confDtls.setTestCases(str);
					confDtls.setFuncDriven(true);
					System.out.println("Testcases for the Functions "+ str + "  are executed");
				}
				else{
					ArrayList<Integer> testCases = new ArrayList<Integer>();
					testCases = parseTokens(str,confDtls);
					System.out.print("TEST CASES ARE HERE : ----- ");
					confDtls.setTestCasesToBeExecuted(testCases);
					System.out.println(testCases);
				}
				row = row + 1;
				if(validate(confDtls))
					configRows.add(confDtls);
			}
			rs.close();
			st.close();
			conn.close();
		} catch (SQLException e) {
			System.out.println("Encountered SQLException while querying the database \n " + e.getMessage());
			System.exit(1);
		} catch (ClassNotFoundException e) {
			System.out.println("Respective class not found. Please add the jar \n " + e.getMessage());
			System.exit(1);
		}catch (Exception e) {
			System.out.println("Exception while reading the Config.xls for Configurations \n " + e.getMessage());
			System.exit(1);
		}
		return configRows;
	}

	/**
	 *  This method is to validate the ConfigDetails Object which is read as part of the testConfigurations
	 * @param confDtls The ConfigDetails object which is to be validated is passed as a parameter
	 * @return The boolean status of the ConfigDetails object . true if valid & false if invalid
	 */
	
	private ArrayList<ConfigDetails>  readFromDB(){
		
		ArrayList<ConfigDetails>  configRows = new ArrayList<ConfigDetails> ();
		String sql = "Select * from config where execute=1";
		String dbName = "phpscheduler_old";
		String password ="root";
		String userName ="root";
		String jdbcDriver ="com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/";
			
		try {

			
			Class.forName(jdbcDriver);
			Connection conn = DriverManager.getConnection(url + dbName,	userName, password);
			Statement stmt = conn.createStatement();

		
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);

			System.out.println("Configuration & execution List");
			int row = 1;
			
			while (rs.next()) {
				System.out.println("--------------------------------");
				ConfigDetails confDtls = new ConfigDetails();
				
				String str = rs.getString(1);
				
				if(str == null)
					continue;
				
				System.out.println(" Details of Config Row : " + row);
				confDtls.setAppName(rs.getString("AppName"));
				
				confDtls.setProperties("settings.DB");
				confDtls.setScriptPath(rs.getString("URL"));
				confDtls.setBrowser(rs.getString("Browser"));
				confDtls.setTestDataSource("DB");
				
					ArrayList<Integer> testCases = new ArrayList<Integer>();
					testCases = parseTokens(str,confDtls);
					System.out.print("TEST CASES ARE HERE : ----- ");
					confDtls.setTestCasesToBeExecuted(testCases);
					System.out.println(testCases);
				
				row = row + 1;
				if(validate(confDtls))
					configRows.add(confDtls);
			}
			rs.close();
			st.close();
			conn.close();
		} catch (SQLException e) {
			System.out.println("Encountered SQLException while querying the database \n " + e.getMessage());
			System.exit(1);
		} catch (ClassNotFoundException e) {
			System.out.println("Respective class not found. Please add the jar \n " + e.getMessage());
			System.exit(1);
		}catch (Exception e) {
			System.out.println("Exception while reading the Config.xls for Configurations \n " + e.getMessage());
			System.exit(1);
		}
		return configRows;
		
	}
	
	private boolean validate(ConfigDetails confDtls){
		
		boolean status = true;
		
		String errMsg = "";
			
		try{
			new URL(confDtls.getScriptPath());
		}catch(MalformedURLException e){
			status = false;
			errMsg = errMsg + " \nThe URL specified is not in a valid format";
		}
		
		if(confDtls.getBrowser().equalsIgnoreCase("")){
			errMsg = errMsg + " \nNo Browser is specified to run the Test. Using default Value \"firefox\"";
			confDtls.setBrowser("firefox");
		}
		
		String tdSource = confDtls.getTestDataSource();

		if(tdSource.equalsIgnoreCase("")){
			errMsg = errMsg + " \nNo Test Data Source is specified . Using default value as DB";
			confDtls.setFuncDriven(false);
			confDtls.setTestDataSource("DB");
		}

		if(tdSource.endsWith(".java") ){
			confDtls.setFuncDriven(true);
			confDtls.setTestDataSource(tdSource.substring(0,tdSource.length()-5));
		}
		if(tdSource.equalsIgnoreCase("DB")|| tdSource.equalsIgnoreCase("DataBase") ){
			confDtls.setFuncDriven(false);
		}
		else if(tdSource.endsWith(".xls") || tdSource.endsWith(".xml") ){
			confDtls.setFuncDriven(false);
		}
		
		if(!errMsg.equalsIgnoreCase(""))
			System.out.println(errMsg);
		if(status){
			System.out.println("Application Under Test :: " + confDtls.getAppName());
			System.out.println("Script Execution URL : "+ confDtls.getScriptPath());
			System.out.println("Script Execution Browser : "+ confDtls.getBrowser());
			if(confDtls.isFuncDriven())
				System.out.println(" Functional Class : " + confDtls.getTestDataSource());
			else
				System.out.println("Test Data Details : " + confDtls.getTestDataSource());
			System.out.println("Properties File: " + confDtls.getProperties());
		}
		return status;
	}
	
	
	
	
	public  ArrayList<ConfigDetails> readFromCmd(String args[])
	{
		ArrayList<ConfigDetails>  configRows = new ArrayList<ConfigDetails> ();
		ConfigDetails cnfdtl = new ConfigDetails();
		cnfdtl.setFuncDriven(false); 
		cnfdtl.setPrefix(prefix);
		for(int i=0;i<args.length;i++)
		{
			if(args[i].split("=")[0].equalsIgnoreCase("url")){
				System.out.println("The URL value is---" +args[i].split("=")[1]);
				cnfdtl.setScriptPath(args[i].split("=")[1]);	
			}
			
			if(args[i].split("=")[0].equalsIgnoreCase("application"))
			{
				System.out.println("The Aplication value is---" +args[i].split("=")[1]);
				cnfdtl.setAppName(args[i].split("=")[1]);
				cnfdtl.setProperties("../properties/" +args[i].split("=")[1]+ ".xml");
			}
			
			if(args[i].split("=")[0].equalsIgnoreCase("testcases"))
			{
				String str=args[i].split("=")[1];
				System.out.println("The Test cases to be executed are---" +str);
				
				if (str.toLowerCase().startsWith("fe:")){
					cnfdtl.setTestCases(str);
					System.out.println("Test cases for Module "+ str.substring(3) + "  are executed");
				}
				else if (str.toLowerCase().startsWith("tt:")){
					cnfdtl.setTestCases(str);
					System.out.println("Test cases for TestType "+ str.substring(3) + "  are executed");
				}else if (str.toLowerCase().startsWith("rq:")){
					cnfdtl.setTestCases(str);
					System.out.println("Test cases for requirement "+ str.substring(3) + "  are executed");
				}else if (str.toLowerCase().startsWith("pr:")){
					cnfdtl.setTestCases(str);
					System.out.println("Testcases for the Priority "+ str.substring(3)+ "  are executed");
				}else if (str.equalsIgnoreCase("all") || str.equalsIgnoreCase("")){
					cnfdtl.setTestCases("all");
					System.out.println("All the test cases are executed");
				}
				else if (str.startsWith("tp:")){
					cnfdtl.setTestCases(str);
					System.out.println("Testcases for the test Plan "+ str.substring(3)+ "  are executed");
				}else if(str.contains(".") || str.contains("*")){
					cnfdtl.setTestCases(str);
					cnfdtl.setFuncDriven(true);
					System.out.println("Testcases matching the regex " + str + " are executed ");
				} else if(str.matches(".*([A-Z||a-z]).*")){
					cnfdtl.setTestCases(str);
					cnfdtl.setFuncDriven(true);
					System.out.println("Testcases for the Functions "+ str + "  are executed");
				}
				else{
					ArrayList<Integer> testCases = new ArrayList<Integer>();
					testCases = parseTokens(str,cnfdtl);
					System.out.print("TEST CASES ARE HERE : ----- ");
					cnfdtl.setTestCasesToBeExecuted(testCases);
					System.out.println(testCases);
				}
			}
			
			if(args[i].split("=")[0].equalsIgnoreCase("browser"))
			{
				System.out.println("The browser value is---" +args[i].split("=")[1]);
				cnfdtl.setBrowser(args[i].split("=")[1]);
			}
			
			if(args[i].split("=")[0].equalsIgnoreCase("testDataSource"))
			{
				System.out.println("The Test data source value is---" +args[i].split("=")[1]);
				cnfdtl.setTestDataSource(args[i].split("=")[1]);
			}
	
	
		}
		
		configRows.add(cnfdtl);
		
		return configRows;
	}

}