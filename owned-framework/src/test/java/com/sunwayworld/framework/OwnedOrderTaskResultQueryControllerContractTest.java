package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import com.sunwayworld.owned.internal.web.OwnedOrderTaskResultQueryController;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Contract guard for the owned order-task-result read seam. */
public final class OwnedOrderTaskResultQueryControllerContractTest {
    private static final String PREFIX = "/secure/emc/module/bp/bp/order-task-results";

    public static void main(String[] args) {
        Set<String> paths = mappedPaths();
        require(paths, PREFIX + "/queries");
        require(paths, PREFIX + "/queries/raw");
        require(paths, PREFIX + "/queries/searchable");
        require(paths, PREFIX + "/data-query-titles/queries");
        require(paths, PREFIX + "/data-query-rows/queries");
        require(paths, PREFIX + "/qcnames/queries");
        require(paths, PREFIX + "/data-titles/queries");
        require(paths, PREFIX + "/data-rows/queries");
        require(paths, PREFIX + "/preprocessing-result/table-heads/queries");
        require(paths, PREFIX + "/preprocessing-result/table-rows/queries");
        require(paths, PREFIX + "/info/status");
        require(paths, PREFIX + "/{id}/info/status");
        require(paths, PREFIX + "/{id}");
        require(paths, PREFIX + "/{id}/action/print");
        require(paths, PREFIX + "/{id}/columns/{column}");
        boolean registered = false;
        for (Method method : OwnedIdentityConfiguration.class.getDeclaredMethods()) {
            if (method.getReturnType() == OwnedOrderTaskResultQueryController.class) registered = true;
        }
        if (!registered) throw new AssertionError("order-task-result controller is not registered");
        System.out.println("owned order-task-result query mapping checks passed");
    }

    private static Set<String> mappedPaths() {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedOrderTaskResultQueryController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class);
            if (post != null) paths.addAll(Arrays.asList(post.path()));
            GetMapping get = method.getAnnotation(GetMapping.class);
            if (get != null) paths.addAll(Arrays.asList(get.path()));
        }
        return paths;
    }

    private static void require(Set<String> paths, String path) {
        if (!paths.contains(path)) throw new AssertionError("missing order-task-result path: " + path);
    }
}
