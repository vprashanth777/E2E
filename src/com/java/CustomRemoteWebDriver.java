package com.java;

import org.openqa.selenium.remote.*;
import org.openqa.selenium.*;
import java.net.*;

/**
 *  Class which is subclass of RemoteWebdriver. Written to implement the ScreenShot
 */

public class CustomRemoteWebDriver extends RemoteWebDriver implements TakesScreenshot {
	
	/**
	 *  Constructor to create the Webdriver object using grid
	 */
	
	public CustomRemoteWebDriver(URL url, DesiredCapabilities dc) {
		super(url, dc);
	}

	/**
	 *  The method overridden to implement the screen shot functionality
	 */
	
	@Override
	public <X> X getScreenshotAs(OutputType<X> target)
			throws WebDriverException {
		if ((Boolean) getCapabilities().getCapability(CapabilityType.TAKES_SCREENSHOT)) {
			String base64Str = execute(DriverCommand.SCREENSHOT).getValue().toString();
			return target.convertFromBase64Png(base64Str);
		}
		return null;
	}

}