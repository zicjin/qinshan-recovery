package com.sunwayworld.emc.module.bp.info.infotmpl.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcInfoTmplFieldBean;
import com.sunwayworld.emc.module.bp.info.infotmpl.dao.EmcInfoTmplFieldDao;
import com.sunwayworld.emc.module.bp.info.infotmpl.service.EmcInfoTmplFieldService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-19
 */

@Repository
@GikamBean
public class EmcInfoTmplFieldServiceImpl implements EmcInfoTmplFieldService {
    /**
     * 模板字段项初始数据条数
     */
    private static final int TMPL_FIELD_INIT_COUNT = 130;
    
    @Autowired
    private EmcInfoTmplFieldDao infoTmplFieldDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcInfoTmplFieldDao getDao() {
        return infoTmplFieldDao;
    }

    /**
     * 初始模板字段项数据
     */
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void initData(Long infoTmplId) {
        List<EmcInfoTmplFieldBean> indexTestItemBeanList = new ArrayList<>();
        for (int i = 1; i <= TMPL_FIELD_INIT_COUNT; i++) {
            EmcInfoTmplFieldBean infoTmplFieldBean = new EmcInfoTmplFieldBean();
            infoTmplFieldBean.setId(ApplicationContextHelper.getNextIdentity());
            infoTmplFieldBean.setInfoTmplId(infoTmplId);
            infoTmplFieldBean.setField("RN" + (i < 10 ? StringUtils.leftPad(String.valueOf(i), 2) : String.valueOf(i)));
            indexTestItemBeanList.add(infoTmplFieldBean);
        }
        getDao().insert(indexTestItemBeanList);
    }
    
    @Override
    @Log(value = "复制模板字段项", type = LogType.INSERT)
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public List<Pair<Long, Long>> copy(Pair<Long, Long> infoTmplIdPair) {
        //查询原模板对象
        List<EmcInfoTmplFieldBean> infoTmplFieldList = getDao().selectListByOneColumnValue(infoTmplIdPair.getFirst(), "INFOTMPLID", Order.asc("ID"));
        if(CollectionUtils.isEmpty(infoTmplFieldList)) {
            return new ArrayList<>();
        }
        //模板字段项ID映射对象列表
        List<Pair<Long, Long>> infoTmplFieldIdPairList = new ArrayList<>();
        //新的模板字段项列表
        List<EmcInfoTmplFieldBean> newInfoTmplFieldList = infoTmplFieldList.stream().map(infoTmplField -> {
            Long fromInfoTmplFieldId = infoTmplField.getId();
            //
            infoTmplField.setId(ApplicationContextHelper.getNextIdentity());
            infoTmplField.setInfoTmplId(infoTmplIdPair.getSecond());
            //
            infoTmplFieldIdPairList.add(Pair.of(fromInfoTmplFieldId, infoTmplField.getId()));
            
            return infoTmplField;
        }).collect(Collectors.toList());
        //插入数据库
        getDao().insert(newInfoTmplFieldList);
        
        return infoTmplFieldIdPairList;
    }
}
