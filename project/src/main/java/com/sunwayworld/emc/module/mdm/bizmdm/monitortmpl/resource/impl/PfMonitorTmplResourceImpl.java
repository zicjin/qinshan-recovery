package com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeCostBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeSamplingCostBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.resource.PfMonitorTmplResource;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorSchemeCostService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.PfMonitorTmplService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("监测模板")
@RestController
@GikamBean
public class PfMonitorTmplResourceImpl extends EmcMonitorTmplResourceImpl implements PfMonitorTmplResource {

    @Autowired
    private PfMonitorTmplService monitorTmplService;

    @Autowired
    EmcMonitorSchemeCostService monitorSchemeCostService;

    @Autowired
    private PfMonitorTmplService pfMonitorTmplService;

    @Override
    @Log(value = "新增监测费用", type = LogType.INSERT)
    @RequestMapping(value = "/costs", method = RequestMethod.POST)
    public Long insertSchemeCost(RestJsonWrapperBean wrapper) {
        return monitorSchemeCostService.insert(wrapper);
    }

    @Override
    @Log(value = "根据监测模板ID查询监测费用", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/costs/queries", method = RequestMethod.POST)
    public Page<EmcMonitorSchemeCostBean> selectSchemeCostPaginationByTmplId(@PathVariable("id") String id, RestJsonWrapperBean wrapper) {
        return monitorSchemeCostService.selectPaginationByFilter(SearchFilter.instance().match("MONITORTMPLID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value = "复制监测方案到其他监测模板", type = LogType.SELECT)
    @RequestMapping(value = "/schemes/action/copy-to", method = RequestMethod.POST)
    public void copySchemeTo(RestJsonWrapperBean wrapper) {
        monitorTmplService.copySchemeTo(wrapper);
    }

    @Override
    @Log(value = "监测方案启用", type = LogType.SELECT)
    @RequestMapping(value = "/schemes/action/enable", method = RequestMethod.POST)
    public void enableScheme(RestJsonWrapperBean wrapper) {
        monitorTmplService.enableScheme(wrapper);
    }

    @Override
    @Log(value = "监测方案停用", type = LogType.SELECT)
    @RequestMapping(value = "/schemes/action/disable", method = RequestMethod.POST)
    public void disableScheme(RestJsonWrapperBean wrapper) {
        monitorTmplService.disableScheme(wrapper);
    }

    @Override
    @Log(value = "根据公司复制监测能力库数据", type = LogType.INSERT)
    @RequestMapping(value = "/copy-all-monitor-to-org", method = RequestMethod.POST)
    public void copyAllMonitorToOrg(RestJsonWrapperBean wrapper) {
    	monitorTmplService.copyAllMonitorToOrg(wrapper);
    }

    @Override
    @Log(value = "监测方案修改名称", type = LogType.UPDATE)
    @RequestMapping(value = "/schemes/action/edit-schemes-name", method = RequestMethod.POST)
    public void editSchemesName(RestJsonWrapperBean wrapper) {
        monitorTmplService.editSchemesName(wrapper);
    }

    @Override
    @Log(value = "查询监测方案采样费用", type = LogType.SELECT)
    @RequestMapping(value = "/sampling-costs/queries", method = RequestMethod.POST)
    public Page<EmcMonitorSchemeSamplingCostBean> selectSchemeSamplingCostPagination(RestJsonWrapperBean wrapper) {
        return monitorTmplService.selectSchemeSamplingCostPagination(wrapper);
    }

    @Override
    @Log(value = "添加采样费用", type = LogType.INSERT)
    @RequestMapping(value = "/sampling-costs/action/insert", method = RequestMethod.POST)
    public void insertSamplingCost(RestJsonWrapperBean wrapper) {
        monitorTmplService.insertSamplingCost(wrapper);
    }

    @Override
    @Log(value = "删除采样费用", type = LogType.DELETE)
    @RequestMapping(value = "/sampling-costs/action/delete", method = RequestMethod.DELETE)
    public void deleteSamplingCost(RestJsonWrapperBean wrapper) {
        monitorTmplService.deleteSamplingCost(wrapper);
    }

    @Override
    @Log(value = "同步监测方案上的费用信息到其他监测模板", type = LogType.SELECT)
    @RequestMapping(value = "/schemes/action/sync-price-info-to", method = RequestMethod.POST)
    public void syncPriceInfoTo(RestJsonWrapperBean wrapper) {
        monitorTmplService.syncPriceInfoTo(wrapper);
    }

    @Override
    @Log(value = "复制到-采样费用", type = LogType.SELECT)
    @RequestMapping(value = "/action/copy-to-sampling-cost", method = RequestMethod.POST)
    public Long copyToSamplingCost(RestJsonWrapperBean wrapper) {
        return pfMonitorTmplService.copyToSamplingCost(wrapper);
    }
}
