/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api.shakearound;

import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.AccessTokenApi;
import java.io.File;

public class ShakeAroundMaterialApi {
    private static String materialAddUrl = "https://api.weixin.qq.com/shakearound/material/add?access_token=";

    public static ApiResult addMaterial(File file, MaterialType materialType) {
        String url = materialAddUrl + AccessTokenApi.getAccessTokenStr() + "&type=" + materialType.name();
        String jsonResult = HttpUtils.upload(url, file, null);
        return new ApiResult(jsonResult);
    }

    public static ApiResult addMaterial(File file) {
        return ShakeAroundMaterialApi.addMaterial(file, MaterialType.icon);
    }

    public static enum MaterialType {
        icon,
        license;

    }
}
