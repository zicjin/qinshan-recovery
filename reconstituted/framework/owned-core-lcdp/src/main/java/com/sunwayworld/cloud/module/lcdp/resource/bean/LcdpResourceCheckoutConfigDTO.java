/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractBaseData;

public class LcdpResourceCheckoutConfigDTO
extends AbstractBaseData {
    @Transient
    private static final long serialVersionUID = 5994051573862896104L;
    private String exportEnvironmentDBType;
    private String exportSysClientJsFlag;

    public String getExportEnvironmentDBType() {
        return this.exportEnvironmentDBType;
    }

    public void setExportEnvironmentDBType(String exportEnvironmentDBType) {
        this.exportEnvironmentDBType = exportEnvironmentDBType;
    }

    public String getExportSysClientJsFlag() {
        return this.exportSysClientJsFlag;
    }

    public void setExportSysClientJsFlag(String exportSysClientJsFlag) {
        this.exportSysClientJsFlag = exportSysClientJsFlag;
    }
}
