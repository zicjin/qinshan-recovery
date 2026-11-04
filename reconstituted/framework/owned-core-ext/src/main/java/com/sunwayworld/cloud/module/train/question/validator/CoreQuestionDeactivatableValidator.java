/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.question.validator;

import com.sunwayworld.cloud.module.train.exam.bean.CoreExamQuestionBean;
import com.sunwayworld.cloud.module.train.exam.service.CoreExamQuestionService;
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
public class CoreQuestionDeactivatableValidator
implements DataValidator {
    @Autowired
    private CoreExamQuestionService coreExamQuestionService;

    @Override
    public boolean doValid(Object ... args) {
        RestJsonWrapperBean warpper = (RestJsonWrapperBean)args[0];
        List<Long> questionIdList = warpper.parseId(Long.class);
        List<CoreExamQuestionBean> examQuestionList = this.coreExamQuestionService.selectListByFilter(SearchFilter.instance().match("QUESTIONID", questionIdList).filter(MatchPattern.OR), new Order[0]);
        if (!examQuestionList.isEmpty()) {
            examQuestionList.forEach(e -> this.addConstraintViolation(e.getExt$Item("questioncontent") + ":" + I18nHelper.getMessage("CORE.MODULE.TRAIN.QUESTION.TIP.QUESTION_ALREADY_BE_USED", new String[0])));
            return false;
        }
        return true;
    }
}
