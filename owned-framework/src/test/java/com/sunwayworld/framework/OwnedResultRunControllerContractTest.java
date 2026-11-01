package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedResultRunController;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Static guard for result-run read aliases and the QC-related-order projection. */
public final class OwnedResultRunControllerContractTest {
    public static void main(String[] args) throws Exception {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedResultRunController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class);
            if (post != null) paths.addAll(Arrays.asList(post.path()));
            GetMapping get = method.getAnnotation(GetMapping.class);
            if (get != null) paths.addAll(Arrays.asList(get.path()));
        }
        require(paths, "/secure/emc/module/bp/result/runs/queries");
        require(paths, "/secure/emc/module/bp/result/runs/queries/raw");
        require(paths, "/secure/emc/module/bp/result/runs/queries/searchable");
        require(paths, "/secure/emc/module/bp/result/runs/queries/roles/{roleId}");
        require(paths, "/secure/emc/module/bp/result/runs/queries/tasks");
        require(paths, "/secure/emc/module/bp/result/runs/queries/undoable");
        require(paths, "/secure/emc/module/bp/result/runs/queries/withdrawable");
        require(paths, "/secure/emc/module/bp/result/runs/queries/all");
        require(paths, "/secure/emc/module/bp/result/runs/qc-related-order/queries");
        require(paths, "/secure/emc/module/bp/result/runs/info/status");
        require(paths, "/secure/emc/module/bp/result/runs/{id}/info/status");
        Field field = OwnedResultRunController.class.getDeclaredField("QC_RELATED_ORDER_TYPE_PROFILE");
        field.setAccessible(true);
        Map<?, ?> profile = (Map<?, ?>) field.get(null);
        if (profile.size() != 17) throw new AssertionError("unexpected QC-related-order profile size: " + profile.size());
        System.out.println("owned result-run read alias contract checks passed");
    }

    private static void require(Set<String> paths, String path) {
        if (!paths.contains(path)) throw new AssertionError("missing result-run path: " + path);
    }
}
