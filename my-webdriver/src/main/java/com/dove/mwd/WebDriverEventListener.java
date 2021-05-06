package com.dove.mwd;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("deprecation")
public class WebDriverEventListener extends AbstractWebDriverEventListener {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void beforeAlertAccept(WebDriver driver) {
		logger.info("{}", driver.switchTo().alert().getText());
	}

	@Override
	public void beforeAlertDismiss(WebDriver driver) {
		logger.info("{}", driver.switchTo().alert().getText());
	}

	@Override
	public void beforeNavigateTo(String url, WebDriver driver) {
		logger.info("{}", driver.getCurrentUrl(), url);
	}

	@Override
	public void beforeNavigateBack(WebDriver driver) {
		logger.info("{}", driver.getCurrentUrl());
	}

	@Override
	public void beforeNavigateForward(WebDriver driver) {
		logger.info("{}", driver.getCurrentUrl());
	}

	@Override
	public void beforeNavigateRefresh(WebDriver driver) {
		logger.info("{}", driver.getCurrentUrl());
	}

	@Override
	public void beforeFindBy(By by, WebElement element, WebDriver driver) {
		logger.info("{}", by);
	}

	@Override
	public void beforeClickOn(WebElement element, WebDriver driver) {
		logger.info("{}", element);
	}

	@Override
	public void beforeChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
		logger.info("{}", element);
	}

	@Override
	public void beforeScript(String script, WebDriver driver) {
		logger.info("{}", script);
	}

	@Override
	public void beforeSwitchToWindow(String windowName, WebDriver driver) {
		logger.info("{}", windowName);
	}

	@Override
	public void beforeGetText(WebElement element, WebDriver driver) {
		logger.info("{}", element);
	}
}
