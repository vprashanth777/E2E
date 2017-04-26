package com.java.objects;

public class ResultDetails {
	
	private boolean flag = false;
	private String errorMessage = "";
	private String warningMessage = "";
	private String comment = "";
	/**
	 * @return Returns the flag.
	 */
	
	public boolean getFlag() {
		return flag;
	}
	
	/**
	 * @param flag The flag to set.
	 */
	
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
	/**
	 * @return Returns the errorMessage.
	 */
	
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @return Returns the warningMessage.
	 */
	
	public String getWarningMessage() {
		return warningMessage;
	}
	
	/**
	 * @param errorMessage The errorMessage to set.
	 */
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	/**
	 * @param WarningMessage The WarningMessage to set.
	 */
	
	public void setWarningMessage(String warningMessage) {
		this.warningMessage = warningMessage;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the comments
	 */
	public String getComment() {
		return comment;
	}
}
