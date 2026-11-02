package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedEquipmentCurveConfigQueryController;
import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

public final class OwnedEquipmentCurveConfigQueryControllerContractTest {
    public static void main(String[] args) {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedEquipmentCurveConfigQueryController.class.getDeclaredMethods()) {
            GetMapping get = method.getAnnotation(GetMapping.class); if (get != null) paths.addAll(Arrays.asList(get.path()));
            PostMapping post = method.getAnnotation(PostMapping.class); if (post != null) paths.addAll(Arrays.asList(post.path()));
        }
        for (String path : new String[]{"/secure/emc/module/bp/result/curves/equipt-curves-config/info/status", "/secure/emc/module/bp/result/curves/equipt-curves-config/queries", "/secure/emc/module/bp/result/curves/equipt-curves-config/queries/raw", "/secure/emc/module/bp/result/curves/equipt-curves-config/queries/searchable"}) if (!paths.contains(path)) throw new AssertionError("missing curve-config route: " + path);
        boolean registered = false; for (Method method : OwnedIdentityConfiguration.class.getDeclaredMethods()) if (method.getReturnType() == OwnedEquipmentCurveConfigQueryController.class) registered = true;
        if (!registered) throw new AssertionError("curve-config controller is not registered");
        System.out.println("owned equipment curve-config query contract checks passed");
    }
}
