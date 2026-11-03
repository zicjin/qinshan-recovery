package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import com.sunwayworld.owned.internal.web.OwnedSampleMainReadController;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;

/** Static guard for sample detail/status/column read aliases. */
public final class OwnedSampleMainReadControllerContractTest {
    public static void main(String[] args) {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedSampleMainReadController.class.getDeclaredMethods()) {
            GetMapping mapping = method.getAnnotation(GetMapping.class);
            if (mapping != null) paths.addAll(Arrays.asList(mapping.path()));
        }
        for (String path : new String[]{
                "/secure/emc/module/bp/sample/samples/{id}",
                "/secure/emc/module/bp/sample/samples/{id}/action/print",
                "/secure/emc/module/bp/sample/samples/info/status",
                "/secure/emc/module/bp/sample/samples/{id}/info/status",
                "/secure/emc/module/bp/sample/samples/{id}/columns/{column}"}) {
            if (!paths.contains(path)) throw new AssertionError("missing sample read route: " + path);
        }
        boolean registered = false;
        for (Method method : OwnedIdentityConfiguration.class.getDeclaredMethods()) {
            if (method.getReturnType() == OwnedSampleMainReadController.class) registered = true;
        }
        if (!registered) throw new AssertionError("sample main read controller is not registered");
        System.out.println("owned sample main read mapping scope checks passed");
    }
}
