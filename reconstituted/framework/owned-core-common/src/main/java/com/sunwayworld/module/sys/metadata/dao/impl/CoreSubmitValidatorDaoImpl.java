/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.metadata.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.metadata.bean.CoreSubmitValidatorBean;
import com.sunwayworld.module.sys.metadata.dao.CoreSubmitValidatorDao;
import com.sunwayworld.module.sys.metadata.mapper.CoreMetadataMapper;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreSubmitValidatorDaoImpl
extends MybatisDaoSupport<CoreSubmitValidatorBean, Long>
implements CoreSubmitValidatorDao {
    @Autowired
    private CoreMetadataMapper metadataMapper;

    @Override
    public List<Map<String, Object>> selectSubmitValidatorByTable(String table) {
        return this.metadataMapper.selectSubmitValidatorByTable(table);
    }
}
