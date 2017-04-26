package com.java;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;

public class EmailTestReport {

	private String smtpHostName = null;
	private String recipient = null;
	private	String reportType = null;
	private String subject = null;
	private String from = null;
	private String message = null;
	private String port = null;
	private String countersText = null;
	
	private SeleniumDriver driver;
	
	public EmailTestReport(SeleniumDriver driver) {
		this.driver = driver;
	
	}	
	
	public void postMail(int[] counters, String BrowserType, String URL) 	{

		driver.log.info(" Preparing to send Email ");
		String testHTMLReport = driver.hMap.get("testHTMLResultPath");
		String pdfFile = driver.hMap.get("PDFResultsFile");

		Message msg;

		smtpHostName = driver.miscProps.getProperty("SMTP_HOST_NAME");
		recipient = driver.miscProps.getProperty("recipients");
		reportType = driver.miscProps.getProperty("reportType");
		from = driver.miscProps.getProperty("from");
		subject = driver.miscProps.getProperty("subject");
		/*if(counters[2] >0){
			subject=subject+" -- "+driver.miscProps.getProperty("onFailedMergeToSubject");
		}*/
		message = driver.miscProps.getProperty("message");
		port = driver.miscProps.getProperty("SMTP_PORT");		

		driver.log.info(" SMTP HOST :: " + smtpHostName);
		driver.log.info(" SMTP PORT ::  " + port);
		driver.log.info(" From :: " + from);
		driver.log.info(" Subject :: " + subject);
		driver.log.info(" To :: " + recipient);
		
		countersText = "<table border=3 cellpadding=8>"+
		"<tr><td><b> Browser Executed		</b></td><td> "+ BrowserType + " </td></tr>" +
		"<tr><td><b> URL					</b></td><td> "+ URL		 + " </td></tr>" +
		"<tr><td><b> Total Cases Executed 	</b></td><td> "+ driver.confDtls.getTestCasesToBeExecuted().size() + " </td></tr>" +
		"<tr><td><b> Total Cases Passed		</b></td><td> "+ counters[1] + " </td></tr>" +
		"<tr><td><b> Total Cases Failed		</b></td><td> "+ counters[2] + " </td></tr>" + 
		"<tr><td><b> Total Cases Skipped		</b></td><td> "+ (driver.confDtls.getTestCasesToBeExecuted().size() - counters[0]) + " </td></tr>" + 
		"<tr><td><b> Total Execution Time(in Seconds)		</b></td><td> "+ ( driver.hMap.get("TiMe_CaLc")) + " </td></tr>" + "</table> ";
		
		//countersText = countersText+"<br>"+"\n \n Total Execution Time(in Seconds) is \n \n "+driver.hMap.get("totalExecutionTime")+"</br>";
		
		/*if(!(driver.roundOfExecution == 1)){
			if((driver.roundOfExecution == 2))
			  subject = subject + " -- FailedCases";
			else
				subject = subject + " -- FailedCases -- Round: "+driver.roundOfExecution;
		}*/
		
		Properties newprops = new Properties();
		newprops.put("mail.smtp.host", smtpHostName);
		newprops.setProperty("mail.smtp.port", port);		
		
		Session session;
	
			if(!(driver.miscProps.getProperty("email_UserName","false").equals("false")&&driver.miscProps.getProperty("email_UserName","false").equals("false")))
			{
				//newprops.put("mail.smtp.socketFactory.port", port);  
				//newprops.put("mail.smtp.socketFactory.class",  				            "javax.net.ssl.SSLSocketFactory");  
				newprops.put("mail.smtp.auth", "true");  
				//newprops.put("mail.smtp.port", port);  
			//	newprops.put("mail.smtp.auth", "true");
				newprops.put("mail.smtp.starttls.enable", "true");
			//	newprops.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
				session = Session.getInstance(newprops,
						  new javax.mail.Authenticator() {
							protected PasswordAuthentication getPasswordAuthentication() {
								return new PasswordAuthentication(driver.miscProps.getProperty("email_UserName"), driver.miscProps.getProperty("email_Password"));
							}
						  });
			}
			else{
				 session = Session.getDefaultInstance(newprops, null);
			}
		
		
		/* session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication("username","password");
					}
				});*/
		
		driver.log.info(" Session Object is created ");
		
		if (reportType.equalsIgnoreCase("Basic")) {
			message = message.replace("&&Counters&&",countersText);
		} else if (reportType.equalsIgnoreCase("HTML")) {
			message = "";
			try {
				String line;
				BufferedReader input = new BufferedReader(new FileReader(testHTMLReport));			
				while ((line = input.readLine()) != null){
					message = message+line;
				}		
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		try{
		
		msg = new MimeMessage(session);
		
		InternetAddress addressFrom = new InternetAddress(from);
		msg.setFrom(addressFrom);
		driver.log.info(" From Address set ");
		
		String[] recipients = recipient.split(";");
		InternetAddress[] addressTo =new InternetAddress[recipients.length];
		for (int i = 0; i < recipients.length; i++)
		{
			addressTo[i] = new InternetAddress(recipients[i]);
			driver.log.info(" Added " + recipients[i]);
		}
		
		msg.setRecipients(Message.RecipientType.TO, addressTo);
		driver.log.info(" Recpients are added ");
		
		Date d = new SimpleDateFormat("MMddyy_HHmmss").parse(driver.hMap.get("TimeStamp"));
				if(counters[2] >0){
					msg.setSubject(subject +" - Failure");
				}
				else{
					msg.setSubject(subject);
				}
		//msg.setSubject(subject +" - "+ d.toString() );
		driver.log.info(" Subject is created ");
		
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(message, "text/html");
		
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);
		String testResultPath =driver.hMap.get("TestResultsPath");
		
		messageBodyPart = new MimeBodyPart();
		DataSource tResult = new FileDataSource(testResultPath);
		messageBodyPart.setDataHandler(new DataHandler(tResult));
		messageBodyPart.setFileName(testResultPath.substring(testResultPath.lastIndexOf('/')+ 1, testResultPath.length()));
		multipart.addBodyPart(messageBodyPart);
		driver.log.info(" TestResults Excel File is added ");
		
		String testData = driver.miscProps.getProperty("attachment1","$$");
		if(!testData.equalsIgnoreCase("$$")) {
			messageBodyPart = new MimeBodyPart();
			DataSource tData = new FileDataSource("..//TestInputs//"+testData);
			messageBodyPart.setDataHandler(new DataHandler(tData));
			messageBodyPart.setFileName(testData);
			multipart.addBodyPart(messageBodyPart);
			driver.log.info(" Attachment1 is added ");
		}
		
		String checklist = driver.miscProps.getProperty("attachment2","$$");
		
		if(!checklist.equalsIgnoreCase("$$")) {
			messageBodyPart = new MimeBodyPart();		
			DataSource tChecklist = new FileDataSource("..//TestInputs//"+checklist);
			messageBodyPart.setDataHandler(new DataHandler(tChecklist));
			messageBodyPart.setFileName(checklist);
			multipart.addBodyPart(messageBodyPart);
			driver.log.info(" Attachment2 is added ");
		}
		
		messageBodyPart = new MimeBodyPart();		
		DataSource tReport = new FileDataSource(testHTMLReport);
		messageBodyPart.setDataHandler(new DataHandler(tReport));
		messageBodyPart.setFileName(tReport.getName());
		multipart.addBodyPart(messageBodyPart);
		driver.log.info(" TestResults HTML Report is added ");
		
		if(driver.createpdf){

			messageBodyPart = new MimeBodyPart();		
			DataSource tReportPDF = new FileDataSource(pdfFile);
			messageBodyPart.setDataHandler(new DataHandler(tReportPDF));
			messageBodyPart.setFileName(tReportPDF.getName());
			multipart.addBodyPart(messageBodyPart);
			driver.log.info(" TestResults PDF Report is added ");
			
		}
		
		msg.setSentDate(new Date());
		msg.setContent(multipart);
		System.out.println("Sending Email...");
		driver.log.info(" Sending Mail ");
		Transport.send(msg);
		driver.log.info(" Mail Sent ");
		System.out.println("Report E-mail Sent.");
		}catch(Exception e){
			e.printStackTrace();
			driver.log.error(" Unable to send email ");
			driver.log.error(e.getMessage());
			System.out.println("Unable to send mail");
			System.out.println(e.getMessage());
			
		}
	}
}