/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filemanage.validator;

import com.sunwayworld.cloud.module.qms.filemanage.service.CoreFileManageAuthService;
import com.sunwayworld.cloud.module.qms.filemanage.service.CoreFileManageVersionService;
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
public class CoreFileManageDeleteValidator
implements DataValidator {
    @Autowired
    private CoreFileManageVersionService coreFileManageVersionService;
    @Autowired
    private CoreFileManageAuthService coreFileManageAuthService;

    @Override
    public boolean doValid(Object ... args) {
        RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];
        List<Long> idList = wrapper.parseId(Long.class);
        List CoreFileManageVersionList = this.coreFileManageVersionService.selectListByFilter(SearchFilter.instance().match("FILEMANAGEID", idList).filter(MatchPattern.OR), new Order[0]);
        if (!CoreFileManageVersionList.isEmpty()) {
            this.addConstraintViolation(I18nHelper.getMessage("CORE.MODULE.QMS.FILE_MANAGE.CONTAIN_VERSION_CAN_NOT_DELETE", new String[0]));
            return false;
        }
        List coreFileManageAuthList = this.coreFileManageAuthService.selectListByFilter(SearchFilter.instance().match("FILEMANAGEID", idList).filter(MatchPattern.OR), new Order[0]);
        if (!coreFileManageAuthList.isEmpty()) {
            this.addConstraintViolation(I18nHelper.getMessage("CORE.MODULE.QMS.FILE_MANAGE.CONTAIN_AUTH_CAN_NOT_DELETE", new String[0]));
            return false;
        }
        return true;
    }
}
