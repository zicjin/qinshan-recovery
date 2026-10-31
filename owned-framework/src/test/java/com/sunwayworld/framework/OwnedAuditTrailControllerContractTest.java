package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedAuditTrailController;
import java.lang.reflect.Method;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Static contract guard for the source-owned audit-trail read aliases. */
public final class OwnedAuditTrailControllerContractTest {
    public static void main(String[] args) {
        int mappings = 0; boolean records = false; boolean lines = false;
        boolean status = false; boolean statusById = false;
        for (Method method : OwnedAuditTrailController.class.getDeclaredMethods()) {
            PostMapping mapping = method.getAnnotation(PostMapping.class);
            if (mapping != null) {
                mappings++;
                for (String path : mapping.path()) {
                    if (path.endsWith("/at-records/queries")) records = true;
                    if (path.endsWith("/at-records/record-lines/queries")) lines = true;
                }
            }
            GetMapping statusMapping = method.getAnnotation(GetMapping.class);
            if (statusMapping != null) {
                for (String path : statusMapping.path()) {
                    if (path.endsWith("/audit-records/info/status")) status = true;
                    if (path.endsWith("/audit-records/{id}/info/status")) statusById = true;
                }
            }
        }
        if (mappings != 2 || !records || !lines || !status || !statusById) {
            throw new AssertionError("audit trail route aliases are incomplete: "
                    + mappings + "/" + records + "/" + lines + "/" + status + "/" + statusById);
        }
        System.out.println("owned audit trail controller contract checks passed (record, line and status queries)");
    }
}
