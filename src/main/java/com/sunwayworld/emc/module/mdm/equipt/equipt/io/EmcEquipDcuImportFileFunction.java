package com.sunwayworld.emc.module.mdm.equipt.equipt.io;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasResultBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.EmcEasResultService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.io.file.FileHelper;
import com.sunwayworld.framework.io.file.FileRepresentation;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.ImportResultRepresentation;
import com.sunwayworld.module.item.file.function.ImportFileFunction;

/**
 * 导入采集结果
 * @author zhengxy@sunwayworld.com 2020年09月17日
 */
@Component(value = "emcEquipDcuImportFileFunction")
public class EmcEquipDcuImportFileFunction implements ImportFileFunction {

    @Autowired
    private EmcEasResultService easResultService;

    @Override
    public ImportResultRepresentation apply(CoreFileBean excelFile) {
        ImportResultRepresentation importResultRepresentation = new ImportResultRepresentation();
        FileRepresentation fr = FileRepresentation.of(excelFile);

        List<EmcEasResultBean> resultList = ExcelHelper.readDataFromExcel(FileHelper.getAbsolutePath(fr).toString(), 0, 1, EmcEasResultBean.class);
        if (resultList.isEmpty()) {
            return importResultRepresentation;
        }

        Long scriptId = NumberUtils.parseLong(excelFile.getTargetId().split("\\$")[1]);
        resultList.forEach(result -> {
            result.setId(ApplicationContextHelper.getNextIdentity());
            result.setScriptId(scriptId);
        });
        easResultService.getDao().insert(resultList);

        return importResultRepresentation;
    }

}
