/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.helpdocument.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.helpdocument.bean.CoreHelpDocumentCatalogBean;
import com.sunwayworld.module.item.helpdocument.dao.CoreHelpDocumentCatalogDao;
import com.sunwayworld.module.item.helpdocument.mapper.CoreHelpDocumentCatalogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreHelpDocumentCatalogDaoImpl
extends MybatisDaoSupport<CoreHelpDocumentCatalogBean, Long>
implements CoreHelpDocumentCatalogDao {
    @Autowired
    private CoreHelpDocumentCatalogMapper coreHelpDocumentCatalogMapper;

    public CoreHelpDocumentCatalogMapper getMapper() {
        return this.coreHelpDocumentCatalogMapper;
    }
}
