/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.support;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.JsonUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LcdpWrapperParseUtils {
    public static Map<String, Object> parseMap(RestJsonWrapperBean wrapper) {
        List<List<String>> bodyList = wrapper.getBodyList();
        if (bodyList.isEmpty()) {
            throw new CheckedException("\u524d\u7aef\u53c2\u6570\u89e3\u6790\u5f02\u5e38!");
        }
        List<String> list = bodyList.get(0);
        String objString = list.get(0);
        return JsonUtils.parse(JSONObject.parseObject(objString), Map.class);
    }

    public static Map<String, Object> parseMap(RestJsonWrapperBean wrapper, int index) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String, Object>> list = LcdpWrapperParseUtils.parseList(wrapper, index);
        if (!list.isEmpty()) {
            map = list.get(0);
        }
        return map;
    }

    public static List<Map<String, Object>> parseList(RestJsonWrapperBean wrapper) {
        ArrayList<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        List<List<String>> bodyList = wrapper.getBodyList();
        if (bodyList.isEmpty()) {
            throw new CheckedException("\u524d\u7aef\u53c2\u6570\u89e3\u6790\u5f02\u5e38!");
        }
        List<String> list = bodyList.get(0);
        for (String objString : list) {
            mapList.add(JsonUtils.parse(JSONObject.parseObject(objString), Map.class));
        }
        return mapList;
    }

    public static List<Map<String, Object>> parseList(RestJsonWrapperBean wrapper, int index) {
        ArrayList<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        List<List<String>> bodyList = wrapper.getBodyList();
        if (bodyList.isEmpty() || bodyList.size() <= index) {
            return mapList;
        }
        List<String> list = bodyList.get(index);
        for (String objString : list) {
            mapList.add(JsonUtils.parse(JSONObject.parseObject(objString), Map.class));
        }
        return mapList;
    }

    public static <ID> List<ID> parseIdList(RestJsonWrapperBean wrapper, Class<ID> idType) {
        List<Map<String, Object>> list = LcdpWrapperParseUtils.parseList(wrapper);
        return list.stream().map(e -> ConvertUtils.convert(e.get("id"), idType)).collect(Collectors.toList());
    }

    public static Map<String, Object> lowerCaseKey(Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        HashMap<String, Object> returnMap = new HashMap<String, Object>();
        map.forEach((key, value) -> {
            if (Timestamp.class.isInstance(value)) {
                returnMap.put(key.toLowerCase(), value.toString());
            } else {
                returnMap.put(key.toLowerCase(), value);
            }
        });
        return returnMap;
    }

    public static List<Map<String, Object>> lowerCaseKey(List<Map<String, Object>> list) {
        if (list == null) {
            return null;
        }
        ArrayList<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> map : list) {
            Map<String, Object> returnMap = LcdpWrapperParseUtils.lowerCaseKey(map);
            returnList.add(returnMap);
        }
        return returnList;
    }

    public static RestJsonWrapperBean toRestJsonWrapper(List<Map<String, Object>> list) {
        ArrayList<List<String>> bodyList = new ArrayList<List<String>>();
        ArrayList<String> jsonList = new ArrayList<String>();
        for (Map<String, Object> map : list) {
            String json = JSONObject.toJSONString(map);
            jsonList.add(json);
        }
        bodyList.add(jsonList);
        RestJsonWrapperBean jsonWrapper = new RestJsonWrapperBean(null, bodyList, null);
        return jsonWrapper;
    }

    public static RestJsonWrapperBean toRestJsonWrapper(String requestJson) {
        JSONArray array;
        if (StringUtils.isEmpty(requestJson)) {
            return new RestJsonWrapperBean();
        }
        JSONObject jo = JSONObject.parseObject(requestJson);
        String converterJson = jo.toJSONString();
        HashMap<String, String> paramMap = new HashMap<String, String>();
        String paramStr = jo.getString("p");
        if (!StringUtils.isEmpty(paramStr)) {
            JSONObject paramObject = JSONObject.parseObject(paramStr);
            paramObject.entrySet().stream().forEach(e -> paramMap.put((String)e.getKey(), ConvertUtils.convert(e.getValue(), String.class)));
        }
        ArrayList<List<String>> bodyList = new ArrayList<List<String>>();
        ArrayList bodyServiceList = new ArrayList();
        int index = 0;
        while ((array = jo.getJSONArray("b" + (index > 0 ? Integer.valueOf(index) : ""))) != null && !array.isEmpty()) {
            String serviceName = array.getString(0);
            JSONArray contentArray = array.getJSONArray(1);
            if (contentArray != null && !contentArray.isEmpty()) {
                List contentList = contentArray.stream().map(a -> ((JSONObject)a).toJSONString()).collect(Collectors.toList());
                bodyList.add(contentList);
                if (StringUtils.isEmpty(serviceName)) {
                    bodyServiceList.add(null);
                } else if (StringUtils.contains(serviceName, "_")) {
                    bodyServiceList.add((GenericService<?, ?>)ApplicationContextHelper.getServiceByTable(serviceName));
                } else {
                    bodyServiceList.add((GenericService<?, ?>)ApplicationContextHelper.getBean(serviceName));
                }
            }
            ++index;
        }
        return new RestJsonWrapperBean(converterJson, paramMap, bodyList, bodyServiceList);
    }

    public static RestJsonWrapperBean toRestJsonWrapper(Map<String, String> paramMap, List<Map<String, Object>> list, List<GenericService<?, ?>> bodyServiceList) {
        ArrayList<List<String>> bodyList = new ArrayList<List<String>>();
        ArrayList<String> jsonList = new ArrayList<String>();
        for (Map<String, Object> map : list) {
            String json = JSONObject.toJSONString(map);
            jsonList.add(json);
        }
        bodyList.add(jsonList);
        RestJsonWrapperBean jsonWrapper = new RestJsonWrapperBean(paramMap, bodyList, bodyServiceList);
        return jsonWrapper;
    }

    public static RestJsonWrapperBean toRestJsonWrapper(String requestjson, Map<String, String> paramMap, List<Map<String, Object>> list, List<GenericService<?, ?>> bodyServiceList) {
        ArrayList<List<String>> bodyList = new ArrayList<List<String>>();
        ArrayList<String> jsonList = new ArrayList<String>();
        for (Map<String, Object> map : list) {
            String json = JSONObject.toJSONString(map);
            jsonList.add(json);
        }
        bodyList.add(jsonList);
        RestJsonWrapperBean jsonWrapper = new RestJsonWrapperBean(requestjson, paramMap, bodyList, bodyServiceList);
        return jsonWrapper;
    }
}
