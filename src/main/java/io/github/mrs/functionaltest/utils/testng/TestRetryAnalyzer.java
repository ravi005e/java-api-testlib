package io.github.mrs.functionaltest.utils.testng;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestRetryAnalyzer implements IRetryAnalyzer {
    int retryAttempt = 0;
    int retryAttempts = 3;

	private static final Logger LOGGER = LogManager.getLogger(TestRetryAnalyzer.class);

    @Override
    public boolean retry(ITestResult result) {
        if (retryAttempt < retryAttempts) {
        	retryAttempt++;
        	LOGGER.info("Retry #" + retryAttempt + " for test: " + result.getMethod().getMethodName() + " with status\n" +
            	getResultStatusName(result.getStatus()) + ", on \nthread: " + Thread.currentThread().getName());
            return true;
        }
        return false;
    }

    private String getResultStatusName(int status) {
    	String resultName = null;
    	if(status==1)
    		resultName = "SUCCESS";
    	if(status==2)
    		resultName = "FAILURE";
    	if(status==3)
    		resultName = "SKIP";
    	return resultName;
    }
}
