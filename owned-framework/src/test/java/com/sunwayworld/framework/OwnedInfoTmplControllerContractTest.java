package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedInfoTmplController;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Static contract for info-template and child read aliases. */
public final class OwnedInfoTmplControllerContractTest {
    public static void main(String[] args) {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedInfoTmplController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class); if (post != null) paths.addAll(Arrays.asList(post.path()));
            GetMapping get = method.getAnnotation(GetMapping.class); if (get != null) paths.addAll(Arrays.asList(get.path()));
        }
        require(paths, "/secure/emc/module/bp/info/info-tmpls/queries");
        require(paths, "/secure/emc/module/bp/info/info-tmpls/queries/raw");
        require(paths, "/secure/emc/module/bp/info/info-tmpls/queries/searchable");
        require(paths, "/secure/emc/module/bp/info/info-tmpls/info/status");
        require(paths, "/secure/emc/module/bp/info/info-tmpls/{tmplId}/fields/queries");
        require(paths, "/secure/emc/module/bp/info/info-tmpls/fields/queries/raw");
        require(paths, "/secure/emc/module/bp/info/info-tmpls/fields/queries/selectable");
        require(paths, "/secure/emc/module/bp/info/info-tmpls/items/queries");
        require(paths, "/secure/emc/module/bp/info/info-tmpls/{tmplId}/points/queries");
        require(paths, "/secure/emc/module/bp/info/info-tmpls/points/queries");
        System.out.println("owned info-template read contract checks passed");
    }
    private static void require(Set<String> paths, String path) { if (!paths.contains(path)) throw new AssertionError("missing info-template route: " + path); }
}
