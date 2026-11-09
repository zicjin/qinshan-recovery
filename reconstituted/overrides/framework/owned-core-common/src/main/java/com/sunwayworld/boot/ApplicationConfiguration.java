/*
 * Reconstructed from supplier class metadata and implemented with public Spring APIs.
 */
package com.sunwayworld.boot;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.i18n.DatabaseDrivenMessageSource;
import com.sunwayworld.framework.thymeleaf.templateresolver.GikamResourceTemplateResolver;
import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.servlet.MultipartConfigElement;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
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
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
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
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.thymeleaf.exceptions.TemplateEngineException;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.Locale;

@Configuration
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
@EnableCaching
public class ApplicationConfiguration extends ApplicationObjectSupport
        implements SchedulingConfigurer {
    @Lazy @Autowired private TransactionManager transactionManager;
    private DataSource dataSource;

    @PostConstruct
    public void initApplicationContext() {
        ApplicationContextHelper.setApplicationContext(getApplicationContext());
    }

    @Bean @Primary
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {
    }

    @Bean
    public ThreadPoolExecutor taskExecutor() {
        return new ThreadPoolExecutor(8, 32, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000));
    }

    @Bean
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

    @Bean @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisCacheWriter redisCacheWriter(RedisConnectionFactory factory) {
        return RedisCacheWriter.nonLockingRedisCacheWriter(factory);
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        return new RedisCacheManager(redisCacheWriter(factory), RedisCacheConfiguration.defaultCacheConfig());
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource());
        factory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(
                "classpath*:com/sunwayworld/**/mapper/**/*-oracle-mapper.xml"));
        factory.setPlugins(new org.apache.ibatis.plugin.Interceptor[]{
                new com.sunwayworld.framework.mybatis.interceptor.MybatisInterceptor(),
                new com.sunwayworld.framework.mybatis.interceptor.MybatisResultSetInterceptor()
        });
        SqlSessionFactory sqlSessionFactory = factory.getObject();
        sqlSessionFactory.getConfiguration().setJdbcTypeForNull(org.apache.ibatis.type.JdbcType.VARCHAR);
        return sqlSessionFactory;
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate() throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory());
    }

    @Bean
    public static MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer configurer = new GikamMapperScannerConfigurer();
        configurer.setBasePackage("com.sunwayworld");
        configurer.setSqlSessionTemplateBeanName("sqlSessionTemplate");
        return configurer;
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() throws java.io.IOException {
        java.nio.file.Path location = com.sunwayworld.framework.io.file.FilePathManager
                .getLocalPath(com.sunwayworld.framework.io.file.FileScope.temp).resolve("multipart");
        java.nio.file.Files.createDirectories(location);
        org.springframework.boot.web.servlet.MultipartConfigFactory factory =
                new org.springframework.boot.web.servlet.MultipartConfigFactory();
        factory.setLocation(location.toString());
        org.springframework.core.env.Environment environment = getApplicationContext().getEnvironment();
        String threshold = environment.getProperty("spring.servlet.multipart.file-size-threshold");
        String maxFile = environment.getProperty("spring.servlet.multipart.max-file-size");
        String maxRequest = environment.getProperty("spring.servlet.multipart.max-request-size");
        factory.setFileSizeThreshold(org.springframework.util.unit.DataSize.parse(threshold == null ? "2MB" : threshold));
        factory.setMaxFileSize(org.springframework.util.unit.DataSize.parse(maxFile == null ? "100MB" : maxFile));
        factory.setMaxRequestSize(org.springframework.util.unit.DataSize.parse(maxRequest == null ? "100MB" : maxRequest));
        return factory.createMultipartConfig();
    }

    @Bean
    public HandlerAdapter servletHandlerAdapter() {
        return new org.springframework.web.servlet.handler.SimpleServletHandlerAdapter();
    }

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(Locale.CHINA);
        resolver.setCookieName("locale");
        return resolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Bean
    public MessageSource messageSource() {
        return new DatabaseDrivenMessageSource(getApplicationContext());
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(DataSource.class)
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public HikariDataSource dataSource(DataSourceProperties properties) {
        HikariDataSource dataSource = properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        dataSource.setPoolName("lims-owned-oracle");
        dataSource.setMinimumIdle(2);
        dataSource.setMaximumPoolSize(8);
        dataSource.setConnectionTimeout(10000L);
        dataSource.setValidationTimeout(3000L);
        dataSource.setInitializationFailTimeout(5000L);
        return dataSource;
    }

    private DataSource dataSource() {
        if (dataSource == null) {
            dataSource = getApplicationContext().getBean(DataSource.class);
        }
        return dataSource;
    }

    @Bean
    public ITemplateResolver gikamTemplateResolver() {
        GikamResourceTemplateResolver resolver = new GikamResourceTemplateResolver(getApplicationContext());
        resolver.setPrefix("classpath:/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCacheable(true);
        return resolver;
    }

    @Bean
    public ErrorPageRegistrar errorPageRegister() {
        return registry -> registry.addErrorPages(
                new ErrorPage(HttpStatus.NOT_FOUND, "/error/thymeleaf"),
                new ErrorPage(TemplateEngineException.class, "/error/thymeleaf"));
    }

    @Bean
    @ConditionalOnMissingBean(HiddenHttpMethodFilter.class)
    @ConditionalOnProperty(prefix = "spring.mvc.hiddenmethod.filter", name = "enabled", matchIfMissing = false)
    public OrderedHiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new OrderedHiddenHttpMethodFilter();
    }

    @Bean
    @Profile("no-tx")
    public TransactionInterceptor txAdvice() {
        RuleBasedTransactionAttribute notSupported = new RuleBasedTransactionAttribute();
        notSupported.setPropagationBehavior(4);
        HashMap<String, TransactionAttribute> txMap = new HashMap<>();
        txMap.put("*", notSupported);
        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
        source.setNameMap(txMap);
        return new TransactionInterceptor(this.transactionManager, source);
    }

    @Bean
    @Profile("no-tx")
    public Advisor txAdviceAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("within(com.sunwayworld..*) and @annotation(org.springframework.transaction.annotation.Transactional)");
        return new DefaultPointcutAdvisor(pointcut, this.txAdvice());
    }
}
