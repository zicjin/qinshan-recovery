package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import com.sunwayworld.owned.internal.web.OwnedProjectEditLogQueryController;
import java.lang.reflect.Method;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

public final class OwnedProjectEditLogQueryControllerContractTest {
    public static void main(String[] args) {
        boolean status=false, query=false;
        for (Method method : OwnedProjectEditLogQueryController.class.getDeclaredMethods()) {
            GetMapping get=method.getAnnotation(GetMapping.class); if(get!=null&&get.path().length==1)status=true;
            PostMapping post=method.getAnnotation(PostMapping.class); if(post!=null&&post.path().length==3)query=true;
        }
        if(!status||!query)throw new AssertionError("project-edit-log aliases incomplete");
        boolean registered=false;for(Method method:OwnedIdentityConfiguration.class.getDeclaredMethods())if(method.getReturnType()==OwnedProjectEditLogQueryController.class)registered=true;
        if(!registered)throw new AssertionError("project-edit-log controller is not registered");
        try {
            java.nio.file.Path path=java.nio.file.Paths.get("owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedProjectEditLogQueryController.java");
            if(!java.nio.file.Files.exists(path))path=java.nio.file.Paths.get("lims-recovery/owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedProjectEditLogQueryController.java");
            if(!java.nio.file.Files.exists(path))path=java.nio.file.Paths.get("src/main/java/com/sunwayworld/owned/internal/web/OwnedProjectEditLogQueryController.java");
            String source=new String(java.nio.file.Files.readAllBytes(path),"UTF-8");
            for(String required:new String[]{"T_EMC_PROJECT_EDIT_LOG","T.CREATEDBYORGID=?","ROW_NUMBER()","pageNumber","totalMap","ext$","contentType(MediaType.APPLICATION_JSON)"})if(!source.contains(required))throw new AssertionError("missing contract: "+required);
        }catch(java.io.IOException error){throw new AssertionError(error);}
        System.out.println("owned project-edit-log query contract checks passed");
    }
}
