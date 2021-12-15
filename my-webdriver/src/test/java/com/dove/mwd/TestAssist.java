package com.dove.mwd;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v96.emulation.Emulation;
import org.openqa.selenium.devtools.v96.log.Log;
import org.openqa.selenium.devtools.v96.network.Network;
import org.openqa.selenium.devtools.v96.network.model.ConnectionType;
import org.openqa.selenium.devtools.v96.network.model.Headers;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.IAnnotationTransformer;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.ITestAnnotation;
import org.testng.annotations.Listeners;
import org.testng.annotations.Parameters;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Listeners(TestListener.class)
public class TestAssist implements IAnnotationTransformer {
	private DeviceInfo device;
	private DevTools devtools;
	protected WebDriver driver;
	protected WebDriverWait explicitlyWait;
	protected String mainWindow;
	protected SoftAssertions softly;

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface TestRail {
		String id() default "";
	}
	
	/**
	 * To use RetryAnalyzer, add this code in 'testng.xml'.<br>
	 * <br>
	 * &nbsp;&nbsp;&lt;listeners&gt;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;listener class-name="com.dove.mwd.WebDriverConfiguration" /&gt;<br>
	 * &nbsp;&nbsp;&lt;/listeners&gt;
	 * 
	 */
	@Override
	public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
		annotation.setRetryAnalyzer(TestListener.class);
	}
	
	/**
	 * To use AbstractDriverOptions, override this method in class and overload getWebDriver().
	 */
	@Parameters({"app", "appActivity", "phase"})
	@BeforeSuite
	public void beforeSuite(@org.testng.annotations.Optional("") String app,
			@org.testng.annotations.Optional("") String appActivity,
			@org.testng.annotations.Optional("real") String phase) {
		try {
			Optional.ofNullable(device).ifPresent(d -> {
				if (app.isEmpty()) { // Mobile Web
					d.getCapabilities().setCapability("browserName", (d.isAndroid() ? "Chrome" : " Safari"));
				} else { // Mobile Application
					d.getCapabilities().setCapability((d.isAndroid() ? "appPackage" : "bundleId"), app);
					if (d.isAndroid()) { // MainActivity
						d.getCapabilities().setCapability("appActivity", appActivity);
					}
				}
			});
			WebDriver original = device == null ? WebDriverFactory.getWebDriver(/* options here */) :
					WebDriverFactory.getWebDriver(device.getCapabilities());
			driver = new EventFiringDecorator(new WebDriverEventListener()).decorate(original);
			if (original instanceof ChromeDriver) {
				devtools = ((ChromeDriver) original).getDevTools();
				devtools.createSession();
				log.info("DevTools session created.");
			}
		} catch (MalformedURLException e) {
			log.error("{}", e.getMessage());
		}
	}
	
	@BeforeTest
	public void beforeTest() {
		Optional.ofNullable(driver).ifPresent(d -> {
			explicitlyWait = new WebDriverWait(d, Duration.ofSeconds(Utils.AJAX_TIMEOUT));
			mainWindow = d.getWindowHandle();
			d.manage().window().maximize();
		});
	}

	@BeforeMethod
	public void beforeMethod(ITestContext context, Method method) throws NoSuchMethodException {
		softly = new SoftAssertions();
		Optional.ofNullable(getClass().getMethod(method.getName()).getAnnotation(TestRail.class)).map(TestRail::id).ifPresentOrElse(id -> context.setAttribute("id", id), () -> context.setAttribute("id", null));
	}
	
	@AfterMethod
	public void afterMethod(ITestResult result) throws IOException {
		if (!result.isSuccess()) {
			TakesScreenshot scrshot = (TakesScreenshot) driver;
			File scrFile = scrshot.getScreenshotAs(OutputType.FILE);
			String fileName = Utils.SCREENSHOT_FOLDER + File.separator + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS ")) + result.getName() + ".png";
			FileUtils.copyFile(scrFile, new File(fileName));
		}
	}
	
	@AfterSuite(alwaysRun = true)
	public void afterSuite() {
		Optional.ofNullable(driver).ifPresent(WebDriver::quit);
	}
	
	/**
	 * Console logs will be shown.
	 */
	public void captureConsoleLog() {
		Optional.ofNullable(devtools).ifPresent(d -> {
			d.send(Log.enable());
			d.addListener(Log.entryAdded(), entry -> log.info(entry.getText()));
			log.info("Capturing console logs start.");
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
					log.error("Response Error:\nName: {}\nHTTP Status: {}\nType: {}", resp.getResponse().getUrl(), resp.getResponse().getStatus(), resp.getType());
				}
			});
			log.info("Capturing HTTP status code 4XX and 5XX responses start.");
		});
	}
	
	/**
	 * Clear listeners that capturing console log and capturing error response.
	 */
	public void clearListeners() {
		Optional.ofNullable(devtools).ifPresent(d -> {
			d.clearListeners();
			log.info("Browser cache and cookies cleared.");
		});
	}
	
	/**
	 * Add custom header.
	 * 
	 * @param key header key
	 * @param value header value
	 */
	public void addExtraHeader(String key, Object value) {
		Optional.ofNullable(devtools).ifPresent(d -> {
			d.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
			d.send(Network.setExtraHTTPHeaders(new Headers(Map.of(key, value))));
			log.info("Extra Header value added: ({}, {})", key, value);
		});
    }
	
	/**
	 * Clear browser cache and cookies.
	 */
	public void clearBrowserCacheAndCookies() {
		Optional.ofNullable(devtools).ifPresent(d -> {
			d.send(Network.clearBrowserCache());
			d.send(Network.clearBrowserCookies());
			log.info("Browser cache and cookies cleared.");
		});
	}
	
	/**
	 * Set Fake GPS.
	 * 
	 * @param latitude latitude
	 * @param longitude longitude
	 * @param accuracy accuracy
	 */
	public void setGeolocation(Number latitude, Number longitude, Number accuracy) {
		Optional.ofNullable(devtools).ifPresent(d -> {
			d.send(Emulation.setGeolocationOverride(
					Optional.of(latitude),
					Optional.of(longitude),
					Optional.of(accuracy)));
			log.info("Your geolocation: ({}, {}, {})", latitude, longitude, accuracy);
		});
	}
	
	/**
	 * Clear geolocation.
	 */
	public void clearGeoLocation() {
		Optional.ofNullable(devtools).ifPresent(d -> {
			d.send(Emulation.clearGeolocationOverride());
			log.info("Overriding geolocation cleared.");
		});
	}
	
	/**
	 * Set network emulation.
	 * 
	 * @param profile profile
	 */
	public void setNetworkEmulation(NetworkProfile profile) {
		Optional.ofNullable(devtools).ifPresent(d -> {
			d.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
			d.send(Network.emulateNetworkConditions(profile.isOffine(), profile.getLatency(), profile.getDownload(), profile.getUpload(), Optional.of(ConnectionType.OTHER)));
			log.info("Network emulation start: {}", profile.getValue());
		});
	}

	public void setUserAgent(String userAgent) {
		Optional.ofNullable(devtools).ifPresent(d -> {
			d.send(Network.setUserAgentOverride(userAgent, Optional.empty(), Optional.empty(), Optional.empty()));
			log.info("UserAgent overrided: {}", userAgent);
		});
	}
	
	/**
	 * Clear all listeners and close session of DevTools.
	 */
	public void closeDevTools() {
		Optional.ofNullable(devtools).ifPresent(d -> {
			d.close();
			log.info("DevTools closed.");
		});
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
			new WebDriverWait(driver, Duration.ofSeconds(Utils.AJAX_TIMEOUT)).until(jQueryLoadFinished());
		} catch (NotDefinedException e) {
			log.warn("{}: {}", e.getMessage(), driver.getCurrentUrl());
		}
		try {
			new WebDriverWait(driver, Duration.ofSeconds(Utils.AJAX_TIMEOUT)).until(angularLoadFinished());
		} catch (NotDefinedException e) {
			log.warn("{}: {}", e.getMessage(), driver.getCurrentUrl());
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
	 * Click element using javascript instead of webdriver method.
	 * 
	 * @param element element
	 */
	public void clickUsingScript(WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
	}
	
	/**
	 * Get name of switched frame/iframe.
	 * 
	 * @return frame name
	 */
	public String getCurrentFrameName() {
		try {
			return ((JavascriptExecutor) driver).executeScript("return window.frameElement.name").toString();
		} catch (WebDriverException e) {
			if (e.getMessage().contains("Cannot read property 'name' of null")) {
				log.info("No frame.");
			}
			return null;
		}
	}
	
	/**
	 * Scroll to (0, 0)
	 */
	public void scrollToTop() {
		((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0)");
	}
}
