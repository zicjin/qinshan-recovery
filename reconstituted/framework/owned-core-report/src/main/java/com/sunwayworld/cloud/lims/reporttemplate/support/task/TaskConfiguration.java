/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.task;

import java.util.concurrent.ThreadPoolExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class TaskConfiguration {
    @Autowired
    private Environment environment;
    private static final Logger logger = LogManager.getLogger(TaskConfiguration.class);

    @Bean(value={"limsSqlDataResolverExecutor"})
    public ThreadPoolTaskExecutor limsSqlDataResolverExecutor() {
        String corePoolSize = this.environment.getProperty("limsSqlDataResolverExecutor.CorePoolSize", "1");
        String maxPoolSize = this.environment.getProperty("limsSqlDataResolverExecutor.MaxPoolSize", "10");
        String queueCapacity = this.environment.getProperty("limsSqlDataResolverExecutor.QueueCapacity", "500");
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(Integer.parseInt(corePoolSize));
        taskExecutor.setMaxPoolSize(Integer.parseInt(maxPoolSize));
        taskExecutor.setQueueCapacity(Integer.parseInt(queueCapacity));
        taskExecutor.setThreadNamePrefix("limsSqlDataResolverExecutor");
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy(){

            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                logger.error("limsSqlDataResolverExecutor rejectedExecution");
            }
        });
        return taskExecutor;
    }
}
