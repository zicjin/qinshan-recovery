package com.sunwayworld.framework.mybatis.mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

/** One standard filter expression and its case-insensitive row matcher. */
public class FilterParamPattern {
    private Map<String, Object> map = new IdentityHashMap<String, Object>();
    private String matchPattern;
    private String matchField;

    public FilterParamPattern(String matchPattern, String key, Object value) {
        this.matchPattern = matchPattern; this.matchField = key; this.map.put(key.toUpperCase(), value);
    }
    public FilterParamPattern(String matchPattern, Map<String, Object> map) {
        this.matchPattern = matchPattern; this.matchField = map.keySet().stream().findFirst().orElse(null);
        map.forEach((k, v) -> this.map.put(k.toUpperCase(), v));
    }
    public Map<String, Object> getMap() { return map; }
    public String getMatchPattern() { return matchPattern; }
    public void setMatchPattern(String matchPattern) { this.matchPattern = matchPattern; }
    public String getMatchField() { return matchField; }
    public void setMatchField(String matchField) { this.matchField = matchField; }

    public boolean match(Map<String, Object> targetMap) {
        MatchPattern p = MatchPattern.getPatternIfPossible(matchPattern);
        if (p == null) return false;
        String n = p.name();
        if (n.equals("SB")) return allStrings(targetMap, 0);
        if (n.equals("SC") || n.equals("CBM")) return allStrings(targetMap, 1);
        if (n.equals("SE")) return allStrings(targetMap, 2);
        if (n.equals("SEQ")) return allStrings(targetMap, 3);
        if (n.equals("CISB")) return allStrings(targetMap, 4);
        if (n.equals("CISC")) return allStrings(targetMap, 5);
        if (n.equals("CISE")) return allStrings(targetMap, 6);
        if (n.equals("CISEQ")) return allStrings(targetMap, 7);
        if (n.equals("DG")) return allDates(targetMap, 0);
        if (n.equals("DGOE")) return allDates(targetMap, 1);
        if (n.equals("DL")) return allDates(targetMap, 2);
        if (n.equals("DLOE")) return allDates(targetMap, 3);
        if (n.equals("DEQ")) return allDates(targetMap, 4);
        if (n.equals("DOR")) return anyDatesNotEqual(targetMap);
        if (n.equals("TG")) return allTimes(targetMap, 0);
        if (n.equals("TGOE")) return allTimes(targetMap, 1);
        if (n.equals("TL")) return allTimes(targetMap, 2);
        if (n.equals("TLOE")) return allTimes(targetMap, 3);
        if (n.equals("TEQ")) return allTimes(targetMap, 4);
        if (n.equals("TOR")) return anyTimesNotEqual(targetMap);
        if (n.equals("NG")) return allNumbers(targetMap, 0);
        if (n.equals("NGOE")) return allNumbers(targetMap, 1);
        if (n.equals("NL")) return allNumbers(targetMap, 2);
        if (n.equals("NLOE")) return allNumbers(targetMap, 3);
        if (n.equals("NEQ")) return allNumbers(targetMap, 4);
        if (n.equals("NOR")) return anyNumbersEqual(targetMap);
        if (n.equals("EQ")) return allObjects(targetMap, 0);
        if (n.equals("DIFFER")) return allObjects(targetMap, 1);
        if (n.equals("OR") || n.equals("IN") || n.equals("OIN")) return anyObjectsEqual(targetMap);
        return false;
    }

