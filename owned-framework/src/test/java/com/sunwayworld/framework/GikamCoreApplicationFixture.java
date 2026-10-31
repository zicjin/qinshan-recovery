package com.sunwayworld.framework;

import com.sunwayworld.GikamCoreApplication;
import com.sunwayworld.framework.spring.annotation.SunwayBootApplication;
import org.springframework.context.annotation.Bean;

@SunwayBootApplication
public class GikamCoreApplicationFixture extends GikamCoreApplication {
    @Bean public String baseFixtureMarker() { return "owned-base-fixture"; }
}
