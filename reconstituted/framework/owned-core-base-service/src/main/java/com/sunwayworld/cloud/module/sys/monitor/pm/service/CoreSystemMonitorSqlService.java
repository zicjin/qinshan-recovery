/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.sys.monitor.pm.service;

import com.sunwayworld.cloud.module.sys.monitor.pm.bean.CoreSystemMonitorSqlBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreSystemMonitorSqlService
extends GenericService<CoreSystemMonitorSqlBean, Long> {
    public Long insert(CoreSystemMonitorSqlBean var1);

    public void revise(RestJsonWrapperBean var1);

    public void cancelRevise(RestJsonWrapperBean var1);

    public void cleanAll();
}
