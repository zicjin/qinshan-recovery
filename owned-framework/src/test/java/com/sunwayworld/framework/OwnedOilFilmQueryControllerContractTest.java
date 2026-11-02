package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import com.sunwayworld.owned.internal.web.OwnedOilFilmQueryController;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Static contract guard for the oil-film result read family. */
public final class OwnedOilFilmQueryControllerContractTest {
    public static void main(String[] args) {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedOilFilmQueryController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class); if (post != null) paths.addAll(Arrays.asList(post.path()));
            GetMapping get = method.getAnnotation(GetMapping.class); if (get != null) paths.addAll(Arrays.asList(get.path()));
        }
        require(paths, "/secure/emc/module/bp/result/oil-films/queries");
        require(paths, "/secure/emc/module/bp/result/oil-films/queries/raw");
        require(paths, "/secure/emc/module/bp/result/oil-films/queries/searchable");
        require(paths, "/secure/emc/module/bp/result/oil-films/info/status");
        require(paths, "/secure/emc/module/bp/result/oil-films/{id}");
        boolean registered = false;
        for (Method method : OwnedIdentityConfiguration.class.getDeclaredMethods()) if (method.getReturnType() == OwnedOilFilmQueryController.class) registered = true;
        if (!registered) throw new AssertionError("oil-film controller is not registered");
        System.out.println("owned oil-film query contract checks passed");
    }
    private static void require(Set<String> paths, String path) { if (!paths.contains(path)) throw new AssertionError("missing oil-film route: " + path); }
}
