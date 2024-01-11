package com.sunwayworld.emc.module.erp.bean.contract;

/** ERP 样品类型 */
public class MidSamplingTypeDTO {

    private Long id;
    private String typeNo;
    private String typeName;
    private String typeGroupName;
    private Integer sampleWorkPoint;
    private String productCategory;
    private Boolean status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTypeNo() {
        return typeNo;
    }

    public void setTypeNo(String typeNo) {
        this.typeNo = typeNo;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeGroupName() {
        return typeGroupName;
    }

    public void setTypeGroupName(String typeGroupName) {
        this.typeGroupName = typeGroupName;
    }

    public Integer getSampleWorkPoint() {
        return sampleWorkPoint;
    }

    public void setSampleWorkPoint(Integer sampleWorkPoint) {
        this.sampleWorkPoint = sampleWorkPoint;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public Boolean isStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
