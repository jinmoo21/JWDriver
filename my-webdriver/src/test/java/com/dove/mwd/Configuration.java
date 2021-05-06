package com.dove.mwd;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IAnnotationTransformer;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.ITestAnnotation;
import org.testng.annotations.Listeners;

import io.github.bonigarcia.wdm.WebDriverManager;

@SuppressWarnings("deprecation")
@Listeners(TestListener.class)
public class Configuration implements IAnnotationTransformer {
	private final String screenshotPath = new File("screenshots").getAbsolutePath();
	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected EventWebDriver driver;
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
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;listener class-name="com.dove.mwd.Configuration" /&gt;<br>
	 * &nbsp;&nbsp;&lt;/listeners&gt;
	 * 
	 */
	@SuppressWarnings("rawtypes") 
	@Override
	public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
		annotation.setRetryAnalyzer(TestListener.class);
	}
	
	@BeforeSuite
	public void beforeSuite() {
		WebDriverManager.chromedriver().setup();
		driver = new EventWebDriver(WebDriverFactory.getWebDriver());
		explicitlyWait = new WebDriverWait(driver, Duration.ofSeconds(Util.AJAX_TIMEOUT));
		mainWindow = driver.getWindowHandle();
		driver.manage().window().maximize();
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
			String fileName = screenshotPath + File.separator + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS ")) + result.getName() + ".png";
			FileUtils.copyFile(scrFile, new File(fileName));
		}
	}
	
	@AfterSuite
	public void afterSuite() {
		Optional.ofNullable(driver).ifPresent(d -> d.quit());
	}
}
