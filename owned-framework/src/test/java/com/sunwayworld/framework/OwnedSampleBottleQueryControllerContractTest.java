package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import com.sunwayworld.owned.internal.web.OwnedSampleBottleQueryController;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

public final class OwnedSampleBottleQueryControllerContractTest {
    public static void main(String[] args) {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedSampleBottleQueryController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class); if (post != null) paths.addAll(Arrays.asList(post.path()));
            GetMapping get = method.getAnnotation(GetMapping.class); if (get != null) paths.addAll(Arrays.asList(get.path()));
        }
        require(paths, "/secure/emc/module/bp/sample-bottles/queries");
        require(paths, "/secure/emc/module/bp/sample-bottles/queries/raw");
        require(paths, "/secure/emc/module/bp/sample-bottles/queries/searchable");
        require(paths, "/secure/emc/module/bp/sample-bottles/info/status");
        require(paths, "/secure/emc/module/bp/sample-bottles/{id}");
        boolean registered = false;
        for (Method method : OwnedIdentityConfiguration.class.getDeclaredMethods()) if (method.getReturnType() == OwnedSampleBottleQueryController.class) registered = true;
        if (!registered) throw new AssertionError("sample-bottle controller is not registered");
        System.out.println("owned sample-bottle query contract checks passed");
    }
    private static void require(Set<String> paths, String path) { if (!paths.contains(path)) throw new AssertionError("missing sample-bottle route: " + path); }
}
