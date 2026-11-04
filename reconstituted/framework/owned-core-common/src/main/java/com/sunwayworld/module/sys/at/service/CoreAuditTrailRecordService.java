/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.at.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.at.bean.CoreAuditTraceOverviewDTO;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordDTO;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineDTO;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;

public interface CoreAuditTrailRecordService
extends GenericService<CoreAuditTrailRecordBean, Long> {
    public Long insert(CoreAuditTrailRecordBean var1);

    public List<CoreAuditTrailRecordDTO> selectRecordList(String var1, String var2);

    public List<CoreAuditTrailRecordBean> selectAtRecordList(String var1, String var2);

    public List<CoreAuditTrailRecordLineBean> selectRecordLineTableNameList(@PathVariable Long var1);

    public List<CoreAuditTrailRecordLineBean> selectRecordLineList(@PathVariable Long var1, @PathVariable String var2);

    public Page<CoreAuditTrailRecordLineBean> selectRecordLinePaginationList(RestJsonWrapperBean var1);

    public CoreAuditTrailRecordLineBean selectRecordLineDetail(@PathVariable Long var1);

    public List<CoreAuditTrailRecordBean> selectAtRecordList(String var1, RestJsonWrapperBean var2);

    public Page<CoreAuditTrailRecordBean> selectAtRecordPaginationList(String var1, RestJsonWrapperBean var2);

    public Page<CoreAuditTraceOverviewDTO> selectTraceOverview(RestJsonWrapperBean var1);

    public List<CoreAuditTrailRecordLineDTO> selectRecordLineDTOByTableNameAndTargetId(String var1, String var2);
}
