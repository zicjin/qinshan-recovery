package com.sunwayworld.emc.module.item.consume.consume.service;

import com.sunwayworld.emc.module.item.consume.consume.bean.EmcCsmStockBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcCsmStockService extends GenericService<EmcCsmStockBean, Long> {

    Long insert(String mtlId, RestJsonWrapperBean wrapper);
    
    void storage(Long id, RestJsonWrapperBean wrapper);
    
    void inventory(String id, RestJsonWrapperBean wrapper);
    
    void abandon(String id, RestJsonWrapperBean wrapper);
    
    void recieve(Long id, RestJsonWrapperBean wrapper);
    
    void delete(String id, RestJsonWrapperBean wrapper);
}
