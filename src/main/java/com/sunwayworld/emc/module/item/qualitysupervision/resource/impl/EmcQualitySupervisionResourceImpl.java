package com.sunwayworld.emc.module.item.qualitysupervision.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.qualitysupervision.bean.EmcQualitySupervisionContBean;
import com.sunwayworld.emc.module.item.qualitysupervision.bean.EmcQualitySupervisionPlanBean;
import com.sunwayworld.emc.module.item.qualitysupervision.resource.EmcQualitySupervisionResource;
import com.sunwayworld.emc.module.item.qualitysupervision.service.EmcQualitySupervisionService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
/**
 * @author zhangft@sunwayworld.com
 * @date 2019-12-10
 */
@LogModule("质量监督")
@RestController
@GikamBean
public class EmcQualitySupervisionResourceImpl implements EmcQualitySupervisionResource {
    @Autowired
    private EmcQualitySupervisionService qualitySupervisionService;

    @Override
    public EmcQualitySupervisionService getService() {
        return qualitySupervisionService;
    }
    

    @Override
    @Log(value = "查询质量监督计划", type = LogType.SELECT)
    @RequestMapping(value = "/{supervisionId}/plans/queries", method = RequestMethod.POST)
    public Page<EmcQualitySupervisionPlanBean> selectQualitySupervisionPlanPaginationBySupervisionId(@PathVariable Long supervisionId, RestJsonWrapperBean wrapper) {
        return getService().selectQualitySupervisionPlanPaginationBySupervisionId(supervisionId, wrapper);
    }

    @Override
    @Log(value = "新增质量监督计划", type = LogType.INSERT)
    @RequestMapping(value = "/{supervisionId}/plans", method = RequestMethod.POST)
    public Long insertQualitySupervisionPlan(@PathVariable Long supervisionId, RestJsonWrapperBean wrapper) {
        return getService().insertQualitySupervisionPlan(supervisionId, wrapper);
    }

    @Override
    @Log(value = "删除质量监督计划", type = LogType.DELETE)
    @RequestMapping(value = "/plans", method = RequestMethod.DELETE)
    public void deleteQualitySupervisionPlan(RestJsonWrapperBean wrapper) {
        getService().deleteQualitySupervisionPlan(wrapper);
    }

    @Override
    @Log(value = "查询质量监督计划内容", type = LogType.SELECT)
    @RequestMapping(value = "/{planId}/conts/queries", method = RequestMethod.POST)
    public Page<EmcQualitySupervisionContBean> selectQualitySupervisionContPaginationByPlanId(@PathVariable Long planId, RestJsonWrapperBean wrapper) {
        return getService().selectQualitySupervisionContPaginationByPlanId(planId, wrapper);
    }

    @Override
    @Log(value = "新增质量监督计划内容", type = LogType.INSERT)
    @RequestMapping(value = "/{planId}/conts", method = RequestMethod.POST)
    public Long insertQualitySupervisionCont(@PathVariable Long planId, RestJsonWrapperBean wrapper) {
        return getService().insertQualitySupervisionCont(planId, wrapper);
    }

    @Override
    @Log(value = "删除质量监督计划内容", type = LogType.DELETE)
    @RequestMapping(value = "/conts", method = RequestMethod.DELETE)
    public void deleteQualitySupervisionCont(RestJsonWrapperBean wrapper) {
        getService().deleteQualitySupervisionCont(wrapper);
    }
}
