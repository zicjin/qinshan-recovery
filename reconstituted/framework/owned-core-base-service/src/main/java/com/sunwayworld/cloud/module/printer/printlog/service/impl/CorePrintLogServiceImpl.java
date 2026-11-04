/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.printer.printlog.service.impl;

import com.sunwayworld.cloud.module.printer.printer.bean.CorePrinterPropertiesDTO;
import com.sunwayworld.cloud.module.printer.printlog.bean.CorePrintLogBean;
import com.sunwayworld.cloud.module.printer.printlog.persistent.dao.CorePrintLogDao;
import com.sunwayworld.cloud.module.printer.printlog.service.CorePrintLogService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CorePrintLogServiceImpl
implements CorePrintLogService {
    @Autowired
    private CorePrintLogDao corePrintLogDao;

    @Override
    public CorePrintLogDao getDao() {
        return this.corePrintLogDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(CorePrinterPropertiesDTO printerPropertiesDTO) {
        CorePrintLogBean printLog = new CorePrintLogBean();
        printLog.setId(ApplicationContextHelper.getNextIdentity());
        printLog.setCopies(printerPropertiesDTO.getCopies());
        printLog.setFileId(printerPropertiesDTO.getFileId());
        printLog.setFileName(printerPropertiesDTO.getFileName());
        printLog.setPrinterName(printerPropertiesDTO.getPrinterName());
        this.getDao().insert(printLog);
        return printLog.getId();
    }
}
