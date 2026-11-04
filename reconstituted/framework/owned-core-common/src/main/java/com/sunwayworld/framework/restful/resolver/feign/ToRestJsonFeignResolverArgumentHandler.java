/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.restful.resolver.feign;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.beans.BeanPropertyHelper;
import com.sunwayworld.framework.exception.ResolverArgumentException;
import com.sunwayworld.framework.restful.resolver.AbstractJsonResolveArgumentHandler;
import com.sunwayworld.framework.restful.resolver.feign.annotation.FeignRequestParam;
import com.sunwayworld.framework.support.domain.BaseData;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.JsonUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

public class ToRestJsonFeignResolverArgumentHandler
extends AbstractJsonResolveArgumentHandler<Object> {
    private static final String FEIGN_PARAM_ATTRIBUTE = "SUNWAY_FEIGN_PARAM";

    public ToRestJsonFeignResolverArgumentHandler(MethodParameter parameter, NativeWebRequest webRequest) {
        super(parameter, webRequest);
    }

    @Override
    public boolean supports() {
        HttpServletRequest request = this.getWebRequest().getNativeRequest(HttpServletRequest.class);
        return !"GET".equals(request.getMethod()) && StringUtils.contains(request.getContentType(), "application/json");
    }

    @Override
    public Object resolveArgument() {
        Object value;
        String requestJson = this.getRequestBody();
        if (StringUtils.isEmpty(requestJson)) {
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(requestJson);
        MethodParameter parameter = this.getParameter();
        FeignRequestParam parameterAnnotation = parameter.getParameterAnnotation(FeignRequestParam.class);
        String key = parameterAnnotation.value();
        if (!StringUtils.isEmpty(key)) {
            value = jsonObject.get(key);
            if (value == null && parameterAnnotation.required()) {
                throw new ResolverArgumentException(String.format("required param %s is not present", key));
            }
        } else {
            key = parameter.getParameterName();
            value = jsonObject.get(key);
        }
        Class<?> parameterType = parameter.getParameterType();
        if (value != null) {
            if (ClassUtils.isPrimitiveType(parameterType)) {
                throw new ResolverArgumentException(String.format("param %s can not be primitiveType", key));
            }
            if (ClassUtils.isWrapperType(parameterType)) {
                return ConvertUtils.convert(value, parameterType);
            }
            if (String.class.equals(parameterType)) {
                return value.toString();
            }
            return this.parseObject(value, parameterType);
        }
        if (ClassUtils.isWrapperType(parameterType)) {
            if (parameterAnnotation.required()) {
                throw new ResolverArgumentException(String.format("required param %s is not present", key));
            }
            return null;
        }
        if (!parameterAnnotation.parseAllFields()) {
            if (parameterAnnotation.required()) {
                throw new ResolverArgumentException(String.format("required param %s is not present", key));
            }
            return null;
        }
        Object result = this.parseObject(jsonObject, parameterType);
        if (!parameterAnnotation.required()) {
            return result;
        }
        boolean haveValue = false;
        List<BeanPropertyDescriptor> bpdList = BeanPropertyHelper.getBeanPropertyDescriptorList(parameterType);
        for (BeanPropertyDescriptor bpd : bpdList) {
            if (bpd.isExt$BeanProperty() || bpd.getPropertyValue(result) == null) continue;
            haveValue = true;
        }
        if (!haveValue) {
            throw new ResolverArgumentException(String.format("required param %s is not present", key));
        }
        return result;
    }

    private String getRequestBody() {
        NativeWebRequest webRequest = this.getWebRequest();
        String jsonBody = (String)webRequest.getAttribute(FEIGN_PARAM_ATTRIBUTE, 0);
        if (jsonBody == null) {
            jsonBody = this.getRequestJson();
            webRequest.setAttribute(FEIGN_PARAM_ATTRIBUTE, jsonBody, 0);
        }
        return jsonBody;
    }

    private Object parseObject(Object value, Class<?> parameterType) {
        String json = value.toString();
        if (List.class.isAssignableFrom(parameterType)) {
            Class actualTypeArgument = (Class)((ParameterizedType)this.getParameter().getGenericParameterType()).getActualTypeArguments()[0];
            if (BaseData.class.isAssignableFrom(actualTypeArgument)) {
                return JsonUtils.parseList(json, actualTypeArgument);
            }
            return JSON.parseArray(json, actualTypeArgument);
        }
        if (BaseData.class.isAssignableFrom(parameterType)) {
            return JsonUtils.parse(json, parameterType);
        }
        return JSON.parseObject(json, parameterType);
    }
}
