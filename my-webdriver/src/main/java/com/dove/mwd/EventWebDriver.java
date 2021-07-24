package com.dove.mwd;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v90.emulation.Emulation;
import org.openqa.selenium.devtools.v90.log.Log;
import org.openqa.selenium.devtools.v90.network.Network;
import org.openqa.selenium.devtools.v90.network.model.ConnectionType;
import org.openqa.selenium.devtools.v90.network.model.Headers;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("deprecation")
public class EventWebDriver extends EventFiringWebDriver {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private DevTools devtools;

	public EventWebDriver(WebDriver driver) {
		super(driver);
		if (driver instanceof ChromeDriver) {
			devtools = ((ChromeDriver) driver).getDevTools();
			devtools.createSession();
			logger.info("DevTools activated.");
		}
		register(new WebDriverEventListener());
	}
	
	/**
	 * This method will check the list below.<br>
	 * <br>
	 * 1. jQuery load finished.(if exists)<br>
	 * 2. Angular load finished.(if exists)<br>
	 * 3. Javascript load finished.<br>
	 * 4. FE loadEvent finished.
	 */
	public void waitPageLoadFinished() {
		try {
			new WebDriverWait(this, Duration.ofSeconds(Utils.AJAX_TIMEOUT)).until(jQueryLoadFinished());
		} catch (NotDefinedException e) {
			logger.warn("{}: {}", e.getMessage(), getCurrentUrl());
		}
		try {
			new WebDriverWait(this, Duration.ofSeconds(Utils.AJAX_TIMEOUT)).until(angularLoadFinished());
		} catch (NotDefinedException e) {
			logger.warn("{}: {}", e.getMessage(), getCurrentUrl());
		}
		jsLoadFinished();
		loadEventFinished();
	}

	/**
	 * Check jQuery defined, and load finished.
	 * 
	 * @return true or false.
	 */
	public ExpectedCondition<Boolean> jQueryLoadFinished() {
		return d -> {
			if (!Boolean.valueOf(((JavascriptExecutor) d).executeScript("return window.jQuery !== undefined").toString())) {
				throw new NotDefinedException("jQuery not defined");
			}
			return Boolean.valueOf(((JavascriptExecutor) d).executeScript("return jQuery.active === 0").toString());
		};
	}

	/**
	 * Check Angular defined, and load finished.
	 * 
	 * @return true or false.
	 */
	public ExpectedCondition<Boolean> angularLoadFinished() {
		return d -> {
			if (!Boolean.valueOf(((JavascriptExecutor) d).executeScript("return window.angular !== undefined").toString())) {
				throw new NotDefinedException("Angular not defined");
			}
			if (!Boolean.valueOf(((JavascriptExecutor) d).executeScript("return angular.element(document).injector() !== undefined").toString())) {
				throw new NotDefinedException("Injector not defined");
			}
			return Boolean.valueOf(((JavascriptExecutor) d).executeScript("return angular.element(document).injector().get('$http').pendingRequests.length === 0").toString());
		};
	}

	/**
	 * Check javascript load finished.
	 * 
	 * @return true or false.
	 */
	public ExpectedCondition<Boolean> jsLoadFinished() {
		return d -> ((JavascriptExecutor) d).executeScript("return document.readyState").toString().equalsIgnoreCase("complete");
	}

	/**
	 * Check FE performance.timing.loadEventEnd.
	 * 
	 * @return true or false.
	 */
	public ExpectedCondition<Boolean> loadEventFinished() {
		return d -> Boolean.valueOf(((JavascriptExecutor) d).executeScript("return performance.timing.loadEventEnd !== 0").toString());
	}
	
	/**
	 * Console logs will be shown.
	 */
	public void captureConsoleLog() {
		Optional.ofNullable(devtools).ifPresent(d -> {
			d.send(Log.enable());
			d.addListener(Log.entryAdded(), entry -> logger.info(entry.getText()));
			logger.info("Capturing console logs start.");
		});
	}
	
