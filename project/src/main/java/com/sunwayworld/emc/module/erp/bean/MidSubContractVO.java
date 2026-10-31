package com.sunwayworld.emc.module.erp.bean;

import java.io.Serializable;

public class MidSubContractVO implements Serializable {

    /** 试验机构 */
    private String orgName;

    /** 联系人姓名 */
    private String contactName;

    /** 联系人地址 */
    private String contactAddress;

    /** 联系人电话 */
    private String contactPhone;

    /** 跟单员姓名 */
    private String merchandiserName;

    /** 上一家检测机构 */
    private String preOrgNames;

    /** 完成周期 */
    private Integer completePeriod;

    /** 分包单创建时间 */
    private Long tWhen;

    /** po分包成本 */
    private Long poPrice;

    /** 是否是po单 */
    private boolean isPoOrder;

    /** 收件地址 */
    private String recipientAddress;

    /** 寄样物流信息 */
    private String expressInfo;

    /** 备注 */
    private String remarks;

    /** 附件 */
    private String attach;

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getMerchandiserName() {
        return merchandiserName;
    }

    public void setMerchandiserName(String merchandiserName) {
        this.merchandiserName = merchandiserName;
    }

    public String getPreOrgNames() {
        return preOrgNames;
    }

    public void setPreOrgNames(String preOrgNames) {
        this.preOrgNames = preOrgNames;
    }

    public Integer getCompletePeriod() {
        return completePeriod;
    }

    public void setCompletePeriod(Integer completePeriod) {
        this.completePeriod = completePeriod;
    }

    public Long gettWhen() {
        return tWhen;
    }

    public void settWhen(Long tWhen) {
        this.tWhen = tWhen;
    }

    public Long getPoPrice() {
        return poPrice;
    }

    public void setPoPrice(Long poPrice) {
        this.poPrice = poPrice;
    }

    public boolean isPoOrder() {
        return isPoOrder;
    }

    public void setPoOrder(boolean poOrder) {
        isPoOrder = poOrder;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public String getExpressInfo() {
        return expressInfo;
    }

    public void setExpressInfo(String expressInfo) {
        this.expressInfo = expressInfo;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }
}
