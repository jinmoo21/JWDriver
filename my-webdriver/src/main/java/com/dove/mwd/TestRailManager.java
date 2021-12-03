package com.dove.mwd;

import java.util.Optional;

import com.gurock.testrail.APIClient;

import lombok.Getter;

public class TestRailManager {
	@Getter
	private APIClient client;

	public TestRailManager() {
		Optional.ofNullable(System.getProperty("testrail.url")).ifPresent(url -> {
			client = new APIClient(url);
			client.setUser(System.getProperty("testrail.id"));
			client.setPassword(System.getProperty("testrail.pw"));
		});
	}
}
