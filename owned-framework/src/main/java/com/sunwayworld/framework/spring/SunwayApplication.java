package com.sunwayworld.framework.spring;

import com.sunwayworld.framework.cache.memory.MemoryCacheManager;
import com.sunwayworld.owned.internal.context.OwnedApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

/** Main-class registration followed by the public Spring Boot launch. */
public class SunwayApplication extends SpringApplication {
    private ConfigurableApplicationContext launchContext;
    private SunwayApplication(Class<?>... sources) {
        super(null, sources);
        MemoryCacheManager.put("SunwayMainApplicationClass", getMainApplicationClass());
        addInitializers((ApplicationContextInitializer<ConfigurableApplicationContext>) context -> {
            launchContext = context;
            OwnedApplicationContext.register(context);
        });
        addListeners(new OwnedApplicationContext.FailureListener());
    }
    public static ConfigurableApplicationContext run(Class<?> applicationClass, String... args) {
        return run(new Class<?>[]{applicationClass}, args);
    }
    public static ConfigurableApplicationContext run(Class<?>[] applicationClasses, String[] args) {
        SunwayApplication application = new SunwayApplication(applicationClasses);
        try {
            return application.run(args);
        } catch (RuntimeException | Error failure) {
            OwnedApplicationContext.clear(application.launchContext);
            throw failure;
        }
    }
    public static Class<?> getSunwayMainApplicationClass() {
        return MemoryCacheManager.get("SunwayMainApplicationClass");
    }
}
