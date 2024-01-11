package com.sunwayworld.emc.module.mdm.bizmdm.test.io;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestService;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.io.file.FileHelper;
import com.sunwayworld.framework.io.file.FileRepresentation;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.ImportResultRepresentation;
import com.sunwayworld.module.item.file.function.ImportFileFunction;

@Component(value = "emcTestUpdateImportFileFunction")
public class EmcTestUpdateImportFileFunction implements ImportFileFunction {

    @Autowired
    private EmcTestService emcTestService;

    @Override
    @Transactional
    public ImportResultRepresentation apply(CoreFileBean file) {
        ImportResultRepresentation importResultRepresentation = new ImportResultRepresentation();

        FileRepresentation fr = FileRepresentation.of(file);

        List<EmcTestBean> testList = ExcelHelper.readDataFromExcel(FileHelper.getAbsolutePath(fr).toString(), 1, 2, EmcTestBean.class);

        if (testList.size() > 0) {
            emcTestService.updateIfChanged(testList);
        }
        return importResultRepresentation;
    }
}
