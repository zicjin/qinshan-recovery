package com.sunwayworld.owned.internal.context;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.jdbc.datasource.DataSourceUtils;

/** Project-owned lifecycle and JDBC bridge, outside the compatibility namespace. */
public final class OwnedApplicationContext {
    private static volatile ConfigurableApplicationContext current;
    private OwnedApplicationContext() {}
    public static final class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext context) { register(context); }
    }
    public static final class FailureListener implements ApplicationListener<ApplicationFailedEvent> {
        public void onApplicationEvent(ApplicationFailedEvent event) { clear(event.getApplicationContext()); }
    }
    public static synchronized void register(final ConfigurableApplicationContext context) {
        if (context == null) throw new IllegalArgumentException("context must not be null");
        current = context;
        context.addApplicationListener(new ApplicationListener<ContextClosedEvent>() {
            public void onApplicationEvent(ContextClosedEvent event) {
                if (event.getApplicationContext() == context) clear(context);
            }
        });
    }
    public static synchronized void clear(ConfigurableApplicationContext context) {
        if (current == context) current = null;
    }
    public static ConfigurableApplicationContext getApplicationContext() { return current; }
    public static <T> T getBean(Class<T> type) {
        ConfigurableApplicationContext context = current;
        return context == null ? null : context.getBean(type);
    }
    public static <T> T getBean(String name, Class<T> type) {
        ConfigurableApplicationContext context = current;
        if (context == null) return null;
        try { return context.getBean(name, type); }
        catch (NoSuchBeanDefinitionException absent) { return null; }
    }
    public static <T> T getBeanIfPresent(Class<T> type) {
        try { return getBean(type); } catch (NoSuchBeanDefinitionException absent) { return null; }
    }
    public static Connection connection(DataSource source) { return DataSourceUtils.getConnection(source); }
    public interface JdbcWork<T> { T apply(Connection connection) throws SQLException; }
    public static <T> T withConnection(DataSource source, JdbcWork<T> work) throws SQLException {
        Connection connection = connection(source);
        try { return work.apply(connection); }
        finally { DataSourceUtils.releaseConnection(connection, source); }
    }
}
