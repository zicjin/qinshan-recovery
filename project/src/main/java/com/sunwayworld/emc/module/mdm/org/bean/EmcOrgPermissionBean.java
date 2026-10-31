package com.sunwayworld.emc.module.mdm.org.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 单位权限表
 * 
 * @author magh@sunwayworld.com 2019年9月4日
 *
 */
@Table("T_EMC_ORG_PERMISSION")
public class EmcOrgPermissionBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -8204027308226746088L;
    @Id
    private Long id; // <主键>
    private String orgId; // 单位编码
    private String orgName; // 单位名称
    private String permissionTypeId; // 权限类型ID(业务表名)
    private String targetId; // 目标权限ID

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getPermissionTypeId() {
        return permissionTypeId;
    }

    public void setPermissionTypeId(String permissionTypeId) {
        this.permissionTypeId = permissionTypeId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

}
