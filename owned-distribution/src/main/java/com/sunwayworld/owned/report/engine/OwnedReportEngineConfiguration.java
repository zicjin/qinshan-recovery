package com.sunwayworld.owned.report.engine;

import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/** Optional standard report webapp in the application's existing Tomcat/JVM. */
@Configuration
@ConditionalOnProperty(name = "owned.report.engine.enabled", havingValue = "true")
public class OwnedReportEngineConfiguration {
    static final String CONTEXT_PATH = "/report-engine";
    static final String CONNECTION_NAME = "OWNED_LOCAL_REPORT";

    @Bean
    public static BeanDefinitionRegistryPostProcessor reportEngineFactory() {
        return new BeanDefinitionRegistryPostProcessor() {
            @Override public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
                String name = "tomcatServletWebServerFactory";
                if (registry.containsBeanDefinition(name)) registry.removeBeanDefinition(name);
                registry.registerBeanDefinition(name, new RootBeanDefinition(ReportTomcatFactory.class));
            }
            @Override public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) { }
        };
    }

    static Path checkedRoot(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("Report engine root is required");
        }
        Path root = Paths.get(value);
        if (!root.isAbsolute() || !root.normalize().equals(root)) {
            throw new IllegalStateException("Report engine root must be an absolute normalized directory");
        }
        Path current = root.getRoot();
        for (Path component : root) {
            current = current.resolve(component);
            if (Files.isSymbolicLink(current) || !Files.isDirectory(current, LinkOption.NOFOLLOW_LINKS)) {
                throw new IllegalStateException("Report engine root has an unsafe directory component");
            }
        }
        Path template = root.resolve("WEB-INF/reportlets").resolve(ReportEngineAccessFilter.TEMPLATE);
        current = root;
        for (Path component : root.relativize(template)) {
            current = current.resolve(component);
            if (Files.isSymbolicLink(current)) throw new IllegalStateException("Report template may not use symbolic links");
        }
        if (!Files.isRegularFile(template, LinkOption.NOFOLLOW_LINKS)) {
            throw new IllegalStateException("Report template is missing");
        }
        return root;
    }

    public static class ReportTomcatFactory extends TomcatServletWebServerFactory {
        @Autowired private Environment environment;

        @Override protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
            Path root = checkedRoot(environment.getProperty("owned.report.engine.root"));
            Context context = tomcat.addWebapp(tomcat.getHost(), CONTEXT_PATH, root.toString());
            final ClassLoader applicationLoader = getClass().getClassLoader();
            context.setParentClassLoader(new ClassLoader(applicationLoader.getParent()) {
                @Override protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                    if (name.startsWith("javax.servlet.") || name.startsWith("org.apache.catalina.")
                            || name.startsWith("org.apache.tomcat.") || name.startsWith("org.apache.juli.")) {
                        return applicationLoader.loadClass(name);
                    }
                    return super.loadClass(name, resolve);
                }
            });
            StandardJarScanner scanner = new StandardJarScanner();
            scanner.setScanClassPath(false);
            context.setJarScanner(scanner);
            ((StandardContext) context).setDelegate(false);
            ((StandardContext) context).setSessionCookieName("OWNED_REPORT_SESSION");
            ((StandardContext) context).setSessionCookiePath(CONTEXT_PATH);

            ReportEngineAccessFilter filter = new ReportEngineAccessFilter();
            FilterDef definition = new FilterDef();
            definition.setFilterName("ownedPortalReportAccess");
            definition.setFilter(filter);
            definition.setAsyncSupported("false");
            context.addFilterDef(definition);
            FilterMap mapping = new FilterMap();
            mapping.setFilterName(definition.getFilterName());
            mapping.addURLPattern("/*");
            for (String dispatcher : new String[] {"REQUEST", "FORWARD", "INCLUDE", "ASYNC", "ERROR"}) {
                mapping.setDispatcher(dispatcher);
            }
            context.addFilterMapBefore(mapping);

            AtomicReference<AutoCloseable> sessionListener = new AtomicReference<>();
            context.addLifecycleListener(event -> {
                if (Lifecycle.BEFORE_STOP_EVENT.equals(event.getType())) {
                    AutoCloseable listener = sessionListener.getAndSet(null);
                    if (listener != null) {
                        try { listener.close(); }
                        catch (Exception failure) { throw safeFailure("Report session listener cleanup failed", failure); }
                    }
                }
                if (!Lifecycle.AFTER_START_EVENT.equals(event.getType()) || !context.getState().isAvailable()) return;
                ClassLoader previous = Thread.currentThread().getContextClassLoader();
                try {
                    ClassLoader loader = context.getLoader().getClassLoader();
                    Thread.currentThread().setContextClassLoader(loader);
                    registerDatasource(loader, environment);
                    filter.configureEngine(loader);
                    Object listener = loader.loadClass("com.sunwayworld.owned.report.engine.session.PortalReportSessionListener")
                            .getMethod("register", Consumer.class).invoke(null, filter.sessionCreated());
                    if (!(listener instanceof AutoCloseable)) throw new IllegalStateException("Report session listener unavailable");
                    sessionListener.set((AutoCloseable) listener);
                } catch (ReflectiveOperationException | RuntimeException failure) {
                    throw safeFailure("Report engine initialization failed", failure);
                } finally {
                    Thread.currentThread().setContextClassLoader(previous);
                }
            });
            TomcatWebServer server = super.getTomcatWebServer(tomcat);
            if (!context.getState().isAvailable() || sessionListener.get() == null) {
                server.stop();
                throw new IllegalStateException("Report engine context did not complete initialization");
            }
            return server;
        }
    }

    static void registerDatasource(ClassLoader loader, Environment environment) throws ReflectiveOperationException {
        Class<?> jdbcClass = loader.loadClass("com.fr.data.impl.JDBCDatabaseConnection");
        Object jdbc = jdbcClass.getConstructor().newInstance();
        for (String[] setting : new String[][] {
                {"setDriver", "spring.datasource.driver-class-name"}, {"setURL", "spring.datasource.url"},
                {"setUser", "spring.datasource.username"}, {"setPassword", "spring.datasource.password"}}) {
            String value = environment.getProperty(setting[1]);
            if (value == null || value.isEmpty()) throw new IllegalStateException("Report datasource setting is missing: " + setting[1]);
            jdbcClass.getMethod(setting[0], String.class).invoke(jdbc, value);
        }
        String schema = environment.getProperty("owned.report.engine.schema",
                environment.getRequiredProperty("spring.datasource.username").toUpperCase(Locale.ROOT));
        String expectedSchema=environment.getRequiredProperty("spring.datasource.username").toUpperCase(Locale.ROOT);
        if (!schema.matches("[A-Z][A-Z0-9_]{0,127}") || !schema.equals(expectedSchema)) {
            throw new IllegalStateException("Report schema must match the application datasource schema");
        }
        jdbcClass.getMethod("setSchema", String.class).invoke(jdbc, schema);
        Class<?> configClass = loader.loadClass("com.fr.file.ConnectionConfig");
        Class<?> connectionClass = loader.loadClass("com.fr.data.impl.Connection");
        Class<?> workerClass = loader.loadClass("com.fr.transaction.Worker");
        Object worker = Proxy.newProxyInstance(loader, new Class<?>[] {workerClass}, (proxy, method, args) -> {
            if ("targets".equals(method.getName())) return new Class<?>[] {configClass};
            if ("run".equals(method.getName())) {
                Object config = configClass.getMethod("getInstance").invoke(null);
                configClass.getMethod("addConnection", String.class, connectionClass).invoke(config, CONNECTION_NAME, jdbc);
                return null;
            }
            if ("toString".equals(method.getName())) return "OwnedReportDatasourceRegistration";
            if ("hashCode".equals(method.getName())) return System.identityHashCode(proxy);
            if ("equals".equals(method.getName())) return proxy == args[0];
            throw new UnsupportedOperationException("Unsupported report configuration callback");
        });
        Object updated = loader.loadClass("com.fr.transaction.Configurations").getMethod("update", workerClass).invoke(null, worker);
        Object config = configClass.getMethod("getInstance").invoke(null);
        if (!Boolean.TRUE.equals(updated) || configClass.getMethod("getConnection", String.class).invoke(config, CONNECTION_NAME) == null) {
            throw new IllegalStateException("Report datasource registration was not committed");
        }
    }

    private static IllegalStateException safeFailure(String message, Exception failure) {
        Throwable cause = failure;
        while (cause.getCause() != null && cause.getCause() != cause) cause = cause.getCause();
        // Third-party exceptions can contain connection settings. Preserve only
        // their type at the application boundary, never the raw message/cause.
        return new IllegalStateException(message + " (" + cause.getClass().getName() + ")");
    }
}
