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

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator;

public class SchemaSessionFactory {

  private SessionFactory sessionFactory;
  private MetadataSources metadataSources;

  private final StandardServiceRegistry serviceRegistry;

  public SchemaSessionFactory(StandardServiceRegistry serviceRegistry) {
    this.serviceRegistry = serviceRegistry;
    this.metadataSources = new MetadataSources(serviceRegistry);
    this.sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
  }

  public void addMetadataSource(String xml) {
    metadataSources.addInputStream(new ByteArrayInputStream(xml.getBytes()));
    Metadata metadata = metadataSources.buildMetadata();

    // 创建一个配置值的 Map
    Map<String, Object> configurationValues = new HashMap<>();
    SchemaManagementToolCoordinator.process(metadata, serviceRegistry, configurationValues, null);
  }

  public void update() {
    Metadata metadata = metadataSources.buildMetadata();
    this.sessionFactory = metadata.buildSessionFactory();

    this.metadataSources = new MetadataSources(serviceRegistry);

    SessionFactoryOptions sessionFactoryOptions =
        ((SessionFactoryImplementor) sessionFactory).getSessionFactoryOptions();
    //    sessionFactoryOptions.applyInterceptor(new SystemFieldFillInterceptor());
  }

  public Session getCurrentSession() {
    return sessionFactory.getCurrentSession();
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }
}
