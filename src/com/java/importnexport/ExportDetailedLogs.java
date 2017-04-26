package com.java.importnexport;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.java.SeleniumDriver;
import com.java.objects.ResultDetails;
import com.java.objects.TestDataDetails;

/**
 * This is used to write the data into an Excel sheet
 */

public class ExportDetailedLogs {

	public SeleniumDriver driver;

	public ExportDetailedLogs(SeleniumDriver driver) {
		this.driver = driver;
	}

	/**
	 * This method is to export the test step level log to the Database
	 */
	
	public void getMaxLog(){

		Connection conn = null;

		String url = driver.miscProps.getProperty("url");
		String dbName = driver.miscProps.getProperty("dbName");
		String password = driver.miscProps.getProperty("password");
		String userName = driver.miscProps.getProperty("userName");
		String jdbcDriver = driver.miscProps.getProperty("driver");
		
		boolean status = driver.detailedLog || driver.updateResultsDB;
		
		String execlog =  new SimpleDateFormat("MMddyy_HHmmss").format(new Date());
		driver.miscProps.setProperty("execlog", execlog);
		
		if(!status){
			driver.log.info(" Max Log Value :: " + execlog);
			return;
		}

		try {
			
			driver.log.info(" JDBC Class  :: " + jdbcDriver);
			driver.log.info(" Database Name :: " + dbName);
			driver.log.info(" DB Username :: " + userName);
			driver.log.info(" DB Password :: " + password);
			
			Class.forName(jdbcDriver);
			conn = DriverManager.getConnection(url + dbName, userName, password);
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("Select max(tcexecutionlog) from testresults where AppName='"+driver.confDtls.getAppName()+"'");
			while (rs.next()) {
				execlog = rs.getString(1);
				if (execlog == null)
					execlog = "1";
				else
					execlog = (Integer.parseInt(execlog) + 1) + "";
				driver.miscProps.setProperty("execlog", execlog);
				break;
			}
			rs.close();
		} catch (Exception e) {
			driver.log.error(" Exception while retrieving max log value from DB");
			driver.log.error(e.getMessage());
			System.out.println("Exception while retrieving max logs from database");
			System.out.println(e.getMessage());
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
				}
			}
			driver.log.info(" Max Log Value :: " + execlog);
		}
	}
	
	
	public void exportResultsToSQL(TestDataDetails tdd,ResultDetails resultDetails) {
		
		Connection conn = null;

		String url = driver.miscProps.getProperty("url");
		String dbName = driver.miscProps.getProperty("dbName");
		String password = driver.miscProps.getProperty("password");
		String userName = driver.miscProps.getProperty("userName");
		String jdbcDriver = driver.miscProps.getProperty("driver");
	

		driver.log.info(" Exporting Detailed Test Step Result  to DB ");
		driver.log.info(" TestCase ID : " + driver.confDtls.getPrefix()+tdd.getTestCaseID());
		driver.log.info(" Test Step No : " + tdd.getTestDataID());
		driver.log.info(" Status : " + (resultDetails.getFlag() ?  " P A S S " : " F A I L"));

		String execlog = driver.miscProps.getProperty("execlog");
		String status = "FAIL";
		String message = resultDetails.getErrorMessage();
		
		if(resultDetails.getFlag()){
			status = "PASS";
			message = resultDetails.getWarningMessage();
		}

		if(message == null)
			message = "";
		message = message.replace("'","''");
		try {
			Class.forName(jdbcDriver);
			conn = DriverManager.getConnection(url + dbName, userName, password);
			Statement st = conn.createStatement();
					
			String comment = "";
			
			if(driver.inLoop)
				comment = "Looping : " + driver.hMap.get("LOOPCOUNTER")+ " ";
			
			if(driver.inExecute)
				comment = "Parent Case : " + driver.hMap.get("current_execution")+ " ";
			
			driver.log.info(" Message : " + comment);
			
			comment ="'" + (comment + resultDetails.getComment()).replace("'","''") + "'";
			long end_time = Long.parseLong(driver.hMap.get("testStep_endTime"));
			long start_time =Long.parseLong(driver.hMap.get("testStep_startTime"));
			
			if(tdd.getActionType().equalsIgnoreCase("EXECUTETESTCASE"))
			     start_time = Long.parseLong(driver.hMap.get("executeTestCase_startTime"));
			
				
			double taken = (end_time - start_time); 

		
			taken =  (taken / 1000.0);

			String query = "INSERT INTO testlog VALUES('"+driver.confDtls.getAppName()+"','" + driver.confDtls.getPrefix()+tdd.getTestCaseID() + "',"+ tdd.getTestDataID() + 
			",'" + tdd.getTestCaseTitle().replace("'","''") + "',' " + status + "','"+message 	+ "',NULL," + execlog + ","+comment+","+taken+")";
			
			st.executeUpdate(query);
		} catch (ClassNotFoundException e) {

			driver.log.error(" Unable to send detailed logs to DB ");
			driver.log.error(e.getMessage() );
			System.out.println("Can't load the class. Please add the respective jars to CLASSPATH. Unable to perform detailed logs for this thread");
			driver.detailedLog = false;
			System.out.println(e.getMessage());
			return;

		} catch (SQLException e) {
			
			driver.log.error(" Unable to send detailed logs to DB ");
			driver.log.error(e.getMessage() );
			
			System.out.println("SQL Exception while updating detailed logs to database");
			System.out.println(e.getMessage());
			
		} catch (Exception e) {

			driver.log.error(" Unable to send detailed logs to DB ");
			driver.log.error(e.getMessage() );
			
			System.out.println("Exception while updating detailed logs to database");
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
