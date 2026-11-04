/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.dao.impl;

import com.sunwayworld.framework.data.ListChunkIterator;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskHisBean;
import com.sunwayworld.module.sys.bpmn.dao.CoreBpmnInstanceTaskHisDao;
import com.sunwayworld.module.sys.bpmn.mapper.CoreBpmnInstanceTaskHisMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreBpmnInstanceTaskHisDaoImpl
extends MybatisDaoSupport<CoreBpmnInstanceTaskHisBean, Long>
implements CoreBpmnInstanceTaskHisDao {
    @Autowired
    private CoreBpmnInstanceTaskHisMapper instanceTaskHisMapper;

    public CoreBpmnInstanceTaskHisMapper getMapper() {
        return this.instanceTaskHisMapper;
    }

    @Override
    public List<CoreBpmnInstanceTaskHisBean> selectLatestTaskHisList(List<CoreBpmnInstanceTaskHisBean> filterList) {
        ArrayList<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        int loopItemQty = Math.min(filterList.size(), 1000);
        ListChunkIterator<CoreBpmnInstanceTaskHisBean> chunkIterator = ListChunkIterator.of(filterList, loopItemQty);
        while (chunkIterator.hasNext()) {
            mapList.addAll(this.getMapper().selectLatestTaskHisList(chunkIterator.nextChunk()));
        }
        if (CollectionUtils.isEmpty(mapList)) {
            return CollectionUtils.emptyList();
        }
        return mapList.stream().map(m -> PersistableHelper.mapToPersistable(m, CoreBpmnInstanceTaskHisBean.class)).collect(Collectors.toList());
    }
}
