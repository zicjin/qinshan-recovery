package com.sunwayworld.emc.module.bp.project.projscheme.io;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjSchemeBean;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjSchemeService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.item.file.function.CoreFileImportFunction;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;

@Component(value = "pfProjSchemeImportFileFunction")
public class PfProjSchemeImportFileFunction implements CoreFileImportFunction {

    @Autowired
    private EmcProjSchemeService projSchemeService;

    @Override
    public CoreFileImportResultDTO apply(CoreFileBean file) {
        CoreFileImportResultDTO result = new CoreFileImportResultDTO();
        List<EmcProjSchemeBean> itemList = ExcelHelper.readDataFromExcel(
                CoreFileUtils.getLocalPath(file).toString(), 1, 2, EmcProjSchemeBean.class);
        if (itemList.isEmpty()) {
            return result;
        }
        List<EmcProjSchemeBean> validItemList = itemList.stream()
                .filter(p -> !StringUtils.isEmpty(p.getFolderNo())).collect(Collectors.toList());
        if (validItemList.isEmpty()) {
            return result;
        }
        List<String> folderNoList = validItemList.stream().map(EmcProjSchemeBean::getFolderNo)
                .distinct().collect(Collectors.toList());
        List<EmcProjSchemeBean> schemeList = this.projSchemeService.getDao()
                .selectListByOneColumnValues(folderNoList, "folderNo", new Order[0]);
        if (schemeList.isEmpty()) {
            throw new InvalidDataException("导入数据必须有系统生成的点位");
        }
        EmcProjSchemeBean modelScheme = schemeList.get(0);
        List<EmcProjSchemeBean> insertList = new ArrayList<>();
        List<EmcProjSchemeBean> updateList = new ArrayList<>();
        for (EmcProjSchemeBean excelScheme : validItemList) {
            EmcProjSchemeBean oldScheme = schemeList.stream()
                    .filter(e -> StringUtils.equals(e.getFolderNo(), excelScheme.getFolderNo()))
                    .findFirst().orElse(null);
            EmcProjSchemeBean scheme = new EmcProjSchemeBean();
            if (oldScheme == null) {
                BeanUtils.copyProperties(modelScheme, scheme, EmcConstant.CREATED_COLUMNS);
                scheme.setId(ApplicationContextHelper.getNextIdentity());
                scheme.setFolderNo(excelScheme.getFolderNo());
                insertList.add(scheme);
            } else {
                scheme.setId(oldScheme.getId());
                updateList.add(scheme);
            }
            scheme.setMonitorPointNo(excelScheme.getMonitorPointNo());
            scheme.setMonitorPointName(excelScheme.getMonitorPointName());
        }
        this.projSchemeService.getDao().update(updateList, "MonitorPointNo", "MonitorPointName");
        this.projSchemeService.getDao().insert(insertList);
        return result;
    }
}
