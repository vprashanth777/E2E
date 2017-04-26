package com.java;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

public class Utils {
	public SeleniumDriver driver;
	public Utils(SeleniumDriver driver) {
		this.driver = driver;
	}
    
    /**     
     * Method to get the decrypted data
     * @param secretKey Key used to decrypt data
     * @param encryptedText encrypted text input to getdecrypt
     * @return Returns text after decryption
     */
    
	 public  String getdecrypt( String encryptedText)
		     throws NoSuchAlgorithmException, 
		            InvalidKeySpecException, 
		            NoSuchPaddingException, 
		            InvalidKeyException,
		            InvalidAlgorithmParameterException, 
		            UnsupportedEncodingException, 
		            IllegalBlockSizeException, 
		            BadPaddingException, 
		            IOException{
		 Cipher ecipher;
		    Cipher dcipher;
		    // 8-byte Salt
		    byte[] salt = {
		        (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
		        (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
		    };
		    // Iteration count
		    int iterationCount = 19;
		    String secretKey="ezeon8547";  
		    
		         //Key generation for enc and desc
		        KeySpec keySpec = new PBEKeySpec(secretKey.toCharArray(), salt, iterationCount);
		        SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);        
		         // Prepare the parameter to the ciphers
		        AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
		        //Decryption process; same key will be used for decr
		        dcipher=Cipher.getInstance(key.getAlgorithm());
		        dcipher.init(Cipher.DECRYPT_MODE, key,paramSpec);
		        byte[] enc = new BASE64Decoder().decodeBuffer(encryptedText);
		        byte[] utf8 = dcipher.doFinal(enc);
		        String charSet="UTF-8";     
		        String plainStr = new String(utf8, charSet);
		        System.out.println(" value = "+plainStr);
		    	driver.log.info(" value = "+plainStr );
		        return plainStr;
		    }  
	 
	 /**
		 * Method to get the field form Repository
		 */
		
		public String getFieldFromRepo(String field) {

			String obj = field.toLowerCase();
			
			if (obj.startsWith("obj:")) {
				
				String temp = obj.substring(4);
				
				obj = driver.objRepo.getProperty(temp,temp);
				
				System.out.println(" Field = " + obj);
				driver.log.info(" Field = " + obj);
				return obj;
			}
			
			System.out.println(" Field = " + field);
			return field;
		}
		
	 
	 /**
		 * Method to get Values from HashMap / TestData
		 */
		
		public String getValue(String value) {

			String tempValue = value;

			if (tempValue.toLowerCase().startsWith("key:")) {
				
				tempValue = tempValue.substring(4).toLowerCase();
				
				if(driver.inExecute && !tempValue.contains("::")){
					tempValue = driver.setup_data.getProperty(tempValue,tempValue);
					driver.log.info("value = " + tempValue);
					System.out.println("value = " + tempValue);
					return tempValue;
				}
				
				else {
					String index = "1";
					String val[] = tempValue.split("::"); 
				
					if(val[1].contains("#")){
						int n = val[1].indexOf("#");
						String temp = val[1].substring(0, n);
						index = (val[1].substring(n + 1));
						val[1] = temp;
					}
				
					tempValue = driver.setup_TestData.get(val[0].toLowerCase()+"#"+index).getProperty(val[1],val[1]);
				}
				driver.log.info(" value = " + tempValue);
				System.out.println(" value = " + tempValue);
				return tempValue;
			}
			
			if ( tempValue.toLowerCase().endsWith("#rnd")) {
				
				tempValue = tempValue.substring(3,tempValue.length()-4);
				if(driver.testDataCounter.containsKey(tempValue)){
					int n = 1;
					int count = driver.testDataCounter.get(tempValue);
					n = (int)( count * Math.random()) % count;
					if(n == 0 ){
						n = count;
					}
					tempValue = tempValue + n;
				}
			}
			if(tempValue.trim().contains("<HMV")&&tempValue.trim().contains(">\\+<")||tempValue.trim().contains("<HMV")&&tempValue.trim().contains(">+<"))
			{
				String tmp="";
				if(tempValue.contains(">+ <")||tempValue.contains(">+ <")||tempValue.contains("> + <")) 
				{
					tempValue=tempValue.replace("> +<", ">+<");
					tempValue=tempValue.replace(">+ <", ">+<");
					tempValue=tempValue.replace("> + <", ">+<");
				}
				int i=0,len=tempValue.split(">\\+<").length;
				while(i<len)
				{
					String split="";
					if(i==0)
					{
						split=tempValue.split(">\\+<")[i].substring(1);
					}else if(i==len-1)
					{
						/*System.out.println(tempValue.split(">\\+<")[i]);
						System.out.println(tempValue.split(">\\+<")[i].substring(0, tempValue.split(">\\+<")[i].length()-1));
						*/split=tempValue.split(">\\+<")[i].substring(0, tempValue.split(">\\+<")[i].length()-1);
					}else{
						split=tempValue.split(">\\+<")[i].substring(0, tempValue.split(">\\+<")[i].length()-1);
					}
					if(split.startsWith("HMV"))
					{
						//System.out.println("HMV:"+split.substring(3));
						split=driver.hMap.get(split.substring(3));
						if(split==null){
							System.out.println("There is no object with the name:"+split.substring(3)+" in the HashMap");
						}
					}
					
					tmp=tmp+split;
					i++;
				}
			
				tempValue=tmp;
				driver.log.info(" value = " + tempValue);
				System.out.println(" value = " + tempValue);
				return tempValue;
				
				
				
				
				
			}
			 if(tempValue.startsWith("DECPT:"))
			{
				try {
					System.out.println("Decrypting the given data:"+tempValue.substring(6));
					driver.log.info("Decrypting the given data:"+tempValue.substring(6));
					tempValue = getdecrypt(tempValue.substring(6));
				} catch(Exception e){
					System.out.println();
				}
				driver.log.info(" value = " + tempValue);
				System.out.println(" value = " + tempValue);
				return tempValue;
			}
			 if(tempValue.startsWith("RNDSTR")){
				 final String CHAR_LIST =
		    		        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		    		    final int RANDOM_STRING_LENGTH = 5;
		    		     
		        StringBuffer randStr = new StringBuffer();
		    	Random randomGenerator = new Random();
		    
		        for(int i=0; i<RANDOM_STRING_LENGTH; i++){
		        
		        	int randomInt = randomGenerator.nextInt(51);
		            
		            char ch = CHAR_LIST.charAt(randomInt);
		            randStr.append(ch);
		        }
		    	driver.log.info(" value = " + tempValue.substring(6)+randStr.toString());
				System.out.println(" value = " + tempValue.substring(6)+randStr.toString());
		        return tempValue.substring(6)+randStr.toString();
			 }
			if ( tempValue.toLowerCase().endsWith("#auto")) {
				
				tempValue = tempValue.substring(3,tempValue.length()-5);
				int n = 1;
				
				if(driver.inLoop && driver.testDataCounter.containsKey(tempValue)){
					
					n = Integer.parseInt(driver.hMap.get("LOOPCOUNTER"));
					int count = driver.testDataCounter.get(tempValue);
					n = n % count;
					if(n == 0)
						n = count;
				}
				tempValue = tempValue + n ;
			}

			if (driver.parameterDetails.containsKey(tempValue)) {
				tempValue = driver.parameterDetails.get(tempValue);
				System.out.println(" value = " + tempValue);
				driver.log.info(" value = " + tempValue);
				return tempValue;
			}
				
			if (tempValue.length() >= 3) {

				String tempValueType = tempValue.substring(0, 3);
				tempValue = value.substring(3);

				if (tempValueType.equalsIgnoreCase("RND")) {
					
					int n = 5;
					
					if(tempValue.startsWith(":")){
						tempValue = tempValue.substring(1);
						n = tempValue.indexOf(":");
						if( n != -1){
							String temp = tempValue.substring(n+1);
							n = Integer.parseInt(tempValue.substring(0, n));
							tempValue = temp;
						}else{
							n = Integer.parseInt(tempValue);
							tempValue = "";
						}
					}
					
					for(int i=0;i<n;i++){
						String random = (int) (Math.random() * 9) + "";
						tempValue = tempValue + random;
					}
				} else if (tempValueType.equalsIgnoreCase("HMV")) {
					tempValue = driver.hMap.get(tempValue);
				}else if (tempValueType.equalsIgnoreCase("dt:")||tempValueType.equalsIgnoreCase("dt|")) {

					if (tempValue.indexOf("#") == -1) {
						tempValue = tempValue + "1";
					}

					tempValue = tempValue.replace("#", "").toLowerCase();

					if (driver.parameterDetails.containsKey(tempValue))
						tempValue = driver.parameterDetails.get(tempValue);
				} else if (tempValueType.startsWith("d:")) {
					tempValue = getDate(value.substring(2));
				} else {
					tempValue = value;
				}
			}
			driver.log.info(" value = " + tempValue);
			System.out.println(" value = " + tempValue);
			return tempValue;
		}

	 /**
		 * Method to get required Date
		 */	

		public String getDate(String value) {

			String dateFormat = "MM/dd/yyyy";
			
			int index = value.indexOf(":format:");
			
			if(index!= -1){
				dateFormat = value.substring(index+8);
				value = value.substring(0,index);
			}
			
			System.out.println("Date Format : " + dateFormat);
			System.out.println("Date specified : " +value);
			
			String[] tempValues = value.split(":");
			
			String reqDate = "";

			DateFormat sdf = new SimpleDateFormat(dateFormat);
			Date today = new Date();

			Calendar cal = Calendar.getInstance();
			cal.setTime(today);

			if (tempValues[0].equalsIgnoreCase("currentdate")) {

				reqDate = sdf.format(today);
				System.out.println("Current Date  = " + reqDate);

			} else if (tempValues[0].equalsIgnoreCase("effectivedate")) {

				cal.set(Calendar.DAY_OF_MONTH, 1);

				reqDate = sdf.format((Date) cal.getTime());
				System.out.println("Effective Date = " + reqDate);

			} else if (tempValues[0].equalsIgnoreCase("monthend")) {

				cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));  
				reqDate = sdf.format((Date) cal.getTime());
				System.out.println("Month End Date = " + reqDate);
				
				if (tempValues.length == 3) {

					int changeby = Integer.parseInt(tempValues[2]);
					
					if (tempValues[1].equals("M"))
						cal.add(Calendar.MONTH, changeby);

					else if (tempValues[1].equals("d"))
						cal.add(Calendar.DATE, changeby);

					else if (tempValues[1].equals("y"))
						cal.add(Calendar.YEAR, changeby);

					cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));  
					reqDate = sdf.format(cal.getTime());
					System.out.println("Required date : " + reqDate);
					
				}
				
				driver.hMap.put("strDate", reqDate);
				return reqDate;

			} else if (tempValues[0].toLowerCase().startsWith("HMV")) {

				reqDate = driver.hMap.get(tempValues[0].substring(3));
				try {
					today = (Date) sdf.parse(reqDate);
				} catch (ParseException e) {
					System.out.println("Unable to parse Date :: " + reqDate);
					today = new Date();
				}
				cal.setTime(today);
			}

			if (tempValues.length == 1)
				return reqDate;

			int changeby = Integer.parseInt(tempValues[2]);

			if (tempValues.length == 3) {

				if (tempValues[1].equals("M"))
					cal.add(Calendar.MONTH, changeby);

				else if (tempValues[1].equals("d"))
					cal.add(Calendar.DATE, changeby);

				else if (tempValues[1].equals("y"))
					cal.add(Calendar.YEAR, changeby);

				reqDate = sdf.format(cal.getTime());
				System.out.println(" Required date : " + reqDate);
			}

			driver.hMap.put("strDate", reqDate);
			return reqDate;
		}
		
}
