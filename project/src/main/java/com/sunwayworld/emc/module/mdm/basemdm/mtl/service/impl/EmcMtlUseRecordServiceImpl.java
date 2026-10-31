package com.sunwayworld.emc.module.mdm.basemdm.mtl.service.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlUseRecordBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.dao.EmcMtlUseRecordDao;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcMtlUseRecordService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockLogBean;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.page.MybatisPageHelper;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;

@Repository
@GikamBean
public class EmcMtlUseRecordServiceImpl implements EmcMtlUseRecordService {

    @Autowired
    private EmcMtlUseRecordDao emcMtlUseRecordDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcMtlUseRecordDao getDao() {
        return emcMtlUseRecordDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcMtlUseRecordBean emcMtlUseRecord = jsonWrapper.parseUnique(EmcMtlUseRecordBean.class);
        emcMtlUseRecord.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcMtlUseRecord);
        return emcMtlUseRecord.getId();
    }

    @Override
    public Page<EmcStockLogBean> selectStockLogInApply(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        List<Map<String, Object>> stockLogInApply = getDao().selectStockLogInApply(parameter);
        List<EmcStockLogBean> list = stockLogInApply.stream().map(m -> PersistableHelper.mapToPersistable(m, EmcStockLogBean.class)).collect(Collectors.toList());
        Page<EmcStockLogBean> page = MybatisPageHelper.get(rowBounds, new Supplier<List<EmcStockLogBean>>() {

            @Override
            public List<EmcStockLogBean> get() {
                return list;
            }

        });
        return page;
    }

    @Override
    public void apply(Long id, RestJsonWrapperBean wrapper) {
        EmcMtlUseRecordBean emcMtlUseRecord = wrapper.parseUnique(EmcMtlUseRecordBean.class);
        emcMtlUseRecord.setId(ApplicationContextHelper.getNextIdentity());
        emcMtlUseRecord.setApplyId(id.toString());
        getDao().insert(emcMtlUseRecord);
    }

}
