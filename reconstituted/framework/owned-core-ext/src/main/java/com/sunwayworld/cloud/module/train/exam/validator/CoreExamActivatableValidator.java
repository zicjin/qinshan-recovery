/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.exam.validator;

import com.sunwayworld.cloud.module.train.exam.bean.CoreExamBean;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CoreExamActivatableValidator
implements DataValidator {
    @Override
    public boolean doValid(Object ... args) {
        RestJsonWrapperBean warpper = (RestJsonWrapperBean)args[0];
        List<CoreExamBean> examList = warpper.parse(CoreExamBean.class);
        boolean flag = true;
        for (int i = 0; i < examList.size(); ++i) {
            boolean subFlag = true;
            StringBuilder validSb = new StringBuilder();
            CoreExamBean exam = examList.get(i);
            if (ObjectUtils.isEmpty(exam.getExamBeginTime())) {
                validSb.append(exam.getExamName() + ":" + I18nHelper.getMessage("T_CORE_EXAM.EXAMBEGINTIME", new String[0]) + I18nHelper.getMessage("GIKAM.TRAIN.TIP.CANNOT_BE_EMPTY", new String[0]));
                subFlag = false;
            }
            if (ObjectUtils.isEmpty(exam.getExamEndTime())) {
                if (subFlag) {
                    validSb.append(exam.getExamName() + ":" + I18nHelper.getMessage("T_CORE_EXAM.EXAMENDTIME", new String[0]) + I18nHelper.getMessage("GIKAM.TRAIN.TIP.CANNOT_BE_EMPTY", new String[0]));
                    subFlag = false;
                } else {
                    validSb.append(";" + I18nHelper.getMessage("T_CORE_EXAM.EXAMENDTIME", new String[0]) + I18nHelper.getMessage("GIKAM.TRAIN.TIP.CANNOT_BE_EMPTY", new String[0]));
                }
            }
            if (StringUtils.isEmpty(exam.getExamCategory())) {
                if (subFlag) {
                    validSb.append(exam.getExamName() + ":" + I18nHelper.getMessage("T_CORE_EXAM.EXAMCATEGORY", new String[0]) + I18nHelper.getMessage("GIKAM.TRAIN.TIP.CANNOT_BE_EMPTY", new String[0]));
                    subFlag = false;
                } else {
                    validSb.append(";" + I18nHelper.getMessage("T_CORE_EXAM.EXAMCATEGORY", new String[0]) + I18nHelper.getMessage("GIKAM.TRAIN.TIP.CANNOT_BE_EMPTY", new String[0]));
                }
            }
            if (exam.getExamDuration() == 0L) {
                if (subFlag) {
                    validSb.append(exam.getExamName() + ":" + I18nHelper.getMessage("T_CORE_EXAM.EXAMDURATION", new String[0]) + I18nHelper.getMessage("GIKAM.TRAIN.TIP.CANNOT_BE_EMPTY", new String[0]));
                    subFlag = false;
                } else {
                    validSb.append(";" + I18nHelper.getMessage("T_CORE_EXAM.EXAMDURATION", new String[0]) + "\u4e0d\u80fd\u7b49\u4e8e0");
                }
            }
            if (DateTimeUtils.isBefore(exam.getExamEndTime(), exam.getExamBeginTime())) {
                if (subFlag) {
                    validSb.append(exam.getExamName() + ":\u8003\u8bd5\u7ed3\u675f\u65f6\u95f4\u4e0d\u80fd\u65e9\u4e8e\u8003\u8bd5\u7ed3\u675f\u65f6\u95f4");
                    subFlag = false;
                } else {
                    validSb.append(";\u8003\u8bd5\u7ed3\u675f\u65f6\u95f4\u4e0d\u80fd\u65e9\u4e8e\u8003\u8bd5\u7ed3\u675f\u65f6\u95f4");
                }
            }
            if (subFlag) continue;
            flag = subFlag;
            this.addConstraintViolation(validSb.toString());
        }
        return flag;
    }
}
