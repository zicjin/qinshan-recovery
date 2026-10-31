package com.sunwayworld.framework.utils;

import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Owned replacement for the supplier's JNI DataSource factory.
 *
 * <p>The factory is deliberately limited to an explicitly supplied JDBC
 * driver, URL, user, and password. It does not read license, machine, or
 * supplier-specific configuration and it does not open a connection while
 * constructing the datasource.</p>
 */
public abstract class DataSourceUtils {
    public DataSourceUtils() {}

    public static final DataSource dataSource(String driverClassName, String url,
            String username, String password) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("JDBC url must not be empty");
        }
        DriverManagerDataSource source = new DriverManagerDataSource();
        if (driverClassName != null && !driverClassName.trim().isEmpty()) {
            source.setDriverClassName(driverClassName.trim());
        }
        source.setUrl(url);
        source.setUsername(username);
        source.setPassword(password);
        return source;
    }
}
