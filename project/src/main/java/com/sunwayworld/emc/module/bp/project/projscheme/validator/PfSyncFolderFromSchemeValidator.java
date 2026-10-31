package com.sunwayworld.emc.module.bp.project.projscheme.validator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjSchemeBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhengxy@sunwayworld.com 2022年05月18日
 */
@Repository
public class PfSyncFolderFromSchemeValidator implements DataValidator {

    @Autowired
    private EmcFolderService folderService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcProjSchemeBean> schemeList = wrapper.parse(EmcProjSchemeBean.class);

        if (schemeList.isEmpty()) {
            return true;
        }

        Long projectId = schemeList.get(0).getProjId();
        List<EmcFolderBean> folderList = folderService.getDao().selectListByOneColumnValue(projectId, "projId", Arrays.asList("id", "folderNo"));
        List<String> folderNoList = folderList.stream().map(EmcFolderBean::getFolderNo).collect(Collectors.toList());

        List<String> newFolderNoList = schemeList.stream().map(EmcProjSchemeBean::getFolderNo).collect(Collectors.toList());
        List<String> existedFolderNoList = newFolderNoList.stream().filter(folderNoList::contains).collect(Collectors.toList());

        if (!existedFolderNoList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TIP.CANNOT_SYNC_EXISTED_FOLDER");
            return false;
        }

        return true;
    }
}
