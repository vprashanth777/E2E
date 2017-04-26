package com.java;

import java.io.IOException;
import java.net.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.TransformerException;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import org.apache.log4j.*;

/**
 * Class Written to create the web driver object for grid
 */
@SuppressWarnings("deprecation")
public class DriverObject {

	private DesiredCapabilities capability;
	private String grid_ip = "";
	private String grid_port = "";
	private String browserType = "firefox";
	private String platform;
	private String version;
	private WebDriver driver;
	private Logger log;

	/**
	 * Constructor which takes the parameter BrowserName and creates the capabilities based on properties
	 */
	
	public DriverObject(String browserName,Properties properties,Logger log) {
		this.log = log;
		setBrowserDetails(browserName);
		createCapabilities();
		grid_ip = properties.getProperty("grid_ip","127.0.0.1").trim();
		grid_port = properties.getProperty("grid_port","4444").trim();
		
		log.info(" Selenium Hub IP Address :: " + grid_ip);
		log.info(" Selenium Hub Port :: " + grid_port);
	}

	/**
	 *  Method which creates the WebDriver object
	 */

	public WebDriver createWebDriver() {
		try {
			URL grid_url = new URL("http://" + grid_ip + ":" + grid_port + "/wd/hub");
			driver = new CustomRemoteWebDriver(grid_url, capability);
			log.info(" Created Web Driver Object ");
		} catch (MalformedURLException e) {
			
			log.error(" Unable to create webdriver object due to malformed url ");
			log.error(e.getMessage());
			System.out.println(" Unable to create webdriver object due to malformed url");
			System.out.println(e.getMessage());
			RunTest.configrowCount++;
			Thread.currentThread().stop();
		} catch (Exception e) {
			System.out.println(" Encountered exception while creating webdriver object ");
			System.out.println(e.getMessage());
			RunTest.configrowCount++;
			Thread.currentThread().stop();
		}
		return driver;
	}
		
	/**
	 * Method to get the capabilities to the desired configuration based on the browser name specified
	 */
	
	private void setBrowserDetails(String browserName) {

		String xmlFile = "..//TestInputs//Grid_BrowserList.xml";

		log.info(" Browser Listing FIle :: " + xmlFile);
		System.out.println("Browser List file : " + xmlFile);
		
		Document document = null;
		try {
			DocumentBuilderFactory docbuilderfactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docbuilder = docbuilderfactory.newDocumentBuilder();
			document = docbuilder.parse(xmlFile);
		} catch (ParserConfigurationException e) {
			log.error(" Encountered ParserConfigurationException");
			log.error(e.getMessage());
			System.out.println("Encountered error in creating the XML Parser for browser List file");
			System.out.println(e.getMessage());
			return;
		}catch (SAXException e) {
			log.error(" Encountered SAXException");
			log.error(e.getMessage());
			System.out.println("Error encountered in reading the xml document browser List");
			System.out.println(e.getMessage());
			return;
		} catch (IOException e) {
			log.error(" Encountered IOException");
			log.error(e.getMessage());
			System.out.println("Encountered IO Stream Application Error when reading the browser List file");
			System.out.println(e.getMessage());
			return;
		}
		
		String xpath = "//browserList/browser[@name='" + browserName.toLowerCase() + "']";
		
		Properties props = new Properties();
						
		try {
			NodeList browserList = org.apache.xpath.XPathAPI.selectNodeList(document, xpath);
		
			int count = browserList.getLength();
			if(count == 0){
				System.out.println("No browser Configuration with specified browser doesn't exists");
				System.out.println("Using default as Firefox");
			}else{
				NodeList browserNode = browserList.item(0).getChildNodes();
				count = browserNode.getLength();
			
				int row = 0;
			
				while(row < count){
					if (browserNode.item(row).getNodeType() == Node.ELEMENT_NODE){
						String key = browserNode.item(row).getNodeName().trim().toLowerCase();
						String value = browserNode.item(row).getTextContent().trim().toLowerCase();
						props.setProperty(key,value);
					}
					row++;
				}
			}
		} catch (TransformerException e) {
			log.error(" Encountered TransformerException");
			log.error(e.getMessage());
			System.out.println("Encountered Transformer error in XML File. Using default values");
			System.out.println(e.getMessage());
		}
		
		browserType = props.getProperty("type","firefox");
		platform  = props.getProperty("platform");
		version  = props.getProperty("version");
		
		log.info(" Browser Type :: "+ browserType);
		log.info(" Platform :: "+ platform);
		log.info(" Version :: "+version);
	}

	/**
	 * Method to create the capabilities object
	 */
	
	private void createCapabilities() {

		System.out.println("Browser Type : " + browserType);

		if (browserType == null) {
			capability = DesiredCapabilities.firefox();
		
		} else if (browserType.equalsIgnoreCase("firefox") || browserType.equalsIgnoreCase("ff")) {
			capability = DesiredCapabilities.firefox();

		} else if (browserType.equalsIgnoreCase("internetexplorer") || browserType.equalsIgnoreCase("ie")) {
			capability = DesiredCapabilities.internetExplorer();

		} else if (browserType.equalsIgnoreCase("safari")) {
			capability = DesiredCapabilities.safari();

		} else if (browserType.equalsIgnoreCase("chrome")) {
			capability = DesiredCapabilities.chrome();
		
		} else if (browserType.equalsIgnoreCase("opera")) {
			capability = DesiredCapabilities.opera();
		
		} else {
			capability = DesiredCapabilities.firefox();
		}

		if (platform != null && !(platform.equalsIgnoreCase("any"))) {
			System.out.println("Platform : " + platform);
			capability.setPlatform(Platform.valueOf(platform.toUpperCase()));
		}

		if (version != null) {
			System.out.println("Version : " + version);
			capability.setVersion(version);
		}
	}

}
