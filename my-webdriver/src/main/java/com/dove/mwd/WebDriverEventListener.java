package com.dove.mwd;

import java.net.URL;
import java.time.Duration;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.WebDriver.Timeouts;
import org.openqa.selenium.WebDriver.Window;
import org.openqa.selenium.support.events.WebDriverListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebDriverEventListener implements WebDriverListener {	
	@Override
	public void beforeBack(Navigation navigation) {
		log.info("back");
	}
	
	@Override
	public void beforeForward(Navigation navigation) {
		log.info("forward");
	}
	
	@Override
	public void beforeRefresh(Navigation navigation) {
		log.info("refresh");
	}
	
	@Override
	public void beforeTo(Navigation navigation, String url) {
		log.info("to [{}]", url);
	}

	@Override
	public void beforeTo(Navigation navigation, URL url) {
		log.info("to [{}]", url.toString());
	}

	@Override
	public void beforeGet(WebDriver driver, String url) {
		log.info("GET [{}]", url);
	}
	
	@Override
	public void beforeAccept(Alert alert) {
		log.info("[{}]", alert.getText());
	}
	
	@Override
	public void beforeDismiss(Alert alert) {
		log.info("[{}]", alert.getText());
	}
	
	@Override
	public void beforeSendKeys(Alert alert, String text) {
		log.info("[{}: {}]", alert.getText(), text);
	}
	
	@Override
	public void beforeSetSize(Window window, Dimension size) {
		log.info("[{}]", size.toString());
	}
	
	@Override
	public void beforeFullscreen(Window window) {
		log.info("[Window fullscreen.]");
	}
	
	@Override
	public void beforeMaximize(Window window) {
		log.info("[Window maximize.]");
	}
	
	@Override
	public void beforeClick(WebElement element) {
		log.info("[{}{}]", element.getTagName(), element.getLocation());
	}
	
	@Override
	public void beforeFindElement(WebDriver driver, By locator) {
		log.info("[{}]", locator.toString());
	}
	
	@Override
	public void beforeFindElement(WebElement element, By locator) {
		log.info("[{} {}]", element.getTagName(), locator.toString());
	}
	
	@Override
	public void beforeFindElements(WebDriver driver, By locator) {
		log.info("[{} {}]", locator.toString());
	}
	
	@Override
	public void beforeFindElements(WebElement element, By locator) {
		log.info("[{} {}]", element.getTagName(), locator.toString());
	}
	
	@Override
	public void beforeSendKeys(WebElement element, CharSequence... keysToSend) {
		log.info("[{}{}: {}]", element.getTagName(), element.getLocation(), keysToSend);
	}
	
	@Override
	public void beforeClear(WebElement element) {
		log.info("[{}{}]", element.getTagName(), element.getLocation());
	}
	
	@Override
	public void beforeSubmit(WebElement element) {
		log.info("[{}{}]", element.getTagName(), element.getLocation());
	}
	
	@Override
	public void beforeClose(WebDriver driver) {
		log.info("close");
	}
	
	@Override
	public void beforeQuit(WebDriver driver) {
		log.info("quit");
	}
	
	@Override
	public void beforeAddCookie(Options options, Cookie cookie) {
		log.info("[{}]", cookie.toString());
	}
	
	@Override
	public void beforeDeleteCookie(Options options, Cookie cookie) {
		log.info("[{}]", cookie.toString());
	}
	
	@Override
	public void beforeDeleteCookieNamed(Options options, String name) {
		log.info("[{}]", name);
	}
	
	@Override
	public void beforeDeleteAllCookies(Options options) {
		log.info("[All cookies deleted.]");
	}
	
	@Override
	public void beforeSetPosition(Window window, Point position) {
		log.info("[{}]", position.toString());
	}
	
	@Override
	public void beforeExecuteAsyncScript(WebDriver driver, String script, Object[] args) {
		log.info("[{}]", script);
	}
	
	@Override
	public void beforeExecuteScript(WebDriver driver, String script, Object[] args) {
		log.info("[{}]", script);
	}
	
	@Override
	public void beforeSetScriptTimeout(Timeouts timeouts, Duration duration) {
		log.info("[{}]", duration.toString());
	}
	
	@Override
	public void beforePageLoadTimeout(Timeouts timeouts, Duration duration) {
		log.info("[{}]", duration.toString());
	}
	
}
