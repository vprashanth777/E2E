package com.java;

import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;

import org.xhtmlrenderer.pdf.ITextRenderer;

import com.java.SeleniumDriver;

/**
 * Class which is used to generate the HTML template report
 */
@SuppressWarnings("rawtypes")
public class TemplateGenerator {

	public SeleniumDriver driver;

	public String logo = "";
	private int total;
	private int passed;
	private int failed;
	private int skipped;
	private String ln = "\n";
public	double totalexecutiontime;

	/**
	 * Constructor which is used to create the template generator object
	 */

	public TemplateGenerator(SeleniumDriver driver) {
		this.driver = driver;
		String defaultLogo = "http://valuelabs.com/templates/valuelabs/images/valuelabs-logo.gif";
		logo = driver.miscProps.getProperty("logo", defaultLogo);
	}

	/**
	 * Method to build the template. Takes the count of total , passed , failed
	 * cases as parameters
	 * 
	 * @param Total
	 * @param Passed
	 * @param Failed
	 */

	public void buildTemplate(int Total, int Passed, int Failed) {

		driver.log.info(" Creating HTML Report ");

		String testType = "General";

		total = driver.TestCaseExecutionDetails.size();
		passed = Passed;
		failed = Failed;
		skipped = total - Total;
		String str = driver.confDtls.getTestCases();

		if (str.startsWith("tt:"))
			testType = str.substring(3);

		String resultsType = "Smoke Test Results";
		String strChart = passed + "," + failed + "," + skipped;

		String strBrowser = driver.hMap.get("Browser");
		String strURL = driver.hMap.get("URL");
		String detailFileName = "";

		String parentFolder = "..//TestReports//" + driver.confDtls.getAppName() + "//";
		String chartDimensions = "";
		String chartMaxHeight = "";

		if (total < 10) {
			chartDimensions = "0|5|10";
			chartMaxHeight = "10";
		} else if ((total >= 10) && (total < 20)) {
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
		} else {
			System.out.println("Error: Invalid Chart Scale");
		}

		try {
			detailFileName = parentFolder + driver.hMap.get("htmlFile");
			writeDetailFile(detailFileName, testType, resultsType,strBrowser, strURL,
					strChart, chartDimensions,chartMaxHeight);
		} catch (Exception e) {

		}
	}

	/**
	 * Method used to create the template headers, footers & the generic
	 * structure
	 */

