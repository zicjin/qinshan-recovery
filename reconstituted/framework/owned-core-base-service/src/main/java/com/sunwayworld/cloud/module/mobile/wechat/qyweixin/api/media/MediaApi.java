/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api.media;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.media.MediaFile;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api.AccessTokenApi;
import com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api.media.MediaMpNews;
import com.sunwayworld.framework.utils.JsonUtils;
import java.io.File;
import java.util.HashMap;

public class MediaApi {
    private static String upload_url = "https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token=";
    private static String get_url = "https://qyapi.weixin.qq.com/cgi-bin/media/get?access_token=";
    private static String add_news_url = "https://qyapi.weixin.qq.com/cgi-bin/material/add_mpnews?access_token=";
    private static String addMaterialUrl = "https://qyapi.weixin.qq.com/cgi-bin/material/add_material?agentid=AGENTID&type=TYPE&access_token=ACCESS_TOKEN";
    private static String del_material_url = "https://qyapi.weixin.qq.com/cgi-bin/material/del?access_token=ACCESS_TOKEN&agentid=AGENTID&media_id=MEDIA_ID";
    private static String update_news_url = "https://api.weixin.qq.com/cgi-bin/material/update_news?access_token=";
    private static String get_materialcount_url = "https://qyapi.weixin.qq.com/cgi-bin/material/get_count?access_token=ACCESS_TOKEN&agentid=AGENTID";
    private static String batchget_material_url = "https://qyapi.weixin.qq.com/cgi-bin/material/batchget?access_token=";

    public static ApiResult uploadMedia(MediaType mediaType, File file) {
        String url = upload_url + AccessTokenApi.getAccessTokenStr() + "&type=" + mediaType.get();
        String jsonStr = HttpUtils.upload(url, file, null);
        return new ApiResult(jsonStr);
    }

    public static MediaFile getMedia(String media_id) {
        String url = get_url + AccessTokenApi.getAccessTokenStr() + "&media_id=" + media_id;
        return HttpUtils.download(url);
    }

    public static ApiResult addNews(MediaMpNews mediaMpNews) {
        String url = add_news_url + AccessTokenApi.getAccessTokenStr();
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(mediaMpNews));
        return new ApiResult(jsonResult);
    }

    @Deprecated
    public static ApiResult addMaterial(File file, String agentid, MediaType type) {
        String url = addMaterialUrl.replace("AGENTID", agentid).replace("TYPE", type.get()).replace("ACCESS_TOKEN", AccessTokenApi.getAccessTokenStr());
        String jsonResult = HttpUtils.upload(url, file, null);
        return new ApiResult(jsonResult);
    }

    @Deprecated
    public static ApiResult delMaterial(String media_id, String agentid) {
        String url = del_material_url.replace("AGENTID", agentid).replace("MEDIA_ID", media_id).replace("ACCESS_TOKEN", AccessTokenApi.getAccessTokenStr());
        String jsonResult = HttpUtils.get(url);
        return new ApiResult(jsonResult);
    }

    @Deprecated
    public static ApiResult updateNews(MediaMpNews mediaMpNews) {
        String url = update_news_url + AccessTokenApi.getAccessTokenStr();
        String jsonResult = HttpUtils.post(url, JsonUtils.toJsonString(mediaMpNews));
        return new ApiResult(jsonResult);
    }

    @Deprecated
    public static ApiResult getMaterialCount(String agentid) {
        String url = get_materialcount_url.replace("ACCESS_TOKEN", AccessTokenApi.getAccessTokenStr()).replace("AGENTID", agentid);
        System.out.println(url);
        String jsonResult = HttpUtils.get(url);
        return new ApiResult(jsonResult);
    }

    @Deprecated
    public static ApiResult batchGetMaterial(MediaType mediaType, int offset, int count, int agentid) {
        String url = batchget_material_url + AccessTokenApi.getAccessTokenStr();
        if (offset < 0) {
            offset = 0;
        }
        if (count > 20) {
            count = 20;
        }
        if (count < 1) {
            count = 1;
        }
        HashMap<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("type", mediaType.get());
        dataMap.put("offset", offset);
        dataMap.put("count", count);
        dataMap.put("agentid", agentid);
        String jsonResult = HttpUtils.post(url, JsonUtils.toJsonString(dataMap));
        return new ApiResult(jsonResult);
    }

    public static enum MediaType {
        IMAGE,
        VOICE,
        VIDEO,
        FILE;


        public String get() {
            return this.name().toLowerCase();
        }
    }
}
