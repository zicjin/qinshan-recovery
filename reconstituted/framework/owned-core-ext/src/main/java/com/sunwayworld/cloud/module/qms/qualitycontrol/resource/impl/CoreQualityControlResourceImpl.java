/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.qualitycontrol.resource.impl;

import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreQualityControlPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreQualityControlPlanService;
import com.sunwayworld.cloud.module.qms.qualitycontrol.bean.CoreQualityControlBean;
import com.sunwayworld.cloud.module.qms.qualitycontrol.bean.CoreQualityControlItemBean;
import com.sunwayworld.cloud.module.qms.qualitycontrol.resource.CoreQualityControlResource;
import com.sunwayworld.cloud.module.qms.qualitycontrol.service.CoreQualityControlService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u8d28\u91cf\u63a7\u5236")
@RestController
@GikamBean
public class CoreQualityControlResourceImpl
implements CoreQualityControlResource,
AbstractGenericResource<CoreQualityControlService, CoreQualityControlBean, Long>,
GenericAuditableResource<CoreQualityControlService, CoreQualityControlBean, Long> {
    @Autowired
    private CoreQualityControlService coreQualityControlService;
    @Autowired
    private CoreQualityControlPlanService coreQualityControlPlanService;

    @Override
    public CoreQualityControlService getService() {
        return this.coreQualityControlService;
    }

    @Override
    @Log(value="\u65b0\u589e\u8d28\u91cf\u63a7\u5236", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u8d28\u63a7\u8ba1\u5212\u9009\u62e9\u9875\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/quality-control-plans/queries/choosable"}, method={RequestMethod.POST})
    public Page<CoreQualityControlPlanBean> selectControlPlanChoosablePagination(RestJsonWrapperBean wrapper) {
        return this.coreQualityControlPlanService.selectChoosablePagination(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u8d28\u91cf\u63a7\u5236ID\u67e5\u8be2\u8d28\u91cf\u63a7\u5236-\u5b50\u8868", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/control-items/queries"}, method={RequestMethod.POST})
    public Page<CoreQualityControlItemBean> selectControlItemPaginationByQualityControlId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectControlItemPaginationByQualityControlId(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u8d28\u91cf\u63a7\u5236-\u5b50\u8868", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/control-items"}, method={RequestMethod.POST})
    public Long insertControlItem(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertControlItem(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u8d28\u91cf\u63a7\u5236-\u5b50\u8868", type=LogType.DELETE)
    @RequestMapping(value={"/{id}/control-items"}, method={RequestMethod.DELETE})
    public void deleteControlItem(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteControlItem(id, wrapper);
    }
}
