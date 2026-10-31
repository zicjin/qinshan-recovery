package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedWorkspacePanelController;
import java.lang.reflect.Method;
import org.springframework.web.bind.annotation.GetMapping;

/** Contract guard for the imported original workspace iframe route set. */
public final class OwnedWorkspacePanelControllerContractTest {
    public static void main(String[] args) throws Exception {
        String[] routes = {
            "/module/index/workspaces/download-attachment",
            "/module/index/workspaces/report",
            "/module/index/workspaces/notice-panel",
            "/module/index/workspaces/todo-panel",
            "/module/index/workspaces/todo"
        };
        java.util.Set<String> mappedRoutes = new java.util.HashSet<String>();
        for (Method method : OwnedWorkspacePanelController.class.getDeclaredMethods()) {
            GetMapping mapping = method.getAnnotation(GetMapping.class);
            if (mapping != null) {
                for (String route : mapping.path()) mappedRoutes.add(route);
            }
        }
        if (mappedRoutes.size() != routes.length) throw new AssertionError("workspace panel route count=" + mappedRoutes.size());
        for (String route : routes) if (!mappedRoutes.contains(route)) throw new AssertionError("missing workspace panel route: " + route);
        System.out.println("owned workspace panel controller contract checks passed (" + mappedRoutes.size() + " routes)");
    }
}
