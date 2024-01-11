package com.sunwayworld.emc.module.bp.project.projscheme.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjOrderBean;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjSchemeBean;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjOrderService;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhengxy@sunwayworld.com 2022年05月18日
 */
@Repository
public class PfSyncNewTestValidator implements DataValidator {

    @Autowired
    private EmcProjOrderService projOrderService;

    @Autowired
    private EmcFolderService folderService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcProjSchemeBean> schemeList = wrapper.parse(EmcProjSchemeBean.class);

        List<EmcProjSchemeBean> newSchemeList = schemeList.stream().filter(scheme -> StringUtils.equals(scheme.getExt$Item("status"), Constant.YES)).collect(Collectors.toList());
        if (!newSchemeList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TIP.PLEASE_USE_SYNC_NEW_FOLDER_BTN");
            return false;
        }

        List<Long> schemeIdList = schemeList.stream().map(EmcProjSchemeBean::getId).collect(Collectors.toList());
        List<EmcProjOrderBean> schemeOrderList = projOrderService.selectListByFilter(SearchFilter.instance().match("projSchemeId", schemeIdList).filter(MatchPattern.OR));
        List<EmcProjOrderBean> newSchemeOrderList = schemeOrderList.stream().filter(test -> StringUtils.equals(test.getExt$Item("status"), Constant.YES)).collect(Collectors.toList());

        if (newSchemeOrderList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TIP.CANNOT_SYNC_NEW_TEST");
            return false;
        }

        List<Long> curSchemeIdList = newSchemeOrderList.stream().map(EmcProjOrderBean::getProjSchemeId).distinct().collect(Collectors.toList());
        List<EmcProjSchemeBean> curSchemeList = schemeList.stream().filter(scheme -> curSchemeIdList.contains(scheme.getId())).collect(Collectors.toList());
        List<String> folderNoList = curSchemeList.stream().map(EmcProjSchemeBean::getFolderNo).collect(Collectors.toList());

        List<EmcFolderBean> folderList = folderService.getDao().selectListByOneColumnValues(folderNoList, "folderNo");
        boolean folderDone = folderList.stream().anyMatch(folder -> StringUtils.equals(EmcBizConstant.FOLDER_PROCESSSTATUS_DONE, folder.getStatus()));

        if (folderDone) {
            this.addConstraintViolation("EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TIP.CANNOT_SYNC_NEW_TEST_FROM_REPORT");
            return false;
        }

        return true;
    }
}
