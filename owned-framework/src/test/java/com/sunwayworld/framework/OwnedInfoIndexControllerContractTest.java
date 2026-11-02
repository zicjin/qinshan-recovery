package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedInfoIndexController;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.lang.reflect.Method;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Static contract for the owned info-index read aliases. */
public final class OwnedInfoIndexControllerContractTest {
    public static void main(String[] args) {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedInfoIndexController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class);
            if (post != null) paths.addAll(Arrays.asList(post.path()));
            GetMapping get = method.getAnnotation(GetMapping.class);
            if (get != null) paths.addAll(Arrays.asList(get.path()));
        }
        require(paths, "/secure/emc/module/bp/info/info-indexs/queries");
        require(paths, "/secure/emc/module/bp/info/info-indexs/queries/raw");
        require(paths, "/secure/emc/module/bp/info/info-indexs/queries/searchable");
        require(paths, "/secure/emc/module/bp/info/info-indexs/info/status");
        System.out.println("owned info-index read contract checks passed");
    }

    private static void require(Set<String> paths, String path) {
        if (!paths.contains(path)) throw new AssertionError("missing info-index route: " + path);
    }
}
