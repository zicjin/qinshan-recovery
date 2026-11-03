package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import com.sunwayworld.owned.internal.web.OwnedPretreatmentQueryController;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Static contract guard for pretreatment and method read families. */
public final class OwnedPretreatmentQueryControllerContractTest {
    public static void main(String[] args) {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedPretreatmentQueryController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class); if (post != null) paths.addAll(Arrays.asList(post.path()));
            GetMapping get = method.getAnnotation(GetMapping.class); if (get != null) paths.addAll(Arrays.asList(get.path()));
        }
        require(paths, "/secure/emc/module/bp/result/pretreatments/queries"); require(paths, "/secure/emc/module/bp/result/pretreatments/queries/raw"); require(paths, "/secure/emc/module/bp/result/pretreatments/queries/searchable"); require(paths, "/secure/emc/module/bp/result/pretreatments/extract-methods/queries"); require(paths, "/secure/emc/module/bp/result/pretreatments/purify-methods/queries"); require(paths, "/secure/emc/module/bp/result/pretreatments/info/status"); require(paths, "/secure/emc/module/bp/result/pretreatments/{id}");
        boolean registered = false; for (Method method : OwnedIdentityConfiguration.class.getDeclaredMethods()) if (method.getReturnType() == OwnedPretreatmentQueryController.class) registered = true;
        if (!registered) throw new AssertionError("pretreatment controller is not registered");
        assertMalformedContract("OwnedPretreatmentQueryController.java", "GIKAM.EXCEPTION.UNEXPECTED", "MalformedPretreatmentQueryException");
        System.out.println("owned pretreatment query contract checks passed");
    }
    private static void assertMalformedContract(String file, String... required) {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get("owned-framework/src/main/java/com/sunwayworld/owned/internal/web/" + file);
            if (!java.nio.file.Files.exists(path)) path = java.nio.file.Paths.get("src/main/java/com/sunwayworld/owned/internal/web/" + file);
            String source = new String(java.nio.file.Files.readAllBytes(path), "UTF-8");
            for (String token : required) if (!source.contains(token)) throw new AssertionError("missing malformed-query contract: " + token);
        } catch (java.io.IOException error) { throw new AssertionError(error); }
    }
    private static void require(Set<String> paths, String path) { if (!paths.contains(path)) throw new AssertionError("missing pretreatment route: " + path); }
}
