/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.printer.printer.service.impl;

import com.sunwayworld.cloud.module.printer.printer.bean.CorePrinterPropertiesDTO;
import com.sunwayworld.cloud.module.printer.printer.service.CorePrinterService;
import com.sunwayworld.cloud.module.printer.printlog.service.CorePrintLogService;
import com.sunwayworld.cloud.module.printer.utils.PrinterPaperUtils;
import com.sunwayworld.cloud.module.printer.utils.PrinterUtils;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CorePrinterServiceImpl
implements CorePrinterService {
    @Autowired
    private CorePrintLogService printLogService;

    @Override
    public List<String> selectPrinterNameList() {
        return PrinterUtils.selectPrinterNameList();
    }

    @Override
    public List<String> selectPaperNameList() {
        return PrinterPaperUtils.selectPaperNameList();
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public void print(RestJsonWrapperBean wrapper) {
        CorePrinterPropertiesDTO corePrinterPropertiesDTO = wrapper.parseUnique(CorePrinterPropertiesDTO.class);
        PrinterUtils.print(corePrinterPropertiesDTO);
        this.printLogService.insert(corePrinterPropertiesDTO);
    }
}
