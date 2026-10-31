package com.sunwayworld.emc.module.mdm.equipt.equipt.bean;

import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * @author magt@sunwayworld.com
 * @date 2019-09-20
 */

@Table("T_EMC_EQUIPT_TEST_METHOD")
public class EmcEquiptTestMethodBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -3173125326303163964L;

    @Id
    private Long id; // 主键

    private Long equiptId; // 仪器ID

    private Long testCategoryId; // 测试类型编码

    private Long testMethodId; // 测试方法编码

    private Long testId; // 测试编码

    private Long methodId; // 方法编码

    private String curveNo; // 曲线编码

    private String curveId; // 曲线id

    private String orgId; // 所属单位

    private String analysisCategory; // 分析类型
    @NotNull(defaultValue = Constant.NO)
    private String defaultFlag; // 是否默认

    private String ids; // 用于提取多个id合并的字符串

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getEquiptId() {
        return equiptId;
    }

    public void setEquiptId(Long equiptId) {
        this.equiptId = equiptId;
    }

    public Long getTestCategoryId() {
        return testCategoryId;
    }

    public void setTestCategoryId(Long testCategoryId) {
        this.testCategoryId = testCategoryId;
    }

    public Long getTestMethodId() {
        return testMethodId;
    }

    public void setTestMethodId(Long testMethodId) {
        this.testMethodId = testMethodId;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public Long getMethodId() {
        return methodId;
    }

    public void setMethodId(Long methodId) {
        this.methodId = methodId;
    }

    public String getCurveNo() {
        return curveNo;
    }

    public void setCurveNo(String curveNo) {
        this.curveNo = curveNo;
    }

    public String getCurveId() {
        return curveId;
    }

    public void setCurveId(String curveId) {
        this.curveId = curveId;
    }

    public String getAnalysisCategory() {
        return analysisCategory;
    }

    public void setAnalysisCategory(String analysisCategory) {
        this.analysisCategory = analysisCategory;
    }

    public String getDefaultFlag() {
        return defaultFlag;
    }

    public void setDefaultFlag(String defaultFlag) {
        this.defaultFlag = defaultFlag;
    }

}
