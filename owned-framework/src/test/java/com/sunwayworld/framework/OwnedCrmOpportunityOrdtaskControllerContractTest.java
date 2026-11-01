package com.sunwayworld.framework;
import com.sunwayworld.owned.internal.web.OwnedCrmOpportunityOrdtaskController;
import java.lang.reflect.Method; import java.util.*;
import org.springframework.web.bind.annotation.*;
public final class OwnedCrmOpportunityOrdtaskControllerContractTest {
 public static void main(String[] args){Set<String> p=new HashSet<String>(); for(Method m:OwnedCrmOpportunityOrdtaskController.class.getDeclaredMethods()){PostMapping post=m.getAnnotation(PostMapping.class); if(post!=null)p.addAll(Arrays.asList(post.path())); GetMapping get=m.getAnnotation(GetMapping.class); if(get!=null)p.addAll(Arrays.asList(get.path()));} for(String x:new String[]{"/secure/emc/module/mdm/crm/ordtasks/info/status","/secure/emc/module/mdm/crm/ordtasks/queries","/secure/emc/module/mdm/crm/ordtasks/queries/raw","/secure/emc/module/mdm/crm/ordtasks/queries/searchable"})if(!p.contains(x))throw new AssertionError("missing CRM route "+x); System.out.println("owned CRM opportunity order-task contract checks passed");}
}
