package com.dove.mwd;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.Capabilities;
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

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

public class WebDriverFactory {
	public static WebDriver getWebDriver() throws MalformedURLException {
		switch (Utils.ENVIRONMENT) {
		case "edge":
			WebDriverManager.edgedriver().setup();
			return Utils.IS_REMOTE ? new RemoteWebDriver(new URL(Utils.REMOTE_ADDRESS), new EdgeOptions()) :
				new EdgeDriver();
		case "ie":
			WebDriverManager.iedriver().setup();
			return Utils.IS_REMOTE ? new RemoteWebDriver(new URL(Utils.REMOTE_ADDRESS), new InternetExplorerOptions()) :
				new InternetExplorerDriver();
		case "safari":
			return Utils.IS_REMOTE ? new RemoteWebDriver(new URL(Utils.REMOTE_ADDRESS), new SafariOptions()) :
				new SafariDriver();
		default:
			WebDriverManager.chromedriver().setup();
			return Utils.IS_REMOTE ? new RemoteWebDriver(new URL(Utils.REMOTE_ADDRESS), new ChromeOptions()) :
				new ChromeDriver();
		}
	}
	
	public static WebDriver getWebDriver(AbstractDriverOptions<?> options) throws MalformedURLException {
		switch (Utils.ENVIRONMENT) {
		case "edge":
			WebDriverManager.edgedriver().setup();
			if (options instanceof EdgeOptions) {
				return Utils.IS_REMOTE ? new RemoteWebDriver(new URL(Utils.REMOTE_ADDRESS), (EdgeOptions) options) :
					new EdgeDriver((EdgeOptions) options);
			}
			return Utils.IS_REMOTE ? new RemoteWebDriver(new URL(Utils.REMOTE_ADDRESS), new EdgeOptions()) :
				new EdgeDriver();
		case "ie":
			WebDriverManager.iedriver().setup();
			if (options instanceof InternetExplorerOptions) {
				return Utils.IS_REMOTE ? new RemoteWebDriver(new URL(Utils.REMOTE_ADDRESS), (InternetExplorerOptions) options) :
					new InternetExplorerDriver((InternetExplorerOptions) options);
			}
			return Utils.IS_REMOTE ? new RemoteWebDriver(new URL(Utils.REMOTE_ADDRESS), new InternetExplorerOptions()) :
				new InternetExplorerDriver();
		case "safari":
			if (options instanceof SafariOptions) {
				return Utils.IS_REMOTE ? new RemoteWebDriver(new URL(Utils.REMOTE_ADDRESS), (SafariOptions) options) :
					new SafariDriver((SafariOptions) options);
			}
			return Utils.IS_REMOTE ? new RemoteWebDriver(new URL(Utils.REMOTE_ADDRESS), new SafariOptions()) :
				new SafariDriver();
		default:
			WebDriverManager.chromedriver().setup();
			if (options instanceof ChromeOptions) {
				return Utils.IS_REMOTE ? new RemoteWebDriver(new URL(Utils.REMOTE_ADDRESS), (ChromeOptions) options) :
					new ChromeDriver((ChromeOptions) options);
			}
			return Utils.IS_REMOTE ? new RemoteWebDriver(new URL(Utils.REMOTE_ADDRESS), new ChromeOptions()) :
				new ChromeDriver();
		}
	}
	
	public static WebDriver getWebDriver(Capabilities capabilities) throws MalformedURLException {
		switch (Utils.ENVIRONMENT) {
		case "android":
			return new AndroidDriver(new URL(Utils.REMOTE_ADDRESS), capabilities);
		case "ios":
			return new IOSDriver(new URL(Utils.REMOTE_ADDRESS), capabilities);
		default:
			throw new NotDefinedException("System property of [webdriver.env] must be [Android] or [iOS].");
		}
	}
}
