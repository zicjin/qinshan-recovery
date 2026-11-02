package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import com.sunwayworld.owned.internal.web.OwnedSolQueryController;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Contract guard for the source-owned standard-solution read family. */
public final class OwnedSolQueryControllerContractTest {
    private static final String PREFIX = "/secure/emc/module/bp/sols";

    public static void main(String[] args) {
        Set<String> paths = mappedPaths();
        require(paths, PREFIX + "/queries");
        require(paths, PREFIX + "/queries/raw");
        require(paths, PREFIX + "/queries/searchable");
        require(paths, PREFIX + "/queries/roles/{roleId}");
        require(paths, PREFIX + "/info/status");
        require(paths, PREFIX + "/{id}");
        require(paths, PREFIX + "/{id}/action/print");
        require(paths, PREFIX + "/{id}/info/status");
        require(paths, PREFIX + "/{id}/columns/{column}");
        require(paths, PREFIX + "/{id}/lines/queries");
        require(paths, PREFIX + "/lines/{id}");
        boolean registered = false;
        for (Method method : OwnedIdentityConfiguration.class.getDeclaredMethods()) {
            if (method.getReturnType() == OwnedSolQueryController.class) registered = true;
        }
        if (!registered) throw new AssertionError("solution query controller is not registered");
        System.out.println("owned solution query mapping scope checks passed");
    }

    private static Set<String> mappedPaths() {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedSolQueryController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class);
            if (post != null) paths.addAll(Arrays.asList(post.path()));
            GetMapping get = method.getAnnotation(GetMapping.class);
            if (get != null) paths.addAll(Arrays.asList(get.path()));
        }
        return paths;
    }

    private static void require(Set<String> paths, String path) {
        if (!paths.contains(path)) throw new AssertionError("missing solution route: " + path);
    }
}
