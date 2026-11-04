/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.checkinrecord.service;

import com.sunwayworld.cloud.module.lcdp.checkinrecord.bean.LcdpCheckinRecordBean;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.bean.LcdpCheckOutDTO;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpExportLogFileDTO;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import org.springframework.web.multipart.MultipartFile;

public interface LcdpCheckinRecordService
extends GenericService<LcdpCheckinRecordBean, Long> {
    public void receive(MultipartFile var1, String var2);

    public void importFile(RestJsonWrapperBean var1);

    public LcdpExportLogFileDTO checkinAnalyse(RestJsonWrapperBean var1);

    public String checkin(RestJsonWrapperBean var1);

    public LcdpCheckOutDTO export(RestJsonWrapperBean var1);

    public LcdpCheckOutDTO checkout(RestJsonWrapperBean var1);

    public void ignore(RestJsonWrapperBean var1);

    public RestValidationResultBean checkinValidator(RestJsonWrapperBean var1);
}
