package com.sunwayworld.owned.consumables;

import com.sunwayworld.emc.module.item.consume.consume.bean.EmcCsmStockLogBean;
import com.sunwayworld.emc.module.item.consume.consume.dao.EmcCsmStockLogDao;
import com.sunwayworld.emc.module.item.consume.consume.service.EmcCsmStockLogService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.FilterParamPattern;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import java.util.Map;

/** Original stock-log service; only the resource's trusted STOCKID=EQ read is enabled. */
public final class OwnedConsumablesStockLogService implements EmcCsmStockLogService {
    private final OwnedConsumablesStockLogDao dao;
    public OwnedConsumablesStockLogService(OwnedConsumablesStockLogDao dao) { this.dao = dao; }
    @Override public EmcCsmStockLogDao getDao() { return dao; }
    @Override public Page<EmcCsmStockLogBean> selectPaginationByFilter(SearchFilter filter, RestJsonWrapperBean wrapper) {
        return dao.readPage(stockId(filter), wrapper);
    }
    private static Long stockId(SearchFilter filter) {
        if (filter == null || filter.getFilterMap().size() != 0 || filter.getStdFilterList().size() != 1) throw invalidFilter();
        FilterParamPattern pattern = filter.getStdFilterList().get(0);
        if (!MatchPattern.EQ.name().equals(pattern.getMatchPattern()) || pattern.getMap().size() != 1) throw invalidFilter();
        Map.Entry<String,Object> entry = pattern.getMap().entrySet().iterator().next();
        if (!"STOCKID".equalsIgnoreCase(entry.getKey()) || entry.getValue() == null) throw invalidFilter();
        try { return Long.valueOf(String.valueOf(entry.getValue())); }
        catch (NumberFormatException failure) { throw invalidFilter(); }
    }
    private static IllegalArgumentException invalidFilter() { return new IllegalArgumentException("unsupported stock-log filter shape"); }
}