	public void writeDetailFile(String name, String testType,String resultsType,
			String strBrowser,String strURL, String strChart, String chartDimensions,
			String chartMaxHeight) {

	
		String file = name;
		totalexecutiontime = totalexecutiontime+Double.parseDouble(driver.hMap.get("TiMe_CaLc"));
		String totalTime = Double.toString(totalexecutiontime);
		System.out.println("TOTAL EXECUTION TIME :"+totalexecutiontime);
		driver.hMap.put("totalExecutionTime",totalTime);

		Date d = new Date();
		try {
			String startTime = driver.hMap.get("TimeStamp");
			d = new SimpleDateFormat("MMddyy_HHmmss").parse(driver.hMap.get("TimeStamp"));

		} catch (Exception e) {
		}

		String headContent = "<html> "
				+ ln
				+ " <head>"
				+ ln
				+ " <style>"
				+ ln
				+ "	td.header {"
				+ ln
				+ " background-color:#3399FF;border-top:0px solid #333333;border-bottom:1px dashed #000000;"
				+ ln
				+ "	}"
				+ " td.testDetails { "
				+ ln
				+ " background-color:#3399FF;border-top:5px solid #3399FF;border-bottom:1px dashed #000000;"
				+ ln
				+ "	}"
				+ ln
				+ " span.testDetails {"
				+ ln
				+ " font-size:12px;font-weight:bold;color:#000000;line-height:200%;font-family:verdana;text-decoration:none;"
				+ ln
				+ "}"
				+ ln
				+ "td.execDetails { "
				+ ln
				+ " background-color:#3399FF;border-top:5px solid #3399FF;border-bottom:0px dashed #000000;"
				+ ln
				+ "}"
				+ ln
				+ " span.execDetails {"
				+ ln
				+ " font-size:12px;font-weight:bold;color:#000000;line-height:200%;font-family:verdana;text-decoration:none;"
				+ ln
				+ "}"
				+ ln
				+ "span.pass { "
				+ ln
				+ " font-size: 14px;font-weight:bold;line-height:100%;color:#00FF00;font-family:arial; "
				+ ln
				+ "	}"
				+ ln
				+ " span.fail { "
				+ ln
				+ " font-size: 14px;font-weight:bold;color:#FF0000;line-height:100%;font-family:arial; "
				+ ln
				+ " } "
				+ ln
				+ " span.skip { "
				+ ln
				+ " font-size: 14px;font-weight:bold;color:#0000FF;line-height:100%;font-family:arial; "
				+ ln
				+ " } "
				+ ln
				+ " span.totalexecutiontime { "
				+ ln
				+ " font-size: 14px;font-weight:bold;color:#0000FF;line-height:100%;font-family:arial; "
				+ ln
				+ " } "
				+ ln
				+ " span.title { "
				+ ln
				+ " font-size: 14px;font-weight:normal;color:#000000;line-height:100%;font-family:arial; "
				+ ln
				+ " } "
				+ ln
				+ " td.reqDetails { "
				+ ln
				+ " font-size:12px;font-weight:bold;color:#000000;line-height:100%;font-family:verdana;text-decoration:none; "
				+ ln
				+ " } "
				+ ln
				+ " td.reqData {  "
				+ ln
				+ " font-size:12px;color:#000000;line-height:100%;font-family:verdana;text-decoration:none; "
				+ ln
				+ " } "
				+ ln
				+ " </style> "
				+ ln
				+ " </head> "
				+ ln
				+ "<body leftmargin=\"0\" marginwidth=\"0\" topmargin=\"0\" marginheight=\"0\" offset=\"0\" bgcolor='#FFFFFF'>";

		String header = "<div id=\"header\"> "
				+ ln
				+ " <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">"
				+ ln
				+ " <tr> "
				+ ln
				+ "<td align=\"left\" valign=\"middle\" class=\"header\"> "
				+ ln
				+ "<img id=\"editableImg1\""
				+ " src=\""
				+ logo
				+ "\" height=\"60px\" width=\"250px\" BORDER=\"0\" align=\"center\" />"
				+ ln
				+ "</td>"
				+ ln
				+ "<td align=\"left\""
				+ " valign=\"middle\" class=\"header\">"
				+ ln
				+ "<span style=\"font-size:14px;font-weight:bold;color:#000000;line-"
				+ "height:200%;font-family:verdana;text-decoration:none;\">"
				+ ln
				+ "AUTOMATION TEST RESULTS"
				+ ln
				+ "</span>"
				+ ln
				+ "</td>"
				+ ln
				+ "<td align=\"\" valign=\"middle\" style=\"background-color:#3399FF;border-top:0px solid #000000;border-bottom:"
				+ "1px dashed #000000;\">"
				+ ln
				+ " <span style=\"font-size:15px;font-weight:bold;color:#000000;line-height:100%;font-family:verdana;"
				+ "text-decoration:none;\">" + ln + "</span>" + ln + "</td>"
				+ ln + " </tr>" + ln + "</table>" + ln + "</div>";

		String testDetails = "<div id=\"testDetails\">"
				+ ln
				+ "<table width=\"100%\" cellpadding=\"3\" cellspacing=\"0\"> "
				+ ln
				+ "<tr> "
				+ ln
				+ " <td align=\"left\" valign=\"middle\" class=\"testDetails\"> "
				+ ln + "<span  class=\"testDetails\">" + ln + " Date &amp; Time : "
				+ d.toString() + ln + "</span>" + ln + "</td>" + ln
				+ "<td align=\"left\" valign=\"middle\" class=\"testDetails\">"
				+ ln + "<span  class=\"testDetails\">" + ln + "Test Type : "
				+ testType + ln + " </span> " + ln + " </td> " + ln
				+ "<td align=\"left\" "
				+ "valign=\"middle\" class=\"testDetails\" colspan=\"2\"> " + ln
				+ "<span  class=\"testDetails\"> " + ln
				+ "Application : <font color=\"#FFFFFF\">" + driver.confDtls.getAppName()
				+ " </font> " + ln + " </span>" + ln + " </td> " + ln
				+ " </tr>" + ln + " </table> " + ln + "</div>";


		String execDetails = "<div id=\"execDetails\"> "
				+ ln
				+ "<table width=\"100%\" cellpadding=\"3\" cellspacing=\"0\"> "
				+ ln
				+ "  <tr> "
				+ ln
				+ "<td align=\"left\" valign=\"middle\" class=\"execDetails\">"
				+ ln
				+ "<span class=\"execDetails\">"
				+ ln
				+ "Test Cases Executed : "
				+ total
				+ "</span>"
				+ ln
				+ "</td>"
				+ ln
				+ "	<td align=\"left\" valign=\"middle\" class=\"execDetails\">"
				+ ln + "<span class=\"execDetails\">" + ln + "Passed : "
				+ passed + "</span>" + ln + "</td>" + ln
				+ "<td align=\"left\" valign=\"middle\" class=\"execDetails\">"
				+ ln + "<span class=\"execDetails\">" + ln + "Failed :"
				+ failed + "</span>" + ln + "</td>" + ln
				+ "<td align=\"left\" valign=\"middle\" class=\"execDetails\">"
				+ ln + "<span class=\"execDetails\">" + ln + "Skipped : "
				+ skipped + "</span>" + ln + "</td>" + ln
				+ "<td align=\"left\" valign=\"middle\" class=\"execDetails\">"
				+ ln + "<span class=\"execDetails\">" + ln + "Browser: "
				+ strBrowser + "</span>" + ln + "</td>" + ln 
				//	+ "</tr>" + ln+ "</table>" + ln + "</div> <br/>"
				+ "<td align=\"left\" valign=\"middle\" class=\"execDetails\">"
				+ ln + "<span class=\"execDetails\">" + ln + "Total Execution Time(in Seconds): "
				+ driver.hMap.get("totalExecutionTime") + "</span>" + ln + "</td>" + ln + "</tr>" + ln
				+ "</table>" + ln + "</div> <br/>";//Total Execution Time(in seconds):

		String graph = "<div id=\"graph\"  style=\"padding-left:10px\" > "
				+ ln
				+ "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" bgcolor='#FFFFFF'> "
				+ ln
				+ "<tr> "
				+ ln
				+ "<td bgcolor=\"#FFFFFF\" valign=\"top\" width=\"99%\">"
				+ ln
				+ "<img id=\"graph\" src=\"http://chart.apis.google.com/chart?"
				+ "cht=bvg&amp;chs=350x175&amp;chd=t:"
				+ strChart
				+ "&amp;chds=0,"
				+ chartMaxHeight
				+ "&amp;chxt=x,y&amp;chxs=0,000000,12|1,000000,12&amp;chco=00FF00|FF0000|0000FF|FFFF00&amp;chbh=50,0,20&amp;"
				+ "chxl=0:|Passed|Failed|Skipped|1:|" + chartDimensions
				+ "&amp;chg=25,16.667,2,5&amp;chtt=Total+Test+Cases+=+" + total
				+ "&amp;chts=000000,15\" BORDER=\"0\" align=\"left\" />" + ln+ "</td>" + ln + "</tr>" + ln + "</table>" + ln + "</div>" + ln
				+ "<br/>";

		String genDetails = "<div id=\"genDetails\"  style=\"padding-left:10px\" >"
				+ ln
				+ "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" bgcolor='#FFFFFF'>"
				+ "<tr>"
				+ ln
				+ "<td>"
				+ ln
				+ "<span style=\"font-size:20px;font-weight:bold;color:#000000;font-family:arial;line-height:110%;\">"
				+ ln
				+ "General Details"
				+ ln
				+ "</span>"
				+ ln
				+ "</td>"
				+ ln
				+ " </tr>"
				+ ln
				+ "<tr>"
				+ ln
				+ "<td>"
				+ ln
				+ "<span style=\"font-size:12px;font-weight:bold;color:#000000;font-family:arial;"
				+ "line-height:110%;\">"
				+ ln
				+ "URL : "
				+ ln
				+ "</span>"
				+ ln
				+ "<a href=\""
				+ strURL
				+ "\" style=\"font-size:12px;color:#0000FF;line-height:150%;font-family:trebuchet ms;\">"
				+ ln
				+ strURL
				+ "</a> "
				+ ln
				+ "</td>"
				+ ln
				+ " </tr>"
				+ ln
				+ "</table>" + ln + "</div>";

		String testCaseDetails = "<div id=\"testcaseDetails\" style=\"padding-left:15px\">"
				+ ln
				+ " <p> "
				+ "<span style=\"font-size: 15px;font-weight:bold;color:#000000;font-family:arial;\">Items Tested:</span> </p>"
				+ ln;

		try {

			Set TCset = driver.TestCaseExecutionDetails.keySet();
			Iterator TCiter = TCset.iterator();

			int a[] = new int[TCset.size()];
			int count = 0;
			while (TCiter.hasNext()) {
				a[count] = Integer.parseInt(TCiter.next().toString());
				count++;
			}

			Arrays.sort(a);

			for(int Key : a){

				driver.log.info(" Exporting case :: " + Key);

				String	style = "";
				String Value = driver.TestCaseDetails.get(Key);
				System.out.println("ID " +driver.confDtls.getPrefix()+ Key + " : " + Value);

				if (driver.TestCaseExecutionDetails.get(Key) == "PASS") {
					style = "pass";
					Value = Value + " - Passed";
				} else if (driver.TestCaseExecutionDetails.get(Key).startsWith("FAIL")) {
					style = "fail";
					Value = Value + " - Failed : "	+ driver.TestCaseExecutionDetails.get(Key).substring(4);
				} else if (driver.TestCaseExecutionDetails.get(Key) == ("SKIPPED")) {
					style = "skip";
					Value = Value + " -  No Test Steps Available - Skipped";
				}

				if (driver.TestCaseExecutionDetails.get(Key).startsWith("FAIL"))
				{
					testCaseDetails = testCaseDetails + "<p> <span class=\""+ style + "\">" + driver.confDtls.getPrefix()+Key + ": </span>" + ln;
					testCaseDetails = testCaseDetails + "<span class=\"title\">"+ Value.replace("&", "&amp;") + ": </span>" + ln ;
					try{
						if(driver.FailedCaseScreenShot.get(String.valueOf(Key)).toString().contains("\\..\\"))
						{
							String path=driver.FailedCaseScreenShot.get(String.valueOf(Key)).replace("\\..\\", "/../");
							testCaseDetails = testCaseDetails + "<a href=file:///"+path.replace("\\", "//")+" target=\"_blank\">ScreenShot</a>" + ln + "</p>";
						}
						else if(driver.FailedCaseScreenShot.get(String.valueOf(Key)).toString().contains("Unable to capture the screen shot"))
						{
							testCaseDetails = testCaseDetails + "<a href=file:///"+driver.FailedCaseScreenShot.get(String.valueOf(Key))+" target=\"_blank\"> :ScreenShot: "+driver.FailedCaseScreenShot.get(String.valueOf(Key))+"</a>" + ln + "</p>";
						}
						else{

							testCaseDetails = testCaseDetails + "<a href=file:///"+driver.FailedCaseScreenShot.get(String.valueOf(Key)).replace("\\", "//")+" target=\"_blank\">ScreenShot</a>" + ln + "</p>";
						}
					}catch(Exception e){}
				}
				else
				{
					testCaseDetails = testCaseDetails + "<p> <span class=\""+ style + "\">" + driver.confDtls.getPrefix()+Key + ": </span>" + ln;
					testCaseDetails = testCaseDetails + "<span class=\"title\">"+ Value.replace("&", "&amp;") + "</span>" + ln + "</p>";
				}
			}
		} catch (Exception e) {
			driver.log.error("  Exception while exporting Cases to HTML");
			driver.log.error(e.getMessage());
			System.out.println(e.getMessage());
		}

		testCaseDetails = testCaseDetails + "</div>" + ln + "<br/>";

		BufferedWriter out = null;

		try {
			out = new BufferedWriter(new FileWriter(file));
			out.write(headContent);
			out.write(header);
			out.write(testDetails);
			out.write(execDetails);
			out.write(graph);
			out.write(genDetails);
			out.write(testCaseDetails);

			/*out.write("");

			if (driver.reportPriority)
				out.write(reportPriority());

			if (driver.reportReq)
				out.write(reportRequirement());*/

			out.write("</body>" + ln + "</html>");

		} catch (Exception e) {

			driver.log.error("  Exception while generating html report ");
			driver.log.error(e.getMessage());

			System.err.println(" Exception while generating html report");
			System.out.println(e.getMessage());
		}finally{
			try{
				out.close();
			}catch(Exception e){}
		}

		if(driver.createpdf)
			createPDF();
	}

