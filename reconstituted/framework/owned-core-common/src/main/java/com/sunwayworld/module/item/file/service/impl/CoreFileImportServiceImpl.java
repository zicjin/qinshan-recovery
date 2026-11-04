/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.file.service.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.item.file.function.CoreFileImportFunction;
import com.sunwayworld.module.item.file.service.CoreFileImportService;
import com.sunwayworld.module.item.file.service.CoreFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreFileImportServiceImpl
implements CoreFileImportService {
    @Autowired
    private CoreFileService fileService;

    @Override
    public CoreFileImportResultDTO importData(Long id, String service) {
        CoreFileBean coreFile;
        CoreFileImportFunction fileImportFunction = (CoreFileImportFunction)ApplicationContextHelper.getBean(service);
        if (fileImportFunction.test(coreFile = this.fileService.selectDetail(id), service)) {
            return (CoreFileImportResultDTO)fileImportFunction.apply(coreFile);
        }
        CoreFileImportResultDTO importResult = new CoreFileImportResultDTO();
        importResult.setMessage("GIKAM.FILE.IMPORT.TEMPLATE-MISMATCH");
        return importResult;
    }
}
