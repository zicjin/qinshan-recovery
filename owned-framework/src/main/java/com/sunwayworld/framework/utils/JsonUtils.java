package com.sunwayworld.framework.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONValidator;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.beans.BeanPropertyHelper;
import com.sunwayworld.framework.support.domain.BaseData;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** JSON conversion facade used by recovered business code. */
public class JsonUtils {
    public JsonUtils() { }

    public static final <T> String toJsonString(T value) { return JSONObject.toJSONString(value); }

    public static final <T> T parse(String value, Class<T> type) {
        if (ClassUtils.isSimpleType(type)) return ConvertUtils.convert(value, type);
        T result = ClassUtils.newInstance(type);
        parse(value, result);
        return result;
    }

    public static final <T> T parse(JSONObject value, Class<T> type) {
        if (ClassUtils.isSimpleType(type)) return value.toJavaObject(type);
        T result = ClassUtils.newInstance(type);
        parse(value, result);
        return result;
    }

    public static final <T> void parse(JSONObject value, T target) {
        if (value == null || target == null) return;
        Class<?> rawType = ClassUtils.getRawType(target.getClass());
        List<BeanPropertyDescriptor> descriptors = BeanPropertyHelper.getBeanPropertyDescriptorList(rawType);
        for (Map.Entry<String, Object> entry : value.entrySet()) {
            Object fieldValue = entry.getValue();
            if (fieldValue == null) continue;
            String name = StringUtils.removeStart(entry.getKey(), "ext$.");
            BeanPropertyDescriptor descriptor = null;
            for (BeanPropertyDescriptor candidate : descriptors) {
                if (candidate.getName().equalsIgnoreCase(name)) { descriptor = candidate; break; }
            }
            if (descriptor == null) {
                if (target instanceof BaseData) ((BaseData) target).setExt$Item(name, ConvertUtils.convert(fieldValue, String.class));
                continue;
            }
            if (descriptor.isExt$BeanProperty()) {
                if (target instanceof BaseData && fieldValue instanceof Map<?, ?>) {
                    for (Map.Entry<?, ?> ext : ((Map<?, ?>) fieldValue).entrySet())
                        ((BaseData) target).setExt$Item(ConvertUtils.convert(ext.getKey(), String.class), ConvertUtils.convert(ext.getValue(), String.class));
                }
                continue;
            }
            Class<?> propertyType = descriptor.getPropertyType();
            if (fieldValue instanceof JSONArray && (List.class.isAssignableFrom(propertyType) || propertyType.isArray())) {
                Class<?> elementType = elementType(descriptor, propertyType);
                if (elementType != null) {
                    List<Object> converted = new ArrayList<Object>();
                    for (Object item : (JSONArray) fieldValue) converted.add(parse(JSON.toJSONString(item), elementType));
                    if (List.class.isAssignableFrom(propertyType)) descriptor.setPropertyValue(target, converted);
                    else {
                        Object array = Array.newInstance(elementType, converted.size());
                        for (int i = 0; i < converted.size(); i++) Array.set(array, i, converted.get(i));
                        descriptor.setPropertyValue(target, array);
                    }
                    continue;
                }
            }
            if (fieldValue instanceof JSONObject) {
                descriptor.setPropertyValue(target, parse((JSONObject) fieldValue, propertyType));
            } else if (fieldValue instanceof String) {
                descriptor.setPropertyValue(target, parse((String) fieldValue, propertyType));
            } else {
                descriptor.setPropertyValue(target, fieldValue);
            }
        }
    }

    public static final <T> void parse(String value, T target) { parse(JSON.parseObject(value), target); }

    public static final <T> T parse(String value, String key, Class<T> type) {
        JSONObject object = JSONObject.parseObject(value);
        Object nested = object.get(key);
        return nested == null ? null : parse(JSONObject.toJSONString(nested), type);
    }

    public static final <T> List<T> parseList(String value, String key, Class<T> type) {
        JSONObject object = JSONObject.parseObject(value);
        Object nested = object.get(key);
        return nested == null ? CollectionUtils.emptyList() : parseList(JSON.toJSONString(nested), type);
    }

    public static final <T> List<T> parseList(String value, Class<T> type) {
        if (StringUtils.isEmpty(value)) return CollectionUtils.emptyList();
        List<String> values = JSONObject.parseArray(value, String.class);
        List<T> result = new ArrayList<T>();
        for (String item : values) result.add(parse(item, type));
        return result;
    }

    public static boolean isArray(String value) {
        return isJsonType(value, JSONArray.class);
    }

    public static boolean isObject(String value) {
        return isJsonType(value, JSONObject.class);
    }

    private static boolean isJsonType(String value, Class<?> type) {
        if (value == null) return false;
        JSONValidator validator = null;
        try {
            validator = JSONValidator.from(value);
            if (!validator.validate()) return false;
            return type.isInstance(JSON.parse(value));
        } catch (Throwable ignored) {
            return false;
        } finally {
            if (validator != null) try { validator.close(); } catch (Throwable ignored) { }
        }
    }

    private static Class<?> elementType(BeanPropertyDescriptor descriptor, Class<?> propertyType) {
        Field field = descriptor.getProperty();
        Type generic = field.getGenericType();
        if (generic instanceof ParameterizedType) {
            Type type = ((ParameterizedType) generic).getActualTypeArguments()[0];
            if (type instanceof Class<?>) return (Class<?>) type;
        }
        return propertyType.isArray() ? propertyType.getComponentType() : null;
    }
}
