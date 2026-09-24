package com.sunwayworld.emc.module.mdm.bizmdm.test.io;

import java.util.List;

import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestService;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.item.file.function.CoreFileImportFunction;

@Component(value = "emcTestUpdateImportFileFunction")
public class EmcTestUpdateImportFileFunction implements CoreFileImportFunction {

    @Autowired
    private EmcTestService emcTestService;

    @Override
    @Transactional
    public CoreFileImportResultDTO apply(CoreFileBean file) {
        CoreFileImportResultDTO CoreFileImportResultDTO = new CoreFileImportResultDTO();

        List<EmcTestBean> testList = ExcelHelper.readDataFromExcel(CoreFileUtils.getLocalPath(file).toString(), 1, 2, EmcTestBean.class);

        if (testList.size() > 0) {
            emcTestService.updateIfChanged(testList);
        }
        return CoreFileImportResultDTO;
    }
}
