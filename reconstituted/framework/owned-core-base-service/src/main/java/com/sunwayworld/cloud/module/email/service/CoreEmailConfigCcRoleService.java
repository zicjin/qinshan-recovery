/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.service;

import com.sunwayworld.cloud.module.email.bean.CoreEmailConfigCcRoleBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;

public interface CoreEmailConfigCcRoleService
extends GenericService<CoreEmailConfigCcRoleBean, Long> {
    public List<String> selectUserListByConfig(String var1);
}
