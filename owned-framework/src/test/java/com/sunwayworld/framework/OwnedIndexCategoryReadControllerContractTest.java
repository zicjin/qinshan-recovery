package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import com.sunwayworld.owned.internal.web.OwnedIndexCategoryReadController;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.PostMapping;

public final class OwnedIndexCategoryReadControllerContractTest {
    public static void main(String[] args) {
        Set<String> paths = new HashSet<String>();
        for (Method m : OwnedIndexCategoryReadController.class.getDeclaredMethods()) {
            PostMapping p = m.getAnnotation(PostMapping.class); if (p != null) paths.addAll(Arrays.asList(p.path()));
        }
        require(paths, "/secure/emc/module/mdm/bizmdm/index-categories/queries/order-index/choosable");
        require(paths, "/secure/emc/module/mdm/bizmdm/index-categories/queries/select-by-order");
        boolean registered=false; for(Method m:OwnedIdentityConfiguration.class.getDeclaredMethods()) if(m.getReturnType()==OwnedIndexCategoryReadController.class) registered=true;
        if(!registered) throw new AssertionError("index-category controller is not registered");
        System.out.println("owned index-category read mapping scope checks passed");
    }
    private static void require(Set<String> paths,String path){if(!paths.contains(path))throw new AssertionError("missing index-category route: "+path);}
}
