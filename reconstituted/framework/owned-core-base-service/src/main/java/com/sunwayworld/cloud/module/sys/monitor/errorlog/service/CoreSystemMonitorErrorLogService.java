/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.sys.monitor.errorlog.service;

import com.sunwayworld.cloud.module.sys.monitor.errorlog.bean.CoreSystemMonitorErrorLogBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface CoreSystemMonitorErrorLogService
extends GenericService<CoreSystemMonitorErrorLogBean, Long>,
GenericChoosableService<CoreSystemMonitorErrorLogBean, Long> {
    public void insert(CoreSystemMonitorErrorLogBean var1);

    public void revision(RestJsonWrapperBean var1);

    public void clearErrorLog();

    public void deleteNoRevisionErrorLog(RestJsonWrapperBean var1);

    public void cancelRevision(RestJsonWrapperBean var1);
}
