/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.managereview.resource.impl;

import com.sunwayworld.cloud.module.qms.managereview.bean.CoreManageReviewBean;
import com.sunwayworld.cloud.module.qms.managereview.bean.CoreManageReviewImproveBean;
import com.sunwayworld.cloud.module.qms.managereview.resource.CoreManageReviewResource;
import com.sunwayworld.cloud.module.qms.managereview.service.CoreManageReviewService;
import com.sunwayworld.cloud.module.qms.managereview.validator.CoreManageReviewCompleteTaskValidator;
import com.sunwayworld.cloud.module.qms.managereview.validator.CoreManageReviewSubmitTaskValidator;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.selectable.resource.AbstractGenericSelectableResource;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u7ba1\u7406\u8bc4\u5ba1")
@RestController
@GikamBean
public class CoreManageReviewResourceImpl
implements CoreManageReviewResource,
GenericAuditableResource<CoreManageReviewService, CoreManageReviewBean, Long>,
AbstractGenericResource<CoreManageReviewService, CoreManageReviewBean, Long>,
AbstractGenericSelectableResource<CoreManageReviewService, CoreManageReviewBean, Long> {
    @Autowired
    private CoreManageReviewService coreManageReviewService;

    @Override
    public CoreManageReviewService getService() {
        return this.coreManageReviewService;
    }

    @Override
    @Log(value="\u65b0\u589e\u7ba1\u7406\u8bc4\u5ba1", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u7ba1\u7406\u8bc4\u5ba1ID\u67e5\u8be2\u7ba1\u7406\u8bc4\u5ba1-\u6539\u8fdb\u8ba1\u5212", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/review-improves/queries"}, method={RequestMethod.POST})
    public Page<CoreManageReviewImproveBean> selectReviewImprovePaginationByManageReviewId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectReviewImprovePaginationByManageReviewId(id, wrapper);
    }

    @Override
    @Log(value="\u4fdd\u5b58\u7ba1\u7406\u8bc4\u5ba1-\u6539\u8fdb\u8ba1\u5212", type=LogType.UPDATE)
    @RequestMapping(value={"/{id}/review-improves"}, method={RequestMethod.PUT})
    public void saveReviewImprove(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().saveReviewImprove(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u7ba1\u7406\u8bc4\u5ba1-\u6539\u8fdb\u8ba1\u5212", type=LogType.DELETE)
    @RequestMapping(value={"/review-improves"}, method={RequestMethod.DELETE})
    public void deleteReviewImprove(RestJsonWrapperBean wrapper) {
        this.getService().deleteReviewImprove(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u542f\u52a8", type=LogType.PROCESS)
    @RequestMapping(value={"/action/start-process"}, method={RequestMethod.PUT})
    @ValidateDataWith(value=CoreManageReviewSubmitTaskValidator.class)
    public void startProcess(RestJsonWrapperBean wrapper) {
        this.getService().startProcess(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u5ba1\u6838\u901a\u8fc7", type=LogType.PROCESS)
    @RequestMapping(value={"/action/complete-task"}, method={RequestMethod.PUT})
    @ValidateDataWith(value=CoreManageReviewCompleteTaskValidator.class)
    public void completeTask(RestJsonWrapperBean wrapper) {
        this.getService().completeTask(wrapper);
    }
}
