package com.sunwayworld.emc.module.erp.bean;

import java.util.Map;

/** 订单变更详情返回数据 */
public class OrderChangeDetailResp {

    /** 合同模板信息 */
    private ContractCustomTemplateInfo customTemplateView;

    /** 附件及其地址 */
    private Map<String, String> fileNameMap;

    /** 订单变更备注 */
    private String note;

    /** 订单变更数据变更前 */
    private OrderChangeDiffResp source;

    /** 订单变更数据变更后 */
    private OrderChangeDiffResp target;

    /** 订单变更折扣审核状态： 0 -> 未审核；1 -> 已通过；2 -> 已驳回 */
    private int discountReviewStatus;

    /** 提交人GBID */
    private Long actEngineerGbId;

    /** 提交人 */
    private String actEngineerName;

    public ContractCustomTemplateInfo getCustomTemplateView() {
        return customTemplateView;
    }

    public void setCustomTemplateView(ContractCustomTemplateInfo customTemplateView) {
        this.customTemplateView = customTemplateView;
    }

    public Map<String, String> getFileNameMap() {
        return fileNameMap;
    }

    public void setFileNameMap(Map<String, String> fileNameMap) {
        this.fileNameMap = fileNameMap;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public OrderChangeDiffResp getSource() {
        return source;
    }

    public void setSource(OrderChangeDiffResp source) {
        this.source = source;
    }

    public OrderChangeDiffResp getTarget() {
        return target;
    }

    public void setTarget(OrderChangeDiffResp target) {
        this.target = target;
    }

    public int getDiscountReviewStatus() {
        return discountReviewStatus;
    }

    public void setDiscountReviewStatus(int discountReviewStatus) {
        this.discountReviewStatus = discountReviewStatus;
    }

    public Long getActEngineerGbId() {
        return actEngineerGbId;
    }

    public void setActEngineerGbId(Long actEngineerGbId) {
        this.actEngineerGbId = actEngineerGbId;
    }

    public String getActEngineerName() {
        return actEngineerName;
    }

    public void setActEngineerName(String actEngineerName) {
        this.actEngineerName = actEngineerName;
    }
}
