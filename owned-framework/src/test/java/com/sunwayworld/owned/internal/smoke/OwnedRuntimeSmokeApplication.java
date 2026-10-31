package com.sunwayworld.owned.internal.smoke;

import com.sunwayworld.GikamCoreApplication;
import com.sunwayworld.framework.spring.SunwayApplication;
import com.sunwayworld.framework.spring.annotation.SunwayBootApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

/** A real web bootstrap probe for the owned runtime, outside supplier contracts. */
@SunwayBootApplication
@EnableAutoConfiguration
public class OwnedRuntimeSmokeApplication extends GikamCoreApplication {
    public static void main(String[] args) {
        SunwayApplication.run(OwnedRuntimeSmokeApplication.class, args);
    }
}
