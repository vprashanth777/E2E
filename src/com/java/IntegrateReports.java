package com.java;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;

import org.apache.poi.common.usermodel.Hyperlink;
import org.apache.poi.hssf.record.formula.functions.Row;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.java.importnexport.*;
import com.java.objects.*;

public class IntegrateReports {

	private String app;
	private String files[];
	private ConfigDetails cnfDtls;
	private double totalTime;
	private ExportTestResults expTestRes;
	private Properties props = new Properties();

	private boolean sendMail = true;
	private boolean createpdf = false;
	private boolean isEvidenceUsed = true;

	private String exec_date = "";

	public enum Status {PASS,FAIL,SKIPPED};

	int total,passed,failed;

	private ArrayList<Integer> testcases = new ArrayList<Integer>();
	private String fileName = new SimpleDateFormat("MMddyy_HHmmss").format(new Date());
	private HashMap<Integer,TestResults> testresults = new HashMap<Integer,TestResults>();
	public double totalExecutionTime;


	/**
	 * Constructor of class which is used for integrating reports
	 */

	public IntegrateReports(String app,String fileList,ConfigDetails cnfDtls,double totalTime){
		this.app = app;
		this.files = fileList.split(";");
		this.cnfDtls = cnfDtls;
		this.totalTime = totalTime;
	}







	public void integrateReports(){

		readProperties();
		readReports();
		createHTML();
		createExcel();
	 //   createExcelTestReport();

		if(createpdf)
			createPDF();

		if(sendMail)
			sendMail();

	}

	private void readProperties(){

		ImportProperties xml = new ImportProperties("..//Properties//"+cnfDtls.getAppName()+".xml");

		props = xml.setPropertiesnoLog("appDetails");
		props.putAll(xml.setPropertiesnoLog("common"));
		props.putAll(xml.setPropertiesnoLog("misc"));

		sendMail = Boolean.parseBoolean(props.getProperty("sendMailReport", "true"));
		createpdf = Boolean.parseBoolean(props.getProperty("createpdf","false"));

		if(sendMail){
			props.putAll(xml.setPropertiesnoLog("email"));
		}
	}

	private void readReports() {

		try {
			exec_date = (new SimpleDateFormat("MMddyy_HHmmss").parse(files[0])).toString();
		}catch(Exception e){
			exec_date = (new Date()).toString();
		}

		for(String file : files){
			file = "..//TestReports//" + app + "//" + "Test Results_" + file +".xls";
			try{
				POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
				HSSFWorkbook wb = new HSSFWorkbook(fs);
				HSSFSheet sheet = wb.getSheetAt(0);

				HSSFCell cell = sheet.getRow(0).getCell(7);

				if( isEvidenceUsed && cell == null){
					isEvidenceUsed = false;
				}

				int i = 1;

				while(true){

					TestResults ts = new TestResults();
					HSSFRow row = sheet.getRow(i);

					if(row == null)
						break;

					cell = row.getCell(0);

					if(cell == null )
						break;

					String str = cell.getStringCellValue();

					int tcid  = Integer.parseInt(str.replace(".0", ""));

					ts.setTCID(tcid);
					ts.setTCTitle(row.getCell(1).getStringCellValue());
					ts.setResult(row.getCell(2).getStringCellValue());

					cell = row.getCell(3);
					if(cell == null )
						str = "";
					else
						str = cell.getStringCellValue();
					ts.setErrorMsg(str);

					SimpleDateFormat sd = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
					ts.setTime_Stamp(sd.parse(row.getCell(4).getStringCellValue()));

					ts.setTime_Taken(row.getCell(5).getStringCellValue());

					cell = row.getCell(6);
					if(cell == null )
						str = "";
					else
						str = cell.getStringCellValue();

					ts.setComment(str);

					if(isEvidenceUsed && row.getCell(7)!= null){
						ts.setEvidence(row.getCell(7).getHyperlink().getAddress());
					}

					if(testresults.containsKey(tcid)){

						TestResults temp = testresults.get(tcid);
						int res = temp.getTime_Stamp().compareTo(ts.getTime_Stamp());
						if(res != -1){
							ts = temp;
						}
						testresults.remove(tcid);
					}

					testresults.put(tcid,ts);

					i++;
				}
			} catch(Exception e){
				e.printStackTrace();
				System.out.println(" Exception while querying for testresults");
				System.out.println(e.getMessage());
			}

		}

		Iterator<Integer> tcid = testresults.keySet().iterator();

		while(tcid.hasNext()){
			int testcaseid = tcid.next();
			testcases.add(testcaseid);
		}

		Collections.sort(testcases);
	}

