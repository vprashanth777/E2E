package com.java.importnexport;

import java.io.*;
import java.sql.*;
import java.util.*;
import org.apache.poi.common.usermodel.Hyperlink;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CreationHelper;

import com.java.RunTest;
import com.java.SeleniumDriver;

/**
 * This is used to write the data into an Excel sheet
 */

public class ExportTestResults {

	public SeleniumDriver driver;

	public ExportTestResults(SeleniumDriver driver) {
		this.driver = driver;
	}

	HSSFWorkbook wb = new HSSFWorkbook();

	String fileName = null;
	String htmlFileName = null;
	FileOutputStream fileOut = null;
	InputStream inputStream = null;

	Calendar cal = Calendar.getInstance();
	public final String dateTime1 = "EEE MMM dd hh:mm:ss z yyyy";
	Properties props = new Properties();
	public String testResultPath;
	public String testHTMLResultPath;
	public int headings = 0;

	/**
	 * This method is to write the data into Excel Sheet(Test Results Header)
	 * 
	 * @param result
	 */
	public void exportExcelHeader() {
		try {

			driver.log.info(" Creating Excel File for test results ");
			
			if (headings == 0) {

				String timeStamp = driver.hMap.get("TimeStamp")+"_"+driver.confDtls.getBrowser().toUpperCase();

				fileName = "Test Results_" + timeStamp + ".xls";
				htmlFileName = "Test Results_" + timeStamp + ".html";
				String PDFFileName = "Test Results_" + timeStamp + ".pdf";
				testResultPath = "..//TestReports";
				if(driver.mergeReports){
					//String timeStamp  = driver.hMap.get("TimeStamp");
					if(RunTest.multireport.containsKey(driver.confDtls.getAppName())){
						timeStamp  = timeStamp + ";" + RunTest.multireport.get(driver.confDtls.getAppName());
					}
					RunTest.multireport.put(driver.confDtls.getAppName(),timeStamp);
				}
				
				try{
					testResultPath= testResultPath + "//" + driver.confDtls.getAppName();
					File f = new File(testResultPath);
					if(!f.exists())
						f.mkdir();
				}catch(Exception e){
					driver.log.error(" Unable to create DIR for Testresults Application " );
					System.out.println("Unable to create DIR for Testresults Application");
				}
				
				testHTMLResultPath = testResultPath+ "//" + htmlFileName;
				PDFFileName = testResultPath+ "//" + PDFFileName;
				testResultPath = testResultPath + "//"+ fileName;
				
				driver.hMap.put("htmlFile", htmlFileName);
				driver.hMap.put("PDFResultsFile", PDFFileName);
				
				driver.hMap.put("TestResultsPath", testResultPath);
				driver.hMap.put("testHTMLResultPath", testHTMLResultPath);

				System.out.println("OUT FILE : " + testResultPath);

				wb = new HSSFWorkbook();
				wb.createSheet("Test Result");
				fileOut = new FileOutputStream(testResultPath);
				System.out.println("Test Result file is created");
				HSSFSheet sheet = wb.getSheetAt(0);
				sheet.setAutobreaks(false);

				HSSFRow row = sheet.createRow((short) 0);
				HSSFFont font = wb.createFont();
				font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
				HSSFCellStyle cellStyle = wb.createCellStyle();
				cellStyle.setFont(font);

				// Setting Headings in Test Results file
				row.createCell(0).setCellStyle(cellStyle);
				row.createCell(1).setCellStyle(cellStyle);
				row.createCell(2).setCellStyle(cellStyle);
				row.createCell(3).setCellStyle(cellStyle);
				row.createCell(4).setCellStyle(cellStyle);
				row.createCell(5).setCellStyle(cellStyle);
				row.createCell(6).setCellStyle(cellStyle);

				row.createCell(0).setCellValue("Test Case ID");
				row.createCell(1).setCellValue("Test Case Title");
				row.createCell(2).setCellValue("Result(P/F)");
				row.createCell(3).setCellValue("Error Message");
				row.createCell(4).setCellValue("Time Stamp");
				row.createCell(5).setCellValue("Time Taken (in Seconds)");
				row.createCell(6).setCellValue("Comment");
				
				if(driver.createEvidence){
					row.createCell(7).setCellStyle(cellStyle);
					row.createCell(7).setCellValue("Evidence");
				}
				
				headings = 1;
			}
			wb.write(fileOut);
			fileOut.close();
			
			driver.log.info(" Excel File is created with headers " );
			
		} catch (Exception e) {
			
			driver.log.error(" Excel File is not created with headers " );
			driver.log.error(e.getMessage());
			System.out.println(" Unable to create headers in Excel report");
			System.out.println(e.getMessage());
		}
	}

