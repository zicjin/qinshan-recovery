package com.sunwayworld.emc.module.mdm.bizmdm.test.io;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.io.file.FileHelper;
import com.sunwayworld.framework.io.file.FileRepresentation;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.ImportResultRepresentation;
import com.sunwayworld.module.item.file.function.ImportFileFunction;

@Component(value = "emcTestImportFileFunction")
public class EmcTestImportFileFunction implements ImportFileFunction {

    @Autowired
    private EmcTestService emcTestService;

    @Override
    @Transactional
    public ImportResultRepresentation apply(CoreFileBean file) {
        ImportResultRepresentation importResultRepresentation = new ImportResultRepresentation();

        FileRepresentation fr = FileRepresentation.of(file);

        List<EmcTestBean> testList = ExcelHelper.readDataFromExcel(FileHelper.getAbsolutePath(fr).toString(), 1, 2, EmcTestBean.class);

        if (testList.size() > 0) {
            List<String> testNameList = testList.stream().map(e -> e.getTestName()).collect(Collectors.toList());

            List<EmcTestBean> existTestList = emcTestService.getDao().selectListByOneColumnValues(testNameList, "TESTNAME");

            testList.removeIf(u -> existTestList.stream().anyMatch(m -> m.getTestName().equals(u.getTestName())));

            testList.stream().forEach(e -> {
                e.setId(ApplicationContextHelper.getNextSequenceNumber(emcTestService.getDao().getTable()));
            });
            emcTestService.getDao().insert(testList);
        }
        return importResultRepresentation;
    }
}