	/**
	 * HTTP status code 4XX and 5XX will be shown.
	 */
	public void captureNetworkErrorResponse() {
		Optional.ofNullable(devtools).ifPresent(d -> {
			d.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
			d.addListener(Network.responseReceived(), resp -> {
				if (resp.getResponse().getStatus() >= 400 && resp.getResponse().getStatus() < 600) {
					logger.error("Response Error:\nName: {}\nHTTP Status: {}\nType: {}", resp.getResponse().getUrl(), resp.getResponse().getStatus(), resp.getType());
				}
			});
			logger.info("Capturing HTTP status code 4XX and 5XX responses start.");
		});
	}
	
	/**
	 * Clear listeners that capturing console log and capturing error response.
	 */
	public void clearListeners() {
		Optional.ofNullable(devtools).ifPresent(d -> {
			d.clearListeners();
			logger.info("Browser cache and cookies cleared.");
		});
	}
	
	/**
	 * Add custom header.
	 * 
	 * @param Header key.
	 * @param Header value.
	 */
	public void addExtraHeader(String key, Object value) {
		Optional.ofNullable(devtools).ifPresent(d -> {
			d.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
			d.send(Network.setExtraHTTPHeaders(new Headers(Map.of(key, value))));
			logger.info("Extra Header value added: ({}, {})", key, value);
		});
    }
	
	/**
	 * Clear browser cache and cookies.
	 */
	public void clearBrowserCacheAndCookies() {
		Optional.ofNullable(devtools).ifPresent(d -> {
			d.send(Network.clearBrowserCache());
			d.send(Network.clearBrowserCookies());
			logger.info("Browser cache and cookies cleared.");
		});
	}
	
	/**
	 * Set Fake GPS.
	 * 
	 * @param latitude
	 * @param longitude
	 * @param accuracy
	 */
	public void setGeolocation(Number latitude, Number longitude, Number accuracy) {
		Optional.ofNullable(devtools).ifPresent(d -> {
			d.send(Emulation.setGeolocationOverride(
					Optional.of(latitude),
					Optional.of(longitude),
					Optional.of(accuracy)));
			logger.info("Your geolocation: ({}, {}, {})", latitude, longitude, accuracy);
		});
	}
	
	/**
	 * Clear geolocation.
	 */
	public void clearGeoLocation() {
		Optional.ofNullable(devtools).ifPresent(d -> {
			d.send(Emulation.clearGeolocationOverride());
			logger.info("Overriding geolocation cleared.");
		});
	}
	
	/**
	 * Set network emulation.
	 * 
	 * @param profile
	 */
	public void setNetworkEmulation(NetworkProfile profile) {
		Optional.ofNullable(devtools).ifPresent(d -> {
			d.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
			d.send(Network.emulateNetworkConditions(profile.isOffine(), profile.getLatency(), profile.getDownload(), profile.getUpload(), Optional.of(ConnectionType.OTHER)));
			logger.info("Network emulation start: {}", profile.getValue());
		});
	}
	
	/**
	 * Clear all listeners and close session of DevTools.
	 */
	public void closeDevTools() {
		Optional.ofNullable(devtools).ifPresent(d -> {
			d.close();
			logger.info("DevTools closed.");
		});
	}
	
	/**
	 * Click element using javascript instead of webdriver method.
	 * 
	 * @param element
	 */
	public void clickUsingScript(WebElement element) {
		executeScript("arguments[0].click();", element);
	}
	
	/**
	 * Get name of switched frame/iframe.
	 * 
	 * @return frame name
	 */
	public String getCurrentFrameName() {
		try {
			return executeScript("return window.frameElement.name").toString();
		} catch (WebDriverException e) {
			if (e.getMessage().contains("Cannot read property 'name' of null")) {
				logger.info("No frame.");
			}
			return null;
		}
	}
	
	/**
	 * Scroll to (0, 0)
	 */
	public void scrollToTop() {
		executeScript("window.scrollTo(0, 0)");
	}
}
