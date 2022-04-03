package io.github.mrs.functionaltest.utils.db;

import java.util.Properties;

/**
 * DatabaseConfig is responsible for extracting a named database configuration from a Properties object.
 */
public class DatabaseConfig {

  private final String name;
  private final Properties properties;
  private static final String PROP_PREFIX = "db.";
  private static final String PROP_SUFFIX_JDBC_URL = ".jdbcUrl";
  private static final String PROP_SUFFIX_USERNAME = ".username";
  private static final String PROP_SUFFIX_PASSWORD = ".password";

  DatabaseConfig(String name, Properties properties) {
    this.name = name;
    this.properties = properties;
  }

  public String getName() {
    return this.name;
  }

  protected Properties getProperties() {
    return this.properties;
  }

  public String getJdbcUrl() {
    return this.properties.getProperty(PROP_PREFIX + this.getName() + PROP_SUFFIX_JDBC_URL);
  }

  public String getUsername() {
    return this.properties.getProperty(PROP_PREFIX + this.getName() + PROP_SUFFIX_USERNAME);
  }

  public String getPassword() {
    return this.properties.getProperty(PROP_PREFIX + this.getName() + PROP_SUFFIX_PASSWORD);
  }
}
