/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.restful.resolver.wrapper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestFieldValueTextContainer;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.resolver.AbstractJsonResolveArgumentHandler;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

public class ToRestJsonWrapperResolveArgumentHandler
extends AbstractJsonResolveArgumentHandler<RestJsonWrapperBean> {
    public ToRestJsonWrapperResolveArgumentHandler(MethodParameter parameter, NativeWebRequest webRequest) {
        super(parameter, webRequest);
    }

    @Override
    public boolean supports() {
        HttpServletRequest request = this.getWebRequest().getNativeRequest(HttpServletRequest.class);
        return !"GET".equals(request.getMethod()) && StringUtils.contains(request.getContentType(), "application/json");
    }

    @Override
    public RestJsonWrapperBean resolveArgument() {
        JSONArray array;
        String requestJson = this.getRequestJson();
        if (StringUtils.isEmpty(requestJson)) {
            return new RestJsonWrapperBean();
        }
        JSONObject jo = this.convertRequestJson(requestJson);
        String converterJson = jo.toJSONString();
        HashMap<String, String> paramMap = new HashMap<String, String>();
        String paramStr = jo.getString("p");
        if (!StringUtils.isEmpty(paramStr)) {
            JSONObject paramObject = JSONObject.parseObject(paramStr);
            paramObject.entrySet().stream().forEach(e -> paramMap.put((String)e.getKey(), ConvertUtils.convert(e.getValue(), String.class)));
            String t = (String)paramMap.get("t");
            if (!StringUtils.isEmpty(t)) {
                this.setValueTextContainer(t);
            }
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

    private void setValueTextContainer(String tJson) {
        JSONObject tjo = JSONObject.parseObject(tJson);
        RestFieldValueTextContainer valueTextContainer = RestFieldValueTextContainer.getInstance();
        tjo.forEach((k, v) -> {
            JSONObject vjo = (JSONObject)v;
            String type = vjo.getString("type");
            String items = vjo.getString("items");
            if (items == null) {
                valueTextContainer.addFieldData((String)k, type);
            } else {
                HashMap<String, String> valueTextMap = new HashMap<String, String>();
                JSONArray ija = JSONObject.parseArray(items);
                int j = ija.size();
                for (int i = 0; i < j; ++i) {
                    JSONObject ijo = (JSONObject)ija.get(i);
                    valueTextMap.put(ijo.getString("value"), ijo.getString("text"));
                }
                valueTextContainer.addFieldData((String)k, type, (Map<String, String>)valueTextMap);
            }
        });
        HttpServletRequest request = this.getWebRequest().getNativeRequest(HttpServletRequest.class);
        request.setAttribute("GIKAM_RFVTC", valueTextContainer);
    }

    private JSONObject convertRequestJson(String requestJson) {
        JSONObject jo = JSONObject.parseObject(requestJson);
        String json = jo.getString("requestJson");
        if (!StringUtils.isBlank(json)) {
            return JSONObject.parseObject(json);
        }
        return jo;
    }
}
