package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedAuditRecordController;
import java.lang.reflect.Method;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Static route guard for the source-owned inherited audit-record resource. */
public final class OwnedAuditRecordControllerContractTest {
    public static void main(String[] args) {
        boolean query = false; boolean raw = false; boolean searchable = false; boolean detail = false;
        boolean column = false; boolean print = false; boolean validate = false; boolean unique = false;
        for (Method method : OwnedAuditRecordController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class);
            if (post != null) for (String path : post.path()) {
                if (path.endsWith("/audit-records/queries")) query = true;
                if (path.endsWith("/audit-records/queries/raw")) raw = true;
                if (path.endsWith("/audit-records/queries/searchable")) searchable = true;
            }
            GetMapping get = method.getAnnotation(GetMapping.class);
            if (get != null) for (String path : get.path()) if (path.endsWith("/audit-records/{id}")) detail = true;
            if (get != null) for (String path : get.path()) {
                if (path.endsWith("/audit-records/{id}/columns/{column}")) column = true;
                if (path.endsWith("/audit-records/{id}/action/print")) print = true;
            }
            if (post != null) for (String path : post.path()) {
                if (path.endsWith("/audit-records/{id}/action/validate")) validate = true;
                if (path.contains("/audit-records/") && path.contains("validate-unique")) unique = true;
            }
        }
        if (!query || !raw || !searchable || !detail || !column || !print || !validate || !unique) {
            throw new AssertionError("audit-record source-owned route mappings are incomplete: "
                    + query + "/" + raw + "/" + searchable + "/" + detail + "/"
                    + column + "/" + print + "/" + validate + "/" + unique);
        }
        System.out.println("owned audit-record controller contract checks passed (query/raw/searchable/detail/column/print/validate/unique)");
    }
}
