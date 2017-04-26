package com.java;

import java.io.*;
import java.util.*;
import com.j2bugzilla.base.*;
import com.j2bugzilla.rpc.*;
import org.apache.log4j.*;
import com.j2bugzilla.rpc.BugSearch.SearchLimiter;
import com.j2bugzilla.rpc.BugSearch.SearchQuery;

/**
 * Class to log bugs in bugzilla , whose details are provided in the properties file
 */

public class BugzillaBugLog {

	private Logger log;
	private String bugZillaURL;
	private String userName;
	private String passWord;
	private Hashtable<String, Object> bugdtls = new Hashtable<String, Object>();

	/**
	 * Constructor to Read the bugzilla Properties
	 */
	
	public BugzillaBugLog(Properties miscProps, Logger log) {

		this.log = log;
		
		bugZillaURL = miscProps.getProperty("bt_url");
		userName = miscProps.getProperty("bt_UserName");
		passWord = miscProps.getProperty("bt_Password");

		log.info(" Bugzilla URL :: " + bugZillaURL);
		log.info(" Bugzilla UserName :: " + userName);
		log.info(" Bugzilla Password :: " + passWord);

		log.info(" Product :: " + miscProps.getProperty("productName"));
		log.info(" Component :: " +miscProps.getProperty("componentName"));
		log.info(" Operating System :: " +miscProps.getProperty("op_sys"));
		log.info(" Platform :: " +miscProps.getProperty("platform"));
		log.info(" Version :: " +miscProps.getProperty("version"));
		log.info(" Priority :: " +miscProps.getProperty("priority"));

		bugdtls.put("product", miscProps.getProperty("productName"));
		bugdtls.put("component",miscProps.getProperty("componentName"));
		bugdtls.put("op_sys", miscProps.getProperty("op_sys"));
		bugdtls.put("platform", miscProps.getProperty("platform"));
		bugdtls.put("version",miscProps.getProperty("version"));
		bugdtls.put("priority",miscProps.getProperty("priority"));

	}

	/**
	 * Method to log the bug in bugzilla
	 */
	
	public void logBug(List<String> bugSummary) {

		try {

			log.info(" Connecting to Bugzilla ");
			BugzillaConnector conn = new BugzillaConnector();
			conn.connectTo(bugZillaURL);

			LogIn logIn = new LogIn(userName, passWord);
			conn.executeMethod(logIn);

			log.info(" Logged into Bugzilla ");
			BugFactory factory = new BugFactory().newBug();
			
			String summary = "#"+  bugSummary.get(0)+ " : "+ bugSummary.get(2);
			bugdtls.put("summary", summary);
			String comment = bugSummary.get(4);
			bugdtls.put("comment",comment.split("Screen Shot : ")[0]);
			
			SearchQuery q[] = new SearchQuery[7];
			q[0] = new SearchQuery(SearchLimiter.SUMMARY,summary);
			q[1]=  new SearchQuery(SearchLimiter.PRODUCT,bugdtls.get("product").toString());
			q[2]=  new SearchQuery(SearchLimiter.COMPONENT,bugdtls.get("component").toString());
			q[3]=  new SearchQuery(SearchLimiter.OPERATING_SYSTEM,bugdtls.get("op_sys").toString());
			q[4]=  new SearchQuery(SearchLimiter.PLATFORM,bugdtls.get("platform").toString());
			q[5] = new SearchQuery(SearchLimiter.VERSION,bugdtls.get("version").toString());
			q[6] = new SearchQuery(SearchLimiter.PRIORITY,bugdtls.get("priority").toString());
			
			String fileName = comment.split("Screen Shot : ")[1];
		
			Attachment attachment = null;
			
			try{
				FileInputStream fis = new FileInputStream(fileName);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte buf[] = new byte[1024];

				for (int readNum; (readNum = fis.read(buf)) != -1;)
					bos.write(buf, 0, readNum);

				AttachmentFactory afactory = new AttachmentFactory();
				
				attachment = afactory.newAttachment()
					.setData(bos.toByteArray()).setMime("image/png")
					.setName("screenshot.png")
					.setSummary(comment.split("Screen Shot : ")[0])
					.createAttachment();
				log.info(" Created Attachment ");
			}catch(Exception e){
				log.error(" Unable to create Attachment ");
				System.out.println(" Unable to create Attachment Object");
			}

			BugSearch search = new BugSearch(q);
			conn.executeMethod(search);
			
			List<Bug> results = search.getSearchResults();

			for(Bug b : results){
				if(!b.getStatus().toString().toLowerCase().equalsIgnoreCase("closed")){
					
					log.info(" Existing bug with ID " + b.getID() + " is updated with comment");
					System.out.println("Existing bug updated with comment");
					System.out.println(" Bug ID : " + b.getID());
					
					CommentBug newComment = new CommentBug(b.getID(), bugdtls.get("comment").toString());
					conn.executeMethod(newComment);
					
					AddAttachment add = new AddAttachment(attachment, b);
					conn.executeMethod(add);
					System.out.println(" Attachment added ");
					log.info(" Attachment is added ");
					return;
				}
			}

			Bug objBug = factory.createBug(bugdtls);
			ReportBug report = new ReportBug(objBug);
			conn.executeMethod(report);
			
			System.out.println("New bug created with ID : " + report.getID());
			log.info("New bug created with ID : " + report.getID() + " is created ");
			GetBug getBug =  new GetBug(report.getID());
			conn.executeMethod(getBug);
			
			Bug bug = getBug.getBug();
			AddAttachment add = new AddAttachment(attachment, bug);
			conn.executeMethod(add);
			System.out.println(" Attachment added ");
			log.info(" Attachment is added ");
			return;

		} catch (Exception e) {
			log.error(" Exception while trying to log defect in Bugzilla ");
			log.error(e.getMessage());
			System.out.println("Unable to log defect in Bugzilla");
			System.out.println(e.getMessage());
		}
	}
}