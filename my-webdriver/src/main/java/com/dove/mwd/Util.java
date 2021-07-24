package com.dove.mwd;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {
	private static final Logger logger = LoggerFactory.getLogger(Util.class);
	public static final int AJAX_TIMEOUT = 5;
	public static final int RETRY_COUNT = 2;
	public static final String SCREENSHOT_FOLDER = new File("screenshots").getAbsolutePath();
	public static final String remoteAddress = System.getProperty("webdriver.remote");
	public static final String browser = Optional.ofNullable(System.getProperty("webdriver.browser")).map(String::toLowerCase).orElse("");
	public static final boolean isRemote = Optional.ofNullable(remoteAddress).isPresent();
	
	public static String executeCommand(String command) {
		String result = "";
		try {
			Process process = new ProcessBuilder(command)
					.directory(new File(System.getProperty("user.home")))
					.start();
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String temp = "";
			while ((temp = br.readLine()) != null) {
				result += temp + "\n";
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return result;
	}
	
	/**
	 * Get UDID from iOS device connected on Mac. Required libimobiledevice.
	 * 
	 * @return UDID of device.
	 */
	public static String getUDID() {
		String result = SystemUtils.IS_OS_WINDOWS ? "Only working on Mac OS." :
			executeCommand("/usr/local/bin/idevice_id -l").endsWith("command not found") ? "Required libimobiledevice." :
				executeCommand("/usr/local/bin/idevice_id -l");
		return result;
	}
}
