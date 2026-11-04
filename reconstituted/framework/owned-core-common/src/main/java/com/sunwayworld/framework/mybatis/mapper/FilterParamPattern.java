/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mybatis.mapper;

import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.IdentityHashMap;
import java.util.Map;

public class FilterParamPattern {
    private Map<String, Object> map = new IdentityHashMap<String, Object>();
    private String matchPattern;
    private String matchField;

    public FilterParamPattern(String matchPattern, String key, Object value) {
        this.matchPattern = matchPattern;
        this.matchField = key;
        this.map.put(key.toUpperCase(), value);
    }

    public FilterParamPattern(String matchPattern, Map<String, Object> map) {
        this.matchPattern = matchPattern;
        this.matchField = map.keySet().stream().findFirst().orElse(null);
        map.forEach((k, v) -> this.map.put(k.toUpperCase(), v));
    }

    public Map<String, Object> getMap() {
        return this.map;
    }

    public String getMatchPattern() {
        return this.matchPattern;
    }

    public void setMatchPattern(String matchPattern) {
        this.matchPattern = matchPattern;
    }

    public String getMatchField() {
        return this.matchField;
    }

    public void setMatchField(String matchField) {
        this.matchField = matchField;
    }

    public boolean match(Map<String, Object> targetMap) {
        if (MatchPattern.SB.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().allMatch(e -> StringUtils.startsWith(ObjectUtils.toString(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey())), (String)e.getValue()));
        }
        if (MatchPattern.SC.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().allMatch(e -> StringUtils.contains(ObjectUtils.toString(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey())), (String)e.getValue()));
        }
        if (MatchPattern.SE.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().allMatch(e -> StringUtils.endsWith(ObjectUtils.toString(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey())), (String)e.getValue()));
        }
        if (MatchPattern.SEQ.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().allMatch(e -> StringUtils.equals(ObjectUtils.toString(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey())), (String)e.getValue()));
        }
        if (MatchPattern.CISB.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().allMatch(e -> StringUtils.startsWithIgnoreCase(ObjectUtils.toString(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey())), (String)e.getValue()));
        }
        if (MatchPattern.CISC.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().allMatch(e -> StringUtils.containsIgnoreCase(ObjectUtils.toString(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey())), (String)e.getValue()));
        }
        if (MatchPattern.CISE.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().allMatch(e -> StringUtils.endsWithIgnoreCase(ObjectUtils.toString(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey())), (String)e.getValue()));
        }
        if (MatchPattern.CISEQ.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().allMatch(e -> StringUtils.equalsIgnoreCase(ObjectUtils.toString(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey())), (String)e.getValue()));
        }
        if (MatchPattern.DG.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().allMatch(e -> DateTimeUtils.isAfter(ConvertUtils.convert(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey()), LocalDate.class), ConvertUtils.convert(e.getValue(), LocalDate.class)));
        }
        if (MatchPattern.DGOE.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().allMatch(e -> !DateTimeUtils.isBefore(ConvertUtils.convert(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey()), LocalDate.class), ConvertUtils.convert(e.getValue(), LocalDate.class)));
        }
        if (MatchPattern.DL.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().allMatch(e -> DateTimeUtils.isBefore(ConvertUtils.convert(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey()), LocalDate.class), ConvertUtils.convert(e.getValue(), LocalDate.class)));
        }
        if (MatchPattern.DLOE.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().allMatch(e -> !DateTimeUtils.isAfter(ConvertUtils.convert(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey()), LocalDate.class), ConvertUtils.convert(e.getValue(), LocalDate.class)));
        }
        if (MatchPattern.DEQ.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().allMatch(e -> !DateTimeUtils.isEqual(ConvertUtils.convert(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey()), LocalDate.class), ConvertUtils.convert(e.getValue(), LocalDate.class)));
        }
        if (MatchPattern.DOR.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().anyMatch(e -> !DateTimeUtils.isEqual(ConvertUtils.convert(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey()), LocalDate.class), ConvertUtils.convert(e.getValue(), LocalDate.class)));
        }
        if (MatchPattern.TG.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().allMatch(e -> DateTimeUtils.isAfter(ConvertUtils.convert(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey()), LocalDateTime.class), ConvertUtils.convert(e.getValue(), LocalDateTime.class)));
        }
        if (MatchPattern.TGOE.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().allMatch(e -> !DateTimeUtils.isBefore(ConvertUtils.convert(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey()), LocalDateTime.class), ConvertUtils.convert(e.getValue(), LocalDateTime.class)));
        }
        if (MatchPattern.TL.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().allMatch(e -> DateTimeUtils.isBefore(ConvertUtils.convert(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey()), LocalDateTime.class), ConvertUtils.convert(e.getValue(), LocalDateTime.class)));
        }
        if (MatchPattern.TLOE.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().allMatch(e -> !DateTimeUtils.isAfter(ConvertUtils.convert(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey()), LocalDateTime.class), ConvertUtils.convert(e.getValue(), LocalDateTime.class)));
        }
        if (MatchPattern.TEQ.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().allMatch(e -> !DateTimeUtils.isEqual(ConvertUtils.convert(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey()), LocalDateTime.class), ConvertUtils.convert(e.getValue(), LocalDateTime.class)));
        }
        if (MatchPattern.TOR.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().anyMatch(e -> !DateTimeUtils.isEqual(ConvertUtils.convert(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey()), LocalDateTime.class), ConvertUtils.convert(e.getValue(), LocalDateTime.class)));
        }
        if (MatchPattern.NG.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().allMatch(e -> NumberUtils.isGreater(ConvertUtils.convert(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey()), Double.class), ConvertUtils.convert(e.getValue(), Double.class)));
        }
        if (MatchPattern.NGOE.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().allMatch(e -> !NumberUtils.isLess(ConvertUtils.convert(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey()), Double.class), ConvertUtils.convert(e.getValue(), Double.class)));
        }
        if (MatchPattern.NL.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().allMatch(e -> NumberUtils.isLess(ConvertUtils.convert(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey()), Double.class), ConvertUtils.convert(e.getValue(), Double.class)));
        }
        if (MatchPattern.NLOE.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().allMatch(e -> !NumberUtils.isGreater(ConvertUtils.convert(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey()), Double.class), ConvertUtils.convert(e.getValue(), Double.class)));
        }
        if (MatchPattern.NEQ.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().allMatch(e -> ObjectUtils.equals(ConvertUtils.convert(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey()), Double.class), ConvertUtils.convert(e.getValue(), Double.class)));
        }
        if (MatchPattern.NOR.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().anyMatch(e -> ObjectUtils.equals(ConvertUtils.convert(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey()), Double.class), ConvertUtils.convert(e.getValue(), Double.class)));
        }
        if (MatchPattern.EQ.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().allMatch(e -> ObjectUtils.equals(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey()), e.getValue()));
        }
        if (MatchPattern.DIFFER.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().allMatch(e -> !ObjectUtils.equals(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey()), e.getValue()));
        }
        if (MatchPattern.OR.name().equals(this.getMatchPattern()) || MatchPattern.IN.name().equals(this.getMatchPattern()) || MatchPattern.OIN.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().anyMatch(e -> ObjectUtils.equals(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey()), e.getValue()));
        }
        if (MatchPattern.CBM.name().equals(this.getMatchPattern())) {
            return this.map.entrySet().stream().allMatch(e -> StringUtils.contains(ObjectUtils.toString(CollectionUtils.getValueIgnorecase(targetMap, (String)e.getKey())), (String)e.getValue()));
        }
        return false;
    }
}