	private void createExcelTestReport() {

		try{

			String testreportname =   "Integrated Test Results_" + fileName +".xls";
			HSSFWorkbook workbook=new HSSFWorkbook();
			FileInputStream fis = null;

			for(String file : files){	
				//	String sheetName="Integrated "+file.split("_")[2];
				String sheetName=file.split("_")[2];
				file = "..//TestReports//" + app + "//" + "Test Results_" + file +".xls";
				try{

					fis = new FileInputStream(file);
					HSSFWorkbook workbook2 = new HSSFWorkbook(fis);
					HSSFSheet sheet2 = workbook2.getSheet("Test Result");
					HSSFSheet sheet =  workbook.createSheet(sheetName); 

					HSSFRow newRow = sheet.getRow(1);
					HSSFRow sourceRow =  sheet2.getRow(0);
					if (newRow != null) {
						sheet2.shiftRows(1, sheet2.getLastRowNum(), 1);
					} else {
						newRow = sheet.createRow(1);
					}
					try{
						for(int j=0;j<=sheet2.getPhysicalNumberOfRows();j++){
							sourceRow =  sheet2.getRow(j);
							newRow =sheet.createRow(j);										
							sheet.setColumnWidth(1,  (short)(256*25));
							sheet.setColumnWidth(2,  (short)(256*14));
							sheet.setColumnWidth(3,  (short)(256*30));
							sheet.setColumnWidth(4,  (short)(256*28));
							sheet.setColumnWidth(5, (256 * 28));
							sheet.setColumnWidth(6, (256 * 28));

							if(isEvidenceUsed){
								sheet.setColumnWidth(7, (256 * 28));
							}
							if ( sourceRow== null) {
								newRow  = null;
								continue;
							}
							for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
								// Grab a copy of the old/new cell
								HSSFCell oldCell = sourceRow.getCell(i);
								HSSFCell newCell = newRow.createCell(i);
								// If the old cell is null jump to next cell
								if (oldCell == null) {
									newCell = null;
									continue;
								}			        	    			        	    
								newCell.setCellType(oldCell.getCellType());

								// Set the cell data value
								switch (oldCell.getCellType()) {
								case Cell.CELL_TYPE_BLANK:
									newCell.setCellValue(oldCell.getStringCellValue());
									break;	        	        
								case Cell.CELL_TYPE_NUMERIC:
									newCell.setCellValue(oldCell.getNumericCellValue());
									break;
								case Cell.CELL_TYPE_STRING:
									newCell.setCellValue(oldCell.getRichStringCellValue());
									break;
								}
							}
						}
					}catch(Exception e)	{
						e.printStackTrace();
					}
					fis.close();  
					FileOutputStream fileOut = new FileOutputStream("..//TestReports//" + app + "//"+testreportname);
					workbook.write(fileOut);
					fileOut.flush();
					fileOut.close();
					fis.close();

				}catch(Exception e) {
					e.printStackTrace();
				}

			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void createExcel() {

		try {

			HSSFWorkbook wb = new HSSFWorkbook();

			FileOutputStream fileOut = null;
			fileOut = new FileOutputStream("..//TestReports//" + app + "//" + "Integrated Test Results_" + fileName +".xls");
			wb.createSheet("Test Result");
			HSSFSheet sheet = wb.getSheetAt(0);
			sheet.setAutobreaks(false);
			HSSFRow row = sheet.createRow((short) 0);
			HSSFFont font = wb.createFont();
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			HSSFCellStyle cellStyle = wb.createCellStyle();
			cellStyle.setFont(font);

			row.createCell(0).setCellStyle(cellStyle);
			row.createCell(1).setCellStyle(cellStyle);
			row.createCell(2).setCellStyle(cellStyle);
			row.createCell(3).setCellStyle(cellStyle);
			row.createCell(4).setCellStyle(cellStyle);
			row.createCell(5).setCellStyle(cellStyle);
			row.createCell(6).setCellStyle(cellStyle);

			if(isEvidenceUsed){
				row.createCell(7).setCellStyle(cellStyle);
			}

			row.createCell(0).setCellValue("Test Case ID");
			row.createCell(1).setCellValue("Test Case Title");
			row.createCell(2).setCellValue("Result(P/F)");
			row.createCell(3).setCellValue("Error Message");
			row.createCell(4).setCellValue("Time Stamp");
			row.createCell(5).setCellValue("Time Taken (in Seconds)");
			row.createCell(6).setCellValue("Comment");
			if(isEvidenceUsed){
				row.createCell(7).setCellValue("Evidence");
			}
			int rows = 1;

			for (int i = 0; i < testcases.size(); i++){

				row = sheet.createRow(rows);
				rows++;
				System.out.println(".. Export " +testcases.get(i) );
				TestResults ts = testresults.get(testcases.get(i));
				sheet.setColumnWidth(2,(256 * 13));
				sheet.setColumnWidth(1, (256 * 25));
				sheet.setColumnWidth(2, (256 * 14));
				sheet.setColumnWidth(3, (256 * 30));
				sheet.setColumnWidth(4, (256 * 28));
				sheet.setColumnWidth(5, (256 * 28));
				sheet.setColumnWidth(6, (256 * 28));

				if(isEvidenceUsed){
					sheet.setColumnWidth(7, (256 * 28));
				}

				row.createCell(0).setCellValue(ts.getTCID());
				row.createCell(1).setCellValue(ts.getTCTitle());
				row.createCell(2).setCellValue(ts.getResult());
				row.createCell(3).setCellValue(ts.getErrorMsg());
				row.createCell(4).setCellValue(ts.getTime_Stamp().toString());
				row.createCell(5).setCellValue(ts.getTime_Taken());
				row.createCell(6).setCellValue(ts.getComment());

				//	totalExecutionTime = totalExecutionTime+Double.parseDouble(ts.getTime_Taken());


				if(isEvidenceUsed && ts.getEvidence()!=null){
					HSSFCell cell = row.createCell(7);
					CreationHelper createHelper = wb.getCreationHelper();
					cell.setCellValue("Click Here"); 
					Hyperlink link = createHelper.createHyperlink(Hyperlink.LINK_FILE);
					File f = new File(ts.getEvidence());
					link.setAddress(f.getCanonicalPath());
					cell.setHyperlink((org.apache.poi.ss.usermodel.Hyperlink) link);
				}
			}

			row = sheet.createRow(rows+2);

			wb.write(fileOut);
			fileOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createHTML() {


		String ln = "\n";
		Collections.sort(testcases);

		total = testcases.size();

		String chartDimensions = "0|5|10";
		String chartMaxHeight = "10";

		if ((total >= 10) && (total < 20)) {
			chartDimensions = "0|5|10|15|20";
			chartMaxHeight = "20";
		} else if ((total >= 20) && (total < 50)) {
			chartDimensions = "0|10|20|30|40|50";
			chartMaxHeight = "50";
		} else if ((total >= 50) && (total < 100)) {
			chartDimensions = "0|20|40|60|80|100";
			chartMaxHeight = "100";
		} else if ((total >= 100) && (total < 200)) {
			chartDimensions = "0|40|80|120|160|200";
			chartMaxHeight = "200";
		} else if ((total >= 200) && (total < 300)) {
			chartDimensions = "0|50|100|150|200|250|300";
			chartMaxHeight = "300";
		} else if ((total >= 300) && (total < 400)) {
			chartDimensions = "0|80|160|240|320|400";
			chartMaxHeight = "400";
		} else if ((total >= 400) && (total < 500)) {
			chartDimensions = "0|100|200|300|400|500";
			chartMaxHeight = "500";
		} else if ((total >= 500) && (total < 800)) {
			chartDimensions = "0|160|320|480|640|800";
			chartMaxHeight = "800";
		} else if ((total >= 800) && (total < 1000)) {
			chartDimensions = "0|200|400|600|800|1000";
			chartMaxHeight = "1000";
		}

		passed = 0;
		failed = 0;

		String logo = "http://valuelabs.com/templates/valuelabs/images/valuelabs-logo.gif";
		logo = props.getProperty("logo", logo);

		String tcdetails = "";

		for(int Key : testcases){

			String	style = "skip";

			TestResults ts = testresults.get(Key);
			String title = ts.getTCTitle();

			System.out.println("ID " + Key + " : " + title );

			Status status = Status.valueOf(ts.getResult().toUpperCase());
			/*for(String file : files){
				file = "..//TestReports//" + app + "//" + "Test Results_" + file +".xls";
				try{
					POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
					HSSFWorkbook wb = new HSSFWorkbook(fs);
					HSSFSheet sheet = wb.getSheetAt(0);

                    
					for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
						HSSFRow  row = sheet.getRow(rowIndex);
						if (row != null) {
							String key = null;

							if(row.getCell(0) != null)  {
								HSSFCell cell = row.getCell(5);
								if (cell != null) {
									key = cell.getStringCellValue();
								}
								totalExecutionTime=totalExecutionTime+Double.parseDouble(key.toString());
								totalExecutionTime = (double) Math.round(totalExecutionTime * 100) / 100;
							
							}



						}
					}
					double lastRow = sheet.getLastRowNum()-1;
					totalExecutionTime=totalExecutionTime/lastRow;
				}
				catch(Exception e){
					e.printStackTrace();
					System.out.println(" Exception while querying for testresults");
							System.out.println(e.getMessage());
				}
			}*/

			switch(status){

			case PASS:
				style = "pass";
				title = title + " - Passed";
				passed++;
				break;

			case FAIL:
				failed++;
				style = "fail";
				title = title + " - Failed : " + ts.getErrorMsg().split("Screen Shot : ")[0];
				break;

			default:
				style = "skip";
				title = title +  " -  No Test Steps Available - Skipped";
			}

			String msg = "<p><span class=\""+style+"\">" + Key + ": </span><span class=\"title\">" + title + "</span></p>";
			tcdetails  = tcdetails + msg;
		}
		for(String file : files){
			file = "..//TestReports//" + app + "//" + "Test Results_" + file +".xls";
			try{
				POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
				HSSFWorkbook wb = new HSSFWorkbook(fs);
				HSSFSheet sheet = wb.getSheetAt(0);

                
				for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
					HSSFRow  row = sheet.getRow(rowIndex);
					if (row != null) {
						String key = null;

						if(row.getCell(0) != null)  {
							HSSFCell cell = row.getCell(5);
							if (cell != null) {
								key = cell.getStringCellValue();
							}
							totalExecutionTime=totalExecutionTime+Double.parseDouble(key.toString());
							totalExecutionTime = (double) Math.round(totalExecutionTime * 100) / 100;
						
						}



					}
				}
				/*double lastRow = sheet.getLastRowNum()-1;
				totalExecutionTime=totalExecutionTime/lastRow;*/
			}
			catch(Exception e){
				e.printStackTrace();
				System.out.println(" Exception while querying for testresults");
						System.out.println(e.getMessage());
			}
		}

		int skipped = total - (passed+failed);

		tcdetails = "<div id=\"testcaseDetails\" style=\"padding-left:15px\"><p><span style=\"font-size: 15px;" +
				"font-weight:bold;color:#000000;font-family:arial;\">Items Tested:</span> </p>" + tcdetails + "</div><br/>";


		String execDetails = "<div id=\"execDetails\"><table width=\"100%\" cellpadding=\"3\" cellspacing=\"0\"> <tr> " +
				"<td align=\"left\" valign=\"middle\" class=\"execDetails\"><span class=\"execDetails\">Test Cases Executed : " +
				+ total + "</span></td><td align=\"left\" valign=\"middle\" class=\"execDetails\"><span class=\"execDetails\">" +
				"Passed : " + passed + "</span></td><td align=\"left\" valign=\"middle\" class=\"execDetails\"><span class=\" " +
				"execDetails\">Failed :" + failed + "</span></td><td align=\"left\" valign=\"middle\" class=\"execDetails\"> " +
				"<span class=\"execDetails\">Skipped : " + skipped + "</span></td><td align=\"left\" valign=\"middle\" class=\"" +
				"execDetails\"><span class=\"execDetails\">"  + "Browser: "	+ cnfDtls.getBrowser() +
				"</span></td><td align=\"left\" valign=\"middle\" class=\"execDetails\"><span class=\"execDetails\">" +"Total Execution Time(in Seconds) :"+totalTime  + "</span></td></tr></table>" +
				"</div> <br/>";

		String graph = "<div id=\"graph\" style=\"padding-left:10px\"><table width=\"100%\" cellpadding=\"0\" cellspacing=\"" +
				"0\" bgcolor='#FFFFFF'><tr><td bgcolor=\"#FFFFFF\" valign=\"top\" width=\"99%\"><img id=\"graph\" src=\"http://chart." + 
				"apis.google.com/chart?cht=bvg&amp;chs=350x175&amp;chd=t:"+ passed + "," + failed + "," + skipped + "&amp;chds=0," + chartMaxHeight +
				"&amp;chxt=x,y&amp;chxs=0,000000,12|1,000000,12&amp;chco=00FF00|FF0000|0000FF|FFFF00&amp;chbh=50,0,20&amp;chxl=0:|Passed|Failed|Skipped|1:|" +
				chartDimensions	+ "&amp;chg=25,16.667,2,5&amp;chtt=Total+Test+Cases+=+" + total + "&amp;chts=000000,15\" BORDER=\"0\" align=\"left\" />" +
				"</td></tr></table></div><br/>";

		String genDetails = "<div id=\"genDetails\"  style=\"padding-left:10px\" ><table width=\"100%\" cellpadding=\"0\" " +
				"cellspacing=\"0\" bgcolor='#FFFFFF'><tr><td><span style=\"font-size:20px;font-weight:bold;color:#000000;font-family:" +
				"arial;line-height:110%;\">General Details</span></td></tr><tr><td><span style=\"font-size:12px;font-weight:bold;color:" +
				"#000000;font-family:arial;line-height:110%;\">URL : </span><a href=\"" + cnfDtls.getScriptPath() + "\" style=\"font-size:" +
				"12px;color:#0000FF;line-height:150%;font-family:trebuchet ms;\">"+cnfDtls.getScriptPath() + "</a></td></tr></table></div>";

		String htmlStyles = "<html><head><style>td.header {"+
				"background-color:#3399FF;border-top:0px solid #333333;border-bottom:1px dashed #000000;}td.testDetails{ "	+ 
				"background-color:#3399FF;border-top:5px solid #3399FF;border-bottom:1px dashed #000000;}span.testDetails{" +
				"font-size:12px;font-weight:bold;color:#000000;line-height:200%;font-family:verdana;text-decoration:none;}" +
				"td.execDetails{background-color:#3399FF;border-top:5px solid #3399FF;border-bottom:0px dashed #000000;" +
				"}span.execDetails{font-size:12px;font-weight:bold;color:#000000;line-height:200%;font-family:verdana;"+
				"text-decoration:none;}span.pass{font-size: 14px;font-weight:bold;line-height:100%;color:#00FF00;font-family:" +
				"arial;}span.fail{font-size: 14px;font-weight:bold;color:#FF0000;line-height:100%;font-family:arial;}span.skip"+
				"{font-size:14px;font-weight:bold;color:#0000FF;line-height:100%;font-family:arial;}span.title{font-size: 14px;" +
				"font-weight:normal;color:#000000;line-height:100%;font-family:arial;}td.reqDetails{font-size:12px;font-weight:" + 
				"bold;color:#000000;line-height:100%;font-family:verdana;text-decoration:none;}td.reqData {font-size:12px;color:" +
				"#000000;line-height:100%;font-family:verdana;text-decoration:none;}</style></head><body leftmargin=\"0\" " +
				"marginwidth=\"0\" topmargin=\"0\" marginheight=\"0\" offset=\"0\" bgcolor='#FFFFFF'>";

		String logoheader = "<div id=\"header\"><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr> " +
				"<td align=\"left\" valign=\"middle\" class=\"header\"><img src=\"" + logo +
				"\" height=\"60px\" width=\"250px\" BORDER=\"0\" align=\"center\" /> \n </td> <td align=\"left\" valign=\"middle\""+
				" class=\"header\"><span style=\"font-size:14px;font-weight:bold;color:#000000;line-height:200%;"+
				"font-family:verdana;text-decoration:none;\">CONSOLIDATED AUTOMATION TEST RESULTS</span></td>" +
				"<td align=\"\" valign=\"middle\" style=\"background-color:#3399FF;border-top:0px solid #000000;" +
				"border-bottom:1px dashed #000000;\"><span style=\"font-size:15px;font-weight:bold;color:#000000;" +
				"line-height:100%;font-family:verdana;text-decoration:none;\"></span></td></tr></table></div>";

		String appheader= "<div id=\"testDetails\"><table width=\"100%\" cellpadding=\"3\" cellspacing=\"0\"> " +
				"<tr> <td align=\"left\" valign=\"middle\" class=\"testDetails\"><span  class=\"testDetails\">Date &amp; Time : " +
				exec_date + "</span></td><td align=\"left\" valign=\"middle\" class=\"testDetails\"><span  class=\"testDetails\">" +
				"Test Type : General</span></td><td align=\"left\" valign=\"middle\" class=\"testDetails\" colspan=\"2\"> <span " +
				"class=\"testDetails\">Application : <font color=\"#FFFFFF\">" + cnfDtls.getAppName() + "</font></span></td></tr>" +
				"</table></div>";

		BufferedWriter out = null;

		try{

			out = new BufferedWriter(new FileWriter("..//TestReports//" + app + "//" + "Integrated Test Results_" + fileName +".html"));
			out.write(htmlStyles.replace("><",">"+ln+ "<"));
			out.write(logoheader.replace("><",">"+ln+ "<"));
			out.write(appheader.replace("><",">"+ln+ "<"));
			out.write(execDetails.replace("><",">"+ln+ "<"));
			out.write(graph.replace("><",">"+ln+ "<"));
			out.write(genDetails.replace("><",">"+ln+ "<"));
			out.write(tcdetails.replace("><",">"+ln+ "<"));
			out.write("</body></html>");
			out.close();
		}catch(Exception e){
			System.out.println("Unable to create HTML File");
		}
	}

	private void createPDF() {

		try{
			Thread.sleep(2000);
			String url = new File("..//TestReports//" + app + "//" + "Integrated Test Results_" + fileName +".html").toURI().toURL().toString();
			System.out.println(url);
			OutputStream os = new FileOutputStream("..//TestReports//" + app + "//" + "Integrated Test Results_" + fileName +".pdf");  
			ITextRenderer renderer = new ITextRenderer();
			renderer.setDocument(url);      
			renderer.layout();
			renderer.createPDF(os);        
			os.close();
			Thread.sleep(2000);
		}catch(Exception e){
			System.out.println(" Unable to create PDF File");
			createpdf = false;
		}

	}

	private void sendMail(){

		String htmlFile = "..//TestReports//" + app + "//" + "Integrated Test Results_" + fileName +".html";
		String pdfFile = "..//TestReports//" + app + "//" + "Integrated Test Results_" + fileName +".pdf";
		String excelFile = "..//TestReports//" + app + "//" + "Integrated Test Results_" + fileName +".xls";

		Message msg;

		String smtpHostName = props.getProperty("SMTP_HOST_NAME");
		String recipient = props.getProperty("recipients");
		String reportType = props.getProperty("reportType");
		String from = props.getProperty("from");
		//String subject = props.getProperty("subject","Automation Test Results") + " -- Integrated Reports";
		String subject;
		String message = props.getProperty("message");
		String port = props.getProperty("SMTP_PORT");		

		if(failed > 0){
			subject = props.getProperty("subject","Automation Test Results") + " - Failure";
		}
		else{
			subject = props.getProperty("subject","Automation Test Results") ;
		}

		String countersText = "<table border=3 cellpadding=8>"+
				"<tr><td><b> Browser Executed		</b></td><td> "+ cnfDtls.getBrowser() + " </td></tr>" +
				"<tr><td><b> URL					</b></td><td> "+ cnfDtls.getScriptPath() + " </td></tr>" +
				"<tr><td><b> Total Cases Executed 	</b></td><td> "+ total  + " </td></tr>" +
				"<tr><td><b> Total Cases Passed		</b></td><td> "+ passed + " </td></tr>" +
				"<tr><td><b> Total Cases Failed		</b></td><td> "+  failed+ " </td></tr>" + 
				"<tr><td><b> Total Cases Skipped	</b></td><td> "+ (total - (failed + passed)) + " </td></tr>" + 
				"<tr><td><b> Total Execution Time(in Seconds)		</b></td><td> " +totalTime+  " </td></tr>"+"</table> ";

		Properties newprops = new Properties();
		newprops.put("mail.smtp.host", smtpHostName);
		newprops.setProperty("mail.smtp.port", port);		
		
		
		Session session;
		if(!(props.getProperty("email_UserName","false").equals("false")&&props.getProperty("email_UserName","false").equals("false")))
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
					return new PasswordAuthentication(props.getProperty("email_UserName"), props.getProperty("email_Password"));
				}
			});
		}
		else{
			session = Session.getDefaultInstance(newprops, null);
		}
		if (reportType.equalsIgnoreCase("Basic")) {
			message = message.replace("&&Counters&&",countersText);
		} else if (reportType.equalsIgnoreCase("HTML")) {
			message = "";
			try {
				String line;
				BufferedReader input = new BufferedReader(new FileReader(htmlFile));			
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

			String[] recipients = recipient.split(";");
			InternetAddress[] addressTo =new InternetAddress[recipients.length];
			for (int i = 0; i < recipients.length; i++)
			{
				addressTo[i] = new InternetAddress(recipients[i]);
			}

			msg.setRecipients(Message.RecipientType.TO, addressTo);

			Date d = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy").parse(exec_date);

			//msg.setSubject(subject +" - "+ d.toString() );
			msg.setSubject(subject );

			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(message, "text/html");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);



			messageBodyPart = new MimeBodyPart();
			DataSource tResult = new FileDataSource(excelFile);
			messageBodyPart.setDataHandler(new DataHandler(tResult));
			messageBodyPart.setFileName(tResult.getName());
			multipart.addBodyPart(messageBodyPart);

			String testData = props.getProperty("attachment1","$$");
			if(!testData.equalsIgnoreCase("$$")) {
				messageBodyPart = new MimeBodyPart();
				DataSource tData = new FileDataSource("..//TestInputs//"+testData);
				messageBodyPart.setDataHandler(new DataHandler(tData));
				messageBodyPart.setFileName(testData);
				multipart.addBodyPart(messageBodyPart);
			}

			String checklist = props.getProperty("attachment2","$$");

			if(!checklist.equalsIgnoreCase("$$")) {
				messageBodyPart = new MimeBodyPart();		
				DataSource tChecklist = new FileDataSource("..//TestInputs//"+checklist);
				messageBodyPart.setDataHandler(new DataHandler(tChecklist));
				messageBodyPart.setFileName(checklist);
				multipart.addBodyPart(messageBodyPart);
			}

			messageBodyPart = new MimeBodyPart();		
			DataSource tReport = new FileDataSource(htmlFile);
			messageBodyPart.setDataHandler(new DataHandler(tReport));
			messageBodyPart.setFileName(tReport.getName());
			multipart.addBodyPart(messageBodyPart);

			if(createpdf){

				messageBodyPart = new MimeBodyPart();		
				DataSource tReportPDF = new FileDataSource(pdfFile);
				messageBodyPart.setDataHandler(new DataHandler(tReportPDF));
				messageBodyPart.setFileName(tReportPDF.getName());
				multipart.addBodyPart(messageBodyPart);
			}

			msg.setSentDate(new Date());
			msg.setContent(multipart);
			System.out.println("Sending Email...");
			Transport.send(msg);
			System.out.println("Report E-mail Sent.");
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Unable to send mail");
			System.out.println(e.getMessage());
		}
	}
}
