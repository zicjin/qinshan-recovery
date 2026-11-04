/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.tenant;

import com.sunwayworld.framework.utils.StringUtils;
import java.util.Optional;

public class TenantContext {
    private static final ThreadLocal<String> tenantHolder = new ThreadLocal();

    public static void setTenant(String tenant) {
        tenantHolder.set(tenant);
    }

    public static String getTenant() {
        return Optional.ofNullable(tenantHolder.get()).orElse("master");
    }

    public static void removeTenant() {
        tenantHolder.remove();
    }

    public static boolean isMaster() {
        return tenantHolder.get() == null || "master".equals(tenantHolder.get());
    }

    public static boolean isMaster(String tenant) {
        return tenant == null || "master".equals(tenant);
    }

    public static String getTenantCacheNameSuffix() {
        if ("master".equals(TenantContext.getTenant())) {
            return "";
        }
        return "#@#" + TenantContext.getTenant();
    }

    public static boolean endsWithAnyTenantCacheNameSuffix(String name) {
        if (StringUtils.isBlank(name)) {
            return false;
        }
        return name.matches(".+(#@#)[^#@]+");
    }

    public static String removeTenantCacheNameSuffix(String name) {
        if (!name.contains("#@#")) {
            return name;
        }
        return name.substring(0, name.lastIndexOf("#@#"));
    }
}
