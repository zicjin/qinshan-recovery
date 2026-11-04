/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.bean;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceTreeNodeDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpViewBean;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import com.sunwayworld.module.sys.code.bean.CoreCodeCategoryBean;
import java.util.List;

public class LcdpResourceExportDTO
extends AbstractBaseData {
    private static final long serialVersionUID = 4041717488401902182L;
    private String exportLog;
    List<LcdpResourceTreeNodeDTO> treeNodeDtoList;
    private List<LcdpResourceBean> exportResourceList;
    private List<LcdpTableDTO> tableDTOList;
    private List<LcdpViewBean> viewList;
    private List<CoreCodeCategoryBean> coreCodeCategoryList;
    private List<CoreCodeBean> coreCodeList;

    public String getExportLog() {
        return this.exportLog;
    }

    public void setExportLog(String exportLog) {
        this.exportLog = exportLog;
    }

    public List<LcdpResourceTreeNodeDTO> getTreeNodeDtoList() {
        return this.treeNodeDtoList;
    }

    public void setTreeNodeDtoList(List<LcdpResourceTreeNodeDTO> treeNodeDtoList) {
        this.treeNodeDtoList = treeNodeDtoList;
    }

    public List<LcdpResourceBean> getExportResourceList() {
        return this.exportResourceList;
    }

    public void setExportResourceList(List<LcdpResourceBean> exportResourceList) {
        this.exportResourceList = exportResourceList;
    }

    public List<LcdpTableDTO> getTableDTOList() {
        return this.tableDTOList;
    }

    public void setTableDTOList(List<LcdpTableDTO> tableDTOList) {
        this.tableDTOList = tableDTOList;
    }

    public List<LcdpViewBean> getViewList() {
        return this.viewList;
    }

    public void setViewList(List<LcdpViewBean> viewList) {
        this.viewList = viewList;
    }

    public List<CoreCodeCategoryBean> getCoreCodeCategoryList() {
        return this.coreCodeCategoryList;
    }

    public void setCoreCodeCategoryList(List<CoreCodeCategoryBean> coreCodeCategoryList) {
        this.coreCodeCategoryList = coreCodeCategoryList;
    }

    public List<CoreCodeBean> getCoreCodeList() {
        return this.coreCodeList;
    }

    public void setCoreCodeList(List<CoreCodeBean> coreCodeList) {
        this.coreCodeList = coreCodeList;
    }
}
