/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.workspacescheme.bean;

import com.sunwayworld.cloud.module.item.workspacepanel.bean.CoreWorkspacePanelBean;
import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceSchemeBean;
import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceSchemePanelConfigBean;
import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CoreWorkspaceSchemePanelConfigRepresentation
implements Serializable {
    @Transient
    private static final long serialVersionUID = -3840047831936825096L;
    private List<CoreWorkspaceSchemeBean> schemeList = new ArrayList<CoreWorkspaceSchemeBean>();
    private Long schemeId;
    private List<CoreWorkspacePanelBean> systemPanelList = new ArrayList<CoreWorkspacePanelBean>();
    private List<CoreWorkspaceSchemePanelConfigBean> panelList = new ArrayList<CoreWorkspaceSchemePanelConfigBean>();

    public List<CoreWorkspacePanelBean> getSystemPanelList() {
        return this.systemPanelList;
    }

    public List<CoreWorkspaceSchemeBean> getSchemeList() {
        return this.schemeList;
    }

    public void setSchemeList(List<CoreWorkspaceSchemeBean> schemeList) {
        this.schemeList = schemeList;
    }

    public Long getSchemeId() {
        return this.schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public void setSystemPanelList(List<CoreWorkspacePanelBean> systemPanelList) {
        this.systemPanelList = systemPanelList;
    }

    public List<CoreWorkspaceSchemePanelConfigBean> getPanelList() {
        return this.panelList;
    }

    public void setPanelList(List<CoreWorkspaceSchemePanelConfigBean> panelList) {
        this.panelList = panelList;
    }
}
