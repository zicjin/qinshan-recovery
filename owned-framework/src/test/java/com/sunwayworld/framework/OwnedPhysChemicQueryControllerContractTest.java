package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import com.sunwayworld.owned.internal.web.OwnedPhysChemicQueryController;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Static contract guard for the physchemic result read family. */
public final class OwnedPhysChemicQueryControllerContractTest {
    public static void main(String[] args) {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedPhysChemicQueryController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class);
            if (post != null) paths.addAll(Arrays.asList(post.path()));
            GetMapping get = method.getAnnotation(GetMapping.class);
            if (get != null) paths.addAll(Arrays.asList(get.path()));
        }
        require(paths, "/secure/emc/module/bp/result/physchemic/queries");
        require(paths, "/secure/emc/module/bp/result/physchemic/queries/raw");
        require(paths, "/secure/emc/module/bp/result/physchemic/queries/searchable");
        require(paths, "/secure/emc/module/bp/result/physchemic/info/status");
        require(paths, "/secure/emc/module/bp/result/physchemic/{id}");
        boolean registered = false;
        for (Method method : OwnedIdentityConfiguration.class.getDeclaredMethods()) {
            if (method.getReturnType() == OwnedPhysChemicQueryController.class) registered = true;
        }
        if (!registered) throw new AssertionError("physchemic controller is not registered");
        assertMalformedContract("OwnedPhysChemicQueryController.java", "GIKAM.EXCEPTION.UNEXPECTED", "MalformedPhysChemicQueryException");
        System.out.println("owned physchemic query contract checks passed");
    }
    private static void assertMalformedContract(String file, String... required) {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get("owned-framework/src/main/java/com/sunwayworld/owned/internal/web/" + file);
            if (!java.nio.file.Files.exists(path)) path = java.nio.file.Paths.get("src/main/java/com/sunwayworld/owned/internal/web/" + file);
            String source = new String(java.nio.file.Files.readAllBytes(path), "UTF-8");
            for (String token : required) if (!source.contains(token)) throw new AssertionError("missing malformed-query contract: " + token);
        } catch (java.io.IOException error) { throw new AssertionError(error); }
    }
    private static void require(Set<String> paths, String path) {
        if (!paths.contains(path)) throw new AssertionError("missing physchemic route: " + path);
    }
}
