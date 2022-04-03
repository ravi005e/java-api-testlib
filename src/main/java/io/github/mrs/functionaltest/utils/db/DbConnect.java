package io.github.mrs.functionaltest.utils.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DbConnect {
	private static final Logger LOGGER = LogManager.getLogger(DbConnect.class);

  private static final ConcurrentMap<String, DbConnect> connectionsByName =
      new ConcurrentHashMap<String, DbConnect>(5);

  private Connection connection = null;

  private DbConnect(String jdbcUrl, String username, String password) {
    try {
      Class.forName("org.postgresql.Driver");
      connection = DriverManager.getConnection(jdbcUrl, username, password);
      LOGGER.info("created connection to " + jdbcUrl +
          " with credentials: " + username + " / " + password);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public Connection getConnection(){
    return connection;
  }


  /**
   * Get a shared instance of DbConnectOnlyOnce that works for the specified config.  Connections
   * are created lazily and may be released via the release methods.
   *
   * @param config
   * @return a shared DbConnectOnlyOnce instance for DatabaseConfig
   */
  public static DbConnect getInstance(DatabaseConfig config) {
    if (!connectionsByName.containsKey(config.getName())) {
      DbConnect dbConnectOnlyOnce = new DbConnect(config.getJdbcUrl(), config.getUsername(), config.getPassword());
      connectionsByName.putIfAbsent(config.getName(), dbConnectOnlyOnce);
    }
    return connectionsByName.get(config.getName());
  }


  /**
   * Release an instance of DbConnectOnlyOnce that is held in the shared conn pool.
   *
   * @param instance is the DbConnectOnlyOnce to release
   * @return the instance that was released, if any; null if none was found
   */
  public static DbConnect releaseInstance(DbConnect instance) {
    for (Map.Entry<String, DbConnect> entry : connectionsByName.entrySet()) {
      if (entry.getValue() == instance) {
        DbConnect removedConn = connectionsByName.remove(entry.getKey());
        removedConn.safeClose();
        return removedConn;
      }
    }

    return null;
  }

  /**
   * Executes a read-only query and returns the ResultSet.
   *
   * @param query is the query to be executed in db
   * @return ResultSet . The user is responsible in closing the result set to avoid resource leak using (resultSet.close()).
   */
  public ResultSet executeQuery(String query) {
    ResultSet rs = null;
    try {
      Statement stmt = getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
          ResultSet.CONCUR_READ_ONLY
          );
      rs = stmt.executeQuery(query);

    } catch (SQLException e) {
      LOGGER.info("Exception is :" + e.getMessage());
    }
    return rs;
  }

  /**
   * Updates the db
   *
   * @param query is the query to be updated in db
   * @return ResultSet . The user is responsible in closing the result set to avoid resource leak using (resultSet.close()).
   * @throws SQLException
   */
  public int executeUpdate(String query) throws SQLException {
    int rs = 0;
    Statement stmt = null;
    try {
      stmt = getConnection().createStatement();
      rs = stmt.executeUpdate(query);
      LOGGER.info("Record is updated");
    } catch (SQLException e) {
      LOGGER.info("Exception is :" + e.getMessage());
    } finally {
      stmt.close();
    }
    return rs;
  }

  public int getNumberOfRows(String query) throws SQLException {

    ResultSet resultSet = null;

    int count = 0;

    try {
      resultSet = executeQuery(query);
      resultSet.last();
      count = resultSet.getRow();
      resultSet.beforeFirst();
    } catch (SQLException e) {
      LOGGER.error("Error getting row count", e);
    } finally {
      resultSet.close();
    }
    return count;

  }

  /**
   * Release this instance from the shared pool and close database-related resources; once
   * released this instance will thrown away as it is no longer usable.
   *
   * @throws Exception
   */
  public void release() throws Exception {
    DbConnect.releaseInstance(this);
    safeClose();
  }

  /**
   * Close the java.sql.Connection encapsulated by DbConnectOnlyOnce; swallows SQLException.
   */
  private void safeClose() {
    try {
      getConnection().close();
    } catch (SQLException e) {
      LOGGER.warn("exception encountered while closing connection", e);
    }
  }

}
