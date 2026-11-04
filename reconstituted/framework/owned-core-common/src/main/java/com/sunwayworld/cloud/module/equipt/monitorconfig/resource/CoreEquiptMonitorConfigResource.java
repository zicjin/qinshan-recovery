/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.monitorconfig.resource;

import com.sunwayworld.cloud.module.equipt.monitorconfig.bean.CoreEquiptMonitorConfigBean;
import com.sunwayworld.cloud.module.equipt.monitorconfig.bean.CoreEquiptMonitorLogBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/equipt/monitor-configs"})
public interface CoreEquiptMonitorConfigResource
extends GenericCloudResource<CoreEquiptMonitorConfigBean, Long> {
    public Page<CoreEquiptMonitorLogBean> selectMonitorLogPaginationByEquiptMonitorConfigId(Long var1, RestJsonWrapperBean var2);
}
