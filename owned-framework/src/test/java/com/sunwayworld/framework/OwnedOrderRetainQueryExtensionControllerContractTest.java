package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedOrderRetainQueryExtensionController;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.PostMapping;

/** Static guard for overdue/task order-retain read aliases. */
public final class OwnedOrderRetainQueryExtensionControllerContractTest {
    public static void main(String[] args) {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedOrderRetainQueryExtensionController.class.getDeclaredMethods()) {
            PostMapping mapping = method.getAnnotation(PostMapping.class);
            if (mapping != null) paths.addAll(Arrays.asList(mapping.path()));
        }
        require(paths, "/secure/emc/module/bp/order/order-retains/queries/tasks");
        require(paths, "/secure/emc/module/bp/order/order-retains/queries-overdue");
        System.out.println("owned order-retain query extension contract checks passed");
    }
    private static void require(Set<String> paths, String path) { if (!paths.contains(path)) throw new AssertionError("missing order-retain path: " + path); }
}
