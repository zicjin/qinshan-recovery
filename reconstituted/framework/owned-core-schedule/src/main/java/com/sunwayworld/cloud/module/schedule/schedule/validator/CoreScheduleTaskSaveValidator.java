/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.schedule.validator;

import com.sunwayworld.cloud.module.schedule.bean.CoreScheduleTaskBean;
import com.sunwayworld.framework.jdk.tools.JavaCompilerManager;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.SpringUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

@Repository
public class CoreScheduleTaskSaveValidator
implements DataValidator {
    private static final Logger logger = LogManager.getLogger(CoreScheduleTaskSaveValidator.class);

    @Override
    public boolean doValid(Object ... args) {
        RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];
        CoreScheduleTaskBean task = wrapper.parseUnique(CoreScheduleTaskBean.class);
        String script = task.getTaskScript();
        if (!StringUtils.isEmpty(script)) {
            try {
                script = (String)SpringUtils.invoke("lcdpResourceServiceImpl", "replaceScriptContent", script);
                JavaCompilerManager.compile(script);
            }
            catch (Exception ex) {
                logger.error(ex.getMessage(), (Throwable)ex);
                this.addConstraintViolation(ex.getMessage());
                return false;
            }
        }
        return true;
    }
}
