package com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean;

import java.time.LocalDateTime;

import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Activatable;

/**
 * 样品指标
 * 
 * @author zhangsy@sunwayworld.com 2019/9/16
 *
 */
@Table("T_EMC_ORDER_INDEX")
public class EmcOrderIndexBean extends AbstractInsertable<String>implements Activatable<String> {

    @Transient
    private static final long serialVersionUID = -6154385438578326745L;

    @Id
    private String id; // id<主键>

    private Long indexCategoryId; // 指标分类id

    private String indexName; // 指标名称

    private String index1; // 指标1

    private String index2; // 指标2

    private String index3; // 指标3

    private String index4; // 指标4

    private String index5; // 指标5

    private String index6; // 指标6

    private String reportDisplayName; // 报告中显示名称

    @NotNull(defaultValue = Constant.ACTIVATED_STATUS_YES)
    private String activatedFlag;// 启用标志

    private String activatedById;// 启用人编码

    private String activatedByName;// 启用人名称

    private LocalDateTime activatedTime;// 启用时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getIndexCategoryId() {
        return indexCategoryId;
    }

    public void setIndexCategoryId(Long indexCategoryId) {
        this.indexCategoryId = indexCategoryId;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getIndex1() {
        return index1;
    }

    public void setIndex1(String index1) {
        this.index1 = index1;
    }

    public String getIndex2() {
        return index2;
    }

    public void setIndex2(String index2) {
        this.index2 = index2;
    }

    public String getIndex3() {
        return index3;
    }

    public void setIndex3(String index3) {
        this.index3 = index3;
    }

    public String getIndex4() {
        return index4;
    }

    public void setIndex4(String index4) {
        this.index4 = index4;
    }

    public String getIndex5() {
        return index5;
    }

    public void setIndex5(String index5) {
        this.index5 = index5;
    }

    public String getIndex6() {
        return index6;
    }

    public void setIndex6(String index6) {
        this.index6 = index6;
    }

    public String getReportDisplayName() {
        return reportDisplayName;
    }

    public void setReportDisplayName(String reportDisplayName) {
        this.reportDisplayName = reportDisplayName;
    }

    public String getActivatedFlag() {
        return activatedFlag;
    }

    public void setActivatedFlag(String activatedFlag) {
        this.activatedFlag = activatedFlag;
    }

    public String getActivatedById() {
        return activatedById;
    }

    public void setActivatedById(String activatedById) {
        this.activatedById = activatedById;
    }

    public String getActivatedByName() {
        return activatedByName;
    }

    public void setActivatedByName(String activatedByName) {
        this.activatedByName = activatedByName;
    }

    public LocalDateTime getActivatedTime() {
        return activatedTime;
    }

    public void setActivatedTime(LocalDateTime activatedTime) {
        this.activatedTime = activatedTime;
    }

}