	private void createPDF() {

		try{
			driver.log.info(" Creating PDF Report ");
			Thread.sleep(2000);
			String url = new File(driver.hMap.get("testHTMLResultPath")).toURI().toURL().toString();
			OutputStream os = new FileOutputStream(driver.hMap.get("PDFResultsFile"));  
			ITextRenderer renderer = new ITextRenderer();
			renderer.setDocument(url);      
			renderer.layout();
			renderer.createPDF(os);        
			os.close();
			Thread.sleep(2000);
		}catch(Exception e){

			driver.log.error("  Exception while Creating PDF report ");
			driver.log.error(e.getMessage());

			System.out.println("Unable to create PDF Report File");
			System.out.println("Exception Message :: " + e.getMessage());
			driver.createpdf = false;
		}
	}

	public String reportRequirement(){

		driver.log.info(" Reporting Test Results on Requirement ");
		String content = "";
		try{
			content =  "<div id=\"reqCoverage\" style=\"padding-left:15px;display:table\">" + ln + "<table align=\"left\" " +
					"border=\"1px\" cellpadding=\"5px\" cellspacing=\"0px\">" + ln + "<tr>" + ln + "<td colspan=\"5\">" + ln + "<h2 " +
					"style=\"font-size:12px;font-weight:bold;color:#000000;line-height:150%;font-family:verdana;text-decoration:none;\"" +
					" align=\"middle\"> Requirement Coverage Report </h2>" + ln + "</td>" + ln + "</tr>" + ln + "<tr>" + ln + "<td valign =\"middle\"" +
					" class=\"reqDetails\"> <b> Requirement </b> </td>" + ln + "<td valign =\"middle\" class=\"reqDetails\"> <b> % Executed </b> </td>" + 
					ln + "<td valign =\"middle\" class=\"reqDetails\"> <b> % Passed </b> </td>" + ln +
					"<td valign =\"middle\" class=\"reqDetails\"> <b> Executed Case ID's </b> </td>" + ln +
					"<td valign =\"middle\" class=\"reqDetails\"> <b> Passed Case ID's </b> </td>" + ln +
					"<td valign =\"middle\" class=\"reqDetails\"> <b> Total Execution Time(in Seconds) </b> </td>" + ln +
					" </tr>";

			int pass = 0;
			int count = 0;

			HashMap<String, String> temp = new HashMap<String, String>();
			Iterator testcases = driver.TestCaseExecutionDetails.keySet().iterator();

			while(testcases.hasNext()){
				int tcid = (Integer)testcases.next();
				temp.put(driver.confDtls.getPrefix().toUpperCase()+tcid,driver.TestCaseExecutionDetails.get(tcid));
			}

			Iterator req = driver.TestCaseReq.keySet().iterator(); 

			while(req.hasNext()){

				String strReq = req.next().toString();
				String cases[] = driver.TestCaseReq.get(strReq).split(","); 

				count = 0;
				pass = 0;
				ArrayList<String> executed = new ArrayList<String>();
				ArrayList<String> passed = new ArrayList<String>();

				int total = cases.length;

				for( String tcase : cases){

					String  tcid;
					if(driver.confDtls.getPrefix().length()!=0)
						tcid = tcase.substring(driver.confDtls.getPrefix().length());
					else
						tcid = tcase;


					if (temp.containsKey(tcase.toUpperCase())){
						executed.add(tcid); 
						count++;
						if (temp.get(tcase.toUpperCase()) == ("PASS")){
							pass++;
							passed.add(tcid);
						}
						if (temp.get(tcase.toUpperCase()) == ("SKIPPED")){
							executed.remove(tcid);
							count--;
						}
						temp.remove(tcase.toUpperCase());
					}
				}
				if(count!=0){
					content = content + "<tr>" + ln + "	<td valign =\"middle\" class=\"reqDetails\"> <b> " +
							strReq + ln + "</b> </td>" + ln +"<td valign =\"middle\" class=\"reqData\">  " +
							(int)(count*100.00/total)+"</td>" + ln+ "<td valign =\"middle\" class=\"reqData\"> "+
							(int)(pass*100.00/total)+ "</td>" + ln+ "<td valign =\"middle\" class=\"reqData\"> "+
							getRange(executed)+ "</td>" + ln+"<td valign =\"middle\" class=\"reqData\"> "+
							getRange(passed)+ "</td>" + ln+"</tr>" + ln;
				}
			}

			if(!temp.isEmpty()){
				count = 0;
				pass = 0;
				ArrayList<String> executed = new ArrayList<String>();
				ArrayList<String> passed = new ArrayList<String>();

				testcases = temp.keySet().iterator();

				while(testcases.hasNext()){

					int n = (Integer) testcases.next();

					if (temp.containsKey(n)){
						executed.add(n+""); 
						count++;
						if (temp.get(n) == ("PASS")){
							pass++;
							passed.add(n+"");
						}
						if (temp.get(n) == ("SKIPPED")){
							executed.remove(n+"");
							count--;
						}
					}
				}
				if(count != 0 ){
					content = content + "<tr>" + ln + "	<td valign =\"middle\" class=\"reqDetails\"> <b> " +
							" Not Specified " + ln + "</b> </td>" + ln +"<td valign =\"middle\" class=\"reqData\">  " +
							(int)(100)+"</td>" + ln+ "<td valign =\"middle\" class=\"reqData\"> "+
							(int)(pass*100.00/count)+ "</td>" + ln+ "<td valign =\"middle\" class=\"reqData\"> "+
							getRange(executed)+ "</td>" + ln+"<td valign =\"middle\" class=\"reqData\"> "+
							getRange(passed)+ "</td>" + ln+"</tr>" + ln;
				}
			}

			content =  content + "</table>"+ln+"</div>"+ln+"<br/>";
		}catch(Exception e){

		}
		return content;
	}

