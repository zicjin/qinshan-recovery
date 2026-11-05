/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld;

import com.alibaba.fastjson.serializer.DoubleSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.sunwayworld.framework.utils.SystemUtils;
import java.io.File;
import java.lang.reflect.Type;
import java.util.function.Function;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Import(value={ServletWebServerFactoryAutoConfiguration.class, DispatcherServletAutoConfiguration.class, WebMvcAutoConfiguration.class, ErrorMvcAutoConfiguration.class, HttpEncodingAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class, JacksonAutoConfiguration.class, JmxAutoConfiguration.class, MultipartAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class, ThymeleafAutoConfiguration.class, RedisAutoConfiguration.class, DataSourceAutoConfiguration.class, RestTemplateAutoConfiguration.class, TaskSchedulingAutoConfiguration.class, TaskExecutionAutoConfiguration.class, MailSenderAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, AopAutoConfiguration.class, ElasticsearchRestClientAutoConfiguration.class, ElasticsearchDataAutoConfiguration.class})
@EnableElasticsearchRepositories
@ComponentScan(basePackages={"com.sunwayworld"}, excludeFilters={
    @ComponentScan.Filter(type=FilterType.REGEX, pattern="com\\.sunwayworld\\.owned\\..*")
})
@EnableTransactionManagement
@ConditionalOnProperty(prefix="sunway.app.stand-alone", value={"enabled"}, havingValue="true", matchIfMissing=true)
public class GikamCoreApplication
extends SpringBootServletInitializer
implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
    public static void main(String[] args) {
        SpringApplication.run(GikamCoreApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(GikamCoreApplication.class);
    }

    @Bean(value={"customSecurityConfigure"})
    public Function<HttpSecurity, HttpSecurity> securityConfigureCustomizer() {
        return r -> r;
    }

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        ((TomcatServletWebServerFactory)factory).addContextCustomizers(context -> {
            File file = new File(SystemUtils.getWorkingPath() + "/tomcat");
            file.mkdirs();
            context.setDocBase(file.getAbsolutePath());
        });
    }

    static {
        SerializeConfig config = SerializeConfig.getGlobalInstance();
        config.put((Type)((Object)Double.class), new DoubleSerializer("#.##########"));
        config.put(Double.TYPE, new DoubleSerializer("#.##########"));
    }
}
