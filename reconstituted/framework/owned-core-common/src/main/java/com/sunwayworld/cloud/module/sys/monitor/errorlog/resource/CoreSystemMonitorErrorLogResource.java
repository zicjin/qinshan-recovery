/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.sys.monitor.errorlog.resource;

import com.sunwayworld.cloud.module.sys.monitor.errorlog.bean.CoreSystemMonitorErrorLogBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/module/sys/monitor/error-logs"})
public interface CoreSystemMonitorErrorLogResource
extends GenericCloudResource<CoreSystemMonitorErrorLogBean, Long> {
    public void cancelRevision(RestJsonWrapperBean var1);

    public void revision(RestJsonWrapperBean var1);

    public void clear();

    public void deleteNoRevisionErrorLog(RestJsonWrapperBean var1);
}
