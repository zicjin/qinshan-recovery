package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import com.sunwayworld.owned.internal.web.OwnedOrderTaskResultQueryController;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
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
        requireProfile("/queries", 140);
        requireProfile("/queries/raw", 140);
        requireProfile("/queries/searchable", 140);
        require(paths, PREFIX + "/data-query-titles/queries");
        require(paths, PREFIX + "/data-query-rows/queries");
        require(paths, PREFIX + "/qcnames/queries");
        require(paths, PREFIX + "/data-titles/queries");
        require(paths, PREFIX + "/data-rows/queries");
        require(paths, PREFIX + "/gas-parameter/table-heads/queries");
        require(paths, PREFIX + "/gas-parameter/table-rows/queries");
        require(paths, PREFIX + "/gas-parameter-result/table-heads/queries");
        require(paths, PREFIX + "/gas-parameter-result/table-rows/queries");
        require(paths, PREFIX + "/quick-inputs/table-heads/queries");
        require(paths, PREFIX + "/quick-inputs/table-rows/queries");
        require(paths, PREFIX + "/quick-inputs-sample/table-heads/queries");
        require(paths, PREFIX + "/quick-inputs-sample/table-rows/queries");
        require(paths, PREFIX + "/result-inputs/table-heads/queries");
        require(paths, PREFIX + "/result-inputs/table-rows/queries");
        require(paths, PREFIX + "/preprocessing-result/table-heads/queries");
        require(paths, PREFIX + "/preprocessing-result/table-rows/queries");
        require(paths, PREFIX + "/info/status");
        require(paths, PREFIX + "/{id}/info/status");
        require(paths, PREFIX + "/{id}");
        require(paths, PREFIX + "/{id}/action/print");
        require(paths, PREFIX + "/{id}/columns/{column}");
        require(paths, "/secure/emc/module/bp/bp/order-tasks/{id}/results/queries");
        require(paths, "/secure/emc/module/bp/bp/order-tasks/{id}/results/distinct/queries");
        boolean registered = false;
        for (Method method : OwnedIdentityConfiguration.class.getDeclaredMethods()) {
            if (method.getReturnType() == OwnedOrderTaskResultQueryController.class) registered = true;
        }
        if (!registered) throw new AssertionError("order-task-result controller is not registered");
        PathCheck.requireSource();
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

    @SuppressWarnings("unchecked")
    private static void requireProfile(String path, int expectedSize) {
        try {
            Field field = OwnedOrderTaskResultQueryController.class.getDeclaredField("RESULT_TYPE_PROFILES");
            field.setAccessible(true);
            Map<String, Map<String, String>> profiles = (Map<String, Map<String, String>>) field.get(null);
            Map<String, String> profile = profiles.get(path);
            if (profile == null || profile.size() != expectedSize) throw new AssertionError("unexpected result type profile " + path);
        } catch (ReflectiveOperationException error) { throw new AssertionError("cannot inspect result type profiles", error); }
    }

    private static final class PathCheck {
        static void requireSource() {
            try {
                java.nio.file.Path path = java.nio.file.Paths.get("owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedOrderTaskResultQueryController.java");
                String source = new String(java.nio.file.Files.readAllBytes(path), "UTF-8");
                for (String token : new String[]{"selectResultInputRows", "resultInputTitles", "resultInputRows", "inputColumnId", "originalResult", "reportResult", "scriptFlag", "M.STATUS='logged'", "extractRunId"}) {
                    if (!source.contains(token)) throw new AssertionError("missing result-input projection token: " + token);
                }
            } catch (java.io.IOException error) { throw new AssertionError("cannot inspect result-input source", error); }
        }
    }
}
