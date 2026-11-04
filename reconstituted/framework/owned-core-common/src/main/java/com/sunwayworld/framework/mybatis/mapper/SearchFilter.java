/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mybatis.mapper;

import com.sunwayworld.framework.mybatis.mapper.FilterParamPattern;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.utils.ObjectUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class SearchFilter {
    public Map<String, Object> filterMap = new HashMap<String, Object>();
    public List<FilterParamPattern> stdFilterList = new ArrayList<FilterParamPattern>();

    private SearchFilter() {
    }

    public static <V> SearchFilter instance() {
        return new SearchFilter();
    }

    public <V> Matcher<V> match(String key, V value) {
        return new Matcher<V>(this, key, value);
    }

    public <V> Matcher<V> match(String[] keys, V value) {
        return new Matcher<V>(this, keys, value);
    }

    public Map<String, Object> getFilterMap() {
        return Collections.unmodifiableMap(this.filterMap);
    }

    public List<FilterParamPattern> getStdFilterList() {
        return Collections.unmodifiableList(this.stdFilterList);
    }

    public boolean isEmpty() {
        return this.filterMap.isEmpty() && this.stdFilterList.isEmpty();
    }

    public static class Matcher<V> {
        private SearchFilter filter;
        private String[] keys;
        private V value;

        public Matcher(SearchFilter filter, String key, V value) {
            this.filter = filter;
            this.value = value;
            this.keys = new String[]{key};
        }

        public Matcher(SearchFilter filter, String[] keys, V value) {
            this.filter = filter;
            this.value = value;
            this.keys = keys;
        }

        public SearchFilter filter(MatchPattern match) {
            IdentityHashMap<String, Object> map = new IdentityHashMap<String, Object>();
            for (String key : this.keys) {
                if (this.value != null && List.class.isAssignableFrom(this.value.getClass())) {
                    ((List)this.value).forEach(v -> map.put(ObjectUtils.clone(key), v));
                    continue;
                }
                map.put(key, this.value);
            }
            this.filter.stdFilterList.add(new FilterParamPattern(match.name(), map));
            return this.filter;
        }

        public SearchFilter special() {
            for (String key : this.keys) {
                this.filter.filterMap.put(key, this.value);
            }
            return this.filter;
        }
    }
}
