package com.sunwayworld.emc.module.mdm.equipt.equipt.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.service.EmcEquiptCategoryService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.io.file.FileHelper;
import com.sunwayworld.framework.io.file.FileRepresentation;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.ImportResultRepresentation;
import com.sunwayworld.module.item.file.function.ImportFileFunction;

@Component(value = "emcEquipImportFileFunction")
@GikamBean
public class EmcEquipImportFileFunction implements ImportFileFunction {

    @Autowired
    private EmcEquiptService emcEquiptService;

    @Autowired
    private EmcEquiptCategoryService equiptCategoryService;

    @Override
    public boolean test(CoreFileBean file, String service) {
        return true;
    }

    @Override
    public ImportResultRepresentation apply(CoreFileBean file) {
        ImportResultRepresentation importResultRepresentation = new ImportResultRepresentation();
        FileRepresentation fr = FileRepresentation.of(file);

        List<EmcEquiptBean> equiptList = ExcelHelper.readDataFromExcel(FileHelper.getAbsolutePath(fr).toString(), 2, 3, EmcEquiptBean.class);
        if (equiptList.isEmpty()) {
            return importResultRepresentation;
        }

        List<String> validateImpMsgList = equiptList.stream().filter(stock -> !StringUtils.isEmpty(stock.getExt$Item("validateimpmsg"))).map(stock -> stock.getExt$Item("validateimpmsg")).collect(Collectors.toList());
        if (!validateImpMsgList.isEmpty()) {
            importResultRepresentation.setMessage(validateImpMsgList.get(0));
            return importResultRepresentation;
        }

        List<EmcEquiptBean> validEquiptList = equiptList.stream().filter(e -> !StringUtils.isEmpty(e.getEquiptId()) && !StringUtils.isEmpty(e.getEquiptName()) && !StringUtils.isEmpty(e.getExt$Item("equiptCategory"))).collect(Collectors.toList());
        if (validEquiptList.isEmpty()) {
            return importResultRepresentation;
        }

        String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");
        validEquiptList.forEach(equipt -> equipt.setOrgId(parentOrgId));

        // 处理仪器状态、是否需要维护和溯源方式字段编码
        validEquiptList.forEach(e -> {
            if (StringUtils.contains(e.getStatus(), "-")) {
                e.setStatus(StringUtils.split(e.getStatus(), "-")[1].trim());
            }

            if (StringUtils.contains(e.getMaintain(), "-")) {
                e.setMaintain(StringUtils.split(e.getMaintain(), "-")[1].trim());
            }

            if (StringUtils.contains(e.getSourceMethod(), "-")) {
                e.setSourceMethod(StringUtils.split(e.getSourceMethod(), "-")[1].trim());
            }
        });

        // 处理仪器类型，将仪器类型名称转换为ID
        List<EmcEquiptCategoryBean> categoryList = equiptCategoryService.selectAll();
        Map<String, Long> categoryMap = categoryList.stream().collect(Collectors.toMap(EmcEquiptCategoryBean::getEquiptCategory, EmcEquiptCategoryBean::getId));
        validEquiptList.forEach(e -> e.setEquiptCategoryId(categoryMap.get(e.getExt$Item("equiptCategory"))));

        List<String> equiptIdList = validEquiptList.stream().map(EmcEquiptBean::getEquiptId).collect(Collectors.toList());
        List<EmcEquiptBean> existedEquiptList = emcEquiptService.getDao().selectListByOneColumnValues(equiptIdList, "EQUIPTID", Arrays.asList("ID", "EQUIPTID"));
        List<String> existedEquiptIdList = existedEquiptList.stream().map(EmcEquiptBean::getEquiptId).collect(Collectors.toList());
        Map<String, Long> equiptIdMap = existedEquiptList.stream().collect(Collectors.toMap(EmcEquiptBean::getEquiptId, EmcEquiptBean::getId));

        List<EmcEquiptBean> updateEquiptList = new ArrayList<>();
        validEquiptList.stream().filter(e -> existedEquiptIdList.contains(e.getEquiptId())).forEach(e -> {
            e.setId(equiptIdMap.get(e.getEquiptId()));
            updateEquiptList.add(e);
        });

        List<EmcEquiptBean> insertEquiptList = validEquiptList.stream().filter(e -> !existedEquiptIdList.contains(e.getEquiptId())).collect(Collectors.toList());
        insertEquiptList.forEach(e -> e.setId(ApplicationContextHelper.getNextIdentity()));

        if (!updateEquiptList.isEmpty()) {
            emcEquiptService.getDao().update(updateEquiptList);
        }

        if (!insertEquiptList.isEmpty()) {
            emcEquiptService.getDao().insert(insertEquiptList);
        }

        return importResultRepresentation;
    }

}
