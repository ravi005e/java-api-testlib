package com.github.mrs.functionaltest;

import com.github.mrs.functionaltest.utils.Config;
import com.github.mrs.functionaltest.utils.HttpCaller;
import com.google.gson.JsonElement;

import io.restassured.RestAssured;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;

public abstract class FunctionalTest {
	protected static final Logger LOGGER = LogManager.getLogger(FunctionalTest.class);

	/**
	 * Run global setup routines, including pulling the environment these tests will run against
	 * from the environment variable <tt>FUNCTIONAL_ENV</tt> or the system property <tt>functional.env</tt>
	 * (in that order).
	 *
	 * Note that these routines will run before any <tt>@BeforeSuite</tt>-decorated methods in subclasses,
	 * so if your test suite defines its own setup you can be sure this parent one will run before yours.
	 *
	 */
	@BeforeSuite(alwaysRun = true)
	public void setup() {
		String environment = envOrProperty("FUNCTIONAL_ENV", "functional.env");
		if (StringUtils.isBlank(environment)) {
			throw new IllegalArgumentException("No test environment provided in either [Env: FUNCTIONAL_ENV] or [Prop: functional.env]");
		}
		Properties props = readProperties(environment);
	        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		boolean isDebug = "true".equalsIgnoreCase(envOrProperty("FUNCTIONAL_DEBUG", "functional.debug"));
		if (isDebug) {
			RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
		}
		Config.assign(environment, props, isDebug);
        assignExtraProperties(props);
		HttpCaller.setup(Config.port());
	}

	@AfterTest(alwaysRun = true)
	public void cleanup() throws Exception {
		LOGGER.info("Cleaning up created data...");
		Cleanup.run();
		LOGGER.info("OK: Cleanup done");
	}

	protected String envOrProperty(String environmentVariableName, String propertyName) {
		String value = System.getenv(environmentVariableName);
		if (StringUtils.isBlank(value)) {
			value = System.getProperty(propertyName);
		}
		return value;
	}

	public Properties readProperties(String environment)  {
		Properties props = new Properties();
		String propertiesName = String.format("env-%s.properties", environment);
		try {
			InputStream in = getClass().getClassLoader().getResourceAsStream(propertiesName);
			props.load(in);
			LOGGER.info("OK: Loaded properties from classpath at {}", propertiesName);

			String extraPropertyFileName = getExtraPropertyFileName(environment);
			if (extraPropertyFileName != null) {
				Properties extraProps = new Properties();
				in = getClass().getClassLoader().getResourceAsStream(extraPropertyFileName);
				extraProps.load(in);
				LOGGER.info("OK: Loaded extra properties from classpath at {}", extraProps);
				props.putAll(extraProps);
			}

			return props;
		} catch (IOException e) {
			throw new RuntimeException(String.format(
					"Expected properties file '%s' does not exist on the classpath.", propertiesName));
		}
	}

	public void pauseSleep(int mills, String reason) {
	    if(StringUtils.isBlank(reason)) {
	      throw new IllegalArgumentException(
	          "a reason for pause/sleep must be specified");
	    }

	    LOGGER.info(
	        reason + "[" + mills + "] ms");

	    try {
	      Thread.sleep(mills);
	    } catch (InterruptedException e) {
	      LOGGER.error(
	          "Caught InterruptedException while pause/sleep: "
	              + e.getMessage(), e);
	    }
	}

	protected String getExtraPropertyFileName(String environment) {
		//To be overridden, If your test requires extra properties to be read in
		return null;
	}

	protected void assignExtraProperties(Properties props) {
		//To be overridden, implement assignExtraProperties
		// to do something with those properties
	}

	public List<String> stringToList(String string, String delimiter) {
		return Stream.of(string.split(delimiter))
				.map(String::trim).collect(Collectors.toList());
	}

	public String[] stringToArray(String string, String delimiter) {
		return string.split(delimiter);
	}

	public String removeQuotes(JsonElement jsonElement) {
		return jsonElement.toString().replaceAll("\"", "");
	}

}
