package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedMtlPerCheckQueryController;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Contract guard for the standard-material periodic-check query family. */
public final class OwnedMtlPerCheckQueryControllerContractTest {
    public static void main(String[] args) {
        int posts = 0;
        boolean status = false;
        for (Method method : OwnedMtlPerCheckQueryController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class);
            if (post != null) {
                posts++;
                if (post.path().length != 3) throw new AssertionError("per-check query aliases=" + post.path().length);
            }
            GetMapping get = method.getAnnotation(GetMapping.class);
            if (get != null) for (String path : get.path()) if (path.endsWith("/info/status")) status = true;
        }
        if (posts != 1 || !status) throw new AssertionError("per-check query/status aliases are incomplete");
        Path source = Paths.get("owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedMtlPerCheckQueryController.java");
        if (!Files.exists(source)) source = Paths.get("src/main/java/com/sunwayworld/owned/internal/web/OwnedMtlPerCheckQueryController.java");
        try {
            String text = new String(Files.readAllBytes(source), "UTF-8");
            for (String token : new String[]{
                    "body.put(\"columnTypeMap\", columnTypes())",
                    "totals.put(\"qty_\", Integer.valueOf(page.total))",
                    "checkequipt", "checkreason", "maintenddate", "verifyorg"}) {
                if (!text.contains(token)) throw new AssertionError("per-check projection token missing: " + token);
            }
        } catch (java.io.IOException error) {
            throw new AssertionError(error);
        }
        System.out.println("owned material per-check query controller contract checks passed");
    }
}
