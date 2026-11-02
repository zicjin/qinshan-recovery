package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedDeliveryOrderLabelQueryController;
import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
        boolean registered = false;
        for (Method method : OwnedIdentityConfiguration.class.getDeclaredMethods()) {
            if (method.getReturnType() == OwnedDeliveryOrderLabelQueryController.class) registered = true;
        }
        if (!registered) throw new AssertionError("delivery/order-label controller is not registered");
        System.out.println("owned delivery/order-label query contract checks passed");
    }
}
