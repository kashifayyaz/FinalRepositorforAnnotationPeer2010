/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kashif
 */

import org.hibernate.*;
import org.hibernate.cfg.Configuration;

public class SessionHandler {


  public Session getSession() {
    return sessionFactory.openSession();
  }

  private static final SessionFactory sessionFactory;

  static {
    try {
      // Create the SessionFactory from hibernate.cfg.xml
      sessionFactory = new Configuration().configure().buildSessionFactory();
    } catch (HibernateException x) {
      System.err.println("Initial SessionFactory creation failed. " + x);
      throw new ExceptionInInitializerError(x);
    }
  }

  public static SessionFactory getSessionFactory() {
    return sessionFactory;
  }
}
