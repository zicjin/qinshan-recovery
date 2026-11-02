package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedInfoConfigDataController;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Static contract for info-config and info-data read aliases. */
public final class OwnedInfoConfigDataControllerContractTest {
    public static void main(String[] args) {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedInfoConfigDataController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class);
            if (post != null) paths.addAll(Arrays.asList(post.path()));
            GetMapping get = method.getAnnotation(GetMapping.class);
            if (get != null) paths.addAll(Arrays.asList(get.path()));
        }
        for (String prefix : new String[]{
                "/secure/emc/module/bp/info/info-configs",
                "/secure/emc/module/bp/info/info-datas"}) {
            require(paths, prefix + "/queries");
            require(paths, prefix + "/queries/raw");
            require(paths, prefix + "/queries/searchable");
            require(paths, prefix + "/info/status");
        }
        System.out.println("owned info-config/info-data read contract checks passed");
    }

    private static void require(Set<String> paths, String path) {
        if (!paths.contains(path)) throw new AssertionError("missing info route: " + path);
    }
}
