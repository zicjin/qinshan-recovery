package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import com.sunwayworld.owned.internal.web.OwnedTempHumidityQueryController;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Static contract guard for the temperature/humidity result read family. */
public final class OwnedTempHumidityQueryControllerContractTest {
    public static void main(String[] args) {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedTempHumidityQueryController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class); if (post != null) paths.addAll(Arrays.asList(post.path()));
            GetMapping get = method.getAnnotation(GetMapping.class); if (get != null) paths.addAll(Arrays.asList(get.path()));
        }
        require(paths, "/secure/emc/module/bp/result/temphum/queries"); require(paths, "/secure/emc/module/bp/result/temphum/queries/raw"); require(paths, "/secure/emc/module/bp/result/temphum/queries/searchable"); require(paths, "/secure/emc/module/bp/result/temphum/info/status"); require(paths, "/secure/emc/module/bp/result/temphum/{id}");
        boolean registered = false; for (Method method : OwnedIdentityConfiguration.class.getDeclaredMethods()) if (method.getReturnType() == OwnedTempHumidityQueryController.class) registered = true;
        if (!registered) throw new AssertionError("temperature/humidity controller is not registered");
        System.out.println("owned temperature/humidity query contract checks passed");
    }
    private static void require(Set<String> paths, String path) { if (!paths.contains(path)) throw new AssertionError("missing temperature/humidity route: " + path); }
}
