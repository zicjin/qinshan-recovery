package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedResultRunController;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        require(paths, "/secure/emc/module/bp/result/runs/unique-order-task/queries");
        require(paths, "/secure/emc/module/bp/result/runs/analysis-pretrial/queries");
        require(paths, "/secure/emc/module/bp/result/runs/queries/run-result");
        require(paths, "/secure/emc/module/bp/result/runs/browse-record/queries");
        require(paths, "/secure/emc/module/bp/result/runs/edited-items/queries");
        require(paths, "/secure/emc/module/bp/result/runs/unique-order-item-group/queries");
        require(paths, "/secure/emc/module/bp/result/runs/info/status");
        require(paths, "/secure/emc/module/bp/result/runs/{id}/info/status");
        Field field = OwnedResultRunController.class.getDeclaredField("QC_RELATED_ORDER_TYPE_PROFILE");
        field.setAccessible(true);
        Map<?, ?> profile = (Map<?, ?>) field.get(null);
        if (profile.size() != 17) throw new AssertionError("unexpected QC-related-order profile size: " + profile.size());
        Field unique = OwnedResultRunController.class.getDeclaredField("UNIQUE_ORDER_TASK_TYPE_PROFILE");
        unique.setAccessible(true);
        if (((Map<?, ?>) unique.get(null)).size() != 46) throw new AssertionError("unexpected unique-order-task profile size");
        Field itemGroup = OwnedResultRunController.class.getDeclaredField("UNIQUE_ORDER_ITEM_GROUP_TYPE_PROFILE");
        itemGroup.setAccessible(true);
        if (((Map<?, ?>) itemGroup.get(null)).size() != 26) throw new AssertionError("unexpected unique-order-item-group profile size");
        PathCheck.requireSource();
        System.out.println("owned result-run read alias contract checks passed");
    }

    private static void require(Set<String> paths, String path) {
        if (!paths.contains(path)) throw new AssertionError("missing result-run path: " + path);
    }

    private static final class PathCheck {
        static void requireSource() throws Exception {
            Path path = Paths.get("owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedResultRunController.java");
            if (!Files.exists(path)) path = Paths.get("src/main/java/com/sunwayworld/owned/internal/web/OwnedResultRunController.java");
            String source = new String(Files.readAllBytes(path), "UTF-8");
        for (String token : new String[]{"T_EMC_QC_RELATED_ORDER", "A.CREATEDBYORGID=?", "T_EMC_ORDER_TASK", "A.ANALYSISORGID=?", "STATUS IN ('audit','qcAudit')", "T_CORE_FILE", "readBrowseRecord", "readAnalysisPretrial", "readRunResult", "readUniqueOrderItemGroups", "readEditedItems", "selectItem", "/select-item/{id}", "T_EMC_CAPACITY_ITEM_GROUP", "T_EMC_GROUP_ITEM", "T_EMC_TEST_METHOD", "T_EMC_STANDARD_SET", "ITEM_TESTNAME", "ITEM_METHODNAME", "ITEM_SETNAME", "SELECTED_FLAG", "THEN '1' ELSE NULL", "ROW_NUMBER() OVER", "PAGE_RN_", "relatedOrderId", "testId", "totalPages"}) {
                if (!source.contains(token)) throw new AssertionError("missing result-run contract token: " + token);
            }
        }
    }
}
