package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import com.sunwayworld.owned.internal.web.OwnedSubpackageQueryController;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Contract guard for the source-owned subpackage read family. */
public final class OwnedSubpackageQueryControllerContractTest {
    private static final String PREFIX = "/secure/emc/module/bp/subpackages";
    public static void main(String[] args) {
        Set<String> paths = mappedPaths();
        require(paths, PREFIX + "/queries"); require(paths, PREFIX + "/queries/raw"); require(paths, PREFIX + "/queries/searchable");
        require(paths, PREFIX + "/queries/roles/{roleId}"); require(paths, PREFIX + "/info/status"); require(paths, PREFIX + "/{id}");
        require(paths, PREFIX + "/{id}/action/print"); require(paths, PREFIX + "/{id}/info/status"); require(paths, PREFIX + "/{id}/columns/{column}");
        boolean registered = false; for (Method method : OwnedIdentityConfiguration.class.getDeclaredMethods()) if (method.getReturnType() == OwnedSubpackageQueryController.class) registered = true;
        if (!registered) throw new AssertionError("subpackage query controller is not registered");
        System.out.println("owned subpackage query mapping scope checks passed");
    }
    private static Set<String> mappedPaths() { Set<String> paths = new HashSet<String>(); for (Method method : OwnedSubpackageQueryController.class.getDeclaredMethods()) { PostMapping post = method.getAnnotation(PostMapping.class); if (post != null) paths.addAll(Arrays.asList(post.path())); GetMapping get = method.getAnnotation(GetMapping.class); if (get != null) paths.addAll(Arrays.asList(get.path())); } return paths; }
    private static void require(Set<String> paths, String path) { if (!paths.contains(path)) throw new AssertionError("missing subpackage route: " + path); }
}
