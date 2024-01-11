package com.sunwayworld.emc.module.bp.project.track.bean;

import java.util.List;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.framework.support.domain.BaseData;

/**
 * 项目明细跟踪
 * 
 * @author yuanhh@sunwayworld.com 2020年1月8日
 *
 */
public class EmcProjectItemTrackDTO extends AbstractBaseData implements BaseData {
    @Transient
    private static final long serialVersionUID = -219861650119140583L;
    
    /**
     * 项目跟踪类型编码
     *      --sample:采样管理
     *      --order:样品管理
     *      --run:结果录入
     *      --report:报告管理
     */
    private String categoryCode;
    private String categoryName;//项目跟踪类型名称
    
    List<EmcItemTrackDTO> itemTrackList;

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<EmcItemTrackDTO> getItemTrackList() {
        return itemTrackList;
    }

    public void setItemTrackList(List<EmcItemTrackDTO> itemTrackList) {
        this.itemTrackList = itemTrackList;
    }

}
