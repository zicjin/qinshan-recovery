package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import com.sunwayworld.owned.internal.web.OwnedSampleRecordQueryController;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.PostMapping;

/** Static guard for the source-owned sample-record lookup. */
public final class OwnedSampleRecordQueryControllerContractTest {
    public static void main(String[] args) {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedSampleRecordQueryController.class.getDeclaredMethods()) {
            PostMapping mapping = method.getAnnotation(PostMapping.class);
            if (mapping != null) paths.addAll(Arrays.asList(mapping.path()));
        }
        if (!paths.contains("/secure/emc/module/bp/sample/samples/queries/sample-record")) {
            throw new AssertionError("missing sample-record query route");
        }
        boolean registered = false;
        for (Method method : OwnedIdentityConfiguration.class.getDeclaredMethods()) {
            if (method.getReturnType() == OwnedSampleRecordQueryController.class) registered = true;
        }
        if (!registered) throw new AssertionError("sample-record controller is not registered");
        System.out.println("owned sample-record query mapping scope checks passed");
    }
}
