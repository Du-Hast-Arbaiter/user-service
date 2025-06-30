package com.example.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtil {
    private static final SessionFactory sessionFactory;

    static {
        try {
            String configFile = isTestMode() ? "hibernate-test.cfg.xml" : "hibernate.cfg.xml";

            StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder()
                    .configure(configFile);

            applySystemProperties(registryBuilder);

            StandardServiceRegistry standardRegistry = registryBuilder.build();

            Metadata metadata = new MetadataSources(standardRegistry)
                    .getMetadataBuilder()
                    .build();

            sessionFactory = metadata.getSessionFactoryBuilder().build();
        } catch (Exception ex) {
            System.err.println("Initial SessionFactory creation failed: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }

    private static boolean isTestMode() {
        return "true".equalsIgnoreCase(System.getProperty("test.mode"));
    }

    private static void applySystemProperties(StandardServiceRegistryBuilder builder) {
        if (isTestMode()) {
            String url = System.getProperty("hibernate.connection.url");
            String username = System.getProperty("hibernate.connection.username");
            String password = System.getProperty("hibernate.connection.password");

            if (url != null) builder.applySetting("hibernate.connection.url", url);
            if (username != null) builder.applySetting("hibernate.connection.username", username);
            if (password != null) builder.applySetting("hibernate.connection.password", password);
        }
    }
}