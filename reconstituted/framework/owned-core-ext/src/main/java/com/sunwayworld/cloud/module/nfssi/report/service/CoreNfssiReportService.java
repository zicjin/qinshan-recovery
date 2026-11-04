/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.report.service;

import com.sunwayworld.cloud.module.nfssi.report.bean.CoreNfssiReportBean;
import com.sunwayworld.cloud.module.nfssi.report.bean.CoreNfssiReportItemBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;

public interface CoreNfssiReportService
extends GenericService<CoreNfssiReportBean, String> {
    public Page<CoreNfssiReportItemBean> selectReportItemPaginationByReportId(String var1, RestJsonWrapperBean var2);

    public void rePush(RestJsonWrapperBean var1);

    public String push(CoreNfssiReportBean var1, List<CoreNfssiReportItemBean> var2);
}
