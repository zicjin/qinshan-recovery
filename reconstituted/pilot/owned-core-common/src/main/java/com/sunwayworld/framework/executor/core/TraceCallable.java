package com.sunwayworld.framework.executor.core;

import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.tenant.TenantContext;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.Callable;
import org.springframework.context.i18n.LocaleContextHolder;

public abstract class TraceCallable<V> extends PriorityTask implements Callable<V> {
   private final ThreadPriority priority;
   private final String spanName;
   private final Locale locale;
   private final LoginUser loginUser;
   private final String tenant;

   public TraceCallable(String spanName) {
      this(spanName, TenantContext.getTenant(), ThreadPriority.Normal);
   }

   public TraceCallable(String spanName, ThreadPriority priority) {
      this(spanName, TenantContext.getTenant(), priority);
   }

   public TraceCallable(String spanName, String tenant, ThreadPriority priority) {
      this.spanName = spanName;
      this.priority = priority;
      this.locale = LocaleContextHolder.getLocale();
      this.loginUser = LocalContextHelper.getLoginPrincipal();
      this.tenant = tenant;
   }

   public String getSpanName() {
      return this.spanName;
   }

   @Override
   public final int getPriorityValue() {
      return this.priority.getValue();
   }

   @Override
   public Locale getLocale() {
      return this.locale;
   }

   @Override
   public final String getTenant() {
      return this.tenant;
   }

   @Override
   public final LoginUser getLoginUser() {
      return this.loginUser;
   }

   @Override
   public String toString() {
      return this.getSpanName() + "[" + Optional.ofNullable(this.priority).orElse(ThreadPriority.Normal).name() + "]{" + this.getTenant() + "}";
   }

   public static <V> TraceCallable<V> of(Callable<V> callable) {
      return callable instanceof TraceCallable ? (TraceCallable)callable : of("default", TenantContext.getTenant(), ThreadPriority.Normal, callable);
   }

   public static <V> TraceCallable<V> of(String spanName, Callable<V> callable) {
      return of(spanName, TenantContext.getTenant(), ThreadPriority.Normal, callable);
   }

   public static <V> TraceCallable<V> of(String spanName, String tenant, Callable<V> callable) {
      return of(spanName, tenant, ThreadPriority.Normal, callable);
   }

   public static <V> TraceCallable<V> of(String spanName, ThreadPriority priority, Callable<V> callable) {
      return of(spanName, TenantContext.getTenant(), priority, callable);
   }

   public static <V> TraceCallable<V> of(String spanName, String tenant, ThreadPriority priority, Callable<V> callable) {
      return new TraceCallable<V>(spanName, tenant, priority) {
         @Override
         public V call() throws Exception {
            Object var1;
            try {
               LocaleContextHolder.setLocale(this.getLocale());
               LocalContextHelper.setUserLogin(this.getLoginUser());
               TenantContext.setTenant(this.getTenant());
               var1 = callable.call();
            } finally {
               LocaleContextHolder.resetLocaleContext();
               LocalContextHelper.removeUserLogin();
               TenantContext.removeTenant();
            }

            return (V)var1;
         }
      };
   }
}
