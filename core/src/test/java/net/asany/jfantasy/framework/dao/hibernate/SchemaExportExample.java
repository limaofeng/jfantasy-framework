/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.framework.dao.hibernate;

import java.util.Properties;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class SchemaExportExample {
  public static void main(String[] args) {
    Properties settings = new Properties();

    settings.put("hibernate.data_source", "root");
    settings.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
    settings.put("hibernate.hbm2ddl.auto", "update");
    settings.put("hibernate.show_sql", "true");
    settings.put("hibernate.format_sql", "true");

    // 创建 SessionFactory
    StandardServiceRegistry serviceRegistry =
        new StandardServiceRegistryBuilder().applySettings(settings).build();

    SchemaSessionFactory schemaSessionFactory = new SchemaSessionFactory(serviceRegistry);

    String xml = "";

    schemaSessionFactory.addMetadataSource(xml);

    schemaSessionFactory.update();

    SessionFactory sessionFactory = schemaSessionFactory.getSessionFactory();

    // 创建 Session
    try (Session session = sessionFactory.openSession()) {
      // 进行数据库操作...
    }

    // 关闭 SessionFactory
    sessionFactory.close();
    StandardServiceRegistryBuilder.destroy(serviceRegistry);
  }
}
