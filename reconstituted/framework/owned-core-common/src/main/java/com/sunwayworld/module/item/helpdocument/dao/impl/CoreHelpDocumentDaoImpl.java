/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.helpdocument.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.helpdocument.bean.CoreHelpDocumentBean;
import com.sunwayworld.module.item.helpdocument.dao.CoreHelpDocumentDao;
import com.sunwayworld.module.item.helpdocument.mapper.CoreHelpDocumentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreHelpDocumentDaoImpl
extends MybatisDaoSupport<CoreHelpDocumentBean, Long>
implements CoreHelpDocumentDao {
    @Autowired
    private CoreHelpDocumentMapper coreHelpDocumentMapper;

    public CoreHelpDocumentMapper getMapper() {
        return this.coreHelpDocumentMapper;
    }
}
