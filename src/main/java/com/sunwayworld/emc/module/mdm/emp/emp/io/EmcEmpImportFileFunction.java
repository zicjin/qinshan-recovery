package com.sunwayworld.emc.module.mdm.emp.emp.io;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpBean;
import com.sunwayworld.emc.module.mdm.emp.emp.service.EmcEmpService;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.item.file.function.CoreFileImportFunction;

@Component(value = "emcEmpImportFileFunction")
public class EmcEmpImportFileFunction implements CoreFileImportFunction {

    @Autowired
    private EmcEmpService emcEmpService;

    @Override
    @Transactional
    public CoreFileImportResultDTO apply(CoreFileBean file) {
        CoreFileImportResultDTO fileImportResultDTO = new CoreFileImportResultDTO();

        List<EmcEmpBean> empList = ExcelHelper.readDataFromExcel(CoreFileUtils.getLocalPath(file).toString(), 1, 2, EmcEmpBean.class);

        if (empList.size() > 0) {

            List<String> idList = empList.stream().map(e -> e.getId()).collect(Collectors.toList());
            List<EmcEmpBean> existEmpList = emcEmpService.getDao().selectListByOneColumnValues(idList, "ID");

            List<EmcEmpBean> insertEmpList = new ArrayList<>();
            insertEmpList.addAll(empList);
            List<EmcEmpBean> updateEmpList = new ArrayList<>();
            updateEmpList.addAll(empList);

            insertEmpList.removeIf(u -> existEmpList.stream().anyMatch(m -> m.getId().equals(u.getId())));

            updateEmpList.removeIf(u -> insertEmpList.stream().anyMatch(m -> m.getId().equals(u.getId())));

            if (!insertEmpList.isEmpty()) {
                emcEmpService.getDao().insert(insertEmpList);
            }
            if (!updateEmpList.isEmpty()) {
                emcEmpService.getDao().update(updateEmpList);
            }
        }
        return fileImportResultDTO;
    }
}
