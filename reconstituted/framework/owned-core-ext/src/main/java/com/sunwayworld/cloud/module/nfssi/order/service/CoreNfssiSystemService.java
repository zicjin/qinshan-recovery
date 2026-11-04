/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.order.service;

import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiOrderDTO;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiOrderQueryParamDTO;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiSystemResponseDTO;
import com.sunwayworld.cloud.module.nfssi.report.bean.CoreNfssiReportBean;
import com.sunwayworld.cloud.module.nfssi.report.bean.CoreNfssiReportItemBean;
import java.util.List;

public interface CoreNfssiSystemService {
    public String getToken();

    public List<String> getOrderList(CoreNfssiOrderQueryParamDTO var1);

    public CoreNfssiOrderDTO getOrderInfoById(String var1);

    public CoreNfssiSystemResponseDTO pushReport(CoreNfssiReportBean var1, List<CoreNfssiReportItemBean> var2);
}
