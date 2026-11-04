/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.sequence.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.module.sys.sequence.bean.CoreSequenceLineBean;
import com.sunwayworld.module.sys.sequence.dao.CoreSequenceLineDao;
import com.sunwayworld.module.sys.sequence.service.CoreSequenceLineService;
import com.sunwayworld.module.sys.sequence.service.CoreSequenceService;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreSequenceLineServiceImpl
implements CoreSequenceLineService {
    @Autowired
    private CoreSequenceLineDao sequenceLineDao;
    @Autowired
    @Lazy
    private CoreSequenceService sequenceService;

    @Override
    public CoreSequenceLineDao getDao() {
        return this.sequenceLineDao;
    }

    @Override
    @Transactional
    public void postUpdate(List<String> updatedColumnNameList, List<Long> idList) {
        if (CollectionUtils.containsIgnoreCase(updatedColumnNameList, "sequencecode")) {
            this.sequenceService.buildSequencePattern(((CoreSequenceLineBean)this.selectListByIds(idList).get(0)).getSequenceId());
        }
    }

    @Override
    @Transactional
    public void swap(String column, Long firstId, Long secondId) {
        CoreSequenceLineBean first = (CoreSequenceLineBean)this.getDao().selectById(firstId);
        CoreSequenceLineBean second = (CoreSequenceLineBean)this.getDao().selectById(secondId);
        Field field = ReflectionUtils.findField(this.getDao().getType(), column);
        if (field == null) {
            String key = column.toLowerCase();
            String firstValue = first.getExt$().get(key);
            first.setExt$Item(key, second.getExt$().get(key));
            second.setExt$Item(key, firstValue);
        } else {
            Object firstValue = ReflectionUtils.invokeReadMethod(first, field.getName());
            Object secondValue = ReflectionUtils.invokeReadMethod(second, field.getName());
            ReflectionUtils.invokeWriteMethod(first, field.getName(), secondValue);
            ReflectionUtils.invokeWriteMethod(second, field.getName(), firstValue);
        }
        this.getDao().update(Arrays.asList(first, second), new String[]{column});
        String sequenceId = first.getSequenceId();
        this.sequenceService.buildSequencePattern(sequenceId);
    }
}
