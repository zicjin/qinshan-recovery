package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.context.OwnedApplicationContext;
import com.sunwayworld.owned.internal.i18n.OwnedMessageSource;
import java.util.Locale;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.sunwayworld.framework.spring.SunwayApplication;

/** Exercises automatic message resolution through a real Spring context lifecycle. */
public final class OwnedMessageSourceLifecycleTest {
    @Configuration
    public static class FailingConfiguration {
        @Bean public static BeanFactoryPostProcessor broken() {
            return factory -> { throw new IllegalStateException("expected-bootstrap-failure"); };
        }
    }
    private static GenericApplicationContext context(String message) {
        GenericApplicationContext context = new GenericApplicationContext();
        StaticMessageSource messages = new StaticMessageSource();
        messages.addMessage("number", Locale.US, message);
        context.getBeanFactory().registerSingleton("messageSource", messages);
        context.refresh();
        return context;
    }
    public static void main(String[] args) {
        OwnedMessageSource.setMessageSource(null);
        LocaleContextHolder.setLocale(Locale.US);
        if (!"missing".equals(OwnedMessageSource.getMessage("missing"))) throw new AssertionError("literal fallback");
        GenericApplicationContext first = context("Number {0}");
        OwnedApplicationContext.register(first);
        if (OwnedApplicationContext.getApplicationContext() != first) throw new AssertionError("registered context");
        if (!"Number seven".equals(OwnedMessageSource.getMessage("number", "seven"))) throw new AssertionError("context message resolution");
        if (OwnedApplicationContext.getBeanIfPresent(javax.sql.DataSource.class) != null) throw new AssertionError("no datasource");
        OwnedApplicationContext.clear(new GenericApplicationContext());
        if (OwnedApplicationContext.getApplicationContext() != first) throw new AssertionError("identity-safe clear");
        GenericApplicationContext replacement = context("Replacement {0}");
        OwnedApplicationContext.register(replacement);
        first.close();
        if (OwnedApplicationContext.getApplicationContext() != replacement) throw new AssertionError("old close clears replacement");
        if (!"Replacement seven".equals(OwnedMessageSource.getMessage("number", "seven"))) throw new AssertionError("replacement source");
        replacement.close();
        if (OwnedApplicationContext.getApplicationContext() != null) throw new AssertionError("close clears context");
        if (!"number".equals(OwnedMessageSource.getMessage("number"))) throw new AssertionError("clear stops message resolution");
        StaticMessageSource explicit = new StaticMessageSource();
        explicit.addMessage("explicit", Locale.US, "Explicit");
        OwnedMessageSource.setMessageSource(explicit);
        GenericApplicationContext second = context("Second {0}");
        OwnedApplicationContext.register(second);
        if (!"Explicit".equals(OwnedMessageSource.getMessage("explicit"))) throw new AssertionError("explicit override");
        OwnedApplicationContext.clear(second);
        if (!"Explicit".equals(OwnedMessageSource.getMessage("explicit"))) throw new AssertionError("override survives clear");
        OwnedMessageSource.setMessageSource(null);
        second.close();
        try {
            SunwayApplication.run(FailingConfiguration.class, "--spring.main.web-application-type=none", "--spring.main.banner-mode=off", "--spring.main.log-startup-info=false");
            throw new AssertionError("bootstrap must fail");
        } catch (IllegalStateException expected) {
            if (!"expected-bootstrap-failure".equals(expected.getMessage())) throw expected;
        }
        if (OwnedApplicationContext.getApplicationContext() != null) throw new AssertionError("failed bootstrap leaks context");
        if (!"number".equals(OwnedMessageSource.getMessage("number"))) throw new AssertionError("failed bootstrap leaves message source");
        LocaleContextHolder.resetLocaleContext();
        System.out.println("owned message source lifecycle: PASS (real context messages, replacement, identity clear, close, override persistence, no DataSource, failed SunwayApplication bootstrap)");
    }
}
