package com.sunwayworld.emc.module.mdm.bizmdm.test.bean;


import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

/**
 * 质控分析项表
 *
 * @author changsp@sunwayworld.com
 * @date 2021-07-16
 */

@Table("T_EMC_QC_ITEM")
public class EmcQcItemBean extends AbstractPersistable<Long> implements Persistable<Long> {

    @Transient
    private static final long serialVersionUID = -1027632086125342617L;

    @Id
    private Long id;// 主键

    private Long testMethodId;// 测试方法ID

    private String itemName;// 分析项名称

    private String itemAliasName;// 分析项别名

    private String lowLimit;// 检出低限
    
    private String highLimit;// 检出高限

    private String revisionRule;// 修约规则
    
    private String qcCategoryId;// QC中登录

    private String qcCategoryName;// QC类型名称(多个)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTestMethodId() {
        return testMethodId;
    }

    public void setTestMethodId(Long testMethodId) {
        this.testMethodId = testMethodId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemAliasName() {
        return itemAliasName;
    }

    public void setItemAliasName(String itemAliasName) {
        this.itemAliasName = itemAliasName;
    }

    public String getLowLimit() {
        return lowLimit;
    }

    public void setLowLimit(String lowLimit) {
        this.lowLimit = lowLimit;
    }

    public String getRevisionRule() {
        return revisionRule;
    }

    public void setRevisionRule(String revisionRule) {
        this.revisionRule = revisionRule;
    }

    public String getQcCategoryId() {
        return qcCategoryId;
    }

    public void setQcCategoryId(String qcCategoryId) {
        this.qcCategoryId = qcCategoryId;
    }

    public String getQcCategoryName() {
        return qcCategoryName;
    }

    public void setQcCategoryName(String qcCategoryName) {
        this.qcCategoryName = qcCategoryName;
    }

    public String getHighLimit() {
        return highLimit;
    }

    public void setHighLimit(String highLimit) {
        this.highLimit = highLimit;
    }

}
