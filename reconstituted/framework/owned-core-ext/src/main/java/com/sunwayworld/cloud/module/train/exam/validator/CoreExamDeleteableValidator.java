/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.exam.validator;

import com.sunwayworld.cloud.module.train.train.bean.CoreTrainBean;
import com.sunwayworld.cloud.module.train.train.service.CoreTrainService;
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
public class CoreExamDeleteableValidator
implements DataValidator {
    @Autowired
    private CoreTrainService coreTrainService;

    @Override
    public boolean doValid(Object ... args) {
        RestJsonWrapperBean warpper = (RestJsonWrapperBean)args[0];
        List<Long> examIdList = warpper.parseId(Long.class);
        List<CoreTrainBean> trainList = this.coreTrainService.selectListByFilter(SearchFilter.instance().match("EXAMID", examIdList).filter(MatchPattern.OR), new Order[0]);
        if (!trainList.isEmpty()) {
            trainList.forEach(e -> this.addConstraintViolation(e.getExt$Item("theme") + ":" + I18nHelper.getMessage("GIKAM.TRAIN.TIP.QUOTED", new String[0]) + e.getExt$Item("examname")));
            return false;
        }
        return true;
    }
}
