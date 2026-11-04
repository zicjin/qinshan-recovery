/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.menu.persistent.mapper;

import com.sunwayworld.cloud.module.mobile.menu.bean.MobilePageBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public interface MobilePageMapper
extends GenericMapper<Long> {
    public List<Map<String, Object>> selectMobilePageTreeList(MapperParameter var1);

    public MobilePageBean selectPage(Long var1);
}
