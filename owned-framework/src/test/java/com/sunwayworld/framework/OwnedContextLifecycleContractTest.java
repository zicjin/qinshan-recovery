package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.context.OwnedApplicationContext;
import java.lang.reflect.Proxy;
import org.springframework.context.ConfigurableApplicationContext;

/** Verifies the project-owned context seam without a fake public ApplicationContext facade. */
public final class OwnedContextLifecycleContractTest {
    public static void main(String[] args) {
        ConfigurableApplicationContext first = context();
        ConfigurableApplicationContext second = context();
        OwnedApplicationContext.register(first);
        if (OwnedApplicationContext.getApplicationContext() != first) throw new AssertionError("first registration");
        OwnedApplicationContext.register(second);
        if (OwnedApplicationContext.getApplicationContext() != second) throw new AssertionError("replacement registration");
        OwnedApplicationContext.clear(first);
        if (OwnedApplicationContext.getApplicationContext() != second) throw new AssertionError("stale clear erased replacement");
        OwnedApplicationContext.clear(second);
        if (OwnedApplicationContext.getApplicationContext() != null) throw new AssertionError("matching clear retained context");
        System.out.println("owned context lifecycle contract: PASS");
    }
    private static ConfigurableApplicationContext context() {
        return (ConfigurableApplicationContext) Proxy.newProxyInstance(
                OwnedContextLifecycleContractTest.class.getClassLoader(),
                new Class<?>[] {ConfigurableApplicationContext.class},
                (proxy, method, arguments) -> {
                    if (method.getName().equals("toString")) return "owned-context-proxy";
                    if (method.getName().equals("addApplicationListener")) return null;
                    if (method.getName().equals("getApplicationContext")) return proxy;
                    if (method.getReturnType() == boolean.class) return false;
                    if (method.getReturnType() == int.class) return 0;
                    return null;
                });
    }
}
