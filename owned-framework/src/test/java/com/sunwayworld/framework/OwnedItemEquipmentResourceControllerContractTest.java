package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import com.sunwayworld.owned.internal.web.OwnedItemEquipmentResourceController;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Contract guard for item equipment read resources and their route scope. */
public final class OwnedItemEquipmentResourceControllerContractTest {
    private static final String ITEM = "/secure/emc/module/item/";
    private static final String SCM = "/secure/emc/module/item/scm/";

    public static void main(String[] args) {
        Set<String> paths = mappedPaths();
        String[] resources = {"emc-patrols", "equipt-assay-plans"};
        for (String resource : resources) {
            require(paths, ITEM + resource + "/queries");
            require(paths, ITEM + resource + "/queries/raw");
            require(paths, ITEM + resource + "/queries/searchable");
            require(paths, ITEM + resource + "/info/status");
            require(paths, ITEM + resource + "/1");
        }
        require(paths, SCM + "equipt-purchs/queries");
        require(paths, SCM + "equipt-purchs/queries/raw");
        require(paths, SCM + "equipt-purchs/queries/searchable");
        require(paths, SCM + "equipt-purchs/info/status");
        require(paths, SCM + "equipt-purchs/1");
        require(paths, ITEM + "emc-patrols/queries/raw/month");
        require(paths, ITEM + "emc-patrols/queries/raw/month/patrolUser");
        require(paths, ITEM + "emc-patrols/queries/raw/month/handleUser");
        for (String path : paths) if (path.contains("{resource}")) throw new AssertionError("unbounded item resource path: " + path);
        for (String unsupported : new String[] {"staff-abality-checks", "staff-ability-evaluations", "equipt-purchs"}) {
            if (paths.contains(ITEM + unsupported + "/queries")) throw new AssertionError("item controller captured unsupported route: " + unsupported);
        }
        boolean registered = false;
        for (Method method : OwnedIdentityConfiguration.class.getDeclaredMethods()) {
            if (method.getReturnType() == OwnedItemEquipmentResourceController.class) registered = true;
        }
        if (!registered) throw new AssertionError("owned item equipment controller is not registered");
        System.out.println("owned item equipment mapping scope checks passed");
    }

    private static Set<String> mappedPaths() {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedItemEquipmentResourceController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class);
            if (post != null) paths.addAll(Arrays.asList(post.path()));
            GetMapping get = method.getAnnotation(GetMapping.class);
            if (get != null) paths.addAll(Arrays.asList(get.path()));
        }
        return paths;
    }

    private static void require(Set<String> paths, String path) {
        String expected = path.replace("/1", "/{id}");
        if (!paths.contains(expected)) throw new AssertionError("missing item equipment path: " + expected);
    }
}
