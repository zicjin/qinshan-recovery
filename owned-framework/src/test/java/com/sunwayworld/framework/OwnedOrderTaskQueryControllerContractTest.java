package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import com.sunwayworld.owned.internal.web.OwnedOrderTaskQueryController;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Contract guard for the owned order-task inherited/resource read seam. */
public final class OwnedOrderTaskQueryControllerContractTest {
    private static final String PREFIX = "/secure/emc/module/bp/bp/order-tasks";

    public static void main(String[] args) {
        Set<String> paths = mappedPaths();
        require(paths, PREFIX + "/queries");
        require(paths, PREFIX + "/queries/raw");
        require(paths, PREFIX + "/queries/searchable");
        require(paths, PREFIX + "/queries/runnable");
        require(paths, PREFIX + "/queries/analysis");
        require(paths, PREFIX + "/queries/order-group");
        require(paths, PREFIX + "/queries/order-group-runs");
        require(paths, PREFIX + "/unique-tasks/queries");
        require(paths, PREFIX + "/subpackages/queries");
        require(paths, PREFIX + "/info/status");
        require(paths, PREFIX + "/{id}/info/status");
        require(paths, PREFIX + "/{id}");
        boolean registered = false;
        for (Method method : OwnedIdentityConfiguration.class.getDeclaredMethods()) {
            if (method.getReturnType() == OwnedOrderTaskQueryController.class) registered = true;
        }
        if (!registered) throw new AssertionError("order-task query controller is not registered");
        requireProfile("/queries", 125);
        requireProfile("/queries/raw", 125);
        requireProfile("/queries/searchable", 125);
        requireProfile("/queries/runnable", 16);
        requireProfile("/queries/un-runnable", 98);
        requireProfile("/queries/analysis", 7);
        requireProfile("/queries/order-group", 46);
        requireProfile("/queries/order-group-runs", 62);
        requireProfile("/unique-tasks/queries", 7);
        requireProfile("/subpackages/queries", 89);
        System.out.println("owned order-task query mapping/profile checks passed");
    }

    private static Set<String> mappedPaths() {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedOrderTaskQueryController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class);
            if (post != null) paths.addAll(Arrays.asList(post.path()));
            GetMapping get = method.getAnnotation(GetMapping.class);
            if (get != null) paths.addAll(Arrays.asList(get.path()));
        }
        return paths;
    }

    private static void require(Set<String> paths, String path) {
        if (!paths.contains(path)) throw new AssertionError("missing order-task path: " + path);
    }

    @SuppressWarnings("unchecked")
    private static void requireProfile(String path, int expectedSize) {
        try {
            Field field = OwnedOrderTaskQueryController.class.getDeclaredField("TYPE_PROFILES");
            field.setAccessible(true);
            Map<String, Map<String, String>> profiles = (Map<String, Map<String, String>>) field.get(null);
            Map<String, String> profile = profiles.get(path);
            if (profile == null || profile.size() != expectedSize) {
                throw new AssertionError("unexpected supplier type profile " + path + ": "
                        + (profile == null ? "missing" : profile.size()));
            }
        } catch (ReflectiveOperationException error) {
            throw new AssertionError("cannot inspect order-task type profiles", error);
        }
    }
}
