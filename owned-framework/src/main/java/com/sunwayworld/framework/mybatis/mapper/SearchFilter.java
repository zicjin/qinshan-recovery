package com.sunwayworld.framework.mybatis.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/** Fluent standard/special filter state used by the recovered mapper layer. */
public class SearchFilter {
    public Map<String, Object> filterMap = new HashMap<String, Object>();
    public List<FilterParamPattern> stdFilterList = new ArrayList<FilterParamPattern>();
    private SearchFilter() { }
    public static <V> SearchFilter instance() { return new SearchFilter(); }
    public <V> Matcher<V> match(String key, V value) { return new Matcher<V>(this, key, value); }
    public <V> Matcher<V> match(String[] keys, V value) { return new Matcher<V>(this, keys, value); }
    public Map<String,Object> getFilterMap() { return Collections.unmodifiableMap(filterMap); }
    public List<FilterParamPattern> getStdFilterList() { return Collections.unmodifiableList(stdFilterList); }
    public boolean isEmpty() { return filterMap.isEmpty() && stdFilterList.isEmpty(); }

    public static class Matcher<V> {
        private SearchFilter filter; private String[] keys; private V value;
        public Matcher(SearchFilter filter, String key, V value) { this.filter=filter; this.value=value; this.keys=new String[]{key}; }
        public Matcher(SearchFilter filter, String[] keys, V value) { this.filter=filter; this.value=value; this.keys=keys; }
        public SearchFilter filter(MatchPattern pattern) {
            Map<String,Object> values = new IdentityHashMap<String,Object>();
            for (String key : keys) {
                if (value != null && List.class.isAssignableFrom(value.getClass())) {
                    for (Object item : (List<?>) value) values.put(new String(key), item);
                } else values.put(key, value);
            }
            filter.stdFilterList.add(new FilterParamPattern(pattern.name(), values)); return filter;
        }
        public SearchFilter special() { for (String key : keys) filter.filterMap.put(key, value); return filter; }
    }
}
