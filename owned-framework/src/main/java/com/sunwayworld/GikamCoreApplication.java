package com.sunwayworld;

import com.alibaba.fastjson.serializer.DoubleSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import java.io.File;
import java.util.function.Function;
import org.apache.catalina.Context;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/** Owned application base with the observed public bootstrap contract. */
@ComponentScan(basePackages = {"com.sunwayworld"})
@EnableTransactionManagement
@ConditionalOnProperty(prefix = "sunway.app.stand-alone", value = {"enabled"}, havingValue = "true", matchIfMissing = true)
public class GikamCoreApplication extends SpringBootServletInitializer implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
    public static void main(String[] args) { SpringApplication.run(GikamCoreApplication.class, args); }
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) { return builder.sources(new Class<?>[]{GikamCoreApplication.class}); }
    @Bean("customSecurityConfigure") public Function<HttpSecurity, HttpSecurity> securityConfigureCustomizer() { return r -> r; }
    public void customize(ConfigurableServletWebServerFactory factory) {
        if (!(factory instanceof TomcatServletWebServerFactory)) return;
        ((TomcatServletWebServerFactory) factory).addContextCustomizers(new TomcatContextCustomizer[]{context -> {
            File directory = new File(System.getProperty("user.dir"), "tomcat"); directory.mkdirs(); context.setDocBase(directory.getAbsolutePath());
        }});
    }
    static {
        SerializeConfig config = SerializeConfig.getGlobalInstance();
        config.put(Double.class, (ObjectSerializer) new DoubleSerializer("#.##########"));
        config.put(Double.TYPE, (ObjectSerializer) new DoubleSerializer("#.##########"));
    }
}
