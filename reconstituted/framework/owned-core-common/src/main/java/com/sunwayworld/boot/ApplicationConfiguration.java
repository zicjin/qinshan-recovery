/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.boot;

import java.util.HashMap;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.PostConstruct;
import javax.servlet.MultipartConfigElement;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.boot.web.servlet.filter.OrderedHiddenHttpMethodFilter;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.thymeleaf.exceptions.TemplateEngineException;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
@EnableCaching
public class ApplicationConfiguration
extends ApplicationObjectSupport
implements SchedulingConfigurer {
    @Lazy
    @Autowired
    private TransactionManager transactionManager;

    @Override
    protected native void initApplicationContext();

    @PostConstruct
    public native void init();

    @Bean
    @Primary
    public native RestTemplate restTemplate(RestTemplateBuilder var1);

    @Override
    public native void configureTasks(ScheduledTaskRegistrar var1);

    @Bean
    public native ThreadPoolExecutor taskExecutor();

    @Bean
    public native AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler();

    @Bean
    @Primary
    public native RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory var1);

    @Bean
    public native RedisCacheWriter redisCacheWriter(RedisConnectionFactory var1);

    @Bean
    public native CacheManager cacheManager(RedisConnectionFactory var1);

    @Bean
    public native SqlSessionFactory sqlSessionFactory();

    @Bean
    public native SqlSessionTemplate sqlSessionTemplate();

    @Bean
    public static native MapperScannerConfigurer mapperScannerConfigurer();

    @Bean
    public native MultipartConfigElement multipartConfigElement();

    @Bean
    public native HandlerAdapter servletHandlerAdapter();

    @Bean
    public native LocaleResolver localeResolver();

    @Bean
    public native LocaleChangeInterceptor localeChangeInterceptor();

    @Bean
    public native MessageSource messageSource();

    @Bean
    @Profile(value={"tenant"})
    @ConfigurationProperties(prefix="spring.datasource.hikari")
    public native DataSource dataSource(DataSourceProperties var1);

    @Bean
    public native ITemplateResolver gikamTemplateResolver();

    @Bean
    public ErrorPageRegistrar errorPageRegister() {
        return new ErrorPageRegistrar(){

            @Override
            public void registerErrorPages(ErrorPageRegistry registry) {
                registry.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/error/thymeleaf"), new ErrorPage(TemplateEngineException.class, "/error/thymeleaf"));
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(value={HiddenHttpMethodFilter.class})
    @ConditionalOnProperty(prefix="spring.mvc.hiddenmethod.filter", name={"enabled"}, matchIfMissing=false)
    public OrderedHiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new OrderedHiddenHttpMethodFilter();
    }

    @Bean
    @Profile(value={"no-tx"})
    public TransactionInterceptor txAdvice() {
        RuleBasedTransactionAttribute notSupported = new RuleBasedTransactionAttribute();
        notSupported.setPropagationBehavior(4);
        HashMap<String, TransactionAttribute> txMap = new HashMap<String, TransactionAttribute>();
        txMap.put("*", notSupported);
        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
        source.setNameMap(txMap);
        return new TransactionInterceptor(this.transactionManager, (TransactionAttributeSource)source);
    }

    @Bean
    @Profile(value={"no-tx"})
    public Advisor txAdviceAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("within(com.sunwayworld..*) and @annotation(org.springframework.transaction.annotation.Transactional)");
        return new DefaultPointcutAdvisor(pointcut, this.txAdvice());
    }
}
