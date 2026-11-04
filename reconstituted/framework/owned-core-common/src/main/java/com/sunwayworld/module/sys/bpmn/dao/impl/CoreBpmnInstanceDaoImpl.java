/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.dao.impl;

import com.sunwayworld.framework.data.ListChunkIterator;
import com.sunwayworld.framework.database.dialect.Dialect;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceCandidatorDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceDetailedCandidatorDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskCandidatorDTO;
import com.sunwayworld.module.sys.bpmn.dao.CoreBpmnInstanceDao;
import com.sunwayworld.module.sys.bpmn.mapper.CoreBpmnInstanceMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreBpmnInstanceDaoImpl
extends MybatisDaoSupport<CoreBpmnInstanceBean, Long>
implements CoreBpmnInstanceDao {
    @Autowired
    private CoreBpmnInstanceMapper instanceMapper;
    @Autowired
    private Dialect dialect;

    public CoreBpmnInstanceMapper getMapper() {
        return this.instanceMapper;
    }

    @Override
    public List<CoreBpmnInstanceCandidatorDTO> selectInstanceCandidatorList(List<Long> idList) {
        int chunkSize = this.dialect.getInClauseIdMaxQty();
        ArrayList<CoreBpmnInstanceCandidatorDTO> resultList = new ArrayList<CoreBpmnInstanceCandidatorDTO>();
        ListChunkIterator<Long> chunkIterator = ListChunkIterator.of(idList, chunkSize);
        while (chunkIterator.hasNext()) {
            resultList.addAll(this.getMapper().selectInstanceAuditorList(chunkIterator.nextChunk()).stream().map(m -> PersistableHelper.mapToPersistable(m, CoreBpmnInstanceCandidatorDTO.class)).collect(Collectors.toList()));
        }
        return resultList;
    }

    @Override
    public List<CoreBpmnInstanceTaskCandidatorDTO> selectInstanceTaskAuditorList(List<Long> idList) {
        int chunkSize = this.dialect.getInClauseIdMaxQty();
        ArrayList<CoreBpmnInstanceTaskCandidatorDTO> resultList = new ArrayList<CoreBpmnInstanceTaskCandidatorDTO>();
        ListChunkIterator<Long> chunkIterator = ListChunkIterator.of(idList, chunkSize);
        while (chunkIterator.hasNext()) {
            resultList.addAll(this.getMapper().selectInstanceTaskAuditorList(chunkIterator.nextChunk()).stream().map(m -> PersistableHelper.mapToPersistable(m, CoreBpmnInstanceTaskCandidatorDTO.class)).collect(Collectors.toList()));
        }
        return resultList;
    }

    @Override
    public List<CoreBpmnInstanceDetailedCandidatorDTO> selectInstanceDetailedCandidatorList(List<Long> idList) {
        int chunkSize = this.dialect.getInClauseIdMaxQty();
        ArrayList<CoreBpmnInstanceDetailedCandidatorDTO> resultList = new ArrayList<CoreBpmnInstanceDetailedCandidatorDTO>();
        ListChunkIterator<Long> chunkIterator = ListChunkIterator.of(idList, chunkSize);
        while (chunkIterator.hasNext()) {
            resultList.addAll(this.getMapper().selectInstanceDetailedCandidatorList(chunkIterator.nextChunk()).stream().map(m -> PersistableHelper.mapToPersistable(m, CoreBpmnInstanceDetailedCandidatorDTO.class)).collect(Collectors.toList()));
        }
        return resultList;
    }
}
