package com.java;

import java.util.*;

import com.java.importnexport.ImportConfigDetails;
import com.java.objects.ConfigDetails;

/**
 *  Core class of the framework , which is used to read the test Configuration details and invoke multiple instances
 *  of SeleniumDriver class based on the no.of rows provided
 */

public class RunTest {

	public static HashMap<String,String> multireport = new HashMap<String,String>();

	public static int exit_status = 0;
	public static int configrowCount = 0;

	public static void main(String ar[]) throws Exception {

		String startTime =  new Date().getTime()+"";
		//System.out.println("Start Time is :"+startTime);

//		MacValidation mac= new MacValidation();
//		HashMap<String, String> macValidation=mac.validate();
//
//		if(macValidation.get("status").toString().equals("false"))
//		{
//			System.out.println("Sorry MAC Address :"+macValidation.get("ActualdMacAddress")+" is invalid, Aborting the execution. Please contact Framework provider. ");
//
//
//			System.exit(5);
//		}
//
		String configFile = "Config.xml";
		// configFile = "DB";

		if(ar.length!=0){

			configFile = ar[0];
		}
		ArrayList<ConfigDetails>  confgDtls = new ArrayList<ConfigDetails>();
		ImportConfigDetails impCnf = new ImportConfigDetails(configFile);

		if(ar.length>0){

			confgDtls =	impCnf.readFromCmd(ar);	
		}
		else{
			confgDtls = impCnf.readConfigData();
		}

		System.out.println("--------------------------------");

		int configCount = confgDtls.size();

		int iteration = 0;

		while(iteration != configCount){
			ConfigDetails temp = confgDtls.get(iteration);
			iteration = iteration + 1;
			if(!temp.isFuncDriven()){
				SeleniumDriver seldriver = new SeleniumDriver(temp);
				Thread threadObject = new Thread(seldriver,"ConfigRow"+iteration);
				threadObject.start();
				Thread.sleep(2000);
			}
		}

		iteration = 0;

		while(iteration != configCount){
			ConfigDetails temp = confgDtls.get(iteration);
			iteration = iteration + 1;

			if(temp.isFuncDriven()){
				FunctionalDriver fundriver = new FunctionalDriver(temp);
				Thread threadObject = new Thread(fundriver,"ConfigRow"+iteration);
				threadObject.start();
				Thread.sleep(2000);
			}
		}

		while(true){
			Thread.sleep(10000);
			if(configrowCount == configCount)
				break;
		}

		Iterator<String> appName = multireport.keySet().iterator();

		String endTime =  new Date().getTime()+"";
		//System.out.println("End Time is :"+endTime);
		double end = Double.parseDouble(endTime);
		double start = Double.parseDouble(startTime);
		double totalTime = (end - start)/1000;
		totalTime = (double) Math.round(totalTime * 100) / 100;
		//System.out.println("Total Time is :"+totalTime);
		while(appName.hasNext()){

			String app = appName.next().toString();
			String fileList = multireport.get(app);

			for(ConfigDetails cnfDtls : confgDtls ){

				if(cnfDtls.getAppName().equals(app)){
					IntegrateReports is = new IntegrateReports(app,fileList,cnfDtls,totalTime);
					is.integrateReports();
					Thread.sleep(2000);
					break;
				}
			}
		}

		System.out.println("Completed Execution \n Exit code is " + exit_status);
		Runtime.getRuntime().exit(exit_status);
	}
}