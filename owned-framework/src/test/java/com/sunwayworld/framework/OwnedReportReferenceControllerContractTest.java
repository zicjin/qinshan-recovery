package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedReportReferenceController;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

public final class OwnedReportReferenceControllerContractTest {
    public static void main(String[] args) {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedReportReferenceController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class);
            if (post != null) paths.addAll(Arrays.asList(post.path()));
            GetMapping get = method.getAnnotation(GetMapping.class);
            if (get != null) paths.addAll(Arrays.asList(get.path()));
        }
        for (String path : new String[] {
                "/secure/emc/module/mdm/basemdm/file-categories/queries",
                "/secure/emc/module/mdm/basemdm/file-categories/queries/raw",
                "/secure/emc/module/mdm/basemdm/file-categories/queries/searchable",
                "/secure/emc/module/mdm/basemdm/file-categories/info/status",
                "/secure/emc/module/mdm/basemdm/report-info-references/queries",
                "/secure/emc/module/mdm/basemdm/report-info-references/queries/raw",
                "/secure/emc/module/mdm/basemdm/report-info-references/queries/searchable",
                "/secure/emc/module/mdm/basemdm/report-info-references/info/status"}) {
            if (!paths.contains(path)) throw new AssertionError("missing base-MDM reference route " + path);
        }
        System.out.println("owned report/reference contract checks passed");
    }
}
