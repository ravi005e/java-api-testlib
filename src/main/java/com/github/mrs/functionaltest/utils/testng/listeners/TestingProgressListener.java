package com.github.mrs.functionaltest.utils.testng.listeners;

import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestingProgressListener extends TestListenerAdapter {

	private static final Logger LOGGER = LogManager.getLogger(TestingProgressListener.class);

	@Override
	public void onTestSuccess(ITestResult iTestResult) {
		out("  OK", iTestResult);
	}

	@Override
	public void onTestFailure(ITestResult iTestResult) {
		out("FAIL", iTestResult);
	}

	@Override
	public void onTestSkipped(ITestResult iTestResult) {
		out("SKIP", iTestResult);
	}

	private void out(String status, ITestResult result) {
		long elapsed = result.getEndMillis() - result.getStartMillis();
		String shortClassName = result.getTestClass().getName().replaceFirst("^.*\\.", "");
		String errorMessage = result.getThrowable() != null ? result.getThrowable().getMessage() : "";
		String msg = String.format(
				"%s: %s.%s (%d ms) %s", status, shortClassName, result.getMethod().getMethodName(), elapsed, errorMessage
				);
		LOGGER.info(msg);
	}
}