	/**
	 * This is used to write the test case results into an Excel sheet after
	 * completing the each test case execution
	 * 
	 * @throws IOException
	 */
	public void exportExcelRows(List<String> result) {
		
		try {

			driver.log.info(" Exporting Test case result to excel files " );
			
			System.out.println("testResultPath = " + testResultPath);
			inputStream = new FileInputStream(testResultPath);
			POIFSFileSystem fs = new POIFSFileSystem(inputStream);
			fileOut = new FileOutputStream(testResultPath);
			HSSFWorkbook wb = new HSSFWorkbook(fs);

			HSSFSheet sheet = wb.getSheetAt(0);
			sheet.setAutobreaks(false);
			System.out.println("result.size() = " + result.size());
			int rows; // No of rows
			rows = sheet.getPhysicalNumberOfRows();
			System.out.println("No of rows in sheet=" + rows);
			for (int ii = 0; ii < result.size(); ii++)
				System.out.println("Result = " + result.get(ii));

			HSSFRow row = sheet.createRow(rows);
			result.remove(1);
			boolean  status = (!result.get(2).equalsIgnoreCase("skipped"));
			
			for (int i = 0; i < 5; i++) {
				sheet.setColumnWidth(2,(256 * 13));
				sheet.setColumnWidth(1, (256 * 25));
				sheet.setColumnWidth(2, (256 * 14));
				sheet.setColumnWidth(3, (256 * 30));
				sheet.setColumnWidth(4, (256 * 28));
				sheet.setColumnWidth(5, (256 * 28));
				sheet.setColumnWidth(6, (256 * 28));
				HSSFCell cell = row.createCell(i);
				driver.log.info(" ..in export.." + result.get(i).toString());
				System.out.println("..in export.." + result.get(i).toString());
				HSSFRichTextString str = new HSSFRichTextString(result.get(i).toString());
				cell.setCellValue(str);
			}
			
			try{
				long end_time = Long.parseLong(driver.hMap.get("time_End"));
				long start_time = Long.parseLong(driver.hMap.get("time_Start"));
					
				long taken = (end_time - start_time - driver.sleepcounter); 

				int n =(int) (taken % 1000);
				taken = taken / 1000;

				String val = " Unable to calculate";
				HSSFCell cell = row.createCell(5);
									
				if(taken >= 0)
					val = taken+"."+n;
				
				System.out.println("..in export.." + val);
				driver.log.info("  Exporting time taken to execute :: " + val);
				HSSFRichTextString str = new HSSFRichTextString(val);
				cell.setCellValue(str);
				driver.hMap.put("TiMe_CaLc",val);
				
				if(driver.hMap.containsKey("comment_excel_write")&& status){
					sheet.setColumnWidth(6,(256 * 28));
					cell = row.createCell(6);
					System.out.println("..in export.." + "Comments");
					driver.log.info("  Exporting Comments  :: " + driver.hMap.get("comment_excel_write"));
					System.out.println(driver.hMap.get("comment_excel_write"));
					cell.setCellValue(driver.hMap.get("comment_excel_write")); 
				}

				if(driver.createEvidence && status){
					
					sheet.setColumnWidth(7,(256 * 28));
					cell = row.createCell(7);
					System.out.println("..in export.." + "Evidence");
					CreationHelper createHelper = wb.getCreationHelper();
					cell.setCellValue("Click Here"); 
					Hyperlink link = createHelper.createHyperlink(Hyperlink.LINK_FILE);
					System.out.println(driver.hMap.get("pdf"));
					File f = new File(driver.hMap.get("pdf"));
					link.setAddress(f.getCanonicalPath());
					driver.log.info("  Exporting Evidence File  :: " + f.getCanonicalPath());
					cell.setHyperlink((org.apache.poi.ss.usermodel.Hyperlink) link);
				}
			}catch(Exception e){
				driver.log.error(" Exception occured while exporting time taken / evidence to excel ");
				driver.log.error(e.getMessage());
				System.out.println("Exception occured while exporting time taken / evidence to excel ");
				System.out.println(e.getMessage());
			}

			wb.write(fileOut);
			fileOut.close();

		} catch(Exception e){

			driver.log.error(" Exception occured while exporting results to excel ");
			driver.log.error(e.getMessage());

			System.out.println("Exception occured while exporting results list to excel ");
			System.out.println(e.getMessage());
		}
	}

	/**
	 * This is used to write the Test Summary into an Test Results Excel Sheet
	 * after completion of total test cases execution.
	 */

