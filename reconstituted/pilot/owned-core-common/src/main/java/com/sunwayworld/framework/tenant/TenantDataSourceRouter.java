/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.tenant;

import com.sunwayworld.framework.tenant.TenantContext;
import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class TenantDataSourceRouter
extends AbstractDataSource {
    private static final Logger logger = LogManager.getLogger(TenantDataSourceRouter.class);
    @Nullable
    private Map<String, Object> targetDataSources = new ConcurrentHashMap<String, Object>();
    @Nullable
    private Map<String, DataSource> resolvedDataSources = new ConcurrentHashMap<String, DataSource>();

    public TenantDataSourceRouter(Object defaultTargetDataSource) {
        this.targetDataSources.put("master", defaultTargetDataSource);
        this.resolve("master", defaultTargetDataSource);
    }

    protected DataSource resolveSpecifiedDataSource(Object dataSource) throws IllegalArgumentException {
        if (dataSource instanceof DataSource) {
            return (DataSource)dataSource;
        }
        throw new IllegalArgumentException("Illegal data source value - only [javax.sql.DataSource] supported: " + dataSource);
    }

    public Map<String, DataSource> getResolvedDataSources() {
        Assert.state(this.resolvedDataSources != null, "DataSources not resolved yet - call afterPropertiesSet");
        return Collections.unmodifiableMap(this.resolvedDataSources);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.determineTargetDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return this.determineTargetDataSource().getConnection(username, password);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return (T)this;
        }
        return this.determineTargetDataSource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this) || this.determineTargetDataSource().isWrapperFor(iface);
    }

    public boolean exists(Object lookupKey) {
        return this.targetDataSources.containsKey(lookupKey);
    }

    public void add(String lookupKey, Object dataSource) {
        this.targetDataSources.put(lookupKey, dataSource);
        this.resolve(lookupKey, dataSource);
    }

    public void remove(Object lookupKey) {
        DataSource dataSource = this.resolvedDataSources.get(lookupKey);
        if (dataSource != null) {
            this.targetDataSources.remove(lookupKey);
            try {
                ((Closeable)((Object)dataSource)).close();
            }
            catch (Exception ex) {
                logger.warn(ex.getMessage(), (Throwable)ex);
            }
        }
    }

    public void print() {
        StringBuilder sb = new StringBuilder("*** Tenant (size: ").append(this.resolvedDataSources.size()).append(") : ");
        this.resolvedDataSources.forEach((k, v) -> sb.append("[").append((String)k).append("]"));
        sb.append(" ***");
        logger.info(sb.toString());
    }

    public DataSource determineTargetDataSource() {
        Assert.notNull(this.resolvedDataSources, "DataSource router not initialized");
        String lookupKey = this.determineCurrentLookupKey();
        DataSource dataSource = this.resolvedDataSources.get(lookupKey);
        if (dataSource == null) {
            throw new IllegalStateException("Cannot determine target DataSource for lookup key [" + lookupKey + "]");
        }
        return dataSource;
    }

    @Nullable
    protected String determineCurrentLookupKey() {
        return TenantContext.getTenant();
    }

    private void resolve(String lookupKey, Object dataSource) {
        this.resolvedDataSources.put(lookupKey, this.resolveSpecifiedDataSource(dataSource));
    }
}
