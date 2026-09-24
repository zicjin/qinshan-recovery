package com.sunwayworld.emc.module.mdm.basemdm.mtl.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlMaintainBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlMaintainItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockConcentBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockLogBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.resource.EmcMtlResource;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcMtlMaintainItemService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcMtlMaintainService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcMtlService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockConcentService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockLogService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;

@LogModule("材料管理")
@RestController
@GikamBean
public class EmcMtlResourceImpl implements EmcMtlResource {

    @Autowired
    private EmcMtlService mtlService;

    @Autowired
    private EmcStockService stockService;

    @Autowired
    private EmcStockLogService stockLogService;

    @Autowired
    private EmcMtlMaintainService mtlMaintainService;

    @Autowired
    private EmcMtlMaintainItemService mtlMaintainItemService;

    @Autowired
    private EmcStockConcentService stockConcentService;

    @Override
    public EmcMtlService getService() {
        return mtlService;
    }

    @Log(value = "新增库存", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/{id}/stocks", method = RequestMethod.POST)
    public Long insertStock(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return stockService.insert(id, wrapper);
    }

    @Log(value = "查询库存", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/{id}/stocks/queries")
    public Page<EmcStockBean> selectStockPagationByMtl(@PathVariable String id, RestJsonWrapperBean wrapper) {
        if (StringUtils.equals(id, "0")) {
            return stockService.selectPagination(wrapper);
        }
        return stockService.selectPaginationByFilter(SearchFilter.instance().match("MTLID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Log(value = "查询库存记录", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/stocks/{stockId}/logs/queries")
    public Page<EmcStockLogBean> selectStockLogPagationByStock(@PathVariable Long stockId, RestJsonWrapperBean wrapper) {
        return stockLogService.selectPaginationByFilter(SearchFilter.instance().match("STOCKID", stockId).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value = "删除库存", type = LogType.DELETE)
    @RequestMapping(value = "/{id}/stocks", method = RequestMethod.DELETE)
    public void deleteStock(@PathVariable String id, RestJsonWrapperBean wrapper) {
        stockService.delete(id, wrapper);
    }

    @Log(value = "入库库存", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/stocks/{stockId}/action/stockin", method = RequestMethod.PUT)
    public Long stockinStock(@PathVariable Long stockId, RestJsonWrapperBean wrapper) {
        stockService.storage(stockId, wrapper);
        return stockId;
    }

    @Log(value = "盘存库存", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/{id}/stocks/action/inventory", method = RequestMethod.PUT)
    public void inventoryStock(@PathVariable String id, RestJsonWrapperBean wrapper) {
        stockService.inventory(id, wrapper);
    }

    @Log(value = "废弃库存", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/{id}/stocks/action/abandon", method = RequestMethod.PUT)
    public void abandonStock(@PathVariable String id, RestJsonWrapperBean wrapper) {
        stockService.abandon(id, wrapper);
    }

    @Log(value = "领取库存", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/stocks/{id}/action/recieve", method = RequestMethod.PUT)
    public void recieveStock(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        stockService.recieve(id, wrapper);
    }


    @Log(value = "查询物质维护", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/{id}/maintains/queries")
    public Page<EmcMtlMaintainBean> selectMaintainPagationByMtl(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return mtlMaintainService.selectPaginationByFilter(SearchFilter.instance().match("MTLID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Log(value = "新增物质维护", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/{id}/maintains", method = RequestMethod.POST)
    public void insertMaintain(@PathVariable String id, RestJsonWrapperBean wrapper) {
        mtlMaintainService.insertMaintain(id, wrapper);
    }

    @Log(value = "删除物质维护", type = LogType.DELETE)
    @Override
    @RequestMapping(value = "/{id}/maintains", method = RequestMethod.DELETE)
    public void deleteMaintain(@PathVariable String id, RestJsonWrapperBean wrapper) {
        mtlMaintainService.delete(wrapper);
    }


    @Log(value = "查询分析项", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/maintains/{mtlMaintainId}/maintain-items/queries")
    public Page<EmcMtlMaintainItemBean> selectMaintainItemPagationByMaintain(@PathVariable Long mtlMaintainId, RestJsonWrapperBean wrapper) {
        return mtlMaintainItemService.selectPaginationByFilter(SearchFilter.instance().match("MTLMAITAINID", mtlMaintainId).filter(MatchPattern.EQ), wrapper);
    }

    @Log(value = "新增分析项", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/maintains/{mtlMaintainId}/maintain-items", method = RequestMethod.POST)
    public void insertMaintainItem(@PathVariable Long mtlMaintainId, RestJsonWrapperBean wrapper) {
        mtlMaintainItemService.insert(mtlMaintainId, wrapper);
    }

    @Log(value = "删除分析项", type = LogType.DELETE)
    @Override
    @RequestMapping(value = "/maintains/{mtlMaintainId}/maintain-items", method = RequestMethod.DELETE)
    public void deleteMaintainItem(RestJsonWrapperBean wrapper) {
        mtlMaintainItemService.delete(wrapper);
    }

    @Log(value = "查询浓度", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/stocks/{stockId}/concents/queries")
    public Page<EmcStockConcentBean> selectConcentPagationByStock(@PathVariable Long stockId, RestJsonWrapperBean wrapper) {
        return stockConcentService.selectPaginationByFilter(SearchFilter.instance().match("STOCKID", stockId).filter(MatchPattern.EQ), wrapper);
    }

    @Log(value = "查询浓度", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/stocks/concents/queries")
    public Page<EmcStockConcentBean> selectConcentPagation(RestJsonWrapperBean wrapper) {
        return stockConcentService.selectPagination(wrapper);
    }

    @Override
    @Log(value = "详情选择页查询", type = LogType.SELECT)
    @RequestMapping(value = "/queries/detail-choosable", method = RequestMethod.POST)
    public Page<EmcStockBean> selectDetailChoosablePagination(RestJsonWrapperBean wrapper) {
        return stockService.selectDetailChoosablePagination(wrapper);
    }

    @Override
    @Log(value = "验证库存唯一性", type = LogType.VALIDATE)
    @RequestMapping(value = "/stocks/{id}/action/validate-unique/{columnName}/{columnValue}", method = RequestMethod.POST)
    public RestValidationResultBean validateStockUnique(@PathVariable Long id, @PathVariable String columnName, @PathVariable String columnValue) {
        return stockService.validateStockUnique(id, columnName, columnValue);
    }

    @Override
    @Log(value = "新增一般试剂库存", type = LogType.INSERT)
    @RequestMapping(value = "/{id}/general-mtl-stocks", method = RequestMethod.POST)
    public Long insertGeneralStock(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return stockService.insertGeneralStock(id, wrapper);
    }

    @Log(value = "高低限修约", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/stockConcent/calculate", method = RequestMethod.PUT)
    public void stockConcentCalculate(RestJsonWrapperBean wrapper) {
        stockConcentService.stockConcentCalculate(wrapper);
    }

    @Log(value = "实验室标准样品-标样选择页", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/queries/qc-std-stock", method = RequestMethod.POST)
    public Page<EmcStockBean> selectQcStdStockPagination(RestJsonWrapperBean wrapper) {
        return stockService.selectQcStdStockPagination(wrapper);
    }

    @Log(value = "材料数据导出", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/export-file", method = RequestMethod.POST)
    public String exportFile(RestJsonWrapperBean wrapper) {
        return getService().exportFile(wrapper);
    }

    @Log(value = "一般试剂数据导出", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/export-general-file", method = RequestMethod.POST)
    public String exportGeneralFile(RestJsonWrapperBean wrapper) {
        return getService().exportGeneralFile(wrapper);
    }
}
