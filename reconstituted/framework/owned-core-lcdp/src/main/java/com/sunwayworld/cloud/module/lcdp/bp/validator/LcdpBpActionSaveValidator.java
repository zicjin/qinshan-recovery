/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.validator;

import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessNodeActionBean;
import com.sunwayworld.framework.jdk.tools.JavaCompilerManager;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

@Repository
public class LcdpBpActionSaveValidator
implements DataValidator {
    private static final Logger logger = LogManager.getLogger(LcdpBpActionSaveValidator.class);

    @Override
    public boolean doValid(Object ... args) {
        String checkScriptJava;
        RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];
        List<LcdpBizProcessNodeActionBean> bpNodeActionList = wrapper.parse(LcdpBizProcessNodeActionBean.class);
        if (bpNodeActionList.isEmpty()) {
            return true;
        }
        String scriptType = wrapper.getParamValue("scriptType");
        LcdpBizProcessNodeActionBean bpAction = wrapper.parseUnique(LcdpBizProcessNodeActionBean.class);
        if ("action".equals(scriptType)) {
            String actionScript = bpAction.getActionScript();
            if (!StringUtils.isEmpty(actionScript)) {
                try {
                    JavaCompilerManager.compile(actionScript);
                }
                catch (Exception ex) {
                    logger.error(ex.getMessage(), (Throwable)ex);
                    this.addConstraintViolation(ex.getMessage());
                    return false;
                }
            }
        } else if ("checkJava".equals(scriptType) && !StringUtils.isEmpty(checkScriptJava = bpAction.getCheckScriptJava())) {
            try {
                JavaCompilerManager.compile(checkScriptJava);
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
