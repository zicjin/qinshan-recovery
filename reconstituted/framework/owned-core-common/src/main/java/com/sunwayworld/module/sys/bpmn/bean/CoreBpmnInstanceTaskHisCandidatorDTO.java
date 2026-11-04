/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CoreBpmnInstanceTaskHisCandidatorDTO
implements Serializable {
    private static final long serialVersionUID = -5253860124415526191L;
    private List<String> userIdList = new ArrayList<String>();
    private List<Long> roleIdList = new ArrayList<Long>();

    public List<String> getUserIdList() {
        return this.userIdList;
    }

    public void setUserIdList(List<String> userIdList) {
        this.userIdList = userIdList;
    }

    public void addUserId(String userId) {
        this.userIdList.add(userId);
    }

    public List<Long> getRoleIdList() {
        return this.roleIdList;
    }

    public void setRoleIdList(List<Long> roleIdList) {
        this.roleIdList = roleIdList;
    }

    public void addRoleId(Long roleId) {
        this.roleIdList.add(roleId);
    }
}
