/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.database.monitor;

import com.sunwayworld.framework.tenant.TenantDataSourceRouter;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

@Repository
public class DataSourceMonitor {
    private static Logger log = LoggerFactory.getLogger(DataSourceMonitor.class);
    @Autowired
    private DataSource dataSource;
    @Value(value="${sunway.monitor.datasource.log.enabled:true}")
    private boolean enabled;

    @Scheduled(fixedDelay=10000L)
    public void schedule() {
        if (this.dataSource instanceof TenantDataSourceRouter) {
            TenantDataSourceRouter tdsr = (TenantDataSourceRouter)this.dataSource;
            tdsr.getResolvedDataSources().forEach((k, v) -> {
                HikariDataSource hds = (HikariDataSource)v;
                if (this.enabled) {
                    log.info("\u79df\u6237\uff1a{} *** \u6700\u5c0f\u8fde\u63a5\u6570\uff1a{} \u6700\u5927\u8fde\u63a5\u6570\uff1a{} -- \u5f53\u524d\u8fde\u63a5\u6570\uff1a{} \u5f53\u524d\u6d3b\u8dc3\u6570\uff1a{} ***", k, hds.getMinimumIdle(), hds.getMaximumPoolSize(), hds.getHikariPoolMXBean() != null ? hds.getHikariPoolMXBean().getTotalConnections() : 0, hds.getHikariPoolMXBean() != null ? hds.getHikariPoolMXBean().getActiveConnections() : 0);
                }
            });
        } else {
            HikariDataSource hds = (HikariDataSource)this.dataSource;
            if (this.enabled) {
                log.info("*** \u6700\u5c0f\u8fde\u63a5\u6570\uff1a{} \u6700\u5927\u8fde\u63a5\u6570\uff1a{} -- \u5f53\u524d\u8fde\u63a5\u6570\uff1a{} \u5f53\u524d\u6d3b\u8dc3\u6570\uff1a{} ***", hds.getMinimumIdle(), hds.getMaximumPoolSize(), hds.getHikariPoolMXBean().getTotalConnections(), hds.getHikariPoolMXBean().getActiveConnections());
            }
        }
    }
}
