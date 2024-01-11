package com.sunwayworld.emc.module.mdm.user.io;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.io.file.FileHelper;
import com.sunwayworld.framework.io.file.FileRepresentation;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.ImportResultRepresentation;
import com.sunwayworld.module.item.file.function.ImportFileFunction;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;

@Component(value = "emcUserImportFileFunction")
public class EmcUserImportFileFunction implements ImportFileFunction {

    @Autowired
    private CoreUserService coreUserService;

    @Autowired
    private CoreOrgService coreOrgService;

    @Override
    @Transactional
    public ImportResultRepresentation apply(CoreFileBean file) {
        ImportResultRepresentation importResultRepresentation = new ImportResultRepresentation();

        FileRepresentation fr = FileRepresentation.of(file);

        List<CoreUserBean> userList = ExcelHelper.readDataFromExcel(FileHelper.getAbsolutePath(fr).toString(), 1, 2, CoreUserBean.class);

        if (userList.size() > 0) {

            List<CoreOrgBean> orgList = coreOrgService.selectAll();

            List<String> idList = userList.stream().map(e -> e.getId()).collect(Collectors.toList());
            List<CoreUserBean> existUserList = coreUserService.getDao().selectListByOneColumnValues(idList, "ID");

            userList.forEach(e -> {
                orgList.forEach(m -> {
                    if (m.getOrgName().equals(e.getOrgName())) {
                        e.setOrgId(m.getId());
                        e.setExt$Item("parentorgid", m.getParentId());
                    }
                });
            });

            List<CoreUserBean> insertUserList = new ArrayList<>();
            insertUserList.addAll(userList);
            List<CoreUserBean> updateUserList = new ArrayList<>();
            updateUserList.addAll(userList);

            insertUserList.removeIf(u -> existUserList.stream().anyMatch(m -> m.getId().equals(u.getId())));

            updateUserList.removeIf(u -> insertUserList.stream().anyMatch(m -> m.getId().equals(u.getId())));

            if (!insertUserList.isEmpty()) {
                coreUserService.getDao().insert(insertUserList);
            }
            if (!updateUserList.isEmpty()) {
                coreUserService.getDao().update(updateUserList);
            }
        }
        return importResultRepresentation;
    }
}
