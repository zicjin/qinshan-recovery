/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.helper;

import java.util.Map;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

public class LimsTemplateDataCacheHolder {
    private static final ThreadLocal<Map<String, Object>> CONTAINER = new ThreadLocal();

    public static void set(Map<String, Object> data) {
        CONTAINER.set(data);
    }

    public static <V> void put(String key, V value) {
        Map<String, Object> map = CONTAINER.get();
        if (map == null) {
            map = new CaseInsensitiveMap<String, Object>();
            CONTAINER.set(map);
        }
        map.put(key, value);
    }

    public static void remove(String key) {
        Map<String, Object> map = CONTAINER.get();
        if (map != null) {
            map.remove(key);
        }
    }

    public static <V> V get(String key) {
        Map<String, Object> map = CONTAINER.get();
        if (map == null) {
            return null;
        }
        return (V)map.get(key);
    }

    public static void clean() {
        CONTAINER.remove();
    }

    public static Map<String, Object> getData() {
        return CONTAINER.get();
    }
}
