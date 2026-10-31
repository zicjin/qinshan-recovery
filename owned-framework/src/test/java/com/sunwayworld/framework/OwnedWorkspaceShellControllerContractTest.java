package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedWorkspaceShellController;
import java.lang.reflect.Method;
import org.springframework.web.bind.annotation.GetMapping;

/** Contract guard for the source-owned root and original workspace entrypoint. */
public final class OwnedWorkspaceShellControllerContractTest {
    public static void main(String[] args) throws Exception {
        Method root = OwnedWorkspaceShellController.class.getMethod("root", javax.servlet.http.HttpServletRequest.class);
        Method workspace = OwnedWorkspaceShellController.class.getMethod("workspaces", javax.servlet.http.HttpServletRequest.class);
        if (root.getAnnotation(GetMapping.class) == null || workspace.getAnnotation(GetMapping.class) == null) {
            throw new AssertionError("workspace shell mappings missing");
        }
        if (!"/".equals(root.getAnnotation(GetMapping.class).value()[0])) {
            throw new AssertionError("root mapping changed");
        }
        if (!"/module/index/workspaces".equals(workspace.getAnnotation(GetMapping.class).value()[0])) {
            throw new AssertionError("original workspace mapping changed");
        }
        System.out.println("owned workspace shell contract checks passed");
    }
}
