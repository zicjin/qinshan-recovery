package com.sunwayworld.framework;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.owned.internal.web.OwnedMaterialApplyController;
import java.lang.reflect.Method;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;

/** Contract guard for the source-owned material issue validation route. */
public final class OwnedMaterialApplyControllerContractTest {
    public static void main(String[] args) {
        int post = 0, put = 0, delete = 0;
        boolean withdrawRoute = false, oddRejectRoute = false, undoRoute = false;
        for (Method method : OwnedMaterialApplyController.class.getDeclaredMethods()) {
            if (method.getAnnotation(PostMapping.class) != null) post++;
            PutMapping putMapping = method.getAnnotation(PutMapping.class);
            if (putMapping != null) {
                put++;
                for (String path : putMapping.path()) {
                    if (path.endsWith("/action/withdraw-process")) withdrawRoute = true;
                    if (path.endsWith("/action/odd-reject-task")) oddRejectRoute = true;
                    if (path.endsWith("/action/undo")) undoRoute = true;
                }
            }
            if (method.getAnnotation(DeleteMapping.class) != null) delete++;
        }
        if (post != 2 || put != 6 || delete != 1) throw new AssertionError("material apply route count=" + post + "/" + put + "/" + delete);
        if (!withdrawRoute) throw new AssertionError("material apply withdraw-process route is missing");
        if (!oddRejectRoute) throw new AssertionError("material apply odd-reject-task route is missing");
        if (!undoRoute) throw new AssertionError("material apply undo route is missing");
        Map<Long, Double> parsed = OwnedMaterialApplyController.requested("{\"p\":[{\"stockId\":7,\"applyQty\":2},{\"stockId\":7,\"applyQty\":3}]}");
        if (parsed.size() != 1 || parsed.get(7L) != 5d) throw new AssertionError("material apply quantity aggregation failed: " + parsed);
        if (!OwnedMaterialApplyController.workflowIds("{\"b\":[\"\",[{\"id\":11},{\"id\":12}]]}").toString().equals("[11, 12]")) {
            throw new AssertionError("workflow wrapper parsing failed");
        }
        System.out.println("owned material apply controller contract checks passed (routes, quantity aggregation and workflow wrapper)");
    }
}
