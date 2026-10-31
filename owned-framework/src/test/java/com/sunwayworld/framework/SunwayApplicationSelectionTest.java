package com.sunwayworld.framework;

import com.sunwayworld.framework.spring.SunwayApplication;
import com.sunwayworld.framework.spring.annotation.SunwayBootApplication;
import com.sunwayworld.framework.cache.memory.MemoryCacheManager;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

@SunwayBootApplication
public final class SunwayApplicationSelectionTest {
    @SunwayBootApplication public static class Selected { @Bean public String selected() { return "selected"; } }
    @SunwayBootApplication public static class Other { @Bean public Integer other() { return 1; } }
    public static void main(String[] args) {
        MemoryCacheManager.put("SunwayMainApplicationClass", Selected.class);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(Selected.class,Other.class); context.refresh();
        if (!context.containsBean("selected") || context.containsBean("other")) throw new AssertionError("main class selection");
        context.close();
        System.out.println("sunway application selection: PASS");
    }
}
