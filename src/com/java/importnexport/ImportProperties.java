package com.java.importnexport;

import com.java.RunTest;
import java.util.Properties;
import java.io.IOException;
import javax.xml.parsers.*;
import javax.xml.transform.TransformerException;

//import net.sourceforge.jtds.jdbc.Driver;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import org.apache.log4j.Logger;

@SuppressWarnings("deprecation")

public class ImportProperties {

	private Document document;
	private Logger log;
	
	public ImportProperties(String filename) {
		DocumentBuilderFactory docbuilderfactory = DocumentBuilderFactory.newInstance();
		docbuilderfactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder  docbuilder = docbuilderfactory.newDocumentBuilder();
			document = docbuilder.parse(filename);
		}catch(Exception e){	
			log.error(" Encountered exception while trying to read the properties file");
			System.out.println("Encountered exception while trying to read the properties file");
			System.out.println(e.getMessage());
			RunTest.configrowCount++;
			Thread.currentThread().stop();
		}
	}

	public Properties setPropertiesnoLog(String property_type) {

		Properties properties = new Properties();
		try{ 
			NodeList propertyNodes = null;
			propertyNodes = org.apache.xpath.XPathAPI.selectNodeList(document, "//" + property_type);
			
			if (propertyNodes.getLength() != 0) {

				Node nodes = propertyNodes.item(0);

				if (nodes.getNodeType() == Node.ELEMENT_NODE) {

					NodeList childs = nodes.getChildNodes();

					int count = childs.getLength();

					for (int i = 0; i < count; i++)
						if (childs.item(i).getNodeType() == Node.ELEMENT_NODE) {
							String key = childs.item(i).getNodeName().trim();
							String value = childs.item(i).getTextContent().trim();
							properties.setProperty(key, value);
						}
				}
			}
		}  catch(Exception e){
			log.error(" Encountered Exception while Searching for property :: " + property_type);
			System.out.println("Encountered Exception while Searching for property :: " + property_type);
			System.out.println(e.getMessage());
		}
		return properties;
	}

	public ImportProperties(String filename,Logger log) {

		this.log = log;
		
		log.info(" Creating Document Builder Factory ");
		DocumentBuilderFactory docbuilderfactory = DocumentBuilderFactory.newInstance();
		docbuilderfactory = DocumentBuilderFactory.newInstance();
		try {
			
			DocumentBuilder  docbuilder = docbuilderfactory.newDocumentBuilder();
			document = docbuilder.parse(filename);
			log.info(" Document object created to parse the CML File ");
			
		} catch (ParserConfigurationException e) {
		
			log.error(" Encountered Parser Configuration Exception in property file");
			log.error(e.getMessage());
			System.out.println("Encountered error in creating the Parser");
			System.out.println(e.getMessage());
			log.warn(" Stopping the current thread under execution ");
			RunTest.configrowCount++;
			Thread.currentThread().stop();
			
		} catch (SAXException e) {
			
			log.error(" Encountered SAX Exception in property file");
			log.error(e.getMessage());
			System.out.println("Error encountered in reading the xml document");
			System.out.println(e.getMessage());
			RunTest.configrowCount++;
			log.warn(" Stopping the current thread under execution ");
			Thread.currentThread().stop();
			
		} catch (IOException e) {
			
			log.error(" Encountered IOException while reading property file");
			log.error(e.getMessage());
			System.out.println("Encountered IO Stream Application Error when reading the property file");
			System.out.println(e.getMessage());
			RunTest.configrowCount++;
			log.warn(" Stopping the current thread under execution ");
			Thread.currentThread().stop();
			
		}catch(Exception e){
			
			log.error(" Encountered Exception while reading property file");
			log.error(e.getMessage());
			System.out.println("Encountered exception while trying to read the properties file");
			System.out.println(e.getMessage());
			RunTest.configrowCount++;
			log.warn(" Stopping the current thread under execution ");
			Thread.currentThread().stop();
		}
	}

	public Properties setProperties(String property_type) {

		Properties properties = new Properties();
		
		try{ 
	
			log.info(" Reading properties for " + property_type);
			
			NodeList propertyNodes = null;
			propertyNodes = org.apache.xpath.XPathAPI.selectNodeList(document, "//" + property_type);
			
			if (propertyNodes.getLength() != 0) {

				Node nodes = propertyNodes.item(0);

				if (nodes.getNodeType() == Node.ELEMENT_NODE) {

					NodeList childs = nodes.getChildNodes();

					int count = childs.getLength();

					for (int i = 0; i < count; i++)
						if (childs.item(i).getNodeType() == Node.ELEMENT_NODE) {
							String key = childs.item(i).getNodeName().trim();
							String value = childs.item(i).getTextContent().trim();
							log.info(" Read Property " + key + " :: " + value);
							properties.setProperty(key, value);
						}
				}
			} else{
				log.warn(" The property " + property_type + " has no xml nodes available");
			}

		} catch (TransformerException e) {
			log.error(" Encountered Transformer Exception ");
			log.error(" "+e.getMessage());
			System.out.println("Transformer Exception while searching for property :: "+property_type);
			System.out.println(" "+e.getMessage());
		} catch(Exception e){
			log.error(" Encountered unknown Exception ");
			log.error(e.getMessage());
			System.out.println("Encountered Exception while Searching for property :: " + property_type);
			System.out.println(e.getMessage());
		}

		return properties;
	}

}