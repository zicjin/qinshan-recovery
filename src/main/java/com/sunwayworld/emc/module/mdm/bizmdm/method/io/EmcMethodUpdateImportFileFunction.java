package com.sunwayworld.emc.module.mdm.bizmdm.method.io;
import com.sunwayworld.emc.module.mdm.bizmdm.method.bean.EmcMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.method.service.EmcMethodService;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.io.file.FileHelper;
import com.sunwayworld.framework.io.file.FileRepresentation;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.ImportResultRepresentation;
import com.sunwayworld.module.item.file.function.ImportFileFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(value = "emcMethodUpdateImportFileFunction")
public class EmcMethodUpdateImportFileFunction implements ImportFileFunction {

    @Autowired
    private EmcMethodService methodService;

    @Override
    public boolean test(CoreFileBean file, String service) {
        return true;
    }

    @Override
    public ImportResultRepresentation apply(CoreFileBean file) {
        ImportResultRepresentation importResultRepresentation = new ImportResultRepresentation();

        FileRepresentation fr = FileRepresentation.of(file);

        List<EmcMethodBean> methodList = ExcelHelper.readDataFromExcel(FileHelper.getAbsolutePath(fr).toString(), 0, 1, EmcMethodBean.class);
        if (methodList.size() > 0) {
            methodService.updateIfChanged(methodList);
        }
        return importResultRepresentation;
    }
}
