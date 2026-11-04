package com.sunwayworld.framework.utils;

import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public abstract class DataSourceUtils {
    private DataSourceUtils() {
    }

    public static final DataSource dataSource(String driverClassName, String url, String username, String password) {
        if (url == null || url.trim().isEmpty()) throw new IllegalArgumentException("JDBC url must not be empty");
        DriverManagerDataSource source = new DriverManagerDataSource();
        if (driverClassName != null && !driverClassName.trim().isEmpty()) source.setDriverClassName(driverClassName.trim());
        source.setUrl(url);
        source.setUsername(username);
        source.setPassword(password);
        return source;
    }
}
