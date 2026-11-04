/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.checkinrecord.resource;

import com.sunwayworld.cloud.module.lcdp.checkinrecord.bean.LcdpCheckinRecordBean;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.bean.LcdpCheckOutDTO;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpExportLogFileDTO;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/lcdp/checkin-records"})
public interface LcdpCheckinRecordResource
extends GenericCloudResource<LcdpCheckinRecordBean, Long> {
    @RequestMapping(value={"/action/import"}, method={RequestMethod.POST})
    public void importFile(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/checkin-analyse"}, method={RequestMethod.POST})
    public LcdpExportLogFileDTO checkinAnalyse(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/checkin"}, method={RequestMethod.POST})
    public String checkin(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/export"}, method={RequestMethod.POST})
    public LcdpCheckOutDTO export(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/checkout"}, method={RequestMethod.POST})
    public LcdpCheckOutDTO checkout(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/ignore"}, method={RequestMethod.POST})
    public void ignore(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/checkin-validator"}, method={RequestMethod.POST})
    public RestValidationResultBean checkinValidator(RestJsonWrapperBean var1);
}
