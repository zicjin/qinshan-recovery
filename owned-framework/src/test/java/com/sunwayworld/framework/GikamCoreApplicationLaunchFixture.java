package com.sunwayworld.framework;

import com.sunwayworld.framework.spring.SunwayApplication;
import com.sunwayworld.GikamCoreApplication;
import com.sunwayworld.framework.spring.annotation.SunwayBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SunwayBootApplication
public class GikamCoreApplicationLaunchFixture extends GikamCoreApplication {
    @Bean public String launchMarker() { return "owned-launch"; }
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SunwayApplication.run(GikamCoreApplicationLaunchFixture.class,
                "--spring.main.web-application-type=none", "--spring.main.banner-mode=off", "--spring.main.log-startup-info=false");
        if (!context.containsBean("launchMarker")) throw new AssertionError("owned fixture configuration was not registered");
        context.close();
    }
}
