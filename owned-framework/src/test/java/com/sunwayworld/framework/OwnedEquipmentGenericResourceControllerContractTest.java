package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedEquipmentGenericResourceController;
import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Contract guard for the owned equipment GenericResource read wave. */
public final class OwnedEquipmentGenericResourceControllerContractTest {
    private static final String PREFIX = "/secure/emc/module/bp/";
    private static final String[] RESOURCES = {
            "equipt-crves-detail", "equipt-crves", "equipt-dioxin-sets", "equipt-flow-sets",
            "equipt-sets", "equipt-standard-sets", "equipt-sw-sets", "equipt-temp-sets"};

    public static void main(String[] args) throws Exception {
        Set<String> paths = mappedPaths();
        for (String resource : RESOURCES) {
            require(paths, PREFIX + resource + "/queries");
            require(paths, PREFIX + resource + "/queries/raw");
            require(paths, PREFIX + resource + "/queries/searchable");
            require(paths, PREFIX + resource + "/1");
            require(paths, PREFIX + resource + "/1/action/print");
            require(paths, PREFIX + resource + "/1/columns/id");
            require(paths, PREFIX + resource + "/1/info/status");
        }
        rejectVariableRoute(paths);
        reject(paths, PREFIX + "staff-ability-evaluations/queries");
        boolean registered = false;
        for (Method method : OwnedIdentityConfiguration.class.getDeclaredMethods()) {
            if (method.getReturnType() == OwnedEquipmentGenericResourceController.class) registered = true;
        }
        if (!registered) throw new AssertionError("equipment GenericResource controller is not registered");
        System.out.println("owned equipment GenericResource mapping scope checks passed");
    }

    private static Set<String> mappedPaths() {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedEquipmentGenericResourceController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class);
            if (post != null) paths.addAll(Arrays.asList(post.path()));
            GetMapping get = method.getAnnotation(GetMapping.class);
            if (get != null) paths.addAll(Arrays.asList(get.path()));
        }
        return paths;
    }

    private static void require(Set<String> paths, String suffix) {
        String expected = suffix.replace("/1/", "/{id}/").replace("/1", "/{id}").replace("/columns/id", "/columns/{column}");
        if (!paths.contains(expected)) throw new AssertionError("missing equipment GenericResource path: " + expected);
    }

    private static void rejectVariableRoute(Set<String> paths) {
        for (String path : paths) if (path.contains("{resource}")) throw new AssertionError("unbounded equipment resource path: " + path);
    }

    private static void reject(Set<String> paths, String path) {
        if (paths.contains(path)) throw new AssertionError("unsupported personnel route captured by equipment controller: " + path);
    }
}
