/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.sys.monitor.pm.resource;

import com.sunwayworld.cloud.module.sys.monitor.pm.bean.CoreSystemMonitorSqlBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/module/sys/monitor/system-monitor-pms"})
public interface CoreSystemMonitorSqlResource
extends GenericCloudResource<CoreSystemMonitorSqlBean, Long> {
    public void revise(RestJsonWrapperBean var1);

    public void cancelRevise(RestJsonWrapperBean var1);

    public void cleanAll();
}
