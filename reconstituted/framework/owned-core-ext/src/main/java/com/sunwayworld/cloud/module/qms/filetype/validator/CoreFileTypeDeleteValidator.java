/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filetype.validator;

import com.sunwayworld.cloud.module.qms.filetype.service.CoreFileCategoryService;
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
public class CoreFileTypeDeleteValidator
implements DataValidator {
    @Autowired
    private CoreFileCategoryService coreFileCategoryService;

    @Override
    public boolean doValid(Object ... args) {
        RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];
        List<Long> idList = wrapper.parseId(Long.class);
        List coreFileCategoryList = this.coreFileCategoryService.selectListByFilter(SearchFilter.instance().match("FILETYPEID", idList).filter(MatchPattern.OR), new Order[0]);
        if (!coreFileCategoryList.isEmpty()) {
            this.addConstraintViolation(I18nHelper.getMessage("QMS.FILE.FILETYPES.DELETE.ALERT.FILETYPE_CONTAIN_CATEGORY_CANNOT_DELETE", new String[0]));
            return false;
        }
        return true;
    }
}
