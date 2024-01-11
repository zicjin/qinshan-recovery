package com.sunwayworld.emc.module.item.consume.consume.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.consume.consume.bean.EmcConsumeBean;
import com.sunwayworld.emc.module.item.consume.consume.bean.EmcCsmStockBean;
import com.sunwayworld.emc.module.item.consume.consume.bean.EmcCsmStockLogBean;
import com.sunwayworld.emc.module.item.consume.consume.service.EmcConsumeService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/item/consume/consumes")
public interface EmcConsumeResource extends GenericResource<EmcConsumeService, EmcConsumeBean, String> {

    Long insertStock(String id, RestJsonWrapperBean wrapper);
    
    Page<EmcCsmStockBean> selectStockPagationByMtl(String id, RestJsonWrapperBean wrapper);
    
    Page<EmcCsmStockLogBean> selectStockLogPagationByStock(Long stockId, RestJsonWrapperBean wrapper);
    
    void deleteStock(String id, RestJsonWrapperBean wrapper);
    
    Long stockinStock(Long id, RestJsonWrapperBean wrapper);
    
    void inventoryStock(String id, RestJsonWrapperBean wrapper);
    
    void abandonStock(String id, RestJsonWrapperBean wrapper);
    
    void recieveStock(Long id, RestJsonWrapperBean wrapper);
    
}
