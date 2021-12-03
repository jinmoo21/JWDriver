package com.dove.mwd;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.testng.IRetryAnalyzer;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.gurock.testrail.APIClient;
import com.gurock.testrail.APIException;
import com.gurock.testrail.Status;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestListener implements ITestListener, IRetryAnalyzer {
	private APIClient client = new TestRailManager().getClient();
	private int count = 1;
	
	@Override
	public void onTestStart(ITestResult result) {
		log.info("Test start: {}", result.getName());
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		Optional.ofNullable(client).ifPresent(c -> Optional.ofNullable(result.getTestContext().getAttribute("id")).map(Object::toString).ifPresent(id -> {
			try {
				c.sendPost("add_result/" + id, Map.of("status_id", Status.PASSED));
			} catch (IOException | APIException e) {
				log.error(e.getMessage());
			}
		}));
		log.info("Test passed: {}", result.getName());
	}

	@Override
	public void onTestFailure(ITestResult result) {
		Optional.ofNullable(client).ifPresent(c -> Optional.ofNullable(result.getTestContext().getAttribute("id")).map(Object::toString).ifPresent(id -> {
			try {
				c.sendPost("add_result/" + id, Map.of("comment", result.getThrowable().toString(), "status_id", Status.FAILED));
			} catch (IOException | APIException e) {
				log.error(e.getMessage());
			}
		}));
		log.error("Test failed: {}", result.getName());
		log.error("{}", result.getThrowable().toString());
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		Optional.ofNullable(client).ifPresent(c -> Optional.ofNullable(result.getTestContext().getAttribute("id")).map(Object::toString).ifPresent(id -> {
			try {
				c.sendPost("add_result/" + id, Map.of("status_id", Status.UNTESTED));
			} catch (IOException | APIException e) {
				log.error(e.getMessage());
			}
		}));
		log.warn("Test skipped/untested: {}", result.getName());
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStart(ITestContext context) {
		log.info("TestRail API Binding: {}", Optional.ofNullable(client).isPresent());
	}

	@Override
	public void onFinish(ITestContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean retry(ITestResult result) {
		if (++count <= Utils.RETRY_COUNT) {
			log.info("{}: Retesting #{}/{}", result.getName(), count, Utils.RETRY_COUNT);
			return true;
		}
		return false;
	}
}
