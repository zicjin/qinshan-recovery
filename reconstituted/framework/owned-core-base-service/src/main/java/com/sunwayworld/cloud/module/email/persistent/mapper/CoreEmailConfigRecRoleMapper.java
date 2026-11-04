/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.persistent.mapper;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
import java.util.List;

@GikamBean
public interface CoreEmailConfigRecRoleMapper
extends GenericMapper<Long> {
    public List<String> selectUserListByConfig(String var1);
}
