/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.sequence.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.sequence.service.impl.CoreSequenceAbstractService;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
@ConditionalOnProperty(prefix="sunway.sequence", name={"continuous"}, havingValue="true", matchIfMissing=false)
public class CoreContinuousSequenceServiceImpl
extends CoreSequenceAbstractService {
    @Override
    @Transactional
    public List<String> getNextSequenceList(String id, String key, int qty) {
        return super.getNextSequenceList(id, key, qty);
    }

    @Override
    @Transactional
    public List<Long> getNextIdentityList(String id, int qty) {
        return super.getNextIdentityList(id, qty);
    }
}
