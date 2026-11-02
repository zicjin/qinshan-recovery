package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import com.sunwayworld.owned.internal.web.OwnedProjectChangeQueryController;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Contract guard for project-change order/scheme GenericResource aliases. */
public final class OwnedProjectChangeQueryControllerContractTest {
    public static void main(String[] args) {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedProjectChangeQueryController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class);
            if (post != null) paths.addAll(Arrays.asList(post.path()));
            GetMapping get = method.getAnnotation(GetMapping.class);
            if (get != null) paths.addAll(Arrays.asList(get.path()));
        }
        for (String prefix : new String[] {
                "/secure/emc/module/bp/edit/project-change/orders",
                "/secure/emc/module/bp/edit/project-change/proj-schemes"}) {
            require(paths, prefix + "/info/status");
            require(paths, prefix + "/queries");
            require(paths, prefix + "/queries/raw");
            require(paths, prefix + "/queries/searchable");
        }
        boolean registered = false;
        for (Method method : OwnedIdentityConfiguration.class.getDeclaredMethods()) {
            if (method.getReturnType() == OwnedProjectChangeQueryController.class) registered = true;
        }
        if (!registered) throw new AssertionError("project-change query controller is not registered");
        if (!"T_EMC_ORDER_CHANGE".equals(OwnedProjectChangeQueryController.sourceTable(false)))
            throw new AssertionError("order-change route must use T_EMC_ORDER_CHANGE");
        if (!"T_EMC_PROJ_SCHEME_CHANGE".equals(OwnedProjectChangeQueryController.sourceTable(true)))
            throw new AssertionError("scheme-change route must use T_EMC_PROJ_SCHEME_CHANGE");
        System.out.println("owned project-change query controller contract checks passed");
    }

    private static void require(Set<String> paths, String path) {
        if (!paths.contains(path)) throw new AssertionError("missing project-change query path: " + path);
    }
}
