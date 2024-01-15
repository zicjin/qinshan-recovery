package com.sunwayworld.emc.module.mdm.basemdm.mtl.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlDTO;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlMaintainBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcMtlMaintainService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcMtlService;
import com.sunwayworld.emc.module.mdm.basemdm.mtlcategory.bean.EmcMtlCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtlcategory.service.EmcMtlCategoryService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.item.file.function.CoreFileImportFunction;

@GikamBean
@Component(value = "emcMtlImportFileFunction")
public class EmcMtlImportFileFunction implements CoreFileImportFunction {

    @Autowired
    private EmcMtlCategoryService emcMtlCategoryService;

    @Autowired
    private EmcMtlService emcMtlService;

    @Autowired
    private EmcMtlMaintainService mtlMaintainService;

    @Override
    @Transactional
    public CoreFileImportResultDTO apply(CoreFileBean excelFile) {
        CoreFileImportResultDTO CoreFileImportResultDTO = new CoreFileImportResultDTO();
        FilePathDTO fr = CoreFileUtils.toFilePath(excelFile);

        List<EmcMtlDTO> mtlDTOList = ExcelHelper.readDataFromExcel(FilePathManager.getLocalPath(fr).toString(), 2, 3, EmcMtlDTO.class);
        if (mtlDTOList.isEmpty()) {
            return CoreFileImportResultDTO;
        }

        // 过滤掉没有填写材料名称的数据
        List<EmcMtlDTO> notEmptyMtlList = mtlDTOList.stream().filter(mtl -> !StringUtils.isEmpty(mtl.getMtlName())).collect(Collectors.toList());
        if (notEmptyMtlList.isEmpty()) {
            return CoreFileImportResultDTO;
        }

        // 过滤掉材料类型不是系统中已经维护的
        List<EmcMtlCategoryBean> mtlCategoryList = emcMtlCategoryService.selectAll();
        List<String> mtlCategoryNameList = mtlCategoryList.stream().map(EmcMtlCategoryBean::getMtlCategoryName).collect(Collectors.toList());
        List<EmcMtlDTO> validMtlList = notEmptyMtlList.stream().filter(mtl -> mtlCategoryNameList.contains(mtl.getMtlCategoryName())).collect(Collectors.toList());
        if (validMtlList.isEmpty()) {
            return CoreFileImportResultDTO;
        }

        List<String> validMtlNameList = validMtlList.stream().map(EmcMtlDTO::getMtlName).collect(Collectors.toList());
        List<EmcMtlBean> existedMtlList = emcMtlService.getDao().selectListByOneColumnValues(validMtlNameList, "MTLNAME", Arrays.asList("ID", "MTLNAME"));
        List<String> existedMtlNameList = existedMtlList.stream().map(EmcMtlBean::getMtlName).collect(Collectors.toList());

        Map<String, Long> mtlCategoryNameIdMap = mtlCategoryList.stream().collect(Collectors.toMap(EmcMtlCategoryBean::getMtlCategoryName, EmcMtlCategoryBean::getId));
        Map<String, String> existedNameIdMap = existedMtlList.stream().collect(Collectors.toMap(EmcMtlBean::getMtlName, EmcMtlBean::getId));

        // 待更新的材料
        List<EmcMtlBean> updateMtlList = new ArrayList<>();
        validMtlList.stream().filter(mtl -> existedMtlNameList.contains(mtl.getMtlName())).forEach(mtl -> {
            EmcMtlBean updateMtl = new EmcMtlBean();
            BeanUtils.copyProperties(mtl, updateMtl);
            updateMtl.setMtlCategoryId(mtlCategoryNameIdMap.get(mtl.getMtlCategoryName()));
            updateMtl.setId(existedNameIdMap.get(mtl.getMtlName()));
            updateMtlList.add(updateMtl);
        });

        // 待新增的材料
        List<EmcMtlDTO> notExistedMtlList = validMtlList.stream().filter(mtl -> !existedMtlNameList.contains(mtl.getMtlName())).collect(Collectors.toList());
        List<String> newMtlIdList = ApplicationContextHelper.getNextSequenceList(emcMtlService.getDao().getTable(), notExistedMtlList.size());

        List<EmcMtlBean> insertMtlList = new ArrayList<>(notExistedMtlList.size());
        List<EmcMtlMaintainBean> mtlMaintainList = new ArrayList<>(notExistedMtlList.size());

        Collections.reverse(notExistedMtlList);

        Map<String, String> mtlManagementTypeMap = mtlCategoryList.stream().collect(Collectors.toMap(EmcMtlCategoryBean::getMtlCategoryName, EmcMtlCategoryBean::getManagementType));

        IntStream.range(0, notExistedMtlList.size()).forEach(index -> {
            EmcMtlBean newMtl = new EmcMtlBean();
            EmcMtlDTO copiedMtl = notExistedMtlList.get(index);
            BeanUtils.copyProperties(copiedMtl, newMtl);
            newMtl.setId(newMtlIdList.get(index));
            newMtl.setMtlCategoryId(mtlCategoryNameIdMap.get(copiedMtl.getMtlCategoryName()));
            insertMtlList.add(newMtl);

            // 一般试剂导入时不添加维护物质
            if (!StringUtils.equals("genericReagent", mtlManagementTypeMap.get(copiedMtl.getMtlCategoryName()))) {
                EmcMtlMaintainBean newMtlMaintain = new EmcMtlMaintainBean();
                newMtlMaintain.setCategory(newMtl.getMtlName());
                newMtlMaintain.setMtlName(newMtl.getMtlName());
                newMtlMaintain.setMtlId(newMtl.getId());
                newMtlMaintain.setId(ApplicationContextHelper.getNextIdentity());
                mtlMaintainList.add(newMtlMaintain);
            }
        });

        if (!insertMtlList.isEmpty()) {
            emcMtlService.getDao().insert(insertMtlList);
            mtlMaintainService.getDao().insert(mtlMaintainList);
        }

        if (!updateMtlList.isEmpty()) {
            emcMtlService.getDao().update(updateMtlList);
        }

        return CoreFileImportResultDTO;
    }
}
