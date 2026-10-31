package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedWorkspaceController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** Contract guard for the source-owned original workspace route facade. */
public final class OwnedWorkspaceControllerContractTest {
    public static void main(String[] args) throws Exception {
        RequestMapping mapping = OwnedWorkspaceController.class.getAnnotation(RequestMapping.class);
        if (mapping == null || mapping.value().length != 2) {
            throw new AssertionError("workspace route aliases missing");
        }
        OwnedWorkspaceController controller = new OwnedWorkspaceController();
        if (!"emc/module/index/workspace/index".equals(controller.index())
                || !"emc/module/index/workspace/emc-todo".equals(controller.todo())
                || !"emc/module/index/workspace/emc-report".equals(controller.report())) {
            throw new AssertionError("workspace view names changed");
        }
        int mappedMethods = 0;
        for (java.lang.reflect.Method method : OwnedWorkspaceController.class.getDeclaredMethods()) {
            if (method.getAnnotation(GetMapping.class) != null) mappedMethods++;
        }
        if (mappedMethods != 10) throw new AssertionError("workspace route count=" + mappedMethods);
        System.out.println("owned workspace controller contract checks passed (" + mappedMethods + " routes)");
    }
}
