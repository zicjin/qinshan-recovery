package com.sunwayworld.framework;

import com.sunwayworld.framework.spring.SunwayApplication;
import com.sunwayworld.framework.spring.annotation.SunwayBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

/** Exercises both observed SunwayApplication.run paths through a real context. */
@SunwayBootApplication
public class SunwayApplicationRunContractTest {
    @Bean
    public String runMarker() { return "run-marker"; }

    public static void main(String[] args) {
        ConfigurableApplicationContext single = SunwayApplication.run(
                SunwayApplicationRunContractTest.class,
                "--spring.main.web-application-type=none", "--spring.config.name=owned-run-contract-fixture", "--owned.contract.marker=passed");
        assertSelected(single, "single-source");
        single.close();

        ConfigurableApplicationContext array = SunwayApplication.run(
                new Class<?>[] {SunwayApplicationRunContractTest.class},
                new String[] {"--spring.main.web-application-type=none", "--spring.config.name=owned-run-contract-fixture", "--owned.contract.marker=passed"});
        assertSelected(array, "array-source");
        array.close();
        System.out.println("sunway application run contract: PASS");
    }

    private static void assertSelected(ConfigurableApplicationContext context, String label) {
        if (!"passed".equals(context.getEnvironment().getProperty("owned.contract.marker"))) {
            throw new AssertionError(label + ": command-line arguments were not forwarded");
        }
        if (!context.isActive()) throw new AssertionError(label + ": returned context is not active");
        if (!context.containsBean("runMarker")) throw new AssertionError(label + ": selected configuration was not registered");
        if (SunwayApplication.getSunwayMainApplicationClass() != SunwayApplicationRunContractTest.class) {
            throw new AssertionError(label + ": main application class was not cached");
        }
    }
}
