package com.sunwayworld.owned.consumables;

import com.sunwayworld.emc.module.item.consume.consume.bean.EmcCsmStockBean;
import com.sunwayworld.emc.module.item.consume.consume.dao.EmcCsmStockDao;
import com.sunwayworld.emc.module.item.consume.consume.service.EmcCsmStockService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.FilterParamPattern;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import java.util.Map;

/** Original stock service; writes are enabled only by the isolated loopback seam. */
public final class OwnedConsumablesStockService implements EmcCsmStockService {
    private final OwnedConsumablesStockDao dao;
    public OwnedConsumablesStockService(OwnedConsumablesStockDao dao) { this.dao = dao; }
    @Override public EmcCsmStockDao getDao() { return dao; }
    @Override public Page<EmcCsmStockBean> selectPaginationByFilter(SearchFilter filter, RestJsonWrapperBean wrapper) {
        return dao.readPage(materialId(filter), wrapper);
    }
    @Override public Long insert(String id, RestJsonWrapperBean wrapper) { return dao.insertStock(id, wrapper); }
    @Override public void storage(Long id, RestJsonWrapperBean wrapper) { dao.storageStock(id, wrapper); }
    @Override public void inventory(String id, RestJsonWrapperBean wrapper) { dao.inventoryStock(id, wrapper); }
    @Override public void abandon(String id, RestJsonWrapperBean wrapper) { dao.abandonStock(id, wrapper); }
    @Override public void recieve(Long id, RestJsonWrapperBean wrapper) { dao.receiveStock(id, wrapper); }
    @Override public void delete(String id, RestJsonWrapperBean wrapper) { dao.deleteStocks(id, wrapper); }
    private static String materialId(SearchFilter filter) {
        if (filter == null || filter.getFilterMap().size() != 0 || filter.getStdFilterList().size() != 1) throw invalidFilter();
        FilterParamPattern pattern = filter.getStdFilterList().get(0);
        if (!MatchPattern.EQ.name().equals(pattern.getMatchPattern()) || pattern.getMap().size() != 1) throw invalidFilter();
        Map.Entry<String,Object> entry = pattern.getMap().entrySet().iterator().next();
        if (!"MTLID".equalsIgnoreCase(entry.getKey()) || entry.getValue() == null || String.valueOf(entry.getValue()).trim().isEmpty()) throw invalidFilter();
        return String.valueOf(entry.getValue());
    }
    private static IllegalArgumentException invalidFilter() { return new IllegalArgumentException("unsupported stock filter shape"); }
}
