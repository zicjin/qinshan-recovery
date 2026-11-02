package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedDeliveryOrderLabelQueryController;
import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import java.lang.reflect.Method;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Static contract guard for the shared delivery/order-label read family. */
public final class OwnedDeliveryOrderLabelQueryControllerContractTest {
    public static void main(String[] args) throws Exception {
        boolean query = false, status = false, detail = false;
        for (Method method : OwnedDeliveryOrderLabelQueryController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class);
            if (post != null && method.getName().equals("query") && post.path().length == 6) query = true;
            GetMapping get = method.getAnnotation(GetMapping.class);
            if (get != null && method.getName().equals("status") && get.path().length == 4) status = true;
            if (get != null && method.getName().equals("detail") && get.path().length == 2) detail = true;
        }
        if (!query || !status || !detail) throw new AssertionError("delivery/order-label aliases incomplete");
        java.nio.file.Path sourcePath = java.nio.file.Paths.get("src/main/java/com/sunwayworld/owned/internal/web/OwnedDeliveryOrderLabelQueryController.java");
        if (!java.nio.file.Files.exists(sourcePath)) sourcePath = java.nio.file.Paths.get("lims-recovery/owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedDeliveryOrderLabelQueryController.java");
        String source = new String(java.nio.file.Files.readAllBytes(sourcePath), "UTF-8");
        if (!source.contains("return ok(\"{\\\"ext$\\\":{}}\")")) throw new AssertionError("status envelope must contain ext$ only");
        if (!source.contains("createdById") || !source.contains("createdTime") || !source.contains("labelCode")) throw new AssertionError("order-label camelCase mapping missing");
        if (!source.contains("v.put(\"ext$\",new LinkedHashMap<String,Object>())")) throw new AssertionError("order-label ext$ mapping missing");
        boolean registered = false;
        for (Method method : OwnedIdentityConfiguration.class.getDeclaredMethods()) {
            if (method.getReturnType() == OwnedDeliveryOrderLabelQueryController.class) registered = true;
        }
        if (!registered) throw new AssertionError("delivery/order-label controller is not registered");
        System.out.println("owned delivery/order-label query contract checks passed");
    }
}
