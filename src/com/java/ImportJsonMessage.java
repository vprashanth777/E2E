package com.java;

import java.io.FileReader;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

public class ImportJsonMessage {

	public String getJsonInput(String inputName)
	{
		String input=null;
		String file=null;
		String inputField=null;
		try {
			file=inputName.split(":")[0];
			Object json=new JSONParser().parse(new FileReader(System.getProperty("user.dir")+"\\"+file));
			inputField=inputName.split(":")[1];
			JSONObject jsonObject = (JSONObject) json; 
			System.out.println("The user directry is-----" +System.getProperty("user.dir"));
			JSONObject name=(JSONObject)jsonObject.get(inputField);
			input=name.toString();
			System.out.println(input);
			return input;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return input;
		}
	}

}



