package com.sunwayworld.emc.module.mdm.equipt.equipt.io;

import java.util.List;

import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasResultBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.EmcEasResultService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.item.file.function.CoreFileImportFunction;

/**
 * 导入采集结果
 * @author zhengxy@sunwayworld.com 2020年09月17日
 */
@Component(value = "emcEquipDcuImportFileFunction")
public class EmcEquipDcuImportFileFunction implements CoreFileImportFunction {

    @Autowired
    private EmcEasResultService easResultService;

    @Override
    public CoreFileImportResultDTO apply(CoreFileBean excelFile) {
        CoreFileImportResultDTO fileImportResultDTO = new CoreFileImportResultDTO();
        FilePathDTO fr = CoreFileUtils.toFilePath(excelFile);

        List<EmcEasResultBean> resultList = ExcelHelper.readDataFromExcel(FilePathManager.getLocalPath(fr).toString(), 0, 1, EmcEasResultBean.class);
        if (resultList.isEmpty()) {
            return fileImportResultDTO;
        }

        Long scriptId = NumberUtils.parseLong(excelFile.getTargetId().split("\\$")[1]);
        resultList.forEach(result -> {
            result.setId(ApplicationContextHelper.getNextIdentity());
            result.setScriptId(scriptId);
        });
        easResultService.getDao().insert(resultList);

        return fileImportResultDTO;
    }

}
