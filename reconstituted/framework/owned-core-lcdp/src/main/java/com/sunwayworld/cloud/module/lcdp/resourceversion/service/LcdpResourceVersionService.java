/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resourceversion.service;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceHistoryBean;
import com.sunwayworld.cloud.module.lcdp.resourceversion.bean.LcdpResourceVersionBean;
import com.sunwayworld.cloud.module.lcdp.submitlog.bean.LcdpSubmitLogBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;

public interface LcdpResourceVersionService
extends GenericService<LcdpResourceVersionBean, Long> {
    public void submitResource(RestJsonWrapperBean var1, Long var2);

    public void activateResource(RestJsonWrapperBean var1);

    public void insertByHistoryResourceList(List<LcdpResourceHistoryBean> var1, LcdpSubmitLogBean var2);

    public void insertByTableList(List<LcdpTableBean> var1, LcdpSubmitLogBean var2);

    public void dealHistoryData(RestJsonWrapperBean var1);
}
