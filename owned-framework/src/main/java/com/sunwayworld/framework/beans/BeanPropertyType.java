package com.sunwayworld.framework.beans;

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
    private static final Map<Class<?>, BeanPropertyType> CACHE = new HashMap<Class<?>, BeanPropertyType>();

    BeanPropertyType(Class<?>... classes) {
        this.classes = classes;
    }

    public static BeanPropertyType of(Class<?> type) {
        if (!CACHE.containsKey(type)) {
            synchronized (type) {
                if (!CACHE.containsKey(type)) {
                    BeanPropertyType found = Arrays.stream(values())
                            .filter(candidate -> Arrays.asList(candidate.classes).contains(type))
                            .findAny().orElse(null);
                    CACHE.put(type, Optional.ofNullable(found).orElse(UNKNOWN));
                }
            }
        }
        return CACHE.get(type);
    }
}
