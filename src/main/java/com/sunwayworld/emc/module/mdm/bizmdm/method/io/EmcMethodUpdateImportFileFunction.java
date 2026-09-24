package com.sunwayworld.emc.module.mdm.bizmdm.method.io;
import com.sunwayworld.emc.module.mdm.bizmdm.method.bean.EmcMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.method.service.EmcMethodService;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.item.file.function.CoreFileImportFunction;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(value = "emcMethodUpdateImportFileFunction")
public class EmcMethodUpdateImportFileFunction implements CoreFileImportFunction {

    @Autowired
    private EmcMethodService methodService;

    @Override
    public boolean test(CoreFileBean file, String service) {
        return true;
    }

    @Override
    public CoreFileImportResultDTO apply(CoreFileBean file) {
        CoreFileImportResultDTO CoreFileImportResultDTO = new CoreFileImportResultDTO();

        List<EmcMethodBean> methodList = ExcelHelper.readDataFromExcel(CoreFileUtils.getLocalPath(file).toString(), 0, 1, EmcMethodBean.class);
        if (methodList.size() > 0) {
            methodService.updateIfChanged(methodList);
        }
        return CoreFileImportResultDTO;
    }
}
