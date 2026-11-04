/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.config.helper;

import com.sunwayworld.module.sys.config.bean.CoreLoginConfigBean;
import com.sunwayworld.module.sys.config.bean.CoreSystemConfigDTO;
import com.sunwayworld.module.sys.config.service.CoreLoginConfigService;
import com.sunwayworld.module.sys.config.service.CoreSystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SystemConfigHelper {
    private static CoreSystemConfigService systemConfigService;
    private static CoreLoginConfigService loginConfigService;

    @Autowired
    public void setCoreSystemConfigService(CoreSystemConfigService systemConfigService) {
        SystemConfigHelper.systemConfigService = systemConfigService;
    }

    public static CoreSystemConfigDTO getSystemConfig() {
        return systemConfigService.selectSystemConfig();
    }

    @Autowired
    public void setCoreLoginConfigService(CoreLoginConfigService loginConfigService) {
        SystemConfigHelper.loginConfigService = loginConfigService;
    }

    public static CoreLoginConfigBean getLoginConfig() {
        return loginConfigService.selectLoginConfig();
    }
}
