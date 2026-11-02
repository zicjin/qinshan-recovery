package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedReportResultController;
import java.lang.reflect.Method;
import org.springframework.web.bind.annotation.PostMapping;

public final class OwnedReportResultControllerContractTest {
    public static void main(String[] args) {
        for (Method method : OwnedReportResultController.class.getDeclaredMethods()) {
            PostMapping mapping = method.getAnnotation(PostMapping.class);
            if (mapping != null) for (String path : mapping.path()) if (path.endsWith("/reports/results/queries")) { System.out.println("owned report-result contract checks passed"); return; }
        }
        throw new AssertionError("missing report-result query route");
    }
}
