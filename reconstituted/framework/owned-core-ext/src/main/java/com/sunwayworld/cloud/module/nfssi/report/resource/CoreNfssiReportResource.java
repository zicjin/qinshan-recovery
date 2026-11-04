/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.report.resource;

import com.sunwayworld.cloud.module.nfssi.report.bean.CoreNfssiReportBean;
import com.sunwayworld.cloud.module.nfssi.report.bean.CoreNfssiReportItemBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/nfssi/reports"})
public interface CoreNfssiReportResource
extends GenericCloudResource<CoreNfssiReportBean, String> {
    public Page<CoreNfssiReportItemBean> selectReportItemPaginationByReportId(String var1, RestJsonWrapperBean var2);

    public void rePushReport(RestJsonWrapperBean var1);
}
