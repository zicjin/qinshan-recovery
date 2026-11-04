/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.at.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTraceOverviewDTO;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordDTO;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineDTO;
import com.sunwayworld.module.sys.at.resource.CoreAuditTrailRecordResource;
import com.sunwayworld.module.sys.at.service.CoreAuditTrailRecordLineService;
import com.sunwayworld.module.sys.at.service.CoreAuditTrailRecordService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
public class CoreAuditTrailRecordResourceImpl
implements CoreAuditTrailRecordResource {
    @Autowired
    private CoreAuditTrailRecordService recordService;
    @Autowired
    private CoreAuditTrailRecordLineService recordLineService;

    @Override
    public CoreAuditTrailRecordService getService() {
        return this.recordService;
    }

    public CoreAuditTrailRecordLineService getLineService() {
        return this.recordLineService;
    }

    @Override
    @Log(value="\u5ba1\u8ba1\u8ddf\u8e2a\u8bb0\u5f55\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/queries/searchable/{tableName}/{id}"}, method={RequestMethod.POST})
    public List<CoreAuditTrailRecordDTO> selectRecordList(@PathVariable String tableName, @PathVariable String id) {
        return this.getService().selectRecordList(tableName, id);
    }

    @Override
    @Log(value="\u5ba1\u8ba1\u8ddf\u8e2a\u8bb0\u5f55\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/queries/atrecord/{tableName}/{id}"}, method={RequestMethod.POST})
    public List<CoreAuditTrailRecordBean> selectAtRecordList(@PathVariable String tableName, @PathVariable String id) {
        return this.getService().selectAtRecordList(tableName, id);
    }

    @Override
    @Log(value="\u6839\u636e\u8868\u540d\u67e5\u8be2\u5ba1\u8ba1\u8ddf\u8e2a\u8bb0\u5f55", type=LogType.SELECT)
    @RequestMapping(value={"/queries/atrecord/{tableName}"}, method={RequestMethod.POST})
    public List<CoreAuditTrailRecordBean> selectAtRecordList(@PathVariable String tableName, RestJsonWrapperBean wrapper) {
        return this.getService().selectAtRecordList(tableName, wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u8868\u540d\u67e5\u8be2\u5ba1\u8ba1\u8ddf\u8e2a\u8bb0\u5f55", type=LogType.SELECT)
    @RequestMapping(value={"/atrecord/{tableName}/queries"}, method={RequestMethod.POST})
    public Page<CoreAuditTrailRecordBean> selectAtRecordPaginationList(@PathVariable String tableName, RestJsonWrapperBean wrapper) {
        return this.getService().selectAtRecordPaginationList(tableName, wrapper);
    }

    @Override
    @Log(value="\u5ba1\u8ba1\u8ddf\u8e2a\u5173\u952e\u6570\u636e\u6982\u89c8", type=LogType.SELECT)
    @RequestMapping(value={"/queries/trace-overview"}, method={RequestMethod.POST})
    public Page<CoreAuditTraceOverviewDTO> selectTraceOverview(RestJsonWrapperBean wrapper) {
        return this.getService().selectTraceOverview(wrapper);
    }

    @Override
    @Log(value="\u5ba1\u8ba1\u8ddf\u8e2a\u6570\u636e\u53ca\u660e\u7ec6\u8868\u8be6\u60c5\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/queries/record-lines-with-record/{tableName}/{targetId}"}, method={RequestMethod.POST})
    public List<CoreAuditTrailRecordLineDTO> selectRecordLineDTOByTableNameAndTargetId(@PathVariable String tableName, @PathVariable String targetId) {
        return this.getService().selectRecordLineDTOByTableNameAndTargetId(tableName, targetId);
    }

    @Override
    @Log(value="\u5ba1\u8ba1\u8ddf\u8e2a\u67e5\u8be2\u76f8\u5173\u8868", type=LogType.SELECT)
    @RequestMapping(value={"/queries/record-lines/{recordId}"}, method={RequestMethod.POST})
    public List<CoreAuditTrailRecordLineBean> selectRecordLineTableNameList(@PathVariable Long recordId) {
        return this.getService().selectRecordLineTableNameList(recordId);
    }

    @Override
    @Log(value="\u5ba1\u8ba1\u8ddf\u8e2a\u67e5\u8be2\u76f8\u5173\u8868\u5b57\u6bb5\u4fee\u6539", type=LogType.SELECT)
    @RequestMapping(value={"/queries/record-lines/{recordId}/{tableName}"}, method={RequestMethod.POST})
    public List<CoreAuditTrailRecordLineBean> selectRecordLineList(@PathVariable Long recordId, @PathVariable String tableName) {
        return this.getService().selectRecordLineList(recordId, tableName);
    }

    @Override
    @Log(value="\u5ba1\u8ba1\u8ddf\u8e2a\u660e\u7ec6\u8868\u8be6\u60c5\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/record-lines/{recordLineId}"}, method={RequestMethod.GET})
    public CoreAuditTrailRecordLineBean selectRecordLineDetail(@PathVariable Long recordLineId) {
        return this.getService().selectRecordLineDetail(recordLineId);
    }

    @Override
    @Log(value="\u5ba1\u8ba1\u8ddf\u8e2a\u660e\u7ec6\u8868\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/record-lines/queries"}, method={RequestMethod.POST})
    public Page<CoreAuditTrailRecordLineBean> selectRecordLinePaginationList(RestJsonWrapperBean wrapper) {
        return this.getService().selectRecordLinePaginationList(wrapper);
    }
}
