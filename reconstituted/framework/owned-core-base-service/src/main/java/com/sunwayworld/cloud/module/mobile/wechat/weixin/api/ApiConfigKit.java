/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api;

import com.sunwayworld.cloud.module.mobile.wechat.helper.cache.DefaultAccessTokenCache;
import com.sunwayworld.cloud.module.mobile.wechat.helper.cache.IAccessTokenCache;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.ApiConfig;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApiConfigKit {
    private static final ThreadLocal<String> TL = new ThreadLocal();
    private static final Map<String, ApiConfig> CFG_MAP = new ConcurrentHashMap<String, ApiConfig>();
    private static final String DEFAULT_CFG_KEY = "_default_wx_key_";
    private static boolean devMode = false;
    static IAccessTokenCache accessTokenCache = new DefaultAccessTokenCache();

    public static void setDevMode(boolean devMode) {
        ApiConfigKit.devMode = devMode;
    }

    public static boolean isDevMode() {
        return devMode;
    }

    public static ApiConfig putApiConfig(ApiConfig apiConfig) {
        if (CFG_MAP.size() == 0) {
            CFG_MAP.put(DEFAULT_CFG_KEY, apiConfig);
        }
        return CFG_MAP.put(apiConfig.getAppId(), apiConfig);
    }

    public static ApiConfig removeApiConfig(ApiConfig apiConfig) {
        return ApiConfigKit.removeApiConfig(apiConfig.getAppId());
    }

    public static ApiConfig removeApiConfig(String appId) {
        ApiConfig defaultApiConfig;
        ApiConfig removedConfig = CFG_MAP.remove(appId);
        if (removedConfig != null && (defaultApiConfig = CFG_MAP.get(DEFAULT_CFG_KEY)).getAppId().equals(removedConfig.getAppId())) {
            Iterator<Map.Entry<String, ApiConfig>> iterator;
            CFG_MAP.remove(DEFAULT_CFG_KEY);
            ApiConfig firstApiConfig = null;
            if (CFG_MAP.size() > 0 && (iterator = CFG_MAP.entrySet().iterator()).hasNext()) {
                Map.Entry<String, ApiConfig> entry = iterator.next();
                firstApiConfig = entry.getValue();
            }
            if (firstApiConfig != null) {
                CFG_MAP.put(DEFAULT_CFG_KEY, firstApiConfig);
            }
        }
        return removedConfig;
    }

    public static void removeAll() {
        CFG_MAP.clear();
    }

    public static void setThreadLocalAppId(String appId) {
        if (StringUtils.isBlank(appId)) {
            appId = CFG_MAP.get(DEFAULT_CFG_KEY).getAppId();
        }
        TL.set(appId);
    }

    public static void removeThreadLocalAppId() {
        TL.remove();
    }

    public static String getAppId() {
        String appId = TL.get();
        if (StringUtils.isBlank(appId)) {
            appId = CFG_MAP.get(DEFAULT_CFG_KEY).getAppId();
        }
        return appId;
    }

    public static ApiConfig getApiConfig() {
        String appId = ApiConfigKit.getAppId();
        return ApiConfigKit.getApiConfig(appId);
    }

    public static ApiConfig getApiConfig(String appId) {
        ApiConfig cfg = CFG_MAP.get(appId);
        if (cfg == null) {
            throw new IllegalStateException("\u9700\u7cfb\u7edf\u542f\u52a8\u540e\uff0c\u4e8b\u5148\u8c03\u7528 ApiConfigKit.putApiConfig(apiConfig) \u5c06 appId\u5bf9\u5e94\u7684 ApiConfig \u5bf9\u8c61\u5b58\u5165\uff0c\u624d\u53ef\u4ee5\u4f7f\u7528 ApiConfigKit.getApiConfig() \u7cfb\u5217\u65b9\u6cd5");
        }
        return cfg;
    }

    public static void setAccessTokenCache(IAccessTokenCache accessTokenCache) {
        ApiConfigKit.accessTokenCache = accessTokenCache;
    }

    public static IAccessTokenCache getAccessTokenCache() {
        return accessTokenCache;
    }
}
