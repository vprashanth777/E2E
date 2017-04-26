package com.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import com.java.objects.ResultDetails;

public class RestCalls {
	public static String SelectedHotelOptionIdentifier="";
	public static String HotelresultlistsIdentifier="";
	public static String FlightresultlistsIDentifier="";
	public static String SelectedFlightOptionIdentifier="";
	public static String SelectedInboundLegOptionIdentifier="";
	public static String SelectedOutboundLegOptionIdentifier="";
	public static String CombinedProductresultIdentifier="";
	public static String SelectedBedTypeIdentifier="";
	public static String SelectedHotelRoomOptionIdentifier="";
	public static String session="";
	public static String ShoppingCartID="";
	public static String SelectedPackageIdentifier="";
	public static String TravelInsuranceOptionIdentifier="";
	public static String TravelInsuranceBookingIdentifier="";
	public static String selectedHotelResultListIdentifier="";
	public static String selectPackageLink=null;
	public static String productKey="";
	public static String Traveler1Identifier="";
	public static String Person1Identifier="";
	public static String Traveler2Identifier="";
	public static String Person2Identifier="";
	public static String OrderIdentifier="";
	TestType tt;

	SeleniumDriver driver;
	ImportJsonMessage js= new ImportJsonMessage();

	public RestCalls(TestType tt) {
		this.tt = tt;
	}

	/*public RestCalls(SeleniumDriver driver) {
		this.driver = driver;
	}*/

	private ResultDetails resultDetails = new ResultDetails();



	public  static JSONObject getJsonObject(JSONObject jsonobj, String key)
	{
		JSONObject jb=null;
		try {

			jb = jsonobj.getJSONObject(key);
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}

		return jb;
	}

