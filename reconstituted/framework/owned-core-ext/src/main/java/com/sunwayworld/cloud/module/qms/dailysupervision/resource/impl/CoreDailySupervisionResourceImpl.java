/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.dailysupervision.resource.impl;

import com.sunwayworld.cloud.module.qms.dailysupervision.bean.CoreDailySupervisionBean;
import com.sunwayworld.cloud.module.qms.dailysupervision.bean.CoreDailySupervisionConBean;
import com.sunwayworld.cloud.module.qms.dailysupervision.bean.CoreDailySupervisionItemBean;
import com.sunwayworld.cloud.module.qms.dailysupervision.resource.CoreDailySupervisionResource;
import com.sunwayworld.cloud.module.qms.dailysupervision.service.CoreDailySupervisionService;
import com.sunwayworld.cloud.module.qms.dailysupervision.validator.CoreDailySupervisionCompleteValidator;
import com.sunwayworld.cloud.module.qms.dailysupervision.validator.CoreDailySupervisionItemInertValidator;
import com.sunwayworld.cloud.module.qms.dailysupervision.validator.CoreDailySupervisionStartValidator;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u65e5\u5e38\u76d1\u7763")
@RestController
@GikamBean
public class CoreDailySupervisionResourceImpl
implements CoreDailySupervisionResource,
GenericAuditableResource<CoreDailySupervisionService, CoreDailySupervisionBean, Long>,
AbstractGenericResource<CoreDailySupervisionService, CoreDailySupervisionBean, Long> {
    @Autowired
    private CoreDailySupervisionService coreDailySupervisionService;

    @Override
    public CoreDailySupervisionService getService() {
        return this.coreDailySupervisionService;
    }

    @Override
    @Log(value="\u65b0\u589e\u65e5\u5e38\u76d1\u7763", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u542f\u52a8", type=LogType.PROCESS)
    @RequestMapping(value={"/action/start-process"}, method={RequestMethod.PUT})
    @ValidateDataWith(value=CoreDailySupervisionStartValidator.class)
    public void startProcess(RestJsonWrapperBean wrapper) {
        this.getService().startProcess(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u5ba1\u6838\u901a\u8fc7", type=LogType.PROCESS)
    @RequestMapping(value={"/action/complete-task"}, method={RequestMethod.PUT})
    @ValidateDataWith(value=CoreDailySupervisionCompleteValidator.class)
    public void completeTask(RestJsonWrapperBean wrapper) {
        this.getService().completeTask(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u65e5\u5e38\u76d1\u7763ID\u67e5\u8be2\u65e5\u5e38\u76d1\u7763\u9879\u76ee", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/supervision-items/queries"}, method={RequestMethod.POST})
    public Page<CoreDailySupervisionItemBean> selectSupervisionItemPaginationByDailySupervisionId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectSupervisionItemPaginationByDailySupervisionId(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u65e5\u5e38\u76d1\u7763\u9879\u76ee", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/supervision-items"}, method={RequestMethod.POST})
    @ValidateDataWith(value=CoreDailySupervisionItemInertValidator.class)
    public Long insertSupervisionItem(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertSupervisionItem(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u65e5\u5e38\u76d1\u7763\u9879\u76ee", type=LogType.DELETE)
    @RequestMapping(value={"/{id}/supervision-items"}, method={RequestMethod.DELETE})
    public void deleteSupervisionItem(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteSupervisionItem(id, wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u65e5\u5e38\u76d1\u7763ID\u67e5\u8be2\u65e5\u5e38\u76d1\u7763\u5185\u5bb9", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/supervision-cons/queries"}, method={RequestMethod.POST})
    public Page<CoreDailySupervisionConBean> selectSupervisionConPaginationByDailySupervisionId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectSupervisionConPaginationByDailySupervisionId(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u65e5\u5e38\u76d1\u7763\u5185\u5bb9", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/supervision-cons"}, method={RequestMethod.POST})
    public Long insertSupervisionCon(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertSupervisionCon(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u65e5\u5e38\u76d1\u7763\u5185\u5bb9", type=LogType.DELETE)
    @RequestMapping(value={"/{id}/supervision-cons"}, method={RequestMethod.DELETE})
    public void deleteSupervisionCon(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteSupervisionCon(id, wrapper);
    }
}
