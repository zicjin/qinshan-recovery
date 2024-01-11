package com.sunwayworld.emc.module.item.consume.consume.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.consume.consume.bean.EmcCsmStockBean;
import com.sunwayworld.emc.module.item.consume.consume.bean.EmcCsmStockLogBean;
import com.sunwayworld.emc.module.item.consume.consume.resource.EmcConsumeResource;
import com.sunwayworld.emc.module.item.consume.consume.service.EmcConsumeService;
import com.sunwayworld.emc.module.item.consume.consume.service.EmcCsmStockLogService;
import com.sunwayworld.emc.module.item.consume.consume.service.EmcCsmStockService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("耗材管理")
@RestController
@GikamBean
public class EmcConsumeResourceImpl implements EmcConsumeResource {

    @Autowired
    private EmcConsumeService consumeService;
    
    @Autowired
    private EmcCsmStockService csmStockService;
    
    @Autowired
    private EmcCsmStockLogService csmStockLogService;
    
    
    @Override
    public EmcConsumeService getService() {
        return consumeService;
    }

    @Log(value = "新增库存", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/{id}/stocks", method = RequestMethod.POST)
    public Long insertStock(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return csmStockService.insert(id, wrapper);
    }

    @Log(value = "查询库存", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/{id}/stocks/queries")
    public Page<EmcCsmStockBean> selectStockPagationByMtl(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return csmStockService.selectPaginationByFilter(SearchFilter.instance().match("MTLID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Log(value = "查询库存记录", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/stocks/{stockId}/logs/queries")
    public Page<EmcCsmStockLogBean> selectStockLogPagationByStock(@PathVariable Long stockId, RestJsonWrapperBean wrapper) {
        return csmStockLogService.selectPaginationByFilter(SearchFilter.instance().match("STOCKID", stockId).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value = "删除库存", type = LogType.DELETE)
    @RequestMapping(value = "/{id}/stocks", method = RequestMethod.DELETE)
    public void deleteStock(@PathVariable String id, RestJsonWrapperBean wrapper) {
        csmStockService.delete(id, wrapper);
    }

    @Log(value = "入库库存", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/stocks/{stockId}/action/stockin", method = RequestMethod.PUT) 
    public Long stockinStock(@PathVariable Long stockId, RestJsonWrapperBean wrapper) {
        csmStockService.storage(stockId, wrapper);
        return stockId;
    }

    @Log(value = "盘存库存", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/{id}/stocks/action/inventory", method = RequestMethod.PUT)
    public void inventoryStock(@PathVariable String id, RestJsonWrapperBean wrapper) {
        csmStockService.inventory(id, wrapper);
    }

    @Log(value = "废弃库存", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/{id}/stocks/action/abandon", method = RequestMethod.PUT)
    public void abandonStock(@PathVariable String id, RestJsonWrapperBean wrapper) {
        csmStockService.abandon(id, wrapper);
    }

    @Log(value = "领取库存", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/stocks/{id}/action/recieve", method = RequestMethod.PUT)
    public void recieveStock(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        csmStockService.recieve(id, wrapper);
    }


}
