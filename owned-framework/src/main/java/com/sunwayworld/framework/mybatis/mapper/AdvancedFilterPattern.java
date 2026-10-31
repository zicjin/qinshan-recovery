package com.sunwayworld.framework.mybatis.mapper;

/** Mutable advanced-filter clause carried into the global mapper. */
public class AdvancedFilterPattern {
    private boolean groupStart;
    private boolean groupEnd;
    private String groupLink;
    private String ingroupLink;
    private String name;
    private Object value;
    private String matchPattern;
    public AdvancedFilterPattern(String name, Object value, String matchPattern) { this.name = name; this.value = value; this.matchPattern = matchPattern; }
    public boolean isGroupStart() { return groupStart; }
    public void setGroupStart(boolean value) { groupStart = value; }
    public boolean isGroupEnd() { return groupEnd; }
    public void setGroupEnd(boolean value) { groupEnd = value; }
    public String getGroupLink() { return groupLink; }
    public void setGroupLink(String value) { groupLink = value; }
    public String getIngroupLink() { return ingroupLink; }
    public void setIngroupLink(String value) { ingroupLink = value; }
    public String getName() { return name; }
    public void setName(String value) { name = value; }
    public Object getValue() { return value; }
    public void setValue(Object value) { this.value = value; }
    public String getMatchPattern() { return matchPattern; }
    public void setMatchPattern(String value) { matchPattern = value; }
}
