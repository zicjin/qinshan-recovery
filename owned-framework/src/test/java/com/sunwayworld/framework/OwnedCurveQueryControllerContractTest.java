package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedCurveQueryController;
import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Static contract guard for the result curve read family. */
public final class OwnedCurveQueryControllerContractTest {
    public static void main(String[] args) {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedCurveQueryController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class);
            if (post != null) paths.addAll(Arrays.asList(post.path()));
            GetMapping get = method.getAnnotation(GetMapping.class);
            if (get != null) paths.addAll(Arrays.asList(get.path()));
        }
        require(paths, "/secure/emc/module/bp/result/curves/queries");
        require(paths, "/secure/emc/module/bp/result/curves/queries/raw");
        require(paths, "/secure/emc/module/bp/result/curves/queries/searchable");
        require(paths, "/secure/emc/module/bp/result/curves/info/status");
        require(paths, "/secure/emc/module/bp/result/curves/{id}");
        boolean registered = false;
        for (Method method : OwnedIdentityConfiguration.class.getDeclaredMethods()) {
            if (method.getReturnType() == OwnedCurveQueryController.class) registered = true;
        }
        if (!registered) throw new AssertionError("curve controller is not registered");
        try {
            java.nio.file.Path sourcePath = java.nio.file.Paths.get("owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedCurveQueryController.java");
            if (!java.nio.file.Files.exists(sourcePath)) sourcePath = java.nio.file.Paths.get("src/main/java/com/sunwayworld/owned/internal/web/OwnedCurveQueryController.java");
            String source = new String(java.nio.file.Files.readAllBytes(sourcePath), "UTF-8");
            for (String required : new String[]{"HttpServletResponse", "GIKAM.EXCEPTION.UNEXPECTED", "MalformedCurveQueryException", "response.resetBuffer()"}) {
                if (!source.contains(required)) throw new AssertionError("missing curve malformed-query contract: " + required);
            }
        } catch (java.io.IOException error) { throw new AssertionError(error); }
        System.out.println("owned result curve query contract checks passed");
    }
    private static void require(Set<String> paths, String path) {
        if (!paths.contains(path)) throw new AssertionError("missing curve route: " + path);
    }
}