	public String reportPriority() {

		driver.log.info(" Reporting Test Results on Priorty ");
		String content = "";
		try{
			HashMap<String, String> temp = new HashMap<String, String>();
			Iterator testcases = driver.TestCaseExecutionDetails.keySet()
					.iterator();

			while (testcases.hasNext()) {

				int tcid = (Integer) testcases.next();
				temp.put(driver.confDtls.getPrefix().toUpperCase()+tcid, driver.TestCaseExecutionDetails.get(tcid));
			}

			boolean Nofailure = true;
			content = "<div id=\"priority\" style=\"padding-left:15px;display:table\">"
					+ ln
					+ "<table align=\"left\" "
					+ "border=\"1px\" cellpadding=\"5px\" cellspacing=\"0px\">"
					+ ln
					+ "<tr>"
					+ ln
					+ "<td colspan=\"3\">"
					+ ln
					+ "<h2 "
					+ "style=\"font-size:12px;font-weight:bold;color:#000000;line-height:150%;font-family:verdana;text-decoration:none;\""
					+ " align=\"middle\"> Priority Report </h2>"
					+ ln
					+ "</td>"
					+ ln
					+ "</tr>"
					+ ln
					+ "<tr>"
					+ ln
					+ "<td valign =\"middle\""
					+ " class=\"reqDetails\"> <b> Priority </b> </td>"
					+ ln
					+ "<td valign =\"middle\" class=\"reqDetails\"> <b> Failed Case Id's </b> </td>"
					+ ln
					+ "<td valign =\"middle\" class=\"reqDetails\"> <b> No. of Cases Failed </b> </td>"
					+ ln + " </tr>";

			Iterator priority = driver.TestCasePriority.keySet().iterator();
			int fail = 0;

			while (priority.hasNext()) {

				String priority_name = (String) priority.next();

				fail = 0;
				String cases[] = driver.TestCasePriority.get(priority_name).split(
						",");

				HashMap<String, String> pr = new HashMap<String, String>();
				ArrayList<String> failed = new ArrayList<String>();
				for (String tcase : cases) {
					if (pr.containsKey(tcase)) {
						continue;
					} else {
						pr.put(tcase, "");
					}
					if (temp.containsKey(tcase.toUpperCase())) {

						if (temp.get(tcase.toUpperCase()).startsWith("FAIL")) {
							fail++;
							failed.add(tcase.toUpperCase());
							Nofailure = false;
						}
						temp.remove(tcase.toUpperCase());
					}
				}
				if (fail != 0) {
					content = content + "<tr>" + ln
							+ "	<td valign =\"middle\" class=\"reqDetails\"> <b> "
							+ priority_name + ln + "</b> </td>" + ln
							+ "<td valign =\"middle\" class=\"reqData\"> "
							+ getRange(failed) + "</td>" + ln
							+ "<td valign =\"middle\" class=\"reqData\"> "
							+ (failed.size()) + "</td>" + ln + "</tr>" + ln;
				}
			}
			if (!temp.isEmpty()) {
				fail = 0;
				ArrayList<String> failed = new ArrayList<String>();
				testcases = temp.keySet().iterator();
				while (testcases.hasNext()) {

					int n = (Integer) testcases.next();

					if (temp.get(n).startsWith("FAIL")) {
						fail++;
						failed.add(n + "");
						Nofailure = false;
					}

				}

				if (fail != 0) {
					content = content + "<tr>" + ln
							+ "	<td valign =\"middle\" class=\"reqDetails\"> <b> "
							+ " P1 " + ln + "</b> </td>" + ln
							+ "<td valign =\"middle\" class=\"reqData\"> "
							+ getRange(failed) + "</td>" + ln
							+ "<td valign =\"middle\" class=\"reqData\"> "
							+ (failed.size()) + "</td>" + ln + "</tr>" + ln;
				}

			}

			content = content + "</table>" + ln + "</div>" + ln + "<br/>";
			if (Nofailure)
				content = "";
			return content;}
		catch(Exception e){

		}
		return content;
	}

