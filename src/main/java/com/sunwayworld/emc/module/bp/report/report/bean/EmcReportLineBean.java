package com.sunwayworld.emc.module.bp.report.report.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 报告明细
 * @author zhengxy@sunwayworld.com 2022年01月14日
 */
@Table("T_EMC_REPORT_LINE")
public class EmcReportLineBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -5847110574230736921L;

    @Id
    private Long id;  // 主键
    private Long reportId;  // 报告ID
    private Long projId;  // 项目ID
    private Long folderId;  // 点位ID
    private Long orderId;  // 样品ID
    private Long orderTaskId;  // 样品测试ID

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public Long getProjId() {
        return projId;
    }

    public void setProjId(Long projId) {
        this.projId = projId;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderTaskId() {
        return orderTaskId;
    }

    public void setOrderTaskId(Long orderTaskId) {
        this.orderTaskId = orderTaskId;
    }

}
