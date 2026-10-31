package com.sunwayworld.framework.mybatis.mapper;

import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Mutable mapper parameter state retained by the owned compatibility layer. */
public class MapperParameter extends HashMap<String, Object> {
    private static final long serialVersionUID = -5846726410121110379L;
    public static final String FILTER_PARAM_PATTERNS = "filterParamPatterns";
    public static final String FILTER_BY_ITEM_LIST = "filter_by_item_list";
    public static final String TARGET_FILTER = "targetFilter";
    public static final String ADVANCED_FILTER_PATTERNS = "advancedFilterPatterns";
    public static final String MULTI_PROPERTY_FILTER_PATTERN = "multiPropertyFilterPattern";

    public MapperParameter() { }
    public MapperParameter(int p0) { super(p0); }
    public MapperParameter(Map<? extends String, ? extends Object> p0) { super(p0); }
    public MapperParameter(int p0, float p1) { super(p0, p1); }

    public void setTableName(String p0) { put("service_table", p0); }
    public void setDetailQueries() { put("queries_detail", "1"); }
    public void setRawQueries() { put("queries_raw", "1"); }
    public boolean isRawQueries() { return "1".equals(get("queries_raw")); }
    public void setSearchableQueries() { put("queries_searchable", "1"); }
    public void setAuditableQueries() { put("queries_auditable", "1"); }
    public void setChoosableQueries() { put("queries_choosable", "1"); }
    public void setSelectableQueries() { put("queries_selectable", "1"); }
    public void setSuspendableQueries() { put("queries_suspendable", "1"); }
    public void setUndoableQueries() { put("queries_undoable", "1"); }
    public void setWithdrawableQueries() { put("queries_withdrawable", "1"); }

    public void setFilter(SearchFilter p0) {
        if (!p0.getFilterMap().isEmpty()) putAll(p0.getFilterMap());
        if (!p0.getStdFilterList().isEmpty()) {
            addFilterParamPatterns(p0.getStdFilterList().toArray(new FilterParamPattern[0]));
        }
    }

    public void addFilterParamPatterns(FilterParamPattern... p0) {
        FilterParamPattern[] current = getFilterParamPatterns();
        put(FILTER_PARAM_PATTERNS, ArrayUtils.concat(current == null ? new FilterParamPattern[0] : current, p0));
    }

    public FilterParamPattern[] getFilterParamPatterns() {
        return (FilterParamPattern[]) get(FILTER_PARAM_PATTERNS);
    }

    public <T extends Persistable<?>> void setFilter(List<T> p0, String... p1) {
        if (p0 == null || p0.isEmpty()) return;
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (T item : p0) {
            if (item == null) continue;
            Map<String, Object> values = new HashMap<String, Object>();
            if (p1 == null || p1.length == 0) {
                values.putAll(BeanUtils.toMap(item));
                values.putAll(item.getExt$());
            } else {
                for (String column : p1) values.put(column, BeanUtils.getPropertyValue(item, column));
            }
            if (!values.isEmpty()) result.add(values);
        }
        if (!result.isEmpty()) put(FILTER_BY_ITEM_LIST, result);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getFilterByItemList() {
        return (List<Map<String, Object>>) get(FILTER_BY_ITEM_LIST);
    }

    public void setTargetFilter(RestJsonWrapperBean.TargetTableFilterData p0) { put(TARGET_FILTER, p0); }
    public RestJsonWrapperBean.TargetTableFilterData getTargetFilter() {
        return (RestJsonWrapperBean.TargetTableFilterData) get(TARGET_FILTER);
    }
    public void setAdvancedFilterPatterns(AdvancedFilterPattern[] p0) { put(ADVANCED_FILTER_PATTERNS, p0); }
    public AdvancedFilterPattern[] getAdvancedFilterPatterns() {
        return (AdvancedFilterPattern[]) get(ADVANCED_FILTER_PATTERNS);
    }
    public void setMultiPropertyFilterPattern(MultiPropertyFilterPattern p0) { put(MULTI_PROPERTY_FILTER_PATTERN, p0); }
    public MultiPropertyFilterPattern getMultiPropertyFilterPattern() {
        return (MultiPropertyFilterPattern) get(MULTI_PROPERTY_FILTER_PATTERN);
    }

    public void setSuspendedFlag(String p0) {
        setFilter(SearchFilter.instance().match("lastSuspendedFlag", p0).filter(MatchPattern.EQ));
    }

    public void setProcessStatus(String p0, ProcessStatus p1) {
        SearchFilter filter = SearchFilter.instance();
        switch (p1) {
            case DRAFT:
            case DRAFT_REJECTED:
                filter.match(p0, "draft").filter(MatchPattern.SB); break;
            case APPROVE:
            case APPROVE_REJECTED:
                filter.match(p0, "approve").filter(MatchPattern.SB); break;
            case DONE:
                filter.match(p0, "done").filter(MatchPattern.SEQ); break;
            default:
                filter.match(p0, p1.name().toLowerCase()).filter(MatchPattern.SPECIAL);
        }
        setFilter(filter);
    }

    public void setActivatedFlag(String p0) {
        setFilter(SearchFilter.instance().match("activatedFlag", p0).filter(MatchPattern.EQ));
    }

    public void setAuthorityParameter(String p0) { put("authority_" + p0, "1"); }
    public void setAuditAuthority() { setAuthorityParameter("audit"); }
    public void setOrgAuthority() { setAuthorityParameter("org"); }
    public void setCurrentAndChildOrgAuthority(String p0) {
        throw new UnsupportedOperationException("Owned read-only boundary: MapperParameter.setCurrentAndChildOrgAuthority requires an organization service");
    }

    @SuppressWarnings("unchecked")
    public void setOrderParam(String p0, String p1) {
        List<Pair<String, String>> values = (List<Pair<String, String>>) get("orderParams");
        if (values == null) values = new ArrayList<Pair<String, String>>();
        values.add(Pair.of(p0, p1));
        put("orderParams", values);
    }

    @SuppressWarnings("unchecked")
    public boolean hasOrderParam() {
        List<Pair<String, String>> values = (List<Pair<String, String>>) get("orderParams");
        return values != null && !values.isEmpty();
    }

    public void setSelectByRoleIdParam(Long p0) { put("select_by_roleId", p0); }
    public void setSelectByRoleIdExclusiveParam(Long p0) { put("select_by_roleId_exclusive", p0); }
    public void setSimpleColumns(String p0) { put("select_columns", p0); }

    public static boolean hasOrderParams(List<Pair<String, String>> p0, String p1) {
        if (StringUtils.isBlank(p1)) return p0 != null && !p0.isEmpty();
        if (p0 == null || p0.isEmpty()) return false;
        for (String ignored : StringUtils.split(p1, ",")) {
            final String column = StringUtils.trim(ignored);
            p0.removeIf(pair -> column.equalsIgnoreCase(pair.getFirst()));
        }
        return !p0.isEmpty();
    }

    public void reviseParams(com.sunwayworld.framework.database.context.EntityContext p0) {
        throw new UnsupportedOperationException("Owned read-only boundary: MapperParameter.reviseParams requires database dialect metadata");
    }
}
