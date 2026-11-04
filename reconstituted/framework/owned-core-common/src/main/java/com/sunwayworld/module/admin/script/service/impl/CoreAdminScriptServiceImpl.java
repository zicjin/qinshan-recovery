/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.script.service.impl;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.script.BshExecutor;
import com.sunwayworld.framework.script.BshExecutorCache;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.module.admin.script.bean.CoreAdminScriptResultDTO;
import com.sunwayworld.module.admin.script.service.CoreAdminScriptService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreAdminScriptServiceImpl
implements CoreAdminScriptService {
    private static final Logger logger = LogManager.getLogger(CoreAdminScriptService.class);

    @Override
    public CoreAdminScriptResultDTO execute(RestJsonWrapperBean wrapper) {
        if (!LocalContextHelper.isAdminLogin()) {
            throw new ApplicationRuntimeException("GIKAM.PERMISSION.UNAUTHORIZED", new String[0]);
        }
        String script = wrapper.getParamValue("script");
        logger.info(LocalContextHelper.getLoginUserId() + "--" + script);
        CoreAdminScriptResultDTO result = new CoreAdminScriptResultDTO();
        BshExecutor bshExecutor = BshExecutorCache.INSTANCE.take();
        try {
            Object value = bshExecutor.eval(script);
            result.setCode(200);
            if (value != null) {
                result.setContent(JSON.toJSONString(value));
            }
        }
        catch (Exception ex) {
            logger.warn(ex.getMessage(), (Throwable)ex);
            result.setCode(400);
            result.setContent(ObjectUtils.getStackTrace(ex));
        }
        return result;
    }
}
