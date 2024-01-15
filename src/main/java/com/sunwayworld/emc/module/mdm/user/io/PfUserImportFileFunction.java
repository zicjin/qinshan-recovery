package com.sunwayworld.emc.module.mdm.user.io;

import java.util.List;
import java.util.stream.Collectors;

import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.item.file.function.CoreFileImportFunction;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;

@Component(value = "pfUserImportFileFunction")
public class PfUserImportFileFunction implements CoreFileImportFunction {

    @Autowired
    private CoreUserService coreUserService;

    @Autowired
    private CoreOrgService coreOrgService;

    @Override
    @Transactional
    public CoreFileImportResultDTO apply(CoreFileBean file) {
        CoreFileImportResultDTO fileImportResultDTO = new CoreFileImportResultDTO();

        FilePathDTO fr = CoreFileUtils.toFilePath(file);
        List<CoreUserBean> userList = ExcelHelper.readDataFromExcel(FilePathManager.getLocalPath(fr).toString(), 1, 2, CoreUserBean.class);

        if (userList.isEmpty()) {
            return fileImportResultDTO;
        }

        List<String> userIdList = userList.stream().map(CoreUserBean::getId).distinct().collect(Collectors.toList());
        List<CoreUserBean> existedUserList = coreUserService.selectListByIds(userIdList);
        List<String> existedUserIdList = existedUserList.stream().map(CoreUserBean::getId).collect(Collectors.toList());

        List<String> orgIdList = userList.stream().map(CoreUserBean::getOrgId).distinct().collect(Collectors.toList());
        List<CoreOrgBean> orgList = coreOrgService.selectListByIds(orgIdList);

        userList.forEach(user -> orgList.stream().filter(org -> StringUtils.equals(org.getId(), user.getOrgId())).findFirst().ifPresent(org -> {
            user.setOrgName(org.getOrgName());
            user.setExt$Item("parentOrgId", org.getParentId());
        }));

        List<CoreUserBean> updateUserList = userList.stream().filter(user -> existedUserIdList.contains(user.getId())).collect(Collectors.toList());
        List<CoreUserBean> insertUserList = userList.stream().filter(user -> !existedUserIdList.contains(user.getId())).collect(Collectors.toList());

        if (!insertUserList.isEmpty()) {
            coreUserService.getDao().insert(insertUserList);
        }
        if (!updateUserList.isEmpty()) {
            coreUserService.getDao().update(updateUserList);
        }

        return fileImportResultDTO;
    }
}
