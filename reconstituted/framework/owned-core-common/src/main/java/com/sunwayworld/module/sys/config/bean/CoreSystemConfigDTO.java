/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.config.bean;

import com.sunwayworld.module.sys.config.bean.CoreSystemConfigBean;
import com.sunwayworld.module.sys.config.bean.CoreSystemConfigHelpManualDTO;

public class CoreSystemConfigDTO
extends CoreSystemConfigBean {
    private static final long serialVersionUID = -5013669817403024977L;
    private CoreSystemConfigHelpManualDTO helpManual;

    public CoreSystemConfigHelpManualDTO getHelpManual() {
        return this.helpManual;
    }

    public void setHelpManual(CoreSystemConfigHelpManualDTO helpManual) {
        this.helpManual = helpManual;
    }
}
