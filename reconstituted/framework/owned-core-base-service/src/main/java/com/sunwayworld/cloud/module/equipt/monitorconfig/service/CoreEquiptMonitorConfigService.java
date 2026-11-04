/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.monitorconfig.service;

import com.sunwayworld.cloud.module.equipt.monitorconfig.bean.CoreEquiptMonitorConfigBean;
import com.sunwayworld.cloud.module.equipt.monitorconfig.bean.CoreEquiptMonitorLogBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreEquiptMonitorConfigService
extends GenericService<CoreEquiptMonitorConfigBean, Long> {
    public Page<CoreEquiptMonitorLogBean> selectMonitorLogPaginationByEquiptMonitorConfigId(Long var1, RestJsonWrapperBean var2);

    public void updateEquiptWorkStatus(String var1);

    public Long insertMonitorLogByCode(String var1);

    public String selectLastWorkStatusByCode(String var1);

    public void updateMonitorLogByCode(String var1);
}
