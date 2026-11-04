/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filevolume.validator;

import com.sunwayworld.cloud.module.qms.filetype.service.CoreFileCategoryService;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreFileVolumeDeleteValidator
implements DataValidator {
    @Autowired
    private CoreFileCategoryService coreFileCategoryService;

    @Override
    public boolean doValid(Object ... args) {
        RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];
        List<Long> idList = wrapper.parseId(Long.class);
        List coreFileCategoryList = this.coreFileCategoryService.selectListByIds(idList);
        if (!coreFileCategoryList.isEmpty()) {
            this.addConstraintViolation(I18nHelper.getMessage("QMS.FILE.FILEVOLUME.DELETE.ALERT.FILEVOLUME_BE_QUOTED_CANNOT_DELETE", new String[0]));
            return false;
        }
        return true;
    }
}
