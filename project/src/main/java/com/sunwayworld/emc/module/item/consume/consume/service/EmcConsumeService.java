package com.sunwayworld.emc.module.item.consume.consume.service;

import com.sunwayworld.emc.module.item.consume.consume.bean.EmcConsumeBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcConsumeService extends GenericService<EmcConsumeBean, String> {

    void updateStockQty(String id);
}
