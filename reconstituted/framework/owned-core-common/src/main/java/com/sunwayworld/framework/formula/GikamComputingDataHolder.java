package com.sunwayworld.framework.formula;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections4.map.CaseInsensitiveMap;

import com.sunwayworld.framework.utils.CollectionUtils;

/**
 * 计算公式的引擎所需的数据持有类，用本地线程
 *
 * @author zhangjr@sunwayworld.com 2019-11-13
 */
public class GikamComputingDataHolder {
    public static final String DEPENDENCY_CHECK_KEY = "DEPENDENCY_CHECK";
    public static final String DEPENDENCY_CHECK_PREFIX = "TESTABLE_";

    private static final ThreadLocal<Map<String, Object>> CONTAINER = new ThreadLocal<>();

    public static <V> void put(String key, V value) {
        Map<String, Object> map = CONTAINER.get();
        if (map == null) {
            map = new CaseInsensitiveMap<>();
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

    @SuppressWarnings("unchecked")
    public static <V> V get(String key) {
        Map<String, Object> map = CONTAINER.get();

        if (map == null) {
            return null;
        }

        // 对于检查依赖的key有前缀
        Boolean dependencyCheck = (Boolean) map.get(DEPENDENCY_CHECK_KEY);
        if (dependencyCheck != null && dependencyCheck) {
            Object value = map.get(DEPENDENCY_CHECK_PREFIX + key);

            if (value != null) {
                return (V) value;
            }
        }

        return (V) map.get(key);
    }

    @SuppressWarnings("unchecked")
    public static <V> V getRaw(String key) {
        Map<String, Object> map = CONTAINER.get();

        if (map == null) {
            return null;
        }

        return (V) map.get(key);
    }

    public static <V> void setCurrentData(V item) {
        put("CURRENT_DATA", item);
    }

    public static <V> V getCurrentData() {
        return get("CURRENT_DATA");
    }

    public static void removeCurrentData() {
        remove("CURRENT_DATA");
    }

    public static <ID> void setRelevantData(ID id, List<ID> relevantIdList) {
        if (relevantIdList.isEmpty()) {
            return;
        }

        Map<ID, Set<ID>> map = get("RELEVANT_DATA");
        if (map == null) {
            map = new HashMap<>();
            map.put(id, new HashSet<>(relevantIdList));
            put("RELEVANT_DATA", map);
        }

        Set<ID> relevantIdSet = Optional.ofNullable(map.get(id)).orElseGet(HashSet::new);
        relevantIdSet.addAll(relevantIdList);

        map.put(id, relevantIdSet);
    }

    public static <ID> Map<ID, Set<ID>> getRelevantData() {
        Map<ID, Set<ID>> map = get("RELEVANT_DATA");

        if (map == null) {
            return CollectionUtils.emptyMap();
        }

        return map;
    }

    public static void clean() {
        CONTAINER.remove();
    }
}
