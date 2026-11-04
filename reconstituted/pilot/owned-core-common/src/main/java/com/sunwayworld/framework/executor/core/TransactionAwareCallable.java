/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.executor.core;

import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.executor.aspect.TransactionAwareAspect;
import com.sunwayworld.framework.executor.core.TransactionAwareConnectionWrapper;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.tenant.TenantContext;
import java.sql.Connection;
import java.util.Locale;
import java.util.concurrent.Callable;
import org.springframework.context.i18n.LocaleContextHolder;

public class TransactionAwareCallable<V>
implements Callable<V> {
    private Connection connection;
    private Callable<V> callable;
    private final Locale locale;
    private final LoginUser loginUser;
    private final String tenant;

    public TransactionAwareCallable(Connection connection, Callable<V> callable) {
        this.connection = new TransactionAwareConnectionWrapper(connection);
        this.callable = callable;
        this.locale = LocaleContextHolder.getLocale();
        this.loginUser = LocalContextHelper.getLoginPrincipal();
        this.tenant = TenantContext.getTenant();
    }

    @Override
    public V call() throws Exception {
        try {
            TransactionAwareAspect.setLocalConnection(this.connection);
            LocaleContextHolder.setLocale(this.locale);
            LocalContextHelper.setUserLogin(this.loginUser);
            TenantContext.setTenant(this.tenant);
            V v = this.callable.call();
            return v;
        }
        finally {
            TransactionAwareAspect.removeLocalConnection();
            LocaleContextHolder.resetLocaleContext();
            LocalContextHelper.removeUserLogin();
            TenantContext.removeTenant();
        }
    }
}
