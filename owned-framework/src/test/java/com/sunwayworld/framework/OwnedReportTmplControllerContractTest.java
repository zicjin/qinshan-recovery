package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import com.sunwayworld.owned.internal.web.OwnedReportTmplController;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

public final class OwnedReportTmplControllerContractTest {
    public static void main(String[] args) throws Exception {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedReportTmplController.class.getDeclaredMethods()) {
            GetMapping get = method.getAnnotation(GetMapping.class); if (get != null) paths.addAll(Arrays.asList(get.path()));
            PostMapping post = method.getAnnotation(PostMapping.class); if (post != null) paths.addAll(Arrays.asList(post.path()));
        }
        require(paths, "/secure/emc/module/bp/info/report-tmpls/info/status");
        require(paths, "/secure/emc/module/bp/info/report-tmpls/{id}");
        require(paths, "/secure/emc/module/bp/info/report-tmpls/queries");
        require(paths, "/secure/emc/module/bp/info/report-tmpls/queries/raw");
        require(paths, "/secure/emc/module/bp/info/report-tmpls/queries/searchable");
        require(paths, "/secure/emc/module/bp/info/report-tmpls/{reportTmplId}/queries");
        boolean registered = false; for (Method method : OwnedIdentityConfiguration.class.getDeclaredMethods()) if (method.getReturnType() == OwnedReportTmplController.class) registered = true;
        if (!registered) throw new AssertionError("report-tmpl controller is not registered");
        Method fieldName = OwnedReportTmplController.class.getDeclaredMethod("camel", String.class);
        fieldName.setAccessible(true);
        if (!"reportTmplId".equals(fieldName.invoke(null, "REPORTTMPLID")) || !"createdByOrgId".equals(fieldName.invoke(null, "CREATEDBYORGID"))) throw new AssertionError("declared report-template bean names must survive Oracle labels");
        System.out.println("owned report-template read contract checks passed");
    }
    private static void require(Set<String> paths, String path) { if (!paths.contains(path)) throw new AssertionError("missing report-template route: " + path); }
}
