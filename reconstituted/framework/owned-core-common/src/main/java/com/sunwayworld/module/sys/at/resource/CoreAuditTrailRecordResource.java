/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.at.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.sys.at.bean.CoreAuditTraceOverviewDTO;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordDTO;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineDTO;
import com.sunwayworld.module.sys.at.service.CoreAuditTrailRecordService;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/sys/at-records"})
public interface CoreAuditTrailRecordResource
extends GenericResource<CoreAuditTrailRecordService, CoreAuditTrailRecordBean, Long> {
    public List<CoreAuditTrailRecordDTO> selectRecordList(@PathVariable String var1, @PathVariable String var2);

    public List<CoreAuditTrailRecordBean> selectAtRecordList(@PathVariable String var1, @PathVariable String var2);

    public List<CoreAuditTrailRecordLineBean> selectRecordLineTableNameList(@PathVariable Long var1);

    public List<CoreAuditTrailRecordLineBean> selectRecordLineList(@PathVariable Long var1, @PathVariable String var2);

    public Page<CoreAuditTrailRecordLineBean> selectRecordLinePaginationList(RestJsonWrapperBean var1);

    public CoreAuditTrailRecordLineBean selectRecordLineDetail(@PathVariable Long var1);

    public List<CoreAuditTrailRecordBean> selectAtRecordList(@PathVariable String var1, RestJsonWrapperBean var2);

    public Page<CoreAuditTrailRecordBean> selectAtRecordPaginationList(@PathVariable String var1, RestJsonWrapperBean var2);

    public Page<CoreAuditTraceOverviewDTO> selectTraceOverview(RestJsonWrapperBean var1);

    public List<CoreAuditTrailRecordLineDTO> selectRecordLineDTOByTableNameAndTargetId(@PathVariable String var1, @PathVariable String var2);
}
