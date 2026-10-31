package com.sunwayworld.emc.module.mdm.bizmdm.comparison.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 比对结果
 * @author zhengxy@sunwayworld.com 2020年10月22日
 */
@Table("T_EMC_COMPARISON_RESULT")
public class EmcComparisonResultBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -6932668512609875078L;

    @Id
    private Long id;  // 主键
    private Long folderId;  // 点位ID
    private String testName;  // 测试名称
    private String result;  // 比对结果

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
