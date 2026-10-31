package com.sunwayworld.emc.module.erp.bean;

import java.util.List;

import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcFolderChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcOrderChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcOrderItemGroupChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjOrderChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjSchemeChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjectChangeBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjOrderBean;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjSchemeBean;

/**
 * @author zhengxy@sunwayworld.com 2023年10月24日
 */
public class ErpProjectCompareVO {

    private EmcProjectBean project;
    private EmcProjectChangeBean projectChange;
    private List<EmcProjSchemeBean> projSchemeList;
    private List<EmcProjSchemeChangeBean> projSchemeChangeList;
    private List<EmcProjOrderBean> projOrderList;
    private List<EmcProjOrderChangeBean> projOrderChangeList;
    private List<EmcFolderBean> folderList;
    private List<EmcFolderChangeBean> folderChangeList;
    private List<EmcOrderBean> orderList;
    private List<EmcOrderChangeBean> orderChangeList;
    private List<EmcOrderItemGroupBean> orderItemGroupList;
    private List<EmcOrderItemGroupChangeBean> orderItemGroupChangeList;

    public EmcProjectBean getProject() {
        return project;
    }

    public void setProject(EmcProjectBean project) {
        this.project = project;
    }

    public EmcProjectChangeBean getProjectChange() {
        return projectChange;
    }

    public void setProjectChange(EmcProjectChangeBean projectChange) {
        this.projectChange = projectChange;
    }

    public List<EmcProjSchemeBean> getProjSchemeList() {
        return projSchemeList;
    }

    public void setProjSchemeList(List<EmcProjSchemeBean> projSchemeList) {
        this.projSchemeList = projSchemeList;
    }

    public List<EmcProjSchemeChangeBean> getProjSchemeChangeList() {
        return projSchemeChangeList;
    }

    public void setProjSchemeChangeList(List<EmcProjSchemeChangeBean> projSchemeChangeList) {
        this.projSchemeChangeList = projSchemeChangeList;
    }

    public List<EmcProjOrderBean> getProjOrderList() {
        return projOrderList;
    }

    public void setProjOrderList(List<EmcProjOrderBean> projOrderList) {
        this.projOrderList = projOrderList;
    }

    public List<EmcProjOrderChangeBean> getProjOrderChangeList() {
        return projOrderChangeList;
    }

    public void setProjOrderChangeList(List<EmcProjOrderChangeBean> projOrderChangeList) {
        this.projOrderChangeList = projOrderChangeList;
    }

    public List<EmcFolderBean> getFolderList() {
        return folderList;
    }

    public void setFolderList(List<EmcFolderBean> folderList) {
        this.folderList = folderList;
    }

    public List<EmcFolderChangeBean> getFolderChangeList() {
        return folderChangeList;
    }

    public void setFolderChangeList(List<EmcFolderChangeBean> folderChangeList) {
        this.folderChangeList = folderChangeList;
    }

    public List<EmcOrderBean> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<EmcOrderBean> orderList) {
        this.orderList = orderList;
    }

    public List<EmcOrderChangeBean> getOrderChangeList() {
        return orderChangeList;
    }

    public void setOrderChangeList(List<EmcOrderChangeBean> orderChangeList) {
        this.orderChangeList = orderChangeList;
    }

    public List<EmcOrderItemGroupBean> getOrderItemGroupList() {
        return orderItemGroupList;
    }

    public void setOrderItemGroupList(List<EmcOrderItemGroupBean> orderItemGroupList) {
        this.orderItemGroupList = orderItemGroupList;
    }

    public List<EmcOrderItemGroupChangeBean> getOrderItemGroupChangeList() {
        return orderItemGroupChangeList;
    }

    public void setOrderItemGroupChangeList(List<EmcOrderItemGroupChangeBean> orderItemGroupChangeList) {
        this.orderItemGroupChangeList = orderItemGroupChangeList;
    }
}
