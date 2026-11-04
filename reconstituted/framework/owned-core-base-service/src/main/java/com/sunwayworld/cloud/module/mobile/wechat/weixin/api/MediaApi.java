/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.media.MediaArticles;
import com.sunwayworld.cloud.module.mobile.wechat.helper.media.MediaFile;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.AccessTokenApi;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public class MediaApi {
    private static String upload_url = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=";
    private static String uploadVideoUrl = "http://file.api.weixin.qq.com/cgi-bin/media/uploadvideo?access_token=";
    private static String uploadNews = "https://api.weixin.qq.com/cgi-bin/media/uploadnews?access_token=";
    private static String get_url = "https://api.weixin.qq.com/cgi-bin/media/get?access_token=";
    private static String get_jssdk_media = "https://api.weixin.qq.com/cgi-bin/media/get/jssdk?access_token=";
    private static String add_news_url = "https://api.weixin.qq.com/cgi-bin/material/add_news?access_token=";
    private static String uploadImgUrl = "https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=";
    private static String addMaterialUrl = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=";
    private static String get_material_url = "https://api.weixin.qq.com/cgi-bin/material/get_material?access_token=";
    private static String del_material_url = "https://api.weixin.qq.com/cgi-bin/material/del_material?access_token=";
    private static String update_news_url = "https://api.weixin.qq.com/cgi-bin/material/update_news?access_token=";
    private static String get_materialcount_url = "https://api.weixin.qq.com/cgi-bin/material/get_materialcount?access_token=";
    private static String batchget_material_url = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=";

    public static ApiResult uploadMedia(MediaType mediaType, File file) {
        String url = upload_url + AccessTokenApi.getAccessTokenStr() + "&type=" + mediaType.get();
        String jsonStr = HttpUtils.upload(url, file, null);
        return new ApiResult(jsonStr);
    }

    public static ApiResult uploadVideo(String mediaId, String title, String description) {
        String url = uploadVideoUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, String> mapData = new HashMap<String, String>();
        mapData.put("media_id", mediaId);
        mapData.put("title", title);
        mapData.put("description", description);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(mapData));
        return new ApiResult(jsonResult);
    }

    public static ApiResult uploadNews(List<MediaArticles> mediaArticles) {
        String url = uploadNews + AccessTokenApi.getAccessTokenStr();
        HashMap<String, List<MediaArticles>> dataMap = new HashMap<String, List<MediaArticles>>();
        dataMap.put("articles", mediaArticles);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(dataMap));
        return new ApiResult(jsonResult);
    }

    public static MediaFile getMedia(String media_id) {
        String url = get_url + AccessTokenApi.getAccessTokenStr() + "&media_id=" + media_id;
        return HttpUtils.download(url);
    }

    public static MediaFile getJssdkMedia(String media_id) {
        String url = get_jssdk_media + AccessTokenApi.getAccessTokenStr() + "&media_id=" + media_id;
        return HttpUtils.download(url);
    }

    public static ApiResult addNews(List<MediaArticles> mediaArticles) {
        String url = add_news_url + AccessTokenApi.getAccessTokenStr();
        HashMap<String, List<MediaArticles>> dataMap = new HashMap<String, List<MediaArticles>>();
        dataMap.put("articles", mediaArticles);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(dataMap));
        return new ApiResult(jsonResult);
    }

    public static ApiResult uploadImg(File imgFile) {
        String url = uploadImgUrl + AccessTokenApi.getAccessTokenStr();
        String jsonResult = HttpUtils.upload(url, imgFile, null);
        return new ApiResult(jsonResult);
    }

    public static ApiResult addMaterial(File file, MediaType mediaType) {
        String url = addMaterialUrl + AccessTokenApi.getAccessTokenStr() + "&type=" + mediaType.get();
        String jsonResult = HttpUtils.upload(url, file, null);
        return new ApiResult(jsonResult);
    }

    public static ApiResult addMaterial(File file, String title, String introduction) {
        String url = addMaterialUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("title", title);
        dataMap.put("introduction", introduction);
        String jsonResult = HttpUtils.upload(url, file, JSON.toJSONString(dataMap));
        return new ApiResult(jsonResult);
    }

    public static InputStream getMaterial(String media_id) {
        String url = get_material_url + AccessTokenApi.getAccessTokenStr();
        HashMap<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("media_id", media_id);
        return HttpUtils.download(url, JSON.toJSONString(dataMap));
    }

    public static ApiResult delMaterial(String media_id) {
        String url = del_material_url + AccessTokenApi.getAccessTokenStr();
        HashMap<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("media_id", media_id);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(dataMap));
        return new ApiResult(jsonResult);
    }

    public static ApiResult updateNews(String media_id, int index, MediaArticles mediaArticles) {
        String url = update_news_url + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("media_id", media_id);
        dataMap.put("index", index);
        dataMap.put("articles", mediaArticles);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(dataMap));
        return new ApiResult(jsonResult);
    }

    public static ApiResult getMaterialCount() {
        String url = get_materialcount_url + AccessTokenApi.getAccessTokenStr();
        String jsonResult = HttpUtils.get(url);
        return new ApiResult(jsonResult);
    }

    public static ApiResult batchGetMaterial(MediaType mediaType, int offset, int count) {
        return MediaApi.batchGetMaterial(mediaType.get(), offset, count);
    }

    public static ApiResult batchGetMaterialNews(int offset, int count) {
        return MediaApi.batchGetMaterial("news", offset, count);
    }

    public static ApiResult batchGetMaterial(String mediaType, int offset, int count) {
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
        dataMap.put("type", mediaType);
        dataMap.put("offset", offset);
        dataMap.put("count", count);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(dataMap));
        return new ApiResult(jsonResult);
    }

    public static enum MediaType {
        IMAGE,
        VOICE,
        VIDEO,
        THUMB;


        public String get() {
            return this.name().toLowerCase();
        }
    }
}
