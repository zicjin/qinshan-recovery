package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import com.sunwayworld.owned.internal.web.OwnedProjectQueryController;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

public final class OwnedProjectQueryControllerContractTest {
    public static void main(String[] args) throws Exception {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedProjectQueryController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class); if (post != null) paths.addAll(Arrays.asList(post.path()));
            GetMapping get = method.getAnnotation(GetMapping.class); if (get != null) paths.addAll(Arrays.asList(get.path()));
        }
        for (String path : new String[]{"/secure/emc/module/bp/project/projects/info/status", "/secure/emc/module/bp/project/projects/queries", "/secure/emc/module/bp/project/projects/queries/raw", "/secure/emc/module/bp/project/projects/queries/searchable", "/secure/emc/module/bp/project/projects/select-by-reportedit/queries", "/secure/emc/module/bp/project/projects/reports/queries"}) if (!paths.contains(path)) throw new AssertionError("missing project route " + path);
        boolean registered = false; for (Method method : OwnedIdentityConfiguration.class.getDeclaredMethods()) if (method.getReturnType() == OwnedProjectQueryController.class) registered = true;
        if (!registered) throw new AssertionError("project controller is not registered");
        Path source = Paths.get("owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedProjectQueryController.java"); if (!Files.exists(source)) source = Paths.get("src/main/java/com/sunwayworld/owned/internal/web/OwnedProjectQueryController.java");
        String text = new String(Files.readAllBytes(source), "UTF-8");
        if (!text.contains("T_EMC_PROJECT") || !text.contains("T_CORE_FILE") || !text.contains("T_EMC_REPORT") || !text.contains("CREATEDBYORGID") || !text.contains("BIZCATEGORYNAME") || !text.contains("SAMPLEORDERCOUNT")) throw new AssertionError("project source projection/scope missing");
        if (!text.contains("auditStatus") || !text.contains("processStatus") || !text.contains("statusCode")) throw new AssertionError("project status audit envelope missing");
        System.out.println("owned project query contract checks passed");
    }
}
