/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.sys.monitor.errorlog.resource.impl;

import com.sunwayworld.cloud.module.sys.monitor.errorlog.bean.CoreSystemMonitorErrorLogBean;
import com.sunwayworld.cloud.module.sys.monitor.errorlog.resource.CoreSystemMonitorErrorLogResource;
import com.sunwayworld.cloud.module.sys.monitor.errorlog.service.CoreSystemMonitorErrorLogService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u9519\u8bef\u65e5\u5fd7")
@RestController
@GikamBean
@RequestMapping(value={"/secure/module/sys/monitor/error-logs"})
public class CoreSystemMonitorErrorLogResourceImpl
implements CoreSystemMonitorErrorLogResource,
AbstractGenericResource<CoreSystemMonitorErrorLogService, CoreSystemMonitorErrorLogBean, Long> {
    @Autowired
    private CoreSystemMonitorErrorLogService coreSystemMonitorErrorLogService;

    @Override
    public CoreSystemMonitorErrorLogService getService() {
        return this.coreSystemMonitorErrorLogService;
    }

    @Override
    @Log(value="\u4fee\u6539\u5df2\u4fee\u8ba2\u9519\u8bef\u65e5\u5fd7\u72b6\u6001", type=LogType.UPDATE)
    @RequestMapping(value={"/action/cancel-revise"}, method={RequestMethod.PUT})
    public void cancelRevision(RestJsonWrapperBean wrapper) {
        this.coreSystemMonitorErrorLogService.cancelRevision(wrapper);
    }

    @Override
    @Log(value="\u4fee\u6539\u672a\u4fee\u8ba2\u9519\u8bef\u65e5\u5fd7\u72b6\u6001", type=LogType.UPDATE)
    @RequestMapping(value={"/action/revise"}, method={RequestMethod.PUT})
    public void revision(RestJsonWrapperBean wrapper) {
        this.coreSystemMonitorErrorLogService.revision(wrapper);
    }

    @Override
    @Log(value="\u6e05\u7a7a\u672a\u4fee\u8ba2\u9519\u8bef\u65e5\u5fd7", type=LogType.DELETE)
    @RequestMapping(value={"/action/clear"}, method={RequestMethod.DELETE})
    public void clear() {
        this.coreSystemMonitorErrorLogService.clearErrorLog();
    }

    @Override
    @Log(value="\u5220\u9664\u672a\u4fee\u8ba2\u9519\u8bef\u65e5\u5fd7", type=LogType.DELETE)
    @RequestMapping(value={"/action/delete/no-revision-error-logs"}, method={RequestMethod.DELETE})
    public void deleteNoRevisionErrorLog(RestJsonWrapperBean wrapper) {
        this.coreSystemMonitorErrorLogService.deleteNoRevisionErrorLog(wrapper);
    }
}
