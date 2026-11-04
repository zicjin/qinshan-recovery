/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.beans;

import com.sunwayworld.framework.utils.ArrayUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum BeanPropertyType {
    STRING(String.class),
    DOUBLE(Double.class, Double.TYPE),
    INTEGER(Long.class, Long.TYPE, Integer.class, Integer.TYPE),
    DATE(LocalDate.class),
    DATETIME(LocalDateTime.class),
    UNKNOWN(Object.class);

    private final Class<?>[] classes;
    private static final Map<Class<?>, BeanPropertyType> CACHE;

    private BeanPropertyType(Class<?> ... classes) {
        this.classes = classes;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static BeanPropertyType of(Class<?> clazz) {
        if (!CACHE.containsKey(clazz)) {
            Class<?> clazz2 = clazz;
            synchronized (clazz2) {
                if (!CACHE.containsKey(clazz)) {
                    BeanPropertyType type = Arrays.stream(BeanPropertyType.values()).filter(t -> ArrayUtils.contains(t.classes, clazz)).findAny().orElse(null);
                    CACHE.put(clazz, Optional.ofNullable(type).orElse(UNKNOWN));
                }
            }
        }
        return CACHE.get(clazz);
    }

    static {
        CACHE = new HashMap();
    }
}
