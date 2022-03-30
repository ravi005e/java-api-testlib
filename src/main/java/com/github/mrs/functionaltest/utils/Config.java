package com.github.mrs.functionaltest.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class Config {

	private static String baseURL;
	private static int basePort;  // this gets pulled out of the URL so we can configure RestAssured

	private static String environment;

	private static String clientId;
	private static String clientSecret;

	public static boolean DEBUG = false;
	protected static final Logger LOGGER = LogManager.getLogger(Config.class);

	public static void assign(String environment, Properties props, boolean isDebugging) {
		LOGGER.info("Assigning properties for test run:");
		if (! StringUtils.isBlank(environment)) {
			environment = environment;
		}
		baseURL = getProperty(props,"base.url");
		clientId = getProperty(props, "client.id");
		clientSecret = getProperty(props, "client.secret");

		try {
			URL url = new URL(baseURL);
			if (url.getPort() == -1) {
				basePort = "http".equals(url.getProtocol()) ? 80 : 443;
			} else {
				basePort = url.getPort();
			}
		} catch (MalformedURLException e) {
			basePort = 80;
		}

		LOGGER.info("  BASE_URL now: {}", baseURL);

		DEBUG = isDebugging;
		LOGGER.info("  Debugging on? " + DEBUG);

		if (DEBUG) {
			// slf4j doesn't have a way to change this at runtime,
			// so we modify the underlying log4j logger instead
			Configurator.setRootLevel(Level.DEBUG);
		}
	}

	protected static String getProperty (Properties properties, String key) {
		final String property = properties.getProperty(key);
		if (property == null) {
			throw new RuntimeException(MessageFormat.format("Requested key [{0}] missing in properties file.", key));
		}
		return property;
	}

	/**
	 * @return port associated with base URL, 80 if unspecified http, 443 if unspecified https
	 */
	public static int port() {
		return basePort;
	}

	public static String url(String path) {
		String request = baseURL + path;
		LOGGER.info("  REQUEST_URL now: {}", request);

		return request;
	}

	public static String getEnvironment() {
		return environment;
	}

	public static String getClientId() {
		return clientId;
	}

	public static String getClientSecret() {
		return clientSecret;
	}

}
