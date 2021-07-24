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
		switch (Utils.browser) {
		case "edge":
			WebDriverManager.edgedriver().setup();
			return Utils.isRemote ? new RemoteWebDriver(new URL(Utils.remoteAddress), new EdgeOptions()) :
				new EdgeDriver();
		case "ie":
			WebDriverManager.iedriver().setup();
			return Utils.isRemote ? new RemoteWebDriver(new URL(Utils.remoteAddress), new InternetExplorerOptions()) :
				new InternetExplorerDriver();
		case "safari":
			return Utils.isRemote ? new RemoteWebDriver(new URL(Utils.remoteAddress), new SafariOptions()) :
				new SafariDriver();
		default:
			WebDriverManager.chromedriver().setup();
			return Utils.isRemote ? new RemoteWebDriver(new URL(Utils.remoteAddress), new ChromeOptions()) :
				new ChromeDriver();
		}
	}
	
	public static WebDriver getWebDriver(AbstractDriverOptions<?> options) throws MalformedURLException {
		switch (Optional.ofNullable(System.getProperty("webdriver.browser")).map(String::toLowerCase).orElse("")) {
		case "edge":
			WebDriverManager.edgedriver().setup();
			if (options instanceof EdgeOptions) {
				return Utils.isRemote ? new RemoteWebDriver(new URL(Utils.remoteAddress), (EdgeOptions) options) :
					new EdgeDriver((EdgeOptions) options);
			}
			return Utils.isRemote ? new RemoteWebDriver(new URL(Utils.remoteAddress), new EdgeOptions()) :
				new EdgeDriver();
		case "ie":
			WebDriverManager.iedriver().setup();
			if (options instanceof InternetExplorerOptions) {
				return Utils.isRemote ? new RemoteWebDriver(new URL(Utils.remoteAddress), (InternetExplorerOptions) options) :
					new InternetExplorerDriver((InternetExplorerOptions) options);
			}
			return Utils.isRemote ? new RemoteWebDriver(new URL(Utils.remoteAddress), new InternetExplorerOptions()) :
				new InternetExplorerDriver();
		case "safari":
			if (options instanceof SafariOptions) {
				return Utils.isRemote ? new RemoteWebDriver(new URL(Utils.remoteAddress), (SafariOptions) options) :
					new SafariDriver((SafariOptions) options);
			}
			return Utils.isRemote ? new RemoteWebDriver(new URL(Utils.remoteAddress), new SafariOptions()) :
				new SafariDriver();
		default:
			WebDriverManager.chromedriver().setup();
			if (options instanceof ChromeOptions) {
				return Utils.isRemote ? new RemoteWebDriver(new URL(Utils.remoteAddress), (ChromeOptions) options) :
					new ChromeDriver((ChromeOptions) options);
			}
			return Utils.isRemote ? new RemoteWebDriver(new URL(Utils.remoteAddress), new ChromeOptions()) :
				new ChromeDriver();
		}
	}
}
