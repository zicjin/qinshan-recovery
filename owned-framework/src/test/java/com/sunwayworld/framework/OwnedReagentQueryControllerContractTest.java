package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedReagentQueryController;
import com.sunwayworld.owned.internal.web.QueryRequest;
import java.lang.reflect.Method;
import org.springframework.web.bind.annotation.PostMapping;

/** Contract guard for the source-owned read-only reagent routes. */
public final class OwnedReagentQueryControllerContractTest {
    public static void main(String[] args) {
        int mapped = 0;
        for (Method method : OwnedReagentQueryController.class.getDeclaredMethods()) {
            if (method.getAnnotation(PostMapping.class) != null) mapped++;
        }
        if (mapped != 2) throw new AssertionError("reagent query route count=" + mapped);
        QueryRequest request = QueryRequest.parse(
                "{\"p\":{\"methodId_SEQ\":42,\"n\":2,\"s\":1,"
                        + "\"f\":{\"REAGENTNAME_CISC\":\"acid\"},"
                        + "\"o\":[{\"PREPAREDATE\":\"desc\"}]}}", null);
        OwnedReagentQueryController.validateQuery(request, false);
        boolean rejected = false;
        try {
            OwnedReagentQueryController.validateQuery(
                    QueryRequest.parse("{\"p\":{\"unknown_SEQ\":1}}", null), false);
        } catch (IllegalArgumentException expected) { rejected = true; }
        if (!rejected) throw new AssertionError("unknown reagent query parameter was accepted");
        rejected = false;
        try {
            OwnedReagentQueryController.validateQuery(
                    QueryRequest.parse("{\"p\":{\"methodId_SEQ\":42}}", null), true);
        } catch (IllegalArgumentException expected) { rejected = true; }
        if (!rejected) throw new AssertionError("reagent-only query parameter was accepted for composition");
        System.out.println("owned reagent query controller contract checks passed (" + mapped + " routes, filter/page/order validation)");
    }
}
