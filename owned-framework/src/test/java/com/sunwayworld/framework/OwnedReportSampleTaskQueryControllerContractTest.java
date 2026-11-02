package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedReportSampleTaskQueryController;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

public final class OwnedReportSampleTaskQueryControllerContractTest {
    public static void main(String[] args) {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedReportSampleTaskQueryController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class);
            if (post != null) paths.addAll(Arrays.asList(post.path()));
            GetMapping get = method.getAnnotation(GetMapping.class);
            if (get != null) paths.addAll(Arrays.asList(get.path()));
        }
        for (String path : new String[] {
                "/secure/emc/module/bp/report/reports/queries",
                "/secure/emc/module/bp/report/reports/queries/raw",
                "/secure/emc/module/bp/report/reports/queries/searchable",
                "/secure/emc/module/bp/report/advisory-reports/queries",
                "/secure/emc/module/bp/report/advisory-reports/queries/raw",
                "/secure/emc/module/bp/report/advisory-reports/queries/searchable",
                "/secure/emc/module/bp/sample/samples/queries",
                "/secure/emc/module/bp/sample/samples/queries/raw",
                "/secure/emc/module/bp/sample/samples/queries/searchable",
                "/secure/emc/module/bp/report/reports/queries/tasks",
                "/secure/emc/module/bp/report/advisory-reports/queries/tasks",
                "/secure/emc/module/bp/sample/samples/queries/tasks",
                "/secure/emc/module/bp/report/reports/info/status",
                "/secure/emc/module/bp/report/advisory-reports/info/status",
                "/secure/emc/module/bp/sample/samples/info/status"}) {
            if (!paths.contains(path)) throw new AssertionError("missing report/sample route " + path);
        }
        System.out.println("owned report/sample task-query contract checks passed");
    }
}
