package com.sunwayworld.emc.module.bp.project.projscheme.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjSchemeBean;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhengxy@sunwayworld.com 2022年05月18日
 */
@Repository
public class PfSyncNewFolderValidator implements DataValidator {

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcProjSchemeBean> schemeList = wrapper.parse(EmcProjSchemeBean.class);

        List<EmcProjSchemeBean> newSchemeList = schemeList.stream().filter(scheme -> StringUtils.equals(scheme.getExt$Item("status"), Constant.YES)).collect(Collectors.toList());
        if (newSchemeList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TIP.CANNOT_SYNC_NEW_FOLDER");
            return false;
        }

        return true;
    }
}
