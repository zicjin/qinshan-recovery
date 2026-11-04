/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.formula;

import com.sunwayworld.framework.utils.CollectionUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

public class GikamComputingDataHolder {
    public static final String DEPENDENCY_CHECK_KEY = "DEPENDENCY_CHECK";
    public static final String DEPENDENCY_CHECK_PREFIX = "TESTABLE_";
    private static final ThreadLocal<Map<String, Object>> CONTAINER = new ThreadLocal();

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
        Object value;
        Map<String, Object> map = CONTAINER.get();
        if (map == null) {
            return null;
        }
        Boolean dependencyCheck = (Boolean)map.get(DEPENDENCY_CHECK_KEY);
        if (dependencyCheck != null && dependencyCheck.booleanValue() && (value = map.get(DEPENDENCY_CHECK_PREFIX + key)) != null) {
            return (V)value;
        }
        return (V)map.get(key);
    }

    public static <V> V getRaw(String key) {
        Map<String, Object> map = CONTAINER.get();
        if (map == null) {
            return null;
        }
        return (V)map.get(key);
    }

    public static <V> void setCurrentData(V item) {
        GikamComputingDataHolder.put("CURRENT_DATA", item);
    }

    public static <V> V getCurrentData() {
        return GikamComputingDataHolder.get("CURRENT_DATA");
    }

    public static void removeCurrentData() {
        GikamComputingDataHolder.remove("CURRENT_DATA");
    }

    public static <ID> void setRelevantData(ID id, List<ID> relevantIdList) {
        if (relevantIdList.isEmpty()) {
            return;
        }
        HashMap<ID, HashSet<ID>> map = (HashMap<ID, HashSet<ID>>)GikamComputingDataHolder.get("RELEVANT_DATA");
        if (map == null) {
            map = new HashMap<ID, HashSet<ID>>();
            map.put(id, new HashSet<ID>(relevantIdList));
            GikamComputingDataHolder.put("RELEVANT_DATA", map);
        }
        map.put(id, new HashSet<ID>(relevantIdList));
    }

    public static <ID> Map<ID, Set<ID>> getRelevantData() {
        Map map = (Map)GikamComputingDataHolder.get("RELEVANT_DATA");
        if (map == null) {
            return CollectionUtils.emptyMap();
        }
        return map;
    }

    public static void clean() {
        CONTAINER.remove();
    }
}
