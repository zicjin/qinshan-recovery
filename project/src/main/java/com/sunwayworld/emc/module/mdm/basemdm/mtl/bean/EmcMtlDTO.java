package com.sunwayworld.emc.module.mdm.basemdm.mtl.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;

/**
 * 
 * 材料管理导入DTO
 *
 */
public class EmcMtlDTO extends AbstractTreeNode {

    @Transient
    private static final long serialVersionUID = -6820343203650387563L;

    private String mtlName; // 材料名称

    private String specification; // 规格型号

    private Long mtlCategoryId; // 材料类型编号

    private String mtlCategoryName; // 材料类型名称

    private String harmfulness; // 危害性

    private String reagentCategory; // 试剂类别

    public String getMtlName() {
        return mtlName;
    }

    public void setMtlName(String mtlName) {
        this.mtlName = mtlName;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public Long getMtlCategoryId() {
        return mtlCategoryId;
    }

    public void setMtlCategoryId(Long mtlCategoryId) {
        this.mtlCategoryId = mtlCategoryId;
    }

    public String getMtlCategoryName() {
        return mtlCategoryName;
    }

    public void setMtlCategoryName(String mtlCategoryName) {
        this.mtlCategoryName = mtlCategoryName;
    }

    public String getHarmfulness() {
        return harmfulness;
    }

    public void setHarmfulness(String harmfulness) {
        this.harmfulness = harmfulness;
    }

    public String getReagentCategory() {
        return reagentCategory;
    }

    public void setReagentCategory(String reagentCategory) {
        this.reagentCategory = reagentCategory;
    }
}
