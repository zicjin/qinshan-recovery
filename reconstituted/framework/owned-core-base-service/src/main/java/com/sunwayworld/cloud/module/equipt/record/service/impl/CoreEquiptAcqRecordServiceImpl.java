/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.record.service.impl;

import com.sunwayworld.cloud.module.equipt.record.bean.CoreEquiptAcqRecordBean;
import com.sunwayworld.cloud.module.equipt.record.persistent.dao.CoreEquiptAcqRecordDao;
import com.sunwayworld.cloud.module.equipt.record.service.CoreEquiptAcqRecordService;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreEquiptAcqRecordServiceImpl
implements CoreEquiptAcqRecordService {
    @Autowired
    private CoreEquiptAcqRecordDao coreEquiptAcqRecordDao;

    @Override
    public CoreEquiptAcqRecordDao getDao() {
        return this.coreEquiptAcqRecordDao;
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void insertInNewTransaction(List<CoreEquiptAcqRecordBean> insertAcqRecordList) {
        this.getDao().insert(insertAcqRecordList);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void updateIfChangedInNewTransaction(CoreEquiptAcqRecordBean coreEquiptAcqRecord) {
        this.getDao().updateIfChanged(coreEquiptAcqRecord);
    }

    @Override
    public List<CoreEquiptAcqRecordBean> selectNoneAcqRecordList() {
        return this.selectListByFilter(SearchFilter.instance().match("ACQRESULT", "0").filter(MatchPattern.EQ), Order.asc("ID"));
    }
}
