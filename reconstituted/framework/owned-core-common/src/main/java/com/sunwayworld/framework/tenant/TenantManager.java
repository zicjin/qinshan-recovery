package com.sunwayworld.framework.tenant;

import com.sunwayworld.framework.executor.ExecutorHelper;
import com.sunwayworld.framework.executor.core.TraceCallable;
import com.sunwayworld.framework.executor.core.TraceRunnable;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.module.admin.tenant.bean.CoreAdminTenantBean;
import com.sunwayworld.module.admin.tenant.service.CoreAdminTenantService;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
public class TenantManager {
   @Autowired
   private DataSource dataSource;
   @Lazy
   @Autowired
   private CoreAdminTenantService tenantService;

   public static final native DataSource dataSource(String poolName, String url, String username, String password);

   public boolean isTenantActivated() {
      return this.dataSource instanceof TenantDataSourceRouter;
   }

   public synchronized void reload() {
      if (this.isTenantActivated()) {
         this.masterCall(
            "TENANT_RELOADING",
            () -> {
               TenantDataSourceRouter dsr = (TenantDataSourceRouter)this.dataSource;
               List<CoreAdminTenantBean> tenantList = this.tenantService.selectAll();

               for (CoreAdminTenantBean tenant : tenantList) {
                  if ("0".equals(tenant.getActivatedFlag())) {
                     if (dsr.exists(tenant.getId())) {
                        dsr.remove(tenant.getId());
                     }
                  } else if (!dsr.exists(tenant.getId())) {
                     dsr.add(tenant.getId(), dataSource(tenant.getTenantName(), tenant.getUrl(), tenant.getUsername(), tenant.getPassword()));
                  }
               }

               List<String> removeTenantList = dsr.getResolvedDataSources()
                  .keySet()
                  .stream()
                  .filter(k -> !"master".equals(k) && tenantList.stream().noneMatch(t -> t.getId().equals(k)))
                  .collect(Collectors.toList());
               if (!removeTenantList.isEmpty()) {
                  removeTenantList.forEach(t -> dsr.remove(t));
               }
            }
         );
      }
   }

   public void masterAsyncCall(String spanName, Runnable runnable) {
      ExecutorHelper.submit(TraceRunnable.of(spanName, "master", runnable));
   }

   public void masterCall(String spanName, Runnable runnable) {
      ExecutorHelper.submitAndGet(TraceCallable.of(spanName, "master", this.getCallable(runnable)));
   }

   public <T> T masterCall(String spanName, Callable<T> callable) {
      return ExecutorHelper.submitAndGet(TraceCallable.of(spanName, "master", callable));
   }

   public void tenantAsyncCall(String spanName, String tenant, Runnable runnable) {
      ExecutorHelper.submit(TraceRunnable.of(spanName, tenant, runnable));
   }

   public void tenantCall(String spanName, String tenant, Runnable runnable) {
      ExecutorHelper.submitAndGet(TraceCallable.of(spanName, tenant, this.getCallable(runnable)));
   }

   public void asyncCall(String spanName, Runnable runnable) {
      if (this.isTenantActivated()) {
         TenantDataSourceRouter router = (TenantDataSourceRouter)this.dataSource;
         router.getResolvedDataSources().forEach((k, v) -> ExecutorHelper.submit(TraceRunnable.of(spanName, k, runnable)));
      } else {
         ExecutorHelper.submit(TraceRunnable.of(spanName, runnable));
      }
   }

   public void call(String spanName, Runnable runnable, String... ignoreTenants) {
      if (this.isTenantActivated()) {
         TenantDataSourceRouter router = (TenantDataSourceRouter)this.dataSource;
         router.getResolvedDataSources().forEach((k, v) -> {
            if (!ArrayUtils.contains(ignoreTenants, k)) {
               ExecutorHelper.submitAndGet(TraceCallable.of(spanName, k, this.getCallable(runnable)));
            }
         });
      } else {
         runnable.run();
      }
   }

   public <T> T call(String spanName, String tenant, Callable<T> callable) {
      return ExecutorHelper.submitAndGet(TraceCallable.of(spanName, tenant, callable));
   }

   private Callable<Object> getCallable(Runnable runnable) {
      return new Callable<Object>() {
         @Override
         public Object call() {
            runnable.run();
            return null;
         }
      };
   }
}
