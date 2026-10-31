package com.sunwayworld.emc.module.bp.project.track.bean;

import java.util.List;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.framework.support.domain.BaseData;

/**
 * 项目跟踪
 * 
 * @author yuanhh@sunwayworld.com 2020年1月8日
 *
 */
public class EmcProjectTrackDTO extends AbstractBaseData implements BaseData {

    @Transient
    private static final long serialVersionUID = 973204818073979255L;

    private Long projId;// 项目主键ID
    private String projNo;// 项目编号
    private String projName;// 项目名称
    private String bpmnStatus;//流程状态
    
    private List<EmcProjectItemTrackDTO> projectItemTrackList;

    public Long getProjId() {
        return projId;
    }

    public void setProjId(Long projId) {
        this.projId = projId;
    }

    public String getProjNo() {
        return projNo;
    }

    public void setProjNo(String projNo) {
        this.projNo = projNo;
    }

    public String getProjName() {
        return projName;
    }

    public void setProjName(String projName) {
        this.projName = projName;
    }

    public String getBpmnStatus() {
        return bpmnStatus;
    }

    public void setBpmnStatus(String bpmnStatus) {
        this.bpmnStatus = bpmnStatus;
    }

    public List<EmcProjectItemTrackDTO> getProjectItemTrackList() {
        return projectItemTrackList;
    }

    public void setProjectItemTrackList(List<EmcProjectItemTrackDTO> projectItemTrackList) {
        this.projectItemTrackList = projectItemTrackList;
    }
}
