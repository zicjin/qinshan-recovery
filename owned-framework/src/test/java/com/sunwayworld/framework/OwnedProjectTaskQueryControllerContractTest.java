package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import com.sunwayworld.owned.internal.web.OwnedProjectTaskQueryController;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.web.bind.annotation.PostMapping;

/** Contract guard for the two inherited project task-query aliases. */
public final class OwnedProjectTaskQueryControllerContractTest {
    public static void main(String[] args) throws Exception {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedProjectTaskQueryController.class.getDeclaredMethods()) {
            PostMapping mapping = method.getAnnotation(PostMapping.class);
            if (mapping != null) paths.addAll(Arrays.asList(mapping.path()));
        }
        require(paths, "/secure/emc/module/bp/project/projects/queries/tasks");
        require(paths, "/secure/emc/module/bp/edit/project-change/projects/queries/tasks");
        boolean registered = false;
        for (Method method : OwnedIdentityConfiguration.class.getDeclaredMethods()) {
            if (method.getReturnType() == OwnedProjectTaskQueryController.class) registered = true;
        }
        if (!registered) throw new AssertionError("project task query controller is not registered");
        Field profile = OwnedProjectTaskQueryController.class.getDeclaredField("PROJECT_TYPE_PROFILE");
        profile.setAccessible(true);
        if (((Map<?, ?>) profile.get(null)).size() != 182)
            throw new AssertionError("project task supplier profile must contain 182 fields");
        if (!"T_EMC_PROJECT".equals(OwnedProjectTaskQueryController.projectSourceTable(false)))
            throw new AssertionError("project task route must use T_EMC_PROJECT");
        if (!"T_EMC_PROJECT_CHANGE".equals(OwnedProjectTaskQueryController.projectSourceTable(true)))
            throw new AssertionError("project-change task route must use T_EMC_PROJECT_CHANGE");
        System.out.println("owned project task query controller contract checks passed");
    }

    private static void require(Set<String> paths, String path) {
        if (!paths.contains(path)) throw new AssertionError("missing project task query path: " + path);
    }
}
