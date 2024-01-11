package com.sunwayworld.emc.module.bp.project.projscheme.validator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhengxy@sunwayworld.com 2022年05月18日
 */
@Repository
public class PfDeleteFolderValidator implements DataValidator {

    @Autowired
    private EmcOrderTaskService orderTaskService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcFolderBean> folderList = wrapper.parse(EmcFolderBean.class);

        boolean hasDone = folderList.stream().anyMatch(folder -> EmcBizConstant.FOLDER_PROCESSSTATUS_DONE.equals(folder.getStatus()));
        if (hasDone) {
            this.addConstraintViolation("EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TIP.CANNOT_DELETE_FOLDER_FROM_REPORT");
            return false;
        }

        List<Long> folderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(folderIdList, "folderId", Arrays.asList("id", "runId"));

        List<String> runIdList = orderTaskList.stream().filter(task -> !StringUtils.isEmpty(task.getRunId())).map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());
        if (!runIdList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TIP.CANNOT_DELETE_FOLDER_WITH_RUN");
            return false;
        }

        return true;
    }
}