	public void exportTestSummary(List<String> result) {
		try {
			
			driver.log.info(" Exporting Execution Summary to excel files " );
			
			inputStream = new FileInputStream(testResultPath);
			POIFSFileSystem fs = new POIFSFileSystem(inputStream);
			fileOut = new FileOutputStream(testResultPath);
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(0);
			sheet.setAutobreaks(false);
			int rows;
			rows = sheet.getPhysicalNumberOfRows();
			
			int total = Integer.parseInt(result.get(1));
			int pass = Integer.parseInt(result.get(2));;
			int fail = Integer.parseInt(result.get(3));
			int skip = total - pass - fail;
			
			driver.log.info(" Browser :: "  + result.get(0));
			driver.log.info(" Total Cases :: "  + total);
			driver.log.info(" Passed :: " + pass);
			driver.log.info(" Failed :: " + fail);
			driver.log.info(" Skipped :: " + skip);
			
			for (int ii = 0; ii <= result.size(); ii++) {

				HSSFRow row = sheet.createRow(rows + ii + 1);
				HSSFCell cell;
				HSSFRichTextString str;

				switch (ii) {
				case 0:
					cell = row.createCell(1);
					cell.setCellValue("Browser Tested");
					cell = row.createCell(2);
					str = new HSSFRichTextString(result.get(ii).toString());
					cell.setCellValue(str);
					break;
				case 1:
					cell = row.createCell(1);
					cell.setCellValue("Total Cases Executed");
					cell = row.createCell(2);
					str = new HSSFRichTextString(result.get(ii).toString());
					cell.setCellValue(str);
					break;
				case 2:
					cell = row.createCell(1);
					cell.setCellValue("Total Cases Passed");
					cell = row.createCell(2);
					str = new HSSFRichTextString(result.get(ii).toString());
					cell.setCellValue(str);
					break;
				case 3:
					cell = row.createCell(1);
					cell.setCellValue("Total Cases Failed");
					cell = row.createCell(2);
					str = new HSSFRichTextString(result.get(ii).toString());
					cell.setCellValue(str);
					break;
				case 4:
					cell = row.createCell(1);
					cell.setCellValue("Total Cases Skipped");
					cell = row.createCell(2);
					str = new HSSFRichTextString(skip+"");
					cell.setCellValue(str);
				break;
				}
			}

			wb.write(fileOut);
			fileOut.close();
		} catch (Exception e) {
			
			driver.log.error(" Exception while exporting the testresults Summary " );
			driver.log.error(e.getMessage());
			System.out.println("Exception while exporting the testresults Summary");
			System.out.println(e.getMessage());
		} finally {
			try {
				fileOut.close();
			} catch (Exception e) {
				System.out.println("Exception while exporting the testresults Summary");
				System.out.println(e.getMessage());
			}
		}
	}

	public void exportResultsToSQL(List<String> result) {

		Connection conn = null;
		
		driver.log.info(" Exporting Test Results to DB ");
		
		String url = driver.miscProps.getProperty("url");
		String dbName = driver.miscProps.getProperty("dbName");
		String password = driver.miscProps.getProperty("password");
		String userName = driver.miscProps.getProperty("userName");
		String jdbcDriver = driver.miscProps.getProperty("driver");

		String id = result.get(0);
		String title = result.get(1).replace("'", "''");
		String status = result.get(2);
		String errorReason = result.get(3);
		String execlog = driver.miscProps.getProperty("execlog");

		if (errorReason != null) {
			int n = errorReason.indexOf("Screen Shot");
			if (n == -1)
				n = errorReason.length();
			errorReason = errorReason.substring(0, n);
			errorReason = errorReason.replace("'", "''");
		}
		try {
			Class.forName(jdbcDriver);
			conn = DriverManager.getConnection(url + dbName, userName, password);
			Statement st = conn.createStatement();
		
			String query = "INSERT INTO testresults VALUES('"+driver.confDtls.getAppName()+"','" + id + "',' "
					+ title + "',' " + status + "',' " + errorReason
					+ "',NULL," + execlog + ","+driver.hMap.get("TiMe_CaLc")+")";
			st.executeUpdate(query);
		} catch (ClassNotFoundException e) {
			
			driver.log.error(" Encountered ClassNotFoundException ");
			driver.log.error(e.getMessage());
			System.out.println("Can't load the class. Please add the respective jars to CLASSPATH");
			System.out.println("Will not log test results further");
			System.out.println(e.getMessage());
			driver.updateResultsDB = false;
			return;
		} catch (SQLException e) {
			
			driver.log.error(" Encountered SQLException ");
			driver.log.error(e.getMessage());
			System.out.println("SQL Exception while updting results to DB");
			System.out.println(e.getMessage());
		}catch (Exception e) {
			driver.log.error(" Encountered Unknown Exception ");
			driver.log.error(e.getMessage());
			System.out.println("Exception while updating results to DB");
			System.out.println(e.getMessage());
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
				}
			}
		}
	}
}