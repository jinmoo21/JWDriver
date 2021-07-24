package com.dove.mwd;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

public class WebDriverFactory {
	public static WebDriver getWebDriver() throws MalformedURLException {
		switch (Util.browser) {
		case "edge":
			WebDriverManager.edgedriver().setup();
			return Util.isRemote ? new RemoteWebDriver(new URL(Util.remoteAddress), new EdgeOptions()) :
				new EdgeDriver();
		case "ie":
			WebDriverManager.iedriver().setup();
			return Util.isRemote ? new RemoteWebDriver(new URL(Util.remoteAddress), new InternetExplorerOptions()) :
				new InternetExplorerDriver();
		case "safari":
			return Util.isRemote ? new RemoteWebDriver(new URL(Util.remoteAddress), new SafariOptions()) :
				new SafariDriver();
		default:
			WebDriverManager.chromedriver().setup();
			return Util.isRemote ? new RemoteWebDriver(new URL(Util.remoteAddress), new ChromeOptions()) :
				new ChromeDriver();
		}
	}
	
	public static WebDriver getWebDriver(AbstractDriverOptions<?> options) throws MalformedURLException {
		switch (Optional.ofNullable(System.getProperty("webdriver.browser")).map(String::toLowerCase).orElse("")) {
		case "edge":
			WebDriverManager.edgedriver().setup();
			if (options instanceof EdgeOptions) {
				return Util.isRemote ? new RemoteWebDriver(new URL(Util.remoteAddress), (EdgeOptions) options) :
					new EdgeDriver((EdgeOptions) options);
			}
			return Util.isRemote ? new RemoteWebDriver(new URL(Util.remoteAddress), new EdgeOptions()) :
				new EdgeDriver();
		case "ie":
			WebDriverManager.iedriver().setup();
			if (options instanceof InternetExplorerOptions) {
				return Util.isRemote ? new RemoteWebDriver(new URL(Util.remoteAddress), (InternetExplorerOptions) options) :
					new InternetExplorerDriver((InternetExplorerOptions) options);
			}
			return Util.isRemote ? new RemoteWebDriver(new URL(Util.remoteAddress), new InternetExplorerOptions()) :
				new InternetExplorerDriver();
		case "safari":
			if (options instanceof SafariOptions) {
				return Util.isRemote ? new RemoteWebDriver(new URL(Util.remoteAddress), (SafariOptions) options) :
					new SafariDriver((SafariOptions) options);
			}
			return Util.isRemote ? new RemoteWebDriver(new URL(Util.remoteAddress), new SafariOptions()) :
				new SafariDriver();
		default:
			WebDriverManager.chromedriver().setup();
			if (options instanceof ChromeOptions) {
				return Util.isRemote ? new RemoteWebDriver(new URL(Util.remoteAddress), (ChromeOptions) options) :
					new ChromeDriver((ChromeOptions) options);
			}
			return Util.isRemote ? new RemoteWebDriver(new URL(Util.remoteAddress), new ChromeOptions()) :
				new ChromeDriver();
		}
	}
}
