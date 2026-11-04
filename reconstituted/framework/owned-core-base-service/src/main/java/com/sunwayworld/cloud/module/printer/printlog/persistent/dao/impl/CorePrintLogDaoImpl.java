/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.printer.printlog.persistent.dao.impl;

import com.sunwayworld.cloud.module.printer.printlog.bean.CorePrintLogBean;
import com.sunwayworld.cloud.module.printer.printlog.persistent.dao.CorePrintLogDao;
import com.sunwayworld.cloud.module.printer.printlog.persistent.mapper.CorePrintLogMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CorePrintLogDaoImpl
extends MybatisDaoSupport<CorePrintLogBean, Long>
implements CorePrintLogDao {
    @Autowired
    private CorePrintLogMapper corePrintLogMapper;

    public CorePrintLogMapper getMapper() {
        return this.corePrintLogMapper;
    }
}
