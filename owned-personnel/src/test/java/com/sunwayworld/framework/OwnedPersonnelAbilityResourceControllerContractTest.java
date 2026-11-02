package com.sunwayworld.framework;

import com.sunwayworld.owned.personnel.OwnedPersonnelAbilityResourceController;
import com.sunwayworld.owned.personnel.OwnedPersonnelConfiguration;
import java.lang.reflect.Method;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

public final class OwnedPersonnelAbilityResourceControllerContractTest {
    public static void main(String[] args) {
        boolean query=false, raw=false, searchable=false, status=false, detail=false, detailStatus=false;
        for (Method method : OwnedPersonnelAbilityResourceController.class.getDeclaredMethods()) {
            PostMapping post=method.getAnnotation(PostMapping.class); if(post!=null)for(String path:post.path()){query|=path.endsWith("/queries");raw|=path.endsWith("/queries/raw");searchable|=path.endsWith("/queries/searchable");if(path.contains("{resource}"))throw new AssertionError(path);}
            GetMapping get=method.getAnnotation(GetMapping.class); if(get!=null)for(String path:get.path()){status|=path.endsWith("/info/status");detail|=path.endsWith("/{id}");detailStatus|=path.endsWith("/{id}/info/status");if(path.contains("{resource}"))throw new AssertionError(path);}
        }
        if(!(query&&raw&&searchable&&status&&detail&&detailStatus))throw new AssertionError("personnel ability mappings incomplete");
        boolean registered=false;for(Method method:OwnedPersonnelConfiguration.class.getDeclaredMethods())if(method.getReturnType()==OwnedPersonnelAbilityResourceController.class)registered=true;
        if(!registered)throw new AssertionError("personnel ability controller is not registered");
        System.out.println("owned personnel ability controller contract checks passed");
    }
}
