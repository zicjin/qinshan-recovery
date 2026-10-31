package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedMaterialApplyQueryController;
import java.lang.reflect.Method;
import org.springframework.web.bind.annotation.PostMapping;

public final class OwnedMaterialApplyQueryControllerContractTest {
    public static void main(String[] args) {
        int post = 0;
        boolean withdrawable = false, undoable = false;
        for (Method method : OwnedMaterialApplyQueryController.class.getDeclaredMethods()) {
            PostMapping mapping = method.getAnnotation(PostMapping.class);
            if (mapping != null) {
                post++;
                for (String path : mapping.path()) {
                    if (path.endsWith("/queries/withdrawable")) withdrawable = true;
                    if (path.endsWith("/queries/undoable")) undoable = true;
                }
            }
        }
        if (post != 1) throw new AssertionError("material apply query route count=" + post);
        if (!withdrawable || !undoable) throw new AssertionError("material apply workflow query aliases are missing");
        if (!"approve".equals(OwnedMaterialApplyQueryController.workflowProcessStatus("/queries/withdrawable"))) {
            throw new AssertionError("withdrawable query status filter is not approve");
        }
        if (!"done".equals(OwnedMaterialApplyQueryController.workflowProcessStatus("/queries/undoable"))) {
            throw new AssertionError("undoable query status filter is not done");
        }
        System.out.println("owned material apply query controller contract checks passed");
    }
}
