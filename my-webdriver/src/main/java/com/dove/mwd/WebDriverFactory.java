package com.dove.mwd;

import java.util.Optional;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

public class WebDriverFactory {
	public static WebDriver getWebDriver() {
		switch (Optional.ofNullable(System.getProperty("webdriver.browser")).map(String::toLowerCase).orElse("")) {
		case "edge":
			WebDriverManager.edgedriver().setup();
			return new EdgeDriver();
		case "ie":
			WebDriverManager.iedriver().setup();
			return new InternetExplorerDriver();
		case "safari":
			return new SafariDriver();
		default:
			WebDriverManager.chromedriver().setup();
			return new ChromeDriver();
		}
	}
	
	public static WebDriver getWebDriver(AbstractDriverOptions<?> options) {
		switch (Optional.ofNullable(System.getProperty("webdriver.browser")).map(String::toLowerCase).orElse("")) {
		case "edge":
			WebDriverManager.edgedriver().setup();
			if (options instanceof EdgeOptions) {
				return new EdgeDriver((EdgeOptions) options);
			}
			return new EdgeDriver();
		case "ie":
			WebDriverManager.iedriver().setup();
			if (options instanceof InternetExplorerOptions) {
				return new InternetExplorerDriver((InternetExplorerOptions) options);
			}
			return new InternetExplorerDriver();
		case "safari":
			if (options instanceof SafariOptions) {
				return new SafariDriver((SafariOptions) options);
			}
			return new SafariDriver();
		default:
			WebDriverManager.chromedriver().setup();
			if (options instanceof ChromeOptions) {
				return new ChromeDriver((ChromeOptions) options);
			}
			return new ChromeDriver();
		}
	}
}
