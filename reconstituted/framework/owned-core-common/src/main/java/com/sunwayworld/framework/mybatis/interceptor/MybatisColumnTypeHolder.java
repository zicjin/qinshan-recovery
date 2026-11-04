/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mybatis.interceptor;

import com.sunwayworld.framework.data.CaseInsensitiveLinkedMap;
import com.sunwayworld.framework.utils.CollectionUtils;
import java.util.Map;

public class MybatisColumnTypeHolder {
    private static final ThreadLocal<Map<String, Class<?>>> CONTAINER = new ThreadLocal();

    public static void setColumnTypeMap(Map<String, Class<?>> columnTypeMap) {
        if (CollectionUtils.isEmpty(columnTypeMap)) {
            MybatisColumnTypeHolder.remove();
        } else {
            CONTAINER.set(new CaseInsensitiveLinkedMap(columnTypeMap));
        }
    }

    public static Map<String, Class<?>> popColumnTypeMap() {
        Map<String, Class<?>> map = CONTAINER.get();
        if (map == null) {
            return CollectionUtils.emptyMap();
        }
        MybatisColumnTypeHolder.remove();
        return map;
    }

    public static void remove() {
        CONTAINER.remove();
    }
}
