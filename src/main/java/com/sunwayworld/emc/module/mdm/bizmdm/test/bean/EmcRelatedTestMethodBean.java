package com.sunwayworld.emc.module.mdm.bizmdm.test.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 关联测试方法
 * @author zhengxy@sunwayworld.com 2021年08月26日
 */
@Table("T_EMC_RELATED_TEST_METHOD")
public class EmcRelatedTestMethodBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -8430790759039867634L;

    @Id
    private Long id;  // 主键
    private Long testMethodId;  // 测试方法ID
    private Long relatedTestMethodId;  // 关联测试方法ID

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getTestMethodId() {
        return testMethodId;
    }

    public void setTestMethodId(Long testMethodId) {
        this.testMethodId = testMethodId;
    }

    public Long getRelatedTestMethodId() {
        return relatedTestMethodId;
    }

    public void setRelatedTestMethodId(Long relatedTestMethodId) {
        this.relatedTestMethodId = relatedTestMethodId;
    }

}
