/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.submitlog.service;

import com.sunwayworld.cloud.module.lcdp.resourceversion.bean.LcdpResourceVersionBean;
import com.sunwayworld.cloud.module.lcdp.submitlog.bean.LcdpSubmitLogBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface LcdpSubmitLogService
extends GenericService<LcdpSubmitLogBean, Long> {
    public void submitResource(RestJsonWrapperBean var1);

    public Page<LcdpResourceVersionBean> selectVersionPaginationByLogId(Long var1, RestJsonWrapperBean var2);

    public Page<LcdpResourceVersionBean> viewResource(RestJsonWrapperBean var1);
}
