/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api;

import com.sunwayworld.cloud.module.mobile.wechat.helper.cache.DefaultAccessTokenCache;
import com.sunwayworld.cloud.module.mobile.wechat.helper.cache.IAccessTokenCache;
import com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api.ApiConfig;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApiConfigKit {
    private static final ThreadLocal<String> TL = new ThreadLocal();
    private static final Map<String, ApiConfig> CFG_MAP = new ConcurrentHashMap<String, ApiConfig>();
    private static final String DEFAULT_CFG_KEY = "_default_qywx_key_";
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
        return CFG_MAP.put(apiConfig.getAgentId(), apiConfig);
    }

    public static ApiConfig removeApiConfig(ApiConfig apiConfig) {
        return ApiConfigKit.removeApiConfig(apiConfig.getAgentId());
    }

    public static ApiConfig removeApiConfig(String agentId) {
        return CFG_MAP.remove(agentId);
    }

    public static void setThreadLocalAgentId(String agentId) {
        if (StringUtils.isBlank(agentId)) {
            agentId = CFG_MAP.get(DEFAULT_CFG_KEY).getAgentId();
        }
        TL.set(agentId);
    }

    public static void removeThreadLocalAgentId() {
        TL.remove();
    }

    public static String getAgentId() {
        String agentId = TL.get();
        if (StringUtils.isBlank(agentId)) {
            agentId = CFG_MAP.get(DEFAULT_CFG_KEY).getAgentId();
        }
        return agentId;
    }

    public static ApiConfig getApiConfig() {
        return ApiConfigKit.getApiConfig(ApiConfigKit.getAgentId());
    }

    public static ApiConfig getApiConfig(String agentId) {
        ApiConfig cfg = CFG_MAP.get(agentId);
        if (cfg == null) {
            throw new IllegalStateException("\u9700\u7cfb\u7edf\u542f\u52a8\u540e\uff0c\u4e8b\u5148\u8c03\u7528 ApiConfigKit.putApiConfig(apiConfig) \u5c06 agentId\u5bf9\u5e94\u7684 ApiConfig \u5bf9\u8c61\u5b58\u5165\uff0c\u624d\u53ef\u4ee5\u4f7f\u7528 ApiConfigKit.getApiConfig() \u7cfb\u5217\u65b9\u6cd5");
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
