package com.dove.mwd;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasePage<T> extends LoadableComponent<BasePage<T>> {
	protected final WebDriver driver;
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public BasePage(WebDriver driver) {
		this.driver = driver;
	}

	public abstract String getUrl();
	
	@Override
	protected void load() {
		driver.get(getUrl());
	}

	@Override
	protected void isLoaded() throws Error {
		assertThat(driver.getCurrentUrl()).startsWith(getUrl());
	}
}
