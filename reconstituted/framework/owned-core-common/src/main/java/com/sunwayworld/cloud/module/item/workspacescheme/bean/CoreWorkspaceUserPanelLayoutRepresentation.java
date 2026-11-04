/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.workspacescheme.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CoreWorkspaceUserPanelLayoutRepresentation
implements Serializable {
    @Transient
    private static final long serialVersionUID = 3062171462255928811L;
    private List<Panel> panelList = new ArrayList<Panel>();

    public List<Panel> getPanelList() {
        return this.panelList;
    }

    public void addPanel(String title, String url, String config, String titleDisplay, String refreshFlag) {
        this.panelList.add(new Panel(title, url, config, titleDisplay, refreshFlag));
    }

    private class Panel
    implements Serializable {
        private static final long serialVersionUID = 1200440856022362442L;
        private String title;
        private String url;
        private String config;
        private String titleDisplay;
        private String refreshFlag;

        public Panel(String title, String url, String config, String titleDisplay, String refreshFlag) {
            this.title = title;
            this.url = url;
            this.config = config;
            this.titleDisplay = titleDisplay;
            this.refreshFlag = refreshFlag;
        }

        public String getTitle() {
            return this.title;
        }

        public String getUrl() {
            return this.url;
        }

        public String getConfig() {
            return this.config;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setConfig(String config) {
            this.config = config;
        }

        public String getTitleDisplay() {
            return this.titleDisplay;
        }

        public void setTitleDisplay(String titleDisplay) {
            this.titleDisplay = titleDisplay;
        }

        public String getRefreshFlag() {
            return this.refreshFlag;
        }

        public void setRefreshFlag(String refreshFlag) {
            this.refreshFlag = refreshFlag;
        }
    }
}
