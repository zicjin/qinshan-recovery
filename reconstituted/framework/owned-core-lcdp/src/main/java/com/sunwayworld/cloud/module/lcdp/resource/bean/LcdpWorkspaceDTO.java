/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import java.util.List;

public class LcdpWorkspaceDTO
extends AbstractBaseData {
    @Transient
    private static final long serialVersionUID = -3761256809560894048L;
    private List<String> moduleIdList;
    private List<String> pageAndScriptIdList;

    public List<String> getModuleIdList() {
        return this.moduleIdList;
    }

    public void setModuleIdList(List<String> moduleIdList) {
        this.moduleIdList = moduleIdList;
    }

    public List<String> getPageAndScriptIdList() {
        return this.pageAndScriptIdList;
    }

    public void setPageAndScriptIdList(List<String> pageAndScriptIdList) {
        this.pageAndScriptIdList = pageAndScriptIdList;
    }
}
