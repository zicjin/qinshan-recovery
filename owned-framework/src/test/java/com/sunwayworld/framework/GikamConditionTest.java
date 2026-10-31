package com.sunwayworld.framework;

import java.util.Map;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/** Exercises bean override and literal profile selection through Spring itself. */
public final class GikamConditionTest {
    @Controller @GikamBean
    public static class BaseController { }
    @Controller @GikamBean
    public static class OverrideController extends BaseController { }
    @Controller @GikamBean @Profile({"one", "two"})
    public static class ProfileController { }
    public static class Plain { }
    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @org.springframework.stereotype.Component
    public @interface MetaComponent { }
    @MetaComponent public static class MetaOnly { }
    @org.springframework.web.bind.annotation.RestController public static class Rest { }
    @org.springframework.stereotype.Component public static class ComponentBean { }
    @org.springframework.stereotype.Service public static class ServiceBean { }
    @org.springframework.stereotype.Repository public static class RepositoryBean { }
    @Controller @Profile("!one") public static class LiteralNegation { }
    @Controller @Profile({}) public static class EmptyProfiles { }
    public interface NameMapper { }
    public static class InheritedOnly extends ComponentBean { }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("one", "two");
        context.register(BaseController.class, OverrideController.class, ProfileController.class);
        context.refresh();
        Map<String, BaseController> selected = context.getBeansOfType(BaseController.class);
        if (selected.size() != 1 || !(selected.values().iterator().next() instanceof OverrideController)) {
            throw new AssertionError("enabled subtype must replace base bean: " + selected);
        }
        if (context.getBeansOfType(ProfileController.class).size() != 1) {
            throw new AssertionError("all active literal profiles should enable the bean");
        }
        context.close();

        AnnotationConfigApplicationContext partial = new AnnotationConfigApplicationContext();
        partial.getEnvironment().setActiveProfiles("one");
        partial.register(ProfileController.class);
        partial.refresh();
        if (!partial.getBeansOfType(ProfileController.class).isEmpty()) {
            throw new AssertionError("a missing literal profile must disable the bean");
        }
        partial.close();
        System.out.println("gikam condition selection: PASS (subtype and profile contracts)");
    }
}
