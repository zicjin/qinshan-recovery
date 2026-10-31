package com.sunwayworld.owned.consumables;

import com.sunwayworld.emc.module.item.consume.consume.bean.EmcCsmStockLogBean;
import com.sunwayworld.emc.module.item.consume.consume.dao.EmcCsmStockLogDao;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.owned.internal.data.OwnedReadDao;

/** Bounded original DAO contract; inherited writes fail explicitly. */
public final class OwnedConsumablesStockLogDao extends MybatisDaoSupport<EmcCsmStockLogBean,Long>
        implements EmcCsmStockLogDao, OwnedReadDao<EmcCsmStockLogBean,Long> {
    private final OwnedConsumablesReads reads;
    public OwnedConsumablesStockLogDao(OwnedConsumablesReads reads) { this.reads = reads; }
    @Override public Page<EmcCsmStockLogBean> readPage(RestJsonWrapperBean wrapper) { throw new UnsupportedOperationException("Stock-log read requires the original stock route"); }
    @Override public EmcCsmStockLogBean readDetail(Long id) { return reads.stockLogDetail(id, EmcCsmStockLogBean.class); }
    public Page<EmcCsmStockLogBean> readPage(Long id, RestJsonWrapperBean wrapper) { return reads.logs(id, wrapper, EmcCsmStockLogBean.class); }
}
