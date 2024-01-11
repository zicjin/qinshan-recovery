package com.sunwayworld.emc.module.item.consume.consume.mapper;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

@GikamBean
public interface EmcConsumeMapper extends GenericMapper<String> {

    void updateStockQty(String id);
}
