package org.jfantasy.framework.dao;

import java.sql.*;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.dao.util.JdbcUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:backup/testconfig/spring/applicationContext.xml"})
public class MysqlEngine {

  private static final Log LOG = LogFactory.getLog(MysqlEngine.class);

  @Autowired private DataSource dataSource;

  private static final String SCHEMA = "website";

  @Test
  public void change() throws SQLException {
    Connection connection = dataSource.getConnection();
    try {
      // 查询 非 InnoDB 的表
      Statement statement = connection.createStatement();
      ResultSet resultSet =
          statement.executeQuery(
              "select TABLE_NAME,ENGINE from information_schema.TABLES where TABLE_SCHEMA='"
                  + SCHEMA
                  + "' and ENGINE<>'InnoDB'");
      while (resultSet.next()) {
        LOG.debug(resultSet.getString("TABLE_NAME") + " \t " + resultSet.getString("ENGINE"));
        Connection _connection = dataSource.getConnection();
        try {
          PreparedStatement preparedStatement =
              connection.prepareStatement(
                  "alter table "
                      + SCHEMA
                      + "."
                      + resultSet.getString("TABLE_NAME")
                      + " engine=InnoDB;");
          preparedStatement.executeUpdate();
        } finally {
          JdbcUtil.closeConnection(_connection);
        }
      }
    } finally {
      JdbcUtil.closeConnection(connection);
    }
  }
}
