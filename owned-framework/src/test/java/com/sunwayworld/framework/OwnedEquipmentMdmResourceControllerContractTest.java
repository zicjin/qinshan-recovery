package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedEquipmentMdmResourceController;
import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Contract guard for equipment MDM read resources and their route scope. */
public final class OwnedEquipmentMdmResourceControllerContractTest {
    private static final String BASE = "/secure/emc/module/mdm/basemdm/";
    private static final String EQUIP = "/secure/emc/module/mdm/equipt/";

    public static void main(String[] args) {
        Set<String> paths = mappedPaths();
        for (String resource : new String[] {"equipt-categories", "equipt-requisitions", "equipt-temp"}) requireFamily(paths, BASE + resource);
        for (String resource : new String[] {"equip-appearances", "equip-checks", "equip-verifications", "equipt-acq-params", "equipt-acq-results", "equipt-acq-scripts", "equipt-cali-result-norms", "equipt-calibrations", "equipt-cali-results"}) requireFamily(paths, EQUIP + resource);
        require(paths, EQUIP + "equipt-calibrations/{id}/info/status");
        require(paths, EQUIP + "equipt-cali-results/{id}/info/status");
        for (String resource : new String[] {"equip-checks", "equip-verifications", "equipt-cali-results", "equipt-per-checks"}) {
            require(paths, EQUIP + resource + "/queries/tasks");
            require(paths, EQUIP + resource + "/info/status");
        }
        require(paths, EQUIP + "equipt-acq-scripts/eas-results/queries");
        require(paths, "/secure/emc/module/mdm/bizmdm/tests/container-maintenance/queries/raw");
        for (String path : paths) if (path.contains("{resource}")) throw new AssertionError("unbounded equipment MDM path: " + path);
        if (paths.contains(BASE + "staff-ability-evaluations/queries")) throw new AssertionError("personnel route captured by equipment MDM controller");
        boolean registered = false;
        for (Method method : OwnedIdentityConfiguration.class.getDeclaredMethods()) {
            if (method.getReturnType() == OwnedEquipmentMdmResourceController.class) registered = true;
        }
        if (!registered) throw new AssertionError("owned equipment MDM controller is not registered");
        System.out.println("owned equipment MDM mapping scope checks passed");
    }

    private static Set<String> mappedPaths() {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedEquipmentMdmResourceController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class);
            if (post != null) paths.addAll(Arrays.asList(post.path()));
            GetMapping get = method.getAnnotation(GetMapping.class);
            if (get != null) paths.addAll(Arrays.asList(get.path()));
        }
        return paths;
    }

    private static void requireFamily(Set<String> paths, String base) {
        require(paths, base + "/queries");
        require(paths, base + "/queries/raw");
        require(paths, base + "/queries/searchable");
        require(paths, base + "/info/status");
        require(paths, base + "/{id}");
    }

    private static void require(Set<String> paths, String path) {
        if (!paths.contains(path)) throw new AssertionError("missing equipment MDM path: " + path);
    }
}
