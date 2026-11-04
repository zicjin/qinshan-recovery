/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.train.validator;

import com.sunwayworld.cloud.module.train.train.bean.CoreTrainBean;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CoreTrainActivatableValidator
implements DataValidator {
    @Override
    public boolean doValid(Object ... args) {
        RestJsonWrapperBean warpper = (RestJsonWrapperBean)args[0];
        List<CoreTrainBean> trainList = warpper.parse(CoreTrainBean.class);
        boolean flag = true;
        int index = 1;
        for (int i = 0; i < trainList.size(); ++i) {
            boolean subFlag = true;
            StringBuilder validSb = new StringBuilder();
            CoreTrainBean train = trainList.get(i);
            if (StringUtils.isEmpty(train.getTrainTime())) {
                validSb.append(index + ":" + I18nHelper.getMessage("T_CORE_TRAIN_PLAN.THEME", new String[0]) + I18nHelper.getMessage("GIKAM.TRAIN.TIP.CANNOT_BE_EMPTY", new String[0]));
                subFlag = false;
            }
            if (StringUtils.isEmpty(train.getTrainMethod())) {
                if (subFlag) {
                    validSb.append(index + ":" + I18nHelper.getMessage("T_CORE_TRAIN.ASSESSMENTMETHOD", new String[0]) + I18nHelper.getMessage("GIKAM.TRAIN.TIP.CANNOT_BE_EMPTY", new String[0]));
                    subFlag = false;
                } else {
                    validSb.append(";" + I18nHelper.getMessage("T_CORE_TRAIN.ASSESSMENTMETHOD", new String[0]) + I18nHelper.getMessage("GIKAM.TRAIN.TIP.CANNOT_BE_EMPTY", new String[0]));
                }
            }
            if (StringUtils.isEmpty(train.getLocation())) {
                if (subFlag) {
                    validSb.append(index + ":" + I18nHelper.getMessage("T_CORE_TRAIN.LOCATION", new String[0]) + I18nHelper.getMessage("GIKAM.TRAIN.TIP.CANNOT_BE_EMPTY", new String[0]));
                    subFlag = false;
                } else {
                    validSb.append(";" + I18nHelper.getMessage("T_CORE_TRAIN.LOCATION", new String[0]) + I18nHelper.getMessage("GIKAM.TRAIN.TIP.CANNOT_BE_EMPTY", new String[0]));
                }
            }
            if (StringUtils.isEmpty(train.getTrainTime())) {
                if (subFlag) {
                    validSb.append(index + ":" + I18nHelper.getMessage("T_CORE_TRAIN.TRAINTIME", new String[0]) + I18nHelper.getMessage("GIKAM.TRAIN.TIP.CANNOT_BE_EMPTY", new String[0]));
                    subFlag = false;
                } else {
                    validSb.append(";" + I18nHelper.getMessage("T_CORE_TRAIN.TRAINTIME", new String[0]) + I18nHelper.getMessage("GIKAM.TRAIN.TIP.CANNOT_BE_EMPTY", new String[0]));
                }
            }
            if (StringUtils.isEmpty(train.getTrainingMaterial())) {
                if (subFlag) {
                    validSb.append(index + ":" + I18nHelper.getMessage("T_CORE_TRAIN.TRAININGMATERIAL", new String[0]) + I18nHelper.getMessage("GIKAM.TRAIN.TIP.CANNOT_BE_EMPTY", new String[0]));
                    subFlag = false;
                } else {
                    validSb.append(";" + I18nHelper.getMessage("T_CORE_TRAIN.TRAININGMATERIAL", new String[0]) + I18nHelper.getMessage("GIKAM.TRAIN.TIP.CANNOT_BE_EMPTY", new String[0]));
                }
            }
            if (StringUtils.isEmpty(train.getAssessmentMethod())) {
                if (subFlag) {
                    validSb.append(index + ":" + I18nHelper.getMessage("T_CORE_TRAIN.ASSESSMENTMETHOD", new String[0]) + I18nHelper.getMessage("GIKAM.TRAIN.TIP.CANNOT_BE_EMPTY", new String[0]));
                    subFlag = false;
                } else {
                    validSb.append(";" + I18nHelper.getMessage("T_CORE_TRAIN.ASSESSMENTMETHOD", new String[0]) + I18nHelper.getMessage("GIKAM.TRAIN.TIP.CANNOT_BE_EMPTY", new String[0]));
                }
            }
            if (ObjectUtils.isEmpty(train.getExamId())) {
                if (subFlag) {
                    validSb.append(index + ":" + I18nHelper.getMessage("CORE.MODULE.TRAIN.TRAIN.TITLE.EXAM_NAME", new String[0]) + I18nHelper.getMessage("GIKAM.TRAIN.TIP.CANNOT_BE_EMPTY", new String[0]));
                    subFlag = false;
                } else {
                    validSb.append(";" + I18nHelper.getMessage("CORE.MODULE.TRAIN.TRAIN.TITLE.EXAM_NAME", new String[0]) + I18nHelper.getMessage("GIKAM.TRAIN.TIP.CANNOT_BE_EMPTY", new String[0]));
                }
            }
            if (!subFlag) {
                flag = subFlag;
                this.addConstraintViolation(validSb.toString());
            }
            ++index;
        }
        return flag;
    }
}
