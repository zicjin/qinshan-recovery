package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedReportSendInfoController;
import java.lang.reflect.Method;
import org.springframework.web.bind.annotation.PostMapping;

public final class OwnedReportSendInfoControllerContractTest {
    public static void main(String[] args) {
        for (Method method : OwnedReportSendInfoController.class.getDeclaredMethods()) {
            PostMapping mapping = method.getAnnotation(PostMapping.class);
            if (mapping != null) {
                for (String path : mapping.path()) {
                    if (path.endsWith("/report-send-info/queries")) {
                        System.out.println("owned report send-info contract checks passed");
                        return;
                    }
                }
            }
        }
        throw new AssertionError("missing report send-info query route");
    }
}
