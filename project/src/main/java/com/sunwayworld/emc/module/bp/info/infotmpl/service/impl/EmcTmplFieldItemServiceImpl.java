package com.sunwayworld.emc.module.bp.info.infotmpl.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcInfoTmplFieldBean;
import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcTmplFieldItemBean;
import com.sunwayworld.emc.module.bp.info.infotmpl.dao.EmcTmplFieldItemDao;
import com.sunwayworld.emc.module.bp.info.infotmpl.service.EmcInfoTmplFieldService;
import com.sunwayworld.emc.module.bp.info.infotmpl.service.EmcTmplFieldItemService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-19
 */

@Repository
@GikamBean
public class EmcTmplFieldItemServiceImpl implements EmcTmplFieldItemService {
    @Autowired
    private EmcTmplFieldItemDao tmplFieldItemDao;
    
    @Autowired
    private EmcInfoTmplFieldService infoTmplFieldService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcTmplFieldItemDao getDao() {
        return tmplFieldItemDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcTmplFieldItemBean tmplFieldItem = jsonWrapper.parseUnique(EmcTmplFieldItemBean.class);
        tmplFieldItem.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(tmplFieldItem);
        return tmplFieldItem.getId();
    }
    
    
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long tmplId, RestJsonWrapperBean wrapper) {
         EmcTmplFieldItemBean tmplFieldItem = wrapper.parseUnique(EmcTmplFieldItemBean.class);
            tmplFieldItem.setId(ApplicationContextHelper.getNextIdentity());
            EmcInfoTmplFieldBean filed = infoTmplFieldService.selectById(tmplFieldItem.getInfoTmplFieldId());
            tmplFieldItem.setTmplId(tmplId);
            tmplFieldItem.setInfoTmplFieldName(filed.getFieldTitle());
            getDao().insert(tmplFieldItem);
            return tmplFieldItem.getId();
    }
    
    @Override
    @Log(value = "复制模板字段分析项", type = LogType.INSERT)
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public List<EmcTmplFieldItemBean> copy(List<Pair<Long, Long>> infoTmplFieldIdPairList) {
        // 查询原模板字段分析项
        List<Long> formInfoTmplFieldIdList = infoTmplFieldIdPairList.stream().map(infoTmplFieldIdPair -> {
            return infoTmplFieldIdPair.getFirst();
        }).collect(Collectors.toList());
        List<EmcTmplFieldItemBean> tmplFieldItemList = getDao().selectListByOneColumnValues(formInfoTmplFieldIdList, "INFOTMPLFIELDID", Order.asc("ID"));
        if (CollectionUtils.isEmpty(tmplFieldItemList)) {
            return new ArrayList<>();
        }
        // 新的模板字段项列表
        List<EmcTmplFieldItemBean> newTmplFieldItemList = tmplFieldItemList.stream().map(tmplFieldItem -> {
            List<Pair<Long, Long>> newInfoTmplFieldIdPairList = infoTmplFieldIdPairList.stream()
                    .filter(infoTmplFieldIdPair -> infoTmplFieldIdPair.getFirst().compareTo(tmplFieldItem.getInfoTmplFieldId()) == 0)
                    .collect(Collectors.toList());
            tmplFieldItem.setId(ApplicationContextHelper.getNextIdentity());
            tmplFieldItem.setInfoTmplFieldId(newInfoTmplFieldIdPairList.get(0).getSecond());
            return tmplFieldItem;
        }).collect(Collectors.toList());
        // 插入数据库
        getDao().insert(newTmplFieldItemList);

        return newTmplFieldItemList;
    }

}
