package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import com.sunwayworld.owned.internal.web.OwnedProjectQcQueryController;
import java.lang.reflect.Method;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

public final class OwnedProjectQcQueryControllerContractTest {
    public static void main(String[] args) {
        boolean status=false, query=false, heads=false, rows=false;
        for (Method method : OwnedProjectQcQueryController.class.getDeclaredMethods()) {
            GetMapping get=method.getAnnotation(GetMapping.class); if(get!=null&&method.getName().equals("status")&&get.path().length==2)status=true;
            PostMapping post=method.getAnnotation(PostMapping.class); if(post!=null){if(method.getName().equals("query")&&post.path().length==3)query=true;if(method.getName().equals("tableHeads"))heads=true;if(method.getName().equals("tableRows"))rows=true;}
        }
        if(!status||!query||!heads||!rows)throw new AssertionError("project-qc read aliases incomplete");
        boolean registered=false;for(Method method:OwnedIdentityConfiguration.class.getDeclaredMethods())if(method.getReturnType()==OwnedProjectQcQueryController.class)registered=true;
        if(!registered)throw new AssertionError("project-qc controller is not registered");
        try {java.nio.file.Path p=java.nio.file.Paths.get("owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedProjectQcQueryController.java");if(!java.nio.file.Files.exists(p))p=java.nio.file.Paths.get("src/main/java/com/sunwayworld/owned/internal/web/OwnedProjectQcQueryController.java");String s=new String(java.nio.file.Files.readAllBytes(p),"UTF-8");if(!s.contains("T_EMC_PROJECT_QC")||!s.contains("T_EMC_PROJECT_QC_LINE")||!s.contains("P.CREATEDBYORGID"))throw new AssertionError("project-qc source tables/scope missing");}catch(java.io.IOException e){throw new AssertionError(e);}
        System.out.println("owned project-qc query contract checks passed");
    }
}
