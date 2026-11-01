package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedQualityControlChartController;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Static guard for quality-control chart status/selectable read aliases. */
public final class OwnedQualityControlChartControllerContractTest {
    public static void main(String[] args) {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedQualityControlChartController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class);
            if (post != null) paths.addAll(Arrays.asList(post.path()));
            GetMapping get = method.getAnnotation(GetMapping.class);
            if (get != null) paths.addAll(Arrays.asList(get.path()));
        }
        require(paths, "/secure/emc/module/sa/quality-control-charts/info/status");
        require(paths, "/secure/emc/module/sa/quality-control-charts/items/queries/selectable");
        require(paths, "/secure/emc/module/sa/quality-control-charts/tests/queries/selectable");
        require(paths, "/secure/emc/module/sa/quality-control-charts/qc-categories/queries/selectable");
        require(paths, "/secure/emc/module/sa/quality-control-charts/users/queries/selectable");
        require(paths, "/secure/emc/module/sa/quality-control-charts/queries");
        require(paths, "/secure/emc/module/sa/quality-control-charts/queries/raw");
        require(paths, "/secure/emc/module/sa/quality-control-charts/queries/searchable");
        System.out.println("owned quality-control chart read contract checks passed");
    }
    private static void require(Set<String> paths, String path) {
        if (!paths.contains(path)) throw new AssertionError("missing quality-control chart path: " + path);
    }
}
