/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.service.impl;

import com.sunwayworld.cloud.module.office.service.CoreOfficeTmplService;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.module.item.file.bean.CoreOfficeDTO;
import com.sunwayworld.module.item.file.consumer.CoreOfficeInvokeConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="coreOfficeTmplInvokeConsumer")
public class CoreOfficeTmplInvokeConsumer
implements CoreOfficeInvokeConsumer {
    @Autowired
    private CoreOfficeTmplService officeTmplService;

    @Override
    public void accept(CoreOfficeDTO officeDTO) {
        String tmplId = ServletUtils.getCurrentRequestParamMap().get("tmplId");
        this.officeTmplService.clearBindDs(NumberUtils.parseLong(tmplId));
    }
}
