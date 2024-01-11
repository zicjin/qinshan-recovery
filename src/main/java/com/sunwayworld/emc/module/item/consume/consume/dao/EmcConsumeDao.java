package com.sunwayworld.emc.module.item.consume.consume.dao;

import com.sunwayworld.emc.module.item.consume.consume.bean.EmcConsumeBean;
import com.sunwayworld.framework.support.base.dao.GenericDao;

public interface EmcConsumeDao extends GenericDao<EmcConsumeBean, String> {

    void updateStockQty(String id);
}
