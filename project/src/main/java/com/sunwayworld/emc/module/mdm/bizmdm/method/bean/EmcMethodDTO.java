package com.sunwayworld.emc.module.mdm.bizmdm.method.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;

/**
 *
 * 方法导入DTO
 *
 */
public class EmcMethodDTO extends AbstractTreeNode {

    @Transient
    private static final long serialVersionUID = 8456782773477842350L;

    private String methodName;// 方法名称

    private String testMethodBasis;// 检测方法及依据

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime beginDate;// 开始日期

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime expireDate;// 过期日期

    private Long methodId;// 方法编码

    private Long maxVersionNo;// 最大方法版本号

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getTestMethodBasis() {
        return testMethodBasis;
    }

    public void setTestMethodBasis(String testMethodBasis) {
        this.testMethodBasis = testMethodBasis;
    }

    public LocalDateTime getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(LocalDateTime beginDate) {
        this.beginDate = beginDate;
    }

    public LocalDateTime getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDateTime expireDate) {
        this.expireDate = expireDate;
    }

    public Long getMethodId() {
        return methodId;
    }

    public void setMethodId(Long methodId) {
        this.methodId = methodId;
    }

    public Long getMaxVersionNo() {
        return maxVersionNo;
    }

    public void setMaxVersionNo(Long maxVersionNo) {
        this.maxVersionNo = maxVersionNo;
    }
}
