package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import com.sunwayworld.owned.internal.web.OwnedRunRemindQueryController;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Static contract guard for the run-remind result read family. */
public final class OwnedRunRemindQueryControllerContractTest {
    public static void main(String[] args) {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedRunRemindQueryController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class); if (post != null) paths.addAll(Arrays.asList(post.path()));
            GetMapping get = method.getAnnotation(GetMapping.class); if (get != null) paths.addAll(Arrays.asList(get.path()));
        }
        require(paths, "/secure/emc/module/bp/result/run-reminds/queries"); require(paths, "/secure/emc/module/bp/result/run-reminds/queries/raw"); require(paths, "/secure/emc/module/bp/result/run-reminds/queries/searchable"); require(paths, "/secure/emc/module/bp/result/run-reminds/queries/runRemind/{runId}"); require(paths, "/secure/emc/module/bp/result/run-reminds/queries/resultRemind/{runId}"); require(paths, "/secure/emc/module/bp/result/run-reminds/info/status"); require(paths, "/secure/emc/module/bp/result/run-reminds/{id}");
        boolean registered = false; for (Method method : OwnedIdentityConfiguration.class.getDeclaredMethods()) if (method.getReturnType() == OwnedRunRemindQueryController.class) registered = true;
        if (!registered) throw new AssertionError("run-remind controller is not registered");
        try {
            java.nio.file.Path path = java.nio.file.Paths.get("owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedRunRemindQueryController.java");
            if (!java.nio.file.Files.exists(path)) path = java.nio.file.Paths.get("src/main/java/com/sunwayworld/owned/internal/web/OwnedRunRemindQueryController.java");
            String source = new String(java.nio.file.Files.readAllBytes(path), "UTF-8");
            for (String token : new String[]{"HttpServletResponse", "GIKAM.EXCEPTION.UNEXPECTED", "MalformedRunRemindQueryException", "SELECT A.* FROM T_EMC_RUN_REMIND A\""}) {
                if (!source.contains(token)) throw new AssertionError("missing run-remind contract: " + token);
            }
        } catch (java.io.IOException error) { throw new AssertionError(error); }
        System.out.println("owned run-remind query contract checks passed");
    }
    private static void require(Set<String> paths, String path) { if (!paths.contains(path)) throw new AssertionError("missing run-remind route: " + path); }
}
