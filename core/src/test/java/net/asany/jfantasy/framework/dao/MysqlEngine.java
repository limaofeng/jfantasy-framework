package net.asany.jfantasy.framework.dao;

import java.sql.*;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.dao.util.JdbcUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WebAppConfiguration
// @ContextConfiguration(locations = {"classpath:backup/testconfig/spring/applicationContext.xml"})
public class MysqlEngine {

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
        log.debug(resultSet.getString("TABLE_NAME") + " \t " + resultSet.getString("ENGINE"));
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
          JdbcUtils.closeConnection(_connection);
        }
      }
    } finally {
      JdbcUtils.closeConnection(connection);
    }
  }
}