	public  static JSONObject getJsonObject(JSONArray jsonarry, int index)
	{
		JSONObject jb1=null;
		try {

			jb1 = jsonarry.getJSONObject(index);
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jb1;
	}


	public static JSONArray getAJsonArray(JSONObject jsonobj, String key)
	{
		JSONArray ja1=null;
		try {

			ja1 = jsonobj.getJSONArray(key);
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ja1;
	}



	public static JSONArray getAJsonArray(JSONArray jsonarry, int index)
	{
		JSONArray ja2=null;
		try {

			ja2 = jsonarry.getJSONArray(index);
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ja2;
	}

	public String getJsonValue(JSONObject jsonobj,String jsonFeild)

	{		
		String flds[]=jsonFeild.split("\\.");
		JSONObject temp=jsonobj;
		JSONArray temp1;
		String value1=null;
		for(int f=1;f<flds.length-1;f++)
		{
			if(!flds[f].contains("[")){			
				temp=getJsonObject(temp,flds[f]);
				System.out.println("The "+flds[f]+" value is-----" +temp);
			}
			else{
				temp1=getAJsonArray(temp,flds[f].substring(0,flds[f].indexOf("[")));
				System.out.println("The index value is-----"+flds[f].substring(flds[f].indexOf("[")+1,flds[f].indexOf("]")));
				int id=Integer.parseInt(flds[f].substring(flds[f].indexOf("[")+1,flds[f].indexOf("]")));
				temp=getJsonObject(temp1,id);						
			}
		}
		try {
			value1=temp.getString(flds[flds.length-1]);
			return value1;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//			e.getMessage();

		}
		try {
			int intValue=temp.getInt(flds[flds.length-1]);
			value1=Integer.toString(intValue);
			return value1;

		} catch (JSONException e) {

		}
		try {
			JSONObject jsonValue=temp.getJSONObject(flds[flds.length-1]);
			value1=jsonValue.toString();
			return value1;

		} catch (JSONException e) {


		}

		try {
			JSONArray jsonValue=temp.getJSONArray(flds[flds.length-1]);
			value1=jsonValue.toString();
			return value1;

		} catch (JSONException e) {

			//e.getMessage();			
		}
		return value1;
	}

	public  String getStringValue(String value)

	{
		String tempValue = value;

		if (tempValue.contains("HMV")) {

			Pattern pattern = Pattern.compile("HMV(.*?)\\$");
			Matcher matcher = pattern.matcher(tempValue);

			while (matcher.find()){
				String parameter = matcher.group(1);
				System.out.println("The Hash Mapp value of---" +parameter+ "------is-----" +tt.driver.hMap.get(parameter));
				tt.driver.log.info("The Hash Mapp value of---" +parameter+ "------is-----" +tt.driver.hMap.get(parameter));
				tempValue=tempValue.replaceAll("HMV"+parameter+"\\$",tt.driver.hMap.get(parameter));
			}

			System.out.println(" value = " + tempValue);
			tt.driver.log.info(" value = " + tempValue);
		}
		if (tempValue.contains("dt:")) {

			Pattern pattern = Pattern.compile("dt:(.*?)\\$");
			Matcher matcher = pattern.matcher(tempValue);

			while (matcher.find()){
				String parameter = matcher.group(1);
				System.out.println("Value from the test data is-----" +tt.driver.utils.getValue("dt:"+parameter));
				tt.driver.log.info("Value from the test data is-----" +tt.driver.utils.getValue("dt:"+parameter));
				tempValue=tempValue.replaceAll("dt:" + parameter + "\\$", tt.driver.utils.getValue("dt:"+parameter));
			}

			System.out.println(" value = " + tempValue);
			tt.driver.log.info(" value = " + tempValue);
		}

		System.out.println(" value = " + tempValue);
		tt.driver.log.info(" value = " + tempValue);
		return tempValue;
	}

	public ResultDetails restGet(WebDriver webdriver, String fieldText,
			String value, String fieldName) {
		String CookieInfo = "";
		String StatusCode = "";
		String responseBody = "";
		String storeAttribute = "";
		String nullValues = "";
		String notNullValues= "";
		String nN[] = null;
		String nV[] = null;
		String responseRaw="";
		String responseHeaders="";

		try
		{

			DefaultHttpClient httpClient = new DefaultHttpClient();
			//Replacing the dynamic and static values in fieldText if any.
			fieldText=getStringValue(fieldText);

			String[] TempField = fieldText.split("\\|\\|");
			String [] TempValue = value.split("\\|\\|");

			//Differentiating the FieldText values
			String ContentType = TempField[0];
			String URL = TempField[1];

			if(TempField.length>=3)
				CookieInfo = TempField[2];


			//Differentiating the DataValues
			if(TempValue.length>=1){

				for(int index=0;index<TempValue.length;index++){
					System.out.println("The Field value at index--" +index+"is---" +TempValue[index]);
					if(TempValue[index].startsWith("<code")||TempValue[index].startsWith("<statuscode")){
						StatusCode=TempValue[index].substring(TempValue[index].indexOf("::")+2, TempValue[index].length());
					}
					if(TempValue[index].startsWith("<NULL")){
						nullValues=TempValue[index].substring(TempValue[index].indexOf("::")+2, TempValue[index].length());
					}
					if(TempValue[index].startsWith("<!NULL")){
						notNullValues=TempValue[index].substring(TempValue[index].indexOf("::")+2, TempValue[index].length());
					}
					if(TempValue[index].startsWith("<store")){
						storeAttribute=TempValue[index].substring(TempValue[index].indexOf("::")+2, TempValue[index].length());
					}					
					if(TempValue[index].startsWith("<responseBody")){
						responseBody=TempValue[index].substring(TempValue[index].indexOf("::")+2, TempValue[index].length());
					}
					if(TempValue[index].startsWith("<response>"))
					{
						responseRaw=TempValue[index].substring(TempValue[index].indexOf("::")+2, TempValue[index].length());
					}
					if(TempValue[index].startsWith("<responseHeaders"))
					{
						responseHeaders=TempValue[index].substring(TempValue[index].indexOf("::")+2, TempValue[index].length());
					}
				}
			}

			//Hitting the End Point
			HttpGet getRequest = new HttpGet(URL);

			//Storing the Cookie values if the flag is set
			if(CookieInfo.length()>0&&CookieInfo.equalsIgnoreCase("ALL") ){
				Set<Cookie> allCookies = webdriver.manage().getCookies();
				System.out.println(allCookies);

				Iterator<Cookie> iterator = allCookies.iterator();
				String cookies="";
				while(iterator.hasNext())
				{

					Cookie cookie1 =iterator.next();
					cookies=cookies+cookie1.getName()+"="+cookie1.getValue()+";";

				}
				getRequest.addHeader("Cookie",cookies);
			}
			
			
			if(CookieInfo.length()>0&&CookieInfo.startsWith("<Header"))
			{
				String headers=CookieInfo.substring(CookieInfo.indexOf("::")+2, CookieInfo.length());
				String headerKey=headers.split("=")[0];
				String headerValue=headers.split("=")[1];				
				headerValue=tt.driver.utils.getValue(headerValue);
				getRequest.addHeader(headerKey,"Bearer "+headerValue);			
			}

			tt.driver.log.info("EndPoint URL:"+URL);

			//Getting the response			
			HttpResponse response = httpClient.execute(getRequest);
			HttpEntity responseEntity = response.getEntity();
			String body_respo = new String();
			body_respo = EntityUtils.toString(responseEntity);
			System.out.println("=============================================================================================");
			System.out.println("Response:: "+response);
			System.out.println("=============================================================================================");
			tt.driver.log.info("Response for the Request::"+response);
			System.out.println("=============================================================================================");
			System.out.println("Response Body:: "+body_respo);
			System.out.println("=============================================================================================");
			tt.driver.log.info("Response Body for the Request::"+body_respo);


			//Creating a JSON Object for the response body
			JSONObject jsonObj = null;
			try {
				
				jsonObj = new JSONObject(body_respo);
			} catch (JSONException e1) {
				//e1.printStackTrace();
			}
			
			try {
				JSONArray jsonArray = null;
				jsonArray = new JSONArray(body_respo);
				jsonObj= jsonArray.getJSONObject(0);
			} catch (JSONException e1) {
				//e1.printStackTrace();
			}
			//Validating the status code
			System.out.println("-----------------------------------");
			System.out.println("Status Code from Server:"+ response.getStatusLine().getStatusCode());
			System.out.println("Expected Status Code:"+StatusCode);

			if (! Integer.toString(response.getStatusLine().getStatusCode()).equals(StatusCode.trim()) )
			{
				driver.log.error("Failed : HTTP error code : "+ response.getStatusLine().getStatusCode());	
				resultDetails.setFlag(false);
				resultDetails.setErrorMessage("Status code not matched Expected:"+StatusCode+" Actual:"+response.getStatusLine().getStatusCode());
				return resultDetails;
			}

			if(response.getStatusLine().getStatusCode()==Integer.parseInt(StatusCode)){
				System.out.println(StatusCode + " OK ");
				System.out.println("Valid HTTP Status Code");
				System.out.println("-----------------------------------");
				System.out.println("Verified");
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++");
				tt.driver.log.info("-----------------------------------");
				tt.driver.log.info("Status Code from Server:"+ response.getStatusLine().getStatusCode());
				tt.driver.log.info("Expected Status Code:"+StatusCode);
				tt.driver.log.info(StatusCode + " OK ");
				tt.driver.log.info("Valid HTTP Status Code");
				tt.driver.log.info("-----------------------------------");
				tt.driver.log.info("Verified");
				tt.driver.log.info("+++++++++++++++++++++++++++++++++++++++++++++");
				resultDetails.setFlag(true);

			}

			//Validating NULL values
			if(nullValues.length()>=1){
				nV=nullValues.split(",");
				for(int v=0;v<nV.length;v++)
				{
					try {
						String value1=getJsonValue(jsonObj,nV[v]);
						System.out.println("The value of--" +nV[v]+"---is---"+value1);
						tt.driver.log.info("--------------------------------------------------------------------------------------------------");
						tt.driver.log.info("The object whcih is to be verified as  equal to null is---" +nV[v]+"--and the value is----" +value1);
						tt.driver.log.info("--------------------------------------------------------------------------------------------------");
						if(!(value1.length()>=1))
						{
							tt.driver.log.info("The " +nV[v]+" is equal to null");
							resultDetails.setFlag(true);	
						}
						else{
							tt.driver.log.info("The " +nV[v]+" is not equal to null");
							resultDetails.setFlag(false);	
						}
					}

					catch (Exception e) {
						// TODO Auto-generated catch block
						resultDetails.getErrorMessage();
						resultDetails.setErrorMessage("Error occured while retrieving the JSON value from response");
						tt.driver.log.error("Error occured while retrieving the JSON value from response");
					}
				}
			}

			//Validating !Null values
			if(notNullValues.length()>=1){
				nN=notNullValues.split(",");
				for(int v=0;v<nN.length;v++)
				{
					try {
						String value1=getJsonValue(jsonObj,nN[v]);
						System.out.println("The value of--" +nN[v]+"---is---"+value1);
						tt.driver.log.info("-----------------------------------------------------------------------------------------------------");
						tt.driver.log.info("The object whcih is to be verified as not equal to null is---" +nN[v]+"--and the value is----" +value1);
						tt.driver.log.info("-----------------------------------------------------------------------------------------------------");
						if(value1!=null)
						{
							tt.driver.log.info("The " +nN[v]+" is not equal to null");
							resultDetails.setFlag(true);	
						}
						else{
							tt.driver.log.info("The " +nN[v]+" is equal to null");
							resultDetails.setFlag(false);	
						}
					}

					catch (Exception e) {
						// TODO Auto-generated catch block
						resultDetails.getErrorMessage();
						resultDetails.setErrorMessage("Error occured while retrieving the JSON value from response");
						tt.driver.log.error("Error occured while retrieving the JSON value from response");
					}

				}
			}
			System.out.println("the length of storevalue is---" +storeAttribute.length());

			//Storing the JSONObject Values
			if(storeAttribute.length()>=1)
			{
				String st[]=storeAttribute.split(",");
				for(int s=0;s<st.length;s++)
				{
					String key=	st[s].split("=")[0];
					String vl=st[s].split("=")[1];
					System.out.println("The key  is-----------------" +key);
					tt.driver.log.info("The key  is-----------------" +key);
					System.out.println("The Object value to be stored is----" +vl);
					tt.driver.log.info("The Object value to be stored is----" +vl);
					try {
						String valueToBeStored=getJsonValue(jsonObj,vl);
						System.out.println("the value to be stored in the Hash Map is-----" +valueToBeStored);
						tt.driver.log.info("the value to be stored in the Hash Map is-----" +valueToBeStored);
						tt.driver.hMap.put(key, valueToBeStored);

					}

					catch (Exception e) {
						resultDetails.getErrorMessage();
						resultDetails.setErrorMessage("Error occured while retrieving the JSON value from response");
						tt.driver.log.error("Error occured while retrieving the JSON value from response");
					}
				}
			}

			//Verifying that the given text is present in the response body

			if(responseBody.length()>=1 && body_respo.length()>=1)
			{
				String responseValues[]=responseBody.split(",");
				for(int s=0;s<responseValues.length;s++)
				{
					String actual=	responseValues[s].split("=")[0];
					String expected=responseValues[s].split("=")[1];

					try {
						if(actual.contains("json"))
						{
							actual=getJsonValue(jsonObj,actual);
						}
						else{
							actual=tt.driver.utils.getValue(actual);	
						}

						System.out.println("The Actual value  is-----------------" +actual);
						tt.driver.log.info("The Actual value  is-----------------" +actual);
						System.out.println("The Expected value is----" +expected);
						tt.driver.log.info("The Expected value is----" +expected);

						if(actual.equalsIgnoreCase(expected)||body_respo.contains(expected))
						{
							tt.driver.log.info("The actual value is equal to the expected value");
							resultDetails.setFlag(true);		
						}
						else{
							tt.driver.log.info("The actual value is not equal to the expected value");
							resultDetails.setFlag(false);
						}

					}

					catch (Exception e) {
						resultDetails.getErrorMessage();
						resultDetails.setErrorMessage("Error occured while retrieving the JSON value from response");
						tt.driver.log.error("Error occured while retrieving the JSON value from response");
					}
				}

			}

			//Verifying that the given text is present in the Raw response

			if(responseRaw.length()>=1)
			{
				String responseValues[]=responseRaw.split(",");
				String rawResponse=response.toString();
				for(int s=0;s<responseValues.length;s++)
				{
					if(rawResponse.trim().contains(responseValues[s].trim()))
					{
						System.out.println("Verified");
						System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
						tt.driver.log.info("Verified");
						tt.driver.log.info("+++++++++++++++++++++++++++++++++++++++++++++");
						System.out.println("The value " +responseValues[s]+ " is present in the Raw Response" );
						tt.driver.log.info("The value " +responseValues[s]+ " is present in the Raw Response" );
						resultDetails.setFlag(true);
					}

					else{
						System.out.println("The value " +responseValues[s]+ " is not present in the Raw Response" );
						tt.driver.log.info("The value " +responseValues[s]+ " is not present in the Raw Response" );
						resultDetails.setFlag(false);
					}

				}
			}

			//Verifying that the given text is present in the Headers
			if(responseHeaders.length()>=1)
			{
				String responseValues[]=responseHeaders.split(",");

				for(int s=0;s<responseValues.length;s++)
				{

					String headerValue=	responseValues[s].split("=")[0];
					String expected=responseValues[s].split("=")[1];	
					Header[] headers = response.getHeaders(headerValue);
					for(int h=0;h<headers.length;h++){
						String actual=headers[h].getValue();
						if(actual.trim().equalsIgnoreCase(expected.trim()))
						{
							System.out.println("Verified");
							System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
							tt.driver.log.info("Verified");
							tt.driver.log.info("+++++++++++++++++++++++++++++++++++++++++++++");
							System.out.println("The value " +responseValues[s]+ " is present in the Headers" );
							tt.driver.log.info("The value " +responseValues[s]+ " is present in the Headers" );
							resultDetails.setFlag(true);
						}

						else{
							System.out.println("The value " +responseValues[s]+ " is not present in the Headers" );
							tt.driver.log.info("The value " +responseValues[s]+ " is not present in the Headers" );
							resultDetails.setFlag(false);
						}
					}

				}
			}
		}
		catch (MalformedURLException e) {
			e.printStackTrace();

			resultDetails.getErrorMessage();
			resultDetails.setErrorMessage("response not matched with the expected value");
			tt.driver.log.error("response not matched with the expected value");

		} catch (IOException e) {
			e.printStackTrace();
			resultDetails.getErrorMessage();
			resultDetails.setErrorMessage("response not matched with the expected value");
			tt.driver.log.error("response not matched with the expected value");

		}
		return resultDetails;
	}







	public ResultDetails restPut(WebDriver webdriver, String fieldText,String value, String fieldName) {
		// TODO Auto-generated method stub
		String CookieInfo = "";
		String StatusCode = "";
		String responseBody = "";
		String storeAttribute = "";
		String nullValues = "";
		String notNullValues= "";
		String nN[] = null;
		String nV[] = null;
		String Message = "";
		String PutResponse="";
		String responseRaw="";
		String responseHeaders="";

		try
		{

			DefaultHttpClient httpClient = new DefaultHttpClient();

			//Replacing the Dynamic and static properties in the Fieldtext
			fieldText=getStringValue(fieldText);

			String[] TempField = fieldText.split("\\|\\|");
			String [] TempValue = value.split("\\|\\|");

			//Differentiating the FieldText values
			String ContentType = TempField[0];
			String URL = TempField[1];

			if(TempField.length==3)
				CookieInfo = TempField[2];

			if(TempField.length>3){
				Message= TempField[2];
				CookieInfo=TempField[3];
			}
			if(Message.contains(".json"))
			{
				try{
					Message=js.getJsonInput(Message);
					System.out.println(Message);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

			Message=getStringValue(Message);
			System.out.println("Message::" +Message);
			tt.driver.log.info("Message::" +Message);

			//Differentiating the DataValues
			if(TempValue.length>=1){

				for(int index=0;index<TempValue.length;index++){
					System.out.println("The Field value at index--" +index+"is---" +TempValue[index]);
					if(TempValue[index].startsWith("<code")||TempValue[index].startsWith("<statuscode")){
						StatusCode=TempValue[index].substring(TempValue[index].indexOf("::")+2, TempValue[index].length());
					}
					if(TempValue[index].startsWith("<NULL")){
						nullValues=TempValue[index].substring(TempValue[index].indexOf("::")+2, TempValue[index].length());
					}
					if(TempValue[index].startsWith("<!NULL")){
						notNullValues=TempValue[index].substring(TempValue[index].indexOf("::")+2, TempValue[index].length());
					}
					if(TempValue[index].startsWith("<store")){
						storeAttribute=TempValue[index].substring(TempValue[index].indexOf("::")+2, TempValue[index].length());
					}					
					if(TempValue[index].startsWith("<responseBody>")){
						responseBody=TempValue[index].substring(TempValue[index].indexOf("::")+2, TempValue[index].length());
					}
					if(TempValue[index].startsWith("<response>"))
					{
						responseRaw=TempValue[index].substring(TempValue[index].indexOf("::")+2, TempValue[index].length());
					}
					if(TempValue[index].startsWith("<responseHeaders"))
					{
						responseHeaders=TempValue[index].substring(TempValue[index].indexOf("::")+2, TempValue[index].length());
					}

				}
			}


			//Hitting the End point
			tt.driver.log.info("EndPoint URL:"+URL);
			HttpPut putRequest = new HttpPut(URL);

			//Putting the input message
			StringEntity input = new StringEntity(Message);
			input.setContentType(ContentType);
			putRequest.setEntity(input);

			//Storing the cookie value if the flag is set
			if(CookieInfo.equalsIgnoreCase("ALL") ){
				Set<Cookie> allCookies = webdriver.manage().getCookies();
				//System.out.println(allCookies);

				Iterator<Cookie> iterator = allCookies.iterator();
				String cookies="";
				while(iterator.hasNext())
				{
					
					Cookie cookie1 =iterator.next();
					cookies=cookies+cookie1.getName()+"="+cookie1.getValue()+";";

				}
				putRequest.addHeader("Cookie",cookies);
			}

			//Retrieving the response
			HttpResponse response = httpClient.execute(putRequest);
			HttpEntity responseEntity = response.getEntity();
			String body_respo = new String();
			body_respo = EntityUtils.toString(responseEntity);

			//Validating the status code

			if (StatusCode.length()>1&&! Integer.toString(response.getStatusLine().getStatusCode()).equals(StatusCode.trim()) )
			{
				driver.log.error("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());	
				resultDetails.setFlag(false);
				resultDetails.setErrorMessage("Status code not matched Expected:"+StatusCode+" Actual:"+response.getStatusLine().getStatusCode());
				return resultDetails;
			}

			System.out.println("=============================================================================================");
			System.out.println("Response:: "+response);
			System.out.println("=============================================================================================");
			tt.driver.log.info("Response for the Request::"+response);
			System.out.println("=============================================================================================");
			System.out.println("Response Body:: "+body_respo);
			System.out.println("=============================================================================================");
			tt.driver.log.info("Response Body for the Request::"+body_respo);


			PutResponse=body_respo;
			int statuscode = response.getStatusLine().getStatusCode();

			String ResponseStatus = Integer.toString(statuscode);
			if(ResponseStatus.trim().equalsIgnoreCase(StatusCode.trim())||ResponseStatus.trim().toLowerCase().contains(StatusCode.trim().toLowerCase()))
			{       System.out.println("-----------------------------------");
			System.out.println("Status Code from Server:"+ response.getStatusLine().getStatusCode());
			System.out.println("Expected Status Code:"+StatusCode);
			System.out.println(StatusCode + " OK ");
			System.out.println("Valid HTTP Status Code");
			System.out.println("-----------------------------------");
			tt.driver.log.info("Status Code from Server:"+ response.getStatusLine().getStatusCode());
			tt.driver.log.info("Expected Status Code:"+StatusCode);
			tt.driver.log.info(StatusCode + " OK ");
			tt.driver.log.info("Valid HTTP Status Code");
			tt.driver.log.info("-----------------------------------");
			resultDetails.setFlag(true);
			}


			//Creating an JSON object for the response body
			JSONObject jsonObj = null;
			try {
				jsonObj = new JSONObject(body_respo);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				//e1.getMessage();
			}

			try {
				JSONArray jsonArray = null;
				jsonArray = new JSONArray(body_respo);
				jsonObj= jsonArray.getJSONObject(0);
			} catch (JSONException e1) {
				//e1.printStackTrace();
			}


			//Validating NULL values
			if(nullValues.length()>=1){
				nV=nullValues.split(",");
				for(int v=0;v<nV.length;v++)
				{
					try {
						String value1=getJsonValue(jsonObj,nV[v]);
						System.out.println("The value of--" +nV[v]+"---is---"+value1);
						tt.driver.log.info("--------------------------------------------------------------------------------------------------");
						tt.driver.log.info("The object whcih is to be verified as  equal to null is---" +nV[v]+"--and the value is----" +value1);
						tt.driver.log.info("--------------------------------------------------------------------------------------------------");
						if(!(value1.length()>=1))
						{
							tt.driver.log.info("The " +nV[v]+" is equal to null");
							resultDetails.setFlag(true);	
						}
						else{
							tt.driver.log.info("The " +nV[v]+" is not equal to null");
							resultDetails.setFlag(false);	
						}
					}

					catch (Exception e) {
						// TODO Auto-generated catch block
						resultDetails.getErrorMessage();
						resultDetails.setErrorMessage("Error occured while retrieving the JSON value from response");
						tt.driver.log.error("Error occured while retrieving the JSON value from response");
					}
				}
			}

			//Validating !Null values
			if(notNullValues.length()>=1){
				nN=notNullValues.split(",");
				for(int v=0;v<nN.length;v++)
				{
					try {
						String value1=getJsonValue(jsonObj,nN[v]);
						System.out.println("The value of--" +nN[v]+"---is---"+value1);
						tt.driver.log.info("-----------------------------------------------------------------------------------------------------");
						tt.driver.log.info("The object whcih is to be verified as not equal to null is---" +nN[v]+"--and the value is----" +value1);
						tt.driver.log.info("-----------------------------------------------------------------------------------------------------");
						if(value1!=null)
						{
							tt.driver.log.info("The " +nN[v]+" is not equal to null");
							resultDetails.setFlag(true);	
						}
						else{
							tt.driver.log.info("The " +nN[v]+" is equal to null");
							resultDetails.setFlag(false);	
						}
					}

					catch (Exception e) {
						// TODO Auto-generated catch block
						resultDetails.getErrorMessage();
						resultDetails.setErrorMessage("Error occured while retrieving the JSON value from response");
						tt.driver.log.error("Error occured while retrieving the JSON value from response");
					}

				}
			}
			System.out.println("the length of storevalue is---" +storeAttribute.length());

			//Storing the JSONObject Values
			if(storeAttribute.length()>=1)
			{
				String st[]=storeAttribute.split(",");
				for(int s=0;s<st.length;s++)
				{
					String key=	st[s].split("=")[0];
					String vl=st[s].split("=")[1];
					System.out.println("The key  is-----------------" +key);
					tt.driver.log.info("The key  is-----------------" +key);
					System.out.println("The Object value to be stored is----" +vl);
					tt.driver.log.info("The Object value to be stored is----" +vl);
					try {
						String valueToBeStored=getJsonValue(jsonObj,vl);
						System.out.println("the value to be stored in the Hash Map is-----" +valueToBeStored);
						tt.driver.log.info("the value to be stored in the Hash Map is-----" +valueToBeStored);
						tt.driver.hMap.put(key, valueToBeStored);

					}

					catch (Exception e) {
						resultDetails.getErrorMessage();
						resultDetails.setErrorMessage("Error occured while retrieving the JSON value from response");
						tt.driver.log.error("Error occured while retrieving the JSON value from response");
					}
				}
			}


			//Verifying that the given text is present in the response body

			if(responseBody.length()>=1 && body_respo.length()>=1)
			{
				String responseValues[]=responseBody.split(",");
				for(int s=0;s<responseValues.length;s++)
				{
					String actual=	responseValues[s].split("=")[0];
					String expected=responseValues[s].split("=")[1];

					try {
						if(actual.contains("json"))
						{
							actual=getJsonValue(jsonObj,actual);
						}
						else{
							actual=tt.driver.utils.getValue(actual);	
						}

						System.out.println("The Actual value  is-----------------" +actual);
						tt.driver.log.info("The Actual value  is-----------------" +actual);
						System.out.println("The Expected value is----" +expected);
						tt.driver.log.info("The Expected value is----" +expected);

						if(actual.equalsIgnoreCase(expected)||body_respo.contains(expected))
						{
							tt.driver.log.info("The actual value is equal to the expected value");
							resultDetails.setFlag(true);		
						}
						else{
							tt.driver.log.info("The actual value is not equal to the expected value");
							resultDetails.setFlag(false);
						}

					}

					catch (Exception e) {
						resultDetails.getErrorMessage();
						resultDetails.setErrorMessage("Error occured while retrieving the JSON value from response");
						tt.driver.log.error("Error occured while retrieving the JSON value from response");
					}
				}

			}

			if(responseRaw.length()>=1)
			{
				String responseValues[]=responseRaw.split(",");
				String rawResponse=response.toString();
				for(int s=0;s<responseValues.length;s++)
				{
					if(rawResponse.trim().contains(responseValues[s].trim()))
					{
						System.out.println("Verified");
						System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
						tt.driver.log.info("Verified");
						tt.driver.log.info("+++++++++++++++++++++++++++++++++++++++++++++");
						System.out.println("The value " +responseValues[s]+ " is present in the Raw Response" );
						tt.driver.log.info("The value " +responseValues[s]+ " is present in the Raw Response" );
						resultDetails.setFlag(true);
					}

					else{
						System.out.println("The value " +responseValues[s]+ " is not present in the Raw Response" );
						tt.driver.log.info("The value " +responseValues[s]+ " is not present in the Raw Response" );
						resultDetails.setFlag(false);
					}

				}
			}
			
			if(responseHeaders.length()>=1)
			{
				String responseValues[]=responseHeaders.split(",");

				for(int s=0;s<responseValues.length;s++)
				{

					String headerValue=	responseValues[s].split("=")[0];
					String expected=responseValues[s].split("=")[1];	
					Header[] headers = response.getHeaders(headerValue);
					for(int h=0;h<headers.length;h++){
						String actual=headers[h].getValue();
						if(actual.contains(expected))
						{
							System.out.println("Verified");
							System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
							tt.driver.log.info("Verified");
							tt.driver.log.info("+++++++++++++++++++++++++++++++++++++++++++++");
							System.out.println("The value " +responseValues[s]+ " is present in the Headers" );
							tt.driver.log.info("The value " +responseValues[s]+ " is present in the Headers" );
							resultDetails.setFlag(true);
						}

						else{
							System.out.println("The value " +responseValues[s]+ " is not present in the Headers" );
							tt.driver.log.info("The value " +responseValues[s]+ " is not present in the Headers" );
							resultDetails.setFlag(false);
						}
					}

				}
			}


			httpClient.getConnectionManager().shutdown();
		}

		catch (MalformedURLException e) {

			resultDetails.getErrorMessage();
			resultDetails.setErrorMessage("response not matched Expected:"+responseBody+" Actual:"+PutResponse);
			driver.log.error("response not matched Expected:"+responseBody+" Actual:"+PutResponse);


		} catch (IOException e) {

			e.printStackTrace();
			resultDetails.getErrorMessage();
			resultDetails.setErrorMessage("response not matched Expected:"+responseBody+" Actual:"+PutResponse);
			driver.log.error("response not matched Expected:"+responseBody+" Actual:"+PutResponse);
		}
		return resultDetails;

	}





	public ResultDetails restDelete(WebDriver webdriver, String fieldText,
			String value, String fieldName) {

		System.out.println(fieldText.split("\\|\\|")[0]);
		fieldText=getStringValue(fieldText);
		String[] TempField = fieldText.split("\\|\\|");
		String ContentType = TempField[0];
		String URL = TempField[1];
		String Message = TempField[2];
		String CookieInfo = TempField[3];
		String [] TempValue = value.split("\\|\\|");
		String StatusCode = "";
		String responseBody = "";
		String responseRaw="";
		String responseHeaders="";
		String DeleteResponse="";

		try
		{
			DefaultHttpClient httpClient = new DefaultHttpClient();

			//Differentiating the DataValues
			if(TempValue.length>=1){

				for(int index=0;index<TempValue.length;index++){
					System.out.println("The Field value at index--" +index+"is---" +TempValue[index]);
					if(TempValue[index].startsWith("<code")||TempValue[index].startsWith("<statuscode")){
						StatusCode=TempValue[index].substring(TempValue[index].indexOf("::")+2, TempValue[index].length());
					}
								
					if(TempValue[index].startsWith("<responseBody")){
						responseBody=TempValue[index].substring(TempValue[index].indexOf("::")+2, TempValue[index].length());
					}
					if(TempValue[index].startsWith("<response>"))
					{
						responseRaw=TempValue[index].substring(TempValue[index].indexOf("::")+2, TempValue[index].length());
					}
					if(TempValue[index].startsWith("<responseHeaders"))
					{
						responseHeaders=TempValue[index].substring(TempValue[index].indexOf("::")+2, TempValue[index].length());
					}
				}
			}

			Set<Cookie> allCookies = webdriver.manage().getCookies();
			//System.out.println(allCookies);

			Iterator<Cookie> iterator = allCookies.iterator();
			HttpDelete deleteRequest = new HttpDelete(URL);
			StringEntity input = new StringEntity(Message);
			input.setContentType(ContentType);

			tt.driver.log.info("EndPoint URL:"+URL);
			HttpResponse response = httpClient.execute(deleteRequest);
			if (! Integer.toString(response.getStatusLine().getStatusCode()).equals(StatusCode.trim()) )
			{
				driver.log.error("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());	
				resultDetails.setFlag(false);
				resultDetails.setErrorMessage("Status code not matched Expected:"+StatusCode+" Actual:"+response.getStatusLine().getStatusCode());
				return resultDetails;
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			String output;

			//System.out.println("Output from Server:");
			tt.driver.log.info("Output from Server:");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				tt.driver.log.info(output);
				DeleteResponse=DeleteResponse+output;
				int statuscode = response.getStatusLine().getStatusCode();

				String ResponseStatus = Integer.toString(statuscode);

				if(ResponseStatus.trim().equalsIgnoreCase(StatusCode.trim())||ResponseStatus.trim().toLowerCase().contains(StatusCode.trim().toLowerCase()))
				{
					System.out.println("-----------------------------------");
					System.out.println("Status Code from Server:"+ response.getStatusLine().getStatusCode());
					System.out.println("Expected Status Code:"+StatusCode);
					System.out.println(StatusCode + " OK ");
					System.out.println("Valid HTTP Status Code");
					System.out.println("-----------------------------------");
					tt.driver.log.info("Status Code from Server:"+ response.getStatusLine().getStatusCode());
					tt.driver.log.info("Expected Status Code:"+StatusCode);
					tt.driver.log.info(StatusCode + " OK ");
					tt.driver.log.info("Valid HTTP Status Code");
					tt.driver.log.info("-----------------------------------");
					resultDetails.setFlag(true);
				}


			}
			
			//Verifying that the given text is present in the response body
			
			if(responseRaw.length()>=1)
			{
				String responseValues[]=responseRaw.split(",");
				String rawResponse=response.toString();
				for(int s=0;s<responseValues.length;s++)
				{
					if(rawResponse.trim().contains(responseValues[s].trim()))
					{
						System.out.println("Verified");
						System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
						tt.driver.log.info("Verified");
						tt.driver.log.info("+++++++++++++++++++++++++++++++++++++++++++++");
						System.out.println("The value " +responseValues[s]+ " is present in the Raw Response" );
						tt.driver.log.info("The value " +responseValues[s]+ " is present in the Raw Response" );
						resultDetails.setFlag(true);
					}

					else{
						System.out.println("The value " +responseValues[s]+ " is not present in the Raw Response" );
						tt.driver.log.info("The value " +responseValues[s]+ " is not present in the Raw Response" );
						resultDetails.setFlag(false);
					}

				}
			}
			
			if(responseHeaders.length()>=1)
			{
				String responseValues[]=responseHeaders.split(",");

				for(int s=0;s<responseValues.length;s++)
				{

					String headerValue=	responseValues[s].split("=")[0];
					String expected=responseValues[s].split("=")[1];	
					Header[] headers = response.getHeaders(headerValue);
					for(int h=0;h<headers.length;h++){
						String actual=headers[h].getValue();
						if(actual.contains(expected))
						{
							System.out.println("Verified");
							System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
							tt.driver.log.info("Verified");
							tt.driver.log.info("+++++++++++++++++++++++++++++++++++++++++++++");
							System.out.println("The value " +responseValues[s]+ " is present in the Headers" );
							tt.driver.log.info("The value " +responseValues[s]+ " is present in the Headers" );
							resultDetails.setFlag(true);
						}

						else{
							System.out.println("The value " +responseValues[s]+ " is not present in the Headers" );
							tt.driver.log.info("The value " +responseValues[s]+ " is not present in the Headers" );
							resultDetails.setFlag(false);
						}
					}

				}
			}


			httpClient.getConnectionManager().shutdown();


		}

		catch (MalformedURLException e) {

			resultDetails.getErrorMessage();
			resultDetails.setErrorMessage("response not matched Expected:"+responseBody+" Actual:"+DeleteResponse);
			driver.log.error("response not matched Expected:"+responseBody+" Actual:"+DeleteResponse);

		} catch (IOException e) {

			resultDetails.getErrorMessage();
			resultDetails.setErrorMessage("response not matched Expected:"+responseBody+" Actual:"+DeleteResponse);
			driver.log.error("response not matched Expected:"+responseBody+" Actual:"+DeleteResponse);
		}
		return resultDetails;

	}



	public ResultDetails restPost(WebDriver webdriver, String fieldText, String value,
			String fieldName) {

		String CookieInfo = "";
		String StatusCode = "";
		String responseBody = "";
		String storeAttribute = "";
		String nullValues = "";
		String notNullValues= "";
		String nN[] = null;
		String nV[] = null;
		String PostResponse="";
		String Message="";
		String body_respo="";
		String responseRaw="";
		String responseHeaders="";
		try
		{
			DefaultHttpClient httpClient = new DefaultHttpClient();

			//Replacing the dynamic and static values in fieldText if any.
			fieldText=getStringValue(fieldText);

			String[] TempField = fieldText.split("\\|\\|");
			String [] TempValue = value.split("\\|\\|");

			//Differentiating the FieldText values
			String ContentType = TempField[0];
			String URL = TempField[1];

			if(TempField.length==3)
				CookieInfo = TempField[2];

			if(TempField.length>4){
				CookieInfo= TempField[3];
			}

			if(TempField.length>3){
				Message= TempField[2];
			}
			if(Message.contains(".json"))
			{
				try{
					Message=js.getJsonInput(Message);
					System.out.println(Message);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

			//Replacing the dynamic and static values in Message if any.
			Message=getStringValue(Message);
			System.out.println("Message  is" +Message);

			//Differentiating the DataValues
			if(TempValue.length>=1){

				for(int index=0;index<TempValue.length;index++){
					System.out.println("The Field value at index--" +index+"is---" +TempValue[index]);
					if(TempValue[index].startsWith("<code")||TempValue[index].startsWith("<statuscode")){
						StatusCode=TempValue[index].substring(TempValue[index].indexOf("::")+2, TempValue[index].length());
					}
					if(TempValue[index].startsWith("<NULL")){
						nullValues=TempValue[index].substring(TempValue[index].indexOf("::")+2, TempValue[index].length());
					}
					if(TempValue[index].startsWith("<!NULL")){
						notNullValues=TempValue[index].substring(TempValue[index].indexOf("::")+2, TempValue[index].length());
					}
					if(TempValue[index].startsWith("<store")){
						storeAttribute=TempValue[index].substring(TempValue[index].indexOf("::")+2, TempValue[index].length());
					}					
					if(TempValue[index].startsWith("<responseBody")){
						responseBody=TempValue[index].substring(TempValue[index].indexOf("::")+2, TempValue[index].length());
					}
					if(TempValue[index].startsWith("<response>"))
					{
						responseRaw=TempValue[index].substring(TempValue[index].indexOf("::")+2, TempValue[index].length());
					}
					if(TempValue[index].startsWith("<responseHeaders"))
					{
						responseHeaders=TempValue[index].substring(TempValue[index].indexOf("::")+2, TempValue[index].length());
					}

				}
			}




			//Hitting the End Point
			System.out.println("End Point Url is " +URL);
			tt.driver.log.info("EndPoint URL:"+URL);
			HttpPost postRequest = new HttpPost(URL);

			//Storing the Cookie values if the flag is set
			if(CookieInfo.length()>0&&CookieInfo.equalsIgnoreCase("ALL") ){
				Set<Cookie> allCookies = webdriver.manage().getCookies();
				System.out.println(allCookies);

				Iterator<Cookie> iterator = allCookies.iterator();
				String cookies="";
				while(iterator.hasNext())
				{

					Cookie cookie1 =iterator.next();
					cookies=cookies+cookie1.getName()+"="+cookie1.getValue()+";";

				}
				postRequest.addHeader("Cookie",cookies);
			}
			
			
			
			//Posting the input message
			StringEntity input = new StringEntity(Message);
			input.setContentType(ContentType);
			postRequest.setEntity(input);
			//Getting the response
			HttpResponse response = httpClient.execute(postRequest);
			HttpEntity responseEntity = response.getEntity();
			body_respo = EntityUtils.toString(responseEntity);
			System.out.println("=============================================================================================");
			System.out.println("Response:: "+response);
			System.out.println("=============================================================================================");
			tt.driver.log.info("Response for the Request::"+response);
			System.out.println("=============================================================================================");
			System.out.println("Response Body:: "+body_respo);
			System.out.println("=============================================================================================");
			tt.driver.log.info("Response Body for the Request::"+body_respo);


			//Creating a JSON Object for the response body
			JSONObject jsonObj = null;
			try {
				jsonObj = new JSONObject(body_respo);
			} catch (JSONException e1) {
				//e1.printStackTrace();
			}
			try {
				JSONArray jsonArray = null;
				jsonArray = new JSONArray(body_respo);
				jsonObj= jsonArray.getJSONObject(0);
			} catch (JSONException e1) {
				//e1.printStackTrace();
			}
			//Validating the status code

			int statuscode = response.getStatusLine().getStatusCode();
			String ResponseStatus = Integer.toString(statuscode);
			System.out.println("-----------------------------------");
			System.out.println("Status Code from Server:"+ response.getStatusLine().getStatusCode());
			System.out.println("Expected Status Code:"+StatusCode);

			if (StatusCode.length()>1&&! Integer.toString(response.getStatusLine().getStatusCode()).equals(StatusCode.trim()) )
			{
				driver.log.error("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());	
				resultDetails.setFlag(false);
				resultDetails.setErrorMessage("Status code not matched Expected:"+StatusCode+" Actual:"+response.getStatusLine().getStatusCode());
				return resultDetails;
			}

			if(ResponseStatus.trim().equalsIgnoreCase(StatusCode.trim())||ResponseStatus.trim().toLowerCase().contains(StatusCode.trim().toLowerCase()))
			{

				System.out.println("-----------------------------------");
				System.out.println("Status Code from Server:"+ response.getStatusLine().getStatusCode());
				System.out.println("Expected Status Code:"+StatusCode);
				System.out.println(StatusCode + " OK ");
				System.out.println("Valid HTTP Status Code");
				System.out.println("-----------------------------------");
				tt.driver.log.info("Status Code from Server:"+ response.getStatusLine().getStatusCode());
				tt.driver.log.info("Expected Status Code:"+StatusCode);
				tt.driver.log.info(StatusCode + " OK ");
				tt.driver.log.info("Valid HTTP Status Code");
				tt.driver.log.info("-----------------------------------");
				resultDetails.setFlag(true);
			}


			if(body_respo.length()>=1){
				//Validating NULL values
				if(nullValues.length()>=1){
					nV=nullValues.split(",");
					for(int v=0;v<nV.length;v++)
					{
						try {
							String value1=getJsonValue(jsonObj,nV[v]);
							System.out.println("The value of--" +nV[v]+"---is---"+value1);
							tt.driver.log.info("--------------------------------------------------------------------------------------------------");
							tt.driver.log.info("The object whcih is to be verified as  equal to null is---" +nV[v]+"--and the value is----" +value1);
							tt.driver.log.info("--------------------------------------------------------------------------------------------------");
							if(!(value1.length()>=1))
							{
								tt.driver.log.info("The " +nV[v]+" is equal to null");
								resultDetails.setFlag(true);	
							}
							else{
								tt.driver.log.info("The " +nV[v]+" is not equal to null");
								resultDetails.setFlag(false);	
							}
						}

						catch (Exception e) {
							// TODO Auto-generated catch block
							resultDetails.getErrorMessage();
							resultDetails.setErrorMessage("Error occured while retrieving the JSON value from response");
							tt.driver.log.error("Error occured while retrieving the JSON value from response");
						}
					}
				}

				//Validating !Null values
				if(notNullValues.length()>=1){
					nN=notNullValues.split(",");
					for(int v=0;v<nN.length;v++)
					{
						try {
							String value1=getJsonValue(jsonObj,nN[v]);
							System.out.println("The value of--" +nN[v]+"---is---"+value1);
							tt.driver.log.info("-----------------------------------------------------------------------------------------------------");
							tt.driver.log.info("The object whcih is to be verified as not equal to null is---" +nN[v]+"--and the value is----" +value1);
							tt.driver.log.info("-----------------------------------------------------------------------------------------------------");
							if(value1!=null)
							{
								tt.driver.log.info("The " +nN[v]+" is not equal to null");
								resultDetails.setFlag(true);	
							}
							else{
								tt.driver.log.info("The " +nN[v]+" is equal to null");
								resultDetails.setFlag(false);	
							}
						}

						catch (Exception e) {
							// TODO Auto-generated catch block
							resultDetails.getErrorMessage();
							resultDetails.setErrorMessage("Error occured while retrieving the JSON value from response");
							tt.driver.log.error("Error occured while retrieving the JSON value from response");
						}

					}
				}
			}
			System.out.println("the length of storevalue is---" +storeAttribute.length());

			//Storing the JSONObject Values
			if(storeAttribute.length()>=1)
			{
				String st[]=storeAttribute.split(",");
				for(int s=0;s<st.length;s++)
				{
					String store_Key=	st[s].split("=")[0];
					String store_Value=st[s].split("=")[1];
					System.out.println("The key  is-----------------" +store_Key);
					tt.driver.log.info("The key  is-----------------" +store_Key);
					System.out.println("The Object value to be stored is----" +store_Value);
					tt.driver.log.info("The Object value to be stored is----" +store_Value);
					try {

						if(store_Value.startsWith("$Location")){
							Header[] headers = response.getHeaders("Location");
							String location=headers[0].getValue();
							if(store_Value.toLowerCase().contains("lastindex")){
								int index=0;
								String index_str=store_Value.toLowerCase().substring(store_Value.toLowerCase().lastIndexOf("+")+1);

								if(index_str.length()>=1){
									index=Integer.parseInt(index_str);
								}
								String split=(store_Value.substring(store_Value.toLowerCase().indexOf("(")+1,store_Value.toLowerCase().indexOf(")")));
								if(split.startsWith("\"")){
									split=split.substring(split.indexOf("\"")+1, split.lastIndexOf("\""));
								}
								location=location.substring(location.lastIndexOf(split)+index);

								tt.driver.hMap.put(store_Key, location);
								System.out.println("Stored Property"+store_Key+" :: "+location);
								tt.driver.log.debug("Stored Property"+store_Key+" :: "+location);

							}
						}
							else{
								String valueToBeStored=getJsonValue(jsonObj,store_Value);
								System.out.println("the value to be stored in the Hash Map is-----" +valueToBeStored);
								tt.driver.log.info("the value to be stored in the Hash Map is-----" +valueToBeStored);
								tt.driver.hMap.put(store_Key, valueToBeStored);
							}

					}

					catch (Exception e) {
						resultDetails.getErrorMessage();
						resultDetails.setErrorMessage("Error occured while retrieving the JSON value from response");
						tt.driver.log.error("Error occured while retrieving the JSON value from response");
					}
				}
			}

			//Verifying that the given text is present in the response body

			if(responseBody.length()>=1 && body_respo.length()>=1)
			{
				String responseValues[]=responseBody.split(",");
				for(int s=0;s<responseValues.length;s++)
				{
					String actual=	responseValues[s].split("=")[0];
					String expected=responseValues[s].split("=")[1];

					try {
						if(actual.contains("json"))
						{
							actual=getJsonValue(jsonObj,actual);
						}
						else{
							actual=tt.driver.utils.getValue(actual);	
						}

						System.out.println("The Actual value  is-----------------" +actual);
						tt.driver.log.info("The Actual value  is-----------------" +actual);
						System.out.println("The Expected value is----" +expected);
						tt.driver.log.info("The Expected value is----" +expected);

						if(actual.equalsIgnoreCase(expected)||body_respo.contains(expected))
						{
							tt.driver.log.info("The actual value is equal to the expected value");
							resultDetails.setFlag(true);		
						}
						else{
							tt.driver.log.info("The actual value is not equal to the expected value");
							resultDetails.setFlag(false);
						}

					}

					catch (Exception e) {
						resultDetails.getErrorMessage();
						resultDetails.setErrorMessage("Error occured while retrieving the JSON value from response");
						tt.driver.log.error("Error occured while retrieving the JSON value from response");
					}
				}

			}

			if(responseRaw.length()>=1)
			{
				String responseValues[]=responseRaw.split(",");
				String rawResponse=response.toString();
				for(int s=0;s<responseValues.length;s++)
				{
					if(rawResponse.trim().contains(responseValues[s].trim()))
					{
						System.out.println("Verified");
						System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
						tt.driver.log.info("Verified");
						tt.driver.log.info("+++++++++++++++++++++++++++++++++++++++++++++");
						System.out.println("The value " +responseValues[s]+ " is present in the Raw Response" );
						tt.driver.log.info("The value " +responseValues[s]+ " is present in the Raw Response" );
						resultDetails.setFlag(true);
					}

					else{
						System.out.println("The value " +responseValues[s]+ " is not present in the Raw Response" );
						tt.driver.log.info("The value " +responseValues[s]+ " is not present in the Raw Response" );
						resultDetails.setFlag(false);
					}

				}
			}


			if(responseHeaders.length()>=1)
			{
				String responseValues[]=responseHeaders.split(",");

				for(int s=0;s<responseValues.length;s++)
				{

					String headerValue=	responseValues[s].split("=")[0];
					String expected=responseValues[s].split("=")[1];	
					Header[] headers = response.getHeaders(headerValue);
					for(int h=0;h<headers.length;h++){
						String actual=headers[h].getValue();
						if(actual.equalsIgnoreCase(expected))
						{
							System.out.println("Verified");
							System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
							tt.driver.log.info("Verified");
							tt.driver.log.info("+++++++++++++++++++++++++++++++++++++++++++++");
							System.out.println("The value " +responseValues[s]+ " is present in the Headers" );
							tt.driver.log.info("The value " +responseValues[s]+ " is present in the Headers" );
							resultDetails.setFlag(true);
						}

						else{
							System.out.println("The value " +responseValues[s]+ " is not present in the Headers" );
							tt.driver.log.info("The value " +responseValues[s]+ " is not present in the Headers" );
							resultDetails.setFlag(false);
						}
					}

				}
			}

			httpClient.getConnectionManager().shutdown();
		}

		catch (MalformedURLException e) {

			resultDetails.getErrorMessage();
			resultDetails.setErrorMessage("response not matched Expected:"+responseBody+" Actual:"+PostResponse);
			driver.log.error("response not matched Expected:"+responseBody+" Actual:"+PostResponse);

		} catch (IOException e) {

			resultDetails.getErrorMessage();
			resultDetails.setErrorMessage("response not matched Expected:"+responseBody+" Actual:"+PostResponse);
			driver.log.error("response not matched Expected:"+responseBody+" Actual:"+PostResponse);

		}
		catch (Exception e) {

			resultDetails.getErrorMessage();
			resultDetails.setErrorMessage("response not matched Expected:"+responseBody+" Actual:"+PostResponse);
			driver.log.error("response not matched Expected:"+responseBody+" Actual:"+PostResponse);

		}
		return resultDetails;



	}
}



