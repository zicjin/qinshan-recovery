package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedItemTaskQueryController;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

public final class OwnedItemTaskQueryControllerContractTest {
    public static void main(String[] args) {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedItemTaskQueryController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class); if (post != null) paths.addAll(Arrays.asList(post.path()));
            GetMapping get = method.getAnnotation(GetMapping.class); if (get != null) paths.addAll(Arrays.asList(get.path()));
        }
        String[] names = {"complaints","corrections","emc-cars","improvements","inconformitys","internal-audits","leave-applies","leave-replenishs","lm/overtimes","manage-reviews","precautions","quality-controls","quality-supervisions","quotation/quotationregisters","report-queries","scm/conven-mtls","scm/equipt-purchs","scm/office-mtls","scm/standard-mtls"};
        for (String name : names) {
            if (!paths.contains("/secure/emc/module/item/" + name + "/queries/tasks")) throw new AssertionError("missing item task route " + name);
            if (!paths.contains("/secure/emc/module/item/" + name + "/info/status")) throw new AssertionError("missing item status route " + name);
            if (!paths.contains("/secure/emc/module/item/" + name + "/queries")) throw new AssertionError("missing item query route " + name);
            if (!paths.contains("/secure/emc/module/item/" + name + "/queries/raw")) throw new AssertionError("missing item raw route " + name);
            if (!paths.contains("/secure/emc/module/item/" + name + "/queries/searchable")) throw new AssertionError("missing item searchable route " + name);
            if (!paths.contains("/secure/emc/module/item/" + name + "/{id}")) throw new AssertionError("missing item detail route " + name);
        }
        System.out.println("owned item task/query/detail contract checks passed (19 aliases)");
    }
}
