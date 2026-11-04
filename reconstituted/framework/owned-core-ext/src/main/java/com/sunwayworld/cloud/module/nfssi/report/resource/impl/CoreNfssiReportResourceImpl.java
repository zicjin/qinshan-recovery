/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.report.resource.impl;

import com.sunwayworld.cloud.module.nfssi.report.bean.CoreNfssiReportBean;
import com.sunwayworld.cloud.module.nfssi.report.bean.CoreNfssiReportItemBean;
import com.sunwayworld.cloud.module.nfssi.report.resource.CoreNfssiReportResource;
import com.sunwayworld.cloud.module.nfssi.report.service.CoreNfssiReportService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u62bd\u6837\u5355\u62a5\u544a")
@RestController
@GikamBean
public class CoreNfssiReportResourceImpl
implements CoreNfssiReportResource,
AbstractGenericResource<CoreNfssiReportService, CoreNfssiReportBean, String> {
    @Autowired
    private CoreNfssiReportService coreNfssiReportService;

    @Override
    public CoreNfssiReportService getService() {
        return this.coreNfssiReportService;
    }

    @Override
    @Log(value="\u6839\u636e\u62bd\u6837\u5355\u62a5\u544aID\u67e5\u8be2\u62bd\u6837\u5355\u62a5\u544a\u7ed3\u679c\u8868", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/items/queries"}, method={RequestMethod.POST})
    public Page<CoreNfssiReportItemBean> selectReportItemPaginationByReportId(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return this.getService().selectReportItemPaginationByReportId(id, wrapper);
    }

    @Override
    @Log(value="\u91cd\u65b0\u63a8\u9001\u62a5\u544a\u7ed3\u679c", type=LogType.UPDATE)
    @RequestMapping(value={"/action/re-push"}, method={RequestMethod.PUT})
    public void rePushReport(RestJsonWrapperBean wrapper) {
        this.getService().rePush(wrapper);
    }
}