	private String getRange(ArrayList testcases) {

		try{
			String range = "";
			int previous = 0;

			ArrayList<Integer> tcid = new ArrayList<Integer>();
			int n = testcases.size();
			int i = 0;

			HashMap<Integer, String> hm = new HashMap<Integer, String>();

			while (i < n) {
				try {
					if(testcases.get(i).toString().matches(".*([A-Z]).*"))
						tcid.add(Integer.parseInt((String) testcases.get(i).toString().substring(driver.confDtls.getPrefix().length())));
					else
						tcid.add(Integer.parseInt((String) testcases.get(i)));
				} catch (Exception e) {
				}
				i++;
			}

			Collections.sort(tcid);
			i = 0;
			n = tcid.size();
			while (i < n) {

				int testcase = tcid.get(i);
				if (!hm.containsKey(testcase)) {
					if (range.equalsIgnoreCase("")) {
						range = testcase + "";
						previous = testcase;
					} else {
						int diff = testcase - previous;
						if (diff != 1)
							range = range + "," + testcase;
						else {
							int index = range.lastIndexOf("-");
							if (index > 0) {
								String temp = range.substring(index + 1);
								if (temp.equalsIgnoreCase(previous + ""))
									range = range.substring(0, index + 1)+ testcase;
								else
									range = range + "-" + testcase;
							} else
								range = range + "-" + testcase;
						}
						previous = testcase;
					}
				}
				i++;
			}
			if(driver.confDtls.getPrefix().toString().length()!=0&&range.length()>0)
			{
				range=driver.confDtls.getPrefix()+range.replaceAll("-", "-"+driver.confDtls.getPrefix());/*+range.replaceAll(",",","+driver.confDtls.getPrefix());*/
				range=range.replaceAll(",",","+driver.confDtls.getPrefix());
			}
			return range;
		}catch(Exception e){

		}
		return "";
	}

}