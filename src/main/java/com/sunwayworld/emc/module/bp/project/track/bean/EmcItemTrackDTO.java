package com.sunwayworld.emc.module.bp.project.track.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.framework.support.domain.BaseData;

/**
 * 明细跟踪
 * 
 * @author yuanhh@sunwayworld.com 2020年1月8日
 *
 */
public class EmcItemTrackDTO extends AbstractBaseData implements BaseData {
    @Transient
    private static final long serialVersionUID = 3584622261729675452L;

    private String itemName;// 明细名称
    private Long incompleteCount;// 未完成数量
    private String incompletePercentage;// 未完成百分比
    private Long totalCount;// 总数量
    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public Long getIncompleteCount() {
        return incompleteCount;
    }
    public void setIncompleteCount(Long incompleteCount) {
        this.incompleteCount = incompleteCount;
    }
    public String getIncompletePercentage() {
        return incompletePercentage;
    }
    public void setIncompletePercentage(String incompletePercentage) {
        this.incompletePercentage = incompletePercentage;
    }
    public Long getTotalCount() {
        return totalCount;
    }
    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}
