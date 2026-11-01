package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedOrderRetainController;
import com.sunwayworld.owned.internal.web.OwnedOrderRetainHandleController;
import com.sunwayworld.owned.internal.web.OwnedOrderRetainWriteController;
import java.lang.reflect.Method;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Static route guard for the owned order-retain inherited resource. */
public final class OwnedOrderRetainControllerContractTest {
    public static void main(String[] args) {
        boolean query = false, raw = false, searchable = false, detail = false;
        boolean status = false, statusById = false, print = false;
        for (Method method : OwnedOrderRetainController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class);
            if (post != null) for (String path : post.path()) {
                requireOriginalApiPrefix(path);
                query |= path.endsWith("/order-retains/queries");
                raw |= path.endsWith("/order-retains/queries/raw");
                searchable |= path.endsWith("/order-retains/queries/searchable");
            }
            GetMapping get = method.getAnnotation(GetMapping.class);
            if (get != null) for (String path : get.path()) {
                requireOriginalApiPrefix(path);
                detail |= path.endsWith("/order-retains/{id}");
                status |= path.endsWith("/order-retains/info/status");
                statusById |= path.endsWith("/order-retains/{id}/info/status");
                print |= path.endsWith("/order-retains/{id}/action/print");
            }
        }
        if (!(query && raw && searchable && detail && status && statusById && print)) {
            throw new AssertionError("order-retain mappings incomplete: "
                    + query + "/" + raw + "/" + searchable + "/" + detail + "/"
                    + status + "/" + statusById + "/" + print);
        }
        boolean emcInsert = false, emcDelete = false, emcHandle = false;
        for (Method method : OwnedOrderRetainWriteController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class);
            if (post != null) for (String path : post.path()) emcInsert |= "/secure/emc/module/bp/order/order-retains".equals(path);
            DeleteMapping delete = method.getAnnotation(DeleteMapping.class);
            if (delete != null) for (String path : delete.path()) emcDelete |= "/secure/emc/module/bp/order/order-retains/{id}".equals(path);
        }
        for (Method method : OwnedOrderRetainHandleController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class);
            if (post != null) for (String path : post.path()) emcHandle |= "/secure/emc/module/bp/order/order-retains/action/handle".equals(path);
        }
        if (!(emcInsert && emcDelete && emcHandle)) throw new AssertionError("order-retain emc write aliases incomplete");
        System.out.println("owned order-retain controller contract checks passed");
    }

    private static void requireOriginalApiPrefix(String path) {
        if (!path.startsWith("/secure/emc/module/bp/order/order-retains")) {
            throw new AssertionError("order-retain route must preserve original /secure/emc path: " + path);
        }
    }
}
