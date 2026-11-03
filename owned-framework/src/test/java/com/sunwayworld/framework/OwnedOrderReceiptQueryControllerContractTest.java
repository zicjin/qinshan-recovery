package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import com.sunwayworld.owned.internal.web.OwnedOrderReceiptQueryController;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.PostMapping;

/** Static guard for the order-receipt QC chooser read route. */
public final class OwnedOrderReceiptQueryControllerContractTest {
    public static void main(String[] args) {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedOrderReceiptQueryController.class.getDeclaredMethods()) {
            PostMapping mapping = method.getAnnotation(PostMapping.class);
            if (mapping != null) paths.addAll(Arrays.asList(mapping.path()));
        }
        if (!paths.contains("/secure/emc/module/bp/order/order-receipts/qc/{category}/queries/choosable")) {
            throw new AssertionError("missing order-receipt QC chooser route");
        }
        boolean registered = false;
        for (Method method : OwnedIdentityConfiguration.class.getDeclaredMethods()) {
            if (method.getReturnType() == OwnedOrderReceiptQueryController.class) registered = true;
        }
        if (!registered) throw new AssertionError("order-receipt controller is not registered");
        System.out.println("owned order-receipt QC chooser contract checks passed");
    }
}
