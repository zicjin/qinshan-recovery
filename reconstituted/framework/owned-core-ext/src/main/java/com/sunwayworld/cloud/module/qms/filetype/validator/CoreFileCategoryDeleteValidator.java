/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filetype.validator;

import com.sunwayworld.cloud.module.qms.filemanage.service.CoreFileManageService;
import com.sunwayworld.cloud.module.qms.filetype.service.CoreFileCategoryRoleAuthService;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreFileCategoryDeleteValidator
implements DataValidator {
    @Autowired
    private CoreFileCategoryRoleAuthService coreFileCategoryRoleAuthService;
    @Autowired
    private CoreFileManageService coreFileManageService;

    @Override
    public boolean doValid(Object ... args) {
        RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[1];
        List<Long> idList = wrapper.parseId(Long.class);
        List coreFileCategoryList = this.coreFileCategoryRoleAuthService.selectListByFilter(SearchFilter.instance().match("FILECATTEGORYID", idList).filter(MatchPattern.OR), new Order[0]);
        if (!coreFileCategoryList.isEmpty()) {
            this.addConstraintViolation(I18nHelper.getMessage("QMS.FILE.FILETYPES.DELETE.ALERT.FILECATEGORY_CONTAIN_ROLEAUTH_CANNOT_DELETE", new String[0]));
            return false;
        }
        List fileManageCategoryList = this.coreFileManageService.selectListByFilter(SearchFilter.instance().match("FILECATEGORYID", idList).filter(MatchPattern.OR), new Order[0]);
        if (!fileManageCategoryList.isEmpty()) {
            this.addConstraintViolation(I18nHelper.getMessage("QMS.FILE.FILETYPES.DELETE.ALERT.FILECATEGORY_BE_QUOTED_CANNOT_DELETE", new String[0]));
            return false;
        }
        return true;
    }
}
