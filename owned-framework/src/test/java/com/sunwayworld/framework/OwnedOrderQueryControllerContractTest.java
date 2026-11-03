package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedOrderQueryController;
import java.lang.reflect.Method;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Contract guard for the source-owned order GenericResource family. */
public final class OwnedOrderQueryControllerContractTest {
    public static void main(String[] args) {
        boolean query = false; boolean raw = false; boolean searchable = false; boolean status = false;
        for (Method method : OwnedOrderQueryController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class);
            if (post != null) for (String path : post.path()) {
                if (path.endsWith("/orders/queries")) query = true;
                if (path.endsWith("/orders/queries/raw")) raw = true;
                if (path.endsWith("/orders/queries/searchable")) searchable = true;
            }
            GetMapping get = method.getAnnotation(GetMapping.class);
            if (get != null) for (String path : get.path()) if (path.endsWith("/orders/info/status")) status = true;
        }
        if (!query || !raw || !searchable || !status) throw new AssertionError("order GenericResource mappings are incomplete");
        try {
            Method body = OwnedOrderQueryController.class.getDeclaredMethod("unexpectedBody", String.class);
            body.setAccessible(true);
            String value = String.valueOf(body.invoke(null, "/secure/emc/module/bp/bp/orders/queries"));
            for (String key : new String[]{"\"error\"", "\"message\"", "\"path\"", "\"stacktrace\"", "\"status\":500", "\"timestamp\""}) {
                if (!value.contains(key)) throw new AssertionError("order malformed response missing " + key);
            }
        } catch (ReflectiveOperationException error) {
            throw new AssertionError("order malformed response helper is missing", error);
        }
        System.out.println("owned order GenericResource mapping/error checks passed");
    }
}
