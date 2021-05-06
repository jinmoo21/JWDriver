package com.dove.mwd;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {
	public static final int AJAX_TIMEOUT = 5;
	public static final int RETRY_COUNT = 2;
	public static final Logger logger = LoggerFactory.getLogger(Util.class);
	
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
}
