/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api;

import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.ApiConfigKit;
import java.util.Objects;

public class Weixin {
    protected String appId;

    public static Weixin use(String appId) {
        Objects.requireNonNull(appId, "appId can not be null");
        return new Weixin(appId);
    }

    public static Weixin use() {
        return new Weixin(null);
    }

    Weixin(String appId) {
        this.appId = appId;
    }

    public ApiResult call(Call call) {
        if (this.appId != null) {
            try {
                ApiConfigKit.setThreadLocalAppId(this.appId);
                ApiResult apiResult = call.run();
                return apiResult;
            }
            finally {
                ApiConfigKit.removeThreadLocalAppId();
            }
        }
        return call.run();
    }

    @FunctionalInterface
    public static interface Call {
        public ApiResult run();
    }
}
