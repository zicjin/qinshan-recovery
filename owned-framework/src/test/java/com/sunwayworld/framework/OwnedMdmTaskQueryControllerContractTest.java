package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedMdmTaskQueryController;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

public final class OwnedMdmTaskQueryControllerContractTest {
    public static void main(String[] args) {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedMdmTaskQueryController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class); if (post != null) paths.addAll(Arrays.asList(post.path()));
            GetMapping get = method.getAnnotation(GetMapping.class); if (get != null) paths.addAll(Arrays.asList(get.path()));
        }
        String[] names={"file-modifications","file/file-applies","invoices","purch-applies","signatures"};
        for(String name:names){if(!paths.contains("/secure/emc/module/mdm/"+name+"/queries/tasks"))throw new AssertionError("missing MDM task route "+name);if(!paths.contains("/secure/emc/module/mdm/"+name+"/info/status"))throw new AssertionError("missing MDM status route "+name);}
        System.out.println("owned MDM task-query contract checks passed (5 aliases)");
    }
}
