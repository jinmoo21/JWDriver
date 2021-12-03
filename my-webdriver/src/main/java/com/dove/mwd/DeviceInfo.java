package com.dove.mwd;

import org.openqa.selenium.remote.DesiredCapabilities;

import lombok.Getter;

public enum DeviceInfo {
	PIXEL4_10_SIMULATOR("Android", "Pixel_4_API_29", "10", "Simulator"),
	IPHONE8_14_SIMULATIOR("iOS", "iPhone8", "14.5", "Simulator"),
	NOTE8_9("Android", "Galaxy Note8", "9", "Real"),
	S9_10("Android", "Galaxy S9", "10", "Real");
	
	private String platformName;
	@Getter
	private String deviceType;
	@Getter
	private DesiredCapabilities capabilities = new DesiredCapabilities();

	DeviceInfo(String platformName, String deviceName, String platformVersion, String deviceType) {
		this.platformName = platformName;
		this.deviceType = deviceType;
		capabilities.setCapability("platformName", platformName);
		capabilities.setCapability(isAndroid() && isSimulator() ? "avd" : "deviceName", deviceName);
		capabilities.setCapability("platformVersion", platformVersion);
		capabilities.setCapability("automationName", isAndroid() ? "UiAutomator1" : "XCUITest");
		capabilities.setCapability("noReset", true);
		capabilities.setCapability("language", "ko");
		capabilities.setCapability("locale", isAndroid() ? "KR" : "ko_KR");
		if (isAndroid()) { // Android
			if (isSimulator()) { // Simulator
				capabilities.setCapability("avdArgs", "-dns-server 8.8.8.8");
			} else { // Real Device
				
			}
			capabilities.setCapability("skipServerInstallation", true);
			capabilities.setCapability("skipDeviceInitialization", true);
			//caps.setCapability("ignoreUnimportantViews",true);
		} else { // iOS
			if (isSimulator()) { // Simulator
				
			} else { // Real Device
				capabilities.setCapability("udid", Utils.getUDID());
			}
			// caps.setCapability("showIOSLog", true);
		}
	}
	
	public boolean isAndroid() {
		return platformName.equalsIgnoreCase("android");
	}
	
	public boolean isSimulator() {
		return deviceType.equalsIgnoreCase("simulator");
	}
}
