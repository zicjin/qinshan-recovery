/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.checkinrecord.resource.impl;

import com.sunwayworld.cloud.module.lcdp.checkinrecord.bean.LcdpCheckinRecordBean;
import com.sunwayworld.cloud.module.lcdp.checkinrecord.resource.LcdpCheckinRecordResource;
import com.sunwayworld.cloud.module.lcdp.checkinrecord.service.LcdpCheckinRecordService;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.bean.LcdpCheckOutDTO;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpExportLogFileDTO;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u8fc1\u5165\u8bb0\u5f55\u8868")
@RestController
@GikamBean
public class LcdpCheckinRecordResourceImpl
implements LcdpCheckinRecordResource,
AbstractGenericResource<LcdpCheckinRecordService, LcdpCheckinRecordBean, Long> {
    @Autowired
    private LcdpCheckinRecordService lcdpCheckinRecordService;

    @Override
    public LcdpCheckinRecordService getService() {
        return this.lcdpCheckinRecordService;
    }

    @Override
    @Log(value="\u65b0\u589e\u8fc1\u5165\u8bb0\u5f55\u8868", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u8d44\u6e90\u5bfc\u5165", type=LogType.INSERT)
    public void importFile(RestJsonWrapperBean jsonWrapper) {
        this.getService().importFile(jsonWrapper);
    }

    @Override
    @Log(value="\u8fc1\u5165\u8d44\u6e90\u5206\u6790", type=LogType.SELECT)
    public LcdpExportLogFileDTO checkinAnalyse(RestJsonWrapperBean jsonWrapper) {
        return this.getService().checkinAnalyse(jsonWrapper);
    }

    @Override
    @Log(value="\u8d44\u6e90\u8fc1\u5165", type=LogType.UPDATE)
    public String checkin(RestJsonWrapperBean jsonWrapper) {
        return this.getService().checkin(jsonWrapper);
    }

    @Override
    @Log(value="\u8fc1\u5165\u8d44\u6e90\u518d\u5bfc\u51fa", type=LogType.UPDATE)
    public LcdpCheckOutDTO export(RestJsonWrapperBean jsonWrapper) {
        return this.getService().export(jsonWrapper);
    }

    @Override
    @Log(value="\u8fc1\u5165\u8d44\u6e90\u518d\u8fc1\u51fa", type=LogType.UPDATE)
    public LcdpCheckOutDTO checkout(RestJsonWrapperBean jsonWrapper) {
        return this.getService().checkout(jsonWrapper);
    }

    @Override
    @Log(value="\u8fc1\u5165\u4fe1\u606f\u5ffd\u7565", type=LogType.UPDATE)
    public void ignore(RestJsonWrapperBean jsonWrapper) {
        this.getService().ignore(jsonWrapper);
    }

    @Override
    @Log(value="\u8fc1\u5165\u4fe1\u606f\u6821\u9a8c\u6570\u91cf", type=LogType.SELECT)
    public RestValidationResultBean checkinValidator(RestJsonWrapperBean jsonWrapper) {
        return this.getService().checkinValidator(jsonWrapper);
    }
}
