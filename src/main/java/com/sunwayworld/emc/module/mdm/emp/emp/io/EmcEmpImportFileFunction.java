package com.sunwayworld.emc.module.mdm.emp.emp.io;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpBean;
import com.sunwayworld.emc.module.mdm.emp.emp.service.EmcEmpService;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.io.file.FileHelper;
import com.sunwayworld.framework.io.file.FileRepresentation;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.ImportResultRepresentation;
import com.sunwayworld.module.item.file.function.ImportFileFunction;

@Component(value = "emcEmpImportFileFunction")
public class EmcEmpImportFileFunction implements ImportFileFunction {

    @Autowired
    private EmcEmpService emcEmpService;

    @Override
    @Transactional
    public ImportResultRepresentation apply(CoreFileBean file) {
        ImportResultRepresentation importResultRepresentation = new ImportResultRepresentation();

        FileRepresentation fr = FileRepresentation.of(file);

        List<EmcEmpBean> empList = ExcelHelper.readDataFromExcel(FileHelper.getAbsolutePath(fr).toString(), 1, 2, EmcEmpBean.class);

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
        return importResultRepresentation;
    }
}
