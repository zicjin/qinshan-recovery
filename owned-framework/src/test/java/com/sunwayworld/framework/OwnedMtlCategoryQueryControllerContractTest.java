package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedMtlCategoryQueryController;
import com.sunwayworld.owned.internal.web.QueryRequest;
import java.lang.reflect.Method;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Contract guard for the source-owned material-category read family. */
public final class OwnedMtlCategoryQueryControllerContractTest {
    public static void main(String[] args) {
        int posts = 0;
        boolean status = false, detail = false, detailStatus = false;
        for (Method method : OwnedMtlCategoryQueryController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class);
            if (post != null) {
                posts++;
                if (post.path().length != 3) throw new AssertionError("category query aliases=" + post.path().length);
            }
            GetMapping get = method.getAnnotation(GetMapping.class);
            if (get != null) {
                for (String path : get.path()) {
                    if (path.endsWith("/info/status")) status = true;
                    if (path.endsWith("/{id}")) detail = true;
                    if (path.endsWith("/{id}/info/status")) detailStatus = true;
                }
            }
        }
        if (posts != 1) throw new AssertionError("category query methods=" + posts);
        if (!status || !detail || !detailStatus) throw new AssertionError("category detail/status aliases missing");
        OwnedMtlCategoryQueryController.validateQuery(QueryRequest.parse(
                "{\"p\":{\"n\":1,\"s\":10,\"f\":{\"MTLCATEGORYNAME_CISC\":\"acid\"},\"o\":[{\"ID\":\"desc\"}]}}", null));
        boolean rejected = false;
        try {
            OwnedMtlCategoryQueryController.validateQuery(QueryRequest.parse("{\"p\":{\"unknown_SEQ\":1}}", null));
        } catch (IllegalArgumentException expected) { rejected = true; }
        if (!rejected) throw new AssertionError("unknown category query parameter accepted");
        System.out.println("owned material category query controller contract checks passed");
    }
}
