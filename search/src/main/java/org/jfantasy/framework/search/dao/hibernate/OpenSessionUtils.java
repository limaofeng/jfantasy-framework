// package org.jfantasy.framework.search.dao.hibernate;
//
// import org.hibernate.FlushMode;
// import org.hibernate.HibernateException;
// import org.hibernate.Session;
// import org.hibernate.SessionFactory;
// import org.jfantasy.framework.spring.SpringBeanUtils;
// import org.springframework.dao.DataAccessResourceFailureException;
// import org.springframework.orm.hibernate5.SessionFactoryUtils;
// import org.springframework.orm.hibernate5.SessionHolder;
// import org.springframework.transaction.support.TransactionSynchronizationManager;
//
// public class OpenSessionUtils {
//
//  private static SessionFactory sessionFactory;
//
//  private OpenSessionUtils() {
//    throw new IllegalStateException("Utility class");
//  }
//
//  private static SessionFactory getSessionFactory() {
//    if (sessionFactory == null) {
//      sessionFactory = SpringBeanUtils.getBeanByType(SessionFactory.class);
//    }
//    return sessionFactory;
//  }
//
//  public static Session openSession() {
//    return openSession(getSessionFactory());
//  }
//
//  public static Session openSession(SessionFactory sf) {
//    Session session = sessionFactory.openSession();
//    try {
//      session.setFlushMode(FlushMode.MANUAL);
//      SessionHolder sessionHolder = new SessionHolder(session);
//      TransactionSynchronizationManager.bindResource(sf, sessionHolder);
//      return session;
//    } catch (HibernateException ex) {
//      closeSession(session);
//      throw new DataAccessResourceFailureException("Could not open Hibernate Session", ex);
//    }
//  }
//
//  public static void closeSession(Session session) {
//    SessionFactory sf = session.getSessionFactory();
//    SessionHolder sessionHolder =
//        (SessionHolder) TransactionSynchronizationManager.unbindResource(sf);
//    SessionFactoryUtils.closeSession(sessionHolder.getSession());
//  }
// }
