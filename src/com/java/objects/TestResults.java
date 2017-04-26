package com.java.objects;

import java.util.Date;

/**
 * This is to store each row details form Test Data Details Excel Sheet
 */
public class TestResults {
	
	private int TCID;
	private String TCTitle;
	private String result;
	private String errorMsg;
	private Date time_Stamp;
	private String time_Taken;
	private String comment;
	private String evidence = null;
	
	private String defaults(String value,String defaultValue){
		if(value == null){
			value = defaultValue;
		}
		return value;
	}
	
	public void setTCID(int tcid) {
		TCID = tcid;
	}
	
	public String getTCTitle() {
		return TCTitle;
	}

	public void setTCTitle(String tCTitle) {
		TCTitle = tCTitle;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = defaults(errorMsg, "");
	}

	public Date getTime_Stamp() {
		return time_Stamp;
	}
	
	public void setTime_Stamp(Date time_Stamp) {
		this.time_Stamp = time_Stamp;
	}
	
	public String getTime_Taken() {
		return time_Taken;
	}
	
	public void setTime_Taken(String time_Taken) {
		this.time_Taken = time_Taken;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment =defaults(comment,"");
	}
	
	public int getTCID() {
		return TCID;
	}
	
	public String getEvidence() {
		return evidence;
	}
	
	public void setEvidence(String evidence) {
		this.evidence = evidence;
	}
}
