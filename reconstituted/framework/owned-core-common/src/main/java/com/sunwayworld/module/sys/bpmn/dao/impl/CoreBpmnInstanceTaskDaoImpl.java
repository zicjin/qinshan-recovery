/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.dao.impl;

import com.sunwayworld.framework.data.ListChunkIterator;
import com.sunwayworld.framework.database.dialect.Dialect;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskBean;
import com.sunwayworld.module.sys.bpmn.dao.CoreBpmnInstanceTaskDao;
import com.sunwayworld.module.sys.bpmn.mapper.CoreBpmnInstanceTaskMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreBpmnInstanceTaskDaoImpl
extends MybatisDaoSupport<CoreBpmnInstanceTaskBean, Long>
implements CoreBpmnInstanceTaskDao {
    @Autowired
    private CoreBpmnInstanceTaskMapper instanceTaskMapper;
    @Autowired
    private Dialect dialect;

    public CoreBpmnInstanceTaskMapper getMapper() {
        return this.instanceTaskMapper;
    }

    @Override
    public List<CoreBpmnInstanceTaskBean> selectAuditableInstanceTaskList(@Nullable String userId, List<Long> instIdList) {
        int chunkSize = this.dialect.getInClauseIdMaxQty();
        ArrayList<CoreBpmnInstanceTaskBean> resultList = new ArrayList<CoreBpmnInstanceTaskBean>();
        ListChunkIterator<Long> chunkIterator = ListChunkIterator.of(instIdList, chunkSize);
        while (chunkIterator.hasNext()) {
            resultList.addAll(this.getMapper().selectAuditableInstanceTaskList(userId, chunkIterator.nextChunk()).stream().map(m -> PersistableHelper.mapToPersistable(m, CoreBpmnInstanceTaskBean.class)).collect(Collectors.toList()));
        }
        return resultList;
    }
}
