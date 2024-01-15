package com.sunwayworld.emc.module.bp.project.projscheme.io;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjSchemeBean;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjSchemeService;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.item.file.function.CoreFileImportFunction;

@Component(value = "pfProjSchemeImportFileFunction")
public class PfProjSchemeImportFileFunction implements CoreFileImportFunction {

    @Autowired
    private EmcProjSchemeService projSchemeService;

    @Override
    public CoreFileImportResultDTO apply(CoreFileBean file) {
        CoreFileImportResultDTO CoreFileImportResultDTO = new CoreFileImportResultDTO();
        FilePathDTO fr = CoreFileUtils.toFilePath(file);

        List<EmcProjSchemeBean> itemList = ExcelHelper.readDataFromExcel(FilePathManager.getLocalPath(fr).toString(), 1, 2, EmcProjSchemeBean.class);
        if (itemList.isEmpty()) {
            return CoreFileImportResultDTO;
        }

        List<EmcProjSchemeBean> validItemList = itemList.stream().filter(p -> !StringUtils.isEmpty(p.getFolderNo())).collect(Collectors.toList());
        if (validItemList.isEmpty()) {
            return CoreFileImportResultDTO;
        }

        List<String> folderNoList = validItemList.stream().map(EmcProjSchemeBean::getFolderNo).distinct().collect(Collectors.toList());
        List<EmcProjSchemeBean> schemeList = projSchemeService.getDao().selectListByOneColumnValues(folderNoList, "folderNo", Arrays.asList("id", "folderNo"));

        schemeList.forEach(scheme -> validItemList.stream().filter(item -> StringUtils.equals(scheme.getFolderNo(), item.getFolderNo())).findFirst().ifPresent(item -> {
            scheme.setMonitorPointNo(item.getMonitorPointNo());
            scheme.setMonitorPointName(item.getMonitorPointName());
        }));

        projSchemeService.getDao().update(schemeList, "MonitorPointNo", "MonitorPointName");

        return CoreFileImportResultDTO;
    }

}
