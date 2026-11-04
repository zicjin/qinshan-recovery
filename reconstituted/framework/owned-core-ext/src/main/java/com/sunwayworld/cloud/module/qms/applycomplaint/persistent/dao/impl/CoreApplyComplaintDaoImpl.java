/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.applycomplaint.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.applycomplaint.bean.CoreApplyComplaintBean;
import com.sunwayworld.cloud.module.qms.applycomplaint.persistent.dao.CoreApplyComplaintDao;
import com.sunwayworld.cloud.module.qms.applycomplaint.persistent.mapper.CoreApplyComplaintMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableMetadataHelper;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreApplyComplaintDaoImpl
extends MybatisDaoSupport<CoreApplyComplaintBean, Long>
implements CoreApplyComplaintDao {
    @Autowired
    private CoreApplyComplaintMapper coreApplyComplaintMapper;

    public CoreApplyComplaintMapper getMapper() {
        return this.coreApplyComplaintMapper;
    }

    @Override
    public List<Map<String, Object>> selectAuditStatusByCondition(MapperParameter parameter) {
        parameter.setTableName(PersistableMetadataHelper.getTableName(this.getType()));
        return this.getMapper().selectAuditStatusByCondition(parameter);
    }
}