    private Object value(Map<String, Object> target, String key) {
        for (Map.Entry<String, Object> entry : target.entrySet()) if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(key)) return entry.getValue();
        return null;
    }
    private boolean allStrings(Map<String, Object> target, int op) {
        for (Map.Entry<String,Object> e : map.entrySet()) {
            Object raw = value(target, e.getKey()); String actual = raw == null ? "" : raw.toString(); String expected = (String) e.getValue();
            if (op == 1 || op == 5) { if (actual.isEmpty()) return false; if (expected == null) continue; }
            if (expected == null) return false;
            boolean ok; if (op == 0) ok = actual.startsWith(expected); else if (op == 1) ok = actual.contains(expected); else if (op == 2) ok = actual.endsWith(expected); else if (op == 3) ok = Objects.equals(actual, expected); else if (op == 4) ok = actual.toLowerCase().startsWith(expected.toLowerCase()); else if (op == 5) ok = actual.toLowerCase().contains(expected.toLowerCase()); else if (op == 6) ok = actual.toLowerCase().endsWith(expected.toLowerCase()); else ok = actual.equalsIgnoreCase(expected);
            if (!ok) return false;
        } return true;
    }
    private boolean allDates(Map<String,Object> t, int op) { for (Map.Entry<String,Object> e:map.entrySet()) { LocalDate a=MatchPattern.DG.parse(value(t,e.getKey())), b=MatchPattern.DG.parse(e.getValue()); boolean ok=op==0?a!=null&&b!=null&&a.isAfter(b):op==1?!(a!=null&&b!=null&&a.isBefore(b)):op==2?a!=null&&b!=null&&a.isBefore(b):op==3?!(a!=null&&b!=null&&a.isAfter(b)):!Objects.equals(a,b); if(!ok)return false;} return true; }
    private boolean anyDatesNotEqual(Map<String,Object> t) { for(Map.Entry<String,Object> e:map.entrySet()){LocalDate a=MatchPattern.DG.parse(value(t,e.getKey())),b=MatchPattern.DG.parse(e.getValue());if(!Objects.equals(a,b))return true;}return false; }
    private boolean allTimes(Map<String,Object> t, int op) { for (Map.Entry<String,Object> e:map.entrySet()) { LocalDateTime a=MatchPattern.TG.parse(value(t,e.getKey())), b=MatchPattern.TG.parse(e.getValue()); boolean ok=op==0?a!=null&&b!=null&&a.isAfter(b):op==1?!(a!=null&&b!=null&&a.isBefore(b)):op==2?a!=null&&b!=null&&a.isBefore(b):op==3?!(a!=null&&b!=null&&a.isAfter(b)):!Objects.equals(a,b); if(!ok)return false;} return true; }
    private boolean anyTimesNotEqual(Map<String,Object> t) { for(Map.Entry<String,Object> e:map.entrySet()){LocalDateTime a=MatchPattern.TG.parse(value(t,e.getKey())),b=MatchPattern.TG.parse(e.getValue());if(!Objects.equals(a,b))return true;}return false; }
    private boolean allNumbers(Map<String,Object> t, int op) { for(Map.Entry<String,Object> e:map.entrySet()){Double a=MatchPattern.NG.parse(value(t,e.getKey())),b=MatchPattern.NG.parse(e.getValue()); boolean greater=a!=null&&(b==null||a>b), less=a==null?b!=null:b!=null&&a<b; boolean ok=op==0?greater:op==1?!less:op==2?less:op==3?!greater:Objects.equals(a,b);if(!ok)return false;}return true; }
    private boolean anyNumbersEqual(Map<String,Object> t) { for(Map.Entry<String,Object> e:map.entrySet()){Double a=MatchPattern.NG.parse(value(t,e.getKey())),b=MatchPattern.NG.parse(e.getValue());if(Objects.equals(a,b))return true;}return false; }
    private boolean sameValue(Object a, Object b) { if (a == null) return b == null || "".equals(b); if (b == null) return "".equals(a); if (!a.getClass().equals(b.getClass())) return false;
        if (a instanceof java.util.Collection<?>) {
            java.util.Collection<?> left=(java.util.Collection<?>)a, right=(java.util.Collection<?>)b;
            return left.containsAll(right) && right.containsAll(left);
        }
        if (a instanceof Map<?,?>) {
            Map<?,?> left=(Map<?,?>)a, right=(Map<?,?>)b;
            if (!left.keySet().equals(right.keySet())) return false;
            for (Object key:left.keySet()) if (!sameValue(left.get(key),right.get(key))) return false;
            return true;
        }
        return a.equals(b); }
    private boolean allObjects(Map<String,Object> t, int op) { for(Map.Entry<String,Object> e:map.entrySet()){boolean eq=sameValue(value(t,e.getKey()),e.getValue());if(op==0?!eq:eq)return false;}return true; }
    private boolean anyObjectsEqual(Map<String,Object> t) { for(Map.Entry<String,Object> e:map.entrySet())if(sameValue(value(t,e.getKey()),e.getValue()))return true;return false; }
}
