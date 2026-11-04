/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.io.excel.support;

import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.io.excel.support.PropertyContext;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SheetContext {
    private String topic;
    private String sheetName;
    private List<PropertyContext> propertyList;
    private List<Pair<String, Function<Double, String>>> sumPropertyList = new ArrayList<Pair<String, Function<Double, String>>>();
    private Map<String, Number> totalMap;
    private List<Pair<String, Pair<Integer, Integer>>> subTopicList;
    private List<Pair<String, List<String>>> mergePropertyPairList = new ArrayList<Pair<String, List<String>>>();

    public SheetContext(String topic, String sheetName, List<PropertyContext> propertyList, List<Pair<String, Pair<Integer, Integer>>> subTopicList) {
        this.topic = topic;
        this.sheetName = sheetName;
        this.propertyList = propertyList;
        this.subTopicList = subTopicList;
    }

    public SheetContext(String topic, String sheetName, List<PropertyContext> propertyList) {
        this.topic = topic;
        this.sheetName = sheetName;
        this.propertyList = propertyList;
        this.subTopicList = CollectionUtils.emptyList();
    }

    public String getTopic() {
        return this.topic;
    }

    public String getSheetName() {
        return this.sheetName;
    }

    public List<PropertyContext> getPropertyList() {
        return this.propertyList;
    }

    public List<Pair<String, Function<Double, String>>> getSumPropertyList() {
        return this.sumPropertyList;
    }

    public List<Pair<String, Pair<Integer, Integer>>> getSubTopicList() {
        return this.subTopicList;
    }

    public void setSumProperty(String name) {
        this.sumPropertyList.add(Pair.of(name, v -> NumberUtils.formatNumber(v, 2, false)));
    }

    public void setSumProperty(String name, Function<Double, String> function) {
        this.sumPropertyList.add(Pair.of(name, function));
    }

    public List<Pair<String, List<String>>> getMergePropertyPairList() {
        return this.mergePropertyPairList;
    }

    public void setMergeProperty(String mergePropertyName, String ... comparePropertyNames) {
        this.mergePropertyPairList.add(Pair.of(mergePropertyName, Arrays.asList(comparePropertyNames)));
    }

    public Map<String, Number> getTotalMap() {
        return this.totalMap;
    }

    public void setTotalMap(Map<String, Number> totalMap) {
        this.totalMap = totalMap;
    }
}
