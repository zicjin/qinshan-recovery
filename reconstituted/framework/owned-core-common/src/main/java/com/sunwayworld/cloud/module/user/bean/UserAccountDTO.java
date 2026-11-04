/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.user.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;

public class UserAccountDTO
extends AbstractBaseData {
    private static final long serialVersionUID = 6659446470272034367L;
    private String id;
    private String userNo;
    private String userName;
    private String orgId;
    private String orgName;
    private String userType;
    private String trusteeId;

    public static UserAccountDTO of(CoreUserBean user) {
        UserAccountDTO account = new UserAccountDTO();
        account.setId(user.getId());
        account.setUserNo(user.getUserNo());
        account.setUserName(user.getUserName());
        account.setUserType(user.getUserType());
        account.setOrgId(user.getOrgId());
        account.setOrgName(user.getOrgName());
        account.setExt$(user.getExt$());
        return account;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserNo() {
        return this.userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOrgId() {
        return this.orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return this.orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getUserType() {
        return this.userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getTrusteeId() {
        return this.trusteeId;
    }

    public void setTrusteeId(String trusteeId) {
        this.trusteeId = trusteeId;
    }
}
