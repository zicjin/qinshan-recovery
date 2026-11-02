package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedEquipmentCurveQueryController;
import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

public final class OwnedEquipmentCurveQueryControllerContractTest {
    public static void main(String[] args) throws Exception {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedEquipmentCurveQueryController.class.getDeclaredMethods()) {
            GetMapping get = method.getAnnotation(GetMapping.class);
            if (get != null) paths.addAll(Arrays.asList(get.path()));
            PostMapping post = method.getAnnotation(PostMapping.class);
            if (post != null) paths.addAll(Arrays.asList(post.path()));
        }
        for (String path : new String[]{
                "/secure/emc/module/bp/result/equipt-curves/info/status",
                "/secure/emc/module/bp/result/equipt-curves/queries",
                "/secure/emc/module/bp/result/equipt-curves/queries/raw",
                "/secure/emc/module/bp/result/equipt-curves/queries/searchable",
                "/secure/emc/module/bp/result/equipt-curve-standards/info/status",
                "/secure/emc/module/bp/result/equipt-curve-standards/queries",
                "/secure/emc/module/bp/result/equipt-curve-standards/queries/raw",
                "/secure/emc/module/bp/result/equipt-curve-standards/queries/searchable"}) {
            if (!paths.contains(path)) throw new AssertionError("missing equipment curve route: " + path);
        }
        boolean registered = false;
        for (Method method : OwnedIdentityConfiguration.class.getDeclaredMethods()) {
            if (method.getReturnType() == OwnedEquipmentCurveQueryController.class) registered = true;
        }
        if (!registered) throw new AssertionError("equipment curve controller is not registered");
        Path source = Paths.get("owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedEquipmentCurveQueryController.java");
        if (!Files.exists(source)) source = Paths.get("src/main/java/com/sunwayworld/owned/internal/web/OwnedEquipmentCurveQueryController.java");
        String text = new String(Files.readAllBytes(source), "UTF-8");
        for (String token : new String[]{"T_EMC_EQUIPT_CURVE", "T_EMC_EQUIPT_CURVE_STANDARD", "CREATEDBYORGID=?", "ROW_NUMBER()", "totalMap", "ext$"}) {
            if (!text.contains(token)) throw new AssertionError("missing equipment curve contract token: " + token);
        }
        System.out.println("owned equipment curve query contract checks passed");
    }
}
