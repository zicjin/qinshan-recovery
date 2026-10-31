package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import com.sunwayworld.owned.internal.web.OwnedResultGenericResourceController;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Contract guard for the owned result-equipment GenericResource read seam. */
public final class OwnedResultGenericResourceControllerContractTest {
    private static final String PREFIX = "/secure/emc/module/bp/result/";
    private static final String[] RESOURCES = {"equipt-curve-standards", "equipt-curves"};

    public static void main(String[] args) {
        Set<String> paths = mappedPaths();
        for (String resource : RESOURCES) {
            require(paths, PREFIX + resource + "/queries");
            require(paths, PREFIX + resource + "/queries/raw");
            require(paths, PREFIX + resource + "/queries/searchable");
            require(paths, PREFIX + resource + "/info/status");
            require(paths, PREFIX + resource + "/1");
            require(paths, PREFIX + resource + "/1/action/print");
            require(paths, PREFIX + resource + "/1/columns/id");
        }
        for (String path : paths) if (path.contains("{resource}")) throw new AssertionError("unbounded result resource path: " + path);
        if (paths.contains(PREFIX + "staff-ability-evaluations/queries")) {
            throw new AssertionError("unsupported personnel route captured by result controller");
        }
        boolean registered = false;
        for (Method method : OwnedIdentityConfiguration.class.getDeclaredMethods()) {
            if (method.getReturnType() == OwnedResultGenericResourceController.class) registered = true;
        }
        if (!registered) throw new AssertionError("result GenericResource controller is not registered");
        System.out.println("owned result GenericResource mapping scope checks passed");
    }

    private static Set<String> mappedPaths() {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedResultGenericResourceController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class);
            if (post != null) paths.addAll(Arrays.asList(post.path()));
            GetMapping get = method.getAnnotation(GetMapping.class);
            if (get != null) paths.addAll(Arrays.asList(get.path()));
        }
        return paths;
    }

    private static void require(Set<String> paths, String path) {
        String expected = path.replace("/1/", "/{id}/").replace("/1", "/{id}").replace("/columns/id", "/columns/{column}");
        if (!paths.contains(expected)) throw new AssertionError("missing result GenericResource path: " + expected);
    }
}
