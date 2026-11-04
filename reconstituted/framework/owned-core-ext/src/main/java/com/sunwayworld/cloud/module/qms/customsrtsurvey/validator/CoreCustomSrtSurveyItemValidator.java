/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.customsrtsurvey.validator;

import com.sunwayworld.cloud.module.qms.customsrtsurvey.bean.CoreCustomSrtSurveyItemBean;
import com.sunwayworld.cloud.module.qms.customsrtsurvey.service.CoreCustomSrtSurveyItemService;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class CoreCustomSrtSurveyItemValidator
implements DataValidator {
    @Autowired
    @Lazy
    private CoreCustomSrtSurveyItemService customSrtSurveyItemService;

    @Override
    public boolean doValid(Object ... args) {
        RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[1];
        CoreCustomSrtSurveyItemBean coreCustomSrtSurveyItem = wrapper.parseUnique(CoreCustomSrtSurveyItemBean.class);
        String surveyContent = coreCustomSrtSurveyItem.getSurveyContent();
        List customSrtSurveyItemList = this.customSrtSurveyItemService.selectListByFilter(SearchFilter.instance().match("SURVEYCONTENT", surveyContent).filter(MatchPattern.EQ), new Order[0]);
        if (!customSrtSurveyItemList.isEmpty()) {
            this.addConstraintViolation(I18nHelper.getMessage("CUSTOMSRTSURVEYS.MSG.CONTENT_CONTAINS_REIMPORT_PLEASE", new String[0]));
            return false;
        }
        return true;
    }
}
